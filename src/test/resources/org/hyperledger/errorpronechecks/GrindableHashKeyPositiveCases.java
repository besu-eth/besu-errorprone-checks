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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;
import org.hyperledger.besu.datatypes.BytesHolder;

/**
 * Runs on the default roots against real Tuweni types. {@code BytesHolder} is Besu's, so the test
 * compiles a stand-in at its real name alongside this file.
 */
public class GrindableHashKeyPositiveCases {

  /** Mirrors Hash: under BytesHolder, but never declares Comparable of itself. */
  static class HashLike extends BytesHolder {
    HashLike() {
      super(Bytes32.ZERO);
    }
  }

  /** Declares Comparable of itself but is not final, so the runtime class may not. */
  static class ExtensibleSelfComparable extends BytesHolder
      implements Comparable<ExtensibleSelfComparable> {
    ExtensibleSelfComparable() {
      super(Bytes32.ZERO);
    }

    @Override
    public int compareTo(final ExtensibleSelfComparable other) {
      return 0;
    }
  }

  // An interface: its implementations inherit Comparable<Bytes> and never treeify.
  // BUG: Diagnostic contains: GrindableHashKey
  Map<Bytes, String> bytesKeyedMap = new HashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<Bytes32, String> bytes32KeyedMap = new HashMap<>();

  // Final, but only Comparable<Bytes>, inherited through UInt256Value and Bytes32.
  // BUG: Diagnostic contains: GrindableHashKey
  Map<UInt256, String> uint256KeyedMap = new HashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<HashLike, String> hashKeyedMap = new HashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<ExtensibleSelfComparable, String> extensibleKeyedMap = new HashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<Bytes32> hashSet = new HashSet<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<Bytes32, String> linkedMap = new LinkedHashMap<>();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<Bytes32, String> concurrentMap = new ConcurrentHashMap<>();

  // HashMultimap holds each key's values in a HashSet, so the value position matters too.
  // BUG: Diagnostic contains: GrindableHashKey
  Multimap<String, Bytes32> multimapValues = HashMultimap.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Multimap<Bytes32, String> multimapKeys = HashMultimap.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Table<Bytes32, String, String> tableRowKey = HashBasedTable.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<Bytes32> guavaFactorySet = Sets.newHashSet();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<Bytes32> concurrentKeySet = ConcurrentHashMap.newKeySet();

  // Fully qualified constructors resolve to the same type; a textual check would miss this.
  // BUG: Diagnostic contains: GrindableHashKey
  Map<Bytes32, String> qualifiedConstructor = new java.util.HashMap<>();

  // The bounded-LRU idiom: an anonymous subclass of LinkedHashMap.
  Map<HashLike, String> anonymousLru =
      // BUG: Diagnostic contains: GrindableHashKey
      new LinkedHashMap<HashLike, String>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<HashLike, String> eldest) {
          return size() > 300;
        }
      };

  // BUG: Diagnostic contains: GrindableHashKey
  Set<Bytes32> anonymousWithDiamond = new HashSet<>() {};

  /** A named subclass whose type parameters are not the map's. */
  static class LruMap<V, K> extends LinkedHashMap<K, V> {}

  // BUG: Diagnostic contains: Key type here is org.apache.tuweni.bytes.Bytes32
  Map<Bytes32, String> namedSubclass = new LruMap<String, Bytes32>();

  /** A subclass that fixes the key type itself. */
  static class HashKeyedMap extends HashMap<HashLike, String> {}

  // BUG: Diagnostic contains: GrindableHashKey
  Map<HashLike, String> fixedKeySubclass = new HashKeyedMap();

  // BUG: Diagnostic contains: GrindableHashKey
  BiMap<Bytes32, String> biMapKeys = HashBiMap.create();

  // A BiMap hashes its values as well as its keys.
  // BUG: Diagnostic contains: GrindableHashKey
  BiMap<String, Bytes32> biMapValues = HashBiMap.create();

  // BUG: Diagnostic contains: GrindableHashKey
  ListMultimap<Bytes32, String> arrayListMultimapKeys = ArrayListMultimap.create();

  // BUG: Diagnostic contains: GrindableHashKey
  SetMultimap<String, Bytes32> linkedMultimapValues = LinkedHashMultimap.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Multiset<Bytes32> multiset = HashMultiset.create();

  // BUG: Diagnostic contains: GrindableHashKey
  Map<Bytes32, String> expectedSizeMap = Maps.newHashMapWithExpectedSize(4);

  // BUG: Diagnostic contains: GrindableHashKey
  Map<Bytes32, String> guavaLinkedMap = Maps.newLinkedHashMap();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<Bytes32> guavaLinkedSet = Sets.newLinkedHashSet();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<Bytes32> guavaConcurrentSet = Sets.newConcurrentHashSet();

  // BUG: Diagnostic contains: GrindableHashKey
  Set<Bytes32> jdkLinkedSetFactory = LinkedHashSet.newLinkedHashSet(4);

  void locals() {
    // BUG: Diagnostic contains: GrindableHashKey
    final Map<Bytes32, String> local = new HashMap<>();
    // BUG: Diagnostic contains: GrindableHashKey
    var inferred = new HashMap<Bytes32, String>();
  }
}
