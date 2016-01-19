package org.petitparser.tools;

import org.junit.Test;
import org.petitparser.parser.Parser;

import java.util.HashMap;
import java.util.Map;

import static java.util.function.Function.identity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link CompositeParser}.
 */
public class CompositeParserTest {

  @Test
  public void testStart() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", of('a'));
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
        def("start", ref("loop").or(of('b')));
        def("loop", of('a').seq(ref("start")));
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
        def("start", of('b'));
        redef("start", of('a'));
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
        Parser b = of('b');
        def("start", b);
        redef("start", parser -> {
          assertEquals(b, parser);
          return of('a');
        });
      }
    };
    assertSuccess(parser, "a", 'a', 1);
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "");
  }

  @Test
  public void testRedefineWithAction() {
    Parser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", of('a'));
        action("start", value -> 'b');
      }
    };
    assertSuccess(parser, "a", 'b', 1);
  }

  @Test
  public void testRefCompleted() {
    Map<String, Parser> parsers = new HashMap<>();
    parsers.put("start", of('a'));
    parsers.put("something", of('b'));
    parsers.put("else", of('c'));
    CompositeParser parser = new CompositeParser() {
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
        def("start", of('a'));
      }
    };
    parser.ref("star1");
  }

  @Test(expected = IllegalStateException.class)
  public void testDuplicatedStart() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", of('a'));
        def("start", of('b'));
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testUndefinedStart() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("star1", of('a'));
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testUndefinedRedef() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", of('a'));
        redef("star1", of('b'));
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testUndefinedRedefWithFunction() {
    new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", of('a'));
        redef("star1", identity());
      }
    };
  }

  @Test(expected = IllegalStateException.class)
  public void testDefAfterCompleted() {
    CompositeParser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", of('a'));
      }
    };
    parser.def("some", of('b'));
  }

  @Test(expected = IllegalStateException.class)
  public void testRedefAfterCompleted() {
    CompositeParser parser = new CompositeParser() {
      @Override
      protected void initialize() {
        def("start", of('a'));
      }
    };
    parser.redef("start", of('b'));
  }

}
