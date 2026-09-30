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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Every site here is grindable. HINT marks those that are provably only populated and iterated, so
 * the message should suggest a list; NO_HINT marks those that are searched or escape.
 */
public class GrindableHashKeyUnsearchedCases {

  interface GrindableRoot extends Comparable<GrindableRoot> {}

  static final class Leaf implements GrindableRoot {
    @Override
    public int compareTo(final GrindableRoot other) {
      return 0;
    }
  }

  // BUG: Diagnostic matches: SET_HINT
  private final Set<Leaf> privateIterated = new HashSet<>();

  // Not private: other classes may search it.
  // BUG: Diagnostic matches: NO_HINT
  final Set<Leaf> packageVisible = new HashSet<>();

  private final Set<Leaf> assignedInConstructor;

  GrindableHashKeyUnsearchedCases() {
    // BUG: Diagnostic matches: SET_HINT
    assignedInConstructor = new HashSet<>();
  }

  void fill(final Leaf leaf) {
    privateIterated.add(leaf);
    this.assignedInConstructor.add(leaf);
    packageVisible.add(leaf);
  }

  long count() {
    return privateIterated.stream().count() + assignedInConstructor.size();
  }

  void localSetOnlyIterated(final Iterable<Leaf> in) {
    // BUG: Diagnostic matches: SET_HINT
    final Set<Leaf> s = new HashSet<>();
    for (final Leaf l : in) {
      s.add(l);
    }
    for (final Leaf l : s) {
      System.out.println(l);
    }
    s.forEach(l -> System.out.println(l));
  }

  void localSetSearched(final Leaf leaf) {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Leaf> s = new HashSet<>();
    s.add(leaf);
    System.out.println(s.contains(leaf));
  }

  void localSetAddResultUsed(final Leaf leaf) {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Leaf> s = new HashSet<>();
    if (s.add(leaf)) {
      System.out.println(leaf);
    }
  }

  Set<Leaf> localSetReturned() {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Leaf> s = new HashSet<>();
    return s;
  }

  void localSetPassedAlong() {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Leaf> s = new HashSet<>();
    System.out.println(s);
  }

  void localMapOnlyIterated(final Leaf leaf) {
    // BUG: Diagnostic matches: MAP_HINT
    final Map<Leaf, String> m = new HashMap<>();
    m.put(leaf, "x");
    for (final Map.Entry<Leaf, String> e : m.entrySet()) {
      System.out.println(e);
    }
    m.values().forEach(System.out::println);
  }

  void localMapSearched(final Leaf leaf) {
    // BUG: Diagnostic matches: NO_HINT
    final Map<Leaf, String> m = new HashMap<>();
    m.put(leaf, "x");
    System.out.println(m.get(leaf));
  }

  void localMapSearchedThroughView(final Leaf leaf) {
    // BUG: Diagnostic matches: NO_HINT
    final Map<Leaf, String> m = new HashMap<>();
    m.put(leaf, "x");
    System.out.println(m.keySet().contains(leaf));
  }
}
