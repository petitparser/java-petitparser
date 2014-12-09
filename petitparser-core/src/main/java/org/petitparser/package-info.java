/**
 * This package contains the core library of PetitParser, a dynamic parser
 * combinator framework.
 *
 * <h3>Writing a Simple Grammar</h3>
 *
 * Writing grammars with PetitParser is simple as writing Java code. For
 * example, to write a grammar that can parse identifiers that start with
 * a letter followed by zero or more letter or digits is defined as follows:
 *
 * <pre>
 *   import static import org.petitparser.parser.characters.CharacterParser.*;
 *
 *   class Example {
 *     public static void main(String[] arguments) {
 *       Parser id = letter().seq(letter().or(digit()).star());
 *       ...
 *     }
 *   }
 *
 * </pre>
 *
 * If you look at the object {@code id} in the debugger, you'll notice that the
 * code above builds a tree of parser objects:
 *
 * <pre>
 *   Sequence: This parser accepts a sequence of parsers.
 *     Predicate: This parser accepts a single letter.
 *     Repeater: This parser accepts zero or more times another parser.
 *       Choice: This parser accepts a single word character.
 *         Predicate: This parser accepts a single letter.
 *         Predicate: This parser accepts a single digit.
 * </pre>
 *
 * <h3>Parsing Some Input</h3>
 *
 * To actually parse a {@link String} we can use the method {@link Parser#parse}:
 *
 * <pre>
 *     Result id1 = id.parse("yeah");
 *     Result id2 = id.parse("f12");
 * </pre>
 *
 * The method {@link Parser#parse} returns a parse {@link Result}, which is either an
 * instance of {@link Success} or {@link Failure}. In both examples above we are
 * successful and can retrieve the parse result using {@link Success#get}:
 *
 * <pre>
 *     System.out.println(id1.value);      // ['y', ['e', 'a', 'h']]
 *     System.out.println(id2.value);      // ['f', ['1', '2']]
 * </pre>
 *
 * While it seems odd to get these nested arrays with characters as a return
 * value, this is the default decomposition of the input into a parse tree.
 * We'll see in a while how that can be customized.
 *
 * If we try to parse something invalid we get an instance of {@link Failure} as
 * an answer and we can retrieve a descriptive error message using
 * {@link Failure#getMessage}:
 *
 * <pre>
 *     Result id3 = id.parse('123');
 *     print(id3.getMessage());            // "letter expected"
 *     print(id3.getPosition());           // 0
 * </pre>
 *
 * Trying to retrieve the parse result by calling {@link Failure#get} would throw
 * the exception {@link ParserError}. {@link Context#isSuccess} and {@link Context#isFailure}
 * can be used to decide if the parse was successful.
 *
 * If you are only interested if a given string matches or not you can use the
 * helper method {@link Parser#accept}:
 *
 * <pre>
 *     print(id.accept("foo"));            // true
 *     print(id.accept("123"));            // false
 * </pre>
 *
 * <h3>Different Kinds of Parsers</h3>
 *
 * PetitParser provide a large set of ready-made parser that you can compose
 * to consume and transform arbitrarily complex languages. The terminal parsers
 * are the most simple ones. We've already seen a few of those:
 *
 * <ul>
 *   <li> {@code CharacterParser.of('a')} parses the character {@code a}.
 *   <li> {@code string('abc')} parses the string {@code abc}.
 *   <li> {@code any()} parses any character.
 *   <li> {@code digit()} parses any digit from {@code 0} to {@code 9}.
 *   <li> {@code letter()} parses any letter from {@code a} to {@code z} and {@code A} to {@code Z}.
 *   <li> {@code word()} parses any letter or digit.
 * </ul>
 *
 * So instead of using the letter and digit predicate, we could have written
 * our identifier parser like this:
 *
 * <pre>
 *     Parser id = letter().seq(word().star());
 * </pre>
 *
 * The next set of parsers are used to combine other parsers together:
 *
 * <ul>
 *   <li> {@code p1.seq(p2)} parses {@code p1} followed by {@code p2} (sequence).
 *   <li> {@code p1.or(p2)} parses {@code p1}, if that doesn't work parses {@code p2} (ordered choice).
 *   <li> {@code p.star()} parses {@code p} zero or more times.
 *   <li> {@code p.plus()} parses {@code p} one or more times.
 *   <li> {@code p.optional()} parses {@code p}, if possible.
 *   <li> {@code p.and()} parses {@code p}, but does not consume its input.
 *   <li> {@code p.not()} parses {@code p} and succeed when p fails, but does not consume its input.
 *   <li> {@code p.end()} parses {@code p} and succeed at the end of the input.
 * </ul>
 *
 * To attach an action or transformation to a parser we can use the following
 * methods:
 *
 * <ul>
 *   <li> {@code p.map(value -> ...)} performs the transformation given the function.
 *   <li> {@code p.pick(n)} returns the {@code n}-th element of the list {@code p} returns.
 *   <li> {@code p.flatten()} creates a string from the result of {@code p}.
 *   <li> {@code p.token()} creates a token from the result of {@code p}.
 *   <li> {@code p.trim()} trims whitespaces before and after {@code p}.
 * </ul>
 *
 * To return a string of the parsed identifier, we can modify our parser like
 * this:
 *
 * <pre>
 *     Parser id = letter().seq(word().star()).flatten();
 * </pre>
 *
 * To conveniently find all matches in a given input string you can use
 * {@link Parser#matchesSkipping}:
 *
 * <pre>
 *     List<Object> matches = id.matchesSkipping("foo 123 bar4");
 *     System.out.println(matches);        // [foo", "bar4"]
 * </pre>
 *
 * These are the basic elements to build parsers. There are a few more well
 * documented and tested factory methods in the {@link Parser} class. If you want
 * browse their documentation and tests.
 *
 * <h3>Writing a More Complicated Grammar</h3>
 *
 * Now we are able to write a more complicated grammar for evaluating simple
 * arithmetic expressions. Within a file we start with the grammar for a
 * number (actually an integer):
 *
 * <pre>
 *     Parser number = digit().plus().flatten().trim().map(Integer::fromString);
 * </pre>
 *
 * Then we define the productions for addition and multiplication in order of
 * precedence. Note that we instantiate the productions with undefined parsers
 * upfront, because they recursively refer to each other. Later on we can
 * resolve this recursion by setting their reference:
 *
 * <pre>
 *     Settable term = SettableParser.undefined();
 *     Settable prod = SettableParser.undefined();
 *     Settable prim = SettableParser.undefined();
 *
 *     term.set(prod.seq(char('+').trim()).seq(term).map((values) {
 *       return values[0] + values[2];
 *     }).or(prod));
 *     prod.set(prim.seq(char('*').trim()).seq(prod).map((values) {
 *       return values[0] * values[2];
 *     }).or(prim));
 *     prim.set(char('(').trim().seq(term).seq(char(')'.trim())).map((values) {
 *       return values[1];
 *     }).or(number));
 * </pre>
 *
 * To make sure that our parser consumes all input we wrap it with the `end()`
 * parser into the start production:
 *
 * <pre>
 *     Parser start = term.end();
 * </pre>
 *
 * That's it, now we can test our parser and evaluator:
 *
 * <pre>
 *     System.out.println(start.parse("1 + 2 * 3").value);        // 7
 *     System.out.println(start.parse("(1 + 2) * 3").value);      // 9
 * </pre>
 *
 * As an exercise we could extend the parser to also accept negative numbers
 * and floating point numbers, not only integers. Furthermore it would be
 * useful to support subtraction and division as well. All these features
 * can be added with a few lines of PetitParser code.
 */
package org.petitparser;

