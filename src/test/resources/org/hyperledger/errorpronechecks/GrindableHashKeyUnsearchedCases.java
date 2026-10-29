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
import org.apache.tuweni.bytes.Bytes32;

/**
 * Every site here is grindable. HINT marks those that are provably only populated and iterated, so
 * the message should suggest a list; NO_HINT marks those that are searched or escape.
 */
public class GrindableHashKeyUnsearchedCases {

  // BUG: Diagnostic matches: SET_HINT
  private final Set<Bytes32> privateIterated = new HashSet<>();

  // Not private: other classes may search it.
  // BUG: Diagnostic matches: NO_HINT
  final Set<Bytes32> packageVisible = new HashSet<>();

  private final Set<Bytes32> assignedInConstructor;

  GrindableHashKeyUnsearchedCases() {
    // BUG: Diagnostic matches: SET_HINT
    assignedInConstructor = new HashSet<>();
  }

  void fill(final Bytes32 key) {
    privateIterated.add(key);
    this.assignedInConstructor.add(key);
    packageVisible.add(key);
  }

  long count() {
    return privateIterated.stream().count() + assignedInConstructor.size();
  }

  void localSetOnlyIterated(final Iterable<Bytes32> in) {
    // BUG: Diagnostic matches: SET_HINT
    final Set<Bytes32> s = new HashSet<>();
    for (final Bytes32 l : in) {
      s.add(l);
    }
    for (final Bytes32 l : s) {
      System.out.println(l);
    }
    s.forEach(l -> System.out.println(l));
  }

  void localSetSearched(final Bytes32 key) {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Bytes32> s = new HashSet<>();
    s.add(key);
    System.out.println(s.contains(key));
  }

  void localSetAddResultUsed(final Bytes32 key) {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Bytes32> s = new HashSet<>();
    if (s.add(key)) {
      System.out.println(key);
    }
  }

  Set<Bytes32> localSetReturned() {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Bytes32> s = new HashSet<>();
    return s;
  }

  void localSetPassedAlong() {
    // BUG: Diagnostic matches: NO_HINT
    final Set<Bytes32> s = new HashSet<>();
    System.out.println(s);
  }

  void localMapOnlyIterated(final Bytes32 key) {
    // BUG: Diagnostic matches: MAP_HINT
    final Map<Bytes32, String> m = new HashMap<>();
    m.put(key, "x");
    for (final Map.Entry<Bytes32, String> e : m.entrySet()) {
      System.out.println(e);
    }
    m.values().forEach(System.out::println);
  }

  void localMapSearched(final Bytes32 key) {
    // BUG: Diagnostic matches: NO_HINT
    final Map<Bytes32, String> m = new HashMap<>();
    m.put(key, "x");
    System.out.println(m.get(key));
  }

  void localMapSearchedThroughView(final Bytes32 key) {
    // BUG: Diagnostic matches: NO_HINT
    final Map<Bytes32, String> m = new HashMap<>();
    m.put(key, "x");
    System.out.println(m.keySet().contains(key));
  }
}
