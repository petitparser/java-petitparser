package org.petitparser.grammar.xml;

import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.CharacterParser.pattern;
import static org.petitparser.parser.primitive.CharacterParser.word;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Optimized parser to read character data.
 */
public class XmlCharacterParser extends Parser {

  // character mapping
  private static final Map<String, Character> NAME_TO_CHAR =
      new HashMap<String, Character>() {{

        // xml entities
        put("lt", '<');
        put("gt", '>');
        put("amp", '&');
        put("apos", '\'');
        put("quot", '"');

        // html entities
        put("Aacute", '\u00C1');
        put("aacute", '\u00E1');
        put("Acirc", '\u00C2');
        put("acirc", '\u00E2');
        put("acute", '\u00B4');
        put("AElig", '\u00C6');
        put("aelig", '\u00E6');
        put("Agrave", '\u00C0');
        put("agrave", '\u00E0');
        put("alefsym", '\u2135');
        put("Alpha", '\u0391');
        put("alpha", '\u03B1');
        put("and", '\u2227');
        put("ang", '\u2220');
        put("Aring", '\u00C5');
        put("aring", '\u00E5');
        put("asymp", '\u2248');
        put("Atilde", '\u00C3');
        put("atilde", '\u00E3');
        put("Auml", '\u00C4');
        put("auml", '\u00E4');
        put("bdquo", '\u201E');
        put("Beta", '\u0392');
        put("beta", '\u03B2');
        put("brvbar", '\u00A6');
        put("bull", '\u2022');
        put("cap", '\u2229');
        put("Ccedil", '\u00C7');
        put("ccedil", '\u00E7');
        put("cedil", '\u00B8');
        put("cent", '\u00A2');
        put("Chi", '\u03A7');
        put("chi", '\u03C7');
        put("circ", '\u02C6');
        put("clubs", '\u2663');
        put("cong", '\u2245');
        put("copy", '\u00A9');
        put("crarr", '\u21B5');
        put("cup", '\u222A');
        put("curren", '\u00A4');
        put("dagger", '\u2020');
        put("Dagger", '\u2021');
        put("darr", '\u2193');
        put("dArr", '\u21D3');
        put("deg", '\u00B0');
        put("Delta", '\u0394');
        put("delta", '\u03B4');
        put("diams", '\u2666');
        put("divide", '\u00F7');
        put("Eacute", '\u00C9');
        put("eacute", '\u00E9');
        put("Ecirc", '\u00CA');
        put("ecirc", '\u00EA');
        put("Egrave", '\u00C8');
        put("egrave", '\u00E8');
        put("empty", '\u2205');
        put("emsp", '\u2003');
        put("ensp", '\u2002');
        put("Epsilon", '\u0395');
        put("epsilon", '\u03B5');
        put("equiv", '\u2261');
        put("Eta", '\u0397');
        put("eta", '\u03B7');
        put("ETH", '\u00D0');
        put("eth", '\u00F0');
        put("Euml", '\u00CB');
        put("euml", '\u00EB');
        put("euro", '\u20AC');
        put("exist", '\u2203');
        put("fnof", '\u0192');
        put("forall", '\u2200');
        put("frac12", '\u00BD');
        put("frac14", '\u00BC');
        put("frac34", '\u00BE');
        put("frasl", '\u2044');
        put("Gamma", '\u0393');
        put("gamma", '\u03B3');
        put("ge", '\u2265');
        put("harr", '\u2194');
        put("hArr", '\u21D4');
        put("hearts", '\u2665');
        put("hellip", '\u2026');
        put("Iacute", '\u00CD');
        put("iacute", '\u00ED');
        put("Icirc", '\u00CE');
        put("icirc", '\u00EE');
        put("iexcl", '\u00A1');
        put("Igrave", '\u00CC');
        put("igrave", '\u00EC');
        put("image", '\u2111');
        put("infin", '\u221E');
        put("int", '\u222B');
        put("Iota", '\u0399');
        put("iota", '\u03B9');
        put("iquest", '\u00BF');
        put("isin", '\u2208');
        put("Iuml", '\u00CF');
        put("iuml", '\u00EF');
        put("Kappa", '\u039A');
        put("kappa", '\u03BA');
        put("Lambda", '\u039B');
        put("lambda", '\u03BB');
        put("lang", '\u2329');
        put("laquo", '\u00AB');
        put("larr", '\u2190');
        put("lArr", '\u21D0');
        put("lceil", '\u2308');
        put("ldquo", '\u201C');
        put("le", '\u2264');
        put("lfloor", '\u230A');
        put("lowast", '\u2217');
        put("loz", '\u25CA');
        put("lrm", '\u200E');
        put("lsaquo", '\u2039');
        put("lsquo", '\u2018');
        put("macr", '\u00AF');
        put("mdash", '\u2014');
        put("micro", '\u00B5');
        put("middot", '\u00B7');
        put("minus", '\u2212');
        put("Mu", '\u039C');
        put("mu", '\u03BC');
        put("nabla", '\u2207');
        put("nbsp", '\u00A0');
        put("ndash", '\u2013');
        put("ne", '\u2260');
        put("ni", '\u220B');
        put("not", '\u00AC');
        put("notin", '\u2209');
        put("nsub", '\u2284');
        put("Ntilde", '\u00D1');
        put("ntilde", '\u00F1');
        put("Nu", '\u039D');
        put("nu", '\u03BD');
        put("Oacute", '\u00D3');
        put("oacute", '\u00F3');
        put("Ocirc", '\u00D4');
        put("ocirc", '\u00F4');
        put("OElig", '\u0152');
        put("oelig", '\u0153');
        put("Ograve", '\u00D2');
        put("ograve", '\u00F2');
        put("oline", '\u203E');
        put("Omega", '\u03A9');
        put("omega", '\u03C9');
        put("Omicron", '\u039F');
        put("omicron", '\u03BF');
        put("oplus", '\u2295');
        put("or", '\u2228');
        put("ordf", '\u00AA');
        put("ordm", '\u00BA');
        put("Oslash", '\u00D8');
        put("oslash", '\u00F8');
        put("Otilde", '\u00D5');
        put("otilde", '\u00F5');
        put("otimes", '\u2297');
        put("Ouml", '\u00D6');
        put("ouml", '\u00F6');
        put("para", '\u00B6');
        put("part", '\u2202');
        put("permil", '\u2030');
        put("perp", '\u22A5');
        put("Phi", '\u03A6');
        put("phi", '\u03C6');
        put("Pi", '\u03A0');
        put("pi", '\u03C0');
        put("piv", '\u03D6');
        put("plusmn", '\u00B1');
        put("pound", '\u00A3');
        put("prime", '\u2032');
        put("Prime", '\u2033');
        put("prod", '\u220F');
        put("prop", '\u221D');
        put("Psi", '\u03A8');
        put("psi", '\u03C8');
        put("radic", '\u221A');
        put("rang", '\u232A');
        put("raquo", '\u00BB');
        put("rarr", '\u2192');
        put("rArr", '\u21D2');
        put("rceil", '\u2309');
        put("rdquo", '\u201D');
        put("real", '\u211C');
        put("reg", '\u00AE');
        put("rfloor", '\u230B');
        put("Rho", '\u03A1');
        put("rho", '\u03C1');
        put("rlm", '\u200F');
        put("rsaquo", '\u203A');
        put("rsquo", '\u2019');
        put("sbquo", '\u201A');
        put("Scaron", '\u0160');
        put("scaron", '\u0161');
        put("sdot", '\u22C5');
        put("sect", '\u00A7');
        put("shy", '\u00AD');
        put("Sigma", '\u03A3');
        put("sigma", '\u03C3');
        put("sigmaf", '\u03C2');
        put("sim", '\u223C');
        put("spades", '\u2660');
        put("sub", '\u2282');
        put("sube", '\u2286');
        put("sum", '\u2211');
        put("sup", '\u2283');
        put("sup1", '\u00B9');
        put("sup2", '\u00B2');
        put("sup3", '\u00B3');
        put("supe", '\u2287');
        put("szlig", '\u00DF');
        put("Tau", '\u03A4');
        put("tau", '\u03C4');
        put("there4", '\u2234');
        put("Theta", '\u0398');
        put("theta", '\u03B8');
        put("thetasym", '\u03D1');
        put("thinsp", '\u2009');
        put("THORN", '\u00DE');
        put("thorn", '\u00FE');
        put("tilde", '\u02DC');
        put("times", '\u00D7');
        put("trade", '\u2122');
        put("Uacute", '\u00DA');
        put("uacute", '\u00FA');
        put("uarr", '\u2191');
        put("uArr", '\u21D1');
        put("Ucirc", '\u00DB');
        put("ucirc", '\u00FB');
        put("Ugrave", '\u00D9');
        put("ugrave", '\u00F9');
        put("uml", '\u00A8');
        put("upsih", '\u03D2');
        put("Upsilon", '\u03A5');
        put("upsilon", '\u03C5');
        put("Uuml", '\u00DC');
        put("uuml", '\u00FC');
        put("weierp", '\u2118');
        put("Xi", '\u039E');
        put("xi", '\u03BE');
        put("Yacute", '\u00DD');
        put("yacute", '\u00FD');
        put("yen", '\u00A5');
        put("yuml", '\u00FF');
        put("Yuml", '\u0178');
        put("Zeta", '\u0396');
        put("zeta", '\u03B6');
        put("zwj", '\u200D');
        put("zwnj", '\u200C');

      }};

  // hexadecimal character reference
  private static final Parser ENTITY_HEX = pattern("xX")
      .seq(pattern("A-Fa-f0-9")
          .plus()
          .flatten("Expected hexadecimal character reference")
          .map((String value) -> (char) Integer.parseInt(value, 16)))
      .pick(1);

  // decimal character reference
  private static final Parser ENTITY_DIGIT = of('#')
      .seq(ENTITY_HEX.or(digit()
          .plus()
          .flatten("Expected decimal character reference")
          .map((String value) -> (char) Integer.parseInt(value))))
      .pick(1);

  // named character reference
  private static final Parser ENTITY = of('&')
      .seq(ENTITY_DIGIT.or(word()
          .plus()
          .flatten("Expected named character reference")
          .map((Function<String, Character>) NAME_TO_CHAR::get)))
      .seq(of(';'))
      .pick(1);

  // Encode a string to be serialized as an XML text node.
  public static String encodeXmlText(String input) {
    // only & and < need to be encoded in text
    return input.replace("&", "&amp;").replace("<", "&lt;");
  }

  // Encode a string to be serialized as an XML attribute value.
  public static String encodeXmlAttributeValue(String input) {
    // only " needs to be encoded in attribute value
    return input.replaceAll("\"", "&quot;");
  }

  private final char stopper;
  private final int minLength;

  XmlCharacterParser(String stopper, int minLength) {
    this(stopper.charAt(0), minLength);
  }

  XmlCharacterParser(char stopper, int minLength) {
    this.stopper = stopper;
    this.minLength = minLength;
  }

  @Override
  public Result parseOn(Context context) {
    String input = context.getBuffer();
    StringBuilder output = new StringBuilder();
    int position = context.getPosition();
    int start = position;

    // scan over the characters as fast as possible
    while (position < input.length()) {
      char value = input.charAt(position);
      if (value == stopper) {
        break;
      } else if (value == '&') {
        Result result = ENTITY.parseOn(context.success(null, position));
        if (result.isSuccess() && result.get() != null) {
          output.append(input, start, position);
          output.append((char) result.get());
          position = result.getPosition();
          start = position;
        } else {
          position++;
        }
      } else {
        position++;
      }
    }
    output.append(input, start, position);

    // check for the minimum length
    return output.length() < minLength
        ? context.failure("Unable to parse character data.")
        : context.success(output.toString(), position);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int start = position;
    int length = buffer.length();
    while (position < length) {
      char value = buffer.charAt(position);
      if (value == stopper) {
        break;
      } else {
        position++;
      }
    }
    return position - start < minLength ? -1 : position;
  }

  @Override
  public List<Parser> getChildren() {
    return Collections.singletonList(ENTITY);
  }

  @Override
  public XmlCharacterParser copy() {
    return new XmlCharacterParser(stopper, minLength);
  }
}
