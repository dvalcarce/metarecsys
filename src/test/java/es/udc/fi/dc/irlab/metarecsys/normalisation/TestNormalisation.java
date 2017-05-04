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

import org.junit.Assert;
import org.junit.Test;

import es.udc.fi.dc.irlab.metarecsys.structures.RankScore;
import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;

/**
 * The Class TestNormalisation.
 *
 * @author daniel.valcarce@udc.es
 */
public class TestNormalisation {

    /**
     * Test none normalisation.
     */
    @Test
    public void testNoneNormalisation() {

        final RankScore a = new RankScore(0, 2.0);
        final RankScore b = new RankScore(1, 4.0);
        final RankScore c = new RankScore(2, 8.0);
        final LongObjMap<RankScore> userRanking1 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { a, b, c });

        final RankScore d = new RankScore(0, 2.0);
        final RankScore e = new RankScore(1, 4.0);
        final RankScore f = new RankScore(2, 8.0);
        final LongObjMap<RankScore> userRanking2 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { d, e, f });

        final NoneNormalisation norm = new NoneNormalisation();

        Assert.assertEquals(userRanking2, norm.apply(userRanking1));

    }

    /**
     * Test standard normalisation.
     */
    @Test
    public void testStandardNormalisation() {

        final RankScore a = new RankScore(0, 2.0);
        final RankScore b = new RankScore(1, 4.0);
        final RankScore c = new RankScore(2, 8.0);
        final LongObjMap<RankScore> userRanking1 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { a, b, c });

        final RankScore d = new RankScore(0, 0.0);
        final RankScore e = new RankScore(1, 1.0 / 3.0);
        final RankScore f = new RankScore(2, 1.0);
        final LongObjMap<RankScore> userRanking2 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { d, e, f });

        final StandardNormalisation norm = new StandardNormalisation();

        Assert.assertEquals(userRanking2, norm.apply(userRanking1));

    }

    /**
     * Test sum normalisation.
     */
    @Test
    public void testSumNormalisation() {

        final RankScore a = new RankScore(0, 2.0);
        final RankScore b = new RankScore(1, 4.0);
        final RankScore c = new RankScore(2, 8.0);
        final LongObjMap<RankScore> userRanking1 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { a, b, c });

        final RankScore d = new RankScore(0, 0.0);
        final RankScore e = new RankScore(1, 0.25);
        final RankScore f = new RankScore(2, 0.75);
        final LongObjMap<RankScore> userRanking2 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { d, e, f });

        final SumNormalisation norm = new SumNormalisation();

        Assert.assertEquals(userRanking2, norm.apply(userRanking1));

    }

    /**
     * Test sum normalisation.
     */
    @Test
    public void testZMUVNormalisationWithoutOffset() {

        final RankScore a = new RankScore(0, 2.0);
        final RankScore b = new RankScore(1, 4.0);
        final RankScore c = new RankScore(2, 8.0);
        final LongObjMap<RankScore> userRanking1 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { a, b, c });

        final RankScore d = new RankScore(0, -4.0 / Math.sqrt(21));
        final RankScore e = new RankScore(1, -1.0 / Math.sqrt(21));
        final RankScore f = new RankScore(2, 5.0 / Math.sqrt(21));
        final LongObjMap<RankScore> userRanking2 = HashLongObjMaps.<RankScore> newImmutableMap(
                new long[] { 1L, 2L, 3L }, new RankScore[] { d, e, f });

        final ZMUVNormalisation norm = new ZMUVNormalisation(0.0);

        Assert.assertEquals(userRanking2, norm.apply(userRanking1));

    }

}
