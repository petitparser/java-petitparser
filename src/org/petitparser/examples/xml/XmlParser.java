package org.petitparser.examples.xml;

import static org.petitparser.Chars.pattern;

import org.petitparser.parser.Parser;
import org.petitparser.tools.CompositeParser;
import org.petitparser.tools.Production;

public class XmlParser extends CompositeParser {

  private static final String NAME_START_CHARS = ":A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD";
  private static final String NAME_CHARS = NAME_START_CHARS + "-.0-9\u00B7\u0300-\u036F\u203F-\u2040";

  @Production Parser whitespaces;
  @Production Parser nameStartChar;
  @Production Parser nameChar;

  Parser whitespaces() {
    return whitespaces().plus();
  }

  Parser nameStartChar() {
    return pattern(NAME_START_CHARS);
  }

  Parser nameChar() {
    return pattern(NAME_CHARS);
  }

}
