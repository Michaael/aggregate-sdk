package com.tibbo.aggregate.common.communication;

import java.nio.channels.*;

import com.tibbo.aggregate.common.util.*;

public abstract class SimpleCommandParser<C extends Command> extends BufferedCommandParser<C>
{
  private Byte startChar;
  private byte endChar;
  private Byte endChar2;
  private boolean needBoth;
  
  private boolean started;
  private boolean waitingEndChar2;
  private boolean full;
  
  public SimpleCommandParser(ReadableByteChannel channel, Byte startChar, byte endChar)
  {
    super(channel);
    init(startChar, endChar);
  }
  
  public SimpleCommandParser(ReadableByteChannel channel, Byte startChar, byte endChar, Byte endChar2, boolean needBoth)
  {
    super(channel);
    init(startChar, endChar);
    this.endChar2 = endChar2;
    this.needBoth = needBoth;
  }
  
  abstract protected C createCommandFromBufferContent() throws SyntaxErrorException;
  
  private void init(Byte startChar, byte endChar)
  {
    this.startChar = startChar;
    this.endChar = endChar;
    
    clearCommand();
  }
  
  public void clearCommand()
  {
    started = (startChar == null);
    waitingEndChar2 = false;
    full = false;
    reset();
  }
  
  @Override
  protected C buildCommand() throws SyntaxErrorException
  {
    if (isFull())
    {
      C command = createCommandFromBufferContent();
      
      clearCommand();
      
      return command;
    }
    else
    {
      return null;
    }
  }
  
  @Override
  protected boolean commandEnded(byte cur)
  {
    if (startChar != null && cur == startChar)
    {
      started = true;
      reset();
    }
    else
    {
      if (started)
      {
        if (waitingEndChar2)
        {
          if (cur == endChar2)
          {
            full = true;
            return true;
          }
        }
        else
        {
          if (cur == endChar || (endChar2 != null && !needBoth && cur == endChar2))
          {
            if (endChar2 != null && needBoth)
            {
              waitingEndChar2 = true;
              full = false;
              return false;
            }
            else
            {
              full = true;
              return true;
            }
          }
          else
          {
            addData(cur);
          }
        }
      }
    }
    
    return false;
  }
  
  public boolean isFull()
  {
    return full;
  }
  
}
