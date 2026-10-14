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

import java.util.List;

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GrindableHashKeyTest {

  /**
   * The case files declare their own stand-in hierarchies rather than depending on Tuweni, whose
   * published artifacts target a newer JDK than this project compiles against. Pointing the check
   * at those roots exercises the same logic the default configuration applies to {@code
   * org.apache.tuweni.bytes.Bytes}.
   */
  private static final List<String> ROOTS =
      List.of(
          "-XepOpt:GrindableHashKey:GrindableRoots="
              + "org.hyperledger.errorpronechecks.GrindableHashKeyPositiveCases.GrindableRoot,"
              + "org.hyperledger.errorpronechecks.GrindableHashKeyNegativeCases.GrindableRoot,"
              + "org.hyperledger.errorpronechecks.GrindableHashKeyNegativeCases.SelfComparableCapableRoot,"
              + "org.hyperledger.errorpronechecks.GrindableHashKeyUnsearchedCases.GrindableRoot");

  private CompilationTestHelper compilationHelper;

  @BeforeEach
  public void setup() {
    compilationHelper =
        CompilationTestHelper.newInstance(GrindableHashKey.class, getClass()).setArgs(ROOTS);
  }

  @Test
  public void grindableHashKeyPositiveCases() {
    compilationHelper.addSourceFile("GrindableHashKeyPositiveCases.java").doTest();
  }

  @Test
  public void grindableHashKeyNegativeCases() {
    compilationHelper.addSourceFile("GrindableHashKeyNegativeCases.java").doTest();
  }

  @Test
  public void unsearchedCollectionsSuggestAList() {
    compilationHelper
        .addSourceFile("GrindableHashKeyUnsearchedCases.java")
        .expectErrorMessage(
            "SET_HINT", msg -> msg.contains("GrindableHashKey") && msg.contains("an ArrayList"))
        .expectErrorMessage(
            "MAP_HINT", msg -> msg.contains("GrindableHashKey") && msg.contains("a List of pairs"))
        .expectErrorMessage(
            "NO_HINT",
            msg -> msg.contains("GrindableHashKey") && !msg.contains("avoids hashing entirely"))
        .doTest();
  }

  /**
   * With no roots configured the check falls back to Tuweni's {@code Bytes}, which is absent from
   * this project's classpath. An absent root must simply have nothing to say, rather than the check
   * guessing from type names.
   */
  @Test
  public void silentWhenNoConfiguredRootIsOnTheClasspath() {
    CompilationTestHelper.newInstance(GrindableHashKey.class, getClass())
        .addSourceLines(
            "NoRoots.java",
            "import java.util.HashMap;",
            "import java.util.Map;",
            "public class NoRoots {",
            "  interface Whatever extends Comparable<Whatever> {}",
            "  Map<Whatever, String> plain = new HashMap<>();",
            "}")
        .doTest();
  }
}
