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

import java.nio.file.Path;
import java.util.List;

import picocli.CommandLine;

public class CliOptionMustNotBeConstantNegativeCases {

  private static final int DEFAULT_PORT = 8545;

  // Not final: picocli's value is read.
  @CommandLine.Option(names = {"--max-peers"})
  private int maxPeers = 25;

  // Boxed types are not constant variables, so javac does not inline them.
  @CommandLine.Option(names = {"--rpc-http-max-active-connections"})
  private final Integer rpcHttpMaxConnections = 80;

  @CommandLine.Option(names = {"--rpc-http-enabled"})
  private final Boolean rpcHttpEnabled = false;

  @CommandLine.Option(names = {"--min-gas-price"})
  private final Long minGasPrice = 1000L;

  // The initializer is not a constant expression.
  @CommandLine.Option(names = {"--rpc-http-port"})
  private final int rpcHttpPort = Integer.parseInt("8545");

  @CommandLine.Option(names = {"--identity"})
  private final String identity = String.valueOf(DEFAULT_PORT);

  @CommandLine.Option(names = {"--data-path"})
  private final Path dataPath = Path.of("besu");

  @CommandLine.Option(names = {"--bootnodes"})
  private final List<String> bootNodes = List.of();

  // No initializer.
  @CommandLine.Option(names = {"--network-id"})
  private int networkId;

  // A constant that is not an option.
  private final int notAnOption = 42;

  @CommandLine.Option(names = {"--port"})
  void setPort(final int port) {}
}
