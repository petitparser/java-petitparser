package org.petitparser;

import java.util.Iterator;

/**
 * The input state of the parse.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public interface ParseInput<I> extends ParseState<ParseInput<I>>, Iterator<I> {

}
