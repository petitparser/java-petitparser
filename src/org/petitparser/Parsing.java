package org.petitparser;

import static org.petitparser.Chars.any;

import java.util.List;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Utility methods for parsing input.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Parsing {

  /**
   * Returns the parse result of a {@code parser} in {@code string}.
   *
   * @see Parser#parse(org.petitparser.context.Context)
   */
  public static Result parse(Parser parser, String string) {
    return parser.parse(new Context(string));
  }

  /**
   * Returns {@code true} if a {@code parser} can successfully parse
   * {@code string}.
   */
  public static boolean accepts(Parser parser, String string) {
    return parse(parser, string).isSuccess();
  }

  /**
   * Returns a list of all successful overlapping parses of a {@code parser} in
   * {@code string}.
   */
  public static <T> List<T> matches(Parser parser, String string) {
    final List<T> list = Lists.newArrayList();
    final Parser reader = parser.and().map(new Function<T, Object>() {
      @Override
      public Object apply(T input) {
        list.add(input);
        return null;
      }
    }).seq(any()).or(any()).star();
    parse(reader, string);
    return list;
  }

  /**
   * Returns a list of all successful non-overlapping parses of a {@code parser}
   * in {@code string}.
   */
  public static <T> List<T> matchesSkipping(Parser parser, String string) {
    final List<T> list = Lists.newArrayList();
    final Parser reader = parser.map(new Function<T, Object>() {
      @Override
      public Object apply(T input) {
        list.add(input);
        return null;
      }
    }).or(any()).star();
    parse(reader, string);
    return list;
  }

}
