package org.petitparser.examples.xml;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.examples.xml.ast.XmlNode;
import org.petitparser.parser.Parser;


/**
 * Tests {@link XmlParser}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlParserTest {

  private final Parser parser = new XmlParser();

  @Test
  public void testParseComment() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema><!-- comment --></schema>");
  }

  @Test
  public void testParseCommentWithXml() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema><!-- <foo></foo> --></schema>");
  }

  @Test
  public void testParseComplicated() {
    assertInvariants("<?xml foo?>\n" + "<foo>\n"
        + "  <bar a=\"fasdfasdf\">\n"
        + "    <zork/>\n" + "    <zonk/>\n"
        + "  </bar>\n"
        + "  <!-- with comment -->\n" + "</foo>");
  }

  @Test
  public void testParseDoctype() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "  <!DOCTYPE freaking <schema> [ <!-- schema --> ]  >\n"
        + "  <schema></schema>");
  }

  @Test
  public void testParseEmptyElement() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema/>");
  }

  @Test
  public void testParseNamespace() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xs:schema></xs:schema>");
  }

  @Test
  public void testParseSimple() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema></schema>");
  }

  @Test
  public void testParseSimpleAttribute() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema foo=\"bar\"></schema>");
  }

  @Test
  public void testParseSimpleAttributeWithSingleQuote() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema foo='bar'></schema>");
  }

  @Test
  public void testParseWithWhitespacefterProlog() {
    assertInvariants("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "  <schema></schema>\n");
  }

  private void assertInvariants(String xml) {
    Result result = parser.parse(new Context(xml));
    assertTrue(result.isSuccess());
    XmlNode node = result.get();
    System.out.println(node);
  }

}
