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

import java.util.Set;
import java.util.TreeMap;
import java.util.function.LongConsumer;

import es.udc.fi.dc.irlab.metarecsys.structures.MutableDouble;
import es.udc.fi.dc.irlab.metarecsys.structures.MutableInt;
import es.udc.fi.dc.irlab.metarecsys.structures.RunFile;
import net.openhft.koloboke.collect.set.LongSet;

/**
 * The Class Comb* (common class for CombANZ, CombMNZ and CombSum.
 *
 * Fox, E. A., &amp; Shaw, J. A. (1994). Combination of Multiple Searches. In
 * Proceedings of the Second Conference on Text Retrieval Conference (pp.
 * 243–252).
 *
 * @author daniel.valcarce@udc.es
 */
public abstract class Comb extends RankAggregation {

    /**
     * Instantiates a new comb*.
     *
     * @param maxRank
     *            the max rank
     */
    protected Comb(final int maxRank) {
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
    protected final void computeUserRanking(final Set<RunFile> runs, final long userID,
            final LongSet candidateItems, final TreeMap<Double, LongSet> ranking) {

        candidateItems.forEach((LongConsumer) itemID -> {

            final MutableInt n = new MutableInt(0);
            final MutableDouble accum = new MutableDouble(0.0);

            runs.forEach(run -> {
                final double score = run.getScore(userID, itemID, Double.NaN);
                if (!Double.isNaN(score)) {
                    n.increment();
                    accum.add(score);
                }
            });

            if (n.get() > 0) {
                saveScore(ranking, itemID, computeScore(n.get(), accum.get()));
            }

        });

    }

    /**
     * Compute the comb* score.
     *
     * @param n
     *            the number of runs with a score for the given item
     * @param accum
     *            the sum of the scores for the item in the runs
     * @return the double
     */
    protected abstract double computeScore(int n, double accum);

    /*
     * (non-Javadoc)
     *
     * @see
     * es.udc.fi.dc.irlab.metarecsys.algorithms.MetasearchAlgorithm#toString()
     */
    @Override
    public String toString() {
        return "combANZ";
    }

}
