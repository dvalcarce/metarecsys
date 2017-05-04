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
import es.udc.fi.dc.irlab.metarecsys.structures.RunFile;
import net.openhft.koloboke.collect.set.LongSet;

/**
 * The Class BordaCount.
 *
 * Javed A. Aslam and Mark Montague. 2001. Models for metasearch. In Proceedings
 * of the 24th annual international ACM SIGIR conference on Research and
 * development in information retrieval (SIGIR '01). ACM, New York, NY, USA,
 * 276-284. DOI=http://dx.doi.org/10.1145/383952.384007
 *
 * @author daniel.valcarce@udc.es
 */
public final class BordaCount extends RankAggregation {

    /**
     * Instantiates a new borda count.
     *
     * @param maxRank
     *            the max rank
     */

    protected BordaCount(final int maxRank) {
        super(maxRank);
    }

    /*
     * (non-Javadoc)
     *
     * @see es.udc.fi.dc.irlab.metarecsys.algorithms.MetasearchAlgorithm#
     * fuseRunsAndPrint(int, java.util.Set,
     * net.openhft.koloboke.collect.set.LongSet,
     * net.openhft.koloboke.collect.set.LongSet, java.nio.file.Path)
     */
    @Override
    protected void computeUserRanking(final Set<RunFile> runs, final long userID,
            final LongSet candidateItems, final TreeMap<Double, LongSet> ranking) {

        final int c = maxRank - 1;

        candidateItems.forEach((LongConsumer) itemID -> {
            final MutableDouble score = new MutableDouble(0.0);
            runs.forEach(run -> {
                score.add(c - run.getRank(userID, itemID, c));
            });
            saveScore(ranking, itemID, score.get());
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
        return "borda";
    }

}
