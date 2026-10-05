package org.petitparser.parser.primitive;

/**
 * Character predicate returning a constant boolean value.
 */
public class ConstantCharPredicate implements CharacterPredicate {

  public static final ConstantCharPredicate ANY = new ConstantCharPredicate(true);
  public static final ConstantCharPredicate NONE = new ConstantCharPredicate(false);

  public static ConstantCharPredicate any() {
    return ANY;
  }

  public static ConstantCharPredicate none() {
    return NONE;
  }

  private final boolean result;

  public ConstantCharPredicate(boolean result) {
    this.result = result;
  }

  public boolean getResult() {
    return result;
  }

  @Override
  public boolean test(char value) {
    return result;
  }

  @Override
  public boolean test(int value) {
    return result;
  }

  @Override
  public CharacterPredicate not() {
    return result ? NONE : ANY;
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof ConstantCharPredicate &&
        result == ((ConstantCharPredicate) other).result;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CharacterPredicate &&
        isEqualTo((CharacterPredicate) other);
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(result);
  }

  @Override
  public String toString() {
    return "ConstantCharPredicate[" + result + "]";
  }
}
