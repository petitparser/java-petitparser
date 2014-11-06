package org.petitparser.parser;

import org.petitparser.buffer.Token;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that creates a token from the parsed input.
 */
public class TokenParser extends DelegateParser {

  public TokenParser(Parser delegate) {
    super(delegate);
  }

  @Override
  public Result parse(Context context) {
    Result result = super.parse(context);
    if (result.isSuccess()) {
      Token token = context.getBuffer()
          .newToken(context.getPosition(), result.getPosition(), result.get());
      return result.success(token);
    } else {
      return result;
    }
  }

}
