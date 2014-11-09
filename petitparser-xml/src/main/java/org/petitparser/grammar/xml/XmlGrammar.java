package org.petitparser.grammar.xml;

import static org.petitparser.parser.characters.CharacterParser.pattern;
import static org.petitparser.parser.characters.CharacterParser.whitespace;
import static org.petitparser.Parsers.string;

import java.util.List;

import org.petitparser.tools.CompositeParser;
import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.utils.Functions;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

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
      .seq(CharacterParser.is('"').negate().star().flatten())
      .seq(CharacterParser.is('"')));
    def("attributeValueSingle", CharacterParser.is('\'')
      .seq(CharacterParser.is('\'').negate().star().flatten())
      .seq(CharacterParser.is('\'')));
    def("attributes", ref("whitespace")
      .seq(ref("attribute"))
      .map(Functions.nthOfList(1))
      .star());
    def("comment", string("<!--")
      .seq(string("-->").negate().star().flatten())
      .seq(string("-->"))
      .map(Functions.nthOfList(1)));
    def("content", ref("characterData")
      .or(ref("element"))
      .or(ref("processing"))
      .or(ref("comment"))
      .star());
    def("doctype", string("<!DOCTYPE")
      .seq(ref("whitespace").optional())
      .seq(CharacterParser.is('[').negate().star()
        .seq(CharacterParser.is('['))
        .seq(CharacterParser.is(']').negate().star())
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
      .seq(string("/>")
        .or(CharacterParser.is('>')
          .seq(ref("content"))
          .seq(string("</"))
          .seq(ref("qualified"))
          .seq(ref("whitespace").optional())
          .seq(CharacterParser.is('>'))))
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
    def("processing", string("<?")
      .seq(ref("nameToken"))
      .seq(ref("whitespace")
        .seq(string("?>").negate().star())
        .optional()
        .flatten())
      .seq(string("?>"))
      .map(Functions.permutationOfList(1, 2)));
    def("qualified", ref("nameToken"));

    def("characterData", CharacterParser.is('<').negate().plus().flatten());
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
