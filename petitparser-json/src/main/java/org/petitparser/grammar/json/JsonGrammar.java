package org.petitparser.grammar.json;

import static org.petitparser.parser.characters.CharacterParser.anyOf;
import static org.petitparser.parser.characters.CharacterParser.digit;
import static org.petitparser.parser.characters.CharacterParser.pattern;
import static org.petitparser.Parsers.string;

import org.petitparser.tools.CompositeParser;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Chars;
import org.petitparser.parser.characters.CharacterParser;

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
      CharacterParser.is('[').trim()
        .seq(ref("elements").optional())
        .seq(CharacterParser.is(']').trim()));
    def("elements",
      ref("value").separatedBy(CharacterParser.is(',').trim()));
    def("members",
      ref("pair").separatedBy(CharacterParser.is(',').trim()));
    def("object",
      CharacterParser.is('{').trim()
        .seq(ref("members").optional())
        .seq(CharacterParser.is('}').trim()));
    def("pair",
      ref("stringToken")
        .seq(CharacterParser.is(':').trim())
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
      CharacterParser.is('\\').seq(
          CharacterParser.anyOf(new String(Chars.toArray(ESCAPE_TABLE.keySet())))));
    def("characterOctal",
      string("\\u").seq(CharacterParser.pattern("0-9A-Fa-f").times(4).flatten()));
    def("characterNormal",
        CharacterParser.anyOf("\"\\").negate());
    def("numberPrimitive",
      CharacterParser.is('-').optional()
        .seq(CharacterParser.is('0').or(CharacterParser.digit().plus()))
        .seq(CharacterParser.is('.').seq(CharacterParser.digit().plus()).optional())
        .seq(CharacterParser
            .anyOf("eE").seq(CharacterParser.anyOf("-+").optional()).seq(CharacterParser.digit().plus()).optional()));
    def("stringPrimitive",
      CharacterParser.is('"')
        .seq(ref("characterPrimitive").star())
        .seq(CharacterParser.is('"')));
  }

}
