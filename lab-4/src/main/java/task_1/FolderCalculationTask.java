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

        // Створюємо підзавдання для підпапок
        for (Folder subFolder : folder.getSubFolders()) {
            FolderCalculationTask task = new FolderCalculationTask(subFolder);
            forks.add(task);
            task.fork(); // Запуск у фоні
        }

        // Створюємо підзавдання для документів
        for (Document document : folder.getDocuments()) {
            DocumentCalculationTask task = new DocumentCalculationTask(document);
            forks.add(task);
            task.fork(); // Запуск у фоні
        }

        // Збираємо результати
        for (RecursiveTask<long[]> task : forks) {
            var array = task.join();
            for (int i = 0; i < array.length; i++) {
                wordsAndCount[i] += array[i];
            }
        }
        return wordsAndCount;
    }
}