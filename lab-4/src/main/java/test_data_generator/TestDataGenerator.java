package test_data_generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class DataGenerator {

    private static final Random RANDOM = new Random();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    // 1. Слова, які будуть у КОЖНОМУ файлі (для тестування перетину множин)
    private static final List<String> GUARANTEED_COMMON_WORDS = Arrays.asList("system", "data");

    // 2. Додаткові IT-слова для тестування пошуку за категорією
    private static final List<String> ADDITIONAL_IT_WORDS = Arrays.asList(
            "java", "server", "algorithm", "database", "network",
            "api", "framework", "compiler", "cloud", "security"
    );

    public Path generate(int maxDepth, int dirsPerLevel, int filesPerDir) throws IOException {
        Path root = Paths.get("/home/maksym_vivdychenko/IdeaProjects/lab_4_texts");

        // Створюємо кореневу директорію, якщо її немає
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        System.out.println("Генерація середовища: " + root.toAbsolutePath());
        createContentRecursive(root, maxDepth, dirsPerLevel, filesPerDir);
        System.out.println("Готово! Можна запускати Fork/Join тест.");
        return root;
    }

    private void createContentRecursive(Path currentDir, int depth, int dirsPerLevel, int filesPerDir) throws IOException {
        if (depth < 0) return;

        // Створюємо файли
        for (int i = 0; i < filesPerDir; i++) {
            String fileName = "data_" + UUID.randomUUID().toString().substring(0, 8) + ".txt";
            generateRandomFile(currentDir.resolve(fileName));
        }

        // Рекурсивне створення підпапок
        if (depth > 0) {
            for (int i = 0; i < dirsPerLevel; i++) {
                Path subDir = currentDir.resolve("subdir_" + depth + "_" + i);
                if (!Files.exists(subDir)) {
                    Files.createDirectory(subDir);
                }
                createContentRecursive(subDir, depth - 1, dirsPerLevel, filesPerDir);
            }
        }
    }

    private void generateRandomFile(Path path) throws IOException {
        int lineCount = RANDOM.nextInt(100) + 20; // 20-120 рядків

        // З імовірністю 5% файл буде належати до категорії "ІТ"
        boolean isItFile = RANDOM.nextDouble() < 0.01;

        // Формуємо пул слів для ін'єкції в цей конкретний файл
        List<String> wordsToInject = new ArrayList<>(GUARANTEED_COMMON_WORDS);

        // Визначаємо, скільки додаткових IT-слів додати
        int additionalWordsCount = isItFile ? (RANDOM.nextInt(4) + 5) : (RANDOM.nextInt(2) + 1); // 5-8 для ІТ, 1-2 для звичайних

        // Вибираємо випадкові слова з додаткового списку
        List<String> shuffledItWords = new ArrayList<>(ADDITIONAL_IT_WORDS);
        Collections.shuffle(shuffledItWords);
        wordsToInject.addAll(shuffledItWords.subList(0, additionalWordsCount));

        // Перемішуємо пул, щоб слова розкидалися по тексту випадковим чином
        Collections.shuffle(wordsToInject);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int i = 0; i < lineCount; i++) {
                int wordsInLine = RANDOM.nextInt(20) + 5; // 5-25 слів у рядку
                StringBuilder sb = new StringBuilder();

                for (int j = 0; j < wordsInLine; j++) {
                    // З невеликою ймовірністю (або якщо це кінець рядка і залишились слова) вставляємо наше слово
                    if (!wordsToInject.isEmpty() && RANDOM.nextDouble() < 0.05) {
                        sb.append(wordsToInject.removeLast()).append(" ");
                    } else {
                        // Інакше генеруємо випадкову абракадабру
                        int wordLength = RANDOM.nextInt(12) + 1;
                        sb.append(generateRandomString(wordLength)).append(" ");
                    }
                }
                writer.write(sb.toString().trim());
                writer.newLine();
            }

            // Захисний механізм: якщо після генерації всіх рядків якісь слова для ін'єкції залишилися
            // (наприклад, через низьку ймовірність 0.05), просто дописуємо їх у кінець файлу.
            if (!wordsToInject.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String w : wordsToInject) {
                    sb.append(w).append(" ");
                }
                writer.newLine();
                writer.write(sb.toString().trim());
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

public class TestDataGenerator {
    public static void main(String[] args) throws IOException {
        DataGenerator generator = new DataGenerator();
        var path = generator.generate(5, 4, 20);
        System.out.println("Шлях: " + path.toString());
    }
}