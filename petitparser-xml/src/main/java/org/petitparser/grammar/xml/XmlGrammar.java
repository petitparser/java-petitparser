package org.petitparser.grammar.xml;

import org.petitparser.tools.CompositeParser;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.petitparser.parser.characters.CharacterParser.any;
import static org.petitparser.parser.characters.CharacterParser.of;
import static org.petitparser.parser.characters.CharacterParser.pattern;
import static org.petitparser.parser.characters.CharacterParser.whitespace;
import static org.petitparser.parser.primitive.StringParser.of;


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
        .seq(ref("space").optional())
        .seq(of('='))
        .seq(ref("space").optional())
        .seq(ref("attributeValue"))
        .permute(0, 4));
    def("attributeValue", ref("attributeValueDouble")
        .or(ref("attributeValueSingle"))
        .pick(1));
    def("attributeValueDouble", of('"')
        .seq(any().starLazy(of('"')).flatten())
        .seq(of('"')));
    def("attributeValueSingle", of('\'')
        .seq(any().starLazy(of('\'')).flatten())
        .seq(of('\'')));
    def("attributes", ref("space")
        .seq(ref("attribute"))
        .pick(1)
        .star());
    def("comment", of("<!--")
        .seq(any().starLazy(of("-->")).flatten())
        .seq(of("-->"))
        .pick(1));
    def("cdata", of("<![CDATA[")
        .seq(any().starLazy(of("]]>")).flatten())
        .seq(of("]]>"))
        .pick(1));
    def("content", ref("characterData")
        .or(ref("element"))
        .or(ref("processing"))
        .or(ref("comment"))
        .or(ref("cdata"))
        .star());
    def("doctype", of("<!DOCTYPE")
        .seq(ref("space"))
        .seq(ref("nameToken")
            .or(ref("attributeValue"))
            .or(any().starLazy(of('['))
                .seq(of('['))
                .seq(any().starLazy(of(']')))
                .seq(of(']')))
            .separatedBy(ref("space"))
            .flatten())
        .seq(ref("space").optional())
        .seq(of('>'))
        .pick(2));
    def("document", ref("processing").optional()
        .seq(ref("misc"))
        .seq(ref("doctype").optional())
        .seq(ref("misc"))
        .seq(ref("element"))
        .seq(ref("misc"))
        .permute(0, 2, 4));
    def("element", of('<')
        .seq(ref("qualified"))
        .seq(ref("attributes"))
        .seq(ref("space").optional())
        .seq(of("/>")
            .or(of('>')
                .seq(ref("content"))
                .seq(of("</"))
                .seq(ref("qualified"))
                .seq(ref("space").optional())
                .seq(of('>'))))
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
    def("processing", of("<?")
        .seq(ref("nameToken"))
        .seq(ref("space")
            .seq(any().starLazy(of("?>")).flatten())
            .pick(1).optional(""))
        .seq(of("?>"))
        .permute(1, 2));
    def("qualified", ref("nameToken"));

    def("characterData", pattern("^<").plus().flatten());
    def("misc", ref("space")
        .or(ref("comment"))
        .or(ref("processing"))
        .star());
    def("space", whitespace().plus());

    def("nameToken", ref("nameStartChar")
        .seq(ref("nameChar").star())
        .flatten());
    def("nameStartChar", pattern(NAME_START_CHARS, "Expected name"));
    def("nameChar", pattern(NAME_CHARS));
  }

}
