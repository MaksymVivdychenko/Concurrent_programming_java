import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class WordCounterApp {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    long countOccurrencesInParallel(Folder folder, String word) {
        return forkJoinPool.invoke(new FolderSearchTask(folder, word));
    }

    Long countOccurrencesOnSingleThread(Folder folder, String searchedWord) {
        long count = 0;
        for (Folder subFolder : folder.getSubFolders()) {
            count = count + countOccurrencesOnSingleThread(subFolder, searchedWord);
        }
        for (Document document : folder.getDocuments()) {
            count = count + WordCounter.occurrencesCount(document, searchedWord);
        }
        return count;
    }

    public static void main(String[] args) throws IOException {
        // Приклад ініціалізації даних
        File path = new File("шлях/до/твоїх/текстів");
        Folder root = Folder.fromDirectory(path);

        WordCounterApp app = new WordCounterApp();
        String wordToSearch = "Java";

        long result = app.countOccurrencesInParallel(root, wordToSearch);
        System.out.println("Знайдено " + result + " входжень слова '" + wordToSearch + "'");
    }
}