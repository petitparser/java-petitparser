package org.petitparser.grammar.smalltalk;

import org.junit.Test;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

/**
 * Tests {@link SmalltalkDefinition}.
 */
public class SmalltalkDefinitionTest {

  private final SmalltalkDefinition smalltalk = new SmalltalkDefinition();

  private <T> T validate(String source, String production) {
    Parser parser = smalltalk.build(production).end();
    Result result = parser.parse(source);
    return result.get();
  }

  @Test
  public void testArray1() {
    validate("{}", "array");
  }

  @Test
  public void testArray2() {
    validate("{self foo}", "array");
  }

  @Test
  public void testArray3() {
    validate("{self foo. self bar}", "array");
  }

  @Test
  public void testArray4() {
    validate("{self foo. self bar.}", "array");
  }

  @Test
  public void testAssignment1() {
    validate("1", "expression");
  }

  @Test
  public void testAssignment2() {
    validate("a := 1", "expression");
  }

  @Test
  public void testAssignment3() {
    validate("a := b := 1", "expression");
  }

  @Test
  public void testAssignment6() {
    validate("a := (b := c)", "expression");
  }

  @Test
  public void testComment1() {
    validate("1\"one\"+2", "expression");
  }

  @Test
  public void testComment2() {
    validate("1 \"one\" +2", "expression");
  }

  @Test
  public void testComment3() {
    validate("1\"one\"+\"two\"2", "expression");
  }

  @Test
  public void testComment4() {
    validate("1\"one\"\"two\"+2", "expression");
  }

  @Test
  public void testComment5() {
    validate("1\"one\" \"two\"+2", "expression");
  }

  @Test
  public void testMethod1() {
    validate("negated ^ 0 - self", "method");
  }

  @Test
  public void testMethod2() {
    validate("   negated ^ 0 - self", "method");
  }

  @Test
  public void testMethod3() {
    validate(" negated ^ 0 - self  ", "method");
  }

  @Test
  public void testSequence1() {
    validate("| a | 1 . 2", "sequence");
  }

  @Test
  public void testStatements1() {
    validate("1", "sequence");
  }

  @Test
  public void testStatements2() {
    validate("1 . 2", "sequence");
  }

  @Test
  public void testStatements3() {
    validate("1 . 2 . 3", "sequence");
  }

  @Test
  public void testStatements4() {
    validate("1 . 2 . 3 .", "sequence");
  }

  @Test
  public void testStatements5() {
    validate("1 . . 2", "sequence");
  }

  @Test
  public void testStatements6() {
    validate("1. 2", "sequence");
  }

  @Test
  public void testStatements7() {
    validate(". 1", "sequence");
  }

  @Test
  public void testStatements8() {
    validate(".1", "sequence");
  }

  @Test
  public void testTemporaries1() {
    validate("| a |", "sequence");
  }

  @Test
  public void testTemporaries2() {
    validate("| a b |", "sequence");
  }

  @Test
  public void testTemporaries3() {
    validate("| a b c |", "sequence");
  }

  @Test
  public void testVariable1() {
    validate("trueBinding", "primary");
  }

  @Test
  public void testVariable2() {
    validate("falseBinding", "primary");
  }

  @Test
  public void testVariable3() {
    validate("nilly", "primary");
  }

  @Test
  public void testVariable4() {
    validate("selfish", "primary");
  }

  @Test
  public void testVariable5() {
    validate("supernanny", "primary");
  }

  @Test
  public void testVariable6() {
    validate("super_nanny", "primary");
  }

  @Test
  public void testVariable7() {
    validate("__gen_var_123__", "primary");
  }

  @Test
  public void testArgumentsBlock1() {
    validate("[ :a | ]", "block");
  }

  @Test
  public void testArgumentsBlock2() {
    validate("[ :a :b | ]", "block");
  }

  @Test
  public void testArgumentsBlock3() {
    validate("[ :a :b :c | ]", "block");
  }

  @Test
  public void testComplexBlock1() {
    validate("[ :a | | b | c ]", "block");
  }

  @Test
  public void testComplexBlock2() {
    validate("[:a||b|c]", "block");
  }

  @Test
  public void testSimpleBlock1() {
    validate("[ ]", "block");
  }

  @Test
  public void testSimpleBlock2() {
    validate("[ nil ]", "block");
  }

  @Test
  public void testSimpleBlock3() {
    validate("[ :a ]", "block");
  }

  @Test
  public void testStatementBlock1() {
    validate("[ nil ]", "block");
  }

  @Test
  public void testStatementBlock2() {
    validate("[ | a | nil ]", "block");
  }

  @Test
  public void testStatementBlock3() {
    validate("[ | a b | nil ]", "block");
  }

  @Test
  public void testArrayLiteral1() {
    validate("#()", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral10() {
    validate("#((1 2) #(1 2 3))", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral11() {
    validate("#([1 2] #[1 2 3])", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral2() {
    validate("#(1)", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral3() {
    validate("#(1 2)", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral4() {
    validate("#(true false nil)", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral5() {
    validate("#($a)", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral6() {
    validate("#(1.2)", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral7() {
    validate("#(size #at: at:put: #'==')", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral8() {
    validate("#('baz')", "arrayLiteral");
  }

  @Test
  public void testArrayLiteral9() {
    validate("#((1) 2)", "arrayLiteral");
  }

  @Test
  public void testByteLiteral1() {
    validate("#[]", "byteLiteral");
  }

  @Test
  public void testByteLiteral2() {
    validate("#[0]", "byteLiteral");
  }

  @Test
  public void testByteLiteral3() {
    validate("#[255]", "byteLiteral");
  }

  @Test
  public void testByteLiteral4() {
    validate("#[ 1 2 ]", "byteLiteral");
  }

  @Test
  public void testByteLiteral5() {
    validate("#[ 2r1010 8r77 16rFF ]", "byteLiteral");
  }

  @Test
  public void testCharLiteral1() {
    validate("$a", "charLiteral");
  }

  @Test
  public void testCharLiteral2() {
    validate("$ ", "charLiteral");
  }

  @Test
  public void testCharLiteral3() {
    validate("$$", "charLiteral");
  }

  @Test
  public void testNumberLiteral1() {
    validate("0", "numberLiteral");
  }

  @Test
  public void testNumberLiteral10() {
    validate("10r10", "numberLiteral");
  }

  @Test
  public void testNumberLiteral11() {
    validate("8r777", "numberLiteral");
  }

  @Test
  public void testNumberLiteral12() {
    validate("16rAF", "numberLiteral");
  }

  @Test
  public void testNumberLiteral2() {
    validate("0.1", "numberLiteral");
  }

  @Test
  public void testNumberLiteral3() {
    validate("123", "numberLiteral");
  }

  @Test
  public void testNumberLiteral4() {
    validate("123.456", "numberLiteral");
  }

  @Test
  public void testNumberLiteral5() {
    validate("-0", "numberLiteral");
  }

  @Test
  public void testNumberLiteral6() {
    validate("-0.1", "numberLiteral");
  }

  @Test
  public void testNumberLiteral7() {
    validate("-123", "numberLiteral");
  }

  @Test
  public void testNumberLiteral8() {
    validate("-123", "numberLiteral");
  }

  @Test
  public void testNumberLiteral9() {
    validate("-123.456", "numberLiteral");
  }

  @Test
  public void testSpecialLiteral1() {
    validate("true", "trueLiteral");
  }

  @Test
  public void testSpecialLiteral2() {
    validate("false", "falseLiteral");
  }

  @Test
  public void testSpecialLiteral3() {
    validate("nil", "nilLiteral");
  }

  @Test
  public void testStringLiteral1() {
    validate("''", "stringLiteral");
  }

  @Test
  public void testStringLiteral2() {
    validate("'ab'", "stringLiteral");
  }

  @Test
  public void testStringLiteral3() {
    validate("'ab''cd'", "stringLiteral");
  }

  @Test
  public void testSymbolLiteral1() {
    validate("#foo", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral2() {
    validate("#+", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral3() {
    validate("#key:", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral4() {
    validate("#key:value:", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral5() {
    validate("#'testing-result'", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral6() {
    validate("#__gen__binding", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral7() {
    validate("# fucker", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral8() {
    validate("##fucker", "symbolLiteral");
  }

  @Test
  public void testSymbolLiteral9() {
    validate("## fucker", "symbolLiteral");
  }

  @Test
  public void testBinaryExpression1() {
    validate("1 + 2", "expression");
  }

  @Test
  public void testBinaryExpression2() {
    validate("1 + 2 + 3", "expression");
  }

  @Test
  public void testBinaryExpression3() {
    validate("1 // 2", "expression");
  }

  @Test
  public void testBinaryExpression4() {
    validate("1 -- 2", "expression");
  }

  @Test
  public void testBinaryExpression5() {
    validate("1 ==> 2", "expression");
  }

  @Test
  public void testBinaryMethod1() {
    validate("+ a", "method");
  }

  @Test
  public void testBinaryMethod2() {
    validate("+ a | b |", "method");
  }

  @Test
  public void testBinaryMethod3() {
    validate("+ a b", "method");
  }

  @Test
  public void testBinaryMethod4() {
    validate("+ a | b | c", "method");
  }

  @Test
  public void testBinaryMethod5() {
    validate("-- a", "method");
  }

  @Test
  public void testCascadeExpression1() {
    validate("1 abs; negated", "expression");
  }

  @Test
  public void testCascadeExpression2() {
    validate("1 abs negated; raisedTo: 12; negated", "expression");
  }

  @Test
  public void testCascadeExpression3() {
    validate("1 + 2; - 3", "expression");
  }

  @Test
  public void testKeywordExpression1() {
    validate("1 to: 2", "expression");
  }

  @Test
  public void testKeywordExpression2() {
    validate("1 to: 2 by: 3", "expression");
  }

  @Test
  public void testKeywordExpression3() {
    validate("1 to: 2 by: 3 do: 4", "expression");
  }

  @Test
  public void testKeywordMethod1() {
    validate("to: a", "method");
  }

  @Test
  public void testKeywordMethod2() {
    validate("to: a do: b | c |", "method");
  }

  @Test
  public void testKeywordMethod3() {
    validate("to: a do: b by: c d", "method");
  }

  @Test
  public void testKeywordMethod4() {
    validate("to: a do: b by: c | d | e", "method");
  }

  @Test
  public void testUnaryExpression1() {
    validate("1 abs", "expression");
  }

  @Test
  public void testUnaryExpression2() {
    validate("1 abs negated", "expression");
  }

  @Test
  public void testUnaryMethod1() {
    validate("abs", "method");
  }

  @Test
  public void testUnaryMethod2() {
    validate("abs | a |", "method");
  }

  @Test
  public void testUnaryMethod3() {
    validate("abs a", "method");
  }

  @Test
  public void testUnaryMethod4() {
    validate("abs | a | b", "method");
  }

  @Test
  public void testUnaryMethod5() {
    validate("abs | a |", "method");
  }

  @Test
  public void testPragma1() {
    validate("method <foo>", "method");
  }

  @Test
  public void testPragma10() {
    validate("method <foo: bar>", "method");
  }

  @Test
  public void testPragma11() {
    validate("method <foo: true>", "method");
  }

  @Test
  public void testPragma12() {
    validate("method <foo: false>", "method");
  }

  @Test
  public void testPragma13() {
    validate("method <foo: nil>", "method");
  }

  @Test
  public void testPragma14() {
    validate("method <foo: ()>", "method");
  }

  @Test
  public void testPragma15() {
    validate("method <foo: #()>", "method");
  }

  @Test
  public void testPragma16() {
    validate("method < + 1 >", "method");
  }

  @Test
  public void testPragma2() {
    validate("method <foo> <bar>", "method");
  }

  @Test
  public void testPragma3() {
    validate("method | a | <foo>", "method");
  }

  @Test
  public void testPragma4() {
    validate("method <foo> | a |", "method");
  }

  @Test
  public void testPragma5() {
    validate("method <foo> | a | <bar>", "method");
  }

  @Test
  public void testPragma6() {
    validate("method <foo: 1>", "method");
  }

  @Test
  public void testPragma7() {
    validate("method <foo: 1.2>", "method");
  }

  @Test
  public void testPragma8() {
    validate("method <foo: 'bar'>", "method");
  }

  @Test
  public void testPragma9() {
    validate("method <foo: #'bar'>", "method");
  }

}
