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

import java.util.HashMap;
import java.util.Map;

import es.udc.fi.dc.irlab.metarecsys.structures.RankScore;
import net.openhft.koloboke.collect.map.LongObjMap;

/**
 * The abstract class NormalisationAlgorithm.
 *
 * @author daniel.valcarce@udc.es
 */
public abstract class NormalisationAlgorithm {

    /**
     * The Functional Interface NormalisationBuilder.
     *
     * @author daniel.valcarce@udc.es
     */
    @FunctionalInterface
    interface NormalisationBuilder {

        /**
         * Builds a normalisation algorithm.
         *
         * @return the normalisation algorithm
         */
        NormalisationAlgorithm build();

    }

    /** This structure maps each normalisation algorithm name to its builder. */
    private static final Map<String, NormalisationBuilder> algorithmsMap;

    static {
        algorithmsMap = new HashMap<String, NormalisationBuilder>();
        algorithmsMap.put("none", () -> new NoneNormalisation());
        algorithmsMap.put("standard", () -> new StandardNormalisation());
        algorithmsMap.put("sum", () -> new SumNormalisation());
        algorithmsMap.put("zmuv", () -> new ZMUVNormalisation(0.0));
        algorithmsMap.put("zmuv1", () -> new ZMUVNormalisation(1.0));
        algorithmsMap.put("zmuv2", () -> new ZMUVNormalisation(2.0));
    }

    /**
     * Builds the normalisation algorithm specified by its name.
     *
     * @param name
     *            the name
     * @return the normalisation algorithm
     */
    public static NormalisationAlgorithm build(final String name) {

        final NormalisationBuilder normBuilder = algorithmsMap.get(name);

        if (normBuilder == null) {
            throw new IllegalArgumentException(name + " is not a valid normalisation algorithm");
        }

        return normBuilder.build();

    }

    /**
     * Apply the normalisation to the given user ranking.
     *
     * @param userRanking
     *            the ranking of a user
     * @return a modified ranking
     */
    public abstract LongObjMap<RankScore> apply(LongObjMap<RankScore> userRanking);

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public abstract String toString();

}
