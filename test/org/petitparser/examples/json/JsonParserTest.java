package org.petitparser.examples.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

/**
 * Tests {@link JsonParser}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class JsonParserTest {

  private final Parser parser = new JsonParser();

  // arrays

  @Test
  public void testEmptyArray() {
    List<String> result = parse("[]").get();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSmallArray() {
    List<String> result = parse("[\"a\"]").get();
    assertEquals(1, result.size());
    assertEquals("a", result.get(0));
  }

  @Test
  public void testBigArray() {
    List<String> result = parse(" [ \"a\" , \"b\" ] ").get();
    assertEquals(2, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b", result.get(1));
  }

  @Test
  public void testNestedArray() {
    List<List<String>> result = parse("[[\"a\"]]").get();
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).size());
    assertEquals("a", result.get(0).get(0));
  }

  // objects

  @Test
  public void testEmptyObject() {
    Map<String, Double> result = parse("{}").get();
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSmallObject() {
    Map<String, Double> result = parse("{\"a\":1}").get();
    assertEquals(1, result.size());
    assertEquals(Double.valueOf(1.0), result.get("a"));
  }

  @Test
  public void testBigObject() {
    Map<String, Double> result = parse(" { \"a\" : 1 , \"b\" : 2 } ").get();
    assertEquals(2, result.size());
    assertEquals(Double.valueOf(1.0), result.get("a"));
    assertEquals(Double.valueOf(2.0), result.get("b"));
  }

  @Test
  public void testNestedObject() {
    Map<String, Map<String, Double>> result = parse("{\"object\":{\"1\": 2}}").get();
    assertEquals(1, result.size());
    assertEquals(1, result.get("object").size());
    assertEquals(Double.valueOf(2.0), result.get("object").get("1"));
  }

  // literals

  @Test
  public void testTrue() {
    Boolean result = parse("true").get();
    assertTrue(result);
  }

  @Test
  public void testFalse() {
    Boolean result = parse("false").get();
    assertFalse(result);
  }

  @Test
  public void testNull() {
    Object result = parse("null").get();
    assertNull(result);
  }

  @Test
  public void testFloat() {
    assertEquals(0.0, parse("0.0").get());
    assertEquals(0.12, parse("0.12").get());
    assertEquals(-0.12, parse("-0.12").get());
    assertEquals(12.34, parse("12.34").get());
    assertEquals(-12.34, parse("-12.34").get());
    assertEquals(1.2, parse("1.2e0").get());
    assertEquals(1.2e-1, parse("1.2e-1").get());
    assertEquals(1.2e-1, parse("1.2E-1").get());
  }

  @Test
  public void testInteger() {
    assertEquals(0.0, parse("0").get());
    assertEquals(1.0, parse("1").get());
    assertEquals(-1.0, parse("-1").get());
    assertEquals(12.0, parse("12").get());
    assertEquals(-12.0, parse("-12").get());
    assertEquals(100.0, parse("1e2").get());
    assertEquals(100.0, parse("1e+2").get());
  }

  @Test
  public void testString() {
    assertEquals("", parse("\"\"").get());
    assertEquals("foo", parse("\"foo\"").get());
    assertEquals("foo bar", parse("\"foo bar\"").get());
  }

  @Test
  public void testStringEscaped() {
    assertEquals("\"", parse("\"\\\"\"").get());
    assertEquals("\\", parse("\"\\\\\"").get());
    assertEquals("/", parse("\"\\/\"").get());
    assertEquals("\b", parse("\"\\b\"").get());
    assertEquals("\f", parse("\"\\f\"").get());
    assertEquals("\n", parse("\"\\n\"").get());
    assertEquals("\r", parse("\"\\r\"").get());
    assertEquals("\t", parse("\"\\t\"").get());
    assertEquals("\u20Ac", parse("\"\\u20Ac\"").get());
  }

  @Test
  public void testExplorerEvent() {
    Result result = parse("{\"recordset\": null, \"type\": \"change\", \"fromElement\": null, \"toElement\": null, \"altLeft\": false, \"keyCode\": 0, \"repeat\": false, \"reason\": 0, \"behaviorCookie\": 0, \"contentOverflow\": false, \"behaviorPart\": 0, \"dataTransfer\": null, \"ctrlKey\": false, \"shiftLeft\": false, \"dataFld\": \"\", \"qualifier\": \"\", \"wheelDelta\": 0, \"bookmarks\": null, \"button\": 0, \"srcFilter\": null, \"nextPage\": \"\", \"cancelBubble\": false, \"x\": 89, \"y\": 502, \"screenX\": 231, \"screenY\": 1694, \"srcUrn\": \"\", \"boundElements\": {\"length\": 0}, \"clientX\": 89, \"clientY\": 502, \"propertyName\": \"\", \"shiftKey\": false, \"ctrlLeft\": false, \"offsetX\": 25, \"offsetY\": 2, \"altKey\": false}");
    assertTrue(result.isSuccess());
  }

  @Test
  public void testFirefoxEvent() {
    Result result = parse("{\"type\": \"change\", \"eventPhase\": 2, \"bubbles\": true, \"cancelable\": true, \"timeStamp\": 0, \"CAPTURING_PHASE\": 1, \"AT_TARGET\": 2, \"BUBBLING_PHASE\": 3, \"isTrusted\": true, \"MOUSEDOWN\": 1, \"MOUSEUP\": 2, \"MOUSEOVER\": 4, \"MOUSEOUT\": 8, \"MOUSEMOVE\": 16, \"MOUSEDRAG\": 32, \"CLICK\": 64, \"DBLCLICK\": 128, \"KEYDOWN\": 256, \"KEYUP\": 512, \"KEYPRESS\": 1024, \"DRAGDROP\": 2048, \"FOCUS\": 4096, \"BLUR\": 8192, \"SELECT\": 16384, \"CHANGE\": 32768, \"RESET\": 65536, \"SUBMIT\": 131072, \"SCROLL\": 262144, \"LOAD\": 524288, \"UNLOAD\": 1048576, \"XFER_DONE\": 2097152, \"ABORT\": 4194304, \"ERROR\": 8388608, \"LOCATE\": 16777216, \"MOVE\": 33554432, \"RESIZE\": 67108864, \"FORWARD\": 134217728, \"HELP\": 268435456, \"BACK\": 536870912, \"TEXT\": 1073741824, \"ALT_MASK\": 1, \"CONTROL_MASK\": 2, \"SHIFT_MASK\": 4, \"META_MASK\": 8}");
    assertTrue(result.isSuccess());
  }

  @Test
  public void testWebkitEvent() {
    Result result = parse("{\"returnValue\": true, \"timeStamp\": 1226697417289, \"eventPhase\": 2, \"type\": \"change\", \"cancelable\": false, \"bubbles\": true, \"cancelBubble\": false, \"MOUSEOUT\": 8, \"FOCUS\": 4096, \"CHANGE\": 32768, \"MOUSEMOVE\": 16, \"AT_TARGET\": 2, \"SELECT\": 16384, \"BLUR\": 8192, \"KEYUP\": 512, \"MOUSEDOWN\": 1, \"MOUSEDRAG\": 32, \"BUBBLING_PHASE\": 3, \"MOUSEUP\": 2, \"CAPTURING_PHASE\": 1, \"MOUSEOVER\": 4, \"CLICK\": 64, \"DBLCLICK\": 128, \"KEYDOWN\": 256, \"KEYPRESS\": 1024, \"DRAGDROP\": 2048}");
    assertTrue(result.isSuccess());
  }


  private Result parse(String aString) {
    return parser.parse(new Context(aString));
  }

}
