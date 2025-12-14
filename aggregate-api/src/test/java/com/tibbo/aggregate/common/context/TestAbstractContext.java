package com.tibbo.aggregate.common.context;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.action.ActionDefinition;
import com.tibbo.aggregate.common.action.BasicActionDefinition;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.security.ServerPermissionChecker;
import com.tibbo.aggregate.common.tests.StubContext;
import com.tibbo.aggregate.common.util.Icons;
import com.tibbo.aggregate.common.util.Pair;

public class TestAbstractContext
{
  
  DefaultContextManager contextManager;
  AbstractContext root;
  
  @BeforeEach
  public void setUp() throws Exception
  {
    root = new StubContext("root");
    contextManager = new DefaultContextManager(root, true);
  }
  
  @Test
  public void testAddChild()
  {
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    assertEquals(1, root.getChildren().size());
    assertEquals(child1, root.getChildren().get(0));
    assertEquals(root, child1.getParent());
  }
  
  @Test
  public void testAddChildWithIndexWhenSortingEnabled()
  {
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    StubContext child2 = new StubContext("child2");
    assertThrows(IllegalStateException.class, () -> {
      root.addChild(child2, 0);
    });
  }
  
  @Test
  public void testAddChildWithIndexWhenSortingDisabled()
  {
    root.setChildrenSortingEnabled(false);
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    StubContext child2 = new StubContext("child2");
    root.addChild(child2, 0);
    assertEquals(child2, root.getChildren().get(0));
  }
  
  @Test
  public void testDestroyChild()
  {
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    root.destroyChild(child1, false);
    assertEquals(0, root.getChildren().size());
    assertNull(child1.getParent());
  }
  
  @Test
  public void testDestroy()
  {
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    assertTrue(root.isStarted());
    root.destroy(false);
    assertEquals(0, root.getChildren().size());
    assertTrue(root.isStopped());
  }
  
  @Test
  public void testMoveAndRename() throws ContextException
  {
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    StubContext child2 = new StubContext("child2");
    child1.addChild(child2);
    child2.move(root, "newChild2");
    assertEquals(2, root.getChildren().size());
    assertNotNull(root.getChild("newChild2"));
    assertNull(child1.getChild("child2"));
  }
  
  @Test
  public void testReorderChildWhenSortingEnabled()
  {
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    StubContext child2 = new StubContext("child2");
    root.addChild(child2);
    assertThrows(IllegalStateException.class, () -> {
      root.reorderChild(child1, 1);
    });
    
    root.reorderChild(child2, 0);
  }
  
  @Test
  public void testReorderChildWhenSortingDisabled()
  {
    root.setChildrenSortingEnabled(false);
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    StubContext child2 = new StubContext("child2");
    root.addChild(child2);
    
    root.reorderChild(child2, 0);
    assertEquals(child2, root.getChildren().get(0));
  }
  
  @Test
  public void testGetFunctionDefinitions()
  {
    assertEquals(5, root.getFunctionDefinitions().size());
  }
  
  @Test
  public void testGetFunctionDefinitionsFromDefaultGroup()
  {
    assertFalse(root.getFunctionDefinitions(ContextUtils.GROUP_DEFAULT).isEmpty());
  }
  
  @Test
  public void testGetFunctionDefinitionsIncludeHidden()
  {
    assertEquals(10, root.getFunctionDefinitions(null, true).size());
  }
  
  @Test
  public void testGetActionDefinitions()
  {
    root.addActionDefinition(new BasicActionDefinition("action"));
    assertEquals(1, root.getActionDefinitions().size());
  }
  
  @Test
  public void testAddAlreadyExistentActionDefinitions()
  {
    root.addActionDefinition(new BasicActionDefinition("action"));
    
    final BasicActionDefinition newDefinition = new BasicActionDefinition("action");
    root.addActionDefinition(newDefinition);
    
    assertEquals(newDefinition, root.getActionDefinition("action"));
  }
  
  @Test
  public void testGetActionDefinition()
  {
    ActionDefinition ad = new BasicActionDefinition("action");
    ActionDefinition ad2 = new BasicActionDefinition("action2");
    root.addActionDefinition(ad);
    root.addActionDefinition(ad2);
    assertEquals(ad, root.getActionDefinition("action"));
  }
  
  @Test
  public void testRemoveActionDefinition()
  {
    ActionDefinition ad = new BasicActionDefinition("action");
    root.addActionDefinition(ad);
    root.removeActionDefinition("action");
    assertNull(root.getActionDefinition("action"));
  }
  
  @Test
  public void testGetEventDefinitions()
  {
    assertEquals(2, root.getEventDefinitions().size());
  }
  
  @Test
  public void testGetEventDefinition()
  {
    assertEquals(17, root.getEventDefinitions(null, true).size());
  }
  
  @Test
  public void testGetEventDefinitionDefaultGroup()
  {
    assertEquals(2, root.getEventDefinitions(ContextUtils.GROUP_DEFAULT).size());
  }
  
  @Test
  public void testAddEventDefinition()
  {
    EventDefinition ad = new EventDefinition("event", new TableFormat());
    root.addEventDefinition(ad);
    assertNotNull(root.getEventDefinition("event"));
  }
  
  @Test
  public void testAddAlreadyExistentEventDefinition()
  {
    EventDefinition newDefinition = new EventDefinition("test", new TableFormat());
    root.addEventDefinition(newDefinition);
    
    assertEquals(newDefinition, root.getEventDefinition("test"));
  }
  
  @Test
  public void testRemoveEventDefinition()
  {
    EventDefinition ad = new EventDefinition("event", new TableFormat());
    root.addEventDefinition(ad);
    root.removeEventDefinition("event");
    assertNull(root.getEventDefinition("event"));
  }
  
  @Test
  public void testAddFunctionDefinition()
  {
    FunctionDefinition fd = createSimpleFD("testFunction");
    root.addFunctionDefinition(fd);
    assertNotNull(root.getFunctionDefinition("testFunction"));
  }
  
  @Test
  public void testAddAlreadyExistentFunctionDefinition()
  {
    FunctionDefinition newDefinition = createSimpleFD("function");
    root.addFunctionDefinition(newDefinition);
    
    assertEquals(newDefinition, root.getFunctionDefinition("function"));
  }
  
  @Test
  public void testRemoveFunctionDefinition()
  {
    FunctionDefinition fd = createSimpleFD("testFunction");
    root.addFunctionDefinition(fd);
    root.removeFunctionDefinition("testFunction");
    assertNull(root.getFunctionDefinition("testFunction"));
  }
  
  @Test
  public void testAddVariableDefinition()
  {
    VariableDefinition fd = createSimpleVD("variable");
    root.addVariableDefinition(fd);
    assertNotNull(root.getVariableDefinition("variable"));
  }
  
  @Test
  public void testAddAlreadyExistentVariableDefinition()
  {
    VariableDefinition variableDefinition = createSimpleVD("variable");
    root.addVariableDefinition(variableDefinition);
    variableDefinition = createSimpleVD("variable");
    root.addVariableDefinition(variableDefinition);
    
    assertEquals(variableDefinition, root.getVariableDefinition("variable"));
  }
  
  @Test
  public void testUpdateVariableDefinition()
  {
    VariableDefinition variableDefinition = createSimpleVD("systemVariable");
    variableDefinition.setGroup("");
    root.addVariableDefinition(variableDefinition);
    
    variableDefinition = createSimpleVD("foo", "owner");
    variableDefinition.setGroup("test");
    variableDefinition.setOwner("owner");
    root.addVariableDefinition(variableDefinition);
    
    // For different owner it is not possible to rewrite the variable
    assertEquals(0, root.updateVariableDefinitions(ImmutableMap.of("foo", createSimpleVD("foo", "hacker")), "test", false, true, "hacker").size());
    
    // But for the owner it should be possible
    assertEquals(1, root.updateVariableDefinitions(ImmutableMap.of("foo", createSimpleVD("foo", "owner")), "test", false, true, "owner").size());

    // Adding of the same variable should not affect it
    assertEquals(0, root.updateVariableDefinitions(ImmutableMap.of("foo", createSimpleVD("foo", "owner")), "test", false, true, "owner").size());

    // Nobody can touch system variables without owners
    assertEquals(0, root.updateVariableDefinitions(ImmutableMap.of("systemVariable", createSimpleVD("systemVariable")), "test", false, true, "owner").size());

    assertNotNull(root.getVariableDefinition("systemVariable"));

  }
  
  @Test
  public void testUpdateFunctionDefinition()
  {
    FunctionDefinition functionDefinition = createSimpleFD("systemFunction");
    functionDefinition.setGroup("");
    root.addFunctionDefinition(functionDefinition);
    
    functionDefinition = createSimpleFD("foo");
    functionDefinition.setGroup("test");
    functionDefinition.setOwner("owner");
    root.addFunctionDefinition(functionDefinition);
    
    // For different owner it is not possible to rewrite the function
    assertEquals(0, root.updateFunctionDefinitions(ImmutableMap.of("foo", new Pair<>(createSimpleFD("foo"), true)), "test", false, "hacker").size());
    
    // But for the owner it should be possible
    assertEquals(1, root.updateFunctionDefinitions(ImmutableMap.of("foo", new Pair<>(createSimpleFD("foo"), true)), "test", false, "owner").size());
    
    // Nobody can touch system functions without owners
    assertEquals(0, root.updateFunctionDefinitions(ImmutableMap.of("systemFunction", new Pair<>(createSimpleFD("systemFunction"), true)), "test", false, "owner").size());

    assertNotNull(root.getFunctionDefinition("systemFunction"));
  }

  @Test
  public void testUpdateEventDefinition()
  {
    EventDefinition eventDefinition = createSimpleED("systemEvent");
    eventDefinition.setGroup("");
    root.addEventDefinition(eventDefinition);

    eventDefinition = createSimpleED("foo");
    eventDefinition.setGroup("test");
    eventDefinition.setOwner("owner");
    root.addEventDefinition(eventDefinition);

    // For different owner it is not possible to rewrite the events
    assertEquals(0, root.updateEventDefinitions(ImmutableMap.of("foo", createSimpleED("foo")), "test", false, "hacker").size());

    // But for the owner it should be possible
    assertEquals(1, root.updateEventDefinitions(ImmutableMap.of("foo", createSimpleED("foo")), "test", false, "owner").size());

    // No owner can touch system events without owners
    assertEquals(0, root.updateEventDefinitions(ImmutableMap.of("systemEvent", createSimpleED("systemEvent")), "test", false, "owner").size());

    assertNotNull(root.getEventDefinition("systemEvent"));
  }

  private VariableDefinition createSimpleVD(String name)
  {
    return createSimpleVD(name, null);
  }

  private VariableDefinition createSimpleVD(String name, String owner)
  {
    VariableDefinition variableDefinition = new VariableDefinition(name, new TableFormat(), true, true);
    variableDefinition.setOwner(owner);
    return variableDefinition;
  }
  
  private FunctionDefinition createSimpleFD(String name)
  {
    return new FunctionDefinition(name, new TableFormat(), new TableFormat());
  }

  private EventDefinition createSimpleED(String name)
  {
    return new EventDefinition(name, new TableFormat());
  }
  
  @Test
  public void testModifyVariableDefinitionFormat() throws ContextException
  {
    final String varName = "variable";
    
    final TableFormat format1 = new TableFormat();
    format1.addField(FieldFormat.STRING_FIELD, "strField");
    
    final VariableDefinition variableDefinition = new VariableDefinition(varName, format1, true, true);
    root.addVariableDefinition(variableDefinition);
    
    DataRecord record = new DataRecord(format1);
    record.addString("abc");
    root.addVariableRecord(varName, root.getContextManager().getCallerController(), record);
    
    final DataTable expectedTable1 = new SimpleDataTable(format1);
    expectedTable1.addRecord(record);
    assertEquals(expectedTable1, root.getVariable(varName));
    final DataTable variableData1 = (DataTable) root.getVariableData(varName).getValue();
    assertEquals(expectedTable1, variableData1);
    
    final TableFormat format2 = new TableFormat();
    format2.addField(FieldFormat.STRING_FIELD, "strField");
    format2.addField(FieldFormat.INTEGER_FIELD, "intField");
    variableDefinition.setFormat(format2);
    root.addVariableDefinition(variableDefinition);
    
    final DataTable expectedTable2 = new SimpleDataTable(format2);
    final DataRecord rec = expectedTable2.addRecord();
    rec.addString("abc");
    rec.addInt(0);
    assertEquals(expectedTable2, root.getVariable(varName));
    final DataTable variableData2 = (DataTable) root.getVariableData(varName).getValue();
    assertEquals(expectedTable2, variableData2);
    
    final TableFormat format3 = new TableFormat();
    format3.addField(FieldFormat.STRING_FIELD, "strField");
    variableDefinition.setFormat(format3);
    root.addVariableDefinition(variableDefinition);
    
    assertEquals(expectedTable1, root.getVariable(varName));
    DataTable variableData3 = (DataTable) root.getVariableData(varName).getValue();
    assertEquals(expectedTable1, variableData3);
  }
  
  @Test
  public void testRemoveVariableDefinition()
  {
    VariableDefinition fd = createSimpleVD("variable");
    root.addVariableDefinition(fd);
    root.removeVariableDefinition("variable");
    assertNull(root.getVariableDefinition("variable"));
  }
  
  @Test
  public void testHasParent()
  {
    StubContext child1 = new StubContext("child1");
    root.addChild(child1);
    StubContext child2 = new StubContext("child2");
    root.addChild(child2);
    StubContext child3 = new StubContext("child3");
    child2.addChild(child3);
    
    assertTrue(child1.hasParent(root));
    assertTrue(child3.hasParent(root));
    assertFalse(child3.hasParent(child1));
    assertFalse(root.hasParent(child1));
  }
  
  @Test
  public void testExceptionWhenGetVariableObject()
  {
    assertThrows(ContextRuntimeException.class, () -> {
      root.getVariableObject("test", null);
    });
  }
  
  @Test
  public void testGetVariableObject() throws ContextException
  {
    VariableDefinition vd = new VariableDefinition("testObjectVariable", DataBean.FORMAT, true, true, "", ContextUtils.GROUP_DEFAULT);
    vd.setIconId(Icons.VAR_PROPERTIES);
    vd.setWritePermissions(ServerPermissionChecker.getManagerPermissions());
    vd.setValueClass(DataBean.class);
    StubContext context = new StubContext("test");
    context.addVariableDefinition(vd);
    
    SimpleDataTable data = new SimpleDataTable(DataBean.FORMAT);
    DataRecord dr = data.addRecord();
    dr.setValue(0, "testname");
    dr.setValue(1, "testdesc");
    context.setVariable("testObjectVariable", null, data);
    DataBean bean = (DataBean) context.getVariableObject("testObjectVariable", null);
    assertEquals("testname", bean.getName());
    assertEquals("testdesc", bean.getDescription());
  }
  
  @Test
  public void testGetVariableObjectWhenContextSetupCompleted() throws ContextException
  {
    VariableDefinition vd = new VariableDefinition("testObjectVariable", DataBean.FORMAT, true, true, "", ContextUtils.GROUP_DEFAULT);
    vd.setIconId(Icons.VAR_PROPERTIES);
    vd.setWritePermissions(ServerPermissionChecker.getManagerPermissions());
    vd.setValueClass(DataBean.class);
    root.addVariableDefinition(vd);
    
    SimpleDataTable data = new SimpleDataTable(DataBean.FORMAT);
    DataRecord dr = data.addRecord();
    dr.setValue(0, "testname");
    dr.setValue(1, "testdesc");
    root.setVariable("testObjectVariable", null, data);
    assertEquals(data, root.getVariableObject("testObjectVariable", null));
  }
  
  @Test
  public void testSetVariableField() throws ContextException
  {
    root.setVariableField(StubContext.V_TEST, StubContext.VF_TEST_INT, 123, null);
    assertEquals(new Integer(123), root.getVariable(StubContext.V_TEST).rec().getInt(StubContext.VF_TEST_INT));
  }
  
  @Test
  public void testExceptionWhenSettingVariableField() throws ContextException
  {
    assertThrows(IndexOutOfBoundsException.class, () -> {
      root.setVariableField(StubContext.V_TEST, StubContext.VF_TEST_INT, 1, 123, null);
    });
  }
  
  @Test
  public void testAddRecordToVariable() throws ContextException
  {
    root.addVariableDefinition(new VariableDefinition("testVar", StubContext.VFT_TEST.clone().setMaxRecords(10), true, true));
    root.addVariableRecord("testVar", null, 123);
    assertEquals(new Integer(123), root.getVariable("testVar", null).getRecord(1).getInt(StubContext.VF_TEST_INT));
  }
  
  @Test
  public void testRemoveRecordFromVariable() throws ContextException
  {
    root.addVariableDefinition(new VariableDefinition("testVar", StubContext.VFT_TEST.clone().setMaxRecords(10), true, true));
    root.addVariableRecord("testVar", null, 123);
    root.addVariableRecord("testVar", null, 124);
    root.removeVariableRecords("testVar", null, StubContext.VF_TEST_INT, 123);
    
    assertEquals(2, (int) root.getVariable("testVar", null).getRecordCount());
    assertEquals(new Integer(124), root.getVariable("testVar", null).getRecord(1).getInt(StubContext.VF_TEST_INT));
  }
  
  @Test
  public void testVariableStatus() throws ContextException
  {
    root.enableVariableStatuses(true);
    VariableStatus vs = new VariableStatus("status", "comment");
    root.updateVariableStatus(StubContext.V_TEST, vs, true);
    assertEquals(vs.getStatus(), root.getVariableStatus(root.getVariableDefinition(StubContext.V_TEST)).getStatus());
    assertEquals(vs.getComment(), root.getVariableStatus(root.getVariableDefinition(StubContext.V_TEST)).getComment());
    
    vs = new VariableStatus("status2", "comment2");
    root.updateVariableStatus(StubContext.V_TEST, vs, true);
    assertEquals(vs.getStatus(), root.getVariableStatus(root.getVariableDefinition(StubContext.V_TEST)).getStatus());
    assertEquals(vs.getComment(), root.getVariableStatus(root.getVariableDefinition(StubContext.V_TEST)).getComment());
  }
  
  @Test
  public void testGetVariableStatus() throws ContextException
  {
    root.enableVariableStatuses(true);
    VariableStatus vs = new VariableStatus("status", "comment");
    root.updateVariableStatus(StubContext.V_TEST, vs, true);
    
    SimpleDataTable input = new SimpleDataTable(AbstractContext.FIFT_GET_VARIABLE_STATUS);
    input.addRecord(StubContext.V_TEST);
    VariableStatus res = VariableStatus.ofDataTable(root.callFunction(AbstractContext.F_GET_VARIABLE_STATUS, input));
    assertEquals(vs.getStatus(), res.getStatus());
    assertEquals(vs.getComment(), res.getComment());
  }
  
  @Test
  public void testVariableStatusWhenDisabledStatuses() throws ContextException
  {
    assertThrows(ContextRuntimeException.class, () -> {
      root.updateVariableStatus(StubContext.V_TEST, new VariableStatus("status", "comment"), true);
    });
  }
  
  @Test
  public void testStatusWhenDisabled()
  {
    assertThrows(IllegalStateException.class, () -> {
      root.setStatus(1, "comment");
    });
  }
  
  @Test
  public void testStatus() throws ContextException
  {
    root.enableStatus();
    root.setStatus(1, "comment");
    assertEquals(1, root.getStatus().getStatus());
    assertEquals("comment", root.getStatus().getComment());
  }
  
  @Test
  public void testInvalidNames()
  {
    assertFalse(ContextUtils.isValidContextName("invalid name"));
  }
  
  @Test
  public void testValidNames()
  {
    assertTrue(ContextUtils.isValidContextName(""));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testExecuteTasksConcurrently() throws Exception
  {
    // given
    ThreadPoolExecutor executor = mock(ThreadPoolExecutor.class);
    final int maxWorkers = 10;
    when(executor.getMaximumPoolSize()).thenReturn(maxWorkers);
    final int busyWorkers = 5;
    when(executor.getActiveCount()).thenReturn(busyWorkers);
    when(executor.submit(any(Callable.class)))
        .then(invocation -> {
          Callable<Object> task = invocation.getArgument(0, Callable.class);
          return completedFuture(task.call());
        });
    contextManager.setExecutorService(executor);
    
    AtomicInteger taskCounter = new AtomicInteger(0);
    Callable<Object> aTask = taskCounter::incrementAndGet;
    final int numberOfTasks = 15;
    List<Callable<Object>> tasks = Collections.nCopies(numberOfTasks, aTask);
    
    // when
    final double loadFactor = 0.5;
    root.executeTasksConcurrently(tasks, loadFactor);
    
    // then
    assertEquals(numberOfTasks, taskCounter.get(), "Not all submitted tasks were executed");
    verify(executor, times(numberOfTasks)).submit(aTask);
  }
  
  @SuppressWarnings("UnnecessaryLocalVariable")
  @Test
  public void testExecuteTasksSequentially() throws Exception
  {
    // given
    ThreadPoolExecutor executor = mock(ThreadPoolExecutor.class);
    final int maxWorkers = 10;
    when(executor.getMaximumPoolSize()).thenReturn(maxWorkers);
    final int busyWorkers = maxWorkers; // i.e. there is no free workers in the pool
    when(executor.getActiveCount()).thenReturn(busyWorkers);
    contextManager.setExecutorService(executor);
    
    AtomicInteger taskCounter = new AtomicInteger(0);
    Callable<Object> aTask = taskCounter::incrementAndGet;
    final int numberOfTasks = 15;
    List<Callable<Object>> tasks = Collections.nCopies(numberOfTasks, aTask);
    
    // when
    final double loadFactor = 0.5;
    root.executeTasksConcurrently(tasks, loadFactor);
    
    // then
    assertEquals(numberOfTasks, taskCounter.get());
    verify(executor, times(0)).invokeAll(any(Collection.class));
  }
  
  @Test
  public void testLowLoadFactorLeadsToSequentialExecution() throws Exception
  {
    // given
    ThreadPoolExecutor executor = mock(ThreadPoolExecutor.class);
    final int maxWorkers = 10;
    when(executor.getMaximumPoolSize()).thenReturn(maxWorkers);
    final int busyWorkers = 8; // this means that all the workers are busy
    when(executor.getActiveCount()).thenReturn(busyWorkers);
    contextManager.setExecutorService(executor);
    
    AtomicInteger taskCounter = new AtomicInteger(0);
    Callable<Object> aTask = taskCounter::incrementAndGet;
    final int numberOfTasks = 15;
    List<Callable<Object>> tasks = Collections.nCopies(numberOfTasks, aTask);
    
    // when
    final double loadFactor = 0.2; // when there are just 2 free workers, this value won't allow to utilize them
    root.executeTasksConcurrently(tasks, loadFactor);
    
    // then
    assertEquals(numberOfTasks, taskCounter.get());
    verify(executor, times(0)).invokeAll(any(Collection.class));
  }
}
