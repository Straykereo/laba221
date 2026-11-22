package words;

import java.io.IOException;
import java.nio.file.Path;

public final class Main {
    public static void main(String[] args) {
        Path inputPath = args.length > 0 ? Path.of(args[0]) : Path.of("examples", "input.json");
        Path outputPath = args.length > 1 ? Path.of(args[1]) : Path.of("output.json");

        try {
            JsonIO io = new JsonIO();
            LevelInput input = io.readInput(inputPath);
            LevelOutput output = new LevelGenerator(input.config()).generate(input);
            io.writeOutput(output, outputPath);
            System.out.println("Generated " + output.levels().size() + " level(s): " + outputPath.toAbsolutePath());
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
