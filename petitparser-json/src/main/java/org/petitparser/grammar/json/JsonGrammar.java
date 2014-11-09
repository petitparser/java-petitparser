package org.petitparser.grammar.json;

import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.CompositeParser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JSON grammar definition.
 */
public class JsonGrammar extends CompositeParser {

  protected static Map<Character, Character> createEscapeTable() {
    Map<Character, Character> table = new HashMap<>();
    table.put('\\', '\\');
    table.put('/', '/');
    table.put('"', '"');
    table.put('b', '\b');
    table.put('f', '\f');
    table.put('n', '\n');
    table.put('r', '\r');
    table.put('t', '\t');
    return Collections.unmodifiableMap(table);
  }

  protected static String listToString(Collection<Character> characters) {
    StringBuilder builder = new StringBuilder(characters.size());
    for (Character character : characters) {
      builder.append(character);
    }
    return builder.toString();
  }

  protected static final Map<Character, Character> ESCAPE_TABLE = createEscapeTable();
  protected static final Function<Character, Character> ESCAPE_TABLE_FUNCTION = ESCAPE_TABLE::get;

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

    def("trueToken", StringParser.of("true").flatten().trim());
    def("falseToken", StringParser.of("false").flatten().trim());
    def("nullToken", StringParser.of("null").flatten().trim());
    def("stringToken", ref("stringPrimitive").flatten().trim());
    def("numberToken", ref("numberPrimitive").flatten().trim());

    def("characterPrimitive",
      ref("characterEscape")
        .or(ref("characterOctal"))
        .or(ref("characterNormal")));
    def("characterEscape",
      CharacterParser.is('\\').seq(
          CharacterParser.anyOf(listToString(ESCAPE_TABLE.keySet()))));
    def("characterOctal",
        StringParser.of("\\u").seq(CharacterParser.pattern("0-9A-Fa-f").times(4).flatten()));
    def("characterNormal",
        CharacterParser.anyOf("\"\\").neg());
    def("numberPrimitive",
      CharacterParser.is('-').optional()
        .seq(CharacterParser.is('0').or(CharacterParser.digit().plus()))
        .seq(CharacterParser.is('.').seq(CharacterParser.digit().plus()).optional())
        .seq(CharacterParser.anyOf("eE").seq(CharacterParser.anyOf("-+").optional())
            .seq(CharacterParser.digit().plus()).optional()));
    def("stringPrimitive",
      CharacterParser.is('"')
        .seq(ref("characterPrimitive").star())
        .seq(CharacterParser.is('"')));
  }

}
