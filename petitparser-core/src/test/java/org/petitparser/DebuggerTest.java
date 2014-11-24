package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Result;
import org.petitparser.utils.debugger.Profiler;
import org.petitparser.utils.debugger.Tracer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Profiler} and {@link Tracer}.
 */
public class DebuggerTest {

  @Test
  public void testSuccessfulTrace() {
    List<String> expected = Arrays.asList(
        "FlattenParser",
        "  SequenceParser",
        "    CharacterParser[letter expected]",
        "    Success[1:2]: a",
        "    PossessiveRepeatingParser[0..*]",
        "      CharacterParser[letter or digit expected]",
        "      Failure[1:2]: letter or digit expected",
        "    Success[1:2]: []",
        "  Success[1:2]: [a, []]",
        "Success[1:2]: a"
    );
    List<Tracer.TraceEvent> actual = new ArrayList<>();
    Result result = Tracer.on(ExamplesTest.IDENTIFIER, actual::add)
        .parse("a");
    assertTrue(result.isSuccess());
    assertEquals(expected, actual.stream()
        .map(Tracer.TraceEvent::toString)
        .collect(Collectors.toList()));
  }

  @Test
  public void testFailingTrace() {
    List<String> expected = Arrays.asList(
        "FlattenParser",
        "  SequenceParser",
        "    CharacterParser[letter expected]",
        "    Failure[1:1]: letter expected",
        "  Failure[1:1]: letter expected",
        "Failure[1:1]: letter expected"
    );
    List<Tracer.TraceEvent> actual = new ArrayList<>();
    Result result = Tracer.on(ExamplesTest.IDENTIFIER, actual::add)
        .parse("1");
    assertFalse(result.isSuccess());
    assertEquals(expected, actual.stream()
        .map(Tracer.TraceEvent::toString)
        .collect(Collectors.toList()));
  }

  @Test
  public void testSuccessfulProfile() {
    List<Profiler.Profile> actual = new ArrayList<>();
    Result result = Profiler.on(ExamplesTest.IDENTIFIER, actual::add)
        .parse("ab123");
    assertTrue(result.isSuccess());
    assertEquals(5, actual.size());

    assertEquals("FlattenParser", actual.get(0).parser.toString());
    assertEquals(1, actual.get(0).activationCount);
    assertTrue(actual.get(0).elapsedNanoseconds > 0);

    assertEquals("SequenceParser", actual.get(1).parser.toString());
    assertEquals(1, actual.get(1).activationCount);
    assertTrue(actual.get(1).elapsedNanoseconds > 0);

    assertEquals("PossessiveRepeatingParser[0..*]", actual.get(2).parser.toString());
    assertEquals(1, actual.get(2).activationCount);
    assertTrue(actual.get(2).elapsedNanoseconds > 0);

    assertEquals("CharacterParser[letter or digit expected]", actual.get(3).parser.toString());
    assertEquals(5, actual.get(3).activationCount);
    assertTrue(actual.get(3).elapsedNanoseconds > 0);

    assertEquals("CharacterParser[letter expected]", actual.get(4).parser.toString());
    assertEquals(1, actual.get(4).activationCount);
    assertTrue(actual.get(4).elapsedNanoseconds > 0);
  }

  @Test
  public void testFailingProfile() {
    List<Profiler.Profile> actual = new ArrayList<>();
    Result result = Profiler.on(ExamplesTest.IDENTIFIER, actual::add)
        .parse("1");
    assertFalse(result.isSuccess());
    assertEquals(5, actual.size());

    assertEquals("FlattenParser", actual.get(0).parser.toString());
    assertEquals(1, actual.get(0).activationCount);
    assertTrue(actual.get(0).elapsedNanoseconds > 0);

    assertEquals("SequenceParser", actual.get(1).parser.toString());
    assertEquals(1, actual.get(1).activationCount);
    assertTrue(actual.get(1).elapsedNanoseconds > 0);

    assertEquals("PossessiveRepeatingParser[0..*]", actual.get(2).parser.toString());
    assertEquals(0, actual.get(2).activationCount);
    assertEquals(0, actual.get(2).elapsedNanoseconds);

    assertEquals("CharacterParser[letter or digit expected]", actual.get(3).parser.toString());
    assertEquals(0, actual.get(3).activationCount);
    assertEquals(0, actual.get(3).elapsedNanoseconds);

    assertEquals("CharacterParser[letter expected]", actual.get(4).parser.toString());
    assertEquals(1, actual.get(4).activationCount);
    assertTrue(actual.get(4).elapsedNanoseconds > 0);
  }
}
