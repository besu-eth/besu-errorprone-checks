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

public class RedundantPicoCliArityNegativeCases {

  // Valid: No arity specified (defaults to "1")
  @Option(names = {"--test-option"}, description = "Test option without arity")
  private String testOption;

  // Valid: Non-default arity value
  @Option(
      names = {"--list-option"},
      description = "List option with multiple values",
      arity = "0..*")
  private String[] listOption;

  // Valid: Different arity value
  @Option(names = "--range", arity = "2", description = "Option requiring exactly 2 values")
  private String[] rangeOption;

  // Valid: Zero arity (boolean flag)
  @Option(names = "--flag", arity = "0", description = "Boolean flag")
  private boolean flag;

  // Valid: Option with no arity attribute
  @Option(
      names = {"--standard"},
      description = "Standard option",
      required = false,
      defaultValue = "default")
  private String standardOption;

  // Valid: Suppressed with @SuppressWarnings for intentional Boolean usage
  @SuppressWarnings("RedundantPicoCliArity")
  @Option(
      names = {"--boolean-flag"},
      description = "Boolean field with explicit arity (suppressed)",
      arity = "1")
  private Boolean booleanFlag;

  // Valid: Field-level suppression
  @SuppressWarnings("RedundantPicoCliArity")
  @Option(
      names = {"--another-boolean"},
      description = "Another Boolean field with explicit arity (suppressed)",
      arity = "1")
  public final Boolean anotherBoolean = true;

  // Valid: Different annotation entirely
  @Deprecated
  private String deprecatedField;

  // Valid: Non-PicoCLI annotation
  @SuppressWarnings("unused")
  private String suppressedField;
}