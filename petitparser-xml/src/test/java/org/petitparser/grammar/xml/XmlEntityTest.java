package org.petitparser.grammar.xml;

import static org.junit.Assert.assertEquals;
import static org.petitparser.grammar.xml.XmlCharacterParser.encodeXmlAttributeValue;
import static org.petitparser.grammar.xml.XmlCharacterParser.encodeXmlText;

import org.junit.Test;
import org.petitparser.grammar.xml.ast.XmlDocument;
import org.petitparser.grammar.xml.ast.XmlText;
import org.petitparser.parser.Parser;

/**
 * Tests entity encoding and decoding.
 */
public class XmlEntityTest {

  private final Parser parser = new XmlParser();

  private String decode(String input) {
    XmlDocument document = parser.parse("<data>" + input + "</data>").get();
    XmlText node = (XmlText) document.getRootElement().getChildren().get(0);
    return node.getData();
  }

  @Test
  public void decodeHex() {
    assertEquals("A", decode("&#X41;"));
    assertEquals("a", decode("&#x61;"));
    assertEquals("z", decode("&#x7A;"));
  }

  @Test
  public void decodeInt() {
    assertEquals("A", decode("&#65;"));
    assertEquals("a", decode("&#97;"));
    assertEquals("z", decode("&#122;"));
  }

  @Test
  public void decodeNamed() {
    assertEquals("<", decode("&lt;"));
    assertEquals(">", decode("&gt;"));
    assertEquals("&", decode("&amp;"));
    assertEquals("'", decode("&apos;"));
    assertEquals("\"", decode("&quot;"));
  }

  @Test
  public void decodeInvalid() {
    assertEquals("&invalid;", decode("&invalid;"));
  }

  @Test
  public void decodeIncomplete() {
    assertEquals("&amp", decode("&amp"));
  }

  @Test
  public void decodeEmpty() {
    assertEquals("&;", decode("&;"));
  }

  @Test
  public void decodeSurrounded() {
    assertEquals("a&b", decode("a&amp;b"));
    assertEquals("&x&", decode("&amp;x&amp;"));
  }

  @Test
  public void decodeSequence() {
    assertEquals("&&", decode("&amp;&amp;"));
  }

  @Test
  public void encodeText() {
    assertEquals("&lt;", encodeXmlText("<"));
    assertEquals("&amp;", encodeXmlText("&"));
    assertEquals("hello", encodeXmlText("hello"));
    assertEquals("&lt;foo &amp;amp;>", encodeXmlText("<foo &amp;>"));
  }

  @Test
  public void encodeAttribute() {
    assertEquals("&quot;", encodeXmlAttributeValue("\""));
    assertEquals("hello", encodeXmlAttributeValue("hello"));
    assertEquals("&quot;hello&quot;", encodeXmlAttributeValue("\"hello\""));
  }
}
