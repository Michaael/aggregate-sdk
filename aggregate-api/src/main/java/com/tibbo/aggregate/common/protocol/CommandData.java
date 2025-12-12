package com.tibbo.aggregate.common.protocol;

import java.nio.*;

public class CommandData
{
  /**
   * Максимальный размер команды (100MB).
   * Команды больше этого размера будут отклонены для защиты от OutOfMemoryError.
   */
  private static final int MAX_COMMAND_SIZE = 100 * 1024 * 1024;
  
  private byte startChar;

  private Header header = new Header();
  private Body body = new Body();

  public CommandData(byte startChar)
  {
    this.startChar = startChar;
  }

  public void addNextByte(byte nextByte)
  {
    if (!header.isCompleted())
    {
      header.append(nextByte);
      if (header.isCompleted())
      {
        body.resetFor(header.getLength());
      }
    }
    else if (!body.isCompleted())
    {
      body.append(nextByte);
    }
  }

  public boolean isCompleted()
  {
    return header.isCompleted() && body.isCompleted();
  }

  public void reset()
  {
    header.reset();
    body.reset();
  }

  public Body getBody()
  {
    return body;
  }

  public class Header
  {
    private static final byte WAITING_FOR_START = 0;
    private static final byte READING_LENGTH = 1;

    public static final int LENGTH_SIZE = 4;

    ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_SIZE);
    private byte stage = WAITING_FOR_START;
    private Integer length = null;

    public boolean isCompleted()
    {
      return this.length != null;
    }

    public void append(byte nextByte)
    {
      switch (stage)
      {
        case WAITING_FOR_START:
          if (nextByte == CommandData.this.startChar)
          {
            stage = READING_LENGTH;
          }
          break;
        case READING_LENGTH:
          lengthBuffer.put(nextByte);
          if (!lengthBuffer.hasRemaining())
          {
            length = lengthBuffer.getInt(0);
          }
          break;
      }
    }

    public void reset()
    {
      length = null;
      stage = WAITING_FOR_START;
      lengthBuffer.rewind();
    }

    public int getLength()
    {
      return length;
    }
  }

  public class Body extends Header
  {
    protected Byte type = null;
    protected ByteBuffer contents;

    @Override
    public void append(byte nextByte)
    {
      if (type == null)
        type = nextByte;
      else
        contents.put(nextByte);
    }

    @Override
    public boolean isCompleted()
    {
      return contents != null && !contents.hasRemaining();
    }

    public void resetFor(int length)
    {
      // Проверка максимального размера команды
      if (length > MAX_COMMAND_SIZE)
      {
        throw new IllegalArgumentException("Command size (" + length + 
            " bytes) exceeds maximum allowed size (" + MAX_COMMAND_SIZE + " bytes)");
      }
      
      int typeByte = 1;
      int bodyLength = length - typeByte;
      
      // Проверяем, что размер тела команды положительный
      if (bodyLength < 0)
      {
        throw new IllegalArgumentException("Invalid command length: " + length);
      }
      
      contents = ByteBuffer.allocate(bodyLength);
      this.type = null;
    }

    public Byte getType()
    {
      return type;
    }

    public byte[] getContents()
    {
      return contents.array();
    }
  }
}
