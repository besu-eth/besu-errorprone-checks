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

import picocli.CommandLine.Option;

public class RedundantPicoCliArityPositiveCases {

  // BUG: Diagnostic contains: Redundant arity = "1" specification
  @Option(
      names = {"--test-option"},
      description = "Test option with redundant arity",
      arity = "1")
  private String testOption;

  // BUG: Diagnostic contains: Redundant arity = "1" specification
  @Option(
      names = "--another-option",
      arity = "1",
      description = "Another test option")
  private Integer anotherOption;

  // BUG: Diagnostic contains: Redundant arity = "1" specification
  @Option(names = "--simple", arity = "1")
  private String simpleOption;

  // BUG: Diagnostic contains: Redundant arity = "1" specification
  @Option(
      names = {"--multi-line"},
      description = "Multi-line option definition",
      defaultValue = "default",
      arity = "1",
      required = false)
  private Long multiLineOption;
}