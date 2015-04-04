package org.petitparser.tools;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.FailureParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Helper to compose complex grammars from various primitive parsers. To create a new composite
 * grammar subclass {@link CompositeParser}. Override the method {@link #initialize} and for every
 * production call {@link CompositeParser#def(String, org.petitparser.parser.Parser)} giving the
 * parsers a name. The start production must be named 'start'. To refer to other productions use
 * {@link CompositeParser#ref(String)}. To redefine or attach actions to productions use {@link
 * CompositeParser#redef(String, Function)}, {@link CompositeParser#redef(String,
 * org.petitparser.parser.Parser)} and {@link CompositeParser#action(String, Function)}.
 */
public abstract class CompositeParser extends DelegateParser {

  private boolean completed = false;
  private Map<String, Parser> defined = new HashMap<>();
  private Map<String, SettableParser> undefined = new HashMap<>();

  public CompositeParser() {
    super(FailureParser.withMessage("Undefined start production"));
    initialize();
    complete();
  }

  /**
   * Automatically called by the framework to initialize the grammar.
   */
  protected abstract void initialize();

  /**
   * Internal method that completes the initialization.
   */
  private void complete() {
    replace(delegate, ref("start"));
    for (Map.Entry<String, SettableParser> entry : undefined.entrySet()) {
      if (!defined.containsKey(entry.getKey())) {
        throw new IllegalStateException("Undefined production: " + entry.getKey());
      }
      entry.getValue().replace(entry.getValue().get(), defined.get(entry.getKey()));
    }
    replace(delegate, ref("start"));
    defined = Collections.unmodifiableMap(defined);
    undefined = Collections.unmodifiableMap(new HashMap<>());
    completed = true;
  }

  /**
   * Returns a reference to the production with the given {@code name}.
   * <p>
   * This method works during initialization and after completion of the initialization. During the
   * initialization it returns delegate parsers that are eventually replaced by the real parsers.
   * Afterwards it returns the defined parser (mostly useful for testing).
   */
  public final Parser ref(String name) {
    if (completed) {
      if (!defined.containsKey(name)) {
        throw new IllegalStateException("Undefined production: " + name);
      }
      return defined.get(name);
    } else if (undefined.containsKey(name)) {
      return undefined.get(name);
    } else {
      SettableParser parser = SettableParser.undefined("Uninitialized production: " + name);
      undefined.put(name, parser);
      return parser;
    }
  }

  /**
   * Defines a production with a {@code name} and a {@code parser}. Only call this method during
   * initialization.
   */
  protected final void def(String name, Parser parser) {
    if (completed) {
      throw new IllegalStateException("Completed parsers cannot be redefined");
    }
    if (defined.containsKey(name)) {
      throw new IllegalStateException("Duplicate production: " + name);
    }
    defined.put(Objects.requireNonNull(name), Objects.requireNonNull(parser));
  }

  /**
   * Redefines an existing production with a {@code name} and a new {@code parser}. Only call this
   * method during initialization.
   */
  protected final void redef(String name, Parser parser) {
    if (completed) {
      throw new IllegalStateException("Completed parsers cannot be redefined");
    }
    if (!defined.containsKey(name)) {
      throw new IllegalStateException("Undefined production: " + name);
    }
    defined.put(Objects.requireNonNull(name), Objects.requireNonNull(parser));
  }

  /**
   * Redefines an existing production with a {@code name} and a {@code function} producing a new
   * parser. Only call this method during initialization.
   */
  protected final void redef(String name, Function<Parser, Parser> function) {
    if (!defined.containsKey(name)) {
      throw new IllegalStateException("Undefined production: " + name);
    }
    redef(name, function.apply(defined.get(name)));
  }

  /**
   * Attaches an action {@code function} to an existing production {@code name}. Only call this
   * method during initialization.
   */
  protected final <S, T> void action(String name, final Function<S, T> function) {
    redef(name, parser -> parser.map(function));
  }
}
