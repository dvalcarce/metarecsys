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

package es.udc.fi.dc.irlab.metarecsys;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import es.udc.fi.dc.irlab.metarecsys.algorithms.RankAggregation;
import es.udc.fi.dc.irlab.metarecsys.normalisation.NormalisationAlgorithm;
import es.udc.fi.dc.irlab.metarecsys.structures.RunFile;

/**
 * The Class Metasearch.
 *
 * @author daniel.valcarce@udc.es
 */
public class MetaRecSys {

    static {
        // Logging format
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n");

        // Use old MergeSort algorithm instead of Java 7+ TimSort algorithm
        // because it supports non-transitive Comparators.
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }

    /** The Constant OUT_OPTION. */
    private static final String OUT_OPTION = "out";

    /** The Constant RUN_OPTION. */
    private static final String RUN_OPTION = "run";

    /** The Constant ALG_OPTION. */
    private static final String ALG_OPTION = "alg";

    /** The Constant NORM_OPTION. */
    private static final String NORM_OPTION = "norm";

    /** The Constant MAX_OPTION. */
    private static final String MAX_OPTION = "max";

    /** The Constant DEFAULT_MAX_RANK. */
    private static final String DEFAULT_MAX_RANK = "100";

    /**
     * Gets the cmd options.
     *
     * @return the cmd options
     */
    private static Options getCmdOptions() {
        final Options options = new Options();

        options.addOption("h", "help", false, "show help");

        final Option algorithm = Option.builder(ALG_OPTION).argName("algorithm_name").hasArg()
                .desc("the metarecsys algorithm to use (borda, condorcet, copeland, combANZ, combSum, combMNZ)")
                .longOpt("algorithm").required().build();
        options.addOption(algorithm);

        final Option runs = Option.builder(RUN_OPTION).argName("folder").hasArg()
                .desc("path to the runs folder").longOpt("runs").required().build();
        options.addOption(runs);

        final Option output = Option.builder(OUT_OPTION).argName("folder").hasArg()
                .desc("path to the output folder").longOpt("output").required().build();
        options.addOption(output);

        final Option norm = Option.builder(NORM_OPTION).argName("norm_name").hasArg()
                .desc("the normalisation technique to use (none, standard, sum, zmuv, zmuv1, zmuv2)")
                .longOpt("normalisation").required().build();
        options.addOption(norm);

        final Option max = Option.builder(MAX_OPTION).argName("num").hasArg()
                .desc("maximum number of recommended items per user (100 by default)")
                .longOpt("max_rank").type(Integer.class).build();
        options.addOption(max);

        return options;
    }

    /**
     * Parses the cmd options.
     *
     * @param options
     *            the options
     * @param args
     *            the args
     * @return the command line
     */
    private static CommandLine parseCmdOptions(final Options options, final String args[]) {
        final CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (final ParseException exp) {
            Logger.getGlobal().severe(exp.getMessage());
            return null;
        }
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws ParseException
     *             the parse exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void main(final String args[]) throws ParseException, IOException {

        // Parse CLI options
        final Options options = getCmdOptions();
        final CommandLine cmd = parseCmdOptions(options, args);

        if (cmd == null || cmd.hasOption("h")) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("metarecsys", options);
            System.exit(0);
        }

        final Path runsFolder = Paths.get(cmd.getOptionValue(RUN_OPTION));
        final Path outputFolder = Paths.get(cmd.getOptionValue(OUT_OPTION));
        final int maxRank = Integer.parseInt(cmd.getOptionValue(MAX_OPTION, DEFAULT_MAX_RANK));

        // Build metarecsys algorithms
        final List<RankAggregation> algs = Arrays.stream(cmd.getOptionValues(ALG_OPTION))
                .map(name -> RankAggregation.build(name, maxRank)).collect(Collectors.toList());

        // For each normalisation algorithm
        for (final String norm : cmd.getOptionValues(NORM_OPTION)) {

            // Read runs by fold
            final ConcurrentMap<Integer, List<RunFile>> runsByFold = RunFile.readRuns(runsFolder,
                    maxRank, NormalisationAlgorithm.build(norm));

            // For each metarecsys algorithm
            for (final RankAggregation alg : algs) {

                // For each fold
                runsByFold.forEach((fold, runs) -> {
                    alg.computeAllCombinations(fold, runs, outputFolder);
                });

            }

        }

        RankAggregation.finishPool();
        Logger.getGlobal().info("Finished!");

    }

}
