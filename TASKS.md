# PetitParser Java Parity Tasks

Roadmap to bring `petitparser-core` to feature parity with the canonical Dart implementation while maintaining strict backward compatibility with existing code and adopting modern Java best practices (Java 11 baseline, standard functional interfaces, and stream idioms).

---

## Compatibility, Native Design & Efficiency Principles

### Strict Backward Compatibility
- Preserve all existing public methods, constructors, and classes.
- Downstream modules (`petitparser-json`, `petitparser-xml`, `petitparser-smalltalk`) and user code must compile and pass all tests without modification.
- Additive evolution: introduce new functionality alongside existing APIs (e.g. `starSeparated` returning `SeparatedList` without removing `separatedBy` returning `List<Object>`).
- When introducing generic parameters (`Parser<R>`, `Result<R>`, etc.), ensure raw-type usage compiles cleanly without warnings or breaking changes.

### Native Java Idiomatic Design
- Avoid mechanical translations of Dart-specific constructs (e.g. extension methods, positional record tuples, operator overloads).
- Leverage standard Java paradigms:
  - Fluent method chaining on `Parser` and builders.
  - Standard functional interfaces (`java.util.function.Function`, `Predicate`, `BiFunction`, `Supplier`, `Consumer`).
  - Java `Stream` and lazy `Iterable`/`Iterator` for streaming matching and graph traversals.
  - Immutability for core state (`Context`, `Result`, `Token`, `SeparatedList`) using defensive copies and `Collections.unmodifiableList`.
  - Null-safety checks using `Objects.requireNonNull(arg, "message")`.

### High-Performance & Efficiency Goals
- **Zero-Allocation Fast Path**: Every parser subclass must implement an optimized `int fastParseOn(String buffer, int position)` that performs zero heap allocations (no `Context`, `Result`, or boxed wrapper instances) and returns `-1` on failure without throwing exceptions.
- **Direct String Slicing (Zero-List Lexing)**: Character repeaters (`starString()`, `plusString()`, `repeatString()`) must scan characters using primitive loop indexes on `buffer.charAt(i)` and extract the final string with a single native `buffer.substring(start, stop)` call, completely bypassing intermediate `List<Character>` allocations.
- **$O(1)$ Character Lookup Tables**: Specialize character predicates with boolean lookup tables (`boolean[256]`) or primitive bitmasks (`BitSet`/`long[]`) for ASCII/Latin-1 character sets, executing in constant time rather than branching or binary searching.
- **Range Merging Optimization**: Automatically sort and merge adjacent/overlapping character ranges during grammar construction so runtime checks execute the minimum possible comparisons.
- **HotSpot SIMD Intrinsics**: String literal parsers must leverage JVM-intrinsic methods like `String.startsWith(prefix, position)` and `String.regionMatches` to take advantage of vectorized CPU instructions.
- **Stateless Singleton Reuse**: Use static singleton instances for stateless parsers and predicates (e.g. `PositionParser`, `EpsilonParser`, `NewlineParser`, `ConstantCharPredicate.any()`, `DigitCharPredicate.INSTANCE`).
- **Lazy Stream Matching**: Matcher APIs must provide lazy `Stream<T>` and `Iterable<T>` pipelines to process matches on demand with minimal memory footprint.

---

## Phase 1: Core Action, Primitive & Combinator Parsers

- [x] **Task 1.1: Marker & Lifecycle Interfaces**
  - Create `org.petitparser.parser.combinators.SequentialParser` marker interface.
  - Implement `SequentialParser` on `SequenceParser` and `TrimmingParser`.
  - Create `org.petitparser.parser.combinators.ResolvableParser` with `Parser resolve()`.
  - Implement `ResolvableParser` on `SettableParser`.

- [x] **Task 1.2: WhereParser & Filtering**
  - Implement `org.petitparser.parser.actions.WhereParser<T>` with `java.util.function.Predicate<T>` and custom error message / failure factory `BiFunction<Context, Result, Result>`.
  - Add `where(Predicate<T>)`, `where(Predicate<T>, String)`, and `where(Predicate<T>, BiFunction<Context, Result, Result>)` to `Parser`.
  - Implement zero-allocation fast-parse, copy, and equality methods.

- [x] **Task 1.3: SkipParser & Delimiters**
  - Implement `org.petitparser.parser.combinators.SkipParser` implementing `SequentialParser`.
  - Add `skip(Parser before, Parser after)` to `Parser`.
  - Implement zero-allocation fast-parse, copy, child replacement, and equality methods.

- [x] **Task 1.4: LabelParser & Debugging**
  - Implement `org.petitparser.parser.combinators.LabelParser` wrapping a delegate with a string label.
  - Add `labeled(String label)` to `Parser`.
  - Override `toString()` to display `delegate.toString() + "[" + label + "]"`.

- [ ] **Task 1.5: PositionParser**
  - Implement `org.petitparser.parser.primitive.PositionParser` returning `context.getPosition()`.
  - Provide a reusable static singleton `PositionParser.INSTANCE`.
  - Add static factory `Parser.position()`.
  - Implement zero-allocation fast-parse returning position unchanged.

- [ ] **Task 1.6: NewlineParser**
  - Implement `org.petitparser.parser.primitive.NewlineParser` matching `\n`, `\r\n`, and `\r`.
  - Add static factories `Parser.newline()` and `Parser.newline(String message)`.
  - Implement branch-optimized zero-allocation `fastParseOn`.

- [ ] **Task 1.7: Epsilon Value Support & Primitives**
  - Extend `EpsilonParser` to store an optional result value `value` (defaulting to `null`).
  - Provide a reusable static singleton `EpsilonParser.INSTANCE` for `null`.
  - Add static factories `Parser.epsilon()` and `Parser.epsilon(Object value)`.
  - Add static factories `Parser.failure()` and `Parser.failure(String message)`.
  - Add `Parser.constant(Object value)` replacing results with a constant value.

- [ ] **Task 1.8: Token & Context Formatting Utilities**
  - Add `Token.positionString(String buffer, int position)` returning `"line:column"`.
  - Add `Token.join(Iterable<Token> tokens)` combining adjacent tokens from the same buffer.
  - Add `Context.toPositionString()` returning `"line:column"`.

- [ ] **Task 1.9: Phase 1 Parallel Unit Tests**
  - Create test classes under `src/test/java/org/petitparser/`:
    - `org.petitparser.parser.actions.WhereParserTest`
    - `org.petitparser.parser.combinators.SkipParserTest`
    - `org.petitparser.parser.combinators.LabelParserTest`
    - `org.petitparser.parser.primitive.PositionParserTest`
    - `org.petitparser.parser.primitive.NewlineParserTest`
    - `org.petitparser.parser.primitive.EpsilonParserTest`
    - `org.petitparser.context.TokenTest` (augment with `join` and `positionString` tests)
  - Ensure 100% test coverage for all new lines and branches.

---

## Phase 2: High-Performance Character Repeaters & Separated Sequences

- [ ] **Task 2.1: RepeatingCharacterParser (Zero-List Lexing)**
  - Implement `org.petitparser.parser.repeating.RepeatingCharacterParser` directly returning a `String` via `buffer.substring(start, position)` without intermediate `List<Character>` allocations.
  - Add `starString()`, `starString(String message)` to `Parser`.
  - Add `plusString()`, `plusString(String message)` to `Parser`.
  - Add `timesString(int count)`, `timesString(int count, String message)` to `Parser`.
  - Add `repeatString(int min, int max)`, `repeatString(int min, int max, String message)` to `Parser`.
  - Auto-specialize when called on `CharacterParser` to instantiate `RepeatingCharacterParser`.

- [ ] **Task 2.2: SeparatedList Data Structure**
  - Implement immutable `org.petitparser.parser.repeating.SeparatedList<R, S>` holding `elements` and `separators`.
  - Implement `getSequential()` returning alternating interleaved elements and separators.
  - Implement functional folding: `foldLeft(FoldFunction<R, S> callback)` and `foldRight(FoldFunction<R, S> callback)`.
  - Implement `equals`, `hashCode`, `toString`, and `Iterable<Object>`.

- [ ] **Task 2.3: SeparatedRepeatingParser**
  - Implement `org.petitparser.parser.repeating.SeparatedRepeatingParser` implementing `SequentialParser`.
  - Add `starSeparated(Parser separator)` to `Parser`.
  - Add `plusSeparated(Parser separator)` to `Parser`.
  - Add `timesSeparated(Parser separator, int count)` to `Parser`.
  - Add `repeatSeparated(Parser separator, int min, int max)` to `Parser`.
  - Retain existing `separatedBy` and `delimitedBy` returning `List<Object>` for full backward compatibility.

- [ ] **Task 2.4: Phase 2 Parallel Unit Tests**
  - Create test classes:
    - `org.petitparser.parser.repeating.RepeatingCharacterParserTest`
    - `org.petitparser.parser.repeating.SeparatedListTest`
    - `org.petitparser.parser.repeating.SeparatedRepeatingParserTest`
  - Test zero, single, multiple repetitions, fast-parse, and folding operations.

---

## Phase 3: Strongly Typed Sequences, Tuples & Action Combinators

- [ ] **Task 3.1: Dual-Nature Tuple Hierarchy (`Tuple2` to `Tuple9`)**
  - Implement immutable `org.petitparser.parser.repeating.Tuple2<T1, T2>` through `Tuple9` extending `java.util.AbstractList<Object>`.
  - Provide strongly-typed accessors: `tuple.first()`, `tuple.second()`, `tuple.third()`, etc.
  - Implement `AbstractList` contract (`size()`, `get(int index)`), allowing tuples to be treated directly as `List<Object>` for 100% backward compatibility with legacy tests and downstream consumers (`tuple.equals(Arrays.asList(...)) == true`).
  - Implement zero-allocation accessors and structural equality (`equals`, `hashCode`, `toString`).

- [ ] **Task 3.2: Multi-Arity Functional Interfaces**
  - Create standard functional interfaces in `org.petitparser.utils.functions`:
    - `Function3<T1, T2, T3, R>`, `Function4<T1, T2, T3, T4, R>`, ... `Function9<...>`.
  - Ensure compatibility with Java 11 `BiFunction` for arity-2 sequences.

- [ ] **Task 3.3: Typed Sequence Combinators (`SequenceParser2` to `SequenceParser9`)**
  - Implement `SequenceParser2<T1, T2>` through `SequenceParser9<...>` implementing `SequentialParser`.
  - Add fluent `.then(Parser<TN> next)` on `Parser` and sequence parsers, automatically flattening into `SequenceParser3`, `SequenceParser4`, etc. up to 9 elements.
  - Add strongly-typed `.map(BiFunction<? super T1, ? super T2, ? extends R> function)` on `SequenceParser2`.
  - Add strongly-typed `.map(Function3<...>)` through `.map(Function9<...>)` on `SequenceParser3` through `SequenceParser9`, eliminating the need for users to manually unpack tuple or list elements.
  - Implement zero-allocation `fastParseOn`, `copy`, and equality methods.

- [ ] **Task 3.4: Static Sequence Factories**
  - Add static factories `Parser.seq(Parser<T1> p1, Parser<T2> p2)` returning `SequenceParser2<T1, T2>`.
  - Add static factories `Parser.seq(p1, p2, p3)` through `Parser.seq(p1, ..., p9)` returning corresponding typed sequence parsers.
  - Retain existing `Parser.seq(Parser... parsers)` and instance method `seq(Parser other)` returning `SequenceParser` (`Parser<List<Object>>`) for backward compatibility.

- [ ] **Task 3.5: Type Extraction, Casting & Permutation**
  - Implement `org.petitparser.parser.actions.CastParser<R, S>` with `cast()` and `cast(Class<S> clazz)` on `Parser`.
  - Implement `castList(Class<S> clazz)` on `Parser<List<?>>`.
  - Implement `pick(int index)` with negative indexing support, specialized on `RepeatingParser<E>` to return `Parser<E>`.
  - Implement `permute(int... indices)` returning reordered `Tuple` or `List`.

- [ ] **Task 3.6: Variance-Friendly Alternatives (`or` & `orWiden`)**
  - Add `or(Parser<? extends R> other)` to `Parser<R>` to preserve precise local type inference when using Java `var`.
  - Add `<O> Parser<O> orWiden(Parser<? extends O> other)` and static `Parser.or(Parser<? extends T>... parsers)` for explicit widening across differing types (e.g. `Integer` and `Double` into `Number`).

- [ ] **Task 3.7: Strongly-Typed Grammar Definitions**
  - Introduce `org.petitparser.tools.Production<T>` typed token key:
    - Factory `Production.of(String name)` / `GrammarDefinition.production(String name)`.
  - Add typed `def(Production<T> production, Parser<? extends T> parser)`.
  - Add typed `ref(Production<T> production)` returning `Parser<T>`.
  - Retain string-based `def(String, Parser)` and enhance `ref(String)` with `<T> Parser<T> ref(String name)` for seamless existing code compatibility.

- [ ] **Task 3.8: Phase 3 Parallel Unit Tests**
  - Create test classes:
    - `org.petitparser.parser.repeating.TupleTest` (verifying typed access and `List<Object>` contract)
    - `org.petitparser.parser.combinators.SequenceParserNTest` (verifying `then()`, `map()`, arity 2-9, fast-parse)
    - `org.petitparser.parser.actions.CastParserTest`
    - `org.petitparser.parser.actions.PickParserTest`
    - `org.petitparser.tools.TypedGrammarDefinitionTest`

---

## Phase 4: Indentation-Sensitive Parsing

- [ ] **Task 4.1: Indent Engine**
  - Implement `org.petitparser.tools.Indent` class managing an indentation stack and current indent string.
  - Implement `same()` matching current indentation level.
  - Implement `increase()` requiring deeper indentation and pushing to stack.
  - Implement `decrease()` popping indentation from stack.
  - Implement `during(Parser inner)` with transactional rollback on parse failure and choice backtracking.

- [ ] **Task 4.2: Phase 4 Parallel Unit Tests**
  - Create `org.petitparser.tools.IndentTest`:
    - Success restoring outer indentation.
    - Failure rolling back indentation stack.
    - Nested block scopes.
    - Choice rollback trying alternate indented branches.
    - Custom indentation token parsers (tabs vs spaces).

---

---

## Phase 5: Character Predicate AST & Unicode Code Points

- [ ] **Task 5.1: Hybrid CharacterPredicate Hierarchy**
  - Preserve `@FunctionalInterface CharacterPredicate` interface (`test(char)`) for backward compatibility with user lambdas.
  - Introduce concrete AST classes implementing `CharacterPredicate` and `isEqualTo`:
    - `SingleCharPredicate`, `RangeCharPredicate`, `RangesCharPredicate` (binary search on primitive arrays).
    - `LookupCharPredicate` ($O(1)$ boolean array / `BitSet` lookup table for ASCII/Latin-1).
    - `NotCharPredicate` (negated delegate).
    - Reusable singleton constants: `DigitCharPredicate`, `LetterCharPredicate`, `LowercaseCharPredicate`, `UppercaseCharPredicate`, `WhitespaceCharPredicate`, `WordCharPredicate`, `ConstantCharPredicate.any`, `ConstantCharPredicate.none`.
  - Update `CharacterParser` factories (`digit()`, `letter()`, etc.) to use AST singletons so `p.isEqualTo(p)` succeeds across separate calls.

- [ ] **Task 5.2: Range Merging & Character Optimization**
  - Implement `optimizedRanges(List<RangeCharPredicate> ranges)`:
    - Sort ranges by start and stop.
    - Merge adjacent and overlapping ranges to minimize runtime branch tests.
    - Choose optimal predicate ($O(1)$ Lookup table, Single, Range, or Ranges with binary search).
  - Implement `optimizedString(String string, boolean ignoreCase)`.

- [ ] **Task 5.3: StringParser SIMD & Equality Fix**
  - Refactor `StringParser.ofIgnoringCase` to avoid non-comparable method reference lambdas (`value::equalsIgnoreCase`).
  - Implement `StringIgnoreCaseParser` storing the literal string and comparing literals in `hasEqualProperties`.
  - Use `String.regionMatches` and `String.startsWith` for HotSpot-intrinsic performance.

- [ ] **Task 5.4: Unicode Code Points**
  - Implement `UnicodeCharacterParser` decoding UTF-16 surrogate pairs into 21-bit code points ($0 \dots \text{0x10FFFF}$).
  - Add `unicode` parameter / flag to character primitives (`any`, `char`, `pattern`, `anyOf`, `noneOf`).

- [ ] **Task 5.5: Phase 5 Parallel Unit Tests**
  - Create tests:
    - `org.petitparser.parser.primitive.CharacterPredicateAstTest`
    - `org.petitparser.parser.primitive.UnicodeCharacterParserTest`
    - Verify structural equality of character parsers across all predicate variations.

---

## Phase 6: ExpressionBuilder Modernization

- [ ] **Task 6.1: ExpressionBuilder Enhancements**
  - Introduce generic typing `ExpressionBuilder<T>` and `ExpressionGroup<T>`.
  - Add `primitive(Parser parser)` directly on `ExpressionBuilder`.
  - Add `loopback` getter on `ExpressionBuilder`.
  - Add `optional(Object value)` on `ExpressionGroup`.
  - Refactor binary left/right operator builders to use `plusSeparated` and `SeparatedList.foldLeft` / `foldRight`.
  - Add typed functional callbacks (e.g. ternary `(left, op, right) -> result` via `Function3`) while preserving existing `List<Object>` callbacks for compatibility.

- [ ] **Task 6.2: Phase 6 Parallel Unit Tests**
  - Augment `org.petitparser.tools.ExpressionBuilderTest` to test:
    - Builder-level primitives.
    - Optional expression groups.
    - Typed operator callbacks.

---

## Phase 7: Grammar Reflection & Analyzer

- [ ] **Task 7.1: Grammar Graph Traversal & Analyzer Base**
  - Implement `org.petitparser.utils.Analyzer` leveraging standard Java collections and `Stream`:
    - Reachable parsers discovery (`parsers`).
    - Deep children set cache (`allChildren(parser)`).
    - Graph path search (`findPath`, `findPathTo`, `findAllPaths`, `findAllPathsTo`).

- [ ] **Task 7.2: Grammar Property Computation**
  - Implement nullability fixed-point analysis (`isNullable(parser)`).
  - Implement FIRST-set calculation (`firstSet(parser)`): terminal parsers that can appear first, taking `SequentialParser` into account.
  - Implement FOLLOW-set calculation (`followSet(parser)`): terminal parsers that can immediately succeed `parser`.
  - Implement cycle detection (`cycleSet(parser)`).

- [ ] **Task 7.3: Grammar Reference Inlining**
  - Implement `resolve(Parser parser)` resolving all `ResolvableParser` references into direct cycles/graphs without delegate overhead.

- [ ] **Task 7.4: Phase 7 Parallel Unit Tests**
  - Create `org.petitparser.utils.AnalyzerTest`:
    - First-set and follow-set validation on LL/LR grammar definitions.
    - Nullable chain detection.
    - Path searching between arbitrary nodes.
    - Resolvable reference inlining.

---

## Phase 8: Grammar Linter

- [ ] **Task 8.1: Linter Engine Architecture**
  - Implement `org.petitparser.utils.linter.LinterRule`, `LinterIssue`, `LinterType` (info, warning, error).
  - Implement `linter(Parser root, ...)` executing active rules using `Analyzer`.

- [ ] **Task 8.2: 13 Linter Rules Implementation**
  - [ ] `CharacterRepeaterRule`: Identifies `.star().flatten()` on character parsers and suggests `starString()`.
  - [ ] `DuplicateParserRule`: Identifies duplicate structurally equal parser instances in grammar graph.
  - [ ] `LeftRecursionRule`: Identifies left-recursive loops that lead to infinite recursion.
  - [ ] `NestedChoiceRule`: Identifies nested choice combinators.
  - [ ] `NullableRepeaterRule`: Identifies repeating parsers over nullable delegates that lead to infinite loops.
  - [ ] `OverlappingChoiceRule`: Identifies choices where a prefix choice shadows a later choice.
  - [ ] `RepeatedChoiceRule`: Identifies duplicate branches in a choice parser.
  - [ ] `UnnecessaryFlattenRule`: Identifies `flatten()` on parsers already producing Strings.
  - [ ] `UnnecessaryResolvableRule`: Identifies unresolved resolvable wrappers.
  - [ ] `UnoptimizedFlattenRule`: Identifies `flatten()` containing inner actions.
  - [ ] `UnreachableChoiceRule`: Identifies choices located after unconditional matchers.
  - [ ] `UnresolvedSettableRule`: Identifies `SettableParser` left in undefined state.
  - [ ] `UnusedResultRule`: Identifies complex sub-parses whose results are discarded.

- [ ] **Task 8.3: Phase 8 Parallel Unit Tests**
  - Create `org.petitparser.utils.linter.LinterTest` verifying positive and negative triggers for each of the 13 rules.

---

## Phase 9: Extended Optimizer

- [ ] **Task 9.1: Extensible OptimizeRule Framework**
  - Define `OptimizeRule` interface and refactor `Optimizer` to use modular rules.
  - Keep `RemoveDelegate` and `RemoveDuplicate` rules.

- [ ] **Task 9.2: New Optimizer Rules**
  - Implement `FlattenChoiceRule`: Flattens nested choices `[a, [b, c]]` into `[a, b, c]`.
  - Implement `CharacterRepeaterRule`: Transforms `FlattenParser(PossessiveRepeatingParser(CharacterParser))` into `RepeatingCharacterParser`.

- [ ] **Task 9.3: Phase 9 Parallel Unit Tests**
  - Augment `org.petitparser.utils.OptimizerTest` to cover choice flattening and character repeater rewrites.

---

## Phase 10: Debugger & Matcher Enhancements

- [ ] **Task 10.1: Progress Stepper**
  - Implement `org.petitparser.utils.Progress`:
    - `progress(Parser root, Consumer<ProgressFrame> observer)` visual execution debugger.
    - `ProgressFrame` capturing parser, context, position, and backtracking events.

- [ ] **Task 10.2: Matcher Offset & Lazy Streams**
  - Enhance `accept(String input, int start)` with start offset.
  - Add lazy `matchesAsStream(String input, int start)` returning `java.util.stream.Stream<T>` backed by a custom `Spliterator`.
  - Add lazy `matches(String input, int start)` returning `Iterable<T>`.

- [ ] **Task 10.3: Phase 10 Parallel Unit Tests**
  - Create `org.petitparser.utils.ProgressTest` and `org.petitparser.MatcherTest`.

---

## Phase 11: End-to-End Verification & Backward Compatibility

- [ ] **Task 11.1: Downstream Module Compatibility Verification**
  - Verify `petitparser-json`, `petitparser-xml`, and `petitparser-smalltalk` compile and pass tests without modifications.
  - Ensure raw-type usage compiles cleanly without errors or breaking changes for existing code.

- [ ] **Task 11.2: JMH Benchmark Suite**
  - Measure throughput and allocation rate of `RepeatingCharacterParser` vs `.star().flatten()`.
  - Measure $O(1)$ Lookup table predicates vs chained range predicates.
  - Measure `fastParseOn` zero-allocation performance against Dart baseline.
