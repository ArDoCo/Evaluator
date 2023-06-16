package edu.kit.kastel.mcse.ardoco.evaluator.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.kastel.mcse.ardoco.evaluator.Evaluator;

public class EvaluatorCLI {
    private static final Logger logger = LoggerFactory.getLogger(EvaluatorCLI.class);

    private static final String CMD_HELP = "h";
    private static final String CMD_TRACE_LINK_CSV = "t";
    private static final String CMD_GOLD_STANDARD_CSV = "g";
    private static final String CMD_CONFUSION_MATRIX_SUM = "c";



    private static Options options;

    private EvaluatorCLI() {
        throw new IllegalAccessError();
    }

    public static void main(String[] args) {
        CommandLine cmd;
        try {
            cmd = parseCommandLine(args);
        } catch (IllegalArgumentException | ParseException e) {
            logger.error(e.getMessage());
            printUsage();
            return;
        }

        if (cmd.hasOption(CMD_HELP)) {
            printUsage();
            return;
        }

        Path traceLinkCsv;
        Path goldStandardCsv;
        int confusionMatrixSum = -1;
        try {
            traceLinkCsv = ensurePath(cmd.getOptionValue(CMD_TRACE_LINK_CSV));
            goldStandardCsv = ensurePath(cmd.getOptionValue(CMD_GOLD_STANDARD_CSV));
            confusionMatrixSum = (Integer) cmd.getParsedOptionValue(CMD_CONFUSION_MATRIX_SUM);
        } catch (IOException | ParseException e) {
            logger.warn("Could not properly handle input. Aborting.", e);
            return;
        }

        Evaluator.evaluate(traceLinkCsv, goldStandardCsv, confusionMatrixSum);
    }


    private static void printUsage() {
        var formatter = new HelpFormatter();
        formatter.printHelp("java -jar evaluator.jar", options);
    }

    private static CommandLine parseCommandLine(String[] args) throws ParseException {
        options = new Options();
        Option opt;

        // Define Options ..
        opt = new Option(CMD_HELP, "help", false, "show this message");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option(CMD_TRACE_LINK_CSV, "traceLink", true, "Path to the CSV for the trace links");
        opt.setRequired(true);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(CMD_GOLD_STANDARD_CSV, "goldStandard", true, "Path to the CSV for the gold standard");
        opt.setRequired(true);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(CMD_CONFUSION_MATRIX_SUM, "confusionMatrixSum", true, "Integer for the size of the solution space (= number of artifacts on one side times the number of artifacts on the other side)");
        opt.setRequired(false);
        opt.setType(Integer.class);
        options.addOption(opt);

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static Path ensurePath(String path) throws IOException {
        if (path == null || path.isBlank()) {
            throw new IOException("The specified file does not exist and/or could not be created: " + path);
        }
        var file = new File(path);
        if (file.exists()) {
            return Path.of(file.toURI());
        }
        // File not available
        throw new IOException("The specified file does not exist and/or could not be created: " + path);
    }
}
