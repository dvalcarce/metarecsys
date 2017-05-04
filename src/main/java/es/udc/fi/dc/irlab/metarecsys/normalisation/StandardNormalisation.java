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
 * The Class StandardNormalisation: shift the minimum score to 0.0 and then
 * scale the maximum score to 1.0.
 *
 * Montague, M., &amp; Aslam, J. A. (2001). Relevance Score Normalization for
 * Metasearch. In Proceedings of the Tenth International Conference on
 * Information and Knowledge Management (pp. 427–433). New York, NY, USA: ACM.
 * http://doi.org/10.1145/502585.502657
 *
 * @author daniel.valcarce@udc.es
 */
public final class StandardNormalisation extends NormalisationAlgorithm {

    /*
     * (non-Javadoc)
     *
     * @see
     * es.udc.fi.dc.irlab.metarecsys.normalisation.NormalisationAlgorithm#apply(
     * es.udc.fi.dc.irlab.metarecsys.runs.LongDouble[])
     */
    @Override
    public LongObjMap<RankScore> apply(final LongObjMap<RankScore> userRanking) {

        userRanking.values().stream().mapToDouble(pref -> pref.getScore()).min();

        final double min = userRanking.values().stream().mapToDouble(pref -> pref.getScore()).min()
                .getAsDouble();
        final double max = userRanking.values().stream().mapToDouble(pref -> pref.getScore()).max()
                .getAsDouble();
        final double den = max - min;

        userRanking.values().stream().forEach(pref -> pref.setScore((pref.getScore() - min) / den));

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
        return "standard";
    }

}
