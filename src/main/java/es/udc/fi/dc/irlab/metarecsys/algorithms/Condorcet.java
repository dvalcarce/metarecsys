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

import es.udc.fi.dc.irlab.metarecsys.structures.MutableDouble;
import es.udc.fi.dc.irlab.metarecsys.structures.RunFile;
import net.openhft.koloboke.collect.set.LongSet;

/**
 * The Class Condorcet.
 *
 * Mark Montague and Javed A. Aslam. 2002. Condorcet fusion for improved
 * retrieval. In Proceedings of the eleventh international conference on
 * Information and knowledge management (CIKM '02). ACM, New York, NY, USA,
 * 538-548. DOI=http://dx.doi.org/10.1145/584792.584881
 *
 * @author daniel.valcarce@udc.es
 */
public final class Condorcet extends RankAggregation {

    /**
     * Instantiates a new Condorcet algorithm.
     *
     * @param maxRank
     *            the max rank
     */
    protected Condorcet(final int maxRank) {
        super(maxRank);
    }

    /**
     * Gets the Condorcet comparator. Be careful, this {@code Comparator<Long>}
     * is not transitive and some sorting algorithms may fail. For example, Java
     * 7+ TimSort implementation does not work with it. This is the reason why
     * we set java.util.Arrays.useLegacyMergeSort property to true.
     *
     * The Condorcet comparator returns -1 if item x wins, 0 if it is a tie, 1
     * otherwise.
     *
     * @param userID
     *            the user id
     * @param runs
     *            the runs
     * @return the condorcet comparator
     */
    public static Comparator<Long> getCondorcetComparator(final long userID,
            final Set<RunFile> runs) {

        return (x, y) -> {
            int count = 0;
            for (final RunFile run : runs) {
                count += Integer.compare(run.getRank(userID, x, Integer.MAX_VALUE),
                        run.getRank(userID, y, Integer.MAX_VALUE));
            }
            return count == 0 ? count : count / Math.abs(count);
        };

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

        final MutableDouble score = new MutableDouble(maxRank);

        // Condorcet comparator.
        final Comparator<Long> cmp = getCondorcetComparator(userID, runs);

        // Sort items according to Condorcet comparator
        candidateItems.stream().sorted(cmp).forEachOrdered(itemID -> {
            saveScore(ranking, itemID, score.get());
            score.add(-1.0);
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
        return "condorcet";
    }

}
