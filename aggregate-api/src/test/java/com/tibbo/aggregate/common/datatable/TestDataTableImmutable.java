package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.tests.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDataTableImmutable extends CommonsTestCase
{
  private DataTable dataTable = null;
  
  @Override
  @BeforeEach
  protected void setUp() throws Exception
  {
    super.setUp();
    final TableFormat format = new TableFormat("<<type><S>><<devices><T><A=<F=<<devicePath><S><A=><D=Device Path>>><R=<aaa>><R=<bbb>>>>", new ClassicEncodingSettings(true));
    dataTable = new SimpleDataTable(format, 3);
  }
  
  @Test
  public void testThrowsExceptionOnScalarField()
  {
    try
    {
      dataTable.makeImmutable();

      dataTable.rec().setValue("type", "new value");

      fail(); // should not get here, exception is expected
    }
    catch (IllegalStateException e)
    {
      assertEquals("Immutable", e.getMessage());
    }
  }

  @Test
  public void testThrowsExceptionOnSubtableField()
  {
    try
    {
      dataTable.makeImmutable();

      final DataTable deicesTable = this.dataTable.rec().getDataTable("devices");

      deicesTable.rec().setValue("devicePath", "new device path");

      fail(); // should not get here, exception is expected
    }
    catch (IllegalStateException e)
    {
      assertEquals("Immutable", e.getMessage());
    }
  }
}
