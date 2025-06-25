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

@Generated(
        value = "test",
        comments = "Every method is buggy, but ignored because the class has been tagged generated")
class GeneratedTestClass {

  public void primativeInputMethod(int value) {
  }

}

enum BlobType {
  KZG_PROOF(0),
  KZG_CELL_PROOFS(1);


  private final int versionId;

  // BUG: Diagnostic contains: Method input parameters must be final.
  BlobType(int versionId) {
    this.versionId = versionId;
  }

  public int getVersionId() {
    return versionId;
  }

  // BUG: Diagnostic contains: Method input parameters must be final.
  public static BlobType of(int versionId) {
    for (BlobType blobType : BlobType.values()) {
      if (blobType.getVersionId() == versionId) {
        return blobType;
      }
    }
    throw new IllegalArgumentException("No BlobType found for version ID: " + versionId);
  }

  // BUG: Diagnostic contains: Method input parameters must be final.
  static BlobType from(String name) {
    for (BlobType blobType : BlobType.values()) {
      if (blobType.name().equals(name)) {
        return blobType;
      }
    }
    throw new IllegalArgumentException("No BlobType found for name: " + name);
  }
}
