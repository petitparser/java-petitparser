package org.petitparser.grammar.json;

import static org.petitparser.Chars.anyOf;
import static org.petitparser.Chars.character;
import static org.petitparser.Chars.digit;
import static org.petitparser.Chars.pattern;
import static org.petitparser.Parsers.string;

import org.petitparser.parser.Parser;
import org.petitparser.tools.CompositeParser;
import org.petitparser.tools.Production;

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

  @Override
  protected Parser start() {
    return value.end();
  }

  // grammar
  Parser array;
  Parser elements;
  Parser members;
  Parser object;
  Parser pair;
  Parser value;

  @Production
  Parser array() {
    return character('[').trim()
        .seq(elements.optional())
        .seq(character(']').trim());
  }

  @Production
  Parser elements() {
    return value.separatedBy(character(',').trim());
  }

  @Production
  Parser members() {
    return pair.separatedBy(character(',').trim());
  }

  @Production
  Parser object() {
    return character('{').trim()
        .seq(members.optional())
        .seq(character('}').trim());
  }

  @Production
  Parser pair() {
    return stringToken
        .seq(character(':').trim())
        .seq(value);
  }

  @Production
  Parser value() {
    return stringToken
        .or(numberToken)
        .or(object)
        .or(array)
        .or(trueToken)
        .or(falseToken)
        .or(nullToken);
  }

  // tokens
  Parser trueToken;
  Parser falseToken;
  Parser nullToken;
  Parser stringToken;
  Parser numberToken;

  @Production
  Parser trueToken() {
      return string("true").flatten().trim();
  }

  @Production
  Parser falseToken() {
    return string("false").flatten().trim();
  }

  @Production
  Parser nullToken() {
    return string("null").flatten().trim();
  }

  @Production
  Parser stringToken() {
    return stringPrimitive.flatten().trim();
  }

  @Production
  Parser numberToken() {
    return numberPrimitive.flatten().trim();
  }

  // primitives
  Parser characterPrimitive;
  Parser characterEscape;
  Parser characterNormal;
  Parser characterOctal;
  Parser numberPrimitive;
  Parser stringPrimitive;

  static final ImmutableMap<Character, Character> ESCAPE_TABLE
      = ImmutableMap.<Character, Character>builder()
          .put('\\', '\\')
          .put('/', '/')
          .put('"', '"')
          .put('b', '\b')
          .put('f', '\f')
          .put('n', '\n')
          .put('r', '\r')
          .put('t', '\t')
          .build();
  static final Function<Character, Character> ESCAPE_TABLE_FUNCTION =
      Functions.forMap(ESCAPE_TABLE);

  @Production
  Parser characterPrimitive() {
    return (characterEscape)
        .or(characterOctal)
        .or(characterNormal);
  }

  @Production
  Parser characterEscape() {
    return character('\\').seq(anyOf(new String(Chars.toArray(ESCAPE_TABLE.keySet()))));
  }

  @Production
  Parser characterNormal() {
    return anyOf("\"\\").negate();
  }

  @Production
  Parser characterOctal() {
    return (string("\\u")).seq(pattern("0-9A-Fa-f").times(4).flatten());
  }

  @Production
  Parser numberPrimitive() {
    return character('-').optional()
        .seq(character('0').or(digit().plus()))
        .seq(character('.').seq(digit().plus()).optional())
        .seq(anyOf("eE").seq(anyOf("-+").optional()).seq(digit().plus()).optional());
  }

  @Production
  Parser stringPrimitive() {
    return character('"').seq(characterPrimitive.star()).seq(character('"'));
  }

}
