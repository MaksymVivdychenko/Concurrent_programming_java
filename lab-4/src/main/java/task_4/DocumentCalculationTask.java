package task_4;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class DocumentCalculationTask extends RecursiveTask<Integer> {
    private final Document document;
    private final int startIndex;
    private final int endIndex;
    private final HashSet<String> keywords;

    DocumentCalculationTask(Document document, int startIndex, int endIndex, HashSet<String> keywords) {
        super();
        this.document = document;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.keywords = keywords;
    }

    @Override
    protected Integer compute() {
        if (endIndex - startIndex < 50) {
            return TextAnalyzer.AnalyzeText(document.getLines(), startIndex, endIndex, keywords);
        }
        int middleIndex = startIndex + (endIndex - startIndex) / 2;
        DocumentCalculationTask task1 = new DocumentCalculationTask(document, startIndex, middleIndex, keywords);
        DocumentCalculationTask task2 = new DocumentCalculationTask(document, middleIndex, endIndex, keywords);
        task1.fork();
        var task2Result = task2.compute();
        var task1Result = task1.join();
        return task1Result + task2Result;
    }
}