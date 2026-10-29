/*
 * Copyright Besu Contributors.
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

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.datatypes.BytesHolder;

/** Runs on the default roots, like the positive cases. */
public class GrindableHashKeyNegativeCases {

  /** Mirrors Address: final and declares Comparable of itself, so bins treeify. */
  static final class AddressLike extends BytesHolder implements Comparable<AddressLike> {
    AddressLike() {
      super(Bytes32.ZERO);
    }

    @Override
    public int compareTo(final AddressLike other) {
      return 0;
    }
  }

  /** Shares the root's simple name, but only the real type's subtypes count. */
  interface Bytes {}

  Map<AddressLike, String> addressKeyed = new HashMap<>();

  Map<Bytes, String> lookalikeKeyed = new HashMap<>();

  // Ordinary keys are not this check's business.
  Map<String, String> stringKeyed = new HashMap<>();
  Set<Integer> intSet = new HashSet<>();
  Map<String, Bytes32> grindableOnlyInValuePositionOfAPlainMap = new HashMap<>();

  // Sorted collections bound insert cost regardless of hash distribution.
  Map<Bytes32, String> sortedMap = new TreeMap<>();
  Set<Bytes32> sortedSet = new TreeSet<>();
  Set<Bytes32> skipListSet = new ConcurrentSkipListSet<>();
  Table<Bytes32, String, String> sortedTable = TreeBasedTable.create();

  // hashKeys() is fine when the values are sorted, and this key is not a grindable type.
  SetMultimap<String, Bytes32> sortedValues = MultimapBuilder.hashKeys().treeSetValues().build();

  // A type variable does not resolve to a grindable type here.
  static class Generic<K> {
    Map<K, String> generic = new HashMap<>();
  }
}
