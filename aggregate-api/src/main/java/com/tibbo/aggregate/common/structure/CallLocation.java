package com.tibbo.aggregate.common.structure;

/**
 * A kind of "stack floor" in a typical stacktrace but adapted to AggreGate's Unified Data Model interactions
 *
 * @author Vladimir Plizga
 * @since AGG-10879
 */
public class CallLocation
{
  private final CallKind kind;
  private final int line;
  private final int column;
  private final String callText;

  public CallLocation(CallKind kind, int line, int column, String callText)
  {
    this.kind = kind;
    this.line = line;
    this.column = column;
    this.callText = callText;
  }

  public CallKind getKind()
  {
    return kind;
  }

  public int getLine()
  {
    return line;
  }

  public int getColumn()
  {
    return column;
  }

  public String getCallText()
  {
    return callText;
  }

  @Override
  public String toString()
  {
    return "CallLocation{" +
        "kind=" + kind +
        ", line=" + line +
        ", column=" + column +
        ", callText='" + callText + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    CallLocation that = (CallLocation) o;

    if (line != that.line)
      return false;
    if (column != that.column)
      return false;
    if (kind != that.kind)
      return false;
    return callText.equals(that.callText);
  }

  @Override
  public int hashCode()
  {
    int result = kind.hashCode();
    result = 31 * result + line;
    result = 31 * result + column;
    result = 31 * result + callText.hashCode();
    return result;
  }
}
