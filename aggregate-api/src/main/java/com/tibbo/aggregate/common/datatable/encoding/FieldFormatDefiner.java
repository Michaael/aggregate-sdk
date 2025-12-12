package com.tibbo.aggregate.common.datatable.encoding;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.tibbo.aggregate.common.datatable.FieldFormat;

class FieldFormatDefiner
{
  private final boolean convertDifferentTypesToString;
  
  private final boolean implicitCasting;
  
  private Map<String, LinkedHashSet<FieldFormat>> fieldFormatsByName = new HashMap<>();
  
  FieldFormatDefiner(boolean convertDifferentTypesToString, boolean implicitCasting)
  {
    this.convertDifferentTypesToString = convertDifferentTypesToString;
    this.implicitCasting = implicitCasting;
  }
  
  void put(String fieldName, FieldFormat ff)
  {
    final LinkedHashSet<FieldFormat> fieldFormats = fieldFormatsByName.computeIfAbsent(fieldName, k -> new LinkedHashSet<>());
    
    if (fieldFormats.size() == 1)
    {
      final FieldFormat currentFieldFormat = fieldFormats.iterator().next();
      if (JsonEncodingHelper.looksLikeDefaultField(currentFieldFormat))
        fieldFormats.clear();
    }
    
    fieldFormats.add(ff);
  }
  
  Set<String> getFieldNames()
  {
    return fieldFormatsByName.keySet();
  }
  
  FieldFormat get(String fieldName)
  {
    final LinkedHashSet<FieldFormat> fieldFormats = fieldFormatsByName.get(fieldName);
    
    if (fieldFormats == null || fieldFormats.isEmpty())
      return JsonEncodingHelper.createDefaultFieldFormat(fieldName);
    
    final int numFieldFormats = fieldFormats.size();
    
    if (numFieldFormats == 1)
      return fieldFormats.iterator().next();
    
    if (implicitCasting && isAllNumberFormats(fieldFormats))
      return chooseNumberFormat(fieldFormats);

    if (!convertDifferentTypesToString)
      return fieldFormats.iterator().next();

    return JsonEncodingHelper.createDefaultFieldFormat(fieldName);
  }
  
  private FieldFormat chooseNumberFormat(LinkedHashSet<FieldFormat> fieldFormats)
  {
    FieldFormat result = fieldFormats.iterator().next();
    for (FieldFormat ff : fieldFormats)
    {
      if (ff.getType() == FieldFormat.DOUBLE_FIELD)
      {
        result = ff;
        break;
      }
      if (ff.getType() == FieldFormat.FLOAT_FIELD && result.getType() != FieldFormat.DOUBLE_FIELD)
      {
        result = ff;
      }
      if (ff.getType() == FieldFormat.LONG_FIELD && result.getType() == FieldFormat.INTEGER_FIELD)
        result = ff;
    }
    return result;
  }
  
  private boolean isAllNumberFormats(LinkedHashSet<FieldFormat> fieldFormats)
  {
    for (FieldFormat ff : fieldFormats)
    {
      if (!(ff.getType() == FieldFormat.INTEGER_FIELD ||
          ff.getType() == FieldFormat.LONG_FIELD ||
          ff.getType() == FieldFormat.FLOAT_FIELD ||
          ff.getType() == FieldFormat.DOUBLE_FIELD))
      {
        return false;
      }
    }
    return true;
  }
}
