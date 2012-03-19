package org.petitparser.examples.json;

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
  @Production Parser array;
  @Production Parser elements;
  @Production Parser members;
  @Production Parser object;
  @Production Parser pair;
  @Production Parser value;

  Parser array() {
    return character('[').trim()
        .seq(elements.optional())
        .seq(character(']').trim());
  }

  Parser elements() {
    return value.separatedBy(character(',').trim());
  }

  Parser members() {
    return pair.separatedBy(character(',').trim());
  }

  Parser object() {
    return character('{').trim()
        .seq(members.optional())
        .seq(character('}').trim());
  }

  Parser pair() {
    return stringToken
        .seq(character(':').trim())
        .seq(value);
  }

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
  @Production Parser trueToken;
  @Production Parser falseToken;
  @Production Parser nullToken;
  @Production Parser stringToken;
  @Production Parser numberToken;

  Parser trueToken() {
    return string("true").flatten().trim();
  }

  Parser falseToken() {
    return string("false").flatten().trim();
  }

  Parser nullToken() {
    return string("null").flatten().trim();
  }

  Parser stringToken() {
    return stringPrimitive.flatten().trim();
  }

  Parser numberToken() {
    return numberPrimitive.flatten().trim();
  }

  // primitives
  @Production Parser characterPrimitive;
  @Production Parser characterEscape;
  @Production Parser characterNormal;
  @Production Parser characterOctal;
  @Production Parser numberPrimitive;
  @Production Parser stringPrimitive;

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

  Parser characterPrimitive() {
    return (characterEscape)
        .or(characterOctal)
        .or(characterNormal);
  }

  Parser characterEscape() {
    return character('\\').seq(anyOf(new String(Chars.toArray(ESCAPE_TABLE.keySet()))));
  }

  Parser characterNormal() {
    return anyOf("\"\\").negate();
  }

  Parser characterOctal() {
    return (string("\\u")).seq(pattern("0-9A-Fa-f").times(4).flatten());
  }

  Parser numberPrimitive() {
    return character('-').optional()
        .seq(character('0').or(digit().plus()))
        .seq(character('.').seq(digit().plus()).optional())
        .seq(anyOf("eE").seq(anyOf("-+").optional()).seq(digit().plus()).optional());
  }

  Parser stringPrimitive() {
    return character('"').seq(characterPrimitive.star()).seq(character('"'));
  }

}
