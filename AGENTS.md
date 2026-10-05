# Development Charter & Agent Guidelines

## General Instructions

- Deliver code, tests, and documentation directly with zero introductory fluff or pleasantries.
- Strictly adhere to 2 spaces for indentation.
- Preserve zero external runtime dependencies for `petitparser-core` (rely exclusively on the Java standard library).
- Target Java 11 bytecode compatibility (`<maven.compiler.release>11</maven.compiler.release>`).

---

## Backward Compatibility & Java Idioms

### Backward Compatibility Principles
- **No Breaking Changes**: Existing public methods, constructors, and classes must remain intact. Downstream modules (`petitparser-json`, `petitparser-xml`, `petitparser-smalltalk`) and user code must continue to compile and pass tests without modification.
- **Additive Evolution**: Introduce new functionality alongside existing APIs (e.g., provide `starSeparated` returning `SeparatedList` without removing `separatedBy` returning `List<Object>`).
- **Raw-Type Compatibility**: When introducing generic parameters (`Parser<R>`, `Result<R>`, etc.), ensure raw-type usage compiles cleanly without requiring modifications to existing untyped grammars.

### Java-Centric Best Practices
- **Idiomatic Java vs. Verbatim Dart**: Avoid mechanical translations of Dart-specific features (such as extension methods, positional record tuples, or operator overloading). Leverage standard Java paradigms:
  - Fluent method chaining on `Parser` and builders.
  - Standard functional interfaces (`java.util.function.Function`, `Predicate`, `BiFunction`, `Supplier`, `Consumer`).
  - Java `Stream` and lazy `Iterable`/`Iterator` for streaming matching and graph traversals.
  - Immutability for core state (`Context`, `Result`, `Token`, `SeparatedList`, `Tuple`) using defensive copies and unmodifiable collections.
  - Null-safety checks using `Objects.requireNonNull(arg, "message")`.

### Type System & Generics Guidelines
- **Dual-Nature Tuples**: Java 11 lacks language-level tuples/records. Implement `Tuple2<T1, T2>` through `Tuple9` extending `AbstractList<Object>`.
  - Enables strongly-typed accessors (`tuple.first()`, `tuple.second()`, etc.) without losing compatibility with APIs expecting `List<Object>`.
  - Seamlessly passes equality against standard lists (`tuple.equals(Arrays.asList(...)) == true`).
- **Direct-Mapped Sequences**: Provide fluent `.then()` sequence chaining returning `SequenceParser2` through `SequenceParser9`, offering direct multi-argument mapping (`.map((a, b) -> ...)` via `BiFunction`, `Function3`, etc.) so users rarely need to inspect the underlying `Tuple`.
- **PECS (Producer Extends, Consumer Super)**:
  - Input callbacks consume: `Function<? super R, ? extends S>`, `Predicate<? super R>`, `BiFunction<? super T1, ? super T2, ? extends S>`.
  - Child parser acceptance produces: `Parser<? extends R>`.
- **Local Type Inference (`var`) Preservation**:
  - Keep return types specific rather than introducing unnecessary wildcard returns that cause `var` to degrade to `Parser<Object>`.
  - Provide `or(Parser<? extends R> other)` to retain exact type `R`, and `orWiden(Parser<? extends O> other)` for explicit widening across differing types.
- **Production Keys vs. Method References**:
  - Unlike Dart, Java method references (`this::rule`) do NOT preserve object identity or equality (`this::foo != this::foo`).
  - Provide type-safe `Production<T>` keys (`Production<Expr> EXPR = production("expr")`) in `GrammarDefinition` alongside `SettableParser<T>` fields to deliver 100% type safety and IDE refactoring without synthetic lambda reflection.

---

## Java Performance & Efficiency Guidelines

### Zero-Allocation Fast Parsing
- Subclasses must implement an optimized `int fastParseOn(String buffer, int position)`:
  - Never allocate `Context`, `Result`, `Success`, or `Failure` objects on the fast path.
  - Avoid boxing primitive `char` or `int` values.
  - Never throw exceptions on normal parse failure; return `-1` immediately.

### Primitive Specialization & Character Lookups
- **$O(1)$ Character Lookup Tables**: For ASCII/Latin-1 character sets, specialize character predicates with boolean lookup tables (`boolean[256]`) or primitive bitmasks (`BitSet`/`long[]`) to perform instantaneous $O(1)$ checks instead of linear scans or branching.
- **Cache-Friendly Range Searching**: For sparse/Unicode ranges, use sorted primitive arrays (`char[] starts`, `char[] stops`) with `Arrays.binarySearch`.
- **Pre-Merged Ranges**: Automatically merge adjacent and overlapping ranges during grammar construction so runtime checks execute minimum comparisons.

### Direct Substring Extraction (Zero-List Lexing)
- In lexing and tokenization, eliminate intermediate `List<Character>` allocations. Parsers matching sequences of characters (`starString()`, `plusString()`, `repeatString()`, `flatten()`) must scan characters using primitive loop indexes on `buffer.charAt(i)` and extract the final sub-string with a single `buffer.substring(start, stop)` call.

### JVM Intrinsics & String Matching
- Leverage HotSpot-intrinsic methods such as `String.startsWith(prefix, position)` and `String.regionMatches` for multi-character literals to benefit from vectorized SIMD instructions.

### Singleton Reuse & Allocation Minimization
- Reuse immutable static singleton instances for stateless parsers and predicates (e.g. `PositionParser`, `EpsilonParser`, `NewlineParser`, `ConstantCharPredicate.any()`, `DigitCharPredicate.INSTANCE`).

---

## Architecture & Code Quality

### Package Structure
Implementation files must strictly adhere to the established package layout:
- `org.petitparser.context`: Context, Result, Success, Failure, Token, ParseError.
- `org.petitparser.parser`: Parser base class and common combinator methods.
- `org.petitparser.parser.actions`: Action, Trimming, Flatten, Where, Continuation parsers.
- `org.petitparser.parser.combinators`: Choice, Sequence, Settable, Optional, Not, And, Skip, Label, SequentialParser, ResolvableParser.
- `org.petitparser.parser.primitive`: CharacterParser, CharacterPredicate, StringParser, EpsilonParser, FailureParser, PositionParser, NewlineParser.
- `org.petitparser.parser.repeating`: Possessive, Greedy, Lazy, Limited, RepeatingCharacterParser, SeparatedRepeatingParser, SeparatedList.
- `org.petitparser.tools`: GrammarDefinition, GrammarParser, ExpressionBuilder, Indent.
- `org.petitparser.utils`: Mirror, Optimizer, Analyzer, Linter, Profiler, Tracer, FailureJoiner, Functions.

### Parser Contracts
Every concrete `Parser` subclass must implement:
1. `Result parseOn(Context context)`: Primary parsing logic allocating `Success` or `Failure`.
2. `int fastParseOn(String buffer, int position)`: Optimized parsing logic without allocating context/result objects. Returns new position on success, `-1` on failure.
3. `Parser copy()`: Shallow copy of the parser.
4. `boolean hasEqualProperties(Parser other)`: State comparison if the parser adds fields/configuration.
5. `List<Parser> getChildren()` and `void replace(Parser source, Parser target)`: If the parser references child/delegate parsers.
6. `String toString()`: Human-readable representation including key configuration or message.

### Documentation & Comments
- Provide Javadoc comments (`/** ... */`) for all public classes, interfaces, constructors, methods, and constants.
- Include `@param`, `@return`, and `@throws` tags where appropriate.
- Include executable, accurate code examples for non-trivial public combinators and builders.
- Use `{@link ...}` and `{@code ...}` tags to reference classes and code constructs.

---

## Testing & Quality Assurance

### Parallel Test Hierarchy
- Maintain a strict 1:1 parallel package and class hierarchy between source and test trees:
  - Source: `src/main/java/org/petitparser/pkg/MyClass.java`
  - Test: `src/test/java/org/petitparser/pkg/MyClassTest.java`
- Existing root-level tests in `org.petitparser.*` should be preserved, while new parser tests must follow the parallel package structure.

### Test Coverage Expectations
- Unit tests are required for every line of code, including edge cases, fast-parse paths, error conditions, copy semantics, and structural equality (`isEqualTo`).
- Every parser must be tested with:
  1. Passing inputs verifying parsed value and position.
  2. Failing inputs verifying failure message and exact failure position.
  3. Prefix/sub-string inputs to test non-zero start positions and bounds.
  4. Fast-parse path (`accept(input)` or `fastParseOn(buffer, position)`).
  5. Copy semantics (`copy()`) and structural equality (`isEqualTo()`).

### Running Tests
Execute the Maven wrapper from the repository root:
- Run all tests across the reactor:
  ```bash
  ./mvnw test
  ```
- Run tests only in `petitparser-core`:
  ```bash
  ./mvnw test -pl petitparser-core
  ```
- Run a specific test class:
  ```bash
  ./mvnw test -pl petitparser-core -Dtest=MyClassTest
  ```
- Run a specific test method:
  ```bash
  ./mvnw test -pl petitparser-core -Dtest=MyClassTest#testMethodName
  ```
- Clean and build:
  ```bash
  ./mvnw clean test
  ```
