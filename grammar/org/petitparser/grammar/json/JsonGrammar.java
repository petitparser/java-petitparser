package org.petitparser.grammar.json;

import static org.petitparser.Chars.anyOf;
import static org.petitparser.Chars.character;
import static org.petitparser.Chars.digit;
import static org.petitparser.Chars.pattern;
import static org.petitparser.Parsers.string;

import org.petitparser.parser.CompositeParser;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Chars;

/**
 * JSON grammar definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class JsonGrammar extends CompositeParser {

  protected static final ImmutableMap<Character, Character> ESCAPE_TABLE =
      ImmutableMap.<Character, Character>builder()
          .put('\\', '\\')
          .put('/', '/')
          .put('"', '"')
          .put('b', '\b')
          .put('f', '\f')
          .put('n', '\n')
          .put('r', '\r')
          .put('t', '\t')
          .build();
  protected static final Function<Character, Character> ESCAPE_TABLE_FUNCTION =
      Functions.forMap(ESCAPE_TABLE);

  @Override
  protected void initialize() {
    define("start", reference("value").end());

    define("array",
      character('[').trim()
        .seq(reference("elements").optional())
        .seq(character(']').trim()));
    define("elements",
      reference("value").separatedBy(character(',').trim()));
    define("members",
      reference("pair").separatedBy(character(',').trim()));
    define("object",
      character('{').trim()
        .seq(reference("members").optional())
        .seq(character('}').trim()));
    define("pair",
      reference("stringToken")
        .seq(character(':').trim())
        .seq(reference("value")));
    define("value",
      reference("stringToken")
        .or(reference("numberToken"))
        .or(reference("trueToken"))
        .or(reference("falseToken"))
        .or(reference("nullToken"))
        .or(reference("object"))
        .or(reference("array")));

    define("trueToken", string("true").flatten().trim());
    define("falseToken", string("false").flatten().trim());
    define("nullToken", string("null").flatten().trim());
    define("stringToken", reference("stringPrimitive").flatten().trim());
    define("numberToken", reference("numberPrimitive").flatten().trim());

    define("characterPrimitive",
      reference("characterEscape")
        .or(reference("characterOctal"))
        .or(reference("characterNormal")));
    define("characterEscape",
      character('\\').seq(anyOf(new String(Chars.toArray(ESCAPE_TABLE.keySet())))));
    define("characterOctal",
      string("\\u").seq(pattern("0-9A-Fa-f").times(4).flatten()));
    define("characterNormal",
        anyOf("\"\\").negate());
    define("numberPrimitive",
      character('-').optional()
        .seq(character('0').or(digit().plus()))
        .seq(character('.').seq(digit().plus()).optional())
        .seq(anyOf("eE").seq(anyOf("-+").optional()).seq(digit().plus()).optional()));
    define("stringPrimitive",
      character('"')
        .seq(reference("characterPrimitive").star())
        .seq(character('"')));
  }

}
