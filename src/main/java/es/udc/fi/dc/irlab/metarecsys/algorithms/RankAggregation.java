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
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.LongConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import es.udc.fi.dc.irlab.metarecsys.structures.RunFile;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;

/**
 * The Class MetasearchAlgorithm.
 *
 * @author daniel.valcarce@udc.es
 */
public abstract class RankAggregation {

    /** The number of available processors. */
    private static int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /** The Constant pool. */
    private final static ThreadPoolExecutor pool = new ThreadPoolExecutor(NUM_PROCESSORS,
            NUM_PROCESSORS, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

    /**
     * The Functional Interface MetasearchBuilder.
     *
     * @author daniel.valcarce@udc.es
     */
    @FunctionalInterface
    interface MetaRecSysBuilder {

        /**
         * Builds the RankAggregation algorithm.
         *
         * @param maxRank
         *            the max rank
         * @return the RankAggregation algorithm
         */
        RankAggregation build(int maxRank);
    }

    /** This structure maps each normalisation algorithm name to its builder. */
    private static final Map<String, MetaRecSysBuilder> algorithmsMap;

    static {
        algorithmsMap = new HashMap<String, MetaRecSysBuilder>();
        algorithmsMap.put("borda", maxRank -> new BordaCount(maxRank));
        algorithmsMap.put("condorcet", maxRank -> new Condorcet(maxRank));
        algorithmsMap.put("copeland", maxRank -> new Copeland(maxRank));
        algorithmsMap.put("combANZ", maxRank -> new CombANZ(maxRank));
        algorithmsMap.put("combSum", maxRank -> new CombSum(maxRank));
        algorithmsMap.put("combMNZ", maxRank -> new CombMNZ(maxRank));
    }

    /**
     * Finish pool.
     */
    public static void finishPool() {
        pool.shutdown();
        try {
            pool.awaitTermination(5, TimeUnit.DAYS);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /** The max rank. */
    protected int maxRank;

    /**
     * Instantiates a new RankAggregation algorithm.
     *
     * @param maxRank
     *            the max rank
     */
    protected RankAggregation(final int maxRank) {
        this.maxRank = maxRank;
    }

    /**
     * Builds the RankAggregation specified by its name.
     *
     * @param name
     *            the name
     * @param maxRank
     *            the max rank
     * @return the normalisation algorithm
     */
    public static RankAggregation build(final String name, final int maxRank) {

        final MetaRecSysBuilder metaBuilder = algorithmsMap.get(name == null ? "none" : name);

        if (metaBuilder == null) {
            throw new IllegalArgumentException(name + " is not a valid metarecsys algorithm");
        }

        return metaBuilder.build(maxRank);

    }

    /**
     * Compute all combinations.
     *
     * @param fold
     *            the fold
     * @param runs
     *            the runs
     * @param outputFolder
     *            the output folder
     */
    public void computeAllCombinations(final int fold, final List<RunFile> runs,
            final Path outputFolder) {

        if (!Files.exists(outputFolder)) {
            try {
                Files.createDirectory(outputFolder);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to create folder " + outputFolder);
            }
        } else if (!Files.isDirectory(outputFolder)) {
            throw new IllegalArgumentException(
                    "Path " + outputFolder + " exists and it is not a folder");
        }

        // Create a parallel task for each combination
        final int n = runs.size();
        for (int k = 2; k <= n; k++) {

            CombinationUtils.combination(runs, k).forEach(combination -> {

                final Runnable task = () -> {
                    final LongSet allUsers = HashLongSets.newUpdatableSet();
                    combination.forEach(run -> {
                        allUsers.addAll(run.getUsers());
                    });

                    final String files = String.join("-", combination.stream().map(RunFile::getName)
                            .sorted().collect(Collectors.toList()));
                    final String norm = runs.get(0).getNorm().toString();
                    final String filename = String.format(Locale.ENGLISH, "%s-%s-n%d-%s-fold%d.txt",
                            this, norm, combination.size(), files, fold);

                    final Path outputPath = outputFolder.resolve(filename);
                    final Path tempPath = outputFolder.resolve(filename.replace("txt", "tmp"));

                    if (!Files.exists(outputPath) && !Files.exists(tempPath)) {
                        try {
                            Files.createFile(tempPath);
                            Logger.getGlobal().info("Computing " + filename);
                            fuseAndPrint(fold, combination, allUsers, tempPath);
                            Files.move(tempPath, outputPath, StandardCopyOption.ATOMIC_MOVE);
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                };

                pool.execute(task);

            });

        }

    }

    /**
     * Fuse the specified combination of runs and print the result.
     *
     * @param fold
     *            the fold
     * @param runs
     *            the runs of the current combination
     * @param allUsers
     *            all the users in the current combination
     * @param outputFile
     *            the path to the output file
     */
    protected final void fuseAndPrint(final int fold, final Set<RunFile> runs,
            final LongSet allUsers, final Path outputFile) {

        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile))) {

            final LongSet all = HashLongSets.newUpdatableSet(maxRank);
            runs.forEach(run -> {
                all.addAll(run.getItems());
            });

            allUsers.forEach((LongConsumer) userID -> {
                final TreeMap<Double, LongSet> ranking = new TreeMap<Double, LongSet>(
                        Collections.reverseOrder());

                final LongSet candidateItems = HashLongSets.newUpdatableSet(maxRank);
                runs.forEach(run -> {
                    candidateItems.addAll(run.getRanking(userID).keySet());
                });

                computeUserRanking(runs, userID, candidateItems, ranking);
                printRanking(userID, ranking, writer);
            });

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Compute user ranking.
     *
     * @param runs
     *            the runs
     * @param userID
     *            the user id
     * @param candidateItems
     *            the candidate items
     * @param ranking
     *            the ranking
     */
    protected abstract void computeUserRanking(Set<RunFile> runs, long userID,
            LongSet candidateItems, TreeMap<Double, LongSet> ranking);

    /**
     * Save the score for the given itemID in the ranking structure.
     *
     * @param ranking
     *            the ranking
     * @param itemID
     *            the itemID
     * @param score
     *            the score
     */
    protected final void saveScore(final TreeMap<Double, LongSet> ranking, final long itemID,
            final double score) {

        if (ranking.containsKey(score)) {
            ranking.get(score).add(itemID);
        } else {
            ranking.put(score, HashLongSets.newUpdatableSet(new long[] { itemID }));
        }

    }

    /**
     * Prints the ranking for the given user.
     *
     * @param userID
     *            the userID
     * @param ranking
     *            the ranking
     * @param writer
     *            the buffered writer
     */
    private void printRanking(final long userID, final TreeMap<Double, LongSet> ranking,
            final PrintWriter writer) {

        final String blank = "-";

        int rank = 0;
        String recommenderName = this.toString();

        for (final Map.Entry<Double, LongSet> entry : ranking.entrySet()) {
            final double score = entry.getKey();
            final LongSet items = entry.getValue();
            for (final long itemID : items) {
                writer.println(String.format(Locale.ENGLISH, "%d\tQ0\t%d\t%d\t%f\t%s", userID,
                        itemID, rank, score, recommenderName));
                recommenderName = blank;
                if (++rank == maxRank) {
                    return;
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public abstract String toString();

}
