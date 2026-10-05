package org.petitparser.utils;

import org.petitparser.parser.Parser;

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

  @Override
  public String toString() {
    return getClass().getSimpleName() + " of " + root;
  }
}
