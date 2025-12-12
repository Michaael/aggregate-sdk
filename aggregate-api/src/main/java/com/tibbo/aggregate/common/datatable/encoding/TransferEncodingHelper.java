package com.tibbo.aggregate.common.datatable.encoding;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.protocol.*;
import com.tibbo.aggregate.common.util.*;

public class TransferEncodingHelper
{
  public static final char ESCAPE_CHAR = '%';
  public static final char SEPARATOR_CHAR = '/';
  public static final char START_CHAR = '^';
  public static final char END_CHAR = '$';
  
  public static final int KILO = 1024;
  public static final int KB = KILO;
  public static final int MB = KB * KILO;
  public static final int GB = MB * KILO;
  public static final long TB = (long) GB * KILO;
  
  // TODO: Check all usages of LARGE_DATA_SIZE for improvements (AGG-5534)
  public static final int LARGE_DATA_SIZE = MB * 50;
  
  private static final Map<Character, Character> DIRECT = new HashMap();
  private static final Map<Character, Character> REVERSE = new HashMap();
  
  static
  {
    DIRECT.put(ESCAPE_CHAR, ESCAPE_CHAR);
    DIRECT.put(ProtocolCommandBuilder.CLIENT_COMMAND_SEPARATOR.charAt(0), SEPARATOR_CHAR);
    DIRECT.put(DataTableUtils.ELEMENT_START, DataTableUtils.ELEMENT_VISIBLE_START);
    DIRECT.put(DataTableUtils.ELEMENT_END, DataTableUtils.ELEMENT_VISIBLE_END);
    DIRECT.put(DataTableUtils.ELEMENT_NAME_VALUE_SEPARATOR, DataTableUtils.ELEMENT_VISIBLE_NAME_VALUE_SEPARATOR);
    DIRECT.put((char) AggreGateCommand.START_CHAR, START_CHAR);
    DIRECT.put((char) AggreGateCommand.END_CHAR, END_CHAR);
    
    REVERSE.put(ESCAPE_CHAR, ESCAPE_CHAR);
    REVERSE.put(SEPARATOR_CHAR, ProtocolCommandBuilder.CLIENT_COMMAND_SEPARATOR.charAt(0));
    REVERSE.put(DataTableUtils.ELEMENT_VISIBLE_START, DataTableUtils.ELEMENT_START);
    REVERSE.put(DataTableUtils.ELEMENT_VISIBLE_END, DataTableUtils.ELEMENT_END);
    REVERSE.put(DataTableUtils.ELEMENT_VISIBLE_NAME_VALUE_SEPARATOR, DataTableUtils.ELEMENT_NAME_VALUE_SEPARATOR);
    REVERSE.put(START_CHAR, (char) AggreGateCommand.START_CHAR);
    REVERSE.put(END_CHAR, (char) AggreGateCommand.END_CHAR);
  }
  
  public static String encode(String s, Map<Character, Character> mapping, Integer encodeLevel)
  {
    if (s == null)
    {
      return null;
    }
    
    return new String(encode(s, null, mapping, encodeLevel));
  }
  
  public static StringBuilder encode(String source, StringBuilder result, Map<Character, Character> mapping, Integer encodeLevel)
  {
    if (source == null)
      return null;
    
    if (result == null)
      result = new StringBuilder();
    
    for (int i = 0; i < source.length(); i++)
      encodeChar(mapping, source.charAt(i), result, encodeLevel);
    
    return result;
  }
  
  public static void encodeChar(char c, StringBuilder sb)
  {
    encodeChar(DIRECT, c, sb, 0);
  }
  
  public static void encodeChar(Map<Character, Character> mapping, char c, StringBuilder sb, Integer encodeLevel)
  {
    if (mapping.containsKey(c))
    {
      double recalcedEncode;
      
      if (c == ESCAPE_CHAR)
        recalcedEncode = Math.pow(2, encodeLevel) - 1;
      else
        recalcedEncode = Math.pow(2, encodeLevel - 1);
      
      for (int i = 0; i < recalcedEncode; i++)
      {
        sb.append(ESCAPE_CHAR);
      }
      
      sb.append(mapping.get(c));
    }
    else
    {
      sb.append(c);
    }
  }
  
  public static String encode(String strVal)
  {
    return encode(strVal, DIRECT, 1);
  }
  
  public static String encode(String strVal, Integer encodeLevel)
  {
    return encode(strVal, DIRECT, encodeLevel);
  }
  
  public static StringBuilder encode(String strFrom, StringBuilder strTo, Integer encodeLevel)
  {
    return encode(strFrom, strTo, DIRECT, encodeLevel);
  }
  
  public static String decode(String s, Map<Character, Character> mapping)
  {
    if (s == null)
    {
      return null;
    }
    
    int len = s.length();
    
    StringBuilder out = new StringBuilder(len);
    
    for (int i = 0; i < len; i++)
    {
      char c = s.charAt(i);
      
      if (c == ESCAPE_CHAR && i < len - 1)
      {
        char next = s.charAt(i + 1);
        Character orig = mapping.get(next);
        
        if (orig != null)
        {
          out.append(orig.charValue());
          i += 1;
        }
        else
        {
          // Failover code
          out.append(c);
          out.append(next);
        }
      }
      else
      {
        out.append(c);
      }
    }
    
    return out.toString();
  }
  
  public static String decode(String s)
  {
    return decode(s, REVERSE);
  }
  
  public static String decode(StringWrapper s)
  {
    return decode((s != null) ? s.getString() : null, REVERSE);
  }
  
}
