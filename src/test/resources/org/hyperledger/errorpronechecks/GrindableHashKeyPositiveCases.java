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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The stand-in hierarchy mirrors Tuweni's: a root that declares {@code Comparable} of the root
 * type, so that no implementation can ever declare {@code Comparable} of itself. The test runs the
 * check with {@code GrindableRoots} pointed at {@code GrindableRoot} rather than depending on
 * Tuweni, whose published artifacts target a newer JDK than this project compiles against.
 */
public class GrindableHashKeyPositiveCases {

  interface GrindableRoot extends Comparable<GrindableRoot> {}

  /** Mirrors Bytes32/UInt256: final, but only Comparable&lt;GrindableRoot&gt;. */
  static final class GrindableLeaf implements GrindableRoot {
    @Override
    public int compareTo(final GrindableRoot other) {
      return 0;
    }
  }

  /** Mirrors the Bytes interface itself: extensible, so runtime classes decide, and they cannot. */
  interface GrindableSubInterface extends GrindableRoot {}

  // BUG: Diagnostic contains: GrindableHashKey
  Map<GrindableLeaf, String> leafKeyedMap = new HashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<GrindableRoot, String> rootKeyedMap = new HashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<GrindableSubInterface, String> subInterfaceKeyedMap = new HashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<GrindableLeaf> leafSet = new HashSet<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<GrindableLeaf, String> linkedMap = new LinkedHashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<GrindableLeaf, String> concurrentMap = new ConcurrentHashMap<>();

  // HashMultimap holds each key's values in a HashSet, so the value position matters too.
  // BUG: Diagnostic contains: GrindableHashKey
  Multimap<String, GrindableLeaf> multimapValues = HashMultimap.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Multimap<GrindableLeaf, String> multimapKeys = HashMultimap.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Table<GrindableLeaf, String, String> tableRowKey = HashBasedTable.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<GrindableLeaf> guavaFactorySet = Sets.newHashSet();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<GrindableLeaf> concurrentKeySet = ConcurrentHashMap.newKeySet();

  // Fully qualified constructors resolve to the same type; a textual check would miss this.
  // BUG: Diagnostic contains: GrindableHashKey
  Map<GrindableLeaf, String> qualifiedConstructor = new java.util.HashMap<>();

  void locals() {
    // BUG: Diagnostic contains: GrindableHashKey
    final Map<GrindableLeaf, String> local = new HashMap<>();
    // BUG: Diagnostic contains: GrindableHashKey
    var inferred = new HashMap<GrindableLeaf, String>();
  }
}
