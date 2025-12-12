package com.tibbo.aggregate.common.datatable;

import java.awt.*;

import com.tibbo.aggregate.common.datatable.converter.*;
import com.tibbo.aggregate.common.datatable.converter.Choice;

public class ComplexBean extends MegaGageBean
{
  public static final TableFormat COMPLEX_FORMAT = TestEncodingUtils.TEST_TABLE_FORMAT.clone();
  
  static
  {
    ChoicesConverter cc = new ChoicesConverter(Dimension.class);
    cc.add(new Choice("val1", "val1", new Dimension(1, 1)));
    cc.add(new Choice("val2", "val2", new Dimension(10, 10)));
    cc.add(new Choice("val3", "val3", new Dimension(100, 100)));
    DataTableConversion.registerFormatConverter(cc);
    
    FieldFormat<Object> ff = FieldFormat.create("<dimension><S>");
    Object fv = DataTableConversion.convertValueToField(ff, new Dimension(1, 1), Dimension.class);
    ff.setDefault(fv);
    COMPLEX_FORMAT.addField(ff);
  }
  
  private Dimension dimension;
  
  public ComplexBean()
  {
    super(COMPLEX_FORMAT);
  }
  
  public ComplexBean(DataRecord data)
  {
    super(COMPLEX_FORMAT, data);
  }
  
  public ComplexBean(GageBean gageBean)
  {
    super(COMPLEX_FORMAT, gageBean);
  }
  
  public Dimension getDimension()
  {
    return dimension;
  }
  
  public void setDimension(Dimension dimension)
  {
    this.dimension = dimension;
  }
}
