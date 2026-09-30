/*
 * Copyright Hyperledger Besu Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.errorpronechecks;

import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.BugPattern;
import com.google.errorprone.ErrorProneFlags;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;

/**
 * Flags hash-bucketed collections keyed by a type that is cheap to collide and cannot treeify.
 *
 * <p>Java's {@code HashMap} degrades a colliding bin to a linear walk unless the key declares
 * {@code Comparable} of its own exact type - see {@code HashMap.comparableClassFor}. Tuweni's
 * {@code Bytes} hierarchy hashes with an unseeded base-31 byte polynomial, so collisions are cheap
 * to grind, and <em>no</em> {@code Bytes} subtype can declare {@code Comparable<Self>}: {@code
 * Bytes} already declares {@code Comparable<Bytes>} and Java forbids inheriting one generic
 * interface with two different arguments. The combination turns a linear number of inserts into
 * quadratic work whenever the key bytes are attacker-chosen.
 *
 * <p>The check is structural rather than name-based: it flags a key type only when it is a subtype
 * of the grindable root AND does not directly declare {@code Comparable} of itself. {@code Address}
 * is therefore not flagged - it sits outside the {@code Bytes} hierarchy (it extends {@code
 * BytesHolder}) and declares {@code Comparable<Address>} - while {@code UInt256} and {@code
 * Bytes32} are.
 *
 * <p>Severity is WARNING because a grindable key is acceptable when its bytes cannot be chosen by
 * an attacker - for example keccak outputs. Suppress such sites with a justification.
 *
 * <p>When every use of the collection is visible (a local variable or a private field that never
 * escapes) and it is only ever inserted into and iterated, never searched, the message also points
 * out that a list would avoid hashing altogether. Insertion alone is still a bucket walk, so such a
 * collection is not safe; it just may not need to be a hash table. Whether its deduplication
 * matters is for the author to judge.
 */
@AutoService(BugChecker.class)
@BugPattern(
    summary =
        "Hash-bucketed collection keyed by a Tuweni Bytes-hierarchy type. These keys hash with a"
            + " grindable base-31 polynomial and can never treeify, so chosen collisions make"
            + " insertion quadratic. Use a sorted collection (TreeMap/TreeSet/"
            + "ConcurrentSkipListSet/TreeBasedTable) or a key type that declares Comparable of its"
            + " own exact type.",
    severity = WARNING,
    linkType = BugPattern.LinkType.NONE)
public class GrindableHashKey extends BugChecker
    implements BugChecker.NewClassTreeMatcher, BugChecker.MethodInvocationTreeMatcher {

  private static final long serialVersionUID = 1L;

  /**
   * Roots of hierarchies whose {@code hashCode} is cheaply grindable. Configurable so the check can
   * be tested without the real dependency, and so consumers can add their own: {@code
   * -XepOpt:GrindableHashKey:GrindableRoots=com.example.Foo,com.example.Bar}
   */
  private static final String ROOTS_FLAG = "GrindableHashKey:GrindableRoots";

  private static final ImmutableSet<String> DEFAULT_GRINDABLE_ROOTS =
      ImmutableSet.of("org.apache.tuweni.bytes.Bytes");

  private final ImmutableSet<String> grindableRoots;

  public GrindableHashKey() {
    this(ErrorProneFlags.empty());
  }

  public GrindableHashKey(final ErrorProneFlags flags) {
    final ImmutableSet<String> configured = flags.getSetOrEmpty(ROOTS_FLAG);
    this.grindableRoots = configured.isEmpty() ? DEFAULT_GRINDABLE_ROOTS : configured;
  }

  /** Concrete collection types that bucket by hash. */
  private static final ImmutableSet<String> HASH_BACKED =
      ImmutableSet.of(
          "java.util.HashMap",
          "java.util.HashSet",
          "java.util.LinkedHashMap",
          "java.util.LinkedHashSet",
          "java.util.concurrent.ConcurrentHashMap",
          "com.google.common.collect.HashMultimap",
          "com.google.common.collect.HashBasedTable");

  /** Static factories that return one of the above. */
  private static final ImmutableSet<String> HASH_BACKED_FACTORIES =
      ImmutableSet.of(
          "com.google.common.collect.HashMultimap.create",
          "com.google.common.collect.HashBasedTable.create",
          "com.google.common.collect.Maps.newHashMap",
          "com.google.common.collect.Sets.newHashSet",
          "com.google.common.collect.Sets.newHashSetWithExpectedSize",
          "java.util.concurrent.ConcurrentHashMap.newKeySet",
          "java.util.HashMap.newHashMap",
          "java.util.HashSet.newHashSet");

  @Override
  public Description matchNewClass(final NewClassTree tree, final VisitorState state) {
    final Type type = ASTHelpers.getType(tree);
    if (type == null || !HASH_BACKED.contains(erasedName(type, state))) {
      return Description.NO_MATCH;
    }
    return describeIfGrindable(tree, type, state);
  }

  @Override
  public Description matchMethodInvocation(
      final MethodInvocationTree tree, final VisitorState state) {
    final Symbol.MethodSymbol method = ASTHelpers.getSymbol(tree);
    if (method == null) {
      return Description.NO_MATCH;
    }
    final String qualified = method.owner.getQualifiedName() + "." + method.getSimpleName();
    if (!HASH_BACKED_FACTORIES.contains(qualified)) {
      return Description.NO_MATCH;
    }
    return describeIfGrindable(tree, ASTHelpers.getType(tree), state);
  }

  private Description describeIfGrindable(
      final ExpressionTree tree, final Type type, final VisitorState state) {
    if (type == null) {
      return Description.NO_MATCH;
    }
    for (final Type key : keyPositions(type, state)) {
      if (isGrindable(key, state)) {
        return buildDescription(tree)
            .setMessage(
                message()
                    + " Key type here is "
                    + key
                    + ", a grindable-hierarchy type that does not declare Comparable<"
                    + key
                    + "> on a final class."
                    + unsearchedHint(type, state))
            .build();
      }
    }
    return Description.NO_MATCH;
  }

  /**
   * Type arguments that end up hashed. For a Multimap the values count too, since HashMultimap
   * holds each key's values in a HashSet; likewise a Table's row and column keys.
   */
  private List<Type> keyPositions(final Type type, final VisitorState state) {
    final List<Type> args = type.getTypeArguments();
    final List<Type> keys = new ArrayList<>();
    if (args.isEmpty()) {
      return keys;
    }
    final Types types = state.getTypes();
    final boolean multiKeyed =
        isSubtypeOfErased(type, "com.google.common.collect.Multimap", state, types)
            || isSubtypeOfErased(type, "com.google.common.collect.Table", state, types);
    keys.add(args.get(0));
    if (multiKeyed && args.size() > 1) {
      keys.add(args.get(1));
    }
    return keys;
  }

  /**
   * A key is dangerous when it descends from the grindable root and cannot treeify. The second half
   * mirrors {@code HashMap.comparableClassFor}: only a direct {@code Comparable<Self>} declaration
   * counts.
   */
  private boolean isGrindable(final Type key, final VisitorState state) {
    if (key == null || key.getTag() == com.sun.tools.javac.code.TypeTag.TYPEVAR) {
      return false;
    }
    final Types types = state.getTypes();
    boolean descendsFromAGrindableRoot = false;
    for (final String rootName : grindableRoots) {
      final Type root = state.getTypeFromString(rootName);
      // A root that is absent from this compilation's classpath simply has nothing to say here.
      if (root != null && types.isSubtype(types.erasure(key), types.erasure(root))) {
        descendsFromAGrindableRoot = true;
        break;
      }
    }
    if (!descendsFromAGrindableRoot) {
      return false;
    }
    return !declaresComparableOfSelf(key, state);
  }

  private boolean declaresComparableOfSelf(final Type key, final VisitorState state) {
    final Types types = state.getTypes();
    final Symbol.TypeSymbol symbol = key.asElement();
    if (symbol == null) {
      return false;
    }
    // Only a final type can be exempted. comparableClassFor() inspects the *runtime* class of the
    // key, so for an interface or an extensible class the declaration we can see here says nothing
    // about what actually ends up in the bin. Bytes is the case in point: it declares
    // Comparable<Bytes>, which is Comparable-of-itself, yet every implementation
    // (ArrayWrappingBytes and friends) inherits Comparable<Bytes> rather than declaring
    // Comparable<Self>, so those bins never treeify.
    if ((symbol.flags() & com.sun.tools.javac.code.Flags.FINAL) == 0) {
      return false;
    }
    for (final Type iface : types.interfaces(symbol.type)) {
      if (!"java.lang.Comparable".equals(erasedName(iface, state))) {
        continue;
      }
      final List<Type> args = iface.getTypeArguments();
      if (args.size() == 1 && types.isSameType(types.erasure(args.get(0)), types.erasure(key))) {
        return true;
      }
    }
    return false;
  }

  /** Inserts whose result, when ignored, means the call is not being used as a membership test. */
  private static final ImmutableSet<String> INSERTS =
      ImmutableSet.of("add", "addAll", "put", "putAll", "putIfAbsent");

  /** Calls that neither search the collection nor let it escape. */
  private static final ImmutableSet<String> ITERATIONS =
      ImmutableSet.of(
          "iterator",
          "spliterator",
          "forEach",
          "stream",
          "parallelStream",
          "size",
          "isEmpty",
          "toArray",
          "clear");

  /** Views that are fine so long as they, in turn, are only iterated. */
  private static final ImmutableSet<String> VIEWS =
      ImmutableSet.of("keySet", "values", "entrySet", "entries", "cellSet");

  /**
   * Returns an extra sentence when the collection being created is only ever inserted into and
   * iterated, or an empty string when it is searched or its uses cannot all be seen.
   */
  private String unsearchedHint(final Type collectionType, final VisitorState state) {
    final Symbol target = assignedSymbol(state);
    if (!(target instanceof Symbol.VarSymbol)) {
      return "";
    }
    final boolean visibleEverywhere =
        target.getKind() == ElementKind.LOCAL_VARIABLE
            || (target.getKind() == ElementKind.FIELD
                && target.getModifiers().contains(Modifier.PRIVATE));
    if (!visibleEverywhere) {
      return "";
    }
    final boolean[] onlyInsertedAndIterated = {true};
    new TreePathScanner<Void, Void>() {
      @Override
      public Void visitIdentifier(final IdentifierTree node, final Void unused) {
        check(node);
        return super.visitIdentifier(node, unused);
      }

      @Override
      public Void visitMemberSelect(final MemberSelectTree node, final Void unused) {
        check(node);
        return super.visitMemberSelect(node, unused);
      }

      private void check(final Tree node) {
        if (target.equals(ASTHelpers.getSymbol(node)) && !isInsertOrIteration(getCurrentPath())) {
          onlyInsertedAndIterated[0] = false;
        }
      }
    }.scan(state.getPath().getCompilationUnit(), null);
    if (!onlyInsertedAndIterated[0]) {
      return "";
    }
    final boolean isMap =
        isSubtypeOfErased(collectionType, "java.util.Map", state, state.getTypes())
            || isSubtypeOfErased(
                collectionType, "com.google.common.collect.Multimap", state, state.getTypes())
            || isSubtypeOfErased(
                collectionType, "com.google.common.collect.Table", state, state.getTypes());
    return isMap
        ? " It is never searched by key, only populated and iterated: if keys are unique by"
            + " construction, a List of pairs avoids hashing entirely."
        : " It is never searched, only populated and iterated: if duplicates cannot occur or do"
            + " not matter, an ArrayList avoids hashing entirely.";
  }

  /** The variable the matched creation expression is directly stored into, if any. */
  private static Symbol assignedSymbol(final VisitorState state) {
    final Tree parent = state.getPath().getParentPath().getLeaf();
    if (parent instanceof VariableTree) {
      return ASTHelpers.getSymbol((VariableTree) parent);
    }
    if (parent instanceof AssignmentTree) {
      return ASTHelpers.getSymbol(((AssignmentTree) parent).getVariable());
    }
    return null;
  }

  /**
   * Whether a reference to the collection (or to one of its views) only writes to it or iterates
   * it. Anything else - a lookup, a used insert result, passing it along - counts as a search or an
   * escape.
   */
  private static boolean isInsertOrIteration(final TreePath ref) {
    final Tree node = ref.getLeaf();
    final TreePath parentPath = ref.getParentPath();
    final Tree parent = parentPath.getLeaf();
    if (parent instanceof AssignmentTree) {
      return ((AssignmentTree) parent).getVariable() == node;
    }
    if (parent instanceof EnhancedForLoopTree) {
      return ((EnhancedForLoopTree) parent).getExpression() == node;
    }
    if (!(parent instanceof MemberSelectTree)) {
      return false;
    }
    final TreePath callPath = parentPath.getParentPath();
    if (!(callPath.getLeaf() instanceof MethodInvocationTree)
        || ((MethodInvocationTree) callPath.getLeaf()).getMethodSelect() != parent) {
      return false;
    }
    final String name = ((MemberSelectTree) parent).getIdentifier().toString();
    if (INSERTS.contains(name)) {
      return callPath.getParentPath().getLeaf() instanceof ExpressionStatementTree;
    }
    if (ITERATIONS.contains(name)) {
      return true;
    }
    return VIEWS.contains(name) && isInsertOrIteration(callPath);
  }

  private boolean isSubtypeOfErased(
      final Type type, final String name, final VisitorState state, final Types types) {
    final Type target = state.getTypeFromString(name);
    return target != null && types.isSubtype(types.erasure(type), types.erasure(target));
  }

  private static String erasedName(final Type type, final VisitorState state) {
    return state.getTypes().erasure(type).toString();
  }
}
