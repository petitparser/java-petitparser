PetitParser for Java
====================

[![Build Status](https://travis-ci.org/petitparser/java-petitparser.svg)](https://travis-ci.org/petitparser/java-petitparser)

Grammars for programming languages are traditionally specified statically. They are hard to compose and reuse due to ambiguities that inevitably arise. PetitParser combines ideas from scannnerless parsing, parser combinators, parsing expression grammars and packrat parsers to model grammars and parsers as objects that can be reconfigured dynamically.

This library is open source, stable and well tested. Development happens on [GitHub](https://github.com/petitparser/java-petitparser). Feel free to report issues or create a pull-request there. General questions are best asked on [StackOverflow](http://stackoverflow.com/questions/tagged/petitparser+java).

An introductory tutorial is part of the [class documentation](http://jenkins.lukas-renggli.ch/job/java-petitparser/javadoc/index.html?org/petitparser/package-summary.html).


Basic Usage
-----------

### Installation

From the command line check out the code and install your own copy:

    $ git clone https://github.com/petitparser/java-petitparser.git
    $ cd java-petitparser
    $ git checkout 2.0.0
    $ mvn install

Then add the following snippet to your `pom.xml` file:

    <dependencies>
      <dependency>
        <groupId>com.github.petitparser</groupId>
        <artifactId>petitparser-core</artifactId>
        <version>2.0.0</version>
      </dependency>
    </dependencies>

### Examples

The package comes with a large collections of grammars and language experiments ready to explore:

- `petitparser-json` contains a complete JSON grammar and parser.
- `petitparser-xml` contains a complete XML grammar and parser.
- `petitparser-smalltalk` contains a complete Smalltalk parser.


Misc
----

### History

PetitParser was originally implemented in [Smalltalk](http://scg.unibe.ch/research/helvetia/petitparser). Later on, as a mean to learn these languages, I reimplemented PetitParser in [Java](https://github.com/petitparser/java-petitparser) and [Dart](https://github.com/petitparser/dart-petitparser). The implementations are very similar in their API and the supported features. If possible, the implementations adopt best practises of the target language.

### Ports

- [Dart](https://github.com/petitparser/dart-petitparser)
- [PHP](https://github.com/mindplay-dk/petitparserphp)
- [Smalltalk](http://scg.unibe.ch/research/helvetia/petitparser)

### License

The MIT License, see [LICENSE](https://raw.githubusercontent.com/petitparser/java-petitparser/master/LICENSE).
