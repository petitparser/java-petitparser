package org.petitparser.utils;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.combinators.SettableParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A reflective parser mirror.
 */
public class Mirror implements Iterable<Parser> {

  /**
   * Constructs a mirror of the provided {@code parser}.
   */
  public static Mirror of(Parser parser) {
    return new Mirror(parser);
  }

  private final Parser parser;

  public Mirror(Parser parser) {
    this.parser = Objects.requireNonNull(parser);
  }

  @Override
  public String toString() {
    return super.toString() + " of " + parser.toString();
  }

  /**
   * Returns an {@link Iterator} over {@code parser} and all its reachable descendants.
   */
  @Override
  public Iterator<Parser> iterator() {
    return new ParserIterator(parser);
  }

  private static class ParserIterator implements Iterator<Parser> {

    private final List<Parser> todo = new ArrayList<>();
    private final Set<Parser> seen = new HashSet<>();

    private ParserIterator(Parser root) {
      todo.add(root);
      seen.add(root);
    }

    @Override
    public boolean hasNext() {
      return !todo.isEmpty();
    }

    @Override
    public Parser next() {
      if (todo.isEmpty()) {
        throw new NoSuchElementException();
      }
      Parser current = todo.remove(todo.size() - 1);
      for (Parser parser : current.getChildren()) {
        if (!seen.contains(parser)) {
          todo.add(parser);
          seen.add(parser);
        }
      }
      return current;
    }
  }

  /**
   * Returns a {@link Stream} over {@code parser} and all its reachable descendants.
   */
  public Stream<Parser> stream() {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iterator(), Spliterator.DISTINCT | Spliterator.NONNULL),
        false);
  }

  /**
   * Returns a transformed copy of all parsers reachable from {@code parser}.
   */
  public Parser map(Function<Parser, Parser> transformer) {
    Map<Parser, Parser> mapping = new HashMap<>();
    for (Parser parser : this) {
      mapping.put(parser, transformer.apply(parser.copy()));
    }
    Set<Parser> seen = new HashSet<>(mapping.values());
    List<Parser> todo = new ArrayList<>(mapping.values());
    while (!todo.isEmpty()) {
      Parser parent = todo.remove(todo.size() - 1);
      for (Parser child : parent.getChildren()) {
        if (mapping.containsKey(child)) {
          parent.replace(child, mapping.get(child));
        } else if (!seen.contains(child)) {
          seen.add(child);
          todo.add(child);
        }
      }
    }
    return mapping.get(parser);
  }

  /**
   * Returns a transformed copy of all parsers where unnecessary delegates are removed.
   */
  public Parser removeDelegates() {
    return map(parser -> {
      while (DelegateParser.class.equals(parser.getClass()) ||
          SettableParser.class.equals(parser.getClass())) {
        parser = parser.getChildren().get(0);
      }
      return parser;
    });
  }

  /**
   * Returns a transformed copy of all parsers where unnecessary duplicates are collapsed.
   */
  public Parser removeDuplicates() {
    Set<Parser> uniques = new HashSet<>();
    return map(source -> {
      Optional<Parser> target = uniques.stream()
          .filter(each -> source != each && source.isEqualTo(each))
          .findFirst();
      if (target.isPresent()) {
        return target.get();
      } else {
        uniques.add(source);
        return source;
      }
    });
  }


}
