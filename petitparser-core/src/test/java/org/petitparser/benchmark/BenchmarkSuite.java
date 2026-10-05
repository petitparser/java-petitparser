package org.petitparser.benchmark;

import org.petitparser.context.Context;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.CharacterPredicate;
import org.petitparser.parser.primitive.LookupCharPredicate;
import org.petitparser.parser.primitive.RangesCharPredicate;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.parser.repeating.RepeatingCharacterParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Performance and allocation benchmark suite verifying PetitParser Java optimizations.
 */
public class BenchmarkSuite {

  private BenchmarkSuite() {}

  private static final String REPEATING_INPUT =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".repeat(20);

  private static final String PREDICATE_INPUT =
      "aB3_!@#xY9$%-+zK7*&^~12345abcdefghijKLMNOPQRST".repeat(30);

  private static final String GRAMMAR_INPUT =
      "identifier123 = otherIdentifier + 42 * 99;".repeat(20);

  public static List<BenchmarkResult> runAll(int warmup, int iterations) {
    List<BenchmarkResult> results = new ArrayList<>();
    results.addAll(benchmarkRepeatingCharacterVsStarFlatten(warmup, iterations));
    results.addAll(benchmarkLookupTableVsRanges(warmup, iterations));
    results.addAll(benchmarkFastParseZeroAllocation(warmup, iterations));
    return results;
  }

  public static List<BenchmarkResult> benchmarkRepeatingCharacterVsStarFlatten(int warmup, int iterations) {
    List<BenchmarkResult> results = new ArrayList<>();

    // 1. RepeatingCharacterParser (Zero-List Lexing)
    Parser repeatingParser = CharacterParser.word().plusString();
    BenchmarkResult r1 = BenchmarkRunner.run("RepeatingCharacterParser.parseOn", warmup, iterations, () -> {
      repeatingParser.parseOn(new Context(REPEATING_INPUT, 0));
    });
    results.add(r1);

    // 2. PossessiveRepeatingParser + FlattenParser (Legacy List Allocation)
    Parser flattenParser = CharacterParser.word().plus().flatten();
    BenchmarkResult r2 = BenchmarkRunner.run("Word.plus().flatten().parseOn", warmup, iterations, () -> {
      flattenParser.parseOn(new Context(REPEATING_INPUT, 0));
    });
    results.add(r2);

    // 3. RepeatingCharacterParser fastParseOn
    BenchmarkResult r3 = BenchmarkRunner.run("RepeatingCharacterParser.fastParseOn", warmup, iterations, () -> {
      repeatingParser.fastParseOn(REPEATING_INPUT, 0);
    });
    results.add(r3);

    // 4. FlattenParser fastParseOn
    BenchmarkResult r4 = BenchmarkRunner.run("Word.plus().flatten().fastParseOn", warmup, iterations, () -> {
      flattenParser.fastParseOn(REPEATING_INPUT, 0);
    });
    results.add(r4);

    return results;
  }

  public static List<BenchmarkResult> benchmarkLookupTableVsRanges(int warmup, int iterations) {
    List<BenchmarkResult> results = new ArrayList<>();

    // Word predicate via O(1) Lookup table
    boolean[] table = new boolean[256];
    for (char c = 'a'; c <= 'z'; c++) table[c] = true;
    for (char c = 'A'; c <= 'Z'; c++) table[c] = true;
    for (char c = '0'; c <= '9'; c++) table[c] = true;
    table['_'] = true;
    CharacterPredicate lookupPredicate = new LookupCharPredicate(table);

    // Word predicate via binary search ranges
    char[] starts = new char[]{'0', 'A', '_', 'a'};
    char[] stops = new char[]{'9', 'Z', '_', 'z'};
    CharacterPredicate rangesPredicate = new RangesCharPredicate(starts, stops);

    BenchmarkResult r1 = BenchmarkRunner.run("LookupCharPredicate (O(1) table)", warmup, iterations, () -> {
      for (int i = 0; i < PREDICATE_INPUT.length(); i++) {
        lookupPredicate.test(PREDICATE_INPUT.charAt(i));
      }
    });
    results.add(r1);

    BenchmarkResult r2 = BenchmarkRunner.run("RangesCharPredicate (binary search)", warmup, iterations, () -> {
      for (int i = 0; i < PREDICATE_INPUT.length(); i++) {
        rangesPredicate.test(PREDICATE_INPUT.charAt(i));
      }
    });
    results.add(r2);

    return results;
  }

  public static List<BenchmarkResult> benchmarkFastParseZeroAllocation(int warmup, int iterations) {
    List<BenchmarkResult> results = new ArrayList<>();

    // Grammar: word + spaces + '=' + spaces + word
    Parser assignment = CharacterParser.word().plusString()
        .seq(CharacterParser.whitespace().star())
        .seq(CharacterParser.of('='))
        .seq(CharacterParser.whitespace().star())
        .seq(CharacterParser.word().plusString());

    String input = "myVariable = value123";

    BenchmarkResult rFast = BenchmarkRunner.run("fastParseOn (Zero-Allocation)", warmup, iterations, () -> {
      assignment.fastParseOn(input, 0);
    });
    results.add(rFast);

    BenchmarkResult rFull = BenchmarkRunner.run("parseOn (Context/Result Allocation)", warmup, iterations, () -> {
      assignment.parseOn(new Context(input, 0));
    });
    results.add(rFull);

    return results;
  }

  public static void main(String[] args) {
    System.out.println("================================================================================");
    System.out.println("PetitParser Java Benchmark Suite");
    System.out.println("================================================================================");
    int warmup = 15000;
    int iterations = 25000;

    List<BenchmarkResult> results = runAll(warmup, iterations);
    for (BenchmarkResult result : results) {
      System.out.println(result);
    }
    System.out.println("================================================================================");
  }
}
