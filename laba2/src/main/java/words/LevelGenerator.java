package words;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import words.LevelInput.Config;
import words.LevelInput.DifficultyThresholds;
import java.util.Map;


public final class LevelGenerator {

    private final Config config;

    public LevelGenerator(Config config) {
        this.config = config;
    }

    public LevelOutput generate(LevelInput input) {
        List<String> dictionary = normalizeDictionary(input.dictionary());
        Map<String, Integer> mainIndex = new LinkedHashMap<>();
        for (int i = 0; i < input.mainWords().size(); i++) {
            String word = normalize(input.mainWords().get(i));
            if (!word.isEmpty()) {
                mainIndex.putIfAbsent(word, i);
            }
        }

        List<String> orderedMainWords = mainIndex.keySet().stream().toList();
        List<Level> levels = new ArrayList<>();
        for (String mainWord : orderedMainWords) {
            List<SubWord> subWords = findSubWords(mainWord, dictionary);
            if (subWords.size() < config.minWordsToPass()) {
                continue;
            }
            subWords.sort(Comparator.comparingInt((SubWord sw) -> sw.text().length()).reversed()
                    .thenComparing(SubWord::text));
            int id = mainIndex.get(mainWord) + 1;
            levels.add(new Level(
                    id,
                    mainWord,
                    calculateDifficulty(mainWord, subWords.size()),
                    config.minWordsToPass(),
                    subWords));
        }
        return new LevelOutput(levels);
    }

    private List<SubWord> findSubWords(String mainWord, List<String> dictionary) {
        Map<Integer, Integer> availableLetters = letterCounts(mainWord);
        List<String> result = new ArrayList<>();
        for (String candidate : dictionary) {
            if (candidate.length() < config.minSubWordLength()
                    || candidate.equals(mainWord)
                    || !canBuild(candidate, availableLetters)) {
                continue;
            }
            if (!result.contains(candidate)) {
                result.add(candidate);
            }
        }
        return result.stream().map(word -> new SubWord(word, "")).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }

    private Difficulty calculateDifficulty(String mainWord, int subWordCount) {
        DifficultyThresholds t = config.difficultyThresholds();
        if (mainWord.length() <= t.easyMaxMainWordLength()
                && subWordCount <= t.easyMaxSubWords()) {
            return Difficulty.EASY;
        }
        if (mainWord.length() <= t.mediumMaxMainWordLength()
                && subWordCount <= t.mediumMaxSubWords()) {
            return Difficulty.MEDIUM;
        }
        return Difficulty.HARD;
    }

    static boolean canBuild(String candidate, Map<Integer, Integer> availableLetters) {
        Map<Integer, Integer> copy = new java.util.HashMap<>(availableLetters);
        for (int i = 0; i < candidate.length(); i++) {
            int codePoint = candidate.codePointAt(i);
            Integer left = copy.get(codePoint);
            if (left == null || left <= 0) {
                return false;
            }
            copy.put(codePoint, left - 1);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                i++;
            }
        }
        return true;
    }

    static Map<Integer, Integer> letterCounts(String word) {
        Map<Integer, Integer> counts = new java.util.HashMap<>();
        for (int i = 0; i < word.length(); i++) {
            int codePoint = word.codePointAt(i);
            counts.merge(codePoint, 1, Integer::sum);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                i++;
            }
        }
        return counts;
    }

    private static List<String> normalizeDictionary(List<String> dictionary) {
        List<String> result = new ArrayList<>();
        for (String word : dictionary) {
            String value = normalize(word);
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result;
    }

    static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String upper = value.toUpperCase(Locale.ROOT);
        return upper.replace('Ё', 'Е');
    }
}
