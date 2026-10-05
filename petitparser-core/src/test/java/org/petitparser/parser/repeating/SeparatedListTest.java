package org.petitparser.parser.repeating;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SeparatedList}.
 */
public class SeparatedListTest {

  @Test
  public void testEmpty() {
    SeparatedList<String, String> list = new SeparatedList<>();
    assertTrue(list.getElements().isEmpty());
    assertTrue(list.getSeparators().isEmpty());
    assertTrue(list.getSequentialList().isEmpty());
    assertSame(list, list.getSequential());

    Iterator<Object> iterator = list.iterator();
    assertFalse(iterator.hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void testIteratorExhausted() {
    SeparatedList<String, String> list = new SeparatedList<>();
    list.iterator().next();
  }

  @Test
  public void testSingleElement() {
    SeparatedList<Integer, String> list = new SeparatedList<>(Collections.singletonList(42));
    assertEquals(Collections.singletonList(42), list.getElements());
    assertTrue(list.getSeparators().isEmpty());
    assertEquals(Collections.singletonList(42), list.getSequentialList());

    List<Object> iterated = new ArrayList<>();
    for (Object item : list.getSequential()) {
      iterated.add(item);
    }
    assertEquals(Collections.singletonList(42), iterated);
  }

  @Test
  public void testInterleaved() {
    SeparatedList<Integer, String> list = new SeparatedList<>(
        Arrays.asList(1, 2, 3),
        Arrays.asList("+", "*"));
    assertEquals(Arrays.asList(1, 2, 3), list.getElements());
    assertEquals(Arrays.asList("+", "*"), list.getSeparators());

    List<Object> expected = Arrays.asList(1, "+", 2, "*", 3);
    assertEquals(expected, list.getSequentialList());

    List<Object> iterated = new ArrayList<>();
    for (Object item : list) {
      iterated.add(item);
    }
    assertEquals(expected, iterated);
  }

  @Test
  public void testImmutability() {
    List<Integer> elements = new ArrayList<>(Arrays.asList(1, 2));
    List<String> separators = new ArrayList<>(Collections.singletonList("+"));
    SeparatedList<Integer, String> list = new SeparatedList<>(elements, separators);

    elements.add(3);
    separators.add("*");
    assertEquals(2, list.getElements().size());
    assertEquals(1, list.getSeparators().size());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testElementsUnmodifiable() {
    SeparatedList<Integer, String> list = new SeparatedList<>(Collections.singletonList(1));
    list.getElements().add(2);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSeparatorsUnmodifiable() {
    SeparatedList<Integer, String> list = new SeparatedList<>(
        Arrays.asList(1, 2), Collections.singletonList("+"));
    list.getSeparators().add("-");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSequentialListUnmodifiable() {
    SeparatedList<Integer, String> list = new SeparatedList<>(Collections.singletonList(1));
    list.getSequentialList().add(2);
  }

  @Test(expected = NullPointerException.class)
  public void testNullElements() {
    new SeparatedList<String, String>(null, Collections.emptyList());
  }

  @Test(expected = NullPointerException.class)
  public void testNullSeparators() {
    new SeparatedList<String, String>(Collections.emptyList(), null);
  }

  @Test
  public void testFoldSingle() {
    SeparatedList<Integer, String> list = new SeparatedList<>(Collections.singletonList(42));
    assertEquals(Integer.valueOf(42), list.foldLeft((a, sep, b) -> a + b));
    assertEquals(Integer.valueOf(42), list.foldRight((a, sep, b) -> a + b));
  }

  @Test
  public void testFoldLeft() {
    // ((10 - 3) - 2) = 5
    SeparatedList<Integer, String> list = new SeparatedList<>(
        Arrays.asList(10, 3, 2),
        Arrays.asList("-", "-"));
    int result = list.foldLeft((a, sep, b) -> a - b);
    assertEquals(5, result);
  }

  @Test
  public void testFoldRight() {
    // (10 - (3 - 2)) = 9
    SeparatedList<Integer, String> list = new SeparatedList<>(
        Arrays.asList(10, 3, 2),
        Arrays.asList("-", "-"));
    int result = list.foldRight((a, sep, b) -> a - b);
    assertEquals(9, result);
  }

  @Test(expected = NoSuchElementException.class)
  public void testFoldLeftEmptyThrows() {
    new SeparatedList<Integer, String>().foldLeft((a, sep, b) -> a + b);
  }

  @Test(expected = NoSuchElementException.class)
  public void testFoldRightEmptyThrows() {
    new SeparatedList<Integer, String>().foldRight((a, sep, b) -> a + b);
  }

  @Test(expected = NullPointerException.class)
  public void testFoldLeftNullCallback() {
    new SeparatedList<>(Collections.singletonList(1)).foldLeft(null);
  }

  @Test(expected = NullPointerException.class)
  public void testFoldRightNullCallback() {
    new SeparatedList<>(Collections.singletonList(1)).foldRight(null);
  }

  @Test
  public void testEqualsAndHashCode() {
    SeparatedList<Integer, String> list1 = new SeparatedList<>(
        Arrays.asList(1, 2), Collections.singletonList(","));
    SeparatedList<Integer, String> list2 = new SeparatedList<>(
        Arrays.asList(1, 2), Collections.singletonList(","));
    SeparatedList<Integer, String> diffElements = new SeparatedList<>(
        Arrays.asList(1, 3), Collections.singletonList(","));
    SeparatedList<Integer, String> diffSeparators = new SeparatedList<>(
        Arrays.asList(1, 2), Collections.singletonList(";"));

    assertEquals(list1, list1);
    assertEquals(list1, list2);
    assertEquals(list2, list1);
    assertEquals(list1.hashCode(), list2.hashCode());

    assertNotEquals(list1, diffElements);
    assertNotEquals(list1, diffSeparators);
    assertNotEquals(list1, null);
    assertNotEquals(list1, "other");
  }

  @Test
  public void testToString() {
    assertEquals("SeparatedList()", new SeparatedList<>().toString());
    assertEquals("SeparatedList(1)",
        new SeparatedList<>(Collections.singletonList(1)).toString());
    assertEquals("SeparatedList(1, +, 2, *, 3)",
        new SeparatedList<>(Arrays.asList(1, 2, 3), Arrays.asList("+", "*")).toString());
  }
}
