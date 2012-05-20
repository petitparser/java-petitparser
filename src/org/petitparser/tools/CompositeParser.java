package org.petitparser.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.petitparser.parser.DelegateParser;
import org.petitparser.parser.FailureParser;
import org.petitparser.parser.Parser;
import org.petitparser.utils.Transformations;

import com.google.common.collect.Maps;

/**
 * Helper to compose complex grammars from various primitive parsers. To create
 * a new complex grammar subclass {@link CompositeParser}. For every production
 * create a method that returns its parser. Additionally every production
 * requires a field of the same name, same type and an annotation
 * {@link Production}, otherwise the production is not cached and cannot be used
 * in recursive grammars. Productions should refer to each other by reading the
 * respective fields.
 *
 * <pre>
 * &#064;Production
 * Parser identifier;
 *
 * Parser identifier() {
 *   return letter().seq(word().star()).flatten();
 * }
 * </pre>
 *
 * The start production is returned from {@link #start()}.
 *
 * <pre>
 * &#064;Override
 * Parser start() {
 *   return identifier.end();
 * }
 * </pre>
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class CompositeParser extends DelegateParser {

  /**
   * Default constructor that triggers the code building the composite grammar.
   */
  public CompositeParser() {
    super(new FailureParser("No parser defined"));
    initializeFields(getProductions());
    replace(getDelegate(), Transformations.removeDelegates(start()));
  }

  /**
   * Returns the start production of the grammar.
   */
  protected abstract Parser start();

  private Map<Field, Method> getProductions() {
    Class<?> current = getClass();
    Map<Field, Method> productions = Maps.newHashMap();
    while (!CompositeParser.class.equals(current)) {
      for (Method method : current.getDeclaredMethods()) {
        Production production = method.getAnnotation(Production.class);
        if (production != null) {
          if (method.getParameterTypes().length > 0) {
            throw new IllegalStateException(method.toString()
                + " should not expect arguments");
          }
          String name = production.value().isEmpty()
              ? method.getName() : production.value();
          Field field = lookupField(current, name);
          if (!field.getType().isAssignableFrom(method.getReturnType())) {
            throw new IllegalStateException(method.toString()
                + " is not assignable to " + field.getName());
          }
          productions.put(field, method);
        }
      }
      current = current.getSuperclass();
    }
    return productions;
  }

  private Field lookupField(Class<?> start, String name) {
    try {
      Field field = start.getDeclaredField(name);
      field.setAccessible(true);
      return field;
    } catch (SecurityException exception) {
      throw new IllegalStateException(name + " cannot be accessed in " + start.getName());
    } catch (NoSuchFieldException exception) {
      throw new IllegalStateException(name + " is not a valid field in " + start.getName());
    }
  }

  private void initializeFields(Map<Field, Method> fields) {
    Map<Field, DelegateParser> parsers = Maps.newHashMap();
    for (Field field : fields.keySet()) {
      try {
        DelegateParser parser = new DelegateParser();
        parsers.put(field, parser);
        field.setAccessible(true);
        field.set(this, parser);
      } catch (IllegalArgumentException exception) {
        exception.printStackTrace();
      } catch (IllegalAccessException exception) {
        exception.printStackTrace();
      }
    }
    for (Map.Entry<Field, Method> entry : fields.entrySet()) {
      try {
        entry.getValue().setAccessible(true);
        Parser parser = (Parser) entry.getValue().invoke(this);
        parsers.get(entry.getKey()).replace(parsers.get(entry.getKey()).getDelegate(), parser);
        entry.getKey().set(this, parser);
      } catch (IllegalArgumentException exception) {
        exception.printStackTrace();
      } catch (IllegalAccessException exception) {
        exception.printStackTrace();
      } catch (InvocationTargetException exception) {
        exception.printStackTrace();
      }
    }
  }

}
