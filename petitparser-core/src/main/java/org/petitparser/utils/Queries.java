package org.petitparser.utils;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.petitparser.parser.Parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Utility functions to query parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Queries {

  /**
   * Returns an {@link Iterable} over all parsers reachable from parser
   * {@code root}.
   */
  public static Iterable<Parser> iterable(final Parser root) {
    return new Iterable<Parser>() {
      @Override
      public Iterator<Parser> iterator() {
        return Queries.iterator(root);
      }
    };
  }

  /**
   * Returns an {@link Iterator} over all parsers reachable from parser
   * {@code root}.
   */
  public static Iterator<Parser> iterator(final Parser root) {
    return new Iterator<Parser>() {
      private final List<Parser> todo = Lists.newArrayList(root);
      private final Set<Parser> seen = Sets.newHashSet();

      @Override
      public boolean hasNext() {
        return !todo.isEmpty();
      }

      @Override
      public Parser next() {
        if (todo.isEmpty()) {
          throw new NoSuchElementException();
        }
        Parser parser = todo.remove(todo.size() - 1);
        seen.add(parser);
        todo.addAll(parser.getChildren().stream()
            .filter(child -> !seen.contains(child))
            .collect(Collectors.toList()));
        return parser;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

}
