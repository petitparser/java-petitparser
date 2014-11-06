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
    def("start", ref("value").end());

    def("array",
      character('[').trim()
        .seq(ref("elements").optional())
        .seq(character(']').trim()));
    def("elements",
      ref("value").separatedBy(character(',').trim()));
    def("members",
      ref("pair").separatedBy(character(',').trim()));
    def("object",
      character('{').trim()
        .seq(ref("members").optional())
        .seq(character('}').trim()));
    def("pair",
      ref("stringToken")
        .seq(character(':').trim())
        .seq(ref("value")));
    def("value",
      ref("stringToken")
        .or(ref("numberToken"))
        .or(ref("trueToken"))
        .or(ref("falseToken"))
        .or(ref("nullToken"))
        .or(ref("object"))
        .or(ref("array")));

    def("trueToken", string("true").flatten().trim());
    def("falseToken", string("false").flatten().trim());
    def("nullToken", string("null").flatten().trim());
    def("stringToken", ref("stringPrimitive").flatten().trim());
    def("numberToken", ref("numberPrimitive").flatten().trim());

    def("characterPrimitive",
      ref("characterEscape")
        .or(ref("characterOctal"))
        .or(ref("characterNormal")));
    def("characterEscape",
      character('\\').seq(anyOf(new String(Chars.toArray(ESCAPE_TABLE.keySet())))));
    def("characterOctal",
      string("\\u").seq(pattern("0-9A-Fa-f").times(4).flatten()));
    def("characterNormal",
        anyOf("\"\\").negate());
    def("numberPrimitive",
      character('-').optional()
        .seq(character('0').or(digit().plus()))
        .seq(character('.').seq(digit().plus()).optional())
        .seq(anyOf("eE").seq(anyOf("-+").optional()).seq(digit().plus()).optional()));
    def("stringPrimitive",
      character('"')
        .seq(ref("characterPrimitive").star())
        .seq(character('"')));
  }

}
