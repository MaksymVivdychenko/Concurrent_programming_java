package task_1;

import java.util.concurrent.RecursiveTask;

class DocumentCalculationTask extends RecursiveTask<long[]> {
    private final Document document;

    DocumentCalculationTask(Document document) {
        super();
        this.document = document;
    }

    @Override
    protected long[] compute() {
        return TextAnalyzer.AnalyzeText(document);
    }
}