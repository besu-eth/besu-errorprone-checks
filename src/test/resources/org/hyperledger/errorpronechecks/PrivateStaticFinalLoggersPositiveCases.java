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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateStaticFinalLoggersPositiveCases {

  // BUG: Diagnostic contains:  Logger classes should be private, static, and final.
  private final Logger LOG = LoggerFactory.getLogger(PrivateStaticFinalLoggersPositiveCases.class);
}
