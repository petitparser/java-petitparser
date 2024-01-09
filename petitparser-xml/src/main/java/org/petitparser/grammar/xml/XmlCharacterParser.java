package org.petitparser.grammar.xml;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.CharacterParser.pattern;
import static org.petitparser.parser.primitive.CharacterParser.word;

/**
 * Optimized parser to read character data.
 */
public class XmlCharacterParser extends Parser {

  // character mapping
  @SuppressWarnings("DoubleBraceInitialization")
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
        put("Epsilon", 'Ε');
        put("epsilon", 'ε');
        put("equiv", '≡');
        put("Eta", 'Η');
        put("eta", 'η');
        put("ETH", 'Ð');
        put("eth", 'ð');
        put("Euml", 'Ë');
        put("euml", 'ë');
        put("euro", '€');
        put("exist", '∃');
        put("fnof", 'ƒ');
        put("forall", '∀');
        put("frac12", '½');
        put("frac14", '¼');
        put("frac34", '¾');
        put("frasl", '⁄');
        put("Gamma", 'Γ');
        put("gamma", 'γ');
        put("ge", '≥');
        put("harr", '↔');
        put("hArr", '⇔');
        put("hearts", '♥');
        put("hellip", '…');
        put("Iacute", 'Í');
        put("iacute", 'í');
        put("Icirc", 'Î');
        put("icirc", 'î');
        put("iexcl", '¡');
        put("Igrave", 'Ì');
        put("igrave", 'ì');
        put("image", 'ℑ');
        put("infin", '∞');
        put("int", '∫');
        put("Iota", 'Ι');
        put("iota", 'ι');
        put("iquest", '¿');
        put("isin", '∈');
        put("Iuml", 'Ï');
        put("iuml", 'ï');
        put("Kappa", 'Κ');
        put("kappa", 'κ');
        put("Lambda", 'Λ');
        put("lambda", 'λ');
        put("lang", '〈');
        put("laquo", '«');
        put("larr", '←');
        put("lArr", '⇐');
        put("lceil", '⌈');
        put("ldquo", '“');
        put("le", '≤');
        put("lfloor", '⌊');
        put("lowast", '∗');
        put("loz", '◊');
        put("lrm", '\u200E');
        put("lsaquo", '‹');
        put("lsquo", '‘');
        put("macr", '¯');
        put("mdash", '—');
        put("micro", 'µ');
        put("middot", '·');
        put("minus", '−');
        put("Mu", 'Μ');
        put("mu", 'μ');
        put("nabla", '∇');
        put("nbsp", '\u00A0');
        put("ndash", '–');
        put("ne", '≠');
        put("ni", '∋');
        put("not", '¬');
        put("notin", '∉');
        put("nsub", '⊄');
        put("Ntilde", 'Ñ');
        put("ntilde", 'ñ');
        put("Nu", 'Ν');
        put("nu", 'ν');
        put("Oacute", 'Ó');
        put("oacute", 'ó');
        put("Ocirc", 'Ô');
        put("ocirc", 'ô');
        put("OElig", 'Œ');
        put("oelig", 'œ');
        put("Ograve", 'Ò');
        put("ograve", 'ò');
        put("oline", '‾');
        put("Omega", 'Ω');
        put("omega", 'ω');
        put("Omicron", 'Ο');
        put("omicron", 'ο');
        put("oplus", '⊕');
        put("or", '∨');
        put("ordf", 'ª');
        put("ordm", 'º');
        put("Oslash", 'Ø');
        put("oslash", 'ø');
        put("Otilde", 'Õ');
        put("otilde", 'õ');
        put("otimes", '⊗');
        put("Ouml", 'Ö');
        put("ouml", 'ö');
        put("para", '¶');
        put("part", '∂');
        put("permil", '‰');
        put("perp", '⊥');
        put("Phi", 'Φ');
        put("phi", 'φ');
        put("Pi", 'Π');
        put("pi", 'π');
        put("piv", 'ϖ');
        put("plusmn", '±');
        put("pound", '£');
        put("prime", '′');
        put("Prime", '″');
        put("prod", '∏');
        put("prop", '∝');
        put("Psi", 'Ψ');
        put("psi", 'ψ');
        put("radic", '√');
        put("rang", '〉');
        put("raquo", '»');
        put("rarr", '→');
        put("rArr", '⇒');
        put("rceil", '⌉');
        put("rdquo", '”');
        put("real", 'ℜ');
        put("reg", '®');
        put("rfloor", '⌋');
        put("Rho", 'Ρ');
        put("rho", 'ρ');
        put("rlm", '\u200F');
        put("rsaquo", '›');
        put("rsquo", '’');
        put("sbquo", '‚');
        put("Scaron", 'Š');
        put("scaron", 'š');
        put("sdot", '⋅');
        put("sect", '§');
        put("shy", '\u00AD');
        put("Sigma", 'Σ');
        put("sigma", 'σ');
        put("sigmaf", 'ς');
        put("sim", '∼');
        put("spades", '♠');
        put("sub", '⊂');
        put("sube", '⊆');
        put("sum", '∑');
        put("sup", '⊃');
        put("sup1", '¹');
        put("sup2", '²');
        put("sup3", '³');
        put("supe", '⊇');
        put("szlig", 'ß');
        put("Tau", 'Τ');
        put("tau", 'τ');
        put("there4", '∴');
        put("Theta", 'Θ');
        put("theta", 'θ');
        put("thetasym", 'ϑ');
        put("thinsp", '\u2009');
        put("THORN", 'Þ');
        put("thorn", 'þ');
        put("tilde", '˜');
        put("times", '×');
        put("trade", '™');
        put("Uacute", 'Ú');
        put("uacute", 'ú');
        put("uarr", '↑');
        put("uArr", '⇑');
        put("Ucirc", 'Û');
        put("ucirc", 'û');
        put("Ugrave", 'Ù');
        put("ugrave", 'ù');
        put("uml", '¨');
        put("upsih", 'ϒ');
        put("Upsilon", 'Υ');
        put("upsilon", 'υ');
        put("Uuml", 'Ü');
        put("uuml", 'ü');
        put("weierp", '℘');
        put("Xi", 'Ξ');
        put("xi", 'ξ');
        put("Yacute", 'Ý');
        put("yacute", 'ý');
        put("yen", '¥');
        put("yuml", 'ÿ');
        put("Yuml", 'Ÿ');
        put("Zeta", 'Ζ');
        put("zeta", 'ζ');
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
