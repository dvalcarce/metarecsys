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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import es.udc.fi.dc.irlab.metarecsys.normalisation.NoneNormalisation;
import es.udc.fi.dc.irlab.metarecsys.structures.RunFile;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import net.openhft.koloboke.collect.set.hash.HashObjSets;

/**
 * The Class TestMetaRecSys. It runs some very basic tests on the metarecsys
 * algorithms.
 *
 * @author daniel.valcarce@udc.es
 */
public class TestMetaRecSys {

    /** The max rank. */
    private final int maxRank = 4;

    /** The runs. */
    private Set<RunFile> runs;

    /** The user id. */
    private final long userID = 1;

    /** The all items. */
    private LongSet allItems;

    /** The ranking. */
    private final TreeMap<Double, LongSet> ranking = new TreeMap<Double, LongSet>();

    /**
     * Initialise.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Before
    public void initialise() throws IOException {
        final Path runPath1 = Files.createTempFile("run-meta1", "-fold1.txt");

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(runPath1))) {
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 3, 0, 5.0));
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 1, 1, 3.0));
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 2, 2, 1.0));
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 4, 3, 0.5));
        }

        final Path runPath2 = Files.createTempFile("run-meta2", "-fold1.txt");

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(runPath2))) {
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 1, 0, 2.0));
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 2, 1, 1.5));
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 3, 2, 1.0));
            writer.println(
                    String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t-", userID, 4, 3, 0.5));
        }

        final RunFile run1 = new RunFile(runPath1, maxRank, new NoneNormalisation());
        final RunFile run2 = new RunFile(runPath2, maxRank, new NoneNormalisation());
        runs = HashObjSets.newImmutableSet(new RunFile[] { run1, run2 });
        allItems = run1.getItems();
    }

    /**
     * Test Borda.
     */
    @Test
    public void testBorda() {

        final RankAggregation borda = RankAggregation.build("borda", maxRank);

        borda.computeUserRanking(runs, userID, allItems, ranking);

        final TreeMap<Double, LongSet> userRanking = new TreeMap<Double, LongSet>();
        userRanking.put(5.0, HashLongSets.newUpdatableSet(new long[] { 1 }));
        userRanking.put(4.0, HashLongSets.newUpdatableSet(new long[] { 3 }));
        userRanking.put(3.0, HashLongSets.newUpdatableSet(new long[] { 2 }));
        userRanking.put(0.0, HashLongSets.newUpdatableSet(new long[] { 4 }));

        Assert.assertEquals(userRanking, ranking);

    }

    /**
     * Test Condorcet.
     */
    @Test
    public void testCondorcet() {

        final RankAggregation borda = RankAggregation.build("condorcet", maxRank);

        borda.computeUserRanking(runs, userID, allItems, ranking);

        final TreeMap<Double, LongSet> userRanking = new TreeMap<Double, LongSet>();
        userRanking.put(4.0, HashLongSets.newUpdatableSet(new long[] { 3 }));
        userRanking.put(3.0, HashLongSets.newUpdatableSet(new long[] { 1 }));
        userRanking.put(2.0, HashLongSets.newUpdatableSet(new long[] { 2 }));
        userRanking.put(1.0, HashLongSets.newUpdatableSet(new long[] { 4 }));

        Assert.assertEquals(userRanking, ranking);

    }

    /**
     * Test Copeland.
     */
    @Test
    public void testCopeland() {

        final RankAggregation borda = RankAggregation.build("copeland", maxRank);

        borda.computeUserRanking(runs, userID, allItems, ranking);

        final TreeMap<Double, LongSet> userRanking = new TreeMap<Double, LongSet>();
        userRanking.put(2.0, HashLongSets.newUpdatableSet(new long[] { 1 }));
        userRanking.put(1.0, HashLongSets.newUpdatableSet(new long[] { 3 }));
        userRanking.put(0.0, HashLongSets.newUpdatableSet(new long[] { 2 }));
        userRanking.put(-3.0, HashLongSets.newUpdatableSet(new long[] { 4 }));

        Assert.assertEquals(userRanking, ranking);

    }

    /**
     * Test combSum.
     */
    @Test
    public void testCombSum() {

        final RankAggregation borda = RankAggregation.build("combSum", maxRank);

        borda.computeUserRanking(runs, userID, allItems, ranking);

        final TreeMap<Double, LongSet> userRanking = new TreeMap<Double, LongSet>();
        userRanking.put(6.0, HashLongSets.newUpdatableSet(new long[] { 3 }));
        userRanking.put(5.0, HashLongSets.newUpdatableSet(new long[] { 1 }));
        userRanking.put(2.5, HashLongSets.newUpdatableSet(new long[] { 2 }));
        userRanking.put(1.0, HashLongSets.newUpdatableSet(new long[] { 4 }));

        Assert.assertEquals(userRanking, ranking);

    }

    /**
     * Test combAnz.
     */
    @Test
    public void testCombANZ() {

        final RankAggregation borda = RankAggregation.build("combANZ", maxRank);

        borda.computeUserRanking(runs, userID, allItems, ranking);

        final TreeMap<Double, LongSet> userRanking = new TreeMap<Double, LongSet>();
        userRanking.put(3.0, HashLongSets.newUpdatableSet(new long[] { 3 }));
        userRanking.put(2.5, HashLongSets.newUpdatableSet(new long[] { 1 }));
        userRanking.put(1.25, HashLongSets.newUpdatableSet(new long[] { 2 }));
        userRanking.put(0.5, HashLongSets.newUpdatableSet(new long[] { 4 }));

        Assert.assertEquals(userRanking, ranking);

    }

    /**
     * Test combMNZ.
     */
    @Test
    public void testCombMNZ() {

        final RankAggregation borda = RankAggregation.build("combMNZ", maxRank);

        borda.computeUserRanking(runs, userID, allItems, ranking);

        final TreeMap<Double, LongSet> userRanking = new TreeMap<Double, LongSet>();
        userRanking.put(12.0, HashLongSets.newUpdatableSet(new long[] { 3 }));
        userRanking.put(10.0, HashLongSets.newUpdatableSet(new long[] { 1 }));
        userRanking.put(5.0, HashLongSets.newUpdatableSet(new long[] { 2 }));
        userRanking.put(2.0, HashLongSets.newUpdatableSet(new long[] { 4 }));

        Assert.assertEquals(userRanking, ranking);

    }

}
