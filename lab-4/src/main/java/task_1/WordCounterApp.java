package task_1;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class WordCounterApp {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private static final int maxWordLength = 13;

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
            var wordsAndSizeFromFolder = TextAnalyzer.AnalyzeText(document.getLines(), 0, document.getLines().size());
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

        // --- 1. Послідовне виконання ---
        long startSingle = System.nanoTime();
        long[] resultSingle = app.analyzeOnSingleThread(root);
        long endSingle = System.nanoTime();
        long timeSingleMs = (endSingle - startSingle) / 1_000_000; // конвертація наносекунд у мілісекунди

        System.out.println("Час послідовного виконання: " + timeSingleMs + " мс");

        // --- 2. Паралельне виконання ---
        long startParallel = System.nanoTime();
        long[] resultParallel = app.analyzeInParallel(root);
        long endParallel = System.nanoTime();
        long timeParallelMs = (endParallel - startParallel) / 1_000_000;
        double speedUp = (double)timeSingleMs / timeParallelMs;

        System.out.println("Час паралельного виконання: " +  timeParallelMs + " мс");

        System.out.println("\n--------------------------------");
        System.out.printf("Прискорення: %.2fх\n", speedUp);
        System.out.println("--------------------------------\n");

        // --- 3. Вивід аналітики (на основі паралельного результату) ---
        analyze(resultParallel);

        System.out.println("\nДеталізація масиву:");
        for (int i = 1; i < maxWordLength; i++) {
            if (resultParallel[i] == 0) {
                break;
            }
            System.out.print(i + ": " + resultParallel[i] + " ");
        }
        System.out.println();
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
        System.out.printf("СК відхилення (Std Dev): %.2f\n", standardDeviation);
    }
}