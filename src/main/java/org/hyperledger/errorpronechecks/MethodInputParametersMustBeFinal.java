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

import javax.lang.model.element.Modifier;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;

@AutoService(BugChecker.class)
@BugPattern(
    summary = "Method input parameters must be final.",
    severity = WARNING,
    linkType = BugPattern.LinkType.NONE)
public class MethodInputParametersMustBeFinal extends BugChecker implements MethodTreeMatcher {

  @Override
  public Description matchMethod(final MethodTree tree, final VisitorState state) {
    var methodSymbol = ASTHelpers.getSymbol(tree);
    if (methodSymbol == null || isGeneratedMethod(methodSymbol, state)) {
      return Description.NO_MATCH;
    }

    var enclosingClass = ASTHelpers.enclosingClass(methodSymbol);
    if (enclosingClass == null || isGeneratedClass(enclosingClass, state)) {
      return Description.NO_MATCH;
    }

    boolean isAbstractContext =
        enclosingClass.isInterface() || enclosingClass.isEnum() || enclosingClass.isAnonymous();
    boolean isConstructor = tree.getName().contentEquals("<init>");
    final ModifiersTree mods = tree.getModifiers();

    if (isAbstractContext) {
      if (isConstructor || isConcreteMethod(mods)) {
        return matchParameters(tree);
      }
    } else if (isConstructor || isNotAbstract(mods)) {
      return matchParameters(tree);
    }

    return Description.NO_MATCH;
  }

  private Description matchParameters(final MethodTree tree) {
    for (final VariableTree inputParameter : tree.getParameters()) {
      if (isMissingFinalModifier(inputParameter)) {
        return describeMatch(tree);
      }
    }
    return Description.NO_MATCH;
  }

  private boolean isMissingFinalModifier(final VariableTree param) {
    return !param.getModifiers().getFlags().contains(Modifier.FINAL);
  }

  private boolean isNotAbstract(final ModifiersTree mods) {
    return !mods.getFlags().contains(Modifier.ABSTRACT);
  }

  private boolean isConcreteMethod(final ModifiersTree mods) {
    return mods.getFlags().contains(Modifier.DEFAULT) || mods.getFlags().contains(Modifier.STATIC);
  }

  private boolean isGeneratedMethod(Symbol symbol, VisitorState state) {
    return hasGeneratedAnnotation(symbol, state);
  }

  private boolean isGeneratedClass(Symbol symbol, VisitorState state) {
    Symbol current = symbol;
    while (current instanceof Symbol.ClassSymbol) {
      if (hasGeneratedAnnotation(current, state)) {
        return true;
      }
      current = current.owner;
    }
    return false;
  }

  private boolean hasGeneratedAnnotation(Symbol symbol, VisitorState state) {
    return ASTHelpers.hasAnnotation(symbol, "javax.annotation.Generated", state)
        || ASTHelpers.hasAnnotation(symbol, "javax.annotation.processing.Generated", state)
        || ASTHelpers.hasAnnotation(symbol, "Generated", state);
  }
}
