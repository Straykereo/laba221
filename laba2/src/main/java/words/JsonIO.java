package words;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JsonIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public LevelInput readInput(Path path) throws IOException {
        LevelInput input = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), LevelInput.class);
        if (input == null) {
            throw new IOException("Empty input file: " + path);
        }
        if (input.dictionary() == null || input.mainWords() == null) {
            throw new IOException("Input must contain dictionary and mainWords");
        }
        return new LevelInput(input.dictionary(), input.mainWords(),
                input.config() == null ? LevelInput.Config.defaults() : input.config());
    }

    public String toJson(LevelOutput output) {
        return GSON.toJson(output);
    }

    public void writeOutput(LevelOutput output, Path path) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, toJson(output), StandardCharsets.UTF_8);
    }
}
