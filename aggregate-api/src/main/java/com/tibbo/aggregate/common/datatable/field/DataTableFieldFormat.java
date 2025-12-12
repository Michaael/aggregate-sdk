package com.tibbo.aggregate.common.datatable.field;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.protocol.*;
import com.tibbo.aggregate.common.util.*;

import java.util.Arrays;
import java.util.List;

public class DataTableFieldFormat extends FieldFormat<DataTable>
{
  public static final String EDITOR_DATE_RANGE = "dateRange";

  public DataTableFieldFormat(String name)
  {
    super(name);
    
    addValidator(new AbstractFieldValidator()
    {
      @Override
      public Object validate(Context context, ContextManager contextManager, CallerController caller, Object value) throws ValidationException
      {
        DataTable def = getDefaultValue();
        
        if (def == null || def.getFieldCount() == 0)
        {
          return value;
        }
        
        DataTable dt = (DataTable) value;
        
        if (dt == null)
        {
          return null;
        }
        
        String msg = dt.getFormat().extendMessage(def.getFormat());
        if (msg != null)
        {
          DataTable newValue = def.clone();
          DataTableReplication.copy(dt, newValue, true, true, true);
          value = newValue;
        }
        
        return value;
      }
    });
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.DATATABLE_FIELD;
  }
  
  @Override
  public Class getFieldClass()
  {
    return DataTable.class;
  }
  
  @Override
  public Class getFieldWrappedClass()
  {
    return DataTable.class;
  }
  
  @Override
  public DataTable getNotNullDefault()
  {
    return new SimpleDataTable();
  }
  
  @Override
  protected void makeImmutable()
  {
    super.makeImmutable();
    
    final DataTable defaultValue = getDefaultValue();
    
    if (defaultValue != null)
    {
      defaultValue.makeImmutable();
    }
  }
  
  @Override
  public DataTable valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    try
    {
      DataTable defaultValue = getDefaultValue();
      
      Boolean tempEncodeFieldNames;
      TableFormat oldFormat = null;
      if (settings != null)
      {
        oldFormat = settings.getFormat();
        if (defaultValue != null)
        {
          settings.setFormat(defaultValue.getFormat());
        }
        tempEncodeFieldNames = settings.isEncodeFieldNames();
      }
      else
      {
        settings = new ClassicEncodingSettings(ExpressionUtils.useVisibleSeparators(value));
        tempEncodeFieldNames = false;
      }
      
      try
      {
        DataTable res = null;
        if (value != null)
        {
          final ElementList elements = StringUtils.elements(value, settings.isUseVisibleSeparators());
          final boolean containsID = elements.stream().map(Element::getName).anyMatch(AbstractDataTable.ELEMENT_ID::equals);
          res = containsID ? new ProxyDataTable(elements, settings, validate, null) : new SimpleDataTable(elements, settings, validate);
        }
        
        if (defaultValue != null && defaultValue.getFieldCount() > 0 && !(res != null && res.getFormat().extend(defaultValue.getFormat())))
        {
          DataTable newRes = getDefaultValue().clone();
          DataTableReplication.copy(res, newRes, true, true, true);
          res = newRes;
        }
        
        if (res != null && validate)
        {
          res.validate(null, null, null);
        }
        
        return res;
      }
      finally
      {
        settings.setFormat(oldFormat);
        if (tempEncodeFieldNames)
        {
          settings.setEncodeFieldNames(true);
        }
      }
    }
    catch (Exception ex)
    {
      throw new IllegalArgumentException("Error constructing value of field '" + toString() + "': " + ex.getMessage(), ex);
    }
  }
  
  @Override
  public DataTable valueFromEncodedString(String source, ClassicEncodingSettings settings, boolean validate)
  {
    // Copied from the FieldFormat and enhanced to support compatibility with version 5.41 (because a nicer idea of substituting transferEncoding on the fly is not thread safe)
    
    final String nullElement = settings.isUseVisibleSeparators() ? DataTableUtils.DATA_TABLE_VISIBLE_NULL : DataTableUtils.DATA_TABLE_NULL;
    final boolean sourceIsNull = source.equals(nullElement);
    final boolean compatibilityTransferEncode = ProtocolVersion.V3.equals(settings.getProtocolVersion()) || ProtocolVersion.V2.equals(settings.getProtocolVersion());
    return sourceIsNull ? null : valueFromString(compatibilityTransferEncode ? DataTableUtils.transferDecode(source) : source, settings, validate);
  }
  
  @Override
  public StringBuilder valueToEncodedString(DataTable value, ClassicEncodingSettings settings, StringBuilder sb, Integer encodeLevel)
  {
    String strVal = valueToString(value, settings);
    
    if (strVal == null)
    {
      return sb.append((settings == null || !settings.isUseVisibleSeparators()) ? DataTableUtils.DATA_TABLE_NULL : DataTableUtils.DATA_TABLE_VISIBLE_NULL);
    }
    
    if (settings != null)
    {
      final boolean compatibilityTransferEncode = ProtocolVersion.V3.equals(settings.getProtocolVersion()) || ProtocolVersion.V2.equals(settings.getProtocolVersion());
      if (compatibilityTransferEncode)
      {
        return TransferEncodingHelper.encode(strVal, sb, encodeLevel);
      }
    }
    
    return sb.append(strVal);
  }
  
  @Override
  public String valueToString(DataTable value, ClassicEncodingSettings settings)
  {
    if (value == null)
    {
      return null;
    }
    
    Boolean oldEncodeFormat = settings != null ? settings.isEncodeFormat() : null;
    try
    {
      if (settings != null && (getDefaultValue() == null || getDefaultValue().getFieldCount() == 0 || !Util.equals(getDefaultValue().getFormat(), value.getFormat())))
      {
        settings.setEncodeFormat(true);
      }
      
      return value.encode(settings);
    }
    finally
    {
      if (oldEncodeFormat != null)
      {
        settings.setEncodeFormat(oldEncodeFormat);
      }
    }
  }

  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(EDITOR_LIST, EDITOR_DATE_RANGE);
  }
  
  public static String encodeEditorOptions(boolean showTableData)
  {
    return showTableData ? "1" : "0";
  }
  
}
