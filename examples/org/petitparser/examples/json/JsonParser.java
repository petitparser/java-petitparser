package org.petitparser.examples.json;

import java.util.List;
import java.util.Map;

import org.petitparser.parser.Parser;
import org.petitparser.utils.Functions;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Chars;


/**
 * JSON parser definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class JsonParser extends JsonGrammar {

  @Override
  Parser array() {
    return super.array().map(new Function<List<List<?>>, List<?>>() {
      @Override
      public List<?> apply(List<List<?>> input) {
        return input.get(1) != null ? input.get(1) : Lists.newArrayList();
      }
    });
  }

  @Override
  Parser elements() {
    return super.elements().map(Functions.withoutSpeparators());
  }

  @Override
  Parser members() {
    return super.members().map(Functions.withoutSpeparators());
  }

  @Override
  Parser object() {
    return super.object().map(new Function<List<List<List<Object>>>, Map<Object, Object>>() {
      @Override
      public Map<Object, Object> apply(List<List<List<Object>>> input) {
        Map<Object, Object> result = Maps.newLinkedHashMap();
        if (input.get(1) != null) {
          for (List<Object> list : input.get(1)) {
            result.put(list.get(0), list.get(2));
          }
        }
        return result;
      }
    });
  }

  @Override
  Parser trueToken() {
    return super.trueToken().map(Functions.constant(true));
  }

  @Override
  Parser falseToken() {
    return super.falseToken().map(Functions.constant(false));
  }

  @Override
  Parser nullToken() {
    return super.nullToken().map(Functions.constant(null));
  }

  @Override
  Parser stringToken() {
    return stringPrimitive.map(new Function<List<Character>, String>() {
      @Override
      public String apply(List<Character> input) {
        return new String(Chars.toArray(input));
      }
    });
  }

  @Override
  Parser numberToken() {
    return super.numberToken().map(new Function<String, Number>() {
      @Override
      public Number apply(String input) {
        double floating = Double.parseDouble(input);
        long integral = Math.round(floating);
        return floating == integral ? Long.valueOf(integral) : Double.valueOf(floating);
      }
    });
  }

  @Override
  Parser stringPrimitive() {
    return super.stringPrimitive().map(Functions.nthOfList(1));
  }

  @Override
  Parser characterEscape() {
    return super.characterEscape()
        .map(Functions.lastOfList())
        .map(ESCAPE_TABLE_FUNCTION);
  }

  @Override
  Parser characterOctal() {
    return super.characterOctal().map(new Function<List<String>, Character>(){
      @Override
      public Character apply(List<String> input) {
        // cannot be larger than 0xFFFF, so we should be safe with 16-bit
        return Character.toChars(Integer.parseInt(input.get(1), 16))[0];
      }
    });
  }


}
