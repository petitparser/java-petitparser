package org.petitparser.examples.xml;

import static org.petitparser.Chars.character;
import static org.petitparser.Chars.pattern;
import static org.petitparser.Parsers.string;

import org.petitparser.Chars;
import org.petitparser.parser.Parser;
import org.petitparser.tools.CompositeParser;
import org.petitparser.tools.Production;

/**
 * Basic XML grammar definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlParser extends CompositeParser {

  @Override
  public Parser start() {
    return document.end();
  }

  @Production Parser attribute;
  @Production Parser attributeValue;
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
        .seq(attributeValue);
  }

  Parser attributeValue() {
    return or(
        character('"').seq(character('"').negate().star().flatten()).seq(character('"')),
        character('\'').seq(character('\'').negate().star().flatten()).seq(character('\'')));
  }

  Parser attributes() {
    return whitespace.seq(attribute).star();
  }

  Parser comment() {
    return string("<!--")
        .seq(string("-->").negate().star().flatten())
        .seq(string("-->"));
  }

  Parser content() {
    return characterData.optional()
        .seq(element.or(processing).or(comment))
        .seq(characterData.optional())
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
        .seq(character('>'));
  }

  Parser document() {
    return processing.optional()
        .seq(misc)
        .seq(doctype.optional())
        .seq(misc)
        .seq(element)
        .seq(misc);
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
                .seq(character('>'))));
  }

  Parser processing() {
    return string("<?")
        .seq(nameToken)
        .seq(whitespace
            .seq(string("?>").negate().star())
            .optional()
            .flatten())
        .seq(string("?>"));
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

  public static void main(String[] args) {
    new XmlParser();
  }

}
