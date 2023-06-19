package edu.kit.kastel.mcse.ardoco.evaluator.cli;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluatorCliTest {
    private static final Logger logger = LoggerFactory.getLogger(EvaluatorCliTest.class);
    private static final Map<String, Integer> nameToConfusionMatrixSum = Map.of( //
            "bigbluebutton", 47600,//
            "jabref", 26000, //
            "mediastore", 3589, //
            "teammates", 165330, //
            "teastore", 8815);
    private EvaluatorCli cli;

    @BeforeEach
    void setUp() {
        cli = new EvaluatorCli();
    }

    @Test
    @DisplayName("With no arguments")
    void cliNoArgumentsTest() {
        String[] args = new String[0];

        cli.startEvaluator(args);
        Assertions.assertNull(cli.getLastResults());
    }

    @Test
    @DisplayName("With no ConfusionMatrixSum")
    void cliNoConfusionMatrixSumTest() {
        String[] args = new String[] {//
                "-g", "src\\test\\resources\\testCSVs\\gs-sad-code\\teammates.csv", //
                "-t", "src\\test\\resources\\testCSVs\\tls-sad-code\\teammates.csv"//
        };
        cli.startEvaluator(args);
        Assertions.assertNotNull(cli.getLastResults());
    }

    @Test
    @DisplayName("With ConfusionMatrixSum")
    void cliWithConfusionMatrixSumTest() {
        String[] args = new String[] {//
                "-g", "src\\test\\resources\\testCSVs\\gs-sad-code\\teammates.csv", //
                "-t", "src\\test\\resources\\testCSVs\\tls-sad-code\\teammates.csv",//
                "-c", "165330" //
        };
        cli.startEvaluator(args);
        Assertions.assertNotNull(cli.getLastResults());
    }

}
