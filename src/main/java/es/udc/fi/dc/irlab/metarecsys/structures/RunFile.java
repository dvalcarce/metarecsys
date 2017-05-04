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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import es.udc.fi.dc.irlab.metarecsys.normalisation.NormalisationAlgorithm;
import net.openhft.koloboke.collect.map.LongObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;

/**
 * The Class RunFile.
 *
 * @author daniel.valcarce@udc.es
 */
public class RunFile {

    /** The file. */
    private final Path runPath;

    /** The rankings. */
    private final LongObjMap<LongObjMap<RankScore>> rankings;

    /** The fold. */
    private final int fold;

    /** The norm. */
    private final NormalisationAlgorithm norm;

    /** The max rank. */
    private final int maxRank;

    /** The items. */
    private final LongSet items = HashLongSets.newUpdatableSet();

    /** The name. */
    private final String name;

    /**
     * Read the runs from the given folder.
     *
     * @param folder
     *            the folder
     * @param maxRank
     *            the max rank
     * @param norm
     *            the norm
     * @return a concurrent map mapping each fold to its RunFile objects
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static ConcurrentMap<Integer, List<RunFile>> readRuns(final Path folder,
            final int maxRank, final NormalisationAlgorithm norm) throws IOException {

        final Pattern pattern = Pattern.compile("run-.*-fold[0-9]+\\.txt");

        return Files.list(folder).parallel()
                .filter(path -> pattern.matcher(path.getFileName().toString()).matches())
                .map(path -> new RunFile(path, maxRank, norm))
                .collect(Collectors.groupingByConcurrent(RunFile::getFold));

    }

    /**
     * Instantiates a new RunFile.
     *
     * @param runPath
     *            the path to the TREC run file
     * @param maxRank
     *            the maximum number of items in a user ranking
     * @param norm
     *            the normalisation algorithm
     */
    public RunFile(final Path runPath, final int maxRank, final NormalisationAlgorithm norm) {

        if (runPath == null || norm == null) {
            throw new IllegalArgumentException();
        }

        final String runName = runPath.getFileName().toString();
        this.name = runName.substring(4, runName.lastIndexOf("-fold"));

        Logger.getGlobal()
                .info(String.format(Locale.ENGLISH, "Reading %s\t(norm %s)", runName, norm));

        this.runPath = runPath;
        this.norm = norm;
        this.maxRank = maxRank;

        this.fold = computeFold();
        this.rankings = readRun();

    }

    /**
     * Read recommendations from a TREC run file.
     *
     * @return map of <user, recommendations>
     */
    private LongObjMap<LongObjMap<RankScore>> readRun() {

        final LongObjMap<LongObjMap<RankScore>> run = HashLongObjMaps
                .<LongObjMap<RankScore>> newUpdatableMap();
        LongObjMap<RankScore> prefs = HashLongObjMaps.<RankScore> newUpdatableMap(maxRank);

        int lineNumber = 0;

        try (final BufferedReader br = Files.newBufferedReader(runPath)) {

            long userID = Long.MIN_VALUE;
            long newUserID = Long.MIN_VALUE;
            lineNumber = 1;

            for (String line; (line = br.readLine()) != null; lineNumber++) {

                final String[] fields = line.split("\t");
                newUserID = Long.parseLong(fields[0]);

                // Check if we have a new user
                if (userID != newUserID) {
                    // Save old user data and create array for the new data.
                    if (userID != Long.MIN_VALUE) {
                        run.put(userID, norm.apply(prefs));
                        items.addAll(prefs.keySet());
                        prefs = HashLongObjMaps.<RankScore> newUpdatableMap(maxRank);
                    }
                    userID = newUserID;
                }

                final int rank = Integer.parseInt(fields[3]);

                // Exceeding maxRank
                if (rank + 1 > maxRank) {
                    continue;
                }

                final long itemID = Long.parseLong(fields[2]);
                final double score = Double.parseDouble(fields[4]);
                prefs.put(itemID, new RankScore(rank, score));
            }

            run.put(newUserID, norm.apply(prefs));
            items.addAll(prefs.keySet());

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            Logger.getGlobal().severe(String.format(Locale.ENGLISH,
                    "Error in line number %d in file %s", lineNumber, runPath));
            throw e;
        }

        return run;

    }

    /**
     * Get the fold for the current run file.
     *
     * @return fold
     */
    private int computeFold() {
        final Pattern pattern = Pattern.compile(".*fold([0-9]+)\\.txt");
        final Matcher matcher = pattern.matcher(runPath.toString());
        matcher.find();
        return Integer.parseInt(matcher.group(1));
    }

    /**
     * Gets the users.
     *
     * @return the users
     */
    public LongSet getUsers() {
        return rankings.keySet();
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    public LongSet getItems() {
        return items;
    }

    /**
     * Gets the norm.
     *
     * @return the norm
     */
    public NormalisationAlgorithm getNorm() {
        return norm;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the fold.
     *
     * @return the fold
     */
    public final int getFold() {
        return fold;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public Path getPath() {
        return runPath;
    }

    /**
     * Gets the ranking for the given userID.
     *
     * @param userID
     *            the userID
     * @return the ranking
     */
    public LongObjMap<RankScore> getRanking(final long userID) {
        return rankings.get(userID);
    }

    /**
     * Get the score for the given user and item.
     *
     * @param userID
     *            the user id
     * @param itemID
     *            the item id
     * @param defaultScore
     *            the default score
     * @return the score or defaultScore if there is no score
     */
    public double getScore(final long userID, final long itemID, final double defaultScore) {

        final RankScore rankScore = rankings.get(userID).get(itemID);

        return rankScore == null ? defaultScore : rankScore.getScore();

    }

    /**
     * Get the rank for the given user and item.
     *
     * @param userID
     *            the user id
     * @param itemID
     *            the item id
     * @param defaultRank
     *            the default rank
     * @return the rank or defaultRank if there is no rank
     */
    public int getRank(final long userID, final long itemID, final int defaultRank) {
        final RankScore rankScore = rankings.get(userID).get(itemID);

        return rankScore == null ? defaultRank : rankScore.getRank();

    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "RunFile[%s,max_rank=%d,%s]", runPath, maxRank, norm);
    }

}
