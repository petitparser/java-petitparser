package org.petitparser.grammar.xml;

import static org.petitparser.Chars.character;
import static org.petitparser.Chars.pattern;
import static org.petitparser.Chars.whitespace;
import static org.petitparser.Parsers.string;

import java.util.List;

import org.petitparser.parser.CompositeParser;
import org.petitparser.utils.Functions;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * XML grammar definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlGrammar extends CompositeParser {

  private static final String NAME_START_CHARS = ":A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6"
      + "\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF"
      + "\u3001\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD";
  private static final String NAME_CHARS = "-.0-9\u00B7\u0300-\u036F\u203F-\u2040"
      + NAME_START_CHARS;

  @Override
  protected void initialize() {
    define("start", reference("document").end());

    define("attribute", reference("qualified")
      .seq(reference("whitespace").optional())
      .seq(character('='))
      .seq(reference("whitespace").optional())
      .seq(reference("attributeValue"))
      .map(Functions.permutationOfList(0, 4)));
    define("attributeValue", reference("attributeValueDouble")
      .or(reference("attributeValueSingle"))
      .map(Functions.nthOfList(1)));
    define("attributeValueDouble", character('"')
      .seq(character('"').negate().star().flatten())
      .seq(character('"')));
    define("attributeValueSingle", character('\'')
      .seq(character('\'').negate().star().flatten())
      .seq(character('\'')));
    define("attributes", reference("whitespace")
      .seq(reference("attribute"))
      .map(Functions.nthOfList(1))
      .star());
    define("comment", string("<!--")
      .seq(string("-->").negate().star().flatten())
      .seq(string("-->"))
      .map(Functions.nthOfList(1)));
    define("content", reference("characterData")
      .or(reference("element"))
      .or(reference("processing"))
      .or(reference("comment"))
      .star());
    define("doctype", string("<!DOCTYPE")
      .seq(reference("whitespace").optional())
      .seq(character('[').negate().star()
        .seq(character('['))
        .seq(character(']').negate().star())
        .seq(character(']'))
        .flatten())
      .seq(reference("whitespace").optional())
      .seq(character('>'))
      .map(Functions.nthOfList(2)));
    define("document", reference("processing").optional()
      .seq(reference("misc"))
      .seq(reference("doctype").optional())
      .seq(reference("misc"))
      .seq(reference("element"))
      .seq(reference("misc"))
      .map(Functions.permutationOfList(0, 2, 4)));
    define("element", character('<')
      .seq(reference("qualified"))
      .seq(reference("attributes"))
      .seq(reference("whitespace").optional())
      .seq(string("/>")
        .or(character('>')
          .seq(reference("content"))
          .seq(string("</"))
          .seq(reference("qualified"))
          .seq(reference("whitespace").optional())
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
        }));
    define("processing", string("<?")
      .seq(reference("nameToken"))
      .seq(reference("whitespace")
        .seq(string("?>").negate().star())
        .optional()
        .flatten())
      .seq(string("?>"))
      .map(Functions.permutationOfList(1, 2)));
    define("qualified", reference("nameToken"));

    define("characterData", character('<').negate().plus().flatten());
    define("misc", reference("whitespace")
      .or(reference("comment"))
      .or(reference("processing"))
      .star());
    define("whitespace", whitespace().plus());

    define("nameToken", reference("nameStartChar")
      .seq(reference("nameStartChar").star())
      .flatten());
    define("nameStartChar", pattern(NAME_START_CHARS));
    define("nameChar", pattern(NAME_CHARS));
  }

}
