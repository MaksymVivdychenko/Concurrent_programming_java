package task_1;

import java.util.LinkedList;
import java.util.concurrent.RecursiveTask;

class DocumentCalculationTask extends RecursiveTask<long[]> {
    private final int TRESHOLD = 50;
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
    protected long[] compute() {
        long[] wordsAndCount = new long[50];
        if (endIndex - startIndex < TRESHOLD) {
            return TextAnalyzer.AnalyzeText(document.getLines(), startIndex, endIndex);
        }
        int middleIndex = startIndex + (endIndex - startIndex) / 2;
        DocumentCalculationTask task1 = new DocumentCalculationTask(document, startIndex, middleIndex);
        DocumentCalculationTask task2 = new DocumentCalculationTask(document, middleIndex, endIndex);
        task1.fork();
        var task2Result = task2.compute();
        var task1Result = task1.join();
        for (int i = 0; i < task2Result.length; i++) {
            wordsAndCount[i] = task1Result[i] + task2Result[i];
        }

        return wordsAndCount;

    }
}