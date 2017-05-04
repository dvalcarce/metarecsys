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
package es.udc.fi.dc.irlab.metarecsys.structures;

/**
 * A primitive implementation of a {@code Pair<Integer, Double>}.
 *
 * @author daniel.valcarce@udc.es
 */
public final class RankScore {

    /** The rank. */
    private int rank;

    /** The score. */
    private double score;

    /**
     * Instantiates a new LongDouble.
     *
     * @param rank
     *            the rank
     * @param score
     *            the score
     */
    public RankScore(final int rank, final double score) {
        this.rank = rank;
        this.score = score;
    }

    /**
     * Gets the rank.
     *
     * @return the rank
     */
    public final int getRank() {
        return rank;
    }

    /**
     * Sets the rank.
     *
     * @param rank
     *            the new rank
     */
    public final void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * Gets the score.
     *
     * @return the score
     */
    public final double getScore() {
        return score;
    }

    /**
     * Sets the score.
     *
     * @param score
     *            the new score
     */
    public final void setScore(final double score) {
        this.score = score;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return "LongDouble[" + rank + ", " + score + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return rank ^ Double.hashCode(score);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof RankScore)) {
            return false;
        }
        final RankScore other = (RankScore) o;
        return rank == other.getRank() && Math.abs(score - other.getScore()) < 1e-7;
    }

}
