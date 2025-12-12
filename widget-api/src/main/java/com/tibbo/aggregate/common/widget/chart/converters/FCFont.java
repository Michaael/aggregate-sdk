package com.tibbo.aggregate.common.widget.chart.converters;

import java.awt.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.converter.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.expression.*;

public class FCFont extends AbstractFormatConverter<Font>
{
  public static final String FONT_ARIAL = "Arial";
  
  public static final TableFormat FORMAT;
  
  public static final String VF_FONT_NAME = "name";
  public static final String VF_FONT_SIZE = "size";
  public static final String VF_FONT_CUSTOM = "custom";
  public static final String VF_FONT_BOLD = "bold";
  public static final String VF_FONT_ITALIC = "italic";
  
  static
  {
    FORMAT = new TableFormat(1, 1);
    
    FORMAT.setNamingExpression(new Expression("{" + VF_FONT_NAME + "} + ', ' + {" + VF_FONT_SIZE + "}"));
    
    FORMAT.addField(FieldFormat.create("<" + VF_FONT_CUSTOM + "><B><D=" + Cres.get().getString("wCustomFont") + ">"));
    
    FieldFormat ff = FieldFormat.create("<" + VF_FONT_NAME + "><S><F=N><A=" + FONT_ARIAL + "><D=" + Cres.get().getString("name") + "><E=" + StringFieldFormat.EDITOR_FONT + ">");
    FORMAT.addField(ff);
    
    FORMAT.addField(FieldFormat.create("<" + VF_FONT_SIZE + "><I><A=10><D=" + Cres.get().getString("size") + ">"));
    FORMAT.addField(FieldFormat.create("<" + VF_FONT_BOLD + "><B><D=" + Cres.get().getString("bold") + ">"));
    FORMAT.addField(FieldFormat.create("<" + VF_FONT_ITALIC + "><B><D=" + Cres.get().getString("italic") + ">"));
    
    FORMAT.addBinding(VF_FONT_NAME + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + VF_FONT_CUSTOM + "}");
    FORMAT.addBinding(VF_FONT_SIZE + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + VF_FONT_CUSTOM + "}");
    FORMAT.addBinding(VF_FONT_BOLD + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + VF_FONT_CUSTOM + "}");
    FORMAT.addBinding(VF_FONT_ITALIC + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + VF_FONT_CUSTOM + "}");
  }
  
  public FCFont()
  {
    super(Font.class, FORMAT);
  }
  
  @Override
  public Font convertToBean(Object value, Font originalValue)
  {
    return createFont((DataTable) value);
  }
  
  @Override
  public Object convertToTable(Font value, TableFormat format)
  {
    return createTable(value);
  }
  
  public static Font createFont(DataTable source)
  {
    try
    {
      DataRecord rec = source.rec();
      
      if (!rec.getBoolean(VF_FONT_CUSTOM))
      {
        return null;
      }
      
      String name = rec.getString(VF_FONT_NAME);
      
      int style = rec.getBoolean(VF_FONT_BOLD) ? Font.BOLD : Font.PLAIN;
      if (rec.getBoolean(VF_FONT_ITALIC))
      {
        style |= Font.ITALIC;
      }
      
      return new Font(name, style, rec.getInt(VF_FONT_SIZE));
    }
    catch (Exception ex)
    {
      Log.WIDGETS.error("Error creating font", ex);
      return null;
    }
  }
  
  public static DataTable createTable(Font font)
  {
    DataRecord rec = new DataRecord(FORMAT);
    
    if (font != null)
    {
      rec.setValue(VF_FONT_CUSTOM, true);
      rec.setValue(VF_FONT_NAME, font.getName());
      rec.setValue(VF_FONT_SIZE, font.getSize());
      rec.setValue(VF_FONT_BOLD, font.isBold());
      rec.setValue(VF_FONT_ITALIC, font.isItalic());
    }
    
    return rec.wrap();
  }
}
