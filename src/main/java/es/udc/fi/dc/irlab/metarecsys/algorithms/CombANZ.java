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

/**
 * The Class CombANZ.
 *
 * Fox, E. A., &amp; Shaw, J. A. (1994). Combination of Multiple Searches. In
 * Proceedings of the Second Conference on Text Retrieval Conference (pp.
 * 243–252).
 *
 * @author daniel.valcarce@udc.es
 */
public final class CombANZ extends Comb {

    /**
     * Instantiates a new combANZ.
     *
     * @param maxRank
     *            the max rank
     */
    protected CombANZ(final int maxRank) {
        super(maxRank);
    }

    /*
     * (non-Javadoc)
     *
     * @see es.udc.fi.dc.irlab.metarecsys.algorithms.Comb#computeScore(int,
     * double)
     */
    @Override
    protected double computeScore(final int n, final double accum) {
        return accum / n;
    }

    /*
     * (non-Javadoc)
     *
     * @see es.udc.fi.dc.irlab.metarecsys.algorithms.Comb#toString()
     */
    @Override
    public String toString() {
        return "combANZ";
    }

}
