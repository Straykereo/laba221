package words;

import java.util.List;

public record Level(
        int id,
        String mainWord,
        Difficulty difficulty,
        int minWordsToPass,
        List<SubWord> subWords) { }
