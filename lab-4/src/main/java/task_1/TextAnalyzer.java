package task_1;

import java.util.List;

public class TextAnalyzer {
    private static final int wordMaxSize = 50;

    public static long[] AnalyzeText(List<String> lines, int start, int end) {
        long[] wordAndSize = new long[wordMaxSize];
        for (int i = start; i < end; i++) {
            for (String word : getFilteredWords(lines.get(i))) {

                wordAndSize[word.length()]++;
            }
        }
        return wordAndSize;
    }

    public static String[] getFilteredWords(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }

        // 1. Видаляємо всі символи, що не є літерами або цифрами (замінюємо на пробіл)
        // 2. Використовуємо регулярний вираз [^\\p{L}\\p{N}]+
        // \p{L} - будь-яка літера (будь-якою мовою)
        // \p{N} - будь-яка цифра
        // ^ - заперечення (все, крім літер та цифр)
        String cleanText = text.replaceAll("[^\\p{L}\\p{N}]+", " ").trim();

        // 3. Розбиваємо рядок по пробілах
        return cleanText.split("\\s+");
    }
}