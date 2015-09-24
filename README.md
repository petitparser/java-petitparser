PetitParser for Java
====================

[![Build Status](https://travis-ci.org/petitparser/java-petitparser.svg)](https://travis-ci.org/petitparser/java-petitparser)
[![Coverage Status](https://coveralls.io/repos/petitparser/java-petitparser/badge.svg)](https://coveralls.io/r/petitparser/java-petitparser)
[![Dependency Status](https://www.versioneye.com/user/projects/556c52d56365320026ef4600/badge.svg?style=flat)](https://www.versioneye.com/user/projects/556c52d56365320026ef4600)
[![Github Issues](http://githubbadges.herokuapp.com/petitparser/java-petitparser/issues.svg)](https://github.com/petitparser/java-petitparser/issues)

Grammars for programming languages are traditionally specified statically. They are hard to compose and reuse due to ambiguities that inevitably arise. PetitParser combines ideas from scannnerless parsing, parser combinators, parsing expression grammars and packrat parsers to model grammars and parsers as objects that can be reconfigured dynamically.

This library is open source, stable and well tested. Development happens on [GitHub](https://github.com/petitparser/java-petitparser). Feel free to report issues or create a pull-request there. General questions are best asked on [StackOverflow](http://stackoverflow.com/questions/tagged/petitparser+java).


Installation
------------

### Maven

From the command line check out the code and install your own copy:

```bash
git clone https://github.com/petitparser/java-petitparser.git
cd java-petitparser
git checkout 2.0.0
mvn install
```

Then add the following snippet to your `pom.xml` file:

```xml
<dependencies>
  <dependency>
    <groupId>com.github.petitparser</groupId>
    <artifactId>petitparser-core</artifactId>
    <version>2.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

### Bazel

Alternatively, if you'd like to use [Bazel](http://bazel.io/), checkout the repository as above and run:

```bash
bazel build --java_toolchain=//:java_toolchain //petitparser-core:core
bazel test --java_toolchain=//:java_toolchain //petitparser-core:test 
```


Tutorial
--------

### Writing a Simple Grammar

Writing grammars with PetitParser is simple as writing Java code. For example, to write a grammar that can parse identifiers that start with a letter followed by zero or more letter or digits is defined as follows:

```java
import static org.petitparser.parser.primitive.CharacterParser.*;

class Example {
  public static void main(String[] arguments) {
    Parser id = letter().seq(letter().or(digit()).star());
    ...
  }
}
```

If you look at the object `id` in the debugger, you'll notice that the code above builds a tree of parser objects:

- `SequenceParser`: This parser accepts a sequence of parsers.
  - `CharacterParser`: This parser accepts a single letter.
  - `PossessiveRepeatingParser`: This parser accepts zero or more times another parser.
    - `ChoiceParser`: This parser accepts a single word character.
      - `CharacterParser`: This parser accepts a single letter.
      - `CharacterParser`: This parser accepts a single digit.

### Parsing Some Input

To actually parse a `String` we can use the method `Parser#parse(String)`:

```java
Result id1 = id.parse("yeah");
Result id2 = id.parse("f12");
```

The method `String` returns `Result`, which is either an instance of `Success` or `Failure`. In both examples above we are successful and can retrieve the parse result using `Success#get()`:

```java
System.out.println(id1.get());  // ['y', ['e', 'a', 'h']]
System.out.println(id2.get());  // ['f', ['1', '2']]
```

While it seems odd to get these nested arrays with characters as a return value, this is the default decomposition of the input into a parse tree. We'll see in a while how that can be customized.

If we try to parse something invalid we get an instance of `Failure` as an answer and we can retrieve a descriptive error message using `Failure#getMessage()`:

```java
Result id3 = id.parse('123');
System.out.println(id3.getMessage());  // "letter expected"
System.out.println(id3.getPosition());  // 0
```

Trying to retrieve the parse result by calling `Failure#get()` would throw the exception `ParseError`. `Result#isSuccess()` and `Result#isFailure()` can be used to decide if the parse was successful.

If you are only interested if a given string matches or not you can use the helper method `Parser#accept(String)`:

```java
System.out.println(id.accept("foo"));  // true
System.out.println(id.accept("123"));  // false
```

### Different Kinds of Parsers

PetitParser provide a large set of ready-made parser that you can compose to consume and transform arbitrarily complex languages. The terminal parsers are the most simple ones. We've already seen a few of those:

- `CharacterParser.of('a')` parses the character _a_.
- `StringParser.of("abc")` parses the string _abc_.
- `CharacterParser.any()` parses any character.
- `CharacterParser.digit()` parses any digit from _0_ to _9_.
- `CharacterParser.letter()` parses any letter from _a_ to _z_ and _A_ to _Z_.
- `CharacterParser.word()` parses any letter or digit.

Many other parsers are available in `CharacterParser` and `StringParser`.

So instead of using the letter and digit predicate, we could have written our identifier parser like this:

```java
Parser id = letter().seq(word().star());
```

The next set of parsers are used to combine other parsers together:

- `p1.seq(p2)` parses `p1` followed by `p2` (sequence).
- `p1.or(p2)` parses `p1`, if that doesn't work parses `p2` (ordered choice).
- `p.star()` parses `p` zero or more times.
- `p.plus()` parses `p` one or more times.
- `p.optional()` parses `p`, if possible.
- `p.and()` parses `p`, but does not consume its input.
- `p.not()` parses `p` and succeed when p fails, but does not consume its input.
- `p.end()` parses `p` and succeed at the end of the input.

To attach an action or transformation to a parser we can use the following methods:

- `p.map(value -> ...)` performs the transformation given the function.
- `p.pick(n)` returns the `n`-th element of the list `p` returns.
- `p.flatten()` creates a string from the result of `p`.
- `p.token()` creates a token from the result of `p`.
- `p.trim()` trims whitespaces before and after `p`.

To return a string of the parsed identifier, we can modify our parser like this:

```java
Parser id = letter().seq(word().star()).flatten();
```

To conveniently find all matches in a given input string you can use `Parser#matchesSkipping(String)`:

```java
List<Object> matches = id.matchesSkipping("foo 123 bar4");
System.out.println(matches);  // ["foo", "bar4"]
```

These are the basic elements to build parsers. There are a few more well documented and tested factory methods in the `Parser` class. If you want, browse their documentation and tests.

### Writing a More Complicated Grammar

Now we are able to write a more complicated grammar for evaluating simple arithmetic expressions. Within a file we start with the grammar for a number (actually an integer):

```java
Parser number = digit().plus().flatten().trim().map((String value) -> Integer.parseInt(value));
```

Then we define the productions for addition and multiplication in order of precedence. Note that we instantiate the productions with undefined parsers upfront, because they recursively refer to each other. Later on we can resolve this recursion by setting their reference:

```java
SettableParser term = SettableParser.undefined();
SettableParser prod = SettableParser.undefined();
SettableParser prim = SettableParser.undefined();

term.set(prod.seq(of('+').trim()).seq(term).map((List<Integer> values) -> {
  return values.get(0) + values.get(2);
}).or(prod));
prod.set(prim.seq(of('*').trim()).seq(prod).map((List<Integer> values) -> {
  return values.get(0) * values.get(2);
}).or(prim));
prim.set((of('(').trim().seq(term).seq(of(')').trim())).map((List<Integer> values) -> {
  return values.get(1);
}).or(number));
```

To make sure that our parser consumes all input we wrap it with the `end()` parser into the start production:

```java
Parser start = term.end();
```

That's it, now we can test our parser and evaluator:

```java
System.out.println(start.parse("1 + 2 * 3").get());  // 7
System.out.println(start.parse("(1 + 2) * 3").get());  // 9
```

As an exercise we could extend the parser to also accept negative numbers and floating point numbers, not only integers. Furthermore it would be useful to support subtraction and division as well. All these features
can be added with a few lines of PetitParser code.


Misc
----

### Examples

The package comes with a large collections of grammars and language experiments ready to explore:

- `petitparser-json` contains a complete JSON grammar and parser.
- `petitparser-xml` contains a complete XML grammar and parser.
- `petitparser-smalltalk` contains a complete Smalltalk parser.

### History

PetitParser was originally implemented in [Smalltalk](http://scg.unibe.ch/research/helvetia/petitparser). Later on, as a mean to learn these languages, I reimplemented PetitParser in [Java](https://github.com/petitparser/java-petitparser) and [Dart](https://github.com/petitparser/dart-petitparser). The implementations are very similar in their API and the supported features. If possible, the implementations adopt best practises of the target language.

### Ports

- [Dart](https://github.com/petitparser/dart-petitparser)
- [PHP](https://github.com/mindplay-dk/petitparserphp)
- [Smalltalk](http://scg.unibe.ch/research/helvetia/petitparser)

### License

The MIT License, see [LICENSE](https://raw.githubusercontent.com/petitparser/java-petitparser/master/LICENSE).
