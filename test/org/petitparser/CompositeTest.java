package org.petitparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.Chars.character;

import java.util.Map;

import org.junit.Test;
import org.petitparser.parser.CompositeParser;
import org.petitparser.parser.Parser;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * Tests {@link CompositeParser}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class CompositeTest {

  @Test
  public void testStart() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", character('a'));
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
        def("start", ref("loop").or(character('b')));
        def("loop", character('a').seq(ref("start")));
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
        def("start", character('b'));
        redef("start", character('a'));
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
        final Parser b = character('b');
        def("start", b);
        redef("start", new Function<Parser, Parser>() {
          @Override
          public Parser apply(Parser parser) {
            assertEquals(b, parser);
            return character('a');
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
    final Map<String, Parser> parsers = ImmutableMap.<String, Parser>builder()
        .put("start", character('a'))
        .put("something", character('b'))
        .put("else", character('c'))
        .build();
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
        def("start", character('a'));
      }
    };
    parser.ref("star1");
  }

  @Test(expected = IllegalStateException.class)
  public void testDuplicatedStart() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", character('a'));
        def("start", character('b'));
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testUndefinedStart() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("star1", character('a'));
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testUndefinedRedef() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", character('a'));
        redef("star1", character('b'));
      }
    };
  }


}
