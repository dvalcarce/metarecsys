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
package es.udc.fi.dc.irlab.metarecsys.normalisation;

import es.udc.fi.dc.irlab.metarecsys.structures.RankScore;
import net.openhft.koloboke.collect.map.LongObjMap;

/**
 * The Class ZMUVNormalisation. Shift the mean score to 0.0 and then scale the
 * variance score to 1.0.
 *
 * Montague, M., &amp; Aslam, J. A. (2001). Relevance Score Normalization for
 * Metasearch. In Proceedings of the Tenth International Conference on
 * Information and Knowledge Management (pp. 427–433). New York, NY, USA: ACM.
 * http://doi.org/10.1145/502585.502657
 *
 * @author daniel.valcarce@udc.es
 */
public final class ZMUVNormalisation extends NormalisationAlgorithm {

    /** The offset. */
    private final double offset;

    /**
     * Instantiates a new ZMUV normalisation.
     *
     * @param offset
     *            the offset
     */
    public ZMUVNormalisation(final double offset) {
        this.offset = offset;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * es.udc.fi.dc.irlab.metarecsys.normalisation.NormalisationAlgorithm#apply(
     * es.udc.fi.dc.irlab.metarecsys.runs.LongDouble[])
     */
    @Override
    public LongObjMap<RankScore> apply(final LongObjMap<RankScore> userRanking) {

        final double n = userRanking.size();

        // Compute the mean
        final double mean = userRanking.values().stream().mapToDouble(p -> p.getScore()).sum() / n;

        // Compute the standard deviation
        final double meanSquared = userRanking.values().stream()
                .mapToDouble(p -> p.getScore() * p.getScore()).sum() / n;
        final double std = Math.sqrt((meanSquared - mean * mean) * n / (n - 1));

        userRanking.values().stream()
                .forEach(p -> p.setScore(offset + (p.getScore() - mean) / std));

        return userRanking;

    }

    /*
     * (non-Javadoc)
     *
     * @see es.udc.fi.dc.irlab.metarecsys.normalisation.NormalisationAlgorithm#
     * toString()
     */
    @Override
    public String toString() {
        final String offsetPrint = offset != 0.0 ? String.valueOf(offset) : "";
        return "zmuv" + offsetPrint;
    }

}
