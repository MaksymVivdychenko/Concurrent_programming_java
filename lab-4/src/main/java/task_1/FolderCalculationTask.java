package task_1;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class FolderCalculationTask extends RecursiveTask<long[]> {
    private final Folder folder;

    FolderCalculationTask(Folder folder) {
        this.folder = folder;
    }

    @Override
    protected long[] compute() {
        long[] wordsAndCount =  new long[50];
        List<RecursiveTask<long[]>> forks = new LinkedList<>();

        for (Folder subFolder : folder.getSubFolders()) {
            FolderCalculationTask task = new FolderCalculationTask(subFolder);
            forks.add(task);
            task.fork();
        }

        for (Document document : folder.getDocuments()) {
            DocumentCalculationTask task = new DocumentCalculationTask(document, 0, document.getLines().size());
            forks.add(task);
            task.fork();
        }

        for (RecursiveTask<long[]> task : forks) {
            var array = task.join();
            for (int i = 0; i < array.length; i++) {
                wordsAndCount[i] += array[i];
            }
        }
        return wordsAndCount;
    }
}