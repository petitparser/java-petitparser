package org.petitparser.utils;

import java.util.Map;

import org.petitparser.parser.DelegateParser;
import org.petitparser.parser.Parser;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Utility functions to transform parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Transformations {

  /**
   * Returns a transformed parser graph.
   *
   * @param root the root of the parser graph.
   * @param function the transformation function.
   */
  public static Parser map(Parser root, Function<Parser, Parser> function) {
    Map<Parser, Parser> mapping = Maps.newHashMap();
    for (Parser parser : Queries.iterable(root)) {
      try {
        mapping.put(parser, function.apply((Parser) parser.clone()));
      } catch (CloneNotSupportedException e) {
        throw new IllegalStateException(e);
      }
    }
    boolean changed;
    root = mapping.get(root);
    do {
      changed = false;
      for (Parser parent : Queries.iterable(root)) {
        for (Parser oldParser : parent.getChildren()) {
          Parser newParser = mapping.get(oldParser);
          if (newParser != null) {
            parent.replace(oldParser, newParser);
            changed = true;
          }
        }
      }
    } while (changed);
    return root;
  }

  /**
   * Removes plain delegates from the parser starting in {@code root}.
   */
  public static Parser removeDelegates(Parser root) {
    return map(root, new Function<Parser, Parser>() {
      @Override
      public Parser apply(Parser input) {
        if (input.getClass().equals(DelegateParser.class)) {
          return ((DelegateParser) input).getDelegate();
        } else {
          return input;
        }
      }
    });
  }

}
