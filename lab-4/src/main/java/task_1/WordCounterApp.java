package task_1;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class WordCounterApp {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    long[] analyzeInParallel(Folder folder) {
        return forkJoinPool.invoke(new FolderCalculationTask(folder));
    }

    long[] analyzeOnSingleThread(Folder folder) {
        long[] wordsAndSize = new long[50];
        for (Folder subFolder : folder.getSubFolders()) {
            var wordsAndSizeFromFolder = analyzeOnSingleThread(subFolder);
            for (int i = 0; i < wordsAndSizeFromFolder.length; i++) {
                wordsAndSize[i] += wordsAndSizeFromFolder[i];
            }
        }
        for (Document document : folder.getDocuments()) {
            var wordsAndSizeFromFolder = TextAnalyzer.AnalyzeText(document);
            for (int i = 0; i < wordsAndSizeFromFolder.length; i++) {
                wordsAndSize[i] += wordsAndSizeFromFolder[i];
            }
        }
        return wordsAndSize;
    }

    public static void main(String[] args) throws IOException {
        File path = new File("/home/maksym_vivdychenko/IdeaProjects/lab_4_texts");
        Folder root = Folder.fromDirectory(path);

        WordCounterApp app = new WordCounterApp();

        long[] result = app.analyzeInParallel(root);
        analyze(result);
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 0) {
                break;
            }
            System.out.print(i + ": " + result[i] + " ");
        }
    }

    public static void analyze(long[] counts) {
        long totalWords = 0;
        long sumLengths = 0;

        for (int i = 0; i < counts.length; i++) {
            totalWords += counts[i];
            sumLengths += (long) i * counts[i];
        }

        if (totalWords == 0) {
            System.out.println("Масив порожній");
            return;
        }

        double mean = (double) sumLengths / totalWords;

        double varianceSum = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                varianceSum += counts[i] * Math.pow(i - mean, 2);
            }
        }
        double standardDeviation = Math.sqrt(varianceSum / totalWords);

        System.out.printf("Середнє значення (Mean): %.2f\n", mean);
        System.out.printf("СД відхилення (Std Dev): %.2f\n", standardDeviation);
    }
}