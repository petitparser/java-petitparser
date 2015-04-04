package org.petitparser.utils;

import org.junit.Test;
import org.petitparser.ExamplesTest;
import org.petitparser.context.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link org.petitparser.utils.Tracer}.
 */
public class TracerTest {

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

}
