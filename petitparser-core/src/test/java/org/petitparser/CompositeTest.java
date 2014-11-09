package org.petitparser;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.petitparser.tools.CompositeParser;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;

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
    assertFailure(parser, "b", "a expected");
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
    assertTrue(Parsing.accepts(parser, "b"));
    assertTrue(Parsing.accepts(parser, "ab"));
    assertTrue(Parsing.accepts(parser, "aab"));
    assertTrue(Parsing.accepts(parser, "aaab"));
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
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, "");
  }

  @Test
  public void testRedefineWithFunction() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        final Parser b = CharacterParser.is('b');
        def("start", b);
        redef("start", new Function<Parser, Parser>() {
          @Override
          public Parser apply(Parser parser) {
            assertEquals(b, parser);
            return CharacterParser.is('a');
          }
        });
      }
    };
    assertSuccess(parser, "a", 'a', 1);
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, "");
  }

  @Test
  public void testRefCompleted() {
    final Map<String, Parser> parsers =
        ImmutableMap.<String, Parser> builder().put("start", CharacterParser.is('a'))
            .put("something", CharacterParser.is('b'))
            .put("else", CharacterParser.is('c')).build();
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
  public void testRefUnknonw() {
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
