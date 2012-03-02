package org.petitparser;

import org.petitparser.context.Context;

/**
 * Parses a sequence of characters.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class StringParser extends AbstractParser<String> {

  private final String string;

  public StringParser(String string) {
    this.string = string;
  }

  @Override
  public Context<String> parse(Context<?> context) {
    return context.success(string);
  }

}
