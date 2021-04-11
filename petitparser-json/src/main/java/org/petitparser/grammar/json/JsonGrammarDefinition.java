package org.petitparser.grammar.json;

import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.StringParser.of;

import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.tools.GrammarDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JSON grammar definition.
 */
public class JsonGrammarDefinition extends GrammarDefinition {

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
    characters.forEach(builder::append);
    return builder.toString();
  }

  protected static final Map<Character, Character> ESCAPE_TABLE =
      createEscapeTable();
  protected static final Function<Character, Character> ESCAPE_TABLE_FUNCTION =
      ESCAPE_TABLE::get;

  public JsonGrammarDefinition() {
    def("start", ref("value").end());

    def("array", of('[').trim()
        .seq(ref("elements").optional())
        .seq(of(']').trim()));
    def("elements", ref("value").separatedBy(of(',').trim()));
    def("members", ref("pair").separatedBy(of(',').trim()));
    def("object", of('{').trim()
        .seq(ref("members").optional())
        .seq(of('}').trim()));
    def("pair", ref("stringToken")
        .seq(of(':').trim())
        .seq(ref("value")));
    def("value", ref("stringToken")
        .or(ref("numberToken"))
        .or(ref("trueToken"))
        .or(ref("falseToken"))
        .or(ref("nullToken"))
        .or(ref("object"))
        .or(ref("array")));

    def("trueToken", of("true").flatten("Expected 'true'").trim());
    def("falseToken", of("false").flatten("Expected 'false'").trim());
    def("nullToken", of("null").flatten("Expected 'null'").trim());
    def("stringToken",
        ref("stringPrimitive").flatten("Expected string").trim());
    def("numberToken",
        ref("numberPrimitive").flatten("Expected number").trim());

    def("characterPrimitive", ref("characterEscape")
        .or(ref("characterOctal"))
        .or(ref("characterNormal")));
    def("characterEscape", of('\\')
        .seq(anyOf(listToString(ESCAPE_TABLE.keySet()))));
    def("characterOctal", of("\\u")
        .seq(CharacterParser.pattern("0-9A-Fa-f").times(4).flatten()));
    def("characterNormal", anyOf("\"\\").neg());
    def("numberPrimitive", of('-').optional()
        .seq(of('0').or(digit().plus()))
        .seq(of('.').seq(digit().plus()).optional())
        .seq(anyOf("eE").seq(anyOf("-+").optional())
            .seq(digit().plus()).optional()));
    def("stringPrimitive", of('"')
        .seq(ref("characterPrimitive").star())
        .seq(of('"')));
  }
}
