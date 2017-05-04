/*
 * Copyright 2016 Information Retrieval Lab - University of A Coruña
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.udc.fi.dc.irlab.metarecsys.algorithms;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.LongConsumer;

import es.udc.fi.dc.irlab.metarecsys.structures.RunFile;
import net.openhft.koloboke.collect.map.LongIntMap;
import net.openhft.koloboke.collect.map.hash.HashLongIntMaps;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.function.LongIntConsumer;

/**
 * The Class Copeland. Copeland's method or Copeland's pairwise aggregation
 * method is a Condorcet method in which candidates are ordered by the number of
 * pairwise victories, minus the number of pairwise defeats.[1]
 *
 * @author daniel.valcarce@udc.es
 */
public final class Copeland extends RankAggregation {

    /**
     * Instantiates a new Copeland algorithm.
     *
     * @param maxRank
     *            the max rank
     */
    protected Copeland(final int maxRank) {
        super(maxRank);
    }

    /*
     * (non-Javadoc)
     *
     * @see es.udc.fi.dc.irlab.metarecsys.algorithms.MetasearchAlgorithm#
     * computeUserRanking(java.util.Set, long,
     * net.openhft.koloboke.collect.set.LongSet, java.util.TreeMap)
     */
    @Override
    protected void computeUserRanking(final Set<RunFile> runs, final long userID,
            final LongSet candidateItems, final TreeMap<Double, LongSet> ranking) {

        // Condorcet comparator
        final Comparator<Long> cmp = Condorcet.getCondorcetComparator(userID, runs);

        // Scores stores (wins - losses)
        final LongIntMap scores = HashLongIntMaps.newUpdatableMap(candidateItems.size());
        candidateItems.forEach((LongConsumer) itemID1 -> {
            candidateItems.forEach((LongConsumer) itemID2 -> {
                if (itemID1 < itemID2) {
                    final int score = cmp.compare(itemID1, itemID2);
                    scores.addValue(itemID1, -score);
                    scores.addValue(itemID2, score);
                }
            });
        });

        scores.forEach((LongIntConsumer) (itemID, score) -> {
            saveScore(ranking, itemID, score);
        });

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * es.udc.fi.dc.irlab.metarecsys.algorithms.MetasearchAlgorithm#toString()
     */
    @Override
    public String toString() {
        return "copeland";
    }

}
