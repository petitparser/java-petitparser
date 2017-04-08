package org.petitparser.tools;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper to conveniently define and build complex, recursive grammars using plain Java code.
 * <p>
 * To create a new grammar definition subclass {@link GrammarDefinition}. For every production call
 * {@link GrammarDefinition#def(String, Parser)} giving the parsers a name. The start production
 * should be named 'start'.
 * <p>
 * To refer to other productions use {@link GrammarDefinition#ref(String)}. To redefine or attach
 * actions to productions use {@link GrammarDefinition#redef(String, Function)}, {@link
 * GrammarDefinition#redef(String, org.petitparser.parser.Parser)} and {@link
 * GrammarDefinition#action(String, Function)}.
 * <p>
 * To build the resulting grammar call {@link GrammarDefinition#build()}, or wrap it in the class
 * {@link GrammarParser}.
 */
public class GrammarDefinition {

  private final Map<String, Parser> parsers = new HashMap<>();

  /**
   * Returns a reference to the production with the given {@code name}.
   */
  protected final Parser ref(String name) {
    return new Reference(name);
  }

  /**
   * Defines a production with a {@code name} and a {@code parser}.
   */
  protected final void def(String name, Parser parser) {
    if (parsers.containsKey(name)) {
      throw new IllegalStateException("Duplicate production: " + name);
    }
    parsers.put(Objects.requireNonNull(name), Objects.requireNonNull(parser));
  }

  /**
   * Redefines an existing production with a {@code name} and a new {@code parser}.
   */
  protected final void redef(String name, Parser parser) {
    if (!parsers.containsKey(name)) {
      throw new IllegalStateException("Undefined production: " + name);
    }
    parsers.put(Objects.requireNonNull(name), Objects.requireNonNull(parser));
  }

  /**
   * Redefines an existing production with a {@code name} and a {@code function} producing a new
   * parser. Only call this method during initialization.
   */
  protected final void redef(String name, Function<Parser, Parser> function) {
    if (!parsers.containsKey(name)) {
      throw new IllegalStateException("Undefined production: " + name);
    }
    redef(name, function.apply(parsers.get(name)));
  }

  /**
   * Attaches an action {@code function} to an existing production {@code name}. Only call this
   * method during initialization.
   */
  protected final <S, T> void action(String name, Function<S, T> function) {
    redef(name, parser -> parser.map(function));
  }

  /**
   * Builds a parser starting from the production {@code "start"}.
   */
  public Parser build() {
    return build("start");
  }

  /**
   * Builds a parser starting from the provided production {@code name}.
   */
  public Parser build(String name) {
    return resolve(new Reference(name));
  }

  private final Parser resolve(Reference reference) {
    Map<Reference, Parser> mapping = new HashMap<>();
    List<Parser> todo = new ArrayList<>();
    todo.add(dereference(mapping, reference));
    Set<Parser> seen = new HashSet<>(todo);
    while (!todo.isEmpty()) {
      Parser parent = todo.remove(todo.size() - 1);
      for (Parser child : parent.getChildren()) {
        if (child instanceof Reference) {
          Parser referenced = dereference(mapping, (Reference) child);
          parent.replace(child, referenced);
          child = referenced;
        }
        if (!seen.contains(child)) {
          seen.add(child);
          todo.add(child);
        }
      }
    }
    return mapping.get(reference);
  }

  private Parser dereference(Map<Reference, Parser> mapping, Reference reference) {
    Parser parser = mapping.get(reference);
    if (parser == null) {
      List<Reference> references = new ArrayList<>();
      references.add(reference);
      parser = reference.resolve();
      while (parser instanceof Reference) {
        Reference otherReference = (Reference) parser;
        if (references.contains(otherReference)) {
          throw new IllegalStateException("Recursive references detected: " + String.join(", ",
              references.stream().map(ref -> ref.name).collect(Collectors.joining(", "))));
        }
        references.add(otherReference);
        parser = otherReference.resolve();
      }
      for (Reference otherReference : references) {
        mapping.put(otherReference, parser);
      }
    }
    return parser;
  }

  private class Reference extends Parser {

    private final String name;

    private Reference(String name) {
      this.name = Objects.requireNonNull(name);
    }

    private Parser resolve() {
      if (!parsers.containsKey(name)) {
        throw new IllegalStateException("Unknown parser reference: " + name);
      }
      return parsers.get(name);
    }

    @Override
    public Result parseOn(Context context) {
      throw new UnsupportedOperationException("References cannot be parsed.");
    }

    @Override
    public Parser copy() {
      throw new UnsupportedOperationException("References cannot be copied.");
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (other == null || getClass() != other.getClass()) {
        return false;
      }
      Reference reference = (Reference) other;
      return name.equals(reference.name);
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }
  }
}