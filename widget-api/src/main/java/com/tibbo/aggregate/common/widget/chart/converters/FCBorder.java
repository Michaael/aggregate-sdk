package com.tibbo.aggregate.common.widget.chart.converters;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.converter.*;

public class FCBorder extends AbstractFormatConverter<Border>
{
  public static final String BORDER_NONE = "none";
  public static final String BORDER_EMPTY = "empty";
  public static final String BORDER_LINE = "line";
  public static final String BORDER_LOWERED = "lowered";
  public static final String BORDER_RAISED = "raised";
  public static final String BORDER_ETCHED = "etched";
  
  public static final String VF_BORDER_PRIMARY_COLOR = "color";
  public static final String VF_BORDER_SECONDARY_COLOR = "scolor";
  public static final String VF_BORDER_POSITION = "pos";
  public static final String VF_BORDER_TYPE = "type";
  public static final String VF_BORDER_TOP = "top";
  public static final String VF_BORDER_LEFT = "left";
  public static final String VF_BORDER_BOTTOM = "bottom";
  public static final String VF_BORDER_RIGHT = "right";
  public static final String VF_BORDER_TITLE = "title";
  public static final String VF_BORDER_TITLE_COLOR = "tcolor";
  public static final String VF_BORDER_JUSTIFICATION = "justification";
  
  public static final String BORDER_POSITION_INNER = "Inner";
  public static final String BORDER_POSITION_OUTER = "Outer";
  
  public static final TableFormat VFT_BORDER = new TableFormat(2, 2);
  
  static
  {
    FieldFormat posF = FieldFormat.create("<" + VF_BORDER_POSITION + "><S><F=R><D=" + Cres.get().getString("position") + ">");
    Map<String, String> posVals = new LinkedHashMap<String, String>();
    posVals.put(BORDER_POSITION_INNER, Cres.get().getString("wInner"));
    posVals.put(BORDER_POSITION_OUTER, Cres.get().getString("wOuter"));
    posF.setSelectionValues(posVals);
    VFT_BORDER.addField(posF);
    
    VFT_BORDER.addField(FieldFormat.create("<" + VF_BORDER_TYPE + "><S><A=" + BORDER_NONE + "><D=" + Cres.get().getString("type") + "><S=<" + Cres.get().getString("default") + "=" + BORDER_NONE + "><"
        + Cres.get().getString("empty") + "=" + BORDER_EMPTY + "><" + Cres.get().getString("line") + "=" + BORDER_LINE + "><" + Cres.get().getString("wLowered") + "=" + BORDER_LOWERED + "><"
        + Cres.get().getString("wRaised") + "=" + BORDER_RAISED + "><" + Cres.get().getString("wEtched") + "=" + BORDER_ETCHED + ">>"));
    VFT_BORDER.addField(FieldFormat.create("<" + VF_BORDER_TOP + "><I><D=" + Cres.get().getString("wTop") + "><A=1>"));
    VFT_BORDER.addField(FieldFormat.create("<" + VF_BORDER_LEFT + "><I><D=" + Cres.get().getString("wLeft") + "><A=1>"));
    VFT_BORDER.addField(FieldFormat.create("<" + VF_BORDER_BOTTOM + "><I><D=" + Cres.get().getString("wBottom") + "><A=1>"));
    VFT_BORDER.addField(FieldFormat.create("<" + VF_BORDER_RIGHT + "><I><D=" + Cres.get().getString("wRight") + "><A=1>"));
    
    FieldFormat ff = FieldFormat.create("<" + VF_BORDER_PRIMARY_COLOR + "><C><D=" + Cres.get().getString("wPrimaryColor") + ">");
    ff.setDefault(Color.GRAY);
    VFT_BORDER.addField(ff);
    
    ff = FieldFormat.create("<" + VF_BORDER_SECONDARY_COLOR + "><C><D=" + Cres.get().getString("wSecondaryColor") + ">");
    ff.setDefault(Color.WHITE);
    VFT_BORDER.addField(ff);
    
    VFT_BORDER.addField(FieldFormat.create("<" + VF_BORDER_TITLE + "><S><D=" + Cres.get().getString("title") + ">"));
    
    ff = FieldFormat.create("<" + VF_BORDER_TITLE_COLOR + "><C><D=" + Cres.get().getString("wTitleColor") + ">");
    ff.setDefault(Color.BLACK);
    VFT_BORDER.addField(ff);
    
    VFT_BORDER.addField(FieldFormat.create("<" + VF_BORDER_JUSTIFICATION + "><I><A=" + TitledBorder.LEFT + "><D=" + Cres.get().getString("wTitleJustification") + "><S=<"
        + Cres.get().getString("wLeft") + "=" + TitledBorder.LEFT + "><" + Cres.get().getString("center") + "=" + TitledBorder.CENTER + "><" + Cres.get().getString("wRight") + "="
        + TitledBorder.RIGHT + ">>"));
    
    String ref1 = VF_BORDER_TOP + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp1 = "{" + VF_BORDER_TYPE + "} == '" + BORDER_EMPTY + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_LINE + "'";
    VFT_BORDER.addBinding(ref1, exp1);
    String ref2 = VF_BORDER_LEFT + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp2 = "{" + VF_BORDER_TYPE + "} == '" + BORDER_EMPTY + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_LINE + "'";
    VFT_BORDER.addBinding(ref2, exp2);
    String ref3 = VF_BORDER_BOTTOM + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp3 = "{" + VF_BORDER_TYPE + "} == '" + BORDER_EMPTY + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_LINE + "'";
    VFT_BORDER.addBinding(ref3, exp3);
    String ref4 = VF_BORDER_RIGHT + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp4 = "{" + VF_BORDER_TYPE + "} == '" + BORDER_EMPTY + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_LINE + "'";
    VFT_BORDER.addBinding(ref4, exp4);
    String ref5 = VF_BORDER_PRIMARY_COLOR + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp5 = "{" + VF_BORDER_TYPE + "} == '" + BORDER_LINE + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_LOWERED + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_RAISED + "' || {"
        + VF_BORDER_TYPE + "} == '" + BORDER_ETCHED + "'";
    VFT_BORDER.addBinding(ref5, exp5);
    String ref6 = VF_BORDER_SECONDARY_COLOR + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp6 = "{" + VF_BORDER_TYPE + "} == '" + BORDER_LOWERED + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_RAISED + "' || {" + VF_BORDER_TYPE + "} == '" + BORDER_ETCHED + "'";
    VFT_BORDER.addBinding(ref6, exp6);
    String ref7 = VF_BORDER_TITLE + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp7 = "{" + VF_BORDER_TYPE + "} != '" + BORDER_NONE + "'";
    VFT_BORDER.addBinding(ref7, exp7);
    VFT_BORDER.addBinding(VF_BORDER_TITLE_COLOR + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + VF_BORDER_TITLE + "} != ''");
    VFT_BORDER.addBinding(VF_BORDER_JUSTIFICATION + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + VF_BORDER_TITLE + "} != ''");
    
  }
  
  public FCBorder()
  {
    super(Border.class, VFT_BORDER);
  }
  
  @Override
  public Border convertToBean(Object value, Border originalValue)
  {
    return createBorder((DataTable) value);
  }
  
  @Override
  public Object convertToTable(Border value, TableFormat format)
  {
    return createTable(value);
  }
  
  public static DataTable createDefaultTable()
  {
    DataTable table = new SimpleDataTable(VFT_BORDER, true);
    table.getRecord(0).setValue(VF_BORDER_POSITION, BORDER_POSITION_INNER);
    table.getRecord(1).setValue(VF_BORDER_POSITION, BORDER_POSITION_OUTER);
    return table;
  }
  
  private static Border createBorderPart(DataRecord source)
  {
    String type = source.getString(VF_BORDER_TYPE);
    int top = source.getInt(VF_BORDER_TOP);
    int left = source.getInt(VF_BORDER_LEFT);
    int bottom = source.getInt(VF_BORDER_BOTTOM);
    int right = source.getInt(VF_BORDER_RIGHT);
    Color color = source.getColor(VF_BORDER_PRIMARY_COLOR);
    Color scolor = source.getColor(VF_BORDER_SECONDARY_COLOR);
    String title = source.getString(VF_BORDER_TITLE);
    Color tcolor = source.getColor(VF_BORDER_TITLE_COLOR);
    int justification = source.getInt(VF_BORDER_JUSTIFICATION);
    
    Border border = null;
    
    if (type.equals(BORDER_EMPTY))
    {
      border = BorderFactory.createEmptyBorder(top, left, bottom, right);
    }
    else if (type.equals(BORDER_LINE))
    {
      border = BorderFactory.createMatteBorder(top, left, bottom, right, color);
    }
    else if (type.equals(BORDER_LOWERED))
    {
      border = BorderFactory.createBevelBorder(BevelBorder.LOWERED, scolor, color);
    }
    else if (type.equals(BORDER_RAISED))
    {
      border = BorderFactory.createBevelBorder(BevelBorder.RAISED, scolor, color);
    }
    else if (type.equals(BORDER_ETCHED))
    {
      border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED, color, scolor);
    }
    
    if (border != null && title.length() > 0)
    {
      border = BorderFactory.createTitledBorder(border, title, justification, TitledBorder.DEFAULT_POSITION, null, tcolor);
    }
    
    return border;
  }
  
  public static Border createBorder(DataTable source)
  {
    Border inner = createBorderPart(source.getRecord(0));
    Border outer = createBorderPart(source.getRecord(1));
    return (inner == null && outer == null) ? null : BorderFactory.createCompoundBorder(outer, inner);
  }
  
  public static DataTable createTable(Border border)
  {
    DataTable res = createDefaultTable();
    
    if (border != null)
    {
      DataRecord inner = res.getRecord(0);
      DataRecord outer = res.getRecord(1);
      
      CompoundBorder compoundBorder = (CompoundBorder) border;
      fill(inner, compoundBorder.getInsideBorder());
      fill(outer, compoundBorder.getOutsideBorder());
    }
    return res;
  }
  
  private static void fill(DataRecord rec, Border border)
  {
    if (border instanceof TitledBorder)
    {
      TitledBorder b = (TitledBorder) border;
      rec.setValue(VF_BORDER_TITLE, b.getTitle());
      rec.setValue(VF_BORDER_TITLE_COLOR, b.getTitleColor());
      rec.setValue(VF_BORDER_JUSTIFICATION, b.getTitleJustification());
      fill(rec, b.getBorder());
    }
    else if (border instanceof EmptyBorder)
    {
      EmptyBorder b = (EmptyBorder) border;
      rec.setValue(VF_BORDER_TYPE, BORDER_EMPTY);
      rec.setValue(VF_BORDER_TOP, b.getBorderInsets().top);
      rec.setValue(VF_BORDER_LEFT, b.getBorderInsets().left);
      rec.setValue(VF_BORDER_BOTTOM, b.getBorderInsets().bottom);
      rec.setValue(VF_BORDER_RIGHT, b.getBorderInsets().right);
      
      if (border instanceof MatteBorder)
      {
        MatteBorder mb = (MatteBorder) border;
        rec.setValue(VF_BORDER_TYPE, BORDER_LINE);
        rec.setValue(VF_BORDER_PRIMARY_COLOR, mb.getMatteColor());
      }
    }
    else if (border instanceof BevelBorder)
    {
      BevelBorder b = (BevelBorder) border;
      rec.setValue(VF_BORDER_TYPE, b.getBevelType() == BevelBorder.LOWERED ? BORDER_LOWERED : BORDER_RAISED);
      rec.setValue(VF_BORDER_SECONDARY_COLOR, b.getHighlightInnerColor());
      rec.setValue(VF_BORDER_PRIMARY_COLOR, b.getShadowOuterColor());
    }
    else if (border instanceof EtchedBorder)
    {
      EtchedBorder b = (EtchedBorder) border;
      rec.setValue(VF_BORDER_TYPE, BORDER_ETCHED);
      rec.setValue(VF_BORDER_SECONDARY_COLOR, b.getShadowColor());
      rec.setValue(VF_BORDER_PRIMARY_COLOR, b.getHighlightColor());
    }
  }
}
