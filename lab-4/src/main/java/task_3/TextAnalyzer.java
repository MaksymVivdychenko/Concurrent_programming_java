package task_3;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TextAnalyzer {

    public static HashSet<String> AnalyzeText(List<String> lines, int start, int end) {
        HashSet<String> uniqueWords = new HashSet<>();
        for (int i = start; i < end; i++) {
            uniqueWords.addAll(Arrays.asList(getFilteredWords(lines.get(i))));
        }
        return uniqueWords;
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
        String cleanText = text.replaceAll("[^\\p{L}\\p{N}]+", " ").trim().toLowerCase();

        // 3. Розбиваємо рядок по пробілах
        return cleanText.split("\\s+");
    }
}