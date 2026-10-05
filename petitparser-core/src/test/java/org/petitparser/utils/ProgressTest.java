package org.petitparser.utils;

import org.junit.Test;
import org.petitparser.ExamplesTest;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;

/**
 * Tests {@link Progress}.
 */
public class ProgressTest {

  @Test
  public void testSuccessfulProgress() {
    List<Progress.ProgressFrame> frames = new ArrayList<>();
    Result result = Progress.progress(ExamplesTest.IDENTIFIER, frames::add).parse("ab123");
    assertTrue(result.isSuccess());
    assertEquals("ab123", result.get());
    assertEquals(9, frames.size());

    // Frame 0: FlattenParser at position 0
    assertEquals(0, frames.get(0).getPosition());
    assertEquals(-1, frames.get(0).getPreviousPosition());
    assertEquals(0, frames.get(0).getMaxPosition());
    assertFalse(frames.get(0).isBacktracking());
    assertFalse(frames.get(0).isBacktracked());
    assertTrue(frames.get(0).toString().startsWith("* FlattenParser"));

    // Frame 1: SequenceParser at position 0
    assertEquals(0, frames.get(1).getPosition());
    assertEquals(0, frames.get(1).getPreviousPosition());
    assertEquals(0, frames.get(1).getMaxPosition());
    assertFalse(frames.get(1).isBacktracking());
    assertTrue(frames.get(1).toString().startsWith("* SequenceParser"));

    // Frame 2: Sequence child 0 (letter) at position 0
    assertEquals(0, frames.get(2).getPosition());
    assertEquals(0, frames.get(2).getPreviousPosition());
    assertEquals(0, frames.get(2).getMaxPosition());
    assertFalse(frames.get(2).isBacktracking());
    assertTrue(frames.get(2).toString().startsWith("* CharacterParser"));

    // Frame 3: Sequence child 1 (repeating) at position 1
    assertEquals(1, frames.get(3).getPosition());
    assertEquals(0, frames.get(3).getPreviousPosition());
    assertEquals(1, frames.get(3).getMaxPosition());
    assertFalse(frames.get(3).isBacktracking());
    assertTrue(frames.get(3).toString().startsWith("** PossessiveRepeatingParser"));

    // Frame 4: repeating child at position 1 ('b')
    assertEquals(1, frames.get(4).getPosition());
    assertEquals(1, frames.get(4).getPreviousPosition());
    assertEquals(1, frames.get(4).getMaxPosition());
    assertFalse(frames.get(4).isBacktracking());
    assertTrue(frames.get(4).toString().startsWith("** CharacterParser"));

    // Frame 5: repeating child at position 2 ('1')
    assertEquals(2, frames.get(5).getPosition());
    assertEquals(1, frames.get(5).getPreviousPosition());
    assertEquals(2, frames.get(5).getMaxPosition());
    assertFalse(frames.get(5).isBacktracking());
    assertTrue(frames.get(5).toString().startsWith("*** CharacterParser"));

    // Frame 6: repeating child at position 3 ('2')
    assertEquals(3, frames.get(6).getPosition());
    assertEquals(3, frames.get(6).getMaxPosition());
    assertFalse(frames.get(6).isBacktracking());
    assertTrue(frames.get(6).toString().startsWith("**** CharacterParser"));

    // Frame 7: repeating child at position 4 ('3')
    assertEquals(4, frames.get(7).getPosition());
    assertEquals(4, frames.get(7).getMaxPosition());
    assertFalse(frames.get(7).isBacktracking());
    assertTrue(frames.get(7).toString().startsWith("***** CharacterParser"));

    // Frame 8: repeating child at position 5 (end of input)
    assertEquals(5, frames.get(8).getPosition());
    assertEquals(5, frames.get(8).getMaxPosition());
    assertFalse(frames.get(8).isBacktracking());
    assertTrue(frames.get(8).toString().startsWith("****** CharacterParser"));
  }

  @Test
  public void testFailingProgress() {
    List<Progress.ProgressFrame> frames = new ArrayList<>();
    Result result = Progress.progress(ExamplesTest.IDENTIFIER, frames::add).parse("1");
    assertFalse(result.isSuccess());
    assertEquals(3, frames.size());

    assertEquals(0, frames.get(0).getPosition());
    assertTrue(frames.get(0).toString().startsWith("* FlattenParser"));

    assertEquals(0, frames.get(1).getPosition());
    assertTrue(frames.get(1).toString().startsWith("* SequenceParser"));

    assertEquals(0, frames.get(2).getPosition());
    assertTrue(frames.get(2).toString().startsWith("* CharacterParser"));
  }

  @Test
  public void testProgressWithPredicate() {
    List<Progress.ProgressFrame> frames = new ArrayList<>();
    Parser parser = Progress.on(
        ExamplesTest.IDENTIFIER,
        p -> p instanceof CharacterParser,
        frames::add
    );
    Result result = parser.parse("ab123");
    assertTrue(result.isSuccess());
    // Only character parsers should be observed: 1 for letter + 5 for repeat child = 6
    assertEquals(6, frames.size());
    for (Progress.ProgressFrame frame : frames) {
      assertTrue(frame.getParser() instanceof CharacterParser);
    }
  }

  @Test
  public void testBacktrackingDetection() {
    // Parser tries letter & digit, fails, backtracks to position 0, tries letter & letter
    Parser branch1 = letter().seq(digit());
    Parser branch2 = letter().seq(letter());
    Parser choice = branch1.or(branch2);

    List<Progress.ProgressFrame> frames = new ArrayList<>();
    Parser instrumented = Progress.progress(choice, frames::add);
    Result result = instrumented.parse("ab");
    assertTrue(result.isSuccess());

    List<Progress.ProgressFrame> backtrackingFrames = frames.stream()
        .filter(Progress.ProgressFrame::isBacktracking)
        .collect(Collectors.toList());

    assertEquals(1, backtrackingFrames.size());
    Progress.ProgressFrame btFrame = backtrackingFrames.get(0);
    assertEquals(0, btFrame.getPosition());
    assertEquals(1, btFrame.getPreviousPosition());
    assertEquals(1, btFrame.getMaxPosition());
    assertTrue(btFrame.isBacktracking());
    assertTrue(btFrame.isBacktracked());
    assertTrue(btFrame.isBelowMaxPosition());
  }

  @Test
  public void testMultipleRunsResetState() {
    Parser branch1 = letter().seq(digit());
    Parser branch2 = letter().seq(letter());
    Parser choice = branch1.or(branch2);

    List<Progress.ProgressFrame> firstRun = new ArrayList<>();
    Parser instrumented = Progress.progress(choice, firstRun::add);
    instrumented.parse("ab");
    assertTrue(firstRun.stream().anyMatch(Progress.ProgressFrame::isBacktracking));

    List<Progress.ProgressFrame> secondRun = new ArrayList<>();
    Parser instrumented2 = Progress.progress(choice, secondRun::add);
    instrumented2.parse("a1");
    // "a1" matches first branch, so it never backtracks
    assertFalse(secondRun.stream().anyMatch(Progress.ProgressFrame::isBacktracking));
  }

  @Test
  public void testDefaultAndFactoryOverloads() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(buffer));
      Parser parser = Progress.progress(letter());
      Result result = parser.parse("a");
      assertTrue(result.isSuccess());
      String output = buffer.toString();
      assertTrue(output.contains("* CharacterParser"));
    } finally {
      System.setOut(originalOut);
    }

    // Test of() overloads
    List<Progress.ProgressFrame> framesOf = new ArrayList<>();
    Parser pOf = Progress.of(letter(), framesOf::add);
    assertTrue(pOf.parse("a").isSuccess());
    assertEquals(1, framesOf.size());

    List<Progress.ProgressFrame> framesOfPred = new ArrayList<>();
    Parser pOfPred = Progress.of(letter(), p -> true, framesOfPred::add);
    assertTrue(pOfPred.parse("a").isSuccess());
    assertEquals(1, framesOfPred.size());

    // Test progress() without observer defaults to stdout
    Parser pProgDefault = Progress.of(letter());
    assertNotNull(pProgDefault);
  }

  @Test
  public void testProgressFramePropertiesAndEquality() {
    Context c0 = new Context("hello", 0);
    Context c2 = new Context("hello", 2);
    Parser p1 = letter();
    Parser p2 = digit();

    Progress.ProgressFrame frame1 = new Progress.ProgressFrame(p1, c0);
    Progress.ProgressFrame frame2 = new Progress.ProgressFrame(p1, c0);
    Progress.ProgressFrame frame3 = new Progress.ProgressFrame(p1, c2, true);
    Progress.ProgressFrame frame4 = new Progress.ProgressFrame(p2, c0);

    assertEquals(frame1, frame2);
    assertEquals(frame1.hashCode(), frame2.hashCode());
    assertNotEquals(frame1, frame3);
    assertNotEquals(frame1, frame4);
    assertNotEquals(frame1, null);
    assertNotEquals(frame1, "string");

    assertEquals(p1, frame1.getParser());
    assertEquals(c0, frame1.getContext());
    assertEquals(0, frame1.getPosition());
    assertEquals(-1, frame1.getPreviousPosition());
    assertEquals(0, frame1.getMaxPosition());
    assertFalse(frame1.isBacktracking());
    assertFalse(frame1.isBacktracked());

    assertEquals(2, frame3.getPosition());
    assertTrue(frame3.isBacktracking());

    // Test negative position clamp in toString
    Context negContext = new Context("x", -1);
    Progress.ProgressFrame negFrame = new Progress.ProgressFrame(p1, negContext, -1, -1, -1, false);
    assertEquals("* " + p1, negFrame.toString());
  }

  @Test(expected = NullPointerException.class)
  public void testNullRootThrows() {
    Progress.on(null, frame -> {});
  }

  @Test(expected = NullPointerException.class)
  public void testNullPredicateThrows() {
    Progress.on(letter(), null, frame -> {});
  }

  @Test(expected = NullPointerException.class)
  public void testNullObserverThrows() {
    Progress.on(letter(), (Consumer<Progress.ProgressFrame>) null);
  }

  @Test
  public void testConcurrentProgressParsing() throws InterruptedException {
    int threadCount = 8;
    int iterationsPerThread = 50;
    Parser choice = letter().seq(digit()).or(letter().seq(letter()));
    // Shared progress parser across threads
    ThreadLocal<List<Progress.ProgressFrame>> threadFrames = ThreadLocal.withInitial(ArrayList::new);
    Parser instrumented = Progress.progress(choice, frame -> threadFrames.get().add(frame));

    Thread[] threads = new Thread[threadCount];
    boolean[] failures = new boolean[threadCount];
    for (int i = 0; i < threadCount; i++) {
      final int threadIndex = i;
      threads[i] = new Thread(() -> {
        try {
          for (int iter = 0; iter < iterationsPerThread; iter++) {
            threadFrames.get().clear();
            Result res = instrumented.parse("ab");
            if (!res.isSuccess()) {
              failures[threadIndex] = true;
              return;
            }
            List<Progress.ProgressFrame> frames = threadFrames.get();
            // Verify backtracking detection is accurate and not clobbered
            long btCount = frames.stream().filter(Progress.ProgressFrame::isBacktracking).count();
            if (btCount != 1) {
              failures[threadIndex] = true;
              return;
            }
          }
        } catch (Throwable t) {
          failures[threadIndex] = true;
        }
      });
    }

    for (Thread t : threads) {
      t.start();
    }
    for (Thread t : threads) {
      t.join();
    }

    for (int i = 0; i < threadCount; i++) {
      assertFalse("Thread " + i + " experienced concurrency failure", failures[i]);
    }
  }

  @Test
  public void testReentrantProgressParsing() {
    List<Progress.ProgressFrame> outerFrames = new ArrayList<>();
    List<Progress.ProgressFrame> innerFrames = new ArrayList<>();

    Parser innerParser = Progress.progress(digit().plus(), innerFrames::add);
    // Outer parser maps a letter and executes the inner parser within an action
    Parser outerParser = letter().map(ch -> innerParser.parse("123").get());
    Parser instrumentedOuter = Progress.progress(outerParser, outerFrames::add);

    Result result = instrumentedOuter.parse("a");
    assertTrue(result.isSuccess());
    assertFalse(outerFrames.isEmpty());
    assertFalse(innerFrames.isEmpty());
    // Ensure both ran cleanly and outer state wasn't nullified prematurely
    assertFalse(outerFrames.stream().anyMatch(Progress.ProgressFrame::isBacktracking));
    assertFalse(innerFrames.stream().anyMatch(Progress.ProgressFrame::isBacktracking));
  }

  @Test
  public void testDirectInnerInvocationFallback() {
    List<Progress.ProgressFrame> frames = new ArrayList<>();
    Parser root = digit();
    Parser instrumented = Progress.on(root, p -> true, frames::add);
    // The instrumented parser wraps the transformed parser
    Parser inner = instrumented.getChildren().get(0);
    Result result = inner.parseOn(new Context("1", 0));
    assertTrue(result.isSuccess());
    assertEquals(1, frames.size());
    assertEquals(0, frames.get(0).getPosition());
  }
}
