package org.petitparser.utils.functions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Function3} through {@link Function9}.
 */
public class FunctionsTest {

  @Test
  public void testFunction3() {
    Function3<String, String, String, String> f = (a, b, c) -> a + b + c;
    assertEquals("abc", f.apply("a", "b", "c"));
    assertEquals("ABC", f.andThen(String::toUpperCase).apply("a", "b", "c"));
  }

  @Test(expected = NullPointerException.class)
  public void testFunction3NullAfter() {
    Function3<String, String, String, String> f = (a, b, c) -> a + b + c;
    f.andThen(null);
  }

  @Test
  public void testFunction4() {
    Function4<String, String, String, String, String> f = (a, b, c, d) -> a + b + c + d;
    assertEquals("abcd", f.apply("a", "b", "c", "d"));
    assertEquals("ABCD", f.andThen(String::toUpperCase).apply("a", "b", "c", "d"));
  }

  @Test(expected = NullPointerException.class)
  public void testFunction4NullAfter() {
    Function4<String, String, String, String, String> f = (a, b, c, d) -> a + b + c + d;
    f.andThen(null);
  }

  @Test
  public void testFunction5() {
    Function5<String, String, String, String, String, String> f =
        (a, b, c, d, e) -> a + b + c + d + e;
    assertEquals("abcde", f.apply("a", "b", "c", "d", "e"));
    assertEquals("ABCDE", f.andThen(String::toUpperCase).apply("a", "b", "c", "d", "e"));
  }

  @Test(expected = NullPointerException.class)
  public void testFunction5NullAfter() {
    Function5<String, String, String, String, String, String> f =
        (a, b, c, d, e) -> a + b + c + d + e;
    f.andThen(null);
  }

  @Test
  public void testFunction6() {
    Function6<String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g) -> a + b + c + d + e + g;
    assertEquals("abcdef", f.apply("a", "b", "c", "d", "e", "f"));
    assertEquals("ABCDEF", f.andThen(String::toUpperCase).apply("a", "b", "c", "d", "e", "f"));
  }

  @Test(expected = NullPointerException.class)
  public void testFunction6NullAfter() {
    Function6<String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g) -> a + b + c + d + e + g;
    f.andThen(null);
  }

  @Test
  public void testFunction7() {
    Function7<String, String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g, h) -> a + b + c + d + e + g + h;
    assertEquals("abcdefg", f.apply("a", "b", "c", "d", "e", "f", "g"));
    assertEquals("ABCDEFG", f.andThen(String::toUpperCase).apply("a", "b", "c", "d", "e", "f", "g"));
  }

  @Test(expected = NullPointerException.class)
  public void testFunction7NullAfter() {
    Function7<String, String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g, h) -> a + b + c + d + e + g + h;
    f.andThen(null);
  }

  @Test
  public void testFunction8() {
    Function8<String, String, String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g, h, i) -> a + b + c + d + e + g + h + i;
    assertEquals("abcdefgh", f.apply("a", "b", "c", "d", "e", "f", "g", "h"));
    assertEquals("ABCDEFGH", f.andThen(String::toUpperCase).apply("a", "b", "c", "d", "e", "f", "g", "h"));
  }

  @Test(expected = NullPointerException.class)
  public void testFunction8NullAfter() {
    Function8<String, String, String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g, h, i) -> a + b + c + d + e + g + h + i;
    f.andThen(null);
  }

  @Test
  public void testFunction9() {
    Function9<String, String, String, String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g, h, i, j) -> a + b + c + d + e + g + h + i + j;
    assertEquals("abcdefghi", f.apply("a", "b", "c", "d", "e", "f", "g", "h", "i"));
    assertEquals("ABCDEFGHI", f.andThen(String::toUpperCase).apply("a", "b", "c", "d", "e", "f", "g", "h", "i"));
  }

  @Test(expected = NullPointerException.class)
  public void testFunction9NullAfter() {
    Function9<String, String, String, String, String, String, String, String, String, String> f =
        (a, b, c, d, e, g, h, i, j) -> a + b + c + d + e + g + h + i + j;
    f.andThen(null);
  }
}
