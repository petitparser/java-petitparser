package org.petitparser.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.petitparser.parser.DelegateParser;
import org.petitparser.parser.Parser;

/**
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class CompositeParser extends DelegateParser {

  public CompositeParser() {
    super(null);
    initializeFields(getFieldsAndInitializers());
    replace(getDelegate(), start());
  }

  public abstract Parser start();

  private Map<Field, Method> getFieldsAndInitializers() {
    Class<?> current = getClass();
    Map<Field, Method> fields = new HashMap<Field, Method>();
    while (!current.equals(CompositeParser.class)) {
      for (Field field : current.getDeclaredFields()) {
        Production annotation = field.getAnnotation(Production.class);
        if (annotation != null) {
          String name = annotation.value().isEmpty() ? field.getName()
              : annotation.value();
          Method method = null;
          try {
            method = current.getDeclaredMethod(name, new Class<?>[0]);
          } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Missing initializer for "
                + field.toString() + ".");
          }
          if (!method.getReturnType().equals(field.getType())) {
            throw new IllegalStateException(method.toString()
                + " expected to return " + field.getType().getName());
          }
          fields.put(field, method);
        }
      }
      current = current.getSuperclass();
    }
    return fields;
  }

  private void initializeFields(Map<Field, Method> fields) {
    Map<Field, DelegateParser> parsers = new HashMap<Field, DelegateParser>();
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
        parsers.get(entry.getKey()).setDelegate(parser);
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
