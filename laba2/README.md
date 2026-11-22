# Words from word

Java module that builds game levels from a dictionary and a list of main words. For every main word it finds dictionary words that can be built from the letters of the main word, applies filters from `config`, calculates difficulty and writes the result as JSON.

## Technologies

- Java 21
- Gson 2.11.0
- Java Collections Framework
- Stream API

## Input

`examples/input.json`:

```json
{
  "dictionary": ["десант", "адрес", "армия", "место", "метро", "стол", "соль", "дом", "рация", "тора", "магия", "гора"],
  "mainWords": ["ДЕМОНСТРАЦИЯ", "ВЗАИМНОСТЬ"],
  "config": {
    "minSubWordLength": 3,
    "minWordsToPass": 2,
    "difficultyThresholds": {
      "easyMaxMainWordLength": 7,
      "mediumMaxMainWordLength": 10,
      "easyMaxSubWords": 15,
      "mediumMaxSubWords": 30
    }
  }
}
```

`config` affects generation only and is not included into the output JSON.

## Output

Example `output.json`:

```json
{
  "levels": [
    {
      "id": 1,
      "mainWord": "ДЕМОНСТРАЦИЯ",
      "difficulty": "HARD",
      "minWordsToPass": 2,
      "subWords": [
        { "text": "ДЕСАНТ", "hint": "" },
        { "text": "АДРЕС", "hint": "" },
        { "text": "АРМИЯ", "hint": "" },
        { "text": "МЕСТО", "hint": "" },
        { "text": "МЕТРО", "hint": "" },
        { "text": "РАЦИЯ", "hint": "" },
        { "text": "ТОРА", "hint": "" },
        { "text": "ДОМ", "hint": "" }
      ]
    }
  ]
}
```

A level is created only if the number of valid subwords is at least `minWordsToPass`.

## Rules

A dictionary word becomes a subword when:

1. it is present in the dictionary;
2. its length is not less than `minSubWordLength`;
3. it is not equal to the main word;
4. it can be built from the letters of the main word with correct multiplicity;
5. it does not repeat inside the same level.

Registration and the letter `Ё` are normalized: comparison is performed in upper case and `Ё` is treated as `Е`.

## How to run

Build:

```
build.bat
```

Run with default paths:

```
run.bat
```

This reads `examples/input.json` and writes `output.json`.

Run with custom files:

```
run.bat input.json output.json
```

Run self-checks:

```
java -cp out;lib/gson-2.11.0.jar words.SelfTest
```

## Self-checks

`SelfTest` covers the required cases: repeated letters, letter multiplicity, the main word in the dictionary, duplicates, short words, different register and `Ё`, no suitable subwords, exact `minWordsToPass`, a large dictionary and absence of `config` in the output.
