package com.tibbo.aggregate.common.expression;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tibbo.aggregate.common.expression.parser.ASTStart;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.structure.PinpointAware;
import com.tibbo.aggregate.common.util.PublicCloneable;

public class Expression implements Cloneable, PublicCloneable, PinpointAware
{
  public static final String REFERENCE_START = "{";
  
  public static final String REFERENCE_END = "}";
  
  private final String text;
  
  private transient ASTStart rootNode;

  @Nullable
  private transient Pinpoint pinpoint = null;
  
  /**
   * Creates Expression that consists of single reference
   * 
   * @param reference
   *          Reference
   */
  public Expression(Reference reference)
  {
    this(REFERENCE_START + reference.getImage() + REFERENCE_END);
  }

  public Expression(String text)
  {
    if (text == null)
    {
      throw new NullPointerException();
    }

    this.text = text;
  }

  public String getText()
  {
    return text;
  }
  
  public ASTStart getRootNode()
  {
    return rootNode;
  }
  
  public void setRootNode(ASTStart rootNode)
  {
    this.rootNode = rootNode;
  }

  @Override
  public String toString()
  {
    return text;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    Expression other = (Expression) obj;
    if (text == null)
    {
      if (other.text != null)
      {
        return false;
      }
    }
    else if (!text.equals(other.text))
    {
      return false;
    }
    return true;
  }
  
  @Override
  public Expression clone()
  {
    try
    {
      return (Expression) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }

  @Override
  public void assignPinpoint(Pinpoint pinpoint)
  {
    checkState(this.pinpoint == null, "This '%s' already contains pinpoint '%s' but " +
        "somebody attempted to assign another one: '%s'", this, this.pinpoint, pinpoint);
    this.pinpoint = pinpoint;
  }

  @Override
  public Optional<Pinpoint> obtainPinpoint()
  {
    return Optional.ofNullable(pinpoint);
  }
}
