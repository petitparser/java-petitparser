package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultFlattenJoiner implements FlattenJoiner {
  @Override
  public Result join(Context context, Result toBeJoined) {
    Object resultObject = toBeJoined.get();

    if (resultObject == null) {
      return toBeJoined.failure("cannot join null");
    }

    String joined = joinRecursively(toBeJoined.get());

    if (joined != null) {
      return toBeJoined.success(joined);
    } else {
      String flattened = context.getBuffer()
              .substring(context.getPosition(), toBeJoined.getPosition());
      return toBeJoined.success(flattened);
    }
  }

  private String joinRecursively(Object object) {
    if (object instanceof List) {
      List<Object> list = (List) object;
      return list.stream()
              .map(this::joinRecursively)
              .filter(Objects::nonNull)
              .collect(Collectors.joining());
    }
    else if (object instanceof Character) {
      return object.toString();
    }
    else if (object instanceof String) {
      return (String) object;
    }
    else {
      return null;
    }
  }
}
