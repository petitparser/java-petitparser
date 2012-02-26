package org.petitparser.context;

import org.petitparser.ParseContext;
import org.petitparser.ParseInput;
import org.petitparser.ParseOutput;

/**
 * An abstract parse context that forms the root of all parsers in this package.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class GenericParseContext<I, O> implements ParseContext<I, O> {

  protected final ParseInput<I> input;
  protected final ParseOutput<O> output;

  public GenericParseContext(ParseInput<I> input, ParseOutput<O> output) {
    this.input = input;
    this.output = output;
  }


  @Override
  public ParseContext<I, O> snapshot() {
    return new GenericParseContext<I, O>(input.snapshot(), output.snapshot());
  }

  @Override
  public void restore(ParseContext<I, O> state) {
    input.restore(state.input());
    output.restore(state.output());
  }

  @Override
  public ParseInput<I> input() {
    return input;
  }

  @Override
  public ParseOutput<O> output() {
    return output;
  }

}
