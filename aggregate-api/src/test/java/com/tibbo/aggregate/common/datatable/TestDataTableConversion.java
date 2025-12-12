package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.*;
import java.util.*;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.tests.*;
import org.junit.jupiter.api.Test;

public class TestDataTableConversion extends CommonsTestCase
{
  public static final TableFormat GG_TEST_TABLE_FORMAT;
  static
  {
    GG_TEST_TABLE_FORMAT = TestEncodingUtils.TEST_TABLE_FORMAT.clone();
    GG_TEST_TABLE_FORMAT.addField("<gages><T>");
  }
  
  public static GageBean getGageBean() throws DataTableException
  {
    DataTable gageTable = TestEncodingUtils.createTestDataTable(false, 1);
    return (GageBean) DataTableConversion.beanFromTable(gageTable, GageBean.class, TestEncodingUtils.TEST_TABLE_FORMAT, true);
  }
  
  public static java.util.List<GageBean> getGageBeanList(int count) throws DataTableException
  {
    DataTable gageTable = TestEncodingUtils.createTestDataTable(false, count);
    return DataTableConversion.beansFromTable(gageTable, GageBean.class, TestEncodingUtils.TEST_TABLE_FORMAT, true);
  }
  
  @Test
  public void testGageBeanConversion() throws DataTableException
  {
    GageBean gage = getGageBean();
    
    DataTable gageTable = DataTableConversion.beanToTable(gage, TestEncodingUtils.TEST_TABLE_FORMAT);
    GageBean blackSheep = (GageBean) DataTableConversion.beanFromTable(gageTable, GageBean.class, TestEncodingUtils.TEST_TABLE_FORMAT, true);
    DataTable blackSheepTable = DataTableConversion.beanToTable(blackSheep, TestEncodingUtils.TEST_TABLE_FORMAT);
    
    assertEquals(gageTable, blackSheepTable);
  }
  
  @Test
  public void testSeveralGageBeanConversion() throws DataTableException
  {
    java.util.List<GageBean> beans = getGageBeanList(5);
    DataTable gageTable = DataTableConversion.beansToTable(beans, TestEncodingUtils.TEST_TABLE_FORMAT, true);
    java.util.List blackSheeps = DataTableConversion.beansFromTable(gageTable, GageBean.class, TestEncodingUtils.TEST_TABLE_FORMAT, true);
    DataTable blackSheepsTable = DataTableConversion.beansToTable(blackSheeps, TestEncodingUtils.TEST_TABLE_FORMAT, true);
    
    assertEquals(gageTable, blackSheepsTable);
  }
  
  @Test
  public void testWrappedGageBeanConversion() throws DataTableException
  {
    GageGageBean ggb = new GageGageBean();
    ggb.setBooleanField(true);
    ggb.setString("assa");
    ggb.setIntField(444);
    ggb.setData(new Data("rrr", "rrr".getBytes()));
    ggb.setDate(new Date());
    ggb.setDoubleField(777d);
    ggb.setLongField(863l);
    ggb.setFloatField(135f);
    ggb.setTable(new SimpleDataTable());
    ggb.setColor(Color.MAGENTA);
    ggb.setGage(new GageBean[] { getGageBean() });
    
    DataTable ggbTable = DataTableConversion.beanToTable(ggb, GG_TEST_TABLE_FORMAT);
    GageBean blackSheep = (GageBean) DataTableConversion.beanFromTable(ggbTable, GageGageBean.class, GG_TEST_TABLE_FORMAT, true);
    DataTable blackSheepTable = DataTableConversion.beanToTable(blackSheep, GG_TEST_TABLE_FORMAT);
    
    assertEquals(ggbTable, blackSheepTable);
  }
  
  @Test
  public void testAgregateBean() throws DataTableException
  {
    GageBean gageBean = getGageBean();
    DataTable sourceDt = DataTableConversion.beanToTable(gageBean, TestEncodingUtils.TEST_TABLE_FORMAT);
    MegaGageBean ab = new MegaGageBean(gageBean);
    
    MegaGageBean blackSheep = (MegaGageBean) DataTableConversion.beanFromTable(ab.toDataTable(), MegaGageBean.class, TestEncodingUtils.TEST_TABLE_FORMAT, true);
    
    assertEquals(sourceDt, blackSheep.toDataTable());
  }
  
  @Test
  public void testComplexBean() throws DataTableException
  {
    GageBean gageBean = getGageBean();
    ComplexBean ab = new ComplexBean(gageBean);
    ab.setDimension(new Dimension(100, 100));
    
    DataTable abTable = ab.toDataTable();
    ComplexBean blackSheep = (ComplexBean) DataTableConversion.beanFromTable(abTable, ComplexBean.class, ComplexBean.COMPLEX_FORMAT, true);
    assertEquals(abTable, blackSheep.toDataTable());
  }
}
