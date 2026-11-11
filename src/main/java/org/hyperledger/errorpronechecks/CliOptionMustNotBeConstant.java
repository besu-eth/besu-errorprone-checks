/*
 * Copyright contributors to Besu.
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

import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.VariableTreeMatcher;
import com.google.errorprone.fixes.SuggestedFixes;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol.VarSymbol;

/**
 * Flags a picocli {@code @Option} or {@code @Parameters} field that is a compile-time constant.
 *
 * <p>A {@code final} field of a primitive or {@code String} type whose initializer is a constant
 * expression is a constant variable (JLS 4.12.4), and javac copies its value into every place that
 * reads it, so a value picocli writes into the field would never be seen. picocli guards against
 * this: it throws an {@code InitializationException} when the command is constructed. This check
 * reports the same mistake at compile time instead of when the command first starts or is tested.
 * Boxed types such as {@code Integer} are not constant variables, so they are not affected.
 */
@AutoService(BugChecker.class)
@BugPattern(
    summary =
        "A picocli option field must not be a compile-time constant: javac inlines it, and picocli"
            + " rejects such fields when the command is constructed. Remove final.",
    severity = ERROR,
    linkType = BugPattern.LinkType.NONE)
public class CliOptionMustNotBeConstant extends BugChecker implements VariableTreeMatcher {

  private static final String OPTION = "picocli.CommandLine.Option";
  private static final String PARAMETERS = "picocli.CommandLine.Parameters";

  @Override
  public Description matchVariable(final VariableTree tree, final VisitorState state) {
    final VarSymbol symbol = ASTHelpers.getSymbol(tree);
    if (symbol == null || symbol.getKind() != ElementKind.FIELD) {
      return Description.NO_MATCH;
    }
    if (!ASTHelpers.hasAnnotation(symbol, OPTION, state)
        && !ASTHelpers.hasAnnotation(symbol, PARAMETERS, state)) {
      return Description.NO_MATCH;
    }
    // javac records a constant value only for constant variables, the case it inlines.
    if (symbol.getConstValue() == null) {
      return Description.NO_MATCH;
    }
    return SuggestedFixes.removeModifiers(tree, state, Modifier.FINAL)
        .map(fix -> describeMatch(tree, fix))
        .orElseGet(() -> describeMatch(tree));
  }
}
