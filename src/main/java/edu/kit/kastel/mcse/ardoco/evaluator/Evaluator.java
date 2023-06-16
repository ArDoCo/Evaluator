package edu.kit.kastel.mcse.ardoco.evaluator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Evaluator {
    private static Logger logger = LoggerFactory.getLogger(Evaluator.class);

    private Evaluator() {
        throw new IllegalStateException();
    }

    public static EvaluationResults<String> evaluate(Path traceLinkPath, Path goldStandardPath, int confusionMatrixSum) {
        var traceLinks = getLinesFromCsvFile(traceLinkPath);
        var goldStandard = getLinesFromCsvFile(goldStandardPath);

        return evaluate(traceLinks, goldStandard, confusionMatrixSum);
    }

    public static EvaluationResults<String> evaluate(ImmutableList<String> traceLinks, ImmutableList<String> goldStandard, int confusionMatrixSum) {
        EvaluationResults<String> evaluationResults = calculateEvaluationResults(traceLinks, goldStandard, confusionMatrixSum);
        logger.info(evaluationResults.getExtendedResultsString());
        return evaluationResults;
    }

    private static EvaluationResults<String> calculateEvaluationResults(ImmutableList<String> traceLinks, ImmutableCollection<String> goldStandard, int confusionMatrixSum) {
        Set<String> distinctTraceLinks = new HashSet<>(traceLinks.castToCollection());
        Set<String> distinctGoldStandard = new HashSet<>(goldStandard.castToCollection());

        // True Positives are the trace links that are contained on both lists
        Set<String> truePositives = distinctTraceLinks.stream()
                .filter(tl -> isTraceLinkContainedInGoldStandard(tl, distinctGoldStandard))
                .collect(Collectors.toSet());
        ImmutableList<String> truePositivesList = Lists.immutable.ofAll(truePositives);

        // False Positives are the trace links that are only contained in the result set
        Set<String> falsePositives = distinctTraceLinks.stream()
                .filter(tl -> !isTraceLinkContainedInGoldStandard(tl, distinctGoldStandard))
                .collect(Collectors.toSet());
        ImmutableList<String> falsePositivesList = Lists.immutable.ofAll(falsePositives);

        // False Negatives are the trace links that are only contained in the gold standard
        Set<String> falseNegatives = distinctGoldStandard.stream()
                .filter(gstl -> !isGoldStandardTraceLinkContainedInTraceLinks(gstl, distinctTraceLinks))
                .collect(Collectors.toSet());
        ImmutableList<String> falseNegativesList = Lists.immutable.ofAll(falseNegatives);

        int trueNegatives = confusionMatrixSum - truePositives.size() - falsePositives.size() - falseNegatives.size();
        return EvaluationResults.createEvaluationResults(new ResultMatrix<>(truePositivesList, trueNegatives, falsePositivesList, falseNegativesList));
    }

    private static boolean areTraceLinksMatching(String goldStandardTraceLink, String traceLink) {
        traceLink = traceLink.strip();
        goldStandardTraceLink = goldStandardTraceLink.strip();
        return (goldStandardTraceLink.equals(traceLink));
    }

    private static boolean isTraceLinkContainedInGoldStandard(String traceLink, Set<String> goldStandard) {
        return goldStandard.stream().anyMatch(goldStandardTraceLink -> areTraceLinksMatching(goldStandardTraceLink, traceLink));
    }

    private static boolean isGoldStandardTraceLinkContainedInTraceLinks(String goldStandardTraceLink, Set<String> traceLinks) {
        return traceLinks.stream().anyMatch(traceLink -> areTraceLinksMatching(goldStandardTraceLink, traceLink));
    }

    private static ImmutableList<String> getLinesFromCsvFile(Path path) {
        List<String> lines = Lists.mutable.empty();
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        lines.remove(0); //remove header
        return Lists.immutable.fromStream(lines.stream().filter(Predicate.not(String::isBlank)));
    }


}
