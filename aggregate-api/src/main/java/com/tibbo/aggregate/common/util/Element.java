package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;

public class Element implements StringEncodable
{
  private String name;
  private String value;
  private StringEncodable encodable;
  private FieldFormat fieldFormat;
  private Object fieldValue;
  
  public Element(String name, String value)
  {
    this.name = name;
    this.value = value;
  }
  
  public Element(String name, StringEncodable encodable)
  {
    this.name = name;
    this.encodable = encodable;
  }
  
  public Element(String name, FieldFormat ff, Object fieldValue)
  {
    this.name = name;
    this.fieldFormat = ff;
    this.fieldValue = fieldValue;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public String encode(ClassicEncodingSettings settings)
  {
    return new String(encode(new StringBuilder(), settings, false, 0));
  }
  
  public StringBuilder encode(StringBuilder sb, ClassicEncodingSettings settings)
  {
    return encode(sb, settings, false, 0);
  }
  
  public StringBuilder encode(StringBuilder sb, ClassicEncodingSettings settings, Integer encodeLevel)
  {
    return encode(sb, settings, false, encodeLevel);
  }
  
  @Override
  public StringBuilder encode(StringBuilder sb, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    Boolean useVisibleSeparators = false;
    
    if (settings != null)
      useVisibleSeparators = settings.isUseVisibleSeparators();
    
    final char elStart = useVisibleSeparators ? DataTableUtils.ELEMENT_VISIBLE_START : DataTableUtils.ELEMENT_START;
    final char elEnd = useVisibleSeparators ? DataTableUtils.ELEMENT_VISIBLE_END : DataTableUtils.ELEMENT_END;
    final char elNameValSep = useVisibleSeparators ? DataTableUtils.ELEMENT_VISIBLE_NAME_VALUE_SEPARATOR : DataTableUtils.ELEMENT_NAME_VALUE_SEPARATOR;
    
    if (isTransferEncode)
    {
      TransferEncodingHelper.encodeChar(elStart, sb);
      if (name != null)
      {
        TransferEncodingHelper.encode(name, sb, 0);
        TransferEncodingHelper.encodeChar(elNameValSep, sb);
      }
    }
    else
    {
      sb.append(elStart);
      if (name != null)
      {
        sb.append(name);
        sb.append(elNameValSep);
      }
    }
    
    if (encodable != null)
    {
      encodable.encode(sb, settings, isTransferEncode, encodeLevel);
    }
    else if (fieldFormat != null)
    {
      fieldFormat.valueToEncodedString(fieldValue, settings, sb, encodeLevel);
    }
    else
    {
      if (isTransferEncode)
        TransferEncodingHelper.encode(value, sb, encodeLevel);
      else
        sb.append(value);
    }
    
    if (isTransferEncode)
      TransferEncodingHelper.encodeChar(elEnd, sb);
    else
      sb.append(elEnd);
    return sb;
  }
  
  @Override
  public String toString()
  {
    return name + "=" + value;
  }
  
}