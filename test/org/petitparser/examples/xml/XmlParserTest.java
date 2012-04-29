package org.petitparser.examples.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.grammars.xml.XmlParser;
import org.petitparser.grammars.xml.ast.XmlAttribute;
import org.petitparser.grammars.xml.ast.XmlDocument;
import org.petitparser.grammars.xml.ast.XmlElement;
import org.petitparser.grammars.xml.ast.XmlName;
import org.petitparser.grammars.xml.ast.XmlNode;
import org.petitparser.parser.Parser;

import com.google.common.collect.Lists;


/**
 * Tests {@link XmlParser}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlParserTest {

  private final Parser parser = new XmlParser();

  @Test
  public void testComment() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema><!-- comment --></schema>");
  }

  @Test
  public void testCommentWithXml() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema><!-- <foo></foo> --></schema>");
  }

  @Test
  public void testComplicated() {
    assertParseInvariant("<?xml foo?>\n" + "<foo>\n"
        + "  <bar a=\"fasdfasdf\">\n"
        + "    <zork/>\n" + "    <zonk/>\n"
        + "  </bar>\n"
        + "  <!-- with comment -->\n" + "</foo>");
  }

  @Test
  public void testDoctype() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "  <!DOCTYPE freaking <schema> [ <!-- schema --> ]  >\n"
        + "  <schema></schema>");
  }

  @Test
  public void testEmptyElement() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema/>");
  }

  @Test
  public void testNamespace() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xs:schema></xs:schema>");
  }

  @Test
  public void testSimple() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema></schema>");
  }

  @Test
  public void testSimpleAttribute() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema foo=\"bar\"></schema>");
  }

  @Test
  public void testSimpleAttributeWithSingleQuote() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?><schema foo='bar'></schema>");
  }

  @Test
  public void testWhitespaceAfterProlog() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "  <schema></schema>\n");
  }

  @Test
  public void testBookstore() {
    assertParseInvariant("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
        + "<bookstore>\n"
        + "  <book>\n"
        + "    <title lang=\"eng\">Harry Potter</title>\n"
        + "    <price>29.99</price>\n"
        + "  </book>\n"
        + "  <book>\n"
        + "    <title lang=\"eng\">Learning XML</title>\n"
        + "    <price>39.95</price>\n"
        + "  </book>\n"
        + "</bookstore>");
  }

  @Test
  public void testShiporder() {
    assertParseInvariant("<?xml version=\"1.0\"?>\n"
        + "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
        + "\n"
        + "  <xsd:annotation>\n"
        + "    <xsd:documentation xml:lang=\"en\">\n"
        + "     Purchase order schema for Example.com.\n"
        + "     Copyright 2000 Example.com. All rights reserved.\n"
        + "    </xsd:documentation>\n"
        + "  </xsd:annotation>\n"
        + "\n"
        + "  <xsd:element name=\"purchaseOrder\" type=\"PurchaseOrderType\"/>\n"
        + "\n"
        + "  <xsd:element name=\"comment\" type=\"xsd:string\"/>\n"
        + "\n"
        + "  <xsd:complexType name=\"PurchaseOrderType\">\n"
        + "    <xsd:sequence>\n"
        + "      <xsd:element name=\"shipTo\" type=\"USAddress\"/>\n"
        + "      <xsd:element name=\"billTo\" type=\"USAddress\"/>\n"
        + "      <xsd:element ref=\"comment\" minOccurs=\"0\"/>\n"
        + "      <xsd:element name=\"items\"  type=\"Items\"/>\n"
        + "    </xsd:sequence>\n"
        + "    <xsd:attribute name=\"orderDate\" type=\"xsd:date\"/>\n"
        + "  </xsd:complexType>\n"
        + "\n"
        + "  <xsd:complexType name=\"USAddress\">\n"
        + "    <xsd:sequence>\n"
        + "      <xsd:element name=\"name\"   type=\"xsd:string\"/>\n"
        + "      <xsd:element name=\"street\" type=\"xsd:string\"/>\n"
        + "      <xsd:element name=\"city\"   type=\"xsd:string\"/>\n"
        + "      <xsd:element name=\"state\"  type=\"xsd:string\"/>\n"
        + "      <xsd:element name=\"zip\"    type=\"xsd:decimal\"/>\n"
        + "    </xsd:sequence>\n"
        + "    <xsd:attribute name=\"country\" type=\"xsd:NMTOKEN\"\n"
        + "                   fixed=\"US\"/>\n"
        + "  </xsd:complexType>\n"
        + "\n"
        + "  <xsd:complexType name=\"Items\">\n"
        + "    <xsd:sequence>\n"
        + "      <xsd:element name=\"item\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n"
        + "        <xsd:complexType>\n"
        + "          <xsd:sequence>\n"
        + "            <xsd:element name=\"productName\" type=\"xsd:string\"/>\n"
        + "            <xsd:element name=\"quantity\">\n"
        + "              <xsd:simpleType>\n"
        + "                <xsd:restriction base=\"xsd:positiveInteger\">\n"
        + "                  <xsd:maxExclusive value=\"100\"/>\n"
        + "                </xsd:restriction>\n"
        + "              </xsd:simpleType>\n"
        + "            </xsd:element>\n"
        + "            <xsd:element name=\"USPrice\"  type=\"xsd:decimal\"/>\n"
        + "            <xsd:element ref=\"comment\"   minOccurs=\"0\"/>\n"
        + "            <xsd:element name=\"shipDate\" type=\"xsd:date\" minOccurs=\"0\"/>\n"
        + "          </xsd:sequence>\n"
        + "          <xsd:attribute name=\"partNum\" type=\"SKU\" use=\"required\"/>\n"
        + "        </xsd:complexType>\n" + "      </xsd:element>\n"
        + "    </xsd:sequence>\n" + "  </xsd:complexType>\n" + "\n"
        + "  <!-- Stock Keeping Unit, a code for identifying products -->\n"
        + "  <xsd:simpleType name=\"SKU\">\n"
        + "    <xsd:restriction base=\"xsd:string\">\n"
        + "      <xsd:pattern value=\"\\d{3}-[A-Z]{2}\"/>\n"
        + "    </xsd:restriction>\n" + "  </xsd:simpleType>\n" + "\n"
        + "</xsd:schema>");
  }

  private void assertParseInvariant(String aString) {
    XmlNode tree = parser.parse(new Context(aString)).get();
    assertEquals(tree, parser.parse(new Context(tree.toXmlString())).get());
    assertInvariants(tree);
  }

  private void assertInvariants(XmlNode anXmlNode) {
    assertEquivalentInvariant(anXmlNode);
    assertDocumentInvariant(anXmlNode);
    assertParentInvariant(anXmlNode);
    assertForwardInvariant(anXmlNode);
    assertBackwardInvariant(anXmlNode);
    assertNameInvariant(anXmlNode);
    assertAttributeInvariant(anXmlNode);
  }

  private void assertEquivalentInvariant(XmlNode anXmlNode) {
    for (XmlNode node : anXmlNode) {
      assertEquals(node, node);
      assertEquals(node.hashCode(), node.hashCode());
      assertFalse(node.equals(null));
      assertFalse(node.equals(node.getParent()));
      for (XmlNode child : node.getChildren()) {
        assertFalse(node.equals(child));
      }
      for (XmlNode child : node.getAttributes()) {
        assertFalse(node.equals(child));
      }
    }
  }

  private void assertDocumentInvariant(XmlNode anXmlNode) {
    XmlNode root = anXmlNode.getRoot();
    for (XmlNode child : anXmlNode) {
      assertSame(child.getRoot(), root);
      assertSame(child.getDocument(), root);
    }
    XmlDocument document = (XmlDocument) anXmlNode;
    assertTrue(document.getChildren().contains(document.getRootElement()));
  }

  private void assertParentInvariant(XmlNode anXmlNode) {
    for (XmlNode node : anXmlNode) {
      if (node instanceof XmlDocument) {
        assertNull(node.getParent());
      }
      for (XmlNode child : node.getChildren()) {
        assertSame(node, child.getParent());
      }
      for (XmlNode attribute : node.getAttributes()) {
        assertSame(node, attribute.getParent());
      }
    }
  }

  private void assertForwardInvariant(XmlNode anXmlNode) {
    for (XmlNode node : anXmlNode) {
      XmlNode current = node.getFirstChild();
      List<XmlNode> children = Lists.newArrayList(node.getChildren());
      while (current != null) {
        assertSame(current, children.remove(0));
        current = current.getNextSibling();
      }
      assertTrue(children.isEmpty());
    }
  }

  private void assertBackwardInvariant(XmlNode anXmlNode) {
    for (XmlNode node : anXmlNode) {
      XmlNode current = node.getLastChild();
      List<XmlNode> children = Lists.newArrayList(node.getChildren());
      while (current != null) {
        assertSame(current, children.remove(children.size() - 1));
        current = current.getPreviousSibling();
      }
      assertTrue(children.isEmpty());
    }
  }

  private void assertNameInvariant(XmlNode anXmlNode) {
    for (XmlNode node : anXmlNode) {
      if (node instanceof XmlElement) {
        XmlElement element = (XmlElement) node;
        assertNameInvariant(element.getName());
      }
      if (node instanceof XmlAttribute) {
        XmlAttribute attribute = (XmlAttribute) node;
        assertNameInvariant(attribute.getName());
      }
    }
  }

  private void assertNameInvariant(XmlName anXmlName) {
    assertFalse(anXmlName.getLocal().isEmpty());
    assertTrue(anXmlName.getQualified().endsWith(anXmlName.getLocal()));
    if (anXmlName.getPrefix() != null) {
      assertFalse(anXmlName.getPrefix().isEmpty());
      assertTrue(anXmlName.getQualified().startsWith(anXmlName.getPrefix()));
      assertEquals(anXmlName.getPrefix().length(), anXmlName.getQualified().indexOf(':'));
    }
    assertEquals(anXmlName.getQualified(), anXmlName.toXmlString());
    assertNotNull(anXmlName.toString());
  }

  private void assertAttributeInvariant(XmlNode anXmlNode) {
    for (XmlNode node : anXmlNode) {
      if (node instanceof XmlElement) {
        XmlElement element = (XmlElement) node;
        for (XmlAttribute attribute : element.getAttributes()) {
          assertEquals(element.getAttribute(attribute.getName().getLocal()), attribute.getValue());
          assertSame(element.getAttributeNode(attribute.getName().getLocal()), attribute);
        }
        if (element.getAttributes().isEmpty()) {
          assertNull(element.getAttribute("foo"));
          assertNull(element.getAttributeNode("foo"));
        }
      }
    }
  }

}
