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
 * The Class NoneNormalisation. This class applies no normalisation.
 *
 * @author daniel.valcarce@udc.es
 */
public final class NoneNormalisation extends NormalisationAlgorithm {

    /*
     * (non-Javadoc)
     *
     * @see
     * es.udc.fi.dc.irlab.metarecsys.normalisation.NormalisationAlgorithm#apply(
     * es.udc.fi.dc.irlab.metarecsys.runs.LongDouble[])
     */
    @Override
    public LongObjMap<RankScore> apply(final LongObjMap<RankScore> prefs) {
        return prefs;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "none";
    }

}
