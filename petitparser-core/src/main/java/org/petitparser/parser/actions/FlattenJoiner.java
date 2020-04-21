package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

public interface FlattenJoiner {
  Result join(Context context, Result toBeJoined);
}
