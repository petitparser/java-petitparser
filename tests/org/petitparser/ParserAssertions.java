package org.petitparser;

public class ParserAssertions {

  private ParserAssertions() {

  }

  public static <T> void assertParse(Parser<T> parser, String input, T result,
      int position) {


  }

  public static <T> void assertFail(Parser<T> parser, String input) {

  }

}
