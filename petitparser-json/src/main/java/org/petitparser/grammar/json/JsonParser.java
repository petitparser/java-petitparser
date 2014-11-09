package org.petitparser.grammar.json;

import org.petitparser.utils.Functions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JSON parser definition.
 */
public class JsonParser extends JsonGrammar {

  @Override
  protected void initialize() {
    super.initialize();

    action("elements", Functions.withoutSeparators());
    action("members", Functions.withoutSeparators());
    action("array", new Function<List<List<?>>, List<?>>() {
      @Override
      public List<?> apply(List<List<?>> input) {
        return input.get(1) != null ? input.get(1) : new ArrayList<>();
      }
    });
    action("object", new Function<List<List<List<Object>>>, Map<Object, Object>>() {
      @Override
      public Map<Object, Object> apply(List<List<List<Object>>> input) {
        Map<Object, Object> result = new LinkedHashMap<>();
        if (input.get(1) != null) {
          for (List<Object> list : input.get(1)) {
            result.put(list.get(0), list.get(2));
          }
        }
        return result;
      }
    });

    action("trueToken", Functions.constant(true));
    action("falseToken", Functions.constant(false));
    action("nullToken", Functions.constant(null));
    redef("stringToken", ref("stringPrimitive").trim());
    action("numberToken", new Function<String, Number>() {
      @Override
      public Number apply(String input) {
        double floating = Double.parseDouble(input);
        long integral = (long) floating;
        if (floating == integral && input.indexOf('.') == -1) {
          return integral;
        } else {
          return floating;
        }
      }
    });

    action("stringPrimitive", new Function<List<List<Character>>, String>() {
      @Override
      public String apply(List<List<Character>> input) {
        return listToString(input.get(1));
      }
    });
    action("characterEscape", Functions.lastOfList());
    action("characterEscape", ESCAPE_TABLE_FUNCTION);
    action("characterOctal", new Function<List<String>, Character>() {
      @Override
      public Character apply(List<String> input) {
        // cannot be larger than 0xFFFF, so we should be safe with 16-bit
        return Character.toChars(Integer.parseInt(input.get(1), 16))[0];
      }
    });
  }

}
