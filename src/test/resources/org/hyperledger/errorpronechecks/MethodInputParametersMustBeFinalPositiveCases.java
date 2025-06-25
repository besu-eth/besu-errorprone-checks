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

public class MethodInputParametersMustBeFinalPositiveCases {

  // Plain instance methods
  // BUG: Diagnostic contains: Method input parameters must be final.
  public void objectInput(Object value) {}

  // BUG: Diagnostic contains: Method input parameters must be final.
  public void primitiveInput(int count) {}

  // BUG: Diagnostic contains: Method input parameters must be final.
  public void mixedParams(Object obj, int count) {}

  // BUG: Diagnostic contains: Method input parameters must be final.
  public void firstFinal(final Object obj, int count) {}

  // BUG: Diagnostic contains: Method input parameters must be final.
  public void secondFinal(Object obj, final int count) {}

  // BUG: Diagnostic contains: Method input parameters must be final.
  public void varArgsParam(String... values) {}

  // Static method
  // BUG: Diagnostic contains: Method input parameters must be final.
  public static void staticUtility(int value) {}

  // Constructor
  // BUG: Diagnostic contains: Method input parameters must be final.
  public MethodInputParametersMustBeFinalPositiveCases(String description, int id) {}

  // Inner abstract class with concrete method
  public abstract class AbstractInner {
    // BUG: Diagnostic contains: Method input parameters must be final.
    public void concrete(int value) {}
  }
}

