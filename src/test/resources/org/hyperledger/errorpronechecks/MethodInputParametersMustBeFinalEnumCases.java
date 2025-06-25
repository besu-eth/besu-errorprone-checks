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

public enum MethodInputParametersMustBeFinalEnumCases {
  EXAMPLE(1);

  private final int id;

  // BUG: Diagnostic contains: Method input parameters must be final.
  MethodInputParametersMustBeFinalEnumCases(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  // BUG: Diagnostic contains: Method input parameters must be final.
  public static MethodInputParametersMustBeFinalEnumCases fromId(int id) {
    for (var v : values()) {
      if (v.getId() == id) {
        return v;
      }
    }
    throw new IllegalArgumentException("Invalid ID");
  }
}



