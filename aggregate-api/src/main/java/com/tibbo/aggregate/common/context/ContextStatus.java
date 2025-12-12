package com.tibbo.aggregate.common.context;

public class ContextStatus
{
  private int status;
  private String comment;

  public ContextStatus()
  {

  }

  public ContextStatus(int status, String comment)
  {
    this.status = status;
    this.comment = comment;
  }

  public String getComment()
  {
    return comment;
  }

  public int getStatus()
  {
    return status;
  }

  public void setComment(String comment)
  {
    this.comment = comment;
  }

  public void setStatus(int status)
  {
    this.status = status;
  }
}
