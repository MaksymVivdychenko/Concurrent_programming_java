package task_1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

class DataGenerator {

    private static final Random RANDOM = new Random();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Створює тестову структуру для аналізу довжини слів.
     * @param maxDepth Глибина вкладеності (напр. 4)
     * @param dirsPerLevel Кількість підпапок у кожній папці (напр. 3)
     * @param filesPerDir Кількість файлів у кожній папці (напр. 20)
     * @return Шлях до кореневої директорії
     */
    public Path generate(int maxDepth, int dirsPerLevel, int filesPerDir) throws IOException {
        Path root = Paths.get("/home/maksym_vivdychenko/IdeaProjects/lab_4_texts");
        System.out.println("Генерація середовища: " + root.toAbsolutePath());

        createContentRecursive(root, maxDepth, dirsPerLevel, filesPerDir);

        System.out.println("Готово! Можна запускати Fork/Join тест.");
        return root;
    }

    private void createContentRecursive(Path currentDir, int depth, int dirsPerLevel, int filesPerDir) throws IOException {
        if (depth < 0) return;

        // Створюємо файли з випадковим текстом
        for (int i = 0; i < filesPerDir; i++) {
            String fileName = "data_" + UUID.randomUUID().toString().substring(0, 8) + ".txt";
            generateRandomFile(currentDir.resolve(fileName));
        }

        // Рекурсивне створення підпапок
        if (depth > 0) {
            for (int i = 0; i < dirsPerLevel; i++) {
                Path subDir = currentDir.resolve("subdir_" + depth + "_" + i);
                Files.createDirectory(subDir);
                createContentRecursive(subDir, depth - 1, dirsPerLevel, filesPerDir);
            }
        }
    }

    private void generateRandomFile(Path path) throws IOException {
        int lineCount = RANDOM.nextInt(100) + 20; // 20-120 рядків

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int i = 0; i < lineCount; i++) {
                int wordsInLine = RANDOM.nextInt(20) + 5; // 5-25 слів у рядку
                StringBuilder sb = new StringBuilder();

                for (int j = 0; j < wordsInLine; j++) {
                    // Кожне слово має випадкову довжину від 1 до 12 символів
                    int wordLength = RANDOM.nextInt(12) + 1;
                    sb.append(generateRandomString(wordLength)).append(" ");
                }
                writer.write(sb.toString().trim());
                writer.newLine();
            }
        }
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}

public class TestDataGenerator
{
    public static void main(String[] args) throws IOException {
        DataGenerator generator = new DataGenerator();
        var path = generator.generate(5, 5, 20);
        System.out.print(path.toString());
    }
}
