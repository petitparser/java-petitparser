package org.petitparser.utils;

import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.TrimmingParser;
import org.petitparser.parser.combinators.AndParser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.NotParser;
import org.petitparser.parser.combinators.OptionalParser;
import org.petitparser.parser.combinators.SequentialParser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.parser.primitive.PositionParser;
import org.petitparser.parser.repeating.RepeatingParser;
import org.petitparser.parser.repeating.SeparatedRepeatingParser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reflective grammar graph analyzer.
 */
public class Analyzer {

  /**
   * Constructs an {@link Analyzer} for the provided {@code root} parser.
   *
   * @param root the root parser of the grammar
   * @return the analyzer instance
   */
  public static Analyzer of(Parser root) {
    return new Analyzer(root);
  }

  private final Parser root;
  private List<Parser> reachableParsers;
  private final Map<Parser, Set<Parser>> allChildrenCache = new HashMap<>();
  private Map<Parser, Boolean> nullableCache;
  private Map<Parser, Set<Parser>> firstSetsCache;
  private Map<Parser, Set<Parser>> followSetsCache;

  /**
   * Constructs an {@link Analyzer} rooted at {@code root}.
   *
   * @param root the root parser
   */
  public Analyzer(Parser root) {
    this.root = Objects.requireNonNull(root, "Undefined root parser");
  }

  /**
   * Returns the root parser of this analyzer.
   *
   * @return the root parser
   */
  public Parser getRoot() {
    return root;
  }

  /**
   * Returns all parsers reachable from the root in preorder DFS traversal.
   *
   * @return list of reachable parsers
   */
  public List<Parser> parsers() {
    if (reachableParsers == null) {
      List<Parser> result = new ArrayList<>();
      Set<Parser> seen = new HashSet<>();
      Deque<Parser> todo = new ArrayDeque<>();
      todo.push(root);
      seen.add(root);
      while (!todo.isEmpty()) {
        Parser current = todo.pop();
        result.add(current);
        List<Parser> children = current.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
          Parser child = children.get(i);
          if (seen.add(child)) {
            todo.push(child);
          }
        }
      }
      reachableParsers = Collections.unmodifiableList(result);
    }
    return reachableParsers;
  }

  /**
   * Alias for {@link #parsers()}.
   *
   * @return list of reachable parsers
   */
  public List<Parser> getParsers() {
    return parsers();
  }

  /**
   * Returns a sequential {@link Stream} over all reachable parsers.
   *
   * @return stream of reachable parsers
   */
  public Stream<Parser> stream() {
    return parsers().stream();
  }

  /**
   * Returns all deep transitive children of {@code parser}.
   *
   * @param parser the parser
   * @return unmodifiable set of all transitive descendants
   */
  public Set<Parser> allChildren(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    return allChildrenCache.computeIfAbsent(parser, p -> {
      Set<Parser> result = new LinkedHashSet<>();
      Deque<Parser> todo = new ArrayDeque<>(p.getChildren());
      result.addAll(p.getChildren());
      while (!todo.isEmpty()) {
        Parser current = todo.pop();
        for (Parser child : current.getChildren()) {
          if (result.add(child)) {
            todo.push(child);
          }
        }
      }
      return Collections.unmodifiableSet(result);
    });
  }

  /**
   * Finds the shortest path from the root parser to {@code target}.
   *
   * @param target the target parser
   * @return path from root to target, or empty list if unreachable
   */
  public List<Parser> findPathTo(Parser target) {
    return findPath(root, target);
  }

  /**
   * Finds the shortest path from the root parser to a parser matching {@code predicate}.
   *
   * @param predicate the condition to match
   * @return path from root to matched parser, or empty list if unreachable
   */
  public List<Parser> findPathTo(Predicate<Parser> predicate) {
    return findPath(root, predicate);
  }

  /**
   * Finds the shortest path from {@code source} to {@code target}.
   *
   * @param source the source parser
   * @param target the target parser
   * @return path from source to target, or empty list if unreachable
   */
  public List<Parser> findPath(Parser source, Parser target) {
    Objects.requireNonNull(target, "Undefined target parser");
    return findPath(source, target::equals);
  }

  /**
   * Finds the shortest path from {@code source} to a parser matching {@code predicate}.
   *
   * @param source    the source parser
   * @param predicate the condition to match
   * @return path from source to matched parser, or empty list if unreachable
   */
  public List<Parser> findPath(Parser source, Predicate<Parser> predicate) {
    Objects.requireNonNull(source, "Undefined source parser");
    Objects.requireNonNull(predicate, "Undefined predicate");
    if (predicate.test(source)) {
      return Collections.singletonList(source);
    }
    Queue<Parser> queue = new ArrayDeque<>();
    Map<Parser, Parser> parentMap = new HashMap<>();
    Set<Parser> visited = new HashSet<>();
    queue.add(source);
    visited.add(source);
    Parser target = null;
    while (!queue.isEmpty()) {
      Parser current = queue.poll();
      for (Parser child : current.getChildren()) {
        if (visited.add(child)) {
          parentMap.put(child, current);
          if (predicate.test(child)) {
            target = child;
            queue.clear();
            break;
          }
          queue.add(child);
        }
      }
    }
    if (target == null) {
      return Collections.emptyList();
    }
    List<Parser> path = new ArrayList<>();
    for (Parser at = target; at != null; at = parentMap.get(at)) {
      path.add(at);
    }
    Collections.reverse(path);
    return Collections.unmodifiableList(path);
  }

  /**
   * Finds all simple paths from the root parser to {@code target}.
   *
   * @param target the target parser
   * @return all simple paths from root to target
   */
  public List<List<Parser>> findAllPathsTo(Parser target) {
    return findAllPaths(root, target);
  }

  /**
   * Finds all simple paths from the root parser to parsers matching {@code predicate}.
   *
   * @param predicate the condition to match
   * @return all simple paths from root to matched parser
   */
  public List<List<Parser>> findAllPathsTo(Predicate<Parser> predicate) {
    return findAllPaths(root, predicate);
  }

  /**
   * Finds all simple paths from {@code source} to {@code target}.
   *
   * @param source the source parser
   * @param target the target parser
   * @return all simple paths from source to target
   */
  public List<List<Parser>> findAllPaths(Parser source, Parser target) {
    Objects.requireNonNull(target, "Undefined target parser");
    return findAllPaths(source, target::equals);
  }

  /**
   * Finds all simple paths from {@code source} to parsers matching {@code predicate}.
   *
   * @param source    the source parser
   * @param predicate the condition to match
   * @return all simple paths from source to matched parser
   */
  public List<List<Parser>> findAllPaths(Parser source, Predicate<Parser> predicate) {
    Objects.requireNonNull(source, "Undefined source parser");
    Objects.requireNonNull(predicate, "Undefined predicate");
    List<List<Parser>> result = new ArrayList<>();
    List<Parser> currentPath = new ArrayList<>();
    Set<Parser> visited = new HashSet<>();
    findAllPathsHelper(source, predicate, visited, currentPath, result);
    return Collections.unmodifiableList(result);
  }

  private void findAllPathsHelper(
      Parser current,
      Predicate<Parser> predicate,
      Set<Parser> visited,
      List<Parser> currentPath,
      List<List<Parser>> result) {
    currentPath.add(current);
    visited.add(current);
    if (predicate.test(current)) {
      result.add(List.copyOf(currentPath));
    } else {
      for (Parser child : current.getChildren()) {
        if (!visited.contains(child)) {
          findAllPathsHelper(child, predicate, visited, currentPath, result);
        }
      }
    }
    visited.remove(current);
    currentPath.remove(currentPath.size() - 1);
  }

  /**
   * Finds a cycle path from {@code parser} back to itself, if present.
   *
   * @param parser the parser to inspect
   * @return cycle path starting and ending with parser, or empty list if no cycle
   */
  public List<Parser> findCycle(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    for (Parser child : parser.getChildren()) {
      List<Parser> path = findPath(child, parser);
      if (!path.isEmpty()) {
        List<Parser> cycle = new ArrayList<>();
        cycle.add(parser);
        cycle.addAll(path);
        return Collections.unmodifiableList(cycle);
      }
    }
    return Collections.emptyList();
  }

  /**
   * Returns whether {@code parser} is part of a cycle.
   *
   * @param parser the parser to check
   * @return true if cyclic
   */
  public boolean isCyclic(Parser parser) {
    return allChildren(parser).contains(parser);
  }

  /**
   * Returns whether any cycle exists in the reachable grammar graph.
   *
   * @return true if any cyclic parser is present
   */
  public boolean isCyclic() {
    return parsers().stream().anyMatch(this::isCyclic);
  }

  /**
   * Returns all parsers that participate in cycles with {@code parser}.
   *
   * @param parser the parser to check
   * @return set of parsers in cycle with parser, or empty set if parser is not cyclic
   */
  public Set<Parser> cycleSet(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    Set<Parser> children = allChildren(parser);
    if (!children.contains(parser)) {
      return Collections.emptySet();
    }
    Set<Parser> result = new LinkedHashSet<>();
    result.add(parser);
    for (Parser child : children) {
      if (child == parser || allChildren(child).contains(parser)) {
        result.add(child);
      }
    }
    return Collections.unmodifiableSet(result);
  }

  /**
   * Returns all parsers that participate in cycles in this grammar.
   *
   * @return set of all cyclic parsers
   */
  public Set<Parser> cycles() {
    return parsers().stream()
        .filter(this::isCyclic)
        .collect(Collectors.toUnmodifiableSet());
  }

  /**
   * Returns whether {@code parser} is nullable (can succeed matching the empty string).
   *
   * @param parser the parser to check
   * @return true if nullable
   */
  public boolean isNullable(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    if (nullableCache == null) {
      nullableCache = computeNullability();
    }
    Boolean cached = nullableCache.get(parser);
    if (cached != null) {
      return cached;
    }
    return Analyzer.of(parser).isNullable(parser);
  }

  private Map<Parser, Boolean> computeNullability() {
    Map<Parser, Boolean> nullable = new HashMap<>();
    List<Parser> all = parsers();
    for (Parser p : all) {
      if (p instanceof EpsilonParser || p instanceof PositionParser || p instanceof OptionalParser) {
        nullable.put(p, true);
      } else if (p instanceof RepeatingParser && ((RepeatingParser) p).getMin() == 0) {
        nullable.put(p, true);
      } else {
        nullable.put(p, false);
      }
    }

    boolean changed = true;
    int iterations = 0;
    int maxIterations = all.size() * 2 + 1;
    while (changed && iterations++ < maxIterations) {
      changed = false;
      for (Parser p : all) {
        if (Boolean.TRUE.equals(nullable.get(p))) {
          continue;
        }
        boolean nowNullable = false;
        if (p instanceof TrimmingParser) {
          Parser del = p.getChildren().get(0);
          nowNullable = Boolean.TRUE.equals(nullable.get(del));
        } else if (p instanceof SequentialParser) {
          nowNullable = p.getChildren().stream()
              .allMatch(c -> Boolean.TRUE.equals(nullable.get(c)));
        } else if (p instanceof ChoiceParser) {
          nowNullable = p.getChildren().stream()
              .anyMatch(c -> Boolean.TRUE.equals(nullable.get(c)));
        } else if (p instanceof SeparatedRepeatingParser) {
          SeparatedRepeatingParser srp = (SeparatedRepeatingParser) p;
          if (srp.getMin() == 0) {
            nowNullable = true;
          } else {
            nowNullable = Boolean.TRUE.equals(nullable.get(p.getChildren().get(0))) &&
                Boolean.TRUE.equals(nullable.get(p.getChildren().get(1)));
          }
        } else if (p instanceof RepeatingParser) {
          RepeatingParser rp = (RepeatingParser) p;
          if (rp.getMin() == 0) {
            nowNullable = true;
          } else {
            nowNullable = Boolean.TRUE.equals(nullable.get(rp.getChildren().get(0)));
          }
        } else if (p instanceof NotParser) {
          Parser child = p.getChildren().get(0);
          nowNullable = !Boolean.TRUE.equals(nullable.get(child));
        } else if (p instanceof AndParser) {
          Parser child = p.getChildren().get(0);
          nowNullable = Boolean.TRUE.equals(nullable.get(child));
        } else if (!p.getChildren().isEmpty()) {
          nowNullable = p.getChildren().stream()
              .allMatch(c -> Boolean.TRUE.equals(nullable.get(c)));
        }
        if (nowNullable) {
          nullable.put(p, true);
          changed = true;
        }
      }
    }
    return nullable;
  }

  private boolean isTerminal(Parser parser) {
    return parser.getChildren().isEmpty() &&
        !(parser instanceof EpsilonParser) &&
        !(parser instanceof FailureParser) &&
        !(parser instanceof PositionParser);
  }

  /**
   * Returns the FIRST-set of {@code parser}: the terminal parsers that can appear first.
   *
   * @param parser the parser
   * @return set of terminal parsers
   */
  public Set<Parser> firstSet(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    if (firstSetsCache == null) {
      firstSetsCache = computeFirstSets();
    }
    Set<Parser> cached = firstSetsCache.get(parser);
    if (cached != null) {
      return cached;
    }
    return Analyzer.of(parser).firstSet(parser);
  }

  private Map<Parser, Set<Parser>> computeFirstSets() {
    Map<Parser, Set<Parser>> firstSets = new HashMap<>();
    List<Parser> all = parsers();
    for (Parser p : all) {
      Set<Parser> set = new HashSet<>();
      if (isTerminal(p)) {
        set.add(p);
      }
      firstSets.put(p, set);
    }

    boolean changed = true;
    int iterations = 0;
    int maxIterations = all.size() * all.size() + 1;
    while (changed && iterations++ < maxIterations) {
      changed = false;
      for (Parser p : all) {
        if (isTerminal(p)) {
          continue;
        }
        Set<Parser> currentSet = firstSets.get(p);
        int beforeSize = currentSet.size();

        if (p instanceof TrimmingParser) {
          Parser del = p.getChildren().get(0);
          Parser left = p.getChildren().get(1);
          Parser right = p.getChildren().get(2);
          currentSet.addAll(firstSets.get(left));
          currentSet.addAll(firstSets.get(del));
          if (isNullable(del)) {
            currentSet.addAll(firstSets.get(right));
          }
        } else if (p instanceof SequentialParser) {
          for (Parser child : p.getChildren()) {
            currentSet.addAll(firstSets.get(child));
            if (!isNullable(child)) {
              break;
            }
          }
        } else {
          for (Parser child : p.getChildren()) {
            if (p instanceof SeparatedRepeatingParser && child == p.getChildren().get(1)) {
              continue;
            }
            currentSet.addAll(firstSets.get(child));
          }
        }

        if (currentSet.size() > beforeSize) {
          changed = true;
        }
      }
    }

    Map<Parser, Set<Parser>> unmodifiable = new HashMap<>();
    for (Map.Entry<Parser, Set<Parser>> entry : firstSets.entrySet()) {
      unmodifiable.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
    }
    return unmodifiable;
  }

  /**
   * Returns the FOLLOW-set of {@code parser}: the terminal parsers that can immediately succeed it.
   *
   * @param parser the parser
   * @return set of terminal parsers
   */
  public Set<Parser> followSet(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    if (followSetsCache == null) {
      followSetsCache = computeFollowSets();
    }
    Set<Parser> cached = followSetsCache.get(parser);
    if (cached != null) {
      return cached;
    }
    return Analyzer.of(root).computeFollowSetFor(parser);
  }

  private Set<Parser> computeFollowSetFor(Parser parser) {
    // If not in root cache, return empty
    return Collections.emptySet();
  }

  private Map<Parser, Set<Parser>> computeFollowSets() {
    Map<Parser, Set<Parser>> followSets = new HashMap<>();
    List<Parser> all = parsers();
    for (Parser p : all) {
      followSets.put(p, new HashSet<>());
    }

    boolean changed = true;
    int iterations = 0;
    int maxIterations = all.size() * all.size() + 1;
    while (changed && iterations++ < maxIterations) {
      changed = false;
      for (Parser p : all) {
        if (p instanceof TrimmingParser) {
          Parser del = p.getChildren().get(0);
          Parser left = p.getChildren().get(1);
          Parser right = p.getChildren().get(2);
          if (followSets.get(left).addAll(firstSet(left))) changed = true;
          if (followSets.get(left).addAll(firstSet(del))) changed = true;
          if (followSets.get(del).addAll(firstSet(right))) changed = true;
          if (followSets.get(del).addAll(followSets.get(p))) changed = true;
          if (followSets.get(right).addAll(firstSet(right))) changed = true;
          if (followSets.get(right).addAll(followSets.get(p))) changed = true;
        } else if (p instanceof SequentialParser) {
          List<Parser> children = p.getChildren();
          for (int i = 0; i < children.size(); i++) {
            Parser ci = children.get(i);
            boolean allSubsequentNullable = true;
            for (int j = i + 1; j < children.size(); j++) {
              Parser cj = children.get(j);
              if (followSets.get(ci).addAll(firstSet(cj))) {
                changed = true;
              }
              if (!isNullable(cj)) {
                allSubsequentNullable = false;
                break;
              }
            }
            if (allSubsequentNullable) {
              if (followSets.get(ci).addAll(followSets.get(p))) {
                changed = true;
              }
            }
          }
        } else if (p instanceof SeparatedRepeatingParser) {
          Parser del = p.getChildren().get(0);
          Parser sep = p.getChildren().get(1);
          if (followSets.get(del).addAll(firstSet(sep))) changed = true;
          if (followSets.get(del).addAll(followSets.get(p))) changed = true;
          if (followSets.get(sep).addAll(firstSet(del))) changed = true;
          if (isNullable(sep)) {
            if (followSets.get(del).addAll(firstSet(del))) changed = true;
          }
        } else if (p instanceof RepeatingParser) {
          Parser child = p.getChildren().get(0);
          if (followSets.get(child).addAll(firstSet(child))) changed = true;
          if (followSets.get(child).addAll(followSets.get(p))) changed = true;
        } else {
          for (Parser child : p.getChildren()) {
            if (followSets.get(child).addAll(followSets.get(p))) {
              changed = true;
            }
          }
        }
      }
    }

    Map<Parser, Set<Parser>> unmodifiable = new HashMap<>();
    for (Map.Entry<Parser, Set<Parser>> entry : followSets.entrySet()) {
      unmodifiable.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
    }
    return unmodifiable;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " of " + root;
  }
}
