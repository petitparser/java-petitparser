PetitParser for Java
====================

Grammars for programming languages are traditionally specified statically. They are hard to compose and reuse due to ambiguities that inevitably arise. PetitParser combines ideas from scannnerless parsing, parser combinators, parsing expression grammars and packrat parsers to model grammars and parsers as objects that can be reconfigured dynamically.

This library is open source, stable and well tested. Development happens on [GitHub](https://github.com/renggli/java-petitparser). Feel free to report issues or create a pull-request there. General questions are best asked on [StackOverflow](http://stackoverflow.com/questions/tagged/petitparser).

Continuous build results are available from [Jenkins](http://jenkins.lukas-renggli.ch/job/java-petitparser). An introductionary tutorial is part of the [class documentation](http://jenkins.lukas-renggli.ch/job/java-petitparser/javadoc).


Misc
----

### History

PetitParser was originally implemented in [Smalltalk](http://scg.unibe.ch/research/helvetia/petitparser). Later on, as a mean to learn these languages, I reimplemented PetitParser in [Java](https://github.com/renggli/PetitParserJava) and [Dart](https://github.com/renggli/PetitParserDart). The implementations are very similar in their API and the supported features. If possible, the implementations adopt best practises of the target language.

### Ports

- [Dart](https://github.com/renggli/dart-petitparser)
- [PHP](https://github.com/mindplay-dk/petitparserphp)
- [Smalltalk](http://scg.unibe.ch/research/helvetia/petitparser)

### License

The MIT License, see [LICENSE](LICENSE).
