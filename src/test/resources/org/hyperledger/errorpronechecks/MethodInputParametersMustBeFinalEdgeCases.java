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

import javax.annotation.processing.Generated;
import java.util.function.Consumer;

public class MethodInputParametersMustBeFinalEdgeCases {

  // Lambda expression – should NOT trigger
  public void lambdaTest() {
    Consumer<String> consumer = s -> System.out.println(s);
  }

  // Anonymous inner class – should NOT trigger
  Runnable r = new Runnable() {
    @Override
    public void run() {}
  };

  // Method-level @Generated – should not trigger
  @Generated("test")
  public void methodLevelGenerated(int x) {}
}
