/*
 * Copyright Besu Contributors.
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

import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GrindableHashKeyTest {

  private CompilationTestHelper compilationHelper;

  /**
   * The case files run on the default roots against real Tuweni types. {@code BytesHolder} is
   * Besu's and Besu depends on these checks, so a stand-in at its real name is compiled alongside
   * instead. Dropping either default root therefore fails a test.
   */
  @BeforeEach
  public void setup() {
    compilationHelper =
        CompilationTestHelper.newInstance(GrindableHashKey.class, getClass())
            .addSourceLines(
                "org/hyperledger/besu/datatypes/BytesHolder.java",
                "package org.hyperledger.besu.datatypes;",
                "import org.apache.tuweni.bytes.Bytes;",
                "public class BytesHolder {",
                "  protected BytesHolder(final Bytes value) {}",
                "}");
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

  @Test
  public void configuredRootsAreHonoured() {
    CompilationTestHelper.newInstance(GrindableHashKey.class, getClass())
        .setArgs("-XepOpt:GrindableHashKey:GrindableRoots=com.example.Custom")
        .addSourceLines(
            "com/example/Custom.java", "package com.example;", "public interface Custom {}")
        .addSourceLines(
            "CustomKeys.java",
            "import java.util.HashMap;",
            "import java.util.Map;",
            "public class CustomKeys {",
            "  static final class Leaf implements com.example.Custom {}",
            "  // BUG: Diagnostic contains: GrindableHashKey",
            "  Map<Leaf, String> leafKeyed = new HashMap<>();",
            "}")
        .doTest();
  }

  /**
   * A configured root that is absent from the classpath must simply have nothing to say, rather
   * than the check guessing from type names.
   */
  @Test
  public void silentWhenConfiguredRootIsAbsent() {
    CompilationTestHelper.newInstance(GrindableHashKey.class, getClass())
        .setArgs("-XepOpt:GrindableHashKey:GrindableRoots=com.example.Absent")
        .addSourceLines(
            "AbsentRoot.java",
            "import java.util.HashMap;",
            "import java.util.Map;",
            "public class AbsentRoot {",
            "  interface Absent {}",
            "  Map<Absent, String> plain = new HashMap<>();",
            "}")
        .doTest();
  }
}
