package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.tools.CompositeParser;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests {@link CompositeParser}.
 */
public class CompositeTest {

  @Test
  public void testStart() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", CharacterParser.is('a'));
      }
    };
    assertSuccess(parser, "a", 'a', 1);
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "");
  }

  @Test
  public void testCircular() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", ref("loop").or(CharacterParser.is('b')));
        def("loop", CharacterParser.is('a').seq(ref("start")));
      }
    };
    assertTrue(parser.accept("b"));
    assertTrue(parser.accept("ab"));
    assertTrue(parser.accept("aab"));
    assertTrue(parser.accept("aaab"));
  }

  @Test
  public void testRedefineWithParser() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", CharacterParser.is('b'));
        redef("start", CharacterParser.is('a'));
      }
    };
    assertSuccess(parser, "a", 'a', 1);
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "");
  }

  @Test
  public void testRedefineWithFunction() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        final Parser b = CharacterParser.is('b');
        def("start", b);
        redef("start", parser -> {
          assertEquals(b, parser);
          return CharacterParser.is('a');
        });
      }
    };
    assertSuccess(parser, "a", 'a', 1);
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "");
  }

  @Test
  public void testRefCompleted() {
    final Map<String, Parser> parsers = new HashMap<>();
    parsers.put("start", CharacterParser.is('a'));
    parsers.put("something", CharacterParser.is('b'));
    parsers.put("else", CharacterParser.is('c'));
    final CompositeParser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        for (Map.Entry<String, Parser> entry : parsers.entrySet()) {
          def(entry.getKey(), entry.getValue());
        }
      }
    };
    for (Map.Entry<String, Parser> entry : parsers.entrySet()) {
      assertEquals(entry.getValue(), parser.ref(entry.getKey()));
    }
  }

  @Test(expected = IllegalStateException.class)
  public void testRefUnknown() {
    CompositeParser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", CharacterParser.is('a'));
      }
    };
    parser.ref("star1");
  }

  @Test(expected = IllegalStateException.class)
  public void testDuplicatedStart() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", CharacterParser.is('a'));
        def("start", CharacterParser.is('b'));
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testUndefinedStart() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("star1", CharacterParser.is('a'));
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testUndefinedRedef() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", CharacterParser.is('a'));
        redef("star1", CharacterParser.is('b'));
      }
    };
  }

}
