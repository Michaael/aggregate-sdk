package examples.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.examples.AbstractTestExamples;
import com.tibbo.aggregate.common.protocol.ProtocolVersion;
import com.tibbo.aggregate.common.server.EditableChildContextConstants;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ManageUsersTest extends AbstractTestExamples
{
  private static final String TEST_USER = "testUser";
  private static final String TEST_USER_PASSWORD = "testUserPwd123";
  private static final String FIRST_NAME = "firstname";
  
  private Context context;
  
  @BeforeEach
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    context = getContext(Contexts.CTX_USERS);
  }
  
  @AfterEach
  @Override
  protected void tearDown() throws Exception
  {
    prepareForTesting();
    super.tearDown();
  }
  
  @Test
  public void testCreateUser() throws Exception
  {
    Context userContext = createUser();
    
    assertEquals(TEST_USER, userContext.getName());
  }
  
  @Test
  public void testGetVariable() throws Exception
  {
    DataTable variable = createUser().getVariable(EditableChildContextConstants.V_CHILD_INFO);
    
    assertNotNull(variable);
  }
  
  @Test
  public void testEditUser() throws Exception
  {
    Context userContext = createUser();
    DataTable oldInfo = userContext.getVariable(EditableChildContextConstants.V_CHILD_INFO);
    String oldFirstName = oldInfo.rec().getString(FIRST_NAME);
    
    assertEquals(null, oldFirstName);
    
    String newFirstName = "John";
    ManageUsers.editTestUser(userContext);
    DataTable newInfo = userContext.getVariable(EditableChildContextConstants.V_CHILD_INFO);
    
    assertEquals(newFirstName, newInfo.rec().getString(FIRST_NAME));
  }
  
  @Test
  public void testDeleteUser() throws Exception
  {
    createUser();
    ManageUsers.deleteTestUser(context, TEST_USER);
    
    assertNotEquals(0, context.getFunctionData("delete").getExecutionCount());
  }
  
  private void prepareForTesting() throws Exception
  {
    Context userContext = context.get(ContextUtils.userContextPath(TEST_USER));
    if (userContext != null)
      ManageUsers.deleteTestUser(context, TEST_USER);
  }
  
  private Context createUser() throws Exception
  {
    return ManageUsers.createTestUser(context.getContextManager(), TEST_USER, TEST_USER_PASSWORD);
  }

  @Test
  public void testSetVar() throws Exception
  {
    ContextManager cm = rlc.getContextManager();
    Context q5 = cm.get("users.admin.models.q5");
    DataTable childInfo = q5.getVariable("childInfo");

    q5.setVariable("childInfo", childInfo);
    DataTable childInfo2 = q5.getVariable("childInfo");

    assertEquals(childInfo, childInfo2);
  }

  @Test
  public void testEncodeTable() throws Exception
  {
    ClassicEncodingSettings es = new ClassicEncodingSettings(false);
    es.setProtocolVersion(ProtocolVersion.V2);
    es.setEncodeFormat(true);

    String frontendEncode = "\u001CF\u001E\u001C\u001Cname\u001D\u001CS\u001D\u001CF\u001EC\u001D\u001CA\u001E\u001D\u001CD\u001EName\u001D\u001CH\u001EName of the object and corresponding system context, required to refer to this object from other parts of the system. It should satisfy the context naming conventions. WARNING: changing name will corrupt all references to the object.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Name may contain only Latin letters, digits, and underscores\u001D\u001D\u001D\u001C\u001Cdescription\u001D\u001CS\u001D\u001CF\u001EC\u001D\u001CA\u001E\u001D\u001CD\u001EDescription\u001D\u001CH\u001EThe model context description (the text is displayed in the context tree).\u001D\u001CV\u001E\u001CL\u001E0 100\u001D\u001CR\u001E[^\\p{Cntrl}]*^^Description may contain only printable characters\u001D\u001D\u001D\u001C\u001Ctype\u001D\u001CI\u001D\u001CA\u001E1\u001D\u001CD\u001EType\u001D\u001CH\u001EThis option specifies the model type: Relative, Absolute or Instantiable.\u001D\u001CS\u001E\u001CRelative\u001E0\u001D\u001CAbsolute\u001E1\u001D\u001CInstantiable\u001E2\u001D\u001D\u001D\u001C\u001CvalidityExpression\u001D\u001CS\u001D\u001CA\u001E\u001D\u001CD\u001EValidity Expression\u001D\u001CH\u001EActs differently for relative and instantiable model types. For a relative model, determines which context(s) should the model attach to. For an instantiable model, determines which context(s) should model instance containers be attached to.\u001D\u001CE\u001Eexpression\u001D\u001CO\u001E%<F%=%<%<context%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Default Context%>%>%<%<table%>%<T%>%<F%=N%>%<A%=\u001A%>%<D%=Default Table%>%>%<%<references%>%<T%>%<F%=N%>%<A%=%<F%=%<%<reference%>%<S%>%<A%=%>%<D%=Reference%>%>%<%<description%>%<S%>%<A%=%>%<D%=Description%>%>%>%>%<D%=References%>%>%<%<expectedResult%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Expected Result%>%>%<%<contextDescription%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Default Context Description%>%>%<%<tableDescription%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Default Table Description%>%>%<M%=1%>%<X%=1%>%>%<R%=%<\u001A%>%<\u001A%>%<%<R%=%<.:#type%>%<Context Type%>%>%<R%=%<.:%>%<Context Path%>%>%>%<\u001A%>%<\u001A%>%<\u001A%>%>\u001D\u001CG\u001EValidity\u001D\u001D\u001C\u001CvalidityListeners\u001D\u001CT\u001D\u001CA\u001E%<F%=%<%<mask%>%<S%>%<F%=K%>%<A%=%>%<D%=Context Mask%>%<H%=Mask of contexts to monitor events in%%$\n" +
            "%>%<E%=contextmask%>%<I%=fi_context%>%>%<%<event%>%<S%>%<F%=EK%>%<A%=%>%<D%=Event%>%<H%=Name of Event to listen for%>%<I%=fi_event%>%>%<%<expression%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Target Expression%>%<H%=If specified, points to a context those validity should be checked. If Target Expression is not specified (what is suitable in most cases), the system checks validity of context in that Event has occurred.%>%<E%=expression%>%<I%=fi_expression%>%>%<V%=%<K%=%>%>%<B%=%<event#choices%={utilities:eventsByMask('{mask}')}%>%>%>\u001D\u001CD\u001EValidity Update Rules\u001D\u001CH\u001EA list of context masks and event names. If event specified by Event field of this table occurs in any of context matching to the mask specified by Mask field in the same record, Validity Expression will be recalculated for this context. This allows to make model valid or invalid for a certain context if some changes occur in it.\u001D\u001CG\u001EValidity\u001D\u001D\u001C\u001CcontainerType\u001D\u001CS\u001D\u001CA\u001Eobjects\u001D\u001CD\u001EContainer Type\u001D\u001CH\u001EThis option defines the context type of the model containers. The Type string can include only English letters, numbers and underscores.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Type may contain only letters, digits, and underscores\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CcontainerTypeDescription\u001D\u001CS\u001D\u001CA\u001EObjects\u001D\u001CD\u001EContainer Type Description\u001D\u001CH\u001EThis option defines a human-readable description of the model container context type.\u001D\u001CV\u001E\u001CL\u001E0 100\u001D\u001CR\u001E[^\\p{Cntrl}]*^^Description may contain only printable characters\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CcontainerName\u001D\u001CS\u001D\u001CA\u001Eobjects\u001D\u001CD\u001EContainer Name\u001D\u001CH\u001EThis option defines the context name of the model containers. It should satisfy the context naming conventions.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Name may contain only Latin letters, digits, and underscores\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CdefaultContext\u001D\u001CS\u001D\u001CF\u001EN\u001D\u001CA\u001E\u001A\u001D\u001CD\u001EDefault Context\u001D\u001CH\u001EDefault context which is used in all internal expressions.\u001D\u001CE\u001Econtext\u001D\u001CG\u001EValidity\u001D\u001D\u001C\u001CobjectType\u001D\u001CS\u001D\u001CA\u001Eobject\u001D\u001CD\u001EObject Type\u001D\u001CH\u001EThis option is applicable to instantiable models only. It defines the context type of the model instances. The Type string can include only English letters, numbers and underscores.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Type may contain only letters, digits, and underscores\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CobjectTypeDescription\u001D\u001CS\u001D\u001CA\u001EObject\u001D\u001CD\u001EObject Type Description\u001D\u001CH\u001EThis option is applicable to instantiable models only. It defines a human-readable description of the model instance context type.\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CobjectNamingExpression\u001D\u001CS\u001D\u001CA\u001E\u001D\u001CD\u001EObject Naming Expression\u001D\u001CH\u001EThis option is applicable to instantiable models only. It defines description of the instantiable model's context.\u001D\u001CE\u001Eexpression\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001Cenabled\u001D\u001CB\u001D\u001CA\u001E1\u001D\u001CD\u001EEnabled\u001D\u001CH\u001EIf this flag unchecked, the model is deactivated and doesn't perform any active binding processing. However, all variables, function and event definitions added by this model remain available if the model is disabled. Instances of a disabled instantiable model are also not hidden or removed from server context tree.\u001D\u001D\u001C\u001CruleSetCallStackDepthThreshold\u001D\u001CI\u001D\u001CF\u001EA\u001D\u001CA\u001E100\u001D\u001CD\u001ERule Set Call Stack Depth Threshold\u001D\u001CH\u001EAn Information event is generated in this model (and a record is made in the Server log) if the rule set call stack depth exceeds the value defined by this option. This can happen when a rule set is called recursively.\u001D\u001CV\u001E\u001CL\u001E1 2147483647\u001D\u001D\u001D\u001C\u001CnormalConcurrentBindings\u001D\u001CI\u001D\u001CA\u001E3\u001D\u001CD\u001ENormal Concurrent Bindings\u001D\u001CH\u001EThis option defines the core size of the model thread pool, i.e. the standard bindings count processed simultaneously.\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001CmaximumConcurrentBindings\u001D\u001CI\u001D\u001CA\u001E30\u001D\u001CD\u001EMaximum Concurrent Bindings\u001D\u001CH\u001EThis option defines the maximum size of the model thread pool, i.e. the number of concurrently processed bindings allowed in case the binding queue overflows.\u001D\u001CV\u001E\u001CL\u001E1 2147483647\u001D\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001CmaximumBindingQueueLength\u001D\u001CI\u001D\u001CA\u001E100\u001D\u001CD\u001EMaximum Unprocessed Binding Queue Length\u001D\u001CH\u001EThis option defines how many unprocessed binding operations can be queued before the model's thread pool size exceeds its core size in relation to the maximum size.\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001ClogBindingsExecution\u001D\u001CB\u001D\u001CA\u001E0\u001D\u001CD\u001ELog Bindings Execution\u001D\u001CH\u001EThis option enables and disables the logging of all executed bindings.\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001Cprotected\u001D\u001CB\u001D\u001CF\u001EH\u001D\u001CA\u001E0\u001D\u001D\u001CM\u001E1\u001D\u001CX\u001E1\u001D\u001CV\u001E\u001CE\u001Ee21heGltdW1Db25jdXJyZW50QmluZGluZ3N9ID49IHtub3JtYWxDb25jdXJyZW50QmluZGluZ3N9\n" +
            "ID8gbnVsbCA6ICdNYXhpbXVtIGNvbmN1cnJlbnQgYmluZGluZyBjb3VudCBtdXN0IGJlIGdyZWF0\n" +
            "ZXIgb3IgZXF1YWwgdG8gbm9ybWFsIGNvbmN1cnJlbnQgYmluZGluZyBjb3VudCc=\n" +
            "\u001D\u001D\u001CB\u001E\u001CvalidityExpression#hidden\u001E{type} == 1\u001D\u001CvalidityListeners#hidden\u001E{type} == 1\u001D\u001CcontainerType#hidden\u001E{type} != 2\u001D\u001CcontainerTypeDescription#hidden\u001E{type} != 2\u001D\u001CcontainerName#hidden\u001E{type} != 2\u001D\u001CobjectType#hidden\u001E{type} != 2\u001D\u001CobjectTypeDescription#hidden\u001E{type} != 2\u001D\u001CobjectNamingExpression#hidden\u001E{type} != 2\u001D\u001CdefaultContext#hidden\u001E{type} != 0\u001D\u001CvalidityExpression#options\u001EexpressionEditorOptions({type} == 0 ? {defaultContext} : {.:})\u001D\u001D\u001D\u001CR\u001E\u001Cq5\u001D\u001C\u001D\u001C1\u001D\u001C\u001D\u001C%<F%=%<%<mask%>%<S%>%<F%=K%>%<A%=%>%<D%=Context Mask%>%<H%=Mask of contexts to monitor events in%%$\n" +
            "%>%<E%=contextmask%>%<I%=fi_context%>%>%<%<event%>%<S%>%<F%=EK%>%<A%=%>%<D%=Event%>%<H%=Name of Event to listen for%>%<I%=fi_event%>%>%<%<expression%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Target Expression%>%<H%=If specified, points to a context those validity should be checked. If Target Expression is not specified (what is suitable in most cases), the system checks validity of context in that Event has occurred.%>%<E%=expression%>%<I%=fi_expression%>%>%<V%=%<K%=%>%>%<B%=%<event#choices%={utilities:eventsByMask('{mask}')}%>%>%>\u001D\u001Cobjects\u001D\u001CObjects\u001D\u001Cobjects\u001D\u001C\u001A\u001D\u001Cobject\u001D\u001CObject\u001D\u001C\u001D\u001C1\u001D\u001C100\u001D\u001C3\u001D\u001C30\u001D\u001C100\u001D\u001C0\u001D\u001C0\u001D\u001D\u001CT\u001E2020-09-09 08:50:22.335\u001D";

    String sourceGotFromFront = "\u001CF\u001E\u001C\u001Cname\u001D\u001CS\u001D\u001CF\u001EC\u001D\u001CA\u001E\u001D\u001CD\u001EName\u001D\u001CH\u001EName of the object and corresponding system context, required to refer to this object from other parts of the system. It should satisfy the context naming conventions. WARNING: changing name will corrupt all references to the object.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Name may contain only Latin letters, digits, and underscores\u001D\u001D\u001D\u001C\u001Cdescription\u001D\u001CS\u001D\u001CF\u001EC\u001D\u001CA\u001E\u001D\u001CD\u001EDescription\u001D\u001CH\u001EThe model context description (the text is displayed in the context tree).\u001D\u001CV\u001E\u001CL\u001E0 100\u001D\u001CR\u001E[^\\p{Cntrl}]*^^Description may contain only printable characters\u001D\u001D\u001D\u001C\u001Ctype\u001D\u001CI\u001D\u001CA\u001E1\u001D\u001CD\u001EType\u001D\u001CH\u001EThis option specifies the model type: Relative, Absolute or Instantiable.\u001D\u001CS\u001E\u001CRelative\u001E0\u001D\u001CAbsolute\u001E1\u001D\u001CInstantiable\u001E2\u001D\u001D\u001D\u001C\u001CvalidityExpression\u001D\u001CS\u001D\u001CA\u001E\u001D\u001CD\u001EValidity Expression\u001D\u001CH\u001EActs differently for relative and instantiable model types. For a relative model, determines which context(s) should the model attach to. For an instantiable model, determines which context(s) should model instance containers be attached to.\u001D\u001CE\u001Eexpression\u001D\u001CO\u001E%<F%=%<%<context%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Default Context%>%>%<%<table%>%<T%>%<F%=N%>%<A%=\u001A%>%<D%=Default Table%>%>%<%<references%>%<T%>%<F%=N%>%<A%=%<F%=%<%<reference%>%<S%>%<A%=%>%<D%=Reference%>%>%<%<description%>%<S%>%<A%=%>%<D%=Description%>%>%>%>%<D%=References%>%>%<%<expectedResult%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Expected Result%>%>%<%<contextDescription%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Default Context Description%>%>%<%<tableDescription%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Default Table Description%>%>%<M%=1%>%<X%=1%>%>%<R%=%<\u001A%>%<\u001A%>%<%<R%=%<.:#type%>%<Context Type%>%>%<R%=%<.:%>%<Context Path%>%>%>%<\u001A%>%<\u001A%>%<\u001A%>%>\u001D\u001CG\u001EValidity\u001D\u001D\u001C\u001CvalidityListeners\u001D\u001CT\u001D\u001CA\u001E%<F%=%<%<mask%>%<S%>%<F%=K%>%<A%=%>%<D%=Context Mask%>%<H%=Mask of contexts to monitor events in%%$\n" +
            "%>%<E%=contextmask%>%<I%=fi_context%>%>%<%<event%>%<S%>%<F%=EK%>%<A%=%>%<D%=Event%>%<H%=Name of Event to listen for%>%<I%=fi_event%>%>%<%<expression%>%<S%>%<F%=N%>%<A%=\u001A%>%<D%=Target Expression%>%<H%=If specified, points to a context those validity should be checked. If Target Expression is not specified (what is suitable in most cases), the system checks validity of context in that Event has occurred.%>%<E%=expression%>%<I%=fi_expression%>%>%<V%=%<K%=%>%>%<B%=%<event#choices%={utilities:eventsByMask('{mask}')}%>%>%>\u001D\u001CD\u001EValidity Update Rules\u001D\u001CH\u001EA list of context masks and event names. If event specified by Event field of this table occurs in any of context matching to the mask specified by Mask field in the same record, Validity Expression will be recalculated for this context. This allows to make model valid or invalid for a certain context if some changes occur in it.\u001D\u001CG\u001EValidity\u001D\u001D\u001C\u001CcontainerType\u001D\u001CS\u001D\u001CA\u001Eobjects\u001D\u001CD\u001EContainer Type\u001D\u001CH\u001EThis option defines the context type of the model containers. The Type string can include only English letters, numbers and underscores.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Type may contain only letters, digits, and underscores\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CcontainerTypeDescription\u001D\u001CS\u001D\u001CA\u001EObjects\u001D\u001CD\u001EContainer Type Description\u001D\u001CH\u001EThis option defines a human-readable description of the model container context type.\u001D\u001CV\u001E\u001CL\u001E0 100\u001D\u001CR\u001E[^\\p{Cntrl}]*^^Description may contain only printable characters\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CcontainerName\u001D\u001CS\u001D\u001CA\u001Eobjects\u001D\u001CD\u001EContainer Name\u001D\u001CH\u001EThis option defines the context name of the model containers. It should satisfy the context naming conventions.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Name may contain only Latin letters, digits, and underscores\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CdefaultContext\u001D\u001CS\u001D\u001CF\u001EN\u001D\u001CA\u001E\u001A\u001D\u001CD\u001EDefault Context\u001D\u001CH\u001EDefault context which is used in all internal expressions.\u001D\u001CE\u001Econtext\u001D\u001CG\u001EValidity\u001D\u001D\u001C\u001CobjectType\u001D\u001CS\u001D\u001CA\u001Eobject\u001D\u001CD\u001EObject Type\u001D\u001CH\u001EThis option is applicable to instantiable models only. It defines the context type of the model instances. The Type string can include only English letters, numbers and underscores.\u001D\u001CV\u001E\u001CL\u001E1 50\u001D\u001CR\u001E\\w+^^Type may contain only letters, digits, and underscores\u001D\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CobjectTypeDescription\u001D\u001CS\u001D\u001CA\u001EObject\u001D\u001CD\u001EObject Type Description\u001D\u001CH\u001EThis option is applicable to instantiable models only. It defines a human-readable description of the model instance context type.\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001CobjectNamingExpression\u001D\u001CS\u001D\u001CA\u001E\u001D\u001CD\u001EObject Naming Expression\u001D\u001CH\u001EThis option is applicable to instantiable models only. It defines description of the instantiable model's context.\u001D\u001CE\u001Eexpression\u001D\u001CG\u001EInstantiable Model Settings\u001D\u001D\u001C\u001Cenabled\u001D\u001CB\u001D\u001CA\u001E1\u001D\u001CD\u001EEnabled\u001D\u001CH\u001EIf this flag unchecked, the model is deactivated and doesn't perform any active binding processing. However, all variables, function and event definitions added by this model remain available if the model is disabled. Instances of a disabled instantiable model are also not hidden or removed from server context tree.\u001D\u001D\u001C\u001CruleSetCallStackDepthThreshold\u001D\u001CI\u001D\u001CF\u001EA\u001D\u001CA\u001E100\u001D\u001CD\u001ERule Set Call Stack Depth Threshold\u001D\u001CH\u001EAn Information event is generated in this model (and a record is made in the Server log) if the rule set call stack depth exceeds the value defined by this option. This can happen when a rule set is called recursively.\u001D\u001CV\u001E\u001CL\u001E1 2147483647\u001D\u001D\u001D\u001C\u001CnormalConcurrentBindings\u001D\u001CI\u001D\u001CA\u001E3\u001D\u001CD\u001ENormal Concurrent Bindings\u001D\u001CH\u001EThis option defines the core size of the model thread pool, i.e. the standard bindings count processed simultaneously.\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001CmaximumConcurrentBindings\u001D\u001CI\u001D\u001CA\u001E30\u001D\u001CD\u001EMaximum Concurrent Bindings\u001D\u001CH\u001EThis option defines the maximum size of the model thread pool, i.e. the number of concurrently processed bindings allowed in case the binding queue overflows.\u001D\u001CV\u001E\u001CL\u001E1 2147483647\u001D\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001CmaximumBindingQueueLength\u001D\u001CI\u001D\u001CA\u001E100\u001D\u001CD\u001EMaximum Unprocessed Binding Queue Length\u001D\u001CH\u001EThis option defines how many unprocessed binding operations can be queued before the model's thread pool size exceeds its core size in relation to the maximum size.\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001ClogBindingsExecution\u001D\u001CB\u001D\u001CA\u001E0\u001D\u001CD\u001ELog Bindings Execution\u001D\u001CH\u001EThis option enables and disables the logging of all executed bindings.\u001D\u001CG\u001EAdvanced Binding Settings\u001D\u001D\u001C\u001Cprotected\u001D\u001CB\u001D\u001CF\u001EH\u001D\u001CA\u001E0\u001D\u001D\u001CM\u001E1\u001D\u001CX\u001E1\u001D\u001CV\u001E\u001CE\u001Ee21heGltdW1Db25jdXJyZW50QmluZGluZ3N9ID49IHtub3JtYWxDb25jdXJyZW50QmluZGluZ3N9";

    DataTable test = new SimpleDataTable(frontendEncode, es, true);

    ContextManager cm = rlc.getContextManager();
    Context q5 = cm.get("users.admin.models.q5");
    q5.setVariable("childInfo", test);



    DataTable childInfo = q5.getVariable("childInfo");

    assertEquals(childInfo, test);

  }
}
