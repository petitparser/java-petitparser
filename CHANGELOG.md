# Changelog

## 2.5.0

- Upgraded baseline to Java 21 LTS (`<maven.compiler.release>21</maven.compiler.release>`) and modernized GitHub Actions CI.
- Added fluent `.then(...)` sequence chaining with strongly-typed `SequenceParser2` through `SequenceParser9`.
- Added multi-arity `.map(...)` callbacks (`BiFunction`, `Function3` through `Function9`) for type-safe sequence unpacking.
- Introduced dual-nature tuples (`Tuple2` through `Tuple9`) extending `AbstractList<Object>` with typed accessors and standard list compatibility.
- Added type-safe grammar productions with `Production<T>` keys and `GrammarParser` typed lookup.
- Modernized `ExpressionBuilder<T>` with full generic typing, builder-level primitive helpers, loopback access, optional operator groups, `foldLeft`/`foldRight`, and typed action callbacks.
- Added zero-list repeating character parsers (`starString()`, `plusString()`, `timesString()`, `repeatString()`) with SIMD string matching.
- Added full Unicode code point and surrogate pair parsing with `UnicodeCharacterParser`.
- Added `CharacterPredicate` AST hierarchy with range merging and automatic lookup table optimization.
- Added indentation-sensitive parsing engine (`Indent`) for indentation-aware grammars (`Indent.during`, `equal`, `greater`, etc.).
- Added grammar `Analyzer` for computing reachability, cycles, nullability, FIRST sets, and FOLLOW sets.
- Added grammar `Linter` with 13 automated rules detecting anti-patterns.
- Added grammar `Optimizer` rule engine with choice flattening, character repeater rewriting, and delegate removal.
- Added `Progress` visual execution debugger and `ProgressFrame` for inspecting parser backtracking and execution graphs.
- Added lazy stream matching (`matchesAsStream()`) and offset-based searching on `Parser`.
- Added zero-allocation fast parse (`fastParseOn`) across all parsers.
- Added `skip(...)` and delimiter combinators, `where(...)` filtering combinator, `label(...)` parser, and `position()` / `newline()` primitives.
- Comprehensive documentation rewrite and examples modernization in `README.md` and `ExamplesTest.java`.
- Near 100% test coverage across all reactor modules.

## 2.4.0

- Added Java Platform Module System (JPMS) support with `module-info.java`.
- Added configurable failure join strategy on `ChoiceParser` (`FailureJoiner`).
- Improved character parser failure messages and XML grammar performance.
- Migrated continuous integration pipelines to GitHub Actions.

## 2.3.0

- Introduced zero-allocation fast parse mode (`fastParseOn`).
- Added parenthesis expression support to `ExpressionBuilder`.
- Optimized XML and JSON grammars to take advantage of fast parsing.
- Improved Java 11 (LTS) compatibility.

## 2.2.0

- Preserved parser generic types during `copy()`.
- Cleaned up build configuration for Maven Central publication.
- Code cleanups, static analysis fixes, and dependency upgrades.

## 2.1.0

- Introduced `GrammarDefinition` and `GrammarParser` following the Dart architecture.
- Migrated XML and Smalltalk example grammars to grammar definitions.
- Modernized internal collections, replacing legacy synchronized classes.

## 2.0.0

- Major rewrite modernizing PetitParser for Java 8+ with lambda expressions.
- Package structure reorganization into `context`, `actions`, `combinators`, `primitive`, and `repeating`.
- Rebuilt character predicate hierarchy with optimized range checks and bitmask lookups.
- Added expression builder and composite grammar tools.
