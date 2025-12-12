package com.tibbo.aggregate.common.expression.function;

import static com.tibbo.aggregate.common.context.ContextUtils.GROUP_DEFAULT;
import static com.tibbo.aggregate.common.expression.Function.GROUP_CONTEXT_RELATED;
import static com.tibbo.aggregate.common.expression.function.DefaultFunctions.COPY;
import static com.tibbo.aggregate.common.tests.StubContext.E_TEST;
import static com.tibbo.aggregate.common.tests.StubContext.F_TEST;
import static com.tibbo.aggregate.common.tests.StubContext.VFT_TEST;
import static com.tibbo.aggregate.common.tests.StubContext.VF_TEST_INT;
import static com.tibbo.aggregate.common.tests.StubContext.V_TEST;

import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.DefaultContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.context.AvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.CallFunctionExFunction;
import com.tibbo.aggregate.common.expression.function.context.CallFunctionFunction;
import com.tibbo.aggregate.common.expression.function.context.DcFunction;
import com.tibbo.aggregate.common.expression.function.context.DrFunction;
import com.tibbo.aggregate.common.expression.function.context.DtFunction;
import com.tibbo.aggregate.common.expression.function.context.EventAvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.EventFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.EventGroupFunction;
import com.tibbo.aggregate.common.expression.function.context.FireEventFunction;
import com.tibbo.aggregate.common.expression.function.context.FullDescriptionFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionAvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionGroupFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionInputFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionOutputFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.GetVariableFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableExFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableFieldFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableRecordFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableAvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableGroupFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableReadableFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableWritableFunction;
import com.tibbo.aggregate.common.tests.CommonsTestCase;
import com.tibbo.aggregate.common.tests.StubContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestContextFunctions extends CommonsTestCase
{
  private static final String NOT_AVAILABLE_CONTEXT = "InaccessibleContext";
  private static final String NOT_AVAILABLE_FUNCTION = "InaccessibleFunction";
  private static final String NOT_AVAILABLE_VARIABLE = "InaccessibleVariable";
  private static final String NOT_AVAILABLE_EVENT = "InaccessibleEvent";
  private static final String NOT_AVAILABLE_GROUP = "InaccessibleGroup";
  private static final String ROOT_CONTEXT = "root";
  private static final String CHILD_CONTEXT = "child";

  private static final EvaluationEnvironment ENV = new EvaluationEnvironment();
  
  private Evaluator ev;
  
  @BeforeEach
  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    StubContext root = new StubContext(ROOT_CONTEXT);
    StubContext childContext = new StubContext(CHILD_CONTEXT);
    root.addChild(childContext);
    DefaultContextManager contextManager = new DefaultContextManager(root, true);
    
    DefaultReferenceResolver referenceResolver = new DefaultReferenceResolver();
    referenceResolver.setContextManager(contextManager);
    referenceResolver.setCallerController(contextManager.getCallerController());
    referenceResolver.setDefaultContext(root);
    ev = new Evaluator(referenceResolver);
  }
  
  @Test
  @Test
  public void testDtFunction() throws Exception
  {
    Object res = new DtFunction().execute(ev, null);
    
    assertSame(ev.getDefaultResolver().getDefaultTable(), res);
  }
  
  @Test
  @Test
  public void testDrFunction() throws Exception
  {
    Object res = new DrFunction().execute(ev, null);
    
    assertSame(ev.getDefaultResolver().getDefaultRow(), res);
  }
  
  @Test
  @Test
  public void testDcFunction() throws Exception
  {
    String res = (String) new DcFunction().execute(ev, null);
    
    assertSame(ev.getDefaultResolver().getDefaultContext().getPath(), res);
  }
  
  @Test
  @Test
  public void testAvailableFunctionFalseCondition() throws Exception
  {
    boolean isFalse = (boolean) new AvailableFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT);
    
    assertEquals(false, isFalse);
  }
  
  @Test
  public void testAvailableFunctionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) new AvailableFunction().execute(ev, null, CHILD_CONTEXT);
    
    assertEquals(true, isTrue);
  }
  
  @Test
  public void testEventAvailableFunctionFalseCondition() throws Exception
  {
    EventAvailableFunction function = new EventAvailableFunction("eventAvailable", Function.GROUP_CONTEXT_RELATED);
    boolean isFalse = (boolean) function.execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_EVENT);
    
    assertEquals(false, isFalse);
  }
  
  @Test
  public void testEventAvailableFunctionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) new EventAvailableFunction("eventAvailable", Function.GROUP_CONTEXT_RELATED).execute(ev, null, CHILD_CONTEXT, E_TEST);
    
    assertEquals(true, isTrue);
  }
  
  @Test
  public void testFunctionAvailableFunctionFalseCondition() throws Exception
  {
    FunctionAvailableFunction function = new FunctionAvailableFunction("functionReadable", Function.GROUP_CONTEXT_RELATED);
    boolean isFalse = (boolean) function.execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_FUNCTION);
    
    assertEquals(false, isFalse);
  }
  
  @Test
  public void testFunctionAvailableFunctionTrueCondition() throws Exception
  {
    FunctionAvailableFunction function = new FunctionAvailableFunction("functionReadable", Function.GROUP_CONTEXT_RELATED);
    boolean isTrue = (boolean) function.execute(ev, null, CHILD_CONTEXT, COPY);
    
    assertEquals(true, isTrue);
  }
  
  @Test
  public void testVariableAvailableFunctionFalseCondition() throws Exception
  {
    VariableAvailableFunction function = new VariableAvailableFunction();
    boolean isFalse = (boolean) function.execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_VARIABLE);
    
    assertEquals(false, isFalse);
  }
  
  @Test
  public void testVariableAvailableFunctionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) new VariableAvailableFunction().execute(ev, null, CHILD_CONTEXT, V_TEST);
    
    assertEquals(true, isTrue);
  }
  
  @Test
  public void testEventGroupFunctionNotAvailableCondition() throws Exception
  {
    Object nullable = new EventGroupFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_GROUP);
    
    assertNull(nullable);
  }
  
  @Test
  public void testEventGroupFunctionAvailableCondition() throws Exception
  {
    Object result = new EventGroupFunction().execute(ev, null, CHILD_CONTEXT, E_TEST);
    
    assertEquals(GROUP_DEFAULT, result);
  }
  
  @Test
  public void testFunctionGroupFunctionNotAvailableCondition() throws Exception
  {
    Object nullable = new FunctionGroupFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_GROUP);
    
    assertNull(nullable);
  }
  
  @Test
  public void testFunctionGroupFunctionAvailableCondition() throws Exception
  {
    Object result = new FunctionGroupFunction().execute(ev, null, CHILD_CONTEXT, F_TEST);
    
    assertEquals(GROUP_DEFAULT, result);
  }
  
  @Test
  public void testVariableGroupFunctionNotAvailableCondition() throws Exception
  {
    Object nullable = new VariableGroupFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_GROUP);
    
    assertNull(nullable);
  }
  
  @Test
  public void testVariableGroupFunctionAvailableCondition() throws Exception
  {
    Object result = new VariableGroupFunction().execute(ev, null, CHILD_CONTEXT, V_TEST);
    
    assertEquals(GROUP_DEFAULT, result);
  }
  
  @Test
  public void testFunctionInputFormatNotAvailableCondition() throws Exception
  {
    Object nullable = new FunctionInputFormatFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_FUNCTION);
    
    assertNull(nullable);
  }
  
  @Test
  public void testFunctionInputFormatAvailableCondition() throws Exception
  {
    String result = (String) new FunctionInputFormatFunction().execute(ev, null, CHILD_CONTEXT, F_TEST);
    
    assertNotNull(result);
  }
  
  @Test
  public void testFunctionOutputFormatNotAvailableCondition() throws Exception
  {
    Object nullable = new FunctionOutputFormatFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_FUNCTION);
    
    assertNull(nullable);
  }
  
  @Test
  public void testFunctionOutputFormatAvailableCondition() throws Exception
  {
    String result = (String) new FunctionOutputFormatFunction().execute(ev, null, CHILD_CONTEXT, F_TEST);
    
    assertNotNull(result);
  }
  
  @Test
  public void testVariableFormatFunctionNotAvailableCondition() throws Exception
  {
    Object nullable = new VariableFormatFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_VARIABLE);
    
    assertNull(nullable);
  }
  
  @Test
  public void testVariableFormatFunctionAvailableCondition() throws Exception
  {
    String result = (String) new VariableFormatFunction().execute(ev, null, CHILD_CONTEXT, V_TEST);
    
    assertNotNull(result);
  }
  
  @Test
  public void testEventFormatFunctionNotAvailableCondition() throws Exception
  {
    Object nullable = new EventFormatFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_EVENT);
    
    assertNull(nullable);
  }
  
  @Test
  public void testEventFormatFunctionAvailableCondition() throws Exception
  {
    String result = (String) new EventFormatFunction().execute(ev, null, CHILD_CONTEXT, E_TEST);
    
    assertNotNull(result);
  }
  
  @Test
  public void testVariableReadableFunctionFalseCondition() throws Exception
  {
    VariableReadableFunction function = new VariableReadableFunction("variableReadable", Function.GROUP_CONTEXT_RELATED);
    boolean isFalse = (boolean) function.execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_VARIABLE);
    
    assertEquals(false, isFalse);
  }
  
  @Test
  public void testVariableReadableFunctionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) new VariableReadableFunction("variableReadable", Function.GROUP_CONTEXT_RELATED).execute(ev, null, CHILD_CONTEXT, V_TEST);
    
    assertEquals(true, isTrue);
  }
  
  @Test
  public void testVariableWritableFunctionFalseCondition() throws Exception
  {
    VariableWritableFunction function = new VariableWritableFunction(GROUP_CONTEXT_RELATED);
    boolean isFalse = (boolean) function.execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_VARIABLE);
    
    assertEquals(false, isFalse);
  }
  
  @Test
  public void testVariableWritableFunctionTrueCondition() throws Exception
  {
    boolean isTrue = (boolean) new VariableWritableFunction(GROUP_DEFAULT).execute(ev, null, CHILD_CONTEXT, V_TEST);
    
    assertEquals(true, isTrue);
  }
  
  @Test
  public void testCallFunction() throws Exception
  {
    DataTable table = new SimpleDataTable(TableFormat.EMPTY_FORMAT);
    DataTable result = (DataTable) new CallFunctionFunction().execute(ev, ENV, CHILD_CONTEXT, COPY, table);
    
    assertNotNull(result);
  }
  
  @Test
  public void testCallFunctionEx() throws Exception
  {
    DataTable table = new SimpleDataTable(AbstractContext.REPLICATE_INPUT_FORMAT);
    DataTable result = (DataTable) new CallFunctionExFunction().execute(ev, ENV, CHILD_CONTEXT, COPY, table);
    
    assertNotNull(result);
  }
  
  @Test
  public void testCallFunctionWithSameFormat() throws Exception
  {
    String format = "<<name><S><F=RHK><A=>><<description><S><F=R><A=><D=Variable>><<replicate><B><A=0><D=Replicate>><<fields><T><A=<F=>><D=Fields>><<value><T><A=<F=>><D=Value>>";
    DataTable table = new SimpleDataTable(new TableFormat(format, new ClassicEncodingSettings(true)));
    DataTable result = (DataTable) new CallFunctionFunction().execute(ev, ENV, CHILD_CONTEXT, COPY, table);
    
    assertNotNull(result);
  }
  
  @Test
  public void testFireEventFunction() throws Exception
  {
    int level = 1;
    Long result = (Long) new FireEventFunction().execute(ev, ENV, CHILD_CONTEXT, E_TEST, level);
    
    assertNotNull(result);
  }
  
  @Test
  public void testFullDescriptionFunction() throws Exception
  {
    String delimiter = "|";
    String result = (String) new FullDescriptionFunction().execute(ev, null, CHILD_CONTEXT, delimiter);
    
    assertNotNull(result);
  }
  
  @Test
  public void testGetVariableFunction() throws Exception
  {
    String expected = ev.getDefaultResolver().getDefaultContext().getChild(CHILD_CONTEXT).getVariable(V_TEST).getFormat().toString();
    DataTable result = (DataTable) new GetVariableFunction().execute(ev, ENV, CHILD_CONTEXT, V_TEST);
    
    assertNotNull(result);
    assertNotNull(result.rec());
    assertEquals(expected, result.getFormat().toString());
  }
  
  @Test
  public void testSetVariableFieldFunction() throws Exception
  {
    new SetVariableFieldFunction().execute(ev, ENV, CHILD_CONTEXT, V_TEST, VF_TEST_INT, 0, 444);
    Context context = ev.getDefaultResolver().getDefaultContext().getChild(CHILD_CONTEXT);
    DataTable dt = context.getVariable(V_TEST, ev.getDefaultResolver().getCallerController());
    
    int result = dt.rec().getInt(0);
    
    assertEquals(444, result);
  }
  
  @Test
  public void testSetVariableFunction() throws Exception
  {
    new SetVariableFunction().execute(ev, ENV, CHILD_CONTEXT, V_TEST, 42);
    Context context = ev.getDefaultResolver().getDefaultContext().getChild(CHILD_CONTEXT);
    DataTable dt = context.getVariable(V_TEST, ev.getDefaultResolver().getCallerController());
    
    int result = dt.rec().getInt(0);
    
    assertEquals(42, result);
  }
  
  @Test
  public void testSetVariableExFunction() throws Exception
  {
    SimpleDataTable value = new SimpleDataTable(VFT_TEST, (Object) 42);
    new SetVariableExFunction().execute(ev, ENV, CHILD_CONTEXT, V_TEST, value);
    Context context = ev.getDefaultResolver().getDefaultContext().getChild(CHILD_CONTEXT);
    DataTable dt = context.getVariable(V_TEST, ev.getDefaultResolver().getCallerController());
    
    int result = dt.rec().getInt(0);
    
    assertEquals(42, result);
  }
  
  @Test
  public void testSetVariableFunctionWithDataTableParam() throws Exception
  {
    TableFormat tf = new TableFormat(1, 1);
    tf.addField(FieldFormat.create("<int><I>"));
    DataTable table = new SimpleDataTable(tf, true);
    table.rec().setValue(0, 11);
    new SetVariableFunction().execute(ev, ENV, CHILD_CONTEXT, V_TEST, table);
    Context context = ev.getDefaultResolver().getDefaultContext().getChild(CHILD_CONTEXT);
    DataTable dt = context.getVariable(V_TEST, ev.getDefaultResolver().getCallerController());
    
    assertNotNull(dt);
    assertEquals(table.rec().getInt(0), dt.rec().getInt(0));
  }
  
  @Test
  public void testSetVariableRecordFunction() throws Exception
  {
    new SetVariableRecordFunction().execute(ev, null, CHILD_CONTEXT, V_TEST, 0, 33);
    Context context = ev.getDefaultResolver().getDefaultContext().getChild(CHILD_CONTEXT);
    DataTable dt = context.getVariable(V_TEST, ev.getDefaultResolver().getCallerController());
    
    assertEquals(33, (int) dt.rec().getInt(0));
  }
  
  @Test
  public void testCallFunctionEvaluationException() throws Exception
  {
    try
    {
      new CallFunctionFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_FUNCTION);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testFireEventEvaluationException() throws Exception
  {
    try
    {
      new FireEventFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_EVENT, 0);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testFireEventContextException() throws Exception
  {
    try
    {
      new FireEventFunction().execute(ev, null, CHILD_CONTEXT, NOT_AVAILABLE_EVENT, 0);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testFireEventWrongLevel() throws Exception
  {
    try
    {
      new FireEventFunction().execute(ev, null, CHILD_CONTEXT, E_TEST, 10);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testFullDescriptionFunctionEvaluationException() throws Exception
  {
    try
    {
      new FullDescriptionFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testGetVariableFunctionEvaluationException() throws Exception
  {
    try
    {
      new GetVariableFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_VARIABLE);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testGetVariableDoesNotExistVar() throws Exception
  {
    try
    {
      new GetVariableFunction().execute(ev, null, CHILD_CONTEXT, NOT_AVAILABLE_VARIABLE);
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testSetVariableFieldEvaluationException() throws Exception
  {
    try
    {
      new SetVariableFieldFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, V_TEST, VF_TEST_INT, 0, 444);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testSetVariableFunctionEvaluationException() throws Exception
  {
    try
    {
      new SetVariableFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, NOT_AVAILABLE_VARIABLE);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testSetVariableFunctionContextException() throws Exception
  {
    try
    {
      new SetVariableFunction().execute(ev, null, CHILD_CONTEXT, NOT_AVAILABLE_VARIABLE);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testSetVariableRecordEvaluationException() throws Exception
  {
    try
    {
      new SetVariableRecordFunction().execute(ev, null, NOT_AVAILABLE_CONTEXT, V_TEST, 0, 33);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
  
  @Test
  public void testSetVariableRecordContextException() throws Exception
  {
    try
    {
      new SetVariableRecordFunction().execute(ev, null, CHILD_CONTEXT, NOT_AVAILABLE_VARIABLE, 0, 33);
      fail();
    }
    catch (Exception ignore)
    {
      
    }
  }
}
