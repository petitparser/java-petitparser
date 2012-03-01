package org.petitparser;


/**
 * Parses a sequence of characters.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class StringParser extends AbstractParser<String> {

  final String string;

  public StringParser(String string) {
    this.string = string;
  }

  @Override
  public boolean parse(Context context) {
    int position = context.position;
    //if (context.match(string)) {
    //  context.result = string;
    //  return true;
    //} else {
    //  context.position = position;
    //  context.message = string + " expected";
    //  return false;
   // }
    return false;
  }

}
