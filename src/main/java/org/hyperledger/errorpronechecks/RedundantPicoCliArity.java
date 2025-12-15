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

import java.util.Map;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.AnnotationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.AnnotationTree;

@AutoService(BugChecker.class)
@BugPattern(
    summary =
        "Redundant arity = \"1\" specification in PicoCLI @Option annotation. Single-value options default to arity = \"1\".",
    severity = WARNING,
    linkType = BugPattern.LinkType.NONE)
public class RedundantPicoCliArity extends BugChecker implements AnnotationTreeMatcher {

  @Override
  public Description matchAnnotation(AnnotationTree tree, VisitorState state) {
    final AnnotationMirror annotationMirror = ASTHelpers.getAnnotationMirror(tree);
    if (annotationMirror.getAnnotationType().toString().equals("picocli.CommandLine.Option")) {
      final Optional<? extends AnnotationValue> arity =
          getAnnotationValue(annotationMirror, "arity");
      if (arity.isPresent()) {
        final String arityValue = arity.get().getValue().toString();
        if ("\"1\"".equals(arityValue) || "1".equals(arityValue)) {
          return describeMatch(tree);
        }
      }
    }
    return Description.NO_MATCH;
  }

  private Optional<? extends AnnotationValue> getAnnotationValue(
      final AnnotationMirror annotationMirror, final String name) {
    final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues =
        annotationMirror.getElementValues();
    return elementValues.keySet().stream()
        .filter(k -> k.getSimpleName().toString().equals(name))
        .map(elementValues::get)
        .findAny();
  }
}
