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

public class MethodInputParametersMustBeFinalNestedEnumCases {

  enum Status {
    ACTIVE(1),
    INACTIVE(0);

    private final int code;

    // BUG: Diagnostic contains: Method input parameters must be final.
    Status(int code) {
      this.code = code;
    }

    public int getCode() {
      return code;
    }

    // BUG: Diagnostic contains: Method input parameters must be final.
    public static Status fromCode(int code) {
      for (Status s : values()) {
        if (s.code == code) {
          return s;
        }
      }
      throw new IllegalArgumentException("Invalid code: " + code);
    }
  }

  // This method is here to ensure context works when class has other members
  public void unrelatedMethod(final String msg) {
    System.out.println(msg);
  }
}
