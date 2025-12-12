package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.*;

public class ExpressionDumper
{
  
  public static void main(String[] args)
  {
    Log.start();
    try
    {
      // String exp = "((length('abcde') == 5) && (length('test') == 4))?" + "'xxx'" + ":'yyy'";
      String exp = "abs(-123)";
      
      ExpressionUtils.dump(exp);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
