package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

/**
 * A parser that creates a token from the parsed input.
 */
public class TokenParser extends DelegateParser {

  public TokenParser(Parser delegate) {
    super(delegate);
  }

  @Override
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isSuccess()) {
      Token token = new Token(context.getBuffer(), context.getPosition(),
          result.getPosition(), result.get());
      return result.success(token);
    } else {
      return result;
    }
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return delegate.fastParseOn(buffer, position);
  }

  @Override
  public TokenParser copy() {
    return new TokenParser(delegate);
  }
}
