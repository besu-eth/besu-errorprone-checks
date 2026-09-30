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

public class GrindableHashKeyNegativeCases {

  interface GrindableRoot extends Comparable<GrindableRoot> {}

  static final class GrindableLeaf implements GrindableRoot {
    @Override
    public int compareTo(final GrindableRoot other) {
      return 0;
    }
  }

  /**
   * A second configured root that does <em>not</em> itself declare Comparable, so a leaf below it
   * is free to declare Comparable of its own exact type. This is the shape Besu's {@code Address}
   * has, and such a leaf treeifies correctly - so it must not be flagged.
   */
  interface SelfComparableCapableRoot {}

  static final class SelfComparableLeaf
      implements SelfComparableCapableRoot, Comparable<SelfComparableLeaf> {
    @Override
    public int compareTo(final SelfComparableLeaf other) {
      return 0;
    }
  }

  /** Same root, but no Comparable-of-self and not final - still dangerous, covered in positives. */
  static final class PlainLeaf implements SelfComparableCapableRoot {}

  // Declares Comparable of its own exact type on a final class: bins treeify, so not flagged.
  Map<SelfComparableLeaf, String> selfComparableKeyed = new HashMap<>();

  // Ordinary keys are not this check's business.
  Map<String, String> stringKeyed = new HashMap<>();
  Set<Integer> intSet = new HashSet<>();
  Map<String, GrindableLeaf> grindableOnlyInValuePositionOfAPlainMap = new HashMap<>();

  // Sorted collections bound insert cost regardless of hash distribution.
  Map<GrindableLeaf, String> sortedMap = new TreeMap<>();
  Set<GrindableLeaf> sortedSet = new TreeSet<>();
  Set<GrindableLeaf> skipListSet = new ConcurrentSkipListSet<>();
  Table<GrindableLeaf, String, String> sortedTable = TreeBasedTable.create();

  // hashKeys() is fine when the values are sorted, and this key is not a grindable type.
  SetMultimap<String, GrindableLeaf> sortedValues = MultimapBuilder.hashKeys().treeSetValues().build();

  // A type variable does not resolve to a grindable type here.
  static class Generic<K> {
    Map<K, String> generic = new HashMap<>();
  }
}
