package com.tibbo.aggregate.common.datatable;

public class QueryCondition
{
  //Operators
  public static final int EQ = 1; // =
  public static final int GT = 2; // >
  public static final int LT = 4; // <
  public static final int NE = 8; // !=
  public static final int GE = GT | EQ; // >=
  public static final int LE = LT | EQ; // <=

  private String field;
  private Object value;
  private int operator = EQ;

  public QueryCondition(String field, Object value, int operator)
  {
    this.field = field;
    this.value = value;
    this.operator = operator;
  }

  public QueryCondition(String field, Object value)
  {
    this(field, value, EQ);
  }

  public String getField()
  {
    return field;
  }

  public Object getValue()
  {
    return value;
  }

  public int getOperator()
  {
    return operator;
  }
}
