PetitParser
===========

Grammars for programming languages are traditionally specified statically. They are hard to compose and reuse due to ambiguities that inevitably arise. PetitParser combines ideas from scannerless parsing, parser combinators, parsing expression grammars and packrat parsers to model grammars and parsers as objects that can be reconfigured dynamically.

To install dependencies, to compile the code, to run the tests and to generate the JavaDoc use Ant:

    ant all

Dependencies are specified in `ivy.xml`.
