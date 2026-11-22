package words;

import java.util.List;

public record LevelInput(List<String> dictionary, List<String> mainWords, Config config) {

    public record Config(
            int minSubWordLength,
            int minWordsToPass,
            DifficultyThresholds difficultyThresholds) {

        public Config {
            if (minSubWordLength < 1) {
                throw new IllegalArgumentException("minSubWordLength must be positive");
            }
            if (minWordsToPass < 1) {
                throw new IllegalArgumentException("minWordsToPass must be positive");
            }
            if (difficultyThresholds == null) {
                throw new IllegalArgumentException("difficultyThresholds is required");
            }
            difficultyThresholds.validate();
        }

        public static Config defaults() {
            return new Config(3, 8, new DifficultyThresholds(7, 10, 15, 30));
        }
    }

    public record DifficultyThresholds(
            int easyMaxMainWordLength,
            int mediumMaxMainWordLength,
            int easyMaxSubWords,
            int mediumMaxSubWords) {

        void validate() {
            if (easyMaxMainWordLength < 1 || mediumMaxMainWordLength < easyMaxMainWordLength
                    || easyMaxSubWords < 1 || mediumMaxSubWords < easyMaxSubWords) {
                throw new IllegalArgumentException("Invalid difficultyThresholds");
            }
        }
    }
}
