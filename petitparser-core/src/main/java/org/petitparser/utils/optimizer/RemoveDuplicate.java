package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;

import java.util.Set;

/**
 * Backward compatibility alias for {@link RemoveDuplicateRule}.
 */
public class RemoveDuplicate extends RemoveDuplicateRule {

  public RemoveDuplicate() {
    super();
  }

  public RemoveDuplicate(Set<Parser> uniques) {
    super(uniques);
  }
}
