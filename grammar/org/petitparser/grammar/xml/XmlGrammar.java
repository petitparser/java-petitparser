package org.petitparser.grammar.xml;

import static org.petitparser.Chars.character;
import static org.petitparser.Chars.pattern;
import static org.petitparser.Parsers.string;

import java.util.List;

import org.petitparser.Chars;
import org.petitparser.parser.Parser;
import org.petitparser.tools.CompositeParser;
import org.petitparser.tools.Production;
import org.petitparser.utils.Functions;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * XML grammar definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlGrammar extends CompositeParser {

  @Override
  public Parser start() {
    return document.end();
  }

  @Production Parser attribute;
  @Production Parser attributeValue;
  @Production Parser attributeValueDouble;
  @Production Parser attributeValueSingle;
  @Production Parser attributes;
  @Production Parser comment;
  @Production Parser content;
  @Production Parser doctype;
  @Production Parser document;
  @Production Parser element;
  @Production Parser processing;
  @Production Parser qualified;

  Parser attribute() {
    return qualified
        .seq(whitespace.optional())
        .seq(character('='))
        .seq(whitespace.optional())
        .seq(attributeValue)
        .map(Functions.permutationOfList(0, 4));
  }

  Parser attributeValue() {
    return attributeValueDouble
        .or(attributeValueSingle)
        .map(Functions.nthOfList(1));
  }

  Parser attributeValueDouble() {
    return character('"')
        .seq(character('"').negate().star().flatten())
        .seq(character('"'));
  }

  Parser attributeValueSingle() {
    return character('\'')
        .seq(character('\'').negate().star().flatten())
        .seq(character('\''));
  }

  Parser attributes() {
    return whitespace
        .seq(attribute)
        .map(Functions.nthOfList(1))
        .star();
  }

  Parser comment() {
    return string("<!--")
        .seq(string("-->").negate().star().flatten())
        .seq(string("-->"))
        .map(Functions.nthOfList(1));
  }

  Parser content() {
    return characterData
        .or(element)
        .or(processing)
        .or(comment)
        .star();
  }

  Parser doctype() {
    return string("<!DOCTYPE")
        .seq(whitespace.optional())
        .seq(character('[').negate().star()
            .seq(character('['))
            .seq(character(']').negate().star())
            .seq(character(']'))
            .flatten())
        .seq(whitespace.optional())
        .seq(character('>'))
        .map(Functions.nthOfList(2));
  }

  Parser document() {
    return processing.optional()
        .seq(misc)
        .seq(doctype.optional())
        .seq(misc)
        .seq(element)
        .seq(misc)
        .map(Functions.permutationOfList(0, 2, 4));
  }

  Parser element() {
    return character('<')
        .seq(qualified)
        .seq(attributes)
        .seq(whitespace.optional())
        .seq(string("/>")
            .or(character('>')
                .seq(content)
                .seq(string("</"))
                .seq(qualified)
                .seq(whitespace.optional())
                .seq(character('>'))))
        .map(new Function<List<?>, List<?>>() {
          @Override
          public List<?> apply(List<?> list) {
            if (list.get(4).equals("/>")) {
              return Lists.newArrayList(list.get(1), list.get(2), Lists.newArrayList());
            } else {
              List<?> end = (List<?>) list.get(4);
              if (list.get(1).equals(end.get(3))) {
                return Lists.newArrayList(list.get(1), list.get(2), end.get(1));
              } else {
                throw new IllegalStateException("Expected </" + list.get(1) + ">");
              }
            }
          }
        });
  }

  Parser processing() {
    return string("<?")
        .seq(nameToken)
        .seq(whitespace
            .seq(string("?>").negate().star())
            .optional()
            .flatten())
        .seq(string("?>"))
        .map(Functions.permutationOfList(1, 2));
  }

  Parser qualified() {
    return nameToken;
  }

  @Production Parser characterData;
  @Production Parser misc;
  @Production Parser whitespace;

  Parser characterData() {
    return character('<').negate().plus().flatten();
  }

  Parser misc() {
    return whitespace.or(comment).or(processing).star();
  }

  Parser whitespace() {
    return Chars.whitespace().plus();
  }

  private static final String NAME_START_CHARS = ":A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF"
      + "\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001\uD7FF\uF900-\uFDCF"
      + "\uFDF0-\uFFFD";
  private static final String NAME_CHARS = "-.0-9\u00B7\u0300-\u036F\u203F-\u2040"
      + NAME_START_CHARS;

  @Production Parser nameToken;
  @Production Parser nameStartChar;
  @Production Parser nameChar;

  Parser nameToken() {
    return nameStartChar.seq(nameChar.star()).flatten();
  }

  Parser nameStartChar() {
    return pattern(NAME_START_CHARS);
  }

  Parser nameChar() {
    return pattern(NAME_CHARS);
  }

}
