/**
 * This package contains a complete implementation of <a href="http://json.org/">JSON</a>.
 *
 * {@link org.petitparser.grammar.json.JsonParserDefinition} builds nested Java objects from a given
 * JSON string. Consider the following code:
 *
 * <pre>
 * Parser json = new JsonParser();
 * Object result = json.parse('{"a": 1, "b": [2, 3.4], "c": false}');
 * System.out.println(result.value);  // {a: 1, b: [2, 3.4], c: false}
 * </pre>
 *
 * The grammar definition {@link org.petitparser.grammar.json.JsonGrammarDefinition} can be
 * subclassed to construct other objects.
 */
package org.petitparser.grammar.json;
