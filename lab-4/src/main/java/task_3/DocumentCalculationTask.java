package task_3;

import java.util.HashSet;
import java.util.concurrent.RecursiveTask;

class DocumentCalculationTask extends RecursiveTask<HashSet<String>> {
    private final Document document;
    private final int startIndex;
    private final int endIndex;

    DocumentCalculationTask(Document document, int startIndex, int endIndex) {
        super();
        this.document = document;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    protected HashSet<String> compute() {
        if (endIndex - startIndex < 50) {
            return TextAnalyzer.AnalyzeText(document.getLines(), startIndex, endIndex);
        }
        int middleIndex = startIndex + (endIndex - startIndex) / 2;
        DocumentCalculationTask task1 = new DocumentCalculationTask(document, startIndex, middleIndex);
        DocumentCalculationTask task2 = new DocumentCalculationTask(document, middleIndex, endIndex);
        task1.fork();
        var task2Result = task2.compute();
        var task1Result = task1.join();
        task1Result.addAll(task2Result);

        return task1Result;

    }
}