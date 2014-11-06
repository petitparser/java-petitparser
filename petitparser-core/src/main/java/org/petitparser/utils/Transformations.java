package org.petitparser.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.petitparser.parser.DelegateParser;
import org.petitparser.parser.Parser;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Utility functions to transform parsers.
 */
public class Transformations {

  /**
   * Transforms all parsers reachable from {@code parser} with the given {@code function}.
   * The identity function returns a copy of the the incoming parser.
   *
   * @param root the root of the parser graph.
   * @param function the transformation function.
   */
  public static Parser map(Parser root, Function<Parser, Parser> function) {
    Map<Parser, Parser> mapping = Maps.newHashMap();
    for (Parser parser : Queries.iterable(root)) {
      try {
        mapping.put(parser, function.apply(parser.clone()));
      } catch (CloneNotSupportedException exception) {
        throw new IllegalStateException(exception);
      }
    }
    Set<Parser> seen = Sets.newHashSet(mapping.values());
    List<Parser> todo = Lists.newArrayList(mapping.values());
    while (!todo.isEmpty()) {
      Parser parent = todo.remove(todo.size() - 1);
      for (Parser source : parent.getChildren()) {
        if (mapping.containsKey(source)) {
          parent.replace(source, mapping.get(source));
        } else {
          seen.add(source);
          todo.add(source);
        }
      }
    }
    return mapping.get(root);
  }

  /**
   * Removes plain delegates from the parser starting in {@code root}.
   */
  public static Parser removeDelegates(Parser root) {
    return map(root, new Function<Parser, Parser>() {
      @Override
      public Parser apply(Parser input) {
        while (input.getClass().equals(DelegateParser.class)) {
          input = ((DelegateParser) input).getDelegate();
        }
        return input;
      }
    });
  }

  /**
   * Removes duplicate parsers from the parser starting in {@code root}.
   */
  public static Parser removeDuplicates(Parser root) {
    final Set<Parser> uniques = Sets.newHashSet();
    return map(root, new Function<Parser, Parser>() {
      @Override
      public Parser apply(final Parser source) {
        Parser target = Iterables.find(uniques, new Predicate<Parser>() {
          @Override
          public boolean apply(Parser each) {
            return source != each && source.matches(each);
          }
        }, null);
        if (target == null) {
          uniques.add(source);
          return source;
        } else {
          return target;
        }
      }
    });
  }

}
