package org.petitparser.grammar.xml.ast;

import com.google.common.base.Objects;

/**
 * XML processing instruction.
 */
public class XmlProcessing extends XmlData {

  private final String target;

  public XmlProcessing(String target, String data) {
    super(data);
    this.target = target;
  }

  public String getTarget() {
    return target;
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append("<?").append(getTarget()).append(getData()).append("?>");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj) || getClass() != obj.getClass())
      return false;
    XmlProcessing other = (XmlProcessing) obj;
    return target.equals(other.target);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), getTarget());
  }

}
