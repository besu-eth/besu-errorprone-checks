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

import picocli.CommandLine;

public class CliOptionMustNotBeConstantPositiveCases {

  private static final int DEFAULT_PORT = 8545;

  @CommandLine.Option(names = {"--max-peers"})
  // BUG: Diagnostic contains: CliOptionMustNotBeConstant
  private final int maxPeers = 25;

  @CommandLine.Option(names = {"--rpc-http-port"})
  // BUG: Diagnostic contains: CliOptionMustNotBeConstant
  private final int rpcHttpPort = DEFAULT_PORT;

  @CommandLine.Option(names = {"--max-active-connections"})
  // BUG: Diagnostic contains: CliOptionMustNotBeConstant
  private final long maxActiveConnections = 80L * 2;

  @CommandLine.Option(names = {"--rpc-http-enabled"})
  // BUG: Diagnostic contains: CliOptionMustNotBeConstant
  private final boolean rpcHttpEnabled = false;

  @CommandLine.Option(names = {"--identity"})
  // BUG: Diagnostic contains: CliOptionMustNotBeConstant
  private final String identity = "besu";

  @CommandLine.Option(names = {"--logging"})
  // BUG: Diagnostic contains: CliOptionMustNotBeConstant
  private final String logging = "IN" + "FO";

  @CommandLine.Parameters(index = "0")
  // BUG: Diagnostic contains: CliOptionMustNotBeConstant
  private final String subcommandArgument = "";
}
