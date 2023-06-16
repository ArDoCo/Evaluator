package edu.kit.kastel.mcse.ardoco.evaluator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluationTest {
    private static Logger logger = LoggerFactory.getLogger(EvaluationTest.class);
    private static final Map<String, Integer> nameToConfusionMatrixSum = Map.of( //
            "bigbluebutton", 47600,//
            "jabref", 26000, //
            "mediastore", 3589, //
            "teammates", 165330, //
            "teastore", 8815);

        @Test
    void evaluateTest() {
        Path goldStandardCsvDirectory = Paths.get("src/test/resources/testCSVs/gs-sad-code");
        Path traceLinksCsvDirectory = Paths.get("src/test/resources/testCSVs/tls-sad-code");
        File goldStandardCsvDirectoryFile = goldStandardCsvDirectory.toFile();
        File traceLinksCsvDirectoryFile = traceLinksCsvDirectory.toFile();
        Assertions.assertTrue(goldStandardCsvDirectoryFile.exists() && traceLinksCsvDirectoryFile.exists());

        var goldStandardsFiles = goldStandardCsvDirectoryFile.listFiles();
        var traceLinksFiles = traceLinksCsvDirectoryFile.listFiles();

        for (var traceLinks : traceLinksFiles) {
            var name = traceLinks.getName().replaceAll("\\.csv", "");
            logger.info(name);

            var goldStandardOptional = Arrays.stream(goldStandardsFiles).filter(file -> file.getName().replaceAll("\\.csv", "").equals(name)).findFirst();
            if (goldStandardOptional.isEmpty()) {
                continue;
            }
            int confusionMatrixSum = nameToConfusionMatrixSum.get(name);

            EvaluationResults<String> evaluationResults = Evaluator.evaluate(traceLinks.toPath(), goldStandardOptional.get().toPath(), confusionMatrixSum);
            Assertions.assertNotNull(evaluationResults);
        }
    }

}
