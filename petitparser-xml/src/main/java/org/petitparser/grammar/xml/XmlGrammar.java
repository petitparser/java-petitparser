package org.petitparser.grammar.xml;

import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.CompositeParser;
import org.petitparser.utils.Functions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * XML grammar definition.
 */
public class XmlGrammar extends CompositeParser {

  private static final String NAME_START_CHARS = ":A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6"
      + "\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF"
      + "\u3001\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD";
  private static final String NAME_CHARS = "-.0-9\u00B7\u0300-\u036F\u203F-\u2040"
      + NAME_START_CHARS;

  @Override
  protected void initialize() {
    def("start", ref("document").end());

    def("attribute", ref("qualified")
      .seq(ref("whitespace").optional())
      .seq(CharacterParser.is('='))
      .seq(ref("whitespace").optional())
      .seq(ref("attributeValue"))
      .map(Functions.permutationOfList(0, 4)));
    def("attributeValue", ref("attributeValueDouble")
      .or(ref("attributeValueSingle"))
      .map(Functions.nthOfList(1)));
    def("attributeValueDouble", CharacterParser.is('"')
      .seq(CharacterParser.is('"').neg().star().flatten())
      .seq(CharacterParser.is('"')));
    def("attributeValueSingle", CharacterParser.is('\'')
      .seq(CharacterParser.is('\'').neg().star().flatten())
      .seq(CharacterParser.is('\'')));
    def("attributes", ref("whitespace")
      .seq(ref("attribute"))
      .map(Functions.nthOfList(1))
      .star());
    def("comment", StringParser.of("<!--")
      .seq(StringParser.of("-->").neg().star().flatten())
      .seq(StringParser.of("-->"))
      .map(Functions.nthOfList(1)));
    def("content", ref("characterData")
      .or(ref("element"))
      .or(ref("processing"))
      .or(ref("comment"))
      .star());
    def("doctype", StringParser.of("<!DOCTYPE")
      .seq(ref("whitespace").optional())
      .seq(CharacterParser.is('[').neg().star()
        .seq(CharacterParser.is('['))
        .seq(CharacterParser.is(']').neg().star())
        .seq(CharacterParser.is(']'))
        .flatten())
      .seq(ref("whitespace").optional())
      .seq(CharacterParser.is('>'))
      .map(Functions.nthOfList(2)));
    def("document", ref("processing").optional()
      .seq(ref("misc"))
      .seq(ref("doctype").optional())
      .seq(ref("misc"))
      .seq(ref("element"))
      .seq(ref("misc"))
      .map(Functions.permutationOfList(0, 2, 4)));
    def("element", CharacterParser.is('<')
      .seq(ref("qualified"))
      .seq(ref("attributes"))
      .seq(ref("whitespace").optional())
      .seq(StringParser.of("/>")
        .or(CharacterParser.is('>')
          .seq(ref("content"))
          .seq(StringParser.of("</"))
          .seq(ref("qualified"))
          .seq(ref("whitespace").optional())
          .seq(CharacterParser.is('>'))))
      .map(new Function<List<?>, List<?>>() {
          @Override
          public List<?> apply(List<?> list) {
            if (list.get(4).equals("/>")) {
              return Arrays.asList(list.get(1), list.get(2), Arrays.asList());
            } else {
              List<?> end = (List<?>) list.get(4);
              if (list.get(1).equals(end.get(3))) {
                return Arrays.asList(list.get(1), list.get(2), end.get(1));
              } else {
                throw new IllegalStateException("Expected </" + list.get(1) + ">");
              }
            }
          }
        }));
    def("processing", StringParser.of("<?")
      .seq(ref("nameToken"))
      .seq(ref("whitespace")
        .seq(StringParser.of("?>").neg().star())
        .optional()
        .flatten())
      .seq(StringParser.of("?>"))
      .map(Functions.permutationOfList(1, 2)));
    def("qualified", ref("nameToken"));

    def("characterData", CharacterParser.is('<').neg().plus().flatten());
    def("misc", ref("whitespace")
      .or(ref("comment"))
      .or(ref("processing"))
      .star());
    def("whitespace", CharacterParser.whitespace().plus());

    def("nameToken", ref("nameStartChar")
      .seq(ref("nameChar").star())
      .flatten());
    def("nameStartChar", CharacterParser.pattern(NAME_START_CHARS));
    def("nameChar", CharacterParser.pattern(NAME_CHARS));
  }

}
