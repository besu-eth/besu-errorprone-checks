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

public class MethodInputParametersMustBeFinalNegativeCases {

  public void noParams() {}

  public void allFinalParams(final String input, final int count) {}

  public void finalVarArgs(final String... args) {}

  public static void staticFinalParam(final int id) {}

  public MethodInputParametersMustBeFinalNegativeCases(final String name) {}

  @Generated("tool")
  public static class GeneratedContainer {
    public void nonFinalIgnored(int shouldBeIgnored) {}

    public class NestedGenerated {
      public void alsoIgnored(String s) {}
    }
  }

  public interface InterfaceWithAbstractMethod {
    void paramCannotBeFinal(int value); // abstract, allowed
  }
}

