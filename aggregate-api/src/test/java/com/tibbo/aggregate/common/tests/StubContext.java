package com.tibbo.aggregate.common.tests;

import java.util.concurrent.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;

public class StubContext extends AbstractContext
{
  public static final String V_TEST = "test";
  public static final String E_TEST = "test";
  public static final String F_TEST = "test";
  
  public static final String VF_TEST_INT = "int";
  
  public static final TableFormat VFT_TEST = new TableFormat(1, 1);
  static
  {
    VFT_TEST.addField("<" + VF_TEST_INT + "><I>");
  }
  
  public static final String EF_TEST_STR = "str";
  public static final String EF_TEST_INT = "int";
  public static final String EF_TEST_FLOAT = "float";
  
  public static final TableFormat EFT_TEST = new TableFormat(1, 1);
  static
  {
    EFT_TEST.addField("<" + EF_TEST_STR + "><S>");
    EFT_TEST.addField("<" + EF_TEST_INT + "><I>");
    EFT_TEST.addField("<" + EF_TEST_FLOAT + "><F>");
  }
  
  public static final String FIF_PARAMETER = "parameter";
  public static final String F_FUNCTION = "function";
  
  public static final TableFormat FIFT_FUNCTION = new TableFormat(1, 1, "<" + FIF_PARAMETER + "><I>");
  public static final TableFormat FOFT_FUNCTION = FIFT_FUNCTION.clone();
  
  private Semaphore semaphore = new Semaphore(1, true);
  
  public int getCount()
  {
    return count;
  }
  
  private int count = 0;
  
  public StubContext(String name)
  {
    super(name);
  }
  
  @Override
  public void setupMyself() throws ContextException
  {
    super.setupMyself();
    VariableDefinition vd = new VariableDefinition(V_TEST, VFT_TEST, true, true, "Test", ContextUtils.GROUP_DEFAULT);
    vd.setSetter((con, def, caller, request, value) -> {
      count = value.rec().getInt(0);
      semaphore.release();
      
      return true;
    });
    
    vd.setGetter((con, def, caller, request) -> {
      DataTable dt = new SimpleDataTable(VFT_TEST, true);
      dt.rec().setValue(VF_TEST_INT, count);
      return dt;
    });
    
    addVariableDefinition(vd);
    
    addEventDefinition(new EventDefinition(E_TEST, EFT_TEST, "Test Event", ContextUtils.GROUP_DEFAULT));

    FunctionDefinition fd = new FunctionDefinition(F_FUNCTION, FIFT_FUNCTION, FOFT_FUNCTION);
    fd.setImplementation((con, def, caller, request, parameters) -> {
      count = parameters.rec().getInt(0);
      semaphore.release();
      return new SimpleDataTable(FOFT_FUNCTION, parameters.rec().getString(FIF_PARAMETER));
    });
    addFunctionDefinition(fd);

    FunctionDefinition emptyFd = new FunctionDefinition(F_TEST, TableFormat.EMPTY_FORMAT, TableFormat.EMPTY_FORMAT, "Test Function", ContextUtils.GROUP_DEFAULT);
    addFunctionDefinition(emptyFd);
  }
  
  public void acquire(int permits) throws InterruptedException
  {
    semaphore.acquire(permits);
  }
}
