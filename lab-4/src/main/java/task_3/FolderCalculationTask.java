package task_3;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class FolderCalculationTask extends RecursiveTask<HashSet<String>> {
    private final Folder folder;

    FolderCalculationTask(Folder folder) {
        this.folder = folder;
    }

    @Override
    protected HashSet<String> compute() {
        List<RecursiveTask<HashSet<String>>> forks = new LinkedList<>();
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

        if(forks.isEmpty())
        {
            return new HashSet<>();
        }
        var task1 = forks.removeFirst();
        var commonWords = task1.join();
        for (RecursiveTask<HashSet<String>> task : forks) {
            var uniqueWords = task.join();
            commonWords.retainAll(uniqueWords);
        }
        return commonWords;
    }
}