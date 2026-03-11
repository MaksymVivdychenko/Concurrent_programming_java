import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class FolderSearchTask extends RecursiveTask<Long> {
    private final Folder folder;
    private final String searchedWord;

    FolderSearchTask(Folder folder, String searchedWord) {
        this.folder = folder;
        this.searchedWord = searchedWord;
    }

    @Override
    protected Long compute() {
        long count = 0;
        List<RecursiveTask<Long>> forks = new LinkedList<>();

        // Створюємо підзавдання для підпапок
        for (Folder subFolder : folder.getSubFolders()) {
            FolderSearchTask task = new FolderSearchTask(subFolder, searchedWord);
            forks.add(task);
            task.fork(); // Запуск у фоні
        }

        // Створюємо підзавдання для документів
        for (Document document : folder.getDocuments()) {
            DocumentSearchTask task = new DocumentSearchTask(document, searchedWord);
            forks.add(task);
            task.fork(); // Запуск у фоні
        }

        // Збираємо результати
        for (RecursiveTask<Long> task : forks) {
            count += task.join(); // Очікування та отримання результату
        }
        return count;
    }
}