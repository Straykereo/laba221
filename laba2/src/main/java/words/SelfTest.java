package words;

import java.util.ArrayList;
import java.util.List;

public final class SelfTest {
    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        repeatedLetters();
        letterMultiplicity();
        mainWordInDictionary();
        dictionaryDuplicates();
        shortWords();
        registerAndYo();
        noSuitableSubWords();
        exactThreshold();
        largeDictionaryPerformance();
        configIsSeparateFromOutput();

        System.out.println("Passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void repeatedLetters() {
        LevelOutput output = generate(List.of("мам", "школа"), List.of("МАМА"), 1);
        check("repeated letters: valid word included", hasSubWord(output, "МАМ"));
    }

    private static void letterMultiplicity() {
        LevelOutput output = generate(List.of("яня", "яна"), List.of("АНЯ"), 1);
        check("multiplicity: word with two Я rejected", !hasSubWord(output, "ЯНЯ")
                && hasSubWord(output, "ЯНА"));
    }

    private static void mainWordInDictionary() {
        LevelOutput output = generate(List.of("дом", "д"), List.of("ДОМ"), 1);
        check("main word itself rejected", output.levels().isEmpty()
                && !hasSubWord(output, "ДОМ"));
    }

    private static void dictionaryDuplicates() {
        LevelOutput output = generate(List.of("дом", "дом", "ДОМ", "д"), List.of("ДОМА"), 1);
        check("duplicates removed", countLevels(output) == 1
                && output.levels().get(0).subWords().stream()
                        .filter(w -> w.text().equals("ДОМ")).count() == 1);
    }

    private static void shortWords() {
        LevelOutput output = generate(List.of("да", "дом", "д", "мод"), List.of("МОД"), 1);
        check("short words filtered", hasSubWord(output, "ДОМ")
                && output.levels().get(0).subWords().stream()
                        .noneMatch(w -> w.text().length() < 3));
    }

    private static void registerAndYo() {
        LevelOutput output = generate(List.of("семь", "СЕМЬ", "сё"), List.of("СЁМЬЯ"), 1);
        check("register and Ё normalized", hasSubWord(output, "СЕМЬ")
                && output.levels().get(0).subWords().size() == 1);
    }

    private static void noSuitableSubWords() {
        LevelOutput output = generate(List.of("дом", "кот"), List.of("СЛОН"), 1);
        check("level skipped without subwords", output.levels().isEmpty());
    }

    private static void exactThreshold() {
        LevelOutput output = generate(List.of("маш", "шина", "машин", "машина"), List.of("МАШИНА"), 3);
        check("exact minWordsToPass", output.levels().size() == 1
                && output.levels().get(0).subWords().size() == 3);
    }

    private static void largeDictionaryPerformance() {
        List<String> dictionary = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            dictionary.add("слово" + i);
        }
        dictionary.add("мод");
        long started = System.nanoTime();
        LevelOutput output = generate(dictionary, List.of("ДОМ"), 1);
        long millis = (System.nanoTime() - started) / 1_000_000;
        check("large dictionary: correct result", hasSubWord(output, "МОД"));
        check("large dictionary: under 2 seconds (" + millis + " ms)", millis < 2000);
    }

    private static void configIsSeparateFromOutput() {
        String json = new JsonIO().toJson(generate(List.of("дом"), List.of("ДОМ"), 1));
        check("config is not in output JSON", !json.contains("minSubWordLength")
                && !json.contains("difficultyThresholds"));
    }

    private static LevelOutput generate(List<String> dictionary, List<String> mainWords, int minWordsToPass) {
        LevelInput.Config config = new LevelInput.Config(3, minWordsToPass,
                new LevelInput.DifficultyThresholds(7, 10, 15, 30));
        return new LevelGenerator(config).generate(new LevelInput(dictionary, mainWords, config));
    }

    private static boolean hasSubWord(LevelOutput output, String text) {
        String expected = LevelGenerator.normalize(text);
        return output.levels().stream()
                .flatMap(level -> level.subWords().stream())
                .anyMatch(word -> word.text().equals(expected));
    }

    private static int countLevels(LevelOutput output) {
        return output.levels().size();
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("OK: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }
}
