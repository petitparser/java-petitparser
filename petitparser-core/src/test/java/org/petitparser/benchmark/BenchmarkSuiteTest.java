package org.petitparser.benchmark;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.CharacterPredicate;
import org.petitparser.parser.primitive.LookupCharPredicate;
import org.petitparser.parser.primitive.RangesCharPredicate;
import org.petitparser.parser.primitive.StringParser;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Parallel test suite verifying performance, throughput, and zero-allocation characteristics.
 */
public class BenchmarkSuiteTest {

  @Test
  public void testRepeatingCharacterAllocationAndFunctionalParity() {
    String input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".repeat(10);
    Parser repeatingParser = CharacterParser.word().plusString();
    Parser flattenParser = CharacterParser.word().plus().flatten();

    // Functional parity verification
    Result repeatingResult = repeatingParser.parseOn(new Context(input, 0));
    Result flattenResult = flattenParser.parseOn(new Context(input, 0));

    assertTrue(repeatingResult.isSuccess());
    assertTrue(flattenResult.isSuccess());
    assertEquals(input, repeatingResult.get());
    assertEquals(input, flattenResult.get());
    assertEquals(input.length(), repeatingResult.getPosition());
    assertEquals(input.length(), flattenResult.getPosition());

    assertEquals(input.length(), repeatingParser.fastParseOn(input, 0));
    assertEquals(input.length(), flattenParser.fastParseOn(input, 0));

    // Allocation comparison
    List<BenchmarkResult> results = BenchmarkSuite.benchmarkRepeatingCharacterVsStarFlatten(2000, 5000);
    assertEquals(4, results.size());

    BenchmarkResult rRepeating = results.get(0);
    BenchmarkResult rFlatten = results.get(1);
    BenchmarkResult rFastRepeating = results.get(2);

    if (BenchmarkRunner.isThreadAllocationTrackingSupported()) {
      // RepeatingCharacterParser should allocate far fewer bytes than .star().flatten()
      assertTrue("Repeating parser (" + rRepeating.getAllocatedBytes() + "B) should allocate less than flatten ("
          + rFlatten.getAllocatedBytes() + "B)",
          rRepeating.getAllocatedBytes() < rFlatten.getAllocatedBytes());

      // fastParseOn on repeating parser should have zero heap allocations
      assertEquals("fastParseOn should allocate 0 bytes", 0L, rFastRepeating.getAllocatedBytes());
    }
  }

  @Test
  public void testLookupTableVsRangesParity() {
    boolean[] table = new boolean[256];
    for (char c = 'a'; c <= 'z'; c++) table[c] = true;
    for (char c = 'A'; c <= 'Z'; c++) table[c] = true;
    for (char c = '0'; c <= '9'; c++) table[c] = true;
    table['_'] = true;
    CharacterPredicate lookup = new LookupCharPredicate(table);

    char[] starts = new char[]{'0', 'A', '_', 'a'};
    char[] stops = new char[]{'9', 'Z', '_', 'z'};
    CharacterPredicate ranges = new RangesCharPredicate(starts, stops);

    // Verify functional parity across all 65536 char values
    for (int c = 0; c <= Character.MAX_VALUE; c++) {
      char ch = (char) c;
      assertEquals("Discrepancy at char " + c, lookup.test(ch), ranges.test(ch));
    }

    List<BenchmarkResult> results = BenchmarkSuite.benchmarkLookupTableVsRanges(500, 2000);
    assertEquals(2, results.size());
    assertTrue(results.get(0).getOperationsPerSecond() > 0);
    assertTrue(results.get(1).getOperationsPerSecond() > 0);
  }

  @Test
  public void testFastParseZeroAllocationAcrossParsers() {
    if (!BenchmarkRunner.isThreadAllocationTrackingSupported()) {
      return;
    }

    String input = "test123Identifier = 42;";

    Parser parser = CharacterParser.word().plusString()
        .seq(CharacterParser.whitespace().star())
        .seq(CharacterParser.of('='))
        .seq(CharacterParser.whitespace().star())
        .seq(CharacterParser.digit().plusString())
        .seq(CharacterParser.of(';'));

    Parser stringParser = StringParser.of("test123Identifier = 42;");

    // Warmup
    for (int i = 0; i < 2000; i++) {
      parser.fastParseOn(input, 0);
      parser.fastParseOn(input, 5); // failure path
      stringParser.fastParseOn(input, 0);
      stringParser.fastParseOn(input, 5); // failure path
    }

    // Measure success path
    long start = BenchmarkRunner.getThreadAllocatedBytes();
    for (int i = 0; i < 5000; i++) {
      int pos = parser.fastParseOn(input, 0);
      if (pos < 0) {
        throw new IllegalStateException();
      }
    }
    long allocatedSuccess = BenchmarkRunner.getThreadAllocatedBytes() - start;
    assertEquals("fastParseOn success should allocate 0 bytes", 0L, allocatedSuccess);

    // Measure failure path
    start = BenchmarkRunner.getThreadAllocatedBytes();
    for (int i = 0; i < 5000; i++) {
      int pos = parser.fastParseOn(input, 1);
      if (pos >= 0) {
        throw new IllegalStateException();
      }
    }
    long allocatedFailure = BenchmarkRunner.getThreadAllocatedBytes() - start;
    assertEquals("fastParseOn failure should allocate 0 bytes", 0L, allocatedFailure);

    // StringParser fastParseOn
    start = BenchmarkRunner.getThreadAllocatedBytes();
    for (int i = 0; i < 5000; i++) {
      stringParser.fastParseOn(input, 0);
      stringParser.fastParseOn(input, 1);
    }
    long allocatedString = BenchmarkRunner.getThreadAllocatedBytes() - start;
    assertEquals("StringParser fastParseOn should allocate 0 bytes", 0L, allocatedString);
  }

  @Test
  public void testBenchmarkResultMethods() {
    BenchmarkResult result = new BenchmarkResult("TestBenchmark", 1000, 1_000_000_000L, 5000);
    assertEquals("TestBenchmark", result.getName());
    assertEquals(1000, result.getIterations());
    assertEquals(1_000_000_000L, result.getElapsedNanos());
    assertEquals(5000L, result.getAllocatedBytes());

    assertEquals(1000.0, result.getOperationsPerSecond(), 0.001);
    assertEquals(1_000_000.0, result.getNanosPerOperation(), 0.001);
    assertEquals(5.0, result.getBytesPerOperation(), 0.001);

    String str = result.toString();
    assertTrue(str.contains("TestBenchmark"));
    assertTrue(str.contains("ops/s"));
    assertTrue(str.contains("B/op"));
  }

  @Test
  public void testBenchmarkResultZeroIterationsOrElapsed() {
    BenchmarkResult zeroElapsed = new BenchmarkResult("ZeroElapsed", 100, 0, 0);
    assertEquals(0.0, zeroElapsed.getOperationsPerSecond(), 0.001);

    BenchmarkResult zeroIters = new BenchmarkResult("ZeroIters", 0, 100, 0);
    assertEquals(0.0, zeroIters.getNanosPerOperation(), 0.001);
    assertEquals(0.0, zeroIters.getBytesPerOperation(), 0.001);
  }

  @Test
  public void testBenchmarkSuiteRunAllAndMain() {
    List<BenchmarkResult> results = BenchmarkSuite.runAll(100, 500);
    assertNotNull(results);
    assertFalse(results.isEmpty());
    for (BenchmarkResult result : results) {
      assertNotNull(result.getName());
      assertTrue(result.getOperationsPerSecond() > 0);
    }

    // Call main with dry-run
    BenchmarkSuite.main(new String[0]);
  }

  @Test(expected = NullPointerException.class)
  public void testBenchmarkResultNullName() {
    new BenchmarkResult(null, 10, 10, 10);
  }

  @Test
  public void testBenchmarkResultNegativeAllocatedBytes() {
    BenchmarkResult result = new BenchmarkResult("NegativeAlloc", 10, 100, -50);
    assertEquals(0L, result.getAllocatedBytes());
  }

  @Test(expected = NullPointerException.class)
  public void testBenchmarkRunnerNullName() {
    BenchmarkRunner.run(null, 1, 1, () -> {});
  }

  @Test(expected = NullPointerException.class)
  public void testBenchmarkRunnerNullTask() {
    BenchmarkRunner.run("Test", 1, 1, null);
  }
}
