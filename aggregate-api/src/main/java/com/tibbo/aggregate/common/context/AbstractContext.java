package com.tibbo.aggregate.common.context;

import static com.tibbo.aggregate.common.context.ContextOperationType.CALL_FUNCTION;
import static com.tibbo.aggregate.common.context.ContextOperationType.FIRE_EVENT;
import static com.tibbo.aggregate.common.context.ContextOperationType.GET_VARIABLE;
import static com.tibbo.aggregate.common.context.ContextOperationType.SET_VARIABLE;
import static com.tibbo.aggregate.common.server.ServerContextConstants.VF_VISIBLE_CHILDREN_PATH;
import static com.tibbo.aggregate.common.structure.PinpointFactory.newPinpointFor;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.tibbo.aggregate.common.AggreGateRuntimeException;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.action.ActionDefinition;
import com.tibbo.aggregate.common.action.BasicActionDefinition;
import com.tibbo.aggregate.common.action.GroupIdentifier;
import com.tibbo.aggregate.common.action.KeyStroke;
import com.tibbo.aggregate.common.action.ResourceMask;
import com.tibbo.aggregate.common.action.TreeMask;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.ValidationException;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.Enrichment;
import com.tibbo.aggregate.common.event.EventEnrichmentRule;
import com.tibbo.aggregate.common.event.EventLevel;
import com.tibbo.aggregate.common.event.EventProcessingRule;
import com.tibbo.aggregate.common.event.EventUtils;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.parser.NodeEvaluationDetails;
import com.tibbo.aggregate.common.plugin.PluginDirector;
import com.tibbo.aggregate.common.security.DefaultPermissionChecker;
import com.tibbo.aggregate.common.security.NullPermissionChecker;
import com.tibbo.aggregate.common.security.PermissionChecker;
import com.tibbo.aggregate.common.security.Permissions;
import com.tibbo.aggregate.common.server.EditableChildContextConstants;
import com.tibbo.aggregate.common.structure.Pinpoint;
import com.tibbo.aggregate.common.structure.collector.ApplicationStructureLocator;
import com.tibbo.aggregate.common.structure.trace.Span;
import com.tibbo.aggregate.common.util.Icons;
import com.tibbo.aggregate.common.util.Pair;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.Util;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
@SuppressWarnings("rawtypes")
public abstract class AbstractContext<C extends Context> implements Context<C>
{
  private static final String IMPLEMENTATION_METHOD_PREFIX = "callF";
  private static final String SETTER_METHOD_PREFIX = "setV";
  private static final String GETTER_METHOD_PREFIX = "getV";
  
  public static final int EXECUTOR_THREADS_PERCENT_FOR_VISITORS = 10;
  
  public static final String V_INFO = "info";
  public static final String V_CHILDREN = "children";
  public static final String V_VARIABLES = "variables";
  public static final String V_FUNCTIONS = "functions";
  public static final String V_EVENTS = "events";
  public static final String V_ACTIONS = "actions";
  public static final String V_VARIABLE_STATUSES = "variableStatuses";
  
  public static final String F_GET_COPY_DATA = "getCopyData";
  public static final String F_COPY = "copy";
  public static final String F_COPY_TO_CHILDREN = "copyToChildren";
  public static final String F_UPDATE_VARIABLE = "updateVariable";
  public static final String F_GET_VARIABLE_STATUS = "getVariableStatus";
  public static final String F_LOCKED_BY = "lockedBy";
  public static final String F_LOCK = "lock";
  public static final String F_UNLOCK = "unlock";
  public static final String F_BREAK_LOCK = "breakLock";
  
  public static final String E_INFO = "info";
  public static final String E_UPDATED = "updated";
  public static final String E_CHANGE = "change";
  public static final String E_DESTROYED = "destroyed";
  public static final String E_INFO_CHANGED = "infoChanged";
  public static final String E_VARIABLE_ADDED = "variableAdded";
  public static final String E_VARIABLE_REMOVED = "variableRemoved";
  public static final String E_FUNCTION_ADDED = "functionAdded";
  public static final String E_FUNCTION_REMOVED = "functionRemoved";
  public static final String E_EVENT_ADDED = "eventAdded";
  public static final String E_EVENT_REMOVED = "eventRemoved";
  public static final String E_ACTION_ADDED = "actionAdded";
  public static final String E_ACTION_REMOVED = "actionRemoved";
  public static final String E_ACTION_STATE_CHANGED = "actionStateChanged";
  public static final String E_CHILD_REMOVED = "childRemoved";
  public static final String E_CHILD_ADDED = "childAdded";
  public static final String E_VARIABLE_STATUS_CHANGED = "variableStatusChanged";
  
  public static final String VF_INFO_DESCRIPTION = "description";
  public static final String VF_INFO_TYPE = "type";
  public static final String VF_INFO_GROUP = "group";
  public static final String VF_INFO_ICON = "icon";
  public static final String VF_INFO_LOCAL_ROOT = "localRoot";
  public static final String VF_INFO_PEER_ROOT = "peerRoot";
  public static final String VF_INFO_PEER_PRIMARY_ROOT = "peerPrimaryRoot";
  public static final String VF_INFO_REMOTE_ROOT = "remoteRoot";
  public static final String VF_INFO_REMOTE_PATH = "remotePath";
  public static final String VF_INFO_MAPPED = "mapped";
  
  public static final String VF_CHILDREN_NAME = "name";
  public static final String VF_CHILDREN_IS_CONTAINER = "container";

  public static final String VF_VARIABLE_STATUSES_COMMENT = "comment";
  public static final String VF_VARIABLE_STATUSES_STATUS = "status";
  public static final String VF_VARIABLE_STATUSES_NAME = "name";

  public static final String FIF_COPY_DATA_RECIPIENTS = "recipients";
  public static final String FIF_COPY_DATA_GROUP = "group";
  
  public static final String FOF_COPY_DATA_NAME = "name";
  public static final String FOF_COPY_DATA_DESCRIPTION = "description";
  public static final String FOF_COPY_DATA_REPLICATE = "replicate";
  public static final String FOF_COPY_DATA_FIELDS = "fields";
  public static final String FOF_COPY_DATA_VALUE = "value";
  
  public static final String FIF_REPLICATE_FIELDS_NAME = "name";
  public static final String FIF_REPLICATE_FIELDS_DESCRIPTION = "description";
  public static final String FIF_REPLICATE_FIELDS_REPLICATE = "replicate";
  
  public static final String FIF_COPY_DATA_RECIPIENTS_RECIPIENT = "recipient";
  
  public static final String FIF_LOCK_PROPERTIES_EDITOR_UUID = "propertiesEditorUUID";
  public static final String FIF_UNLOCK_PROPERTIES_EDITOR_UUID = "propertiesEditorUUID";
  
  public static final String FIF_VISIBLE_CHILDREN_FILTER_EXPRESSION = "filterExpression";
  public static final String FIF_VISIBLE_CHILDREN_CONTEXT_MASK = "contextMask";
  public static final String FIF_VISIBLE_CHILDREN_PROPERTY_FILTERS = "propertyFilters";
  public static final String FIF_VISIBLE_CHILDREN_GLOBAL_FILTER = "globalFilter";
  public static final String FIF_VISIBLE_CHILDREN_OFFSET = "offset";
  public static final String FIF_VISIBLE_CHILDREN_COUNT = "count";
  public static final String FIF_VISIBLE_CHILDREN_SMART_FILTER_EXPRESSION = "smartFilterExpression";


  public static final String VF_VISIBLE_CHILDREN_PROPERTY_FILTERS_PROPERTY_NAME = "propertyName";
  public static final String VF_VISIBLE_CHILDREN_PROPERTY_FILTERS_PROPERTY_VALUE = "propertyValue";

  public static final String FOF_LOCKED_BY_OWNER_NAME = "lockOwnerName";
  public static final String FOF_LOCK_OWNER_NAME = "lockOwnerName";
  public static final String FOF_UNLOCK_UNLOCKED = "unlocked";
  
  public static final String FOF_VISIBLE_CHILDREN_BATCH = "batch";
  public static final String FOF_VISIBLE_CHILDREN_TOTAL_COUNT = "total";

  public static final String EF_INFO_INFO = "info";
  
  public static final String EF_EVENT_REMOVED_NAME = "name";
  public static final String EF_FUNCTION_REMOVED_NAME = "name";
  public static final String EF_VARIABLE_REMOVED_NAME = "name";
  
  public static final String EF_ACTION_REMOVED_NAME = "name";
  
  public static final String EF_CHILD_REMOVED_CHILD = "child";
  public static final String EF_CHILD_ADDED_CHILD = "child";
  
  private static final String FIELD_REPLICATE_CONTEXT = "context";
  public static final String FIELD_REPLICATE_VARIABLE = "variable";
  public static final String FIELD_REPLICATE_SUCCESSFUL = "successful";
  public static final String FIELD_REPLICATE_ERRORS = "errors";
  
  public static final String V_UPDATE_VARIABLE = "variable";
  public static final String V_UPDATE_VARIABLE_EXPRESSION = "expression";
  public static final String V_VARIABLE_NAME = "variableName";
  
  public static final String EF_UPDATED_VARIABLE = "variable";
  public static final String EF_UPDATED_VALUE = "value";
  public static final String EF_UPDATED_VALUE_OLD = "valueOld";
  public static final String EF_UPDATED_USER = "user";
  public static final String EF_UPDATED_VARIABLE_STATUS = "status";
  public static final String EF_UPDATED_UPDATE_ORIGINATOR = "updateOriginator";
  
  public static final String EF_CHANGE_VARIABLE = "variable";
  public static final String EF_CHANGE_VALUE = "value";
  public static final String EF_CHANGE_DATA = "data";
  
  public static final String FIELD_VD_NAME = "name";
  public static final String FIELD_VD_FORMAT = "format";
  public static final String FIELD_VD_DESCRIPTION = "description";
  public static final String FIELD_VD_READABLE = "readable";
  public static final String FIELD_VD_WRITABLE = "writable";
  public static final String FIELD_VD_HELP = "help";
  public static final String FIELD_VD_GROUP = "group";
  public static final String FIELD_VD_ICON_ID = "iconId";
  public static final String FIELD_VD_HELP_ID = "helpId";
  public static final String FIELD_VD_CACHE_TIME = "cacheTime";
  public static final String FIELD_VD_SERVER_CACHING_MODE = "serverCachingMode";
  public static final String FIELD_VD_ADD_PREVIOUS_VALUE_TO_VARIABLE_UPDATE_EVENT = "addPreviousValueToVariableUpdateEvent";
  private static final String FIELD_VD_READ_PERMISSIONS = "readPermissions";
  private static final String FIELD_VD_WRITE_PERMISSIONS = "writePermissions";

  public static final String FIELD_FD_NAME = "name";
  public static final String FIELD_FD_INPUTFORMAT = "inputformat";
  public static final String FIELD_FD_OUTPUTFORMAT = "outputformat";
  public static final String FIELD_FD_DESCRIPTION = "description";
  public static final String FIELD_FD_HELP = "help";
  public static final String FIELD_FD_GROUP = "group";
  public static final String FIELD_FD_ICON_ID = "iconId";
  public static final String FIELD_FD_CONCURRENT = "concurrent";
  public static final String FIELD_FD_PERMISSIONS = "permissions";
  
  public static final String FIELD_ED_NAME = "name";
  public static final String FIELD_ED_FORMAT = "format";
  public static final String FIELD_ED_DESCRIPTION = "description";
  public static final String FIELD_ED_HELP = "help";
  public static final String FIELD_ED_LEVEL = "level";
  public static final String FIELD_ED_GROUP = "group";
  public static final String FIELD_ED_ICON_ID = "iconId";
  private static final String FIELD_ED_PERMISSIONS = "permissions";

  public static final TableFormat VARIABLE_DEFINITION_FORMAT = new TableFormat();
  
  static
  {
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_NAME + "><S>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_FORMAT + "><S><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_DESCRIPTION + "><S><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_READABLE + "><B>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_WRITABLE + "><B>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_HELP + "><S><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_GROUP + "><S><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_ICON_ID + "><S><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_HELP_ID + "><S><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_CACHE_TIME + "><L><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_ADD_PREVIOUS_VALUE_TO_VARIABLE_UPDATE_EVENT + "><B><A=0>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_READ_PERMISSIONS + "><S><F=N>");
    VARIABLE_DEFINITION_FORMAT.addField("<" + FIELD_VD_WRITE_PERMISSIONS + "><S><F=N>");
  }
  
  private static final TableFormat VFT_VARIABLE_STATUSES = new TableFormat();

  static
  {
    VFT_VARIABLE_STATUSES.addField("<" + VF_VARIABLE_STATUSES_NAME + "><S>");
    VFT_VARIABLE_STATUSES.addField("<" + VF_VARIABLE_STATUSES_STATUS + "><S><F=N>");
    VFT_VARIABLE_STATUSES.addField("<" + VF_VARIABLE_STATUSES_COMMENT + "><S><F=N>");
  }

  public static final TableFormat VFT_VISIBLE_CHILDREN = new TableFormat();

  static
  {
    VFT_VISIBLE_CHILDREN.addField("<" + VF_VISIBLE_CHILDREN_PATH + "><S>");
  }

  private static final TableFormat EF_VARIABLE_ADDED = VARIABLE_DEFINITION_FORMAT.clone();
  
  static
  {
    EF_VARIABLE_ADDED.setMinRecords(1);
    EF_VARIABLE_ADDED.setMaxRecords(1);
  }
  
  public static final TableFormat FUNCTION_DEFINITION_FORMAT = new TableFormat();
  
  static
  {
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_NAME + "><S>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_INPUTFORMAT + "><S><F=N>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_OUTPUTFORMAT + "><S><F=N>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_DESCRIPTION + "><S><F=N>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_HELP + "><S><F=N>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_GROUP + "><S><F=N>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_ICON_ID + "><S><F=N>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_CONCURRENT + "><B><F=N>");
    FUNCTION_DEFINITION_FORMAT.addField("<" + FIELD_FD_PERMISSIONS + "><S><F=N>");
  }
  
  public static final TableFormat EF_FUNCTION_ADDED = FUNCTION_DEFINITION_FORMAT.clone();
  
  static
  {
    EF_FUNCTION_ADDED.setMinRecords(1);
    EF_FUNCTION_ADDED.setMaxRecords(1);
  }
  
  public static final TableFormat EVENT_DEFINITION_FORMAT = new TableFormat();
  
  static
  {
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_NAME + "><S>");
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_FORMAT + "><S><F=N>");
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_DESCRIPTION + "><S><F=N>");
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_HELP + "><S><F=N>");
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_LEVEL + "><I>");
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_GROUP + "><S><F=N>");
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_ICON_ID + "><S><F=N>");
    EVENT_DEFINITION_FORMAT.addField("<" + FIELD_ED_PERMISSIONS + "><S><F=N>");
  }
  
  public static final TableFormat EF_EVENT_ADDED = EVENT_DEFINITION_FORMAT.clone();
  
  static
  {
    EF_EVENT_ADDED.setMinRecords(1);
    EF_EVENT_ADDED.setMaxRecords(1);
  }
  
  protected static final TableFormat VFT_CHILDREN = new TableFormat();

  static
  {
    FieldFormat ff = FieldFormat.create(VF_CHILDREN_NAME, FieldFormat.STRING_FIELD);
    VFT_CHILDREN.addField(ff);

    ff = FieldFormat.create(VF_CHILDREN_IS_CONTAINER, FieldFormat.BOOLEAN_FIELD);
    VFT_CHILDREN.addField(ff);
  }
  
  public static final TableFormat INFO_DEFINITION_FORMAT = new TableFormat(1, 1);
  
  static
  {
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_DESCRIPTION + "><S><F=N><D=" + Cres.get().getString("description") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_TYPE + "><S><D=" + Cres.get().getString("type") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_GROUP + "><S><F=N><D=" + Cres.get().getString("group") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_ICON + "><S><F=N><D=" + Cres.get().getString("conIconId") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_LOCAL_ROOT + "><S><D=" + Cres.get().getString("conLocalRoot") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_PEER_ROOT + "><S><F=N><D=" + Cres.get().getString("conPeerRoot") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_PEER_PRIMARY_ROOT + "><S><F=N><D=" + Cres.get().getString("conPeerPrimaryRoot") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_REMOTE_ROOT + "><S><F=N><D=" + Cres.get().getString("conRemoteRoot") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_REMOTE_PATH + "><S><D=" + Cres.get().getString("conRemotePath") + ">");
    INFO_DEFINITION_FORMAT.addField("<" + VF_INFO_MAPPED + "><B><F=N><D=" + Cres.get().getString("conMapped") + ">");
  }
  
  public static final TableFormat ACTION_DEF_FORMAT = new TableFormat();
  
  static
  {
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_NAME + "><S>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_DESCRIPTION + "><S><F=N>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_HELP + "><S><F=N>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_ACCELERATOR + "><S><F=N>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_DROP_SOURCES + "><T><F=N>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_HIDDEN + "><B>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_ENABLED + "><B>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_ICON_ID + "><S><F=N>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_GROUP + "><S><F=N>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_EXECUTION_GROUP + "><S><F=N>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_DEFAULT + "><B>");
    ACTION_DEF_FORMAT.addField("<" + ActionConstants.FIELD_AD_PERMISSIONS + "><S><F=N>");
  }
  
  public static final TableFormat RESOURCE_MASKS_FORMAT = FieldFormat.create("<" + ActionConstants.FIELD_AD_RESOURCE_MASKS_RESOURCE_MASK + "><S><F=N>").wrap();
  
  public static final TableFormat FIFT_GET_COPY_DATA = new TableFormat(1, 1);
  
  static
  {
    FIFT_GET_COPY_DATA.addField("<" + FIF_COPY_DATA_GROUP + "><S><F=N>");
    FIFT_GET_COPY_DATA.addField("<" + FIF_COPY_DATA_RECIPIENTS + "><T><F=N>");
  }
  
  public static final TableFormat FIFT_GET_COPY_DATA_RECIPIENTS = FieldFormat.create("<" + FIF_COPY_DATA_RECIPIENTS_RECIPIENT + "><S>").wrap();
  
  public static final TableFormat REPLICATE_INPUT_FORMAT = new TableFormat();
  
  static
  {
    REPLICATE_INPUT_FORMAT.addField("<" + FOF_COPY_DATA_NAME + "><S><F=RHK>");
    REPLICATE_INPUT_FORMAT.addField("<" + FOF_COPY_DATA_DESCRIPTION + "><S><F=R><D=" + Cres.get().getString("variable") + ">");
    REPLICATE_INPUT_FORMAT.addField("<" + FOF_COPY_DATA_REPLICATE + "><B><A=0><D=" + Cres.get().getString("replicate") + ">");
    REPLICATE_INPUT_FORMAT.addField("<" + FOF_COPY_DATA_FIELDS + "><T><D=" + Cres.get().getString("fields") + ">");
    REPLICATE_INPUT_FORMAT.addField("<" + FOF_COPY_DATA_VALUE + "><T><D=" + Cres.get().getString("value") + ">");
  }
  
  public static final TableFormat FIFT_REPLICATE_FIELDS = new TableFormat();
  
  static
  {
    FIFT_REPLICATE_FIELDS.addField("<" + FIF_REPLICATE_FIELDS_NAME + "><S><F=RHK>");
    FIFT_REPLICATE_FIELDS.addField("<" + FIF_REPLICATE_FIELDS_DESCRIPTION + "><S><F=R><D=" + Cres.get().getString("field") + ">");
    FIFT_REPLICATE_FIELDS.addField("<" + FIF_REPLICATE_FIELDS_REPLICATE + "><B><A=1><D=" + Cres.get().getString("replicate") + ">");
    FIFT_REPLICATE_FIELDS.setNamingExpression("print({}, '{" + FIF_REPLICATE_FIELDS_REPLICATE + "} ? {" + FIF_REPLICATE_FIELDS_DESCRIPTION + "} : null', ', ')");
  }
  
  public static final TableFormat REPLICATE_OUTPUT_FORMAT = new TableFormat();
  
  static
  {
    REPLICATE_OUTPUT_FORMAT.addField("<" + FIELD_REPLICATE_VARIABLE + "><S><D=" + Cres.get().getString("variable") + ">");
    REPLICATE_OUTPUT_FORMAT.addField("<" + FIELD_REPLICATE_SUCCESSFUL + "><B><D=" + Cres.get().getString("successful") + ">");
    REPLICATE_OUTPUT_FORMAT.addField("<" + FIELD_REPLICATE_ERRORS + "><S><D=" + Cres.get().getString("errors") + ">");
  }
  
  protected static final TableFormat REPLICATE_TO_CHILDREN_OUTPUT_FORMAT = new TableFormat();
  
  static
  {
    REPLICATE_TO_CHILDREN_OUTPUT_FORMAT.addField("<" + FIELD_REPLICATE_CONTEXT + "><S><D=" + Cres.get().getString("context") + ">");
    REPLICATE_TO_CHILDREN_OUTPUT_FORMAT.addField("<" + FIELD_REPLICATE_VARIABLE + "><S><D=" + Cres.get().getString("variable") + ">");
    REPLICATE_TO_CHILDREN_OUTPUT_FORMAT.addField("<" + FIELD_REPLICATE_SUCCESSFUL + "><B><D=" + Cres.get().getString("successful") + ">");
    REPLICATE_TO_CHILDREN_OUTPUT_FORMAT.addField("<" + FIELD_REPLICATE_ERRORS + "><S><D=" + Cres.get().getString("errors") + ">");
  }
  
  public static final TableFormat FIFT_UPDATE_VARIABLE = new TableFormat();
  
  static
  {
    FIFT_UPDATE_VARIABLE.addField(FieldFormat.create(V_UPDATE_VARIABLE, FieldFormat.STRING_FIELD, Cres.get().getString("variable")));
    FIFT_UPDATE_VARIABLE.addField(FieldFormat.create(V_UPDATE_VARIABLE_EXPRESSION, FieldFormat.STRING_FIELD, Cres.get().getString("expression")));
  }
  
  public static final TableFormat FIFT_GET_VARIABLE_STATUS = new TableFormat();

  static
  {
    FIFT_GET_VARIABLE_STATUS.addField(FieldFormat.create(V_VARIABLE_NAME, FieldFormat.STRING_FIELD, Cres.get().getString("variable")));
  }

  public static final TableFormat FIFT_LOCK = new TableFormat(1, 1);
  
  static
  {
    FIFT_LOCK.addField(FieldFormat.create(FIF_LOCK_PROPERTIES_EDITOR_UUID, FieldFormat.STRING_FIELD));
  }
  
  public static final TableFormat FIFT_UNLOCK = new TableFormat(1, 1);
  
  static
  {
    FIFT_UNLOCK.addField(FieldFormat.create(FIF_UNLOCK_PROPERTIES_EDITOR_UUID, FieldFormat.STRING_FIELD));
  }
  
  public static final TableFormat FIFT_VISIBLE_CHILDREN = new TableFormat();

  static
  {
    FIFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FIF_VISIBLE_CHILDREN_FILTER_EXPRESSION, FieldFormat.STRING_FIELD, Cres.get().getString("efFilterExpr")));
    FIFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FIF_VISIBLE_CHILDREN_CONTEXT_MASK, FieldFormat.STRING_FIELD, Cres.get().getString("conContextMask")));
    FIFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FIF_VISIBLE_CHILDREN_OFFSET, FieldFormat.INTEGER_FIELD, Cres.get().getString("offset")));
    FIFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FIF_VISIBLE_CHILDREN_COUNT, FieldFormat.INTEGER_FIELD, Cres.get().getString("count")));
    FIFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FIF_VISIBLE_CHILDREN_PROPERTY_FILTERS, FieldFormat.DATATABLE_FIELD, Cres.get().getString("propertyFilters")));
    FIFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FIF_VISIBLE_CHILDREN_GLOBAL_FILTER, FieldFormat.DATATABLE_FIELD, Cres.get().getString("globalFilter")));
    FIFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FIF_VISIBLE_CHILDREN_SMART_FILTER_EXPRESSION, FieldFormat.STRING_FIELD, Cres.get().getString("smartFilterExpression")));
  }

  public static final TableFormat FOFT_LOCKED_BY = new TableFormat(1, 1);
  
  static
  {
    FOFT_LOCKED_BY.addField(FieldFormat.create(FOF_LOCKED_BY_OWNER_NAME, FieldFormat.STRING_FIELD,
        Cres.get().getString("conPropLockOwnerName")).setNullable(true));
  }
  
  public static final TableFormat FOFT_LOCK = new TableFormat(1, 1);
  
  static
  {
    FOFT_LOCK.addField(FieldFormat.create(FOF_LOCK_OWNER_NAME, FieldFormat.STRING_FIELD,
        Cres.get().getString("conPropLockOwnerName")).setNullable(true));
  }
  
  public static final TableFormat FOFT_UNLOCK = new TableFormat(1, 1);
  
  static
  {
    FOFT_UNLOCK.addField(FieldFormat.create(FOF_UNLOCK_UNLOCKED, FieldFormat.BOOLEAN_FIELD));
  }
  
  public static final TableFormat FOFT_VISIBLE_CHILDREN = new TableFormat(1, 1);

  static
  {
    FOFT_VISIBLE_CHILDREN.addField(FieldFormat.create(FOF_VISIBLE_CHILDREN_BATCH, FieldFormat.DATATABLE_FIELD, "", new SimpleDataTable(VFT_VISIBLE_CHILDREN)));
    FOFT_VISIBLE_CHILDREN.addField("<" + FOF_VISIBLE_CHILDREN_TOTAL_COUNT + "><I>");
  }

  public static final TableFormat EF_UPDATED = new TableFormat(1, 1);
  
  static
  {
    EF_UPDATED.addField("<" + EF_UPDATED_VARIABLE + "><S>");
    EF_UPDATED.addField("<" + EF_UPDATED_VALUE + "><T>");
    EF_UPDATED.addField("<" + EF_UPDATED_VALUE_OLD + "><T><F=N>");
    EF_UPDATED.addField("<" + EF_UPDATED_USER + "><S><F=N>");
    EF_UPDATED.addField("<" + EF_UPDATED_UPDATE_ORIGINATOR + "><I>");
    EF_UPDATED.addField("<" + EF_UPDATED_VARIABLE_STATUS + "><T><F=N>");
  }
  
  public static final TableFormat EF_CHANGE = new TableFormat(1, 1);
  
  static
  {
    EF_CHANGE.addField("<" + EF_CHANGE_VARIABLE + "><S>");
    EF_CHANGE.addField("<" + EF_CHANGE_VALUE + "><T><F=N>");
    EF_CHANGE.addField("<" + EF_CHANGE_DATA + "><S><F=N>");
  }
  
  public static final TableFormat EFT_INFO = new TableFormat(1, 1, "<" + EF_INFO_INFO + "><S><D=" + Cres.get().getString("info") + ">");
  public static final TableFormat EFT_VARIABLE_REMOVED = new TableFormat(1, 1, "<" + EF_VARIABLE_REMOVED_NAME + "><S>");
  public static final TableFormat EFT_EVENT_REMOVED = new TableFormat(1, 1, "<" + EF_EVENT_REMOVED_NAME + "><S>");
  public static final TableFormat EFT_FUNCTION_REMOVED = new TableFormat(1, 1, "<" + EF_FUNCTION_REMOVED_NAME + "><S>");
  public static final TableFormat EFT_CHILD_REMOVED = new TableFormat(1, 1, "<" + EF_CHILD_REMOVED_CHILD + "><S>");
  public static final TableFormat EFT_CHILD_ADDED = new TableFormat(1, 1, "<" + EF_CHILD_ADDED_CHILD + "><S>");
  
  public static final TableFormat EFT_ACTION_REMOVED = new TableFormat(1, 1, "<" + AbstractContext.EF_ACTION_REMOVED_NAME + "><S>");
  
  public static final VariableDefinition VD_INFO = new VariableDefinition(V_INFO, INFO_DEFINITION_FORMAT, true, false, Cres.get().getString("conContextProps"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    VD_INFO.setHidden(true);
    VD_INFO.setReadPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final VariableDefinition VD_VARIABLES = new VariableDefinition(V_VARIABLES, VARIABLE_DEFINITION_FORMAT, true, false, Cres.get().getString("conVarList"));
  
  static
  {
    VD_VARIABLES.setHidden(true);
    VD_VARIABLES.setReadPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final VariableDefinition VD_FUNCTIONS = new VariableDefinition(V_FUNCTIONS, FUNCTION_DEFINITION_FORMAT, true, false, Cres.get().getString("conFuncList"));
  
  static
  {
    VD_FUNCTIONS.setHidden(true);
    VD_FUNCTIONS.setReadPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final VariableDefinition VD_EVENTS = new VariableDefinition(V_EVENTS, EVENT_DEFINITION_FORMAT, true, false, Cres.get().getString("conEvtList"));
  
  static
  {
    VD_EVENTS.setHidden(true);
    VD_EVENTS.setReadPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final VariableDefinition VD_ACTIONS = new VariableDefinition(AbstractContext.V_ACTIONS, ACTION_DEF_FORMAT, true, false, Cres.get().getString("conActionList"));
  
  static
  {
    VD_ACTIONS.setHidden(true);
    VD_ACTIONS.setReadPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final VariableDefinition VD_CHILDREN = new VariableDefinition(V_CHILDREN, VFT_CHILDREN, true, false, Cres.get().getString("conChildList"));
  
  static
  {
    VD_CHILDREN.setHidden(true);
    VD_CHILDREN.setReadPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  protected static final FunctionDefinition FD_GET_COPY_DATA = new FunctionDefinition(F_GET_COPY_DATA, FIFT_GET_COPY_DATA, REPLICATE_INPUT_FORMAT);
  
  static
  {
    FD_GET_COPY_DATA.setHidden(true);
  }
  
  protected static final FunctionDefinition FD_COPY = new FunctionDefinition(F_COPY, REPLICATE_INPUT_FORMAT, REPLICATE_OUTPUT_FORMAT, Cres.get().getString("conCopyProperties"));
  
  static
  {
    FD_COPY.setHidden(true);
  }
  
  protected static final FunctionDefinition FD_COPY_TO_CHILDREN = new FunctionDefinition(F_COPY_TO_CHILDREN, REPLICATE_INPUT_FORMAT, REPLICATE_TO_CHILDREN_OUTPUT_FORMAT,
      Cres.get().getString("conCopyToChildren"));
  
  static
  {
    FD_COPY_TO_CHILDREN.setHidden(true);
  }
  
  protected static final FunctionDefinition FD_UPDATE_VARIABLE = new FunctionDefinition(F_UPDATE_VARIABLE, FIFT_UPDATE_VARIABLE, null, Cres.get().getString("updateVariable"),
      ContextUtils.GROUP_SYSTEM);
  
  static
  {
    FD_UPDATE_VARIABLE.setConcurrent(true);
  }
  
  public static final EventDefinition ED_INFO = new EventDefinition(E_INFO, EFT_INFO, Cres.get().getString("info"), ContextUtils.GROUP_DEFAULT);
  
  static
  {
    ED_INFO.setLevel(EventLevel.INFO);
    ED_INFO.setIconId(Icons.EVT_INFO);
    ED_INFO.getPersistenceOptions().setDedicatedTablePreferred(true);
  }
  
  public static final EventDefinition ED_CHILD_ADDED = new EventDefinition(E_CHILD_ADDED, EFT_CHILD_ADDED, Cres.get().getString("conChildAdded"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_CHILD_ADDED.setConcurrency(EventDefinition.CONCURRENCY_SYNCHRONOUS);
    ED_CHILD_ADDED.setHidden(true);
    ED_CHILD_ADDED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_CHILD_REMOVED = new EventDefinition(E_CHILD_REMOVED, EFT_CHILD_REMOVED, Cres.get().getString("conChildRemoved"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_CHILD_REMOVED.setConcurrency(EventDefinition.CONCURRENCY_SYNCHRONOUS);
    ED_CHILD_REMOVED.setHidden(true);
    ED_CHILD_REMOVED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_VARIABLE_ADDED = new EventDefinition(E_VARIABLE_ADDED, EF_VARIABLE_ADDED, Cres.get().getString("conVarAdded"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_VARIABLE_ADDED.setHidden(true);
    ED_VARIABLE_ADDED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_VARIABLE_REMOVED = new EventDefinition(E_VARIABLE_REMOVED, EFT_VARIABLE_REMOVED, Cres.get().getString("conVarRemoved"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_VARIABLE_REMOVED.setHidden(true);
    ED_VARIABLE_REMOVED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_FUNCTION_ADDED = new EventDefinition(E_FUNCTION_ADDED, EF_FUNCTION_ADDED, Cres.get().getString("conFuncAdded"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_FUNCTION_ADDED.setHidden(true);
    ED_FUNCTION_ADDED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_FUNCTION_REMOVED = new EventDefinition(E_FUNCTION_REMOVED, EFT_FUNCTION_REMOVED, Cres.get().getString("conFuncRemoved"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_FUNCTION_REMOVED.setHidden(true);
    ED_FUNCTION_REMOVED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_EVENT_ADDED = new EventDefinition(E_EVENT_ADDED, EF_EVENT_ADDED, Cres.get().getString("conEvtAdded"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_EVENT_ADDED.setHidden(true);
    ED_EVENT_ADDED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_EVENT_REMOVED = new EventDefinition(E_EVENT_REMOVED, EFT_EVENT_REMOVED, Cres.get().getString("conEvtRemoved"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_EVENT_REMOVED.setHidden(true);
    ED_EVENT_REMOVED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_ACTION_ADDED = new EventDefinition(AbstractContext.E_ACTION_ADDED, ACTION_DEF_FORMAT.clone().setMinRecords(1).setMaxRecords(1),
      Cres.get().getString("conActionAdded"));
  
  static
  {
    ED_ACTION_ADDED.setHidden(true);
    ED_ACTION_ADDED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_ACTION_REMOVED = new EventDefinition(AbstractContext.E_ACTION_REMOVED, EFT_ACTION_REMOVED, Cres.get().getString("conActionRemoved"));
  
  static
  {
    ED_ACTION_REMOVED.setHidden(true);
    ED_ACTION_REMOVED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_ACTION_STATE_CHANGED = new EventDefinition(AbstractContext.E_ACTION_STATE_CHANGED, ACTION_DEF_FORMAT, Cres.get().getString("conActionStateChanged"));
  
  static
  {
    ED_ACTION_STATE_CHANGED.setHidden(true);
    ED_ACTION_STATE_CHANGED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_INFO_CHANGED = new EventDefinition(E_INFO_CHANGED, INFO_DEFINITION_FORMAT, Cres.get().getString("conInfoChanged"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_INFO_CHANGED.setHidden(true);
    ED_INFO_CHANGED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }
  
  public static final EventDefinition ED_UPDATED = new EventDefinition(E_UPDATED, EF_UPDATED, Cres.get().getString("conUpdated"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_UPDATED.setHidden(true);
    ED_UPDATED.setConcurrency(EventDefinition.CONCURRENCY_CONCURRENT);
    ED_UPDATED.setFingerprintExpression("{" + EF_UPDATED_VARIABLE + "}");
  }
  
  public static final EventDefinition ED_CHANGE = new EventDefinition(E_CHANGE, EF_CHANGE, Cres.get().getString("change"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_CHANGE.setHidden(true);
    ED_CHANGE.getPersistenceOptions().setDedicatedTablePreferred(true);
  }
  
  public static final EventDefinition ED_DESTROYED = new EventDefinition(E_DESTROYED, TableFormat.EMPTY_FORMAT, Cres.get().getString("conDestroyedPermanently"), ContextUtils.GROUP_SYSTEM);
  
  static
  {
    ED_DESTROYED.setConcurrency(EventDefinition.CONCURRENCY_SYNCHRONOUS);
    ED_DESTROYED.setHidden(true);
    ED_DESTROYED.setPermissions(DefaultPermissionChecker.getNullPermissions());
  }

  private static final int DEFAULT_EVENT_LEVEL = -1;
  
  private static final Permissions DEFAULT_PERMISSIONS = DefaultPermissionChecker.getNullPermissions();
  
  public static final String CALLER_CONTROLLER_PROPERTY_DEBUG = "debug";
  public static final String CALLER_CONTROLLER_PROPERTY_NO_UPDATED_EVENTS = "no_updated_events";
  public static final String CALLER_CONTROLLER_PROPERTY_NO_CHANGE_EVENTS = "no_change_events";
  public static final String CALLER_CONTROLLER_PROPERTY_NO_STATISTICS = "no_statistics";
  public static final String CALLER_CONTROLLER_PROPERTY_NO_VALIDATION = "no_validation";
  
  public static final int INDEX_HIGHEST = 400;
  public static final int INDEX_VERY_HIGH = 300;
  public static final int INDEX_HIGH = 200;
  public static final int INDEX_HIGHER = 100;
  public static final int INDEX_NORMAL = 0;
  public static final int INDEX_LOWER = -100;
  public static final int INDEX_LOW = -200;
  public static final int INDEX_VERY_LOW = -300;
  public static final int INDEX_LOWEST = -400;

  private static final int VERY_LOW_PERFORMANCE_THRESHOLD = 120000;
  private static final int LOW_PERFORMANCE_THRESHOLD = 20000;
  
  protected static final int SORT_THRESHOLD = 10000;
  
  private static final int MOVE_LOCK_TIMEOUT = 2;

  private static final ThreadLocal<Deque<Span>> currentTrace = ThreadLocal.withInitial(LinkedList::new);
  protected static final ThreadLocal<Pinpoint> currentSource = new ThreadLocal<>();

  private ContextManager<C> contextManager;
  
  private final Map<String, VariableData> variableData = new LinkedHashMap<>();
  private final Map<String, String> variableNameByAlias = new ConcurrentHashMap<>();

  private final ReentrantReadWriteLock variableDataLock = new ReentrantReadWriteLock();
  
  private final Map<String, FunctionData> functionData = new LinkedHashMap<>();
  private final Map<String, String> functionNameByAlias = new ConcurrentHashMap<>();
  private final ReentrantReadWriteLock functionDataLock = new ReentrantReadWriteLock();
  
  private final Map<String, EventData> eventData = new LinkedHashMap<>();
  private final Map<String, String> eventNameByAlias = new ConcurrentHashMap<>();
  private final ReentrantReadWriteLock eventDataLock = new ReentrantReadWriteLock();
  
  private final List<ActionDefinition> actionDefinitions = new ArrayList<>();
  private final ReentrantReadWriteLock actionDefinitionsLock = new ReentrantReadWriteLock();
  
  private final PropertiesLock propertiesLock = new PropertiesLock(this);
  
  private String name;
  private String description;
  private String type;
  private String group;
  private String iconId;
  
  private C parent;
  
  private boolean setupComplete;
  private boolean started;
  private boolean stopped;
  
  private Integer index;
  
  private boolean permissionCheckingEnabled = true;
  private Permissions permissions;
  private Permissions childrenViewPermissions;
  private PermissionChecker permissionChecker = new NullPermissionChecker();
  
  private final List<C> childrenList = new ArrayList<>();
  private final Map<String, C> childrenMap = new ConcurrentHashMap<String, C>();
  private final ReentrantReadWriteLock childrenLock = new ReentrantReadWriteLock();
  
  private boolean valueCheckingEnabled = true;
  private boolean childrenConcurrencyEnabled = false; // Avoid enabling if context's children may have subchildren
  
  private boolean childrenSortingEnabled = true;
  
  private boolean fireUpdateEvents = true;
  
  private ContextStatus status;
  
  private Map<String, VariableStatus> variableStatuses;
  private DataTable variableStatusesTable = null;
  private boolean variableStatusesUpdated;
  private final ReentrantReadWriteLock variableStatusesLock = new ReentrantReadWriteLock();
  protected final CompletableFuture<C> ready = new CompletableFuture<>();
  private String path; // Cached, for internal use
  
  public AbstractContext(String name)
  {
    setName(name);
    index = ContextSortingHelper.getIndex(name);
  }
  
  @Override
  public final void setup(ContextManager contextManager)
  {
    setContextManager(contextManager);
    setup();
  }
  
  protected final void setup()
  {
    try
    {
      if (setupComplete)
      {
        return;
      }

      boolean childrenDebugEnabled = Log.CONTEXT_CHILDREN.isDebugEnabled();

      long startTime = childrenDebugEnabled ? System.currentTimeMillis() : 0;

      setupPermissions();
      
      setupMyself();

      if (childrenDebugEnabled)
      {
        Log.CONTEXT_CHILDREN.debug("Set up context  '" + getPath() + "' in " + (System.currentTimeMillis() - startTime) + " ms");
      }

      setupComplete = true;

      startTime = childrenDebugEnabled ? System.currentTimeMillis() : 0;

      setupChildren();
      if (childrenDebugEnabled)
      {
        Log.CONTEXT_CHILDREN.debug("Set up children of context  '" + getPath() + "' in " + (System.currentTimeMillis() - startTime) + " ms");
      }
      if (!ready.isDone())
      {
        ready.complete((C)this);
      }
    }
    catch (Exception ex)
    {
      if (!ready.isDone())
      {
        ready.completeExceptionally(ex);
      }
      throw new ContextRuntimeException("Error setting up context '" + this + "': " + ex.getMessage(), ex);
    }
  }
  
  public void setupPermissions()
  {
    
  }
  
  public void setupMyself() throws ContextException
  {
    addVariableDefinition(VD_INFO);
    
    addVariableDefinition(VD_VARIABLES);
    
    addVariableDefinition(VD_FUNCTIONS);
    
    addVariableDefinition(VD_EVENTS);
    
    addVariableDefinition(VD_ACTIONS);
    
    addVariableDefinition(VD_CHILDREN);
    
    addFunctionDefinition(FD_GET_COPY_DATA);
    
    addFunctionDefinition(FD_COPY);
    
    addFunctionDefinition(FD_COPY_TO_CHILDREN);
    
    addFunctionDefinition(FD_UPDATE_VARIABLE);
    
    addPropertiesLockFunctionDefinitions();
    
    addEventDefinition(ED_INFO);
    
    addEventDefinition(ED_CHILD_ADDED);
    
    addEventDefinition(ED_CHILD_REMOVED);
    
    addEventDefinition(ED_VARIABLE_ADDED);
    
    addEventDefinition(ED_VARIABLE_REMOVED);
    
    addEventDefinition(ED_FUNCTION_ADDED);
    
    addEventDefinition(ED_FUNCTION_REMOVED);
    
    addEventDefinition(ED_EVENT_ADDED);
    
    addEventDefinition(ED_EVENT_REMOVED);
    
    addEventDefinition(ED_ACTION_ADDED);
    
    addEventDefinition(ED_ACTION_REMOVED);
    
    addEventDefinition(ED_ACTION_STATE_CHANGED);
    
    addEventDefinition(ED_INFO_CHANGED);
    
    addEventDefinition(ED_UPDATED);
    
    addEventDefinition(getChangeEventDefinition());
    
    addEventDefinition(ED_DESTROYED);
  }
  
  private void addPropertiesLockFunctionDefinitions()
  {
    FunctionDefinition fd = new FunctionDefinition(F_LOCK, FIFT_LOCK, FOFT_LOCK, Cres.get().getString("conPropLockLock"));
    fd.setHidden(true);
    fd.setPermissions(getPermissions());
    fd.setImplementation(lockImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_UNLOCK, FIFT_UNLOCK, FOFT_UNLOCK, Cres.get().getString("conPropLockUnlock"));
    fd.setHidden(true);
    fd.setPermissions(getPermissions());
    fd.setImplementation(unlockImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_BREAK_LOCK, null, null,
        Cres.get().getString("conPropLockBreakLock"));
    fd.setPermissions(getPermissions());
    fd.setImplementation(breakLockImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_LOCKED_BY, null, FOFT_LOCKED_BY,
        Cres.get().getString("conPropLockLockedBy"));
    fd.setPermissions(getPermissions());
    fd.setImplementation(lockedByImpl);
    addFunctionDefinition(fd);
  }
  
  public void setupChildren() throws ContextException
  {
    
  }
  
  @Override
  public void teardown()
  {
    
  }
  
  @Override
  public void start()
  {
    // Оптимизация: используем ArrayList вместо LinkedList для лучшей производительности
    List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
    
    childrenLock.readLock().lock();
    try
    {
      for (final Context child : childrenList)
      {
        Callable<Object> task = () ->
        {
          long startTime = System.currentTimeMillis();

          child.start();

          Log.CONTEXT_CHILDREN.debug("Started context  '" + child.getPath() + "' in " + (System.currentTimeMillis() - startTime) + " ms");

          return null;
        };
        
        tasks.add(task);
      }
    }
    finally
    {
      childrenLock.readLock().unlock();
    }
    
    executeTasks(tasks);
    
    started = true;
  }
  
  @Override
  public void stop()
  {
    stopped = true;
    
    // Оптимизация: используем ArrayList вместо LinkedList для лучшей производительности
    List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
    
    childrenLock.readLock().lock();
    try
    {
      for (final Context<C> child : childrenList)
      {
        Callable<Object> task = new Callable<Object>()
        {
          @Override
          public Object call()
          {
            long startTime = System.currentTimeMillis();

            child.stop();

            Log.CONTEXT_CHILDREN.debug("Stopped context  '" + child.getPath() + "' in " + (System.currentTimeMillis() - startTime) + " ms");

            return null;
          }
        };
        
        tasks.add(task);
      }
    }
    finally
    {
      childrenLock.readLock().unlock();
    }
    
    executeTasks(tasks);
    
    started = false;
  }
  
  @Override
  public int compareTo(Context context)
  {
    if (getIndex() != null || context.getIndex() != null)
    {
      Integer my = getIndex() != null ? getIndex() : new Integer(0);
      Integer other = context.getIndex() != null ? context.getIndex() : new Integer(0);
      return other.compareTo(my);
    }
    else
    {
      return getName().compareTo(context.getName());
    }
  }
  
  @Override
  public List<C> getChildren(CallerController caller)
  {
    if (!checkPermissions(getChildrenViewPermissions(), caller, this, null))
    {
      return Collections.emptyList();
    }
    
    // Оптимизация: используем ArrayList вместо LinkedList
    // Предварительно резервируем размер для снижения переаллокаций
    List<C> childList = new ArrayList<C>(childrenList.size());
    childList.addAll(childrenList);
    
    for (Iterator<C> iterator = childList.iterator(); iterator.hasNext();)
    {
      Context<C> cur = iterator.next();
      if (!shouldSeeChild(caller, cur))
      {
        iterator.remove();
      }
      
    }
    
    return childList;
  }
  
  protected boolean shouldSeeChild(CallerController caller, Context<C> cur)
  {
    return checkPermissions(cur.getPermissions(), caller, cur, null) || canSee(caller, cur);
  }
  
  private boolean canSee(CallerController caller, Context<C> con)
  {
    if (!permissionCheckingEnabled)
    {
      return true;
    }
    
    return getPermissionChecker().canSee(caller != null ? caller.getPermissions() : null, con.getPath(), getContextManager());
  }
  
  @Override
  public List<C> getChildren()
  {
    return getChildren(null);
  }
  
  @Override
  public List<C> getVisibleChildren(CallerController caller)
  {
    return getChildren(caller);
  }
  
  @Override
  public boolean hasVisibleChild(String name, CallerController caller)
  {
    return getChild(name, caller) != null;
  }

  @Override
  public List<C> getVisibleChildren()
  {
    return getVisibleChildren(null);
  }
  
  @Override
  public boolean isMapped()
  {
    return false;
  }
  
  @Override
  public List<C> getMappedChildren(CallerController caller)
  {
    return isMapped() ? getVisibleChildren(caller) : getChildren(caller);
  }
  
  @Override
  public List<C> getMappedChildren()
  {
    return getMappedChildren(null);
  }
  
  @Override
  public boolean hasMappedChild(String contextName, CallerController callerController)
  {
    return isMapped() ? hasVisibleChild(contextName, callerController) : getChild(contextName, callerController) != null;
  }

  @Override
  public String getName()
  {
    return name;
  }
  
  @Override
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String description)
  {
    String old = this.description;
    this.description = description;
    
    if (old == null || !old.equals(description))
    {
      contextInfoChanded();
    }
  }
  
  @Override
  public C getParent()
  {
    return parent;
  }
  
  @Override
  public boolean hasParent(C parentContext)
  {
    if (parentContext == null)
    {
      return false;
    }
    
    Context root = this;
    
    while (root.getParent() != null)
    {
      root = root.getParent();
      if (root == parentContext)
      {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public C getRoot()
  {
    Context root = this;
    
    while (root.getParent() != null)
    {
      root = root.getParent();
    }
    
    return (C) root;
  }
  
  @Override
  public C get(String contextPath, CallerController caller)
  {
    if (contextPath == null)
    {
      return null;
    }
    
    boolean relative = ContextUtils.isRelative(contextPath);
    
    if (relative)
    {
      contextPath = contextPath.substring(1);
    }
    
    C cur = relative ? (C) this : getRoot();
    
    if (contextPath.length() == 0)
    {
      return cur;
    }
    
    String fullPath = null;
    
    if (caller != null)
    {
      fullPath = relative ? ContextUtils.createName(getPath(), contextPath) : contextPath;
      
      C cached = (C) caller.lookup(fullPath);
      
      if (cached != null)
      {
        return cached;
      }
    }
    
    String lastName = getRoot().getName();
    List<String> names = StringUtils.split(contextPath, ContextUtils.CONTEXT_NAME_SEPARATOR.charAt(0));
    for (String child : names)
    {
      if (child.length() == 0)
      {
        return null;
      }
      
      if (cur == null)
      {
        break;
      }
      
      lastName = cur.getName();
      cur = (C) cur.getChild(child, caller);
    }

    if (caller != null && cur != null)
    {
      caller.cache(fullPath, cur);
    }
    
    return cur;
  }
  
  @Override
  public C get(String contextName)
  {
    return get(contextName, null);
  }
  
  @Override
  public Permissions getPermissions()
  {
    if (!permissionCheckingEnabled)
    {
      return DEFAULT_PERMISSIONS;
    }
    
    if (permissions != null)
    {
      return permissions;
    }
    
    if (getParent() != null)
    {
      return getParent().getPermissions();
    }
    
    return DEFAULT_PERMISSIONS;
  }
  
  public AbstractContext awaitInitialized() throws ContextException
  {
    return this;
  }

  protected void setName(String name)
  {
    path = null; // Resetting cache
    
    if (!ContextUtils.isValidContextName(name))
    {
      throw new IllegalArgumentException(Cres.get().getString("conIllegalName") + name);
    }
    
    this.name = name;
  }
  
  private C getRootWithLookup(CallerController caller)
  {
    if (caller == null)
    {
      return getRoot();
    }
    
    C root = (C) caller.lookup(Contexts.CTX_ROOT);
    
    if (root == null)
    {
      root = getRoot();
      caller.cache(Contexts.CTX_ROOT, root);
    }
    
    return root;
  }
  
  @Override
  public void setParent(C parent)
  {
    this.parent = parent;
  }
  
  protected void setPermissions(Permissions permissions)
  {
    this.permissions = permissions;
  }
  
  protected void setPermissionChecker(PermissionChecker permissionChecker)
  {
    this.permissionChecker = permissionChecker;
  }
  
  protected void setFireUpdateEvents(boolean fireUpdateEvents)
  {
    this.fireUpdateEvents = fireUpdateEvents;
  }
  
  protected boolean isFireUpdateEvents()
  {
    return fireUpdateEvents;
  }
  
  protected void setContextManager(ContextManager contextManager)
  {
    if (this.contextManager != null && this.contextManager != contextManager)
    {
      throw new IllegalStateException("Context manager already set");
    }
    
    this.contextManager = contextManager;
  }
  
  protected void setChildrenViewPermissions(Permissions childrenViewPermissions)
  {
    this.childrenViewPermissions = childrenViewPermissions;
  }
  
  protected void setChildrenSortingEnabled(boolean childrenSortingEnabled)
  {
    this.childrenSortingEnabled = childrenSortingEnabled;
  }
  
  public boolean isChildrenSortingEnabled()
  {
    return childrenSortingEnabled;
  }
  
  protected void setValueCheckingEnabled(boolean valueCheckingEnabled)
  {
    this.valueCheckingEnabled = valueCheckingEnabled;
  }
  
  public boolean isChildrenConcurrencyEnabled()
  {
    return childrenConcurrencyEnabled;
  }
  
  protected void setChildrenConcurrencyEnabled(boolean childrenConcurrencyEnabled)
  {
    this.childrenConcurrencyEnabled = childrenConcurrencyEnabled;
  }
  
  protected void checkPermissions(Permissions needPermissions, CallerController caller, EntityDefinition accessedEntityDefinition) throws ContextSecurityException
  {
    if (!checkPermissions(needPermissions, caller, this, accessedEntityDefinition))
    {
      if (isEntityProtected(accessedEntityDefinition) && caller != null && caller.isPermissionCheckingEnabled())
      {
        throw new ContextSecurityException(MessageFormat.format(Cres.get().getString("conAccessDeniedInProtectedContext"), accessedEntityDefinition.getName(), this.getPath()));
      }
      
      throw new ContextSecurityException(MessageFormat.format(Cres.get().getString("conAccessDenied"), getPath(), caller != null ? caller.getPermissions() : "", needPermissions));
    }
  }
  
  public boolean checkPermissions(Permissions needPermissions, CallerController caller, Context accessedContext, EntityDefinition accessedEntityDefinition)
  {
    if (!permissionCheckingEnabled)
    {
      return true;
    }
    
    if (isEntityProtected(accessedEntityDefinition) && caller != null && caller.isPermissionCheckingEnabled())
    {
      return false;
    }
    
    return getPermissionChecker().has(caller, needPermissions, accessedContext, accessedEntityDefinition);
  }
  
  @Override
  public void addChild(C child)
  {
    addChild(child, (Integer) null);
  }
  
  public void addChild(C child, Integer index)
  {
    addChild(child, index, null);
  }
  
  public void addChild(C child, DataTable childInfo)
  {
    addChild(child, null, childInfo);
  }
  
  public void addChild(C child, Integer index, DataTable childInfo)
  {
    long startTime = System.currentTimeMillis();
    
    addToChildren(child, index);
    
    try
    {
      child.setParent(this);
      
      child.setup(getContextManager());
      
      setChildInfo(child, childInfo);
      
      if (setupComplete && fireUpdateEvents)
      {
        fireEvent(E_CHILD_ADDED, child.getName());
      }
      
      if (getContextManager() != null)
      {
        // If a child added already has own children, contextAdded() won't be called for them
        getContextManager().contextAdded(child);
      }
    }
    catch (Exception ex)
    {
      childrenLock.writeLock().lock();
      try
      {
        childrenMap.remove(child.getName());
        childrenList.remove(child);
        throw new ContextRuntimeException("Error adding child '" + child + "' to context '" + this + "': " + ex.getMessage(), ex);
      }
      finally
      {
        childrenLock.writeLock().unlock();
      }
    }
    
    Log.CONTEXT_CHILDREN.debug("Added child '" + child.getName() + "' to '" + getPath() + "' in " + (System.currentTimeMillis() - startTime) + " ms");
  }
  
  private void setChildInfo(C child, DataTable childInfo) throws ContextException
  {
    if (childInfo == null)
      return;
    
    VariableDefinition vd = child.getVariableDefinition(EditableChildContextConstants.V_CHILD_INFO);
    if (vd != null)
    {
      DataTable settings = new SimpleDataTable(vd.getFormat());
      DataTableReplication.copy(childInfo, settings, true, true, true);
      child.setVariable(vd.getName(), getContextManager().getCallerController(), settings);
    }
  }
  
  private void addToChildren(C child, Integer index)
  {
    childrenLock.writeLock().lock();
    try
    {
      
      Context existing = getChildWithoutCheckingPerms(child.getName());
      if (existing != null)
      {
        throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("conChildExists"), child.getName(), getPath()));
      }
      
      if (index != null)
      {
        if (childrenSortingEnabled)
        {
          throw new IllegalStateException("Cannot add child with pre-defined index as children sorting is enabled");
        }
        childrenList.add(index, child);
      }
      else
      {
        childrenList.add(child);
      }
      childrenMap.put(child.getName().toLowerCase(Locale.ENGLISH), child);
      
      // Disabling sorting for large child sets to avoid performance degradation. Children management should be anyway performed via groups in this case.
      if (childrenSortingEnabled && childrenList.size() < SORT_THRESHOLD)
      {
        Collections.sort(childrenList);
      }
    }
    finally
    {
      childrenLock.writeLock().unlock();
    }
  }
  
  public void removeFromParent()
  {
    if (getParent() != null)
    {
      getParent().removeChild(this);
    }
    else
    {
      Log.CONTEXT_CHILDREN.debug("Can't remove context '" + getPath() + "' from its parent: no parent context was set");
    }
  }
  
  @Override
  public void destroy(boolean moving)
  {
    if (!moving)
    {
      stop();
      destroyChildren(false);
      
    }
    
    if (fireUpdateEvents)
    {
      EventDefinition ed = getEventDefinition(E_DESTROYED);
      if (ed != null)
      {
        fireEvent(ed.getName());
      }
    }
    
    eventDataLock.readLock().lock();
    try
    {
      for (EventData ed : eventData.values())
      {
        Logger logger = Log.CONTEXT_EVENTS;
        if (logger.isDebugEnabled())
        {
          logger.debug("Removing all listeners of event '" + ed.getDefinition().getName() + "'");
        }
        ed.clearListeners();
      }
    }
    finally
    {
      eventDataLock.readLock().unlock();
    }
    
    removeFromParent();
  }
  
  protected void destroyChildren(boolean moving)
  {
    childrenLock.writeLock().lock();
    try
    {
      for (Context child : new ArrayList<Context>(childrenList))
      {
        child.destroy(moving);
      }
    }
    finally
    {
      childrenLock.writeLock().unlock();
    }
  }
  
  @Override
  public void removeChild(final C child)
  {
    child.teardown();
    
    childrenLock.writeLock().lock();
    try
    {
      if (childrenList.contains(child))
      {
        if (getContextManager() != null)
        {
          try
          {
            child.accept(new DefaultContextVisitor<C>(false, false)
            {
              @Override
              public boolean shouldVisit(C context)
              {
                return true;
              }
              
              @Override
              public void visit(C context)
              {
                getContextManager().contextRemoved(context);
                context.setParent(null);

                AbstractContext abstractContext = (AbstractContext) context;

                ReentrantReadWriteLock currentChildLock = abstractContext.childrenLock;
                currentChildLock.writeLock().lock();
                try
                {
                  abstractContext.childrenList.clear();

                  abstractContext.childrenMap.clear();
                }
                finally
                {
                  currentChildLock.writeLock().unlock();
                }
              }
            });
          }
          catch (ContextException ex)
          {
            throw new ContextRuntimeException(ex);
          }
        }
        
        childrenMap.remove(child.getName().toLowerCase(Locale.ENGLISH));
        childrenList.remove(child);
        
        if (setupComplete && fireUpdateEvents)
        {
          fireEvent(E_CHILD_REMOVED, child.getName());
        }
        
        child.setParent(null);
      }
    }
    finally
    {
      childrenLock.writeLock().unlock();
    }
  }
  
  @Override
  public void removeChild(String name)
  {
    C con = getChildWithoutCheckingPerms(name);
    
    if (con != null)
    {
      removeChild(con);
      return;
    }
    
    Log.CONTEXT_CHILDREN.debug("Remove error: child '" + name + "' not found in context " + getPath());
  }
  
  protected void reorderChild(C child, int index)
  {
    if (childrenSortingEnabled)
    {
      throw new IllegalStateException("Cannot reorder children when children sorting is enabled");
    }
    childrenLock.writeLock().lock();
    try
    {
      int oi = childrenList.indexOf(child);
      if (childrenList.remove(child))
      {
        childrenList.add(index - (oi < index ? 1 : 0), child);
      }
    }
    finally
    {
      childrenLock.writeLock().unlock();
    }
  }
  
  @Override
  public void destroyChild(C child, boolean moving)
  {
    child.destroy(moving);
  }
  
  @Override
  public void updatePrepare()
  {
    
  }
  
  protected void movePrepare(String oldPath, String oldName, String newPath, String newName) throws ContextException
  {
    for (Context child : childrenList)
    {
      ((AbstractContext) child).movePrepare(ContextUtils.createName(oldPath, child.getName()), child.getName(), ContextUtils.createName(newPath, child.getName()), child.getName());
    }
  }
  
  protected void moveInternal(String oldPath, String oldName, String newPath, String newName) throws ContextException
  {
    setName(newName);
    
    eventDataLock.readLock().lock();
    try
    {
      for (EventData ed : eventData.values())
      {
        ed.updateContext(oldPath, newPath);
      }
    }
    finally
    {
      eventDataLock.readLock().unlock();
    }

    childrenLock.readLock().lock();
    try
    {
      for (Context child : childrenList)
      {
        ((AbstractContext) child).moveInternal(ContextUtils.createName(oldPath, child.getName()), child.getName(), ContextUtils.createName(newPath, child.getName()), child.getName());
      }
    }
    finally
    {
      childrenLock.readLock().unlock();
    }
  }
  
  protected void moveFinalize(String oldPath, String oldName, String newPath, String newName) throws ContextException
  {
    for (Context child : childrenList)
    {
      ((AbstractContext) child).moveFinalize(ContextUtils.createName(oldPath, child.getName()), child.getName(), ContextUtils.createName(newPath, child.getName()), child.getName());
    }
  }
  
  @Override
  public void move(C newParent, String newName) throws ContextException
  {
    move(getPath(), newParent, newName);
  }
  
  private void move(String oldPath, Context newParent, String newName) throws ContextException
  {
    Log.CONTEXT.debug("Moving context " + getPath() + " to " + newParent.getPath() + " and/or renaming to " + newName);
    
    String oldName = getName();
    
    String newPath = ContextUtils.createName(newParent.getPath(), newName);
    
    movePrepare(oldPath, oldName, newPath, newName);
    
    lockUnlockVariables(true);
    try
    {
      getParent().destroyChild(this, true);
      
      moveInternal(oldPath, oldName, newPath, newName);
      
      newParent.addChild(this);
    }
    finally
    {
      lockUnlockVariables(false);
    }
    
    moveFinalize(oldPath, oldName, newPath, newName);
  }
  
  private void lockUnlockVariables(boolean lock) throws ContextException
  {
    for (VariableDefinition vd : getVariableDefinitions(contextManager.getCallerController()))
    {
      VariableData vdata = getVariableData(vd.getName());
      if (lock)
      {
        try
        {
          if (!vdata.getReadWriteLock().writeLock().tryLock(MOVE_LOCK_TIMEOUT, java.util.concurrent.TimeUnit.MINUTES))
          {
            throw new ContextException(Cres.get().getString("conLockFailed"));
          }
        }
        catch (InterruptedException ex)
        {
          throw new ContextException(Cres.get().getString("conLockFailed"), ex);
        }
      }
      else
      {
        if (vdata.getReadWriteLock().writeLock().isHeldByCurrentThread())
        {
          vdata.getReadWriteLock().writeLock().unlock();
        }
      }
    }
  }
  
  @Override
  public C getChild(String name, CallerController caller)
  {
    if (!checkPermissions(getChildrenViewPermissions(), caller, this, null))
    {
      return null;
    }
    
    C child = getChildWithoutCheckingPerms(name);
    
    if (child != null && shouldSeeChild(caller, child))
    {
      return child;
    }
    
    return null;
  }
  
  @Override
  public C getChild(String name)
  {
    return getChild(name, null);
  }
  
  private C getChildWithoutCheckingPerms(String name)
  {
    return childrenMap.get(name.toLowerCase(Locale.ENGLISH));
  }
  
  @Override
  public String getPath()
  {
    if (getParent() == null)
    {
      return createPath();
    }
    
    if (path == null)
    {
      path = createPath();
    }
    
    return path;
  }
  
  private String createPath()
  {
    Context con = this;
    String nm = getName();
    
    do
    {
      con = con.getParent();
      if (con != null)
      {
        if (con.getParent() != null)
        {
          nm = con.getName() + ContextUtils.CONTEXT_NAME_SEPARATOR + nm;
        }
      }
    }
    while (con != null);
    
    return nm;
  }
  
  @Override
  public boolean addEventListener(String name, ContextEventListener listener)
  {
    return addEventListener(name, listener, false);
  }
  
  @Override
  public boolean addEventListener(String name, ContextEventListener listener, boolean weak)
  {
    EventData ed = getEventData(name);
    
    if (ed == null)
    {
      throw new IllegalArgumentException(Cres.get().getString("conEvtNotAvail") + name);
    }
    
    try
    {
      Permissions permissions = ed.getDefinition().getPermissions() != null ? ed.getDefinition().getPermissions() : getPermissions();
      checkPermissions(permissions, listener.getCallerController(), ed.getDefinition());
    }
    catch (ContextSecurityException ex)
    {
      Log.CONTEXT_EVENTS.warn("Error adding listener '" + listener + "' of event '" + ed.getDefinition().getName() + "' in context '" + getPath() + "': " + ex.getMessage(), ex);
      return false;
    }

    Logger logger = Log.CONTEXT_EVENTS;
    if (logger.isDebugEnabled())
    {
      logger.debug("Adding '" + listener + "' as listener of event '" + ed.getDefinition().getName() + "' in '" + getPath() + "'");
    }

    return ed.addListener(listener, weak);
  }
  
  @Override
  public boolean removeEventListener(String name, ContextEventListener listener)
  {
    EventData ed = getEventData(name);
    
    if (ed == null)
    {
      Log.CONTEXT_EVENTS.warn("Error removing listener of event '" + name + "' in context '" + getPath() + "': event definition not found", new Exception());
      return false;
    }
    
    Logger logger = Log.CONTEXT_EVENTS;
    if (logger.isDebugEnabled())
    {
      logger.debug("Removing '" + listener + "' listener of event '" + ed.getDefinition().getName() + "' in '" + getPath() + "'");
    }
    
    synchronized (ed)
    {
      return ed.removeListener(listener);
    }
  }
  
  @Override
  public List<VariableDefinition> getVariableDefinitions(CallerController caller)
  {
    return getVariableDefinitions(caller, false);
  }
  
  @Override
  public List<VariableDefinition> getVariableDefinitions(CallerController caller, boolean includeHidden)
  {
    List<VariableDefinition> list = null;
    
    variableDataLock.readLock().lock();
    try
    {
      final Collection<VariableData> values = variableData.values();
      list = new ArrayList<>(values.size());
      boolean debug = caller != null && caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_DEBUG);
      
      for (VariableData d : values)
      {
        VariableDefinition def = d.getDefinition();
        if ((caller == null || caller.isPermissionCheckingEnabled()) && !includeHidden && def.isHidden() && !debug)
        {
          continue;
        }
        
        Permissions readPermissions = def.getReadPermissions() != null ? def.getReadPermissions() : getPermissions();
        Permissions writePermissions = def.getWritePermissions() != null ? def.getWritePermissions() : getPermissions();
        
        writePermissions = new Permissions(writePermissions.getPermissions(), true);
        
        boolean readAccessGranted = checkPermissions(readPermissions, caller, this, def);
        boolean writeAccessGranted = checkPermissions(writePermissions, caller, this, def);
        
        if (!readAccessGranted && !writeAccessGranted)
        {
          continue;
        }
        
        if ((def.isReadable() == readAccessGranted) && (def.isWritable() == writeAccessGranted))
        {
          list.add(def);
        }
        else
        {
          VariableDefinition clone = def.clone();
          
          clone.setReadable(def.isReadable() && readAccessGranted);
          clone.setWritable(def.isWritable() && writeAccessGranted);
          
          list.add(clone);
        }
      }
    }
    finally
    {
      variableDataLock.readLock().unlock();
    }
    
    return list;
  }
  
  @Override
  public List<VariableDefinition> getVariableDefinitions()
  {
    return getVariableDefinitions((CallerController) null);
  }
  
  @Override
  public List<VariableDefinition> getVariableDefinitions(CallerController caller, String group)
  {
    final List<VariableDefinition> vars = getVariableDefinitions(caller);
    List<VariableDefinition> defs = new ArrayList<>(vars.size());
    
    for (VariableDefinition vd : vars)
    {
      if (isBelongedToTheGroup(group, vd.getGroup()))
      {
        defs.add(vd);
      }
    }
    
    return defs;
  }
  
  @Override
  public List<VariableDefinition> getVariableDefinitions(String group)
  {
    return getVariableDefinitions(null, group);
  }
  
  public PermissionChecker getPermissionChecker()
  {
    return permissionChecker;
  }
  
  @Override
  public Permissions getChildrenViewPermissions()
  {
    return childrenViewPermissions != null ? childrenViewPermissions : getPermissions();
  }
  
  @Override
  public ContextManager<C> getContextManager()
  {
    return contextManager;
  }
  
  @Override
  public boolean isSetupComplete()
  {
    return setupComplete;
  }
  
  @Override
  public boolean isStarted()
  {
    return started;
  }
  
  public boolean isStopped()
  {
    return stopped;
  }
  
  public boolean isProtected()
  {
    return false;
  }
  
  public boolean isEntityProtected(EntityDefinition ed)
  {
    return false;
  }
  
  @Override
  public boolean isInitializedStatus()
  {
    return setupComplete;
  }
  
  @Override
  public boolean isInitializedInfo()
  {
    return setupComplete;
  }
  
  @Override
  public boolean isInitializedChildren()
  {
    return setupComplete;
  }
  
  @Override
  public boolean isInitializedVariables()
  {
    return setupComplete;
  }
  
  @Override
  public boolean isInitializedFunctions()
  {
    return setupComplete;
  }
  
  @Override
  public boolean isInitializedEvents()
  {
    return setupComplete;
  }
  
  @Override
  public List<FunctionDefinition> getFunctionDefinitions(CallerController caller)
  {
    return getFunctionDefinitions(caller, false);
  }
  
  @Override
  public List<FunctionDefinition> getFunctionDefinitions(CallerController caller, boolean includeHidden)
  {
    // Оптимизация: используем ArrayList вместо LinkedList
    List<FunctionDefinition> list = new ArrayList<FunctionDefinition>();
    
    boolean debug = caller != null && caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_DEBUG);
    
    functionDataLock.readLock().lock();
    try
    {
      for (FunctionData d : functionData.values())
      {
        FunctionDefinition def = d.getDefinition();
        
        Permissions permissions = def.getPermissions() != null ? def.getPermissions() : getPermissions();
        
        if (!checkPermissions(permissions, caller, this, def))
        {
          continue;
        }
        
        if ((caller == null || caller.isPermissionCheckingEnabled()) && !includeHidden && def.isHidden() && !debug)
        {
          continue;
        }
        
        list.add(def);
      }
    }
    finally
    {
      functionDataLock.readLock().unlock();
    }
    
    return list;
  }
  
  @Override
  public List<FunctionDefinition> getFunctionDefinitions()
  {
    return getFunctionDefinitions((CallerController) null);
  }
  
  @Override
  public List<FunctionDefinition> getFunctionDefinitions(CallerController caller, String group)
  {
    List<FunctionDefinition> defs = new LinkedList<>();
    
    for (FunctionDefinition fd : getFunctionDefinitions(caller))
    {
      if (isBelongedToTheGroup(group, fd.getGroup()))
      {
        defs.add(fd);
      }
    }
    
    return defs;
  }
  
  @Override
  public List<FunctionDefinition> getFunctionDefinitions(String group)
  {
    return getFunctionDefinitions(null, group);
  }
  
  protected ReentrantReadWriteLock getChildrenLock()
  {
    return childrenLock;
  }
  
  @Override
  public String getType()
  {
    return type != null ? type : ContextUtils.getTypeForClass(getClass());
  }
  
  public boolean isPermissionCheckingEnabled()
  {
    return permissionCheckingEnabled;
  }
  
  @Override
  public String getIconId()
  {
    return iconId;
  }
  
  @Override
  public Integer getIndex()
  {
    return index;
  }
  
  @Override
  public String getGroup()
  {
    return group;
  }
  
  @Override
  public String getLocalRoot(boolean withParent)
  {
    return Contexts.CTX_ROOT;
  }
  
  @Override
  public boolean isProxy()
  {
    return false;
  }
  
  @Override
  public boolean isDistributed()
  {
    return false;
  }
  
  @Override
  public boolean isContainer()
  {
    return false;
  }

  @Override
  public String getPeerRoot()
  {
    return null;
  }
  
  @Override
  public String getRemoteRoot()
  {
    return null;
  }
  
  @Override
  public String getRemotePath()
  {
    return getPath();
  }
  
  @Override
  public String getPeerPath()
  {
    return getPath();
  }
  
  @Override
  public String getLocalPrimaryRoot()
  {
    return null;
  }
  
  public void setType(String type)
  {
    if (!ContextUtils.isValidContextType(type))
    {
      throw new IllegalArgumentException(Cres.get().getString("conIllegalType") + type);
    }
    
    String old = this.type;
    this.type = type;
    
    if (old == null || !old.equals(type))
    {
      contextInfoChanded();
    }
  }
  
  protected void setPermissionCheckingEnabled(boolean permissionCheckingEnabled)
  {
    this.permissionCheckingEnabled = permissionCheckingEnabled;
  }
  
  protected void setIconId(String iconId)
  {
    String old = this.iconId;
    this.iconId = iconId;
    
    if (old == null || !old.equals(iconId))
    {
      contextInfoChanded();
    }
  }
  
  private void contextInfoChanded()
  {
    if (setupComplete)
    {
      ContextManager cm = getContextManager();
      if (cm != null)
      {
        cm.contextInfoChanged(this);
      }
      
      if (fireUpdateEvents)
      {
        EventDefinition ed = getEventDefinition(E_INFO_CHANGED);
        if (ed != null)
        {
          fireEvent(E_INFO_CHANGED, createContextInfoTable());
        }
      }
    }
  }
  
  /**
   * @deprecated, Use ContextSortingHelper class for set tree context hierarchy
   */
  @Deprecated
  public void setIndex(Integer index)
  {
    this.index = index;
  }
  
  public void setGroup(String group)
  {
    String old = this.group;
    this.group = group;
    
    if (old == null || !old.equals(group))
    {
      contextInfoChanded();
    }
  }
  
  @Override
  public List<EventDefinition> getEventDefinitions(CallerController caller)
  {
    return getEventDefinitions(caller, false);
  }
  
  @Override
  public List<EventDefinition> getEventDefinitions(CallerController caller, boolean includeHidden)
  {
    // Оптимизация: используем ArrayList вместо LinkedList
    List<EventDefinition> list = new ArrayList<EventDefinition>();
    boolean debug = caller != null && caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_DEBUG);
    
    eventDataLock.readLock().lock();
    try
    {
      for (EventData d : eventData.values())
      {
        Permissions permissions = d.getDefinition().getPermissions() != null ? d.getDefinition().getPermissions() : getPermissions();
        
        if (!checkPermissions(permissions, caller, this, d.getDefinition()))
        {
          continue;
        }
        
        if ((caller == null || caller.isPermissionCheckingEnabled()) && !includeHidden && d.getDefinition().isHidden() && !debug)
        {
          continue;
        }
        
        list.add(d.getDefinition());
      }
    }
    finally
    {
      eventDataLock.readLock().unlock();
    }
    
    return list;
  }
  
  @Override
  public List<EventDefinition> getEventDefinitions()
  {
    return getEventDefinitions((CallerController) null);
  }
  
  @Override
  public List<EventDefinition> getEventDefinitions(CallerController caller, String group)
  {
    List<EventDefinition> res = new LinkedList<>();
    
    for (EventDefinition ed : getEventDefinitions(caller))
    {
      if (isBelongedToTheGroup(group, ed.getGroup()))
      {
        res.add(ed);
      }
    }
    
    return res;
  }
  
  @Override
  public List<EventDefinition> getEventDefinitions(String group)
  {
    return getEventDefinitions(null, group);
  }
  
  @Override
  public ActionDefinition getActionDefinition(String name)
  {
    actionDefinitionsLock.readLock().lock();
    try
    {
      for (ActionDefinition def : actionDefinitions)
      {
        if (def.getName().equals(name))
        {
          return def;
        }
      }
    }
    finally
    {
      actionDefinitionsLock.readLock().unlock();
    }
    
    return null;
  }
  
  @Override
  public ActionDefinition getActionDefinition(String name, CallerController caller)
  {
    for (ActionDefinition ad : getActionDefinitions(caller, true))
    {
      if (ad.getName().equals(name))
      {
        return ad;
      }
    }
    
    return null;
  }
  
  @Override
  public ActionDefinition getDefaultActionDefinition(CallerController caller)
  {
    for (ActionDefinition ad : getActionDefinitions(caller, true))
    {
      if (ad.isDefault())
      {
        return ad;
      }
    }
    
    return null;
  }
  
  @Override
  public List<ActionDefinition> getActionDefinitions(CallerController caller)
  {
    return getActionDefinitions(caller, false);
  }
  
  @Override
  public void addActionDefinition(ActionDefinition def)
  {
    if (def.getName() == null)
    {
      throw new NullPointerException("Action name can't be NULL");
    }
    
    actionDefinitionsLock.writeLock().lock();
    try
    {
      for (Iterator<ActionDefinition> iterator = actionDefinitions.iterator(); iterator.hasNext();)
      {
        ActionDefinition actionDefinition = iterator.next();
        final boolean actionAlreadyPresent = def.getName().equals(actionDefinition.getName());
        
        if (actionAlreadyPresent)
        {
          iterator.remove();
          break;
        }
      }
      
      actionDefinitions.add(def);
      
      Collections.sort(actionDefinitions);
      
      if (isSetupComplete() && isFireUpdateEvents())
      {
        EventDefinition ed = getEventDefinition(AbstractContext.E_ACTION_ADDED);
        if (ed != null)
        {
          fireEvent(ed.getName(), actDefToDataRecord(def).wrap());
        }
      }
    }
    finally
    {
      actionDefinitionsLock.writeLock().unlock();
    }
  }
  
  @Override
  public List<ActionDefinition> getActionDefinitions(CallerController caller, boolean includeHidden)
  {
    // Оптимизация: используем ArrayList вместо LinkedList
    List<ActionDefinition> list = new ArrayList<ActionDefinition>();
    boolean debug = caller != null && caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_DEBUG);
    
    actionDefinitionsLock.readLock().lock();
    try
    {
      for (ActionDefinition d : actionDefinitions)
      {
        if (!checkPermissions(d.getPermissions() != null ? d.getPermissions() : getPermissions(), caller, this, d))
        {
          continue;
        }
        
        if (d.isHidden() && !debug && !includeHidden)
        {
          continue;
        }
        
        list.add(d);
      }
    }
    finally
    {
      actionDefinitionsLock.readLock().unlock();
    }
    
    return list;
  }
  
  @Override
  public List<ActionDefinition> getActionDefinitions()
  {
    return getActionDefinitions(null);
  }

  /**
   * Extracts table format with given getter and puts it into current {@linkplain FormatCache format cache}. If the cache already contains such format, <strong>replaces</strong> the format with cached
   * one by means of given format setter.
   *
   * @param formatGetter
   *          a method link to extract the format (like {@link VariableDefinition#getFormat})
   * @param formatSetter
   *          a method link to replace the format (like {@link VariableDefinition#setFormat})
   */
  protected void applyCachedFormat(Supplier<TableFormat> formatGetter, Consumer<TableFormat> formatSetter)
  {
    TableFormat format = formatGetter.get();
    if (format == null || !format.isImmutable())
    {
      return;
    }
    Optional<FormatCache> formatCacheOpt = obtainFormatCache();
    if (formatCacheOpt.isPresent())
    {
      FormatCache formatCache = formatCacheOpt.get();
      format.applyCachedFormat(formatCache, formatSetter);
    }
  }
  
  @Override
  public void removeActionDefinition(String name)
  {
    ActionDefinition def = getActionDefinition(name);
    
    actionDefinitionsLock.writeLock().lock();
    try
    {
      if (actionDefinitions.remove(def))
      {
        if (isSetupComplete() && isFireUpdateEvents())
        {
          EventDefinition ed = getEventDefinition(AbstractContext.E_ACTION_REMOVED);
          if (ed != null)
          {
            fireEvent(ed.getName(), name);
          }
        }
      }
    }
    finally
    {
      actionDefinitionsLock.writeLock().unlock();
    }
  }
  
  public ActionDefinition actDefFromDataRecord(DataRecord rec)
  {
    BasicActionDefinition def = new BasicActionDefinition(rec.getString(ActionConstants.FIELD_AD_NAME));
    def.setDescription(rec.getString(ActionConstants.FIELD_AD_DESCRIPTION));
    def.setHelp(rec.getString(ActionConstants.FIELD_AD_HELP));
    
    String accelerator = rec.getString(ActionConstants.FIELD_AD_ACCELERATOR);
    if (accelerator != null)
    {
      def.setAccelerator(new KeyStroke(accelerator));
    }
    
    DataTable dropSourcesTable = rec.getDataTable(ActionConstants.FIELD_AD_DROP_SOURCES);
    if (dropSourcesTable != null && dropSourcesTable.getRecordCount() > 0)
    {
      // Оптимизация: используем ArrayList вместо LinkedList
      List<ResourceMask> dropSources = new ArrayList<ResourceMask>();
      for (DataRecord ds : dropSourcesTable)
      {
        dropSources.add(new TreeMask(ds.getString(ActionConstants.FIELD_AD_RESOURCE_MASKS_RESOURCE_MASK)));
      }
      def.setDropSources(dropSources);
    }
    
    def.setHidden(rec.getBoolean(ActionConstants.FIELD_AD_HIDDEN));
    def.setEnabled(rec.getBoolean(ActionConstants.FIELD_AD_ENABLED));
    def.setIconId(rec.getString(ActionConstants.FIELD_AD_ICON_ID));
    def.setGroup(rec.getString(ActionConstants.FIELD_AD_GROUP));
    
    String executionGroup = rec.getString(ActionConstants.FIELD_AD_EXECUTION_GROUP);
    if (executionGroup != null)
    {
      def.setExecutionGroup(new GroupIdentifier(executionGroup));
    }
    
    if (rec.hasField(ActionConstants.FIELD_AD_PERMISSIONS))
      def.setPermissions(new Permissions(rec.getString(ActionConstants.FIELD_AD_PERMISSIONS)));

    def.setDefault(rec.getBoolean(ActionConstants.FIELD_AD_DEFAULT));
    
    return def;
  }
  
  private DataTable getVariable(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    long startTime = System.currentTimeMillis();
    
    setupVariables();
    
    VariableData data = getVariableData(def.getName());
    
    lock(request, data.getReadWriteLock().readLock());

    boolean hasSpan = beginSpan(request, def);
    try
    {
      try
      {
        Permissions permissions = def.getReadPermissions() != null ? def.getReadPermissions() : getPermissions();
        checkPermissions(permissions, caller, def);
        
        if (Log.CONTEXT_VARIABLES.isDebugEnabled())
        {
          Log.CONTEXT_VARIABLES.debug("Trying to get variable '" + def.getName() + "' from context '" + this.getPath() + "'");
        }

        if (hasSpan)
        {
          traceOperation(GET_VARIABLE);
        }

        DataTable result = executeGetter(data, caller, request);

        if (result.isInvalid())
        {
          throw new ContextException(result.getInvalidationMessage());
        }
        
        result = checkVariableValue(def, result, caller);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (duration > LOW_PERFORMANCE_THRESHOLD)
        {
          Level level = duration > VERY_LOW_PERFORMANCE_THRESHOLD ? Level.INFO : Level.DEBUG;
          Log.PERFORMANCE.log(level, "Getting value of variable '" + def + "' in context '" + getPath() + "' took " + duration + " milliseconds");
        }
        logAsyncSessionExecutionAction(caller, request, GET_VARIABLE, def.getName(), null, result, duration);
        return result;
      }
      catch (Exception ex)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("conErrGettingVar"), def.toString(), toString()) + ex.getMessage(), ex);
      }
    }
    finally
    {
      data.getReadWriteLock().readLock().unlock();
      data.registerGetOperation();
      if (hasSpan)
      {
        endSpan();
      }
    }
  }

  private DataTable checkVariableValue(VariableDefinition def, DataTable val, CallerController caller) throws ContextException
  {
    if (!valueCheckingEnabled)
    {
      return val;
    }
    
    DataTable value = val;
    
    if (caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_VALIDATION))
    {
      String msg = checkVariableValueFormat(def, value);
      
      if (msg != null)
      {
        Log.CONTEXT_VARIABLES.debug("Invalid value of variable '" + def.getName() + "': " + msg);
        DataTable newValue = getDefaultValue(def);
        DataTableReplication.copy(value, newValue, true, true, true, true, true);
        List<CompatibilityConverter> converters = def.getCompatibilityConverters();
        if (converters != null)
        {
          for (CompatibilityConverter converter : converters)
          {
            try
            {
              newValue = converter.convert(value, newValue);
            }
            catch (Exception ex)
            {
              Log.CONTEXT_VARIABLES.warn("Error converting value of variable '" + def.getName() + "' by '" + converter + "': " + ex.getMessage(), ex);
            }
          }
        }
        
        value = newValue;
        checkVariableValueFormat(def, value);
      }
    }
    
    return value;
  }
  
  private String checkVariableValueFormat(VariableDefinition def, DataTable table)
  {
    if (!valueCheckingEnabled)
    {
      return null;
    }
    
    if (def.getCompatibilityValidator() != null)
    {
      return def.getCompatibilityValidator().needConvertVariableFormat(def, table);
    }
    
    TableFormat requiredFormat = def.getFormat();
    
    if (requiredFormat != null)
    {
      String msg = table.conformMessage(requiredFormat);
      if (msg != null)
      {
        return "Invalid format: " + msg;
      }
    }
    
    return null;
  }
  
  private DataTable executeGetter(VariableData data, CallerController caller, RequestController request) throws IllegalAccessException, ContextException
  {
    DataTable result = executeGetterMethod(data, caller, request);
    if (result != null)
    {
      return result;
    }
    
    VariableDefinition def = data.getDefinition();
    
    if (def.getGetter() != null)
    {
      result = def.getGetter().get(this, def, caller, request);
    }
    if (result != null)
    {
      return result;
    }
    
    result = getVariableImpl(def, caller, request);
    if (result != null)
    {
      return result;
    }
    
    return executeDefaultGetter(def, caller, false, true); // Setting check to false as we'll check value later
  }
  
  private DataTable executeGetterMethod(VariableData data, CallerController caller, RequestController request) throws IllegalArgumentException, IllegalAccessException, ContextException
  {
    if (!data.isGetterCached())
    {
      Class[] params = { VariableDefinition.class, CallerController.class, RequestController.class };
      
      try
      {
        Method getter = getClass().getMethod(GETTER_METHOD_PREFIX + data.getDefinition().getName(), params);
        data.setGetterMethod(getter);
      }
      catch (NoSuchMethodException ex)
      {
        return null;
      }
      finally
      {
        data.setGetterCached(true);
      }
    }
    
    Method getter = data.getGetterMethod();
    
    if (getter != null)
    {
      try
      {
        return (DataTable) getter.invoke(this, new Object[] { data.getDefinition(), caller, request });
      }
      catch (InvocationTargetException ex)
      {
        throw new ContextException(ex.getCause().getMessage(), ex.getCause());
      }
    }
    
    return null;
  }
  
  public DataTable executeDefaultGetter(String name, CallerController caller) throws ContextException
  {
    return executeDefaultGetter(name, caller, true);
  }
  
  public DataTable executeDefaultGetter(String name, CallerController caller, boolean check) throws ContextException
  {
    return executeDefaultGetter(name, caller, check, true);
  }
  
  public DataTable executeDefaultGetter(String name, CallerController caller, boolean check, boolean createDefault) throws ContextException
  {
    VariableDefinition def = getVariableDefinition(name);
    
    if (def == null)
    {
      throw new ContextException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), name, getPath()));
    }
    
    return executeDefaultGetter(def, caller, check, createDefault);
  }
  
  private DataTable executeDefaultGetter(VariableDefinition def, CallerController caller, boolean check, boolean createDefault) throws ContextException
  {
    DataTable value = executeDefaultGetterImpl(def, caller);
    
    if (value == null)
    {
      return createDefault ? getDefaultValue(def) : null;
    }
    
    return check ? checkVariableValue(def, value, caller) : value;
  }
  
  protected DataTable executeDefaultGetterImpl(VariableDefinition vd, CallerController caller) throws ContextException
  {
    Object value = getVariableData(vd.getName()).getValue();
    
    return value != null ? (DataTable) value : getDefaultValue(vd);
  }
  
  @Override
  public int hashCode()
  {
    if (getParent() == null)
    {
      return super.hashCode();
    }
    
    final int prime = 31;
    int result = 1;
    Context root = getRoot();
    String path = getPath();
    result = prime * result + ((root == null) ? 0 : root.hashCode());
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    AbstractContext other = (AbstractContext) obj;
    if (getRoot() != other.getRoot())
    {
      return false;
    }
    return Util.equals(getPath(), other.getPath());
  }
  
  @Override
  public DataTable getVariable(String name, CallerController caller, RequestController request) throws ContextException
  {
    return getVariable(getAndCheckVariableDefinition(name), caller, request);
  }
  
  @Override
  public DataTable getVariable(String name, CallerController caller) throws ContextException
  {
    return getVariable(getAndCheckVariableDefinition(name), caller, null);
  }
  
  @Override
  public DataTable getVariable(String name) throws ContextException
  {
    return getVariable(getAndCheckVariableDefinition(name), null, null);
  }
  
  @Override
  public DataTable getVariableClone(String name, CallerController caller) throws ContextException
  {
    return getVariable(name, caller).clone();
  }

  protected DataTable getVariableImpl(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    return null;
  }
  
  @Override
  public Object getVariableObject(String name, CallerController caller)
  {
    try
    {
      VariableDefinition def = getAndCheckVariableDefinition(name);
      VariableData data = getVariableData(name);
      
      data.getReadWriteLock().readLock().lock();
      
      try
      {
        if (isSetupComplete() && data.getValue() != null)
        {
          return data.getValue();
        }
        
        if (def.getValueClass() == null)
        {
          throw new ContextException("Value class not defined for variable: " + def.toDetailedString());
        }
        
        Object value = null;
        
        DataTable table = getVariable(name, caller);
        
        List list = DataTableConversion.beansFromTable(table, def.getValueClass(), def.getFormat(), true);
        
        if (def.getFormat().isSingleRecord())
        {
          value = list.get(0);
        }
        else
        {
          value = list;
        }
        
        // Caching must be disabled if write lock is held by current thread (e.g. this method is called from variable setter)
        if (isSetupComplete() && def.isLocalCachingEnabled() && !data.getReadWriteLock().isWriteLockedByCurrentThread())
        {
          data.setValue(value);
        }
        
        return value;
      }
      finally
      {
        data.getReadWriteLock().readLock().unlock();
      }
    }
    catch (Exception ex)
    {
      throw new ContextRuntimeException(ex.getMessage(), ex);
    }
  }
  
  private void setVariable(VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
  {
    validateVariableValueToSet(def, value);

    long startTime = System.currentTimeMillis();
    
    setupVariables();
    
    VariableData data = getVariableData(def.getName());
    
    boolean readLockedBySameThread = data.getReadWriteLock().getReadHoldCount() > 0;
    
    if (!readLockedBySameThread)
    {
      lock(request, data.getReadWriteLock().writeLock());
    }

    boolean hasSpan = beginSpan(request, def);
    try
    {
      if (value == null)
      {
        throw new ContextException("Value cannot be NULL");
      }

      DataTable resultingValue = value;
      try
      {
        Permissions writePermissions = def.getWritePermissions() != null ? def.getWritePermissions() : getPermissions();
        
        writePermissions = new Permissions(writePermissions.getPermissions(), true);
        
        checkPermissions(writePermissions, caller, def);
        
        if (!def.isWritable() && caller != null && caller.isPermissionCheckingEnabled())
        {
          throw new ContextException(Cres.get().getString("conVarReadOnly"));
        }
        
        if (Log.CONTEXT_VARIABLES.isDebugEnabled())
        {
          Log.CONTEXT_VARIABLES.debug("Trying to set variable '" + def.getName() + "' in context '" + this.getPath() + "'");
        }

        if (hasSpan)
        {
          traceOperation(SET_VARIABLE);
        }

        /**
         * Кэширование результата getVariable() для оптимизации производительности.
         * 
         * Проблема: Метод setVariable() ранее вызывал getVariable() несколько раз:
         * 1. Для получения старого значения при storeChangesOnlyInHistory()
         * 2. Для получения текущего значения при проверке read-only полей
         * 3. Для получения текущего значения при валидации формата
         * 
         * Решение: Кэшируем результат первого вызова getVariable() и используем его
         * во всех последующих местах, где требуется текущее значение переменной.
         * 
         * Эффект: 20-30% улучшение производительности setVariable()
         * 
         * @see #getVariable(VariableDefinition, CallerController, RequestController)
         */
        DataTable cachedCurrentValue = null;
        boolean needCurrentValue = def.isAddPreviousValueToVariableUpdateEvent() 
            || def.storeChangesOnlyInHistory()
            || (value.isSimple() && def.getFormat() != null && def.getFormat().hasReadOnlyFields() 
                && caller != null && caller.isPermissionCheckingEnabled())
            || (caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_VALIDATION));
        
        if (needCurrentValue)
        {
          cachedCurrentValue = getVariable(def, caller, request);
        }

        DataTable oldValue = null;
        if (def.isAddPreviousValueToVariableUpdateEvent() || def.storeChangesOnlyInHistory())
          oldValue = cachedCurrentValue;
        
        if (def.storeChangesOnlyInHistory())
        {
          // Edge case: если oldValue null, считаем, что значение изменилось
          boolean valueNotChanged = (oldValue != null && value.equals(oldValue));
          
          if (valueNotChanged)
            return;
        }
        
        if (caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_VALIDATION))
        {
          value.validate(this, this.getContextManager(), caller);
        }
        
        if (value.getTimestamp() == null)
        {
          value = value.cloneIfImmutable();
          resultingValue = value;
          value.setTimestamp(new Date());
        }
        
        // Preventing changes to read-only fields to be made by "client" callers (i.e. ones with permission checking enabled)
        if (value.isSimple() && def.getFormat() != null && def.getFormat().hasReadOnlyFields() && caller != null && caller.isPermissionCheckingEnabled())
        {
          // Use cached value instead of calling getVariable() again
          if (cachedCurrentValue == null)
          {
            cachedCurrentValue = getVariable(def, caller, request);
          }
          resultingValue = cachedCurrentValue.clone();
          DataTableReplication.copy(value, resultingValue, false, true, true, true, true);
          checkVariableValueFormat(def, resultingValue);
        }
        
        if (caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_VALIDATION))
        {
          String msg = checkVariableValueFormat(def, resultingValue);
          
          if (msg != null)
          {
            Log.CONTEXT_VARIABLES.debug("Invalid value of variable '" + def.getName() + "': " + msg + " (value: " + resultingValue + ")");
            value = resultingValue;
            // Use cached value instead of calling getVariable() again
            if (cachedCurrentValue == null)
            {
              cachedCurrentValue = getVariable(def, caller, request);
            }
            resultingValue = cachedCurrentValue.clone();
            DataTableReplication.copy(value, resultingValue, true, true, true, true, true);
          }
        }
        
        if (def.isLocalCachingEnabled())
        {
          data.setValue(null); // Resetting cache
        }

        if (executeSetter(data, caller, request, resultingValue))
          variableUpdated(def, caller, request, resultingValue, oldValue);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (duration > LOW_PERFORMANCE_THRESHOLD)
        {
          Level level = duration > VERY_LOW_PERFORMANCE_THRESHOLD ? Level.INFO : Level.DEBUG;
          Log.PERFORMANCE.log(level, "Setting value of variable '" + def + "' in context '" + getPath() + "' took " + duration + " milliseconds");
        }
        logAsyncSessionExecutionAction(caller, request, SET_VARIABLE, def.getName(), value, null, duration);
      }
      catch (ValidationException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("conErrSettingVar"), def.toString(), toString()) + ex.getMessage(), ex);
      }
    }
    finally
    {
      if (!readLockedBySameThread)
      {
        data.getReadWriteLock().writeLock().unlock();
      }
      data.registerSetOperation();
      if (hasSpan)
      {
        endSpan();
      }
    }
  }
  
  protected void variableUpdated(VariableDefinition def, CallerController caller, RequestController request, DataTable value, DataTable valueOld) throws ContextException
  {
    fireUpdatedEvent(def, caller, request, value, valueOld);
    
    fireChangeEvent(def, caller, new Date(), value);
  }
  
  protected void fireUpdatedEvent(VariableDefinition def, CallerController caller, RequestController request, DataTable value, DataTable valueOld) throws ContextException
  {
    boolean callerAllowsUpdatedEvents = caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_UPDATED_EVENTS);
    
    if (isAllowUpdatedEvents(def) && callerAllowsUpdatedEvents)
    {
      EventDefinition ed = getEventDefinition(E_UPDATED);
      if (ed != null)
      {
        SimpleDataTable status = new SimpleDataTable(VFT_VARIABLE_STATUSES);
        VariableStatus variableStatus = getVariableStatus(def);
        if (variableStatus != null)
        {
          status = new SimpleDataTable(VFT_VARIABLE_STATUSES, def.getName(), variableStatus.getStatus(), variableStatus.getComment());
        }

        int updateOriginator = request != null ? request.getOriginator().getOriginatorType() : Originator.TYPE_UNDEFINED;
        fireEvent(E_UPDATED, def.getName(), value, valueOld, caller != null ? caller.getUsername() : null, updateOriginator, status);
      }
    }
  }
  
  public void fireChangeEvent(VariableDefinition def, CallerController caller, Date timestamp, DataTable value)
  {
    boolean callerAllowsChangeEvents = caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_CHANGE_EVENTS);
    
    if (isAllowUpdatedEvents(def) && callerAllowsChangeEvents)
    {
      EventDefinition ed = getEventDefinition(E_CHANGE);
      if (ed != null)
      {
        FireEventRequestController fer = new FireChangeEventRequestController(def.getChangeEventsExpirationPeriod(), def, value);
        
        DataTable eventData = new SimpleDataTable(ed.getFormat(), def.getName());
        
        fireEvent(ed, eventData, EventLevel.NONE, null, timestamp, null, caller, fer, null);
      }
    }
  }
  
  protected boolean isAllowUpdatedEvents(VariableDefinition def)
  {
    return setupComplete && fireUpdateEvents && def != null && def.isAllowUpdateEvents();
  }
  
  protected void setupVariables() throws ContextException
  {
  }
  
  private boolean executeSetter(VariableData data, CallerController caller, RequestController request, DataTable value) throws IllegalAccessException, ContextException
  {
    VariableDefinition def = data.getDefinition();
    
    if (!isAllowSetterExecution(def, request, value))
      return false;
    
    if (executeSetterMethod(data, caller, request, value))
    {
      return true;
    }
    
    if (def.getSetter() != null)
    {
      if (def.getSetter().set(this, def, caller, request, value))
      {
        return true;
      }
    }
    
    if (setVariableImpl(def, caller, request, value))
    {
      return true;
    }
    
    executeDefaultSetter(def, caller, value);
    return true;
  }
  
  private boolean executeSetterMethod(VariableData data, CallerController caller, RequestController request, DataTable value) throws IllegalArgumentException, IllegalAccessException, ContextException
  {
    if (!data.isSetterCached())
    {
      Class[] params = { VariableDefinition.class, CallerController.class, RequestController.class, DataTable.class };
      try
      {
        Method setter = getClass().getMethod(SETTER_METHOD_PREFIX + data.getDefinition().getName(), params);
        data.setSetterMethod(setter);
      }
      catch (NoSuchMethodException ex)
      {
        return false;
      }
      finally
      {
        data.setSetterCached(true);
      }
    }
    
    Method setter = data.getSetterMethod();
    
    if (setter != null)
    {
      try
      {
        setter.invoke(this, data.getDefinition(), caller, request, value);
        
        return true;
      }
      catch (InvocationTargetException ex)
      {
        throw new ContextException(ex.getCause().getMessage(), ex.getCause());
      }
    }
    
    return false;
  }
  
  public DataTable getDefaultValue(VariableDefinition def)
  {
    DataTable defaultValue = def.getDefaultValue();
    if (defaultValue != null)
    {
      return defaultValue;
    }
    
    return new SimpleDataTable(def.getFormat(), true);
  }
  
  public void executeDefaultSetter(String name, CallerController caller, DataTable value) throws ContextException
  {
    VariableDefinition def = getVariableDefinition(name);
    
    if (def == null)
    {
      throw new ContextException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), name, getPath()));
    }
    
    executeDefaultSetter(def, caller, value);
  }
  
  public void executeDefaultSetter(VariableDefinition def, CallerController caller, DataTable value) throws ContextException
  {
    executeDefaultSetterImpl(def, caller, value);
  }
  
  protected void executeDefaultSetterImpl(VariableDefinition vd, CallerController caller, DataTable value) throws ContextException
  {
    getVariableData(vd.getName()).setValue(value);
  }
  
  @Override
  public void setVariable(String name, CallerController caller, RequestController request, DataTable value) throws ContextException
  {
    VariableDefinition def = getAndCheckVariableDefinition(name);
    setVariable(def, caller, request, value);
  }
  
  @Override
  public void setVariable(String name, CallerController caller, DataTable value) throws ContextException
  {
    setVariable(name, caller, null, value);
  }
  
  @Override
  public void setVariable(String name, DataTable value) throws ContextException
  {
    setVariable(name, null, null, value);
  }
  
  @Override
  public void setVariable(String name, CallerController caller, Object... value) throws ContextException
  {
    VariableDefinition def = getAndCheckVariableDefinition(name);
    setVariable(name, caller, null, new SimpleDataTable(def.getFormat(), value));
  }
  
  @Override
  public void setVariable(String name, Object... value) throws ContextException
  {
    setVariable(name, null, value);
  }
  
  protected boolean setVariableImpl(VariableDefinition def, CallerController caller, RequestController request, DataTable value) throws ContextException
  {
    return false;
  }
  
  protected boolean isAllowSetterExecution(VariableDefinition data, RequestController request, DataTable value) throws ContextException
  {
    return true;
  }
  
  private VariableDefinition getAndCheckVariableDefinition(String name) throws ContextException
  {
    setupVariables();
    
    VariableDefinition def = getVariableDefinition(name);
    
    if (def == null)
    {
      throw new ContextException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), name, getPath()));
    }
    
    return def;
  }
  
  @Override
  public boolean setVariableField(String variable, String field, Object value, CallerController cc) throws ContextException
  {
    return setVariableField(variable, field, 0, value, cc);
  }
  
  @Override
  public boolean setVariableField(String variable, String field, int record, Object value, CallerController cc, RequestController request) throws ContextException
  {
    DataTable tab = getVariableClone(variable, cc);
    
    tab.setTimestamp(new Date());
    Object old = tab.getRecord(record).getValue(field);
    tab.getRecord(record).setValue(field, value);
    setVariable(variable, cc, request, tab);
    return old == null ? value != null : !old.equals(value);
  }
  
  @Override
  public void setVariableField(String variable, String field, Object value, String compareField, Object compareValue, CallerController cc) throws ContextException
  {
    DataTable tab = getVariableClone(variable, cc);
    DataRecord rec = tab.select(compareField, compareValue);
    
    if (rec != null)
    {
      rec.setValue(field, value);
      tab.setTimestamp(new Date());
    }
    else
    {
      throw new ContextException("Record with " + compareField + "=" + compareValue + " not found");
    }
    setVariable(variable, cc, tab);
  }
  
  public void addVariableRecord(String variable, CallerController cc, DataRecord record) throws ContextException
  {
    DataTable tab = getVariableClone(variable, cc);
    tab.addRecord(record);
    setVariable(variable, cc, tab);
  }
  
  public void addVariableRecord(String variable, CallerController cc, Object... recordData) throws ContextException
  {
    DataTable tab = getVariableClone(variable, cc);
    DataRecord rec = tab.addRecord();
    for (int i = 0; i < recordData.length; i++)
    {
      rec.addValue(recordData[i]);
    }
    setVariable(variable, cc, tab);
  }
  
  public void removeVariableRecords(String variable, CallerController cc, String field, Object value) throws ContextException
  {
    DataTable tab = getVariableClone(variable, cc);
    
    for (Iterator i = tab.iterator(); i.hasNext();)
    {
      DataRecord rec = (DataRecord) i.next();
      if (Util.equals(rec.getValue(field), value))
      {
        i.remove();
      }
    }
    
    setVariable(variable, cc, tab);
  }
  
  protected DataTable callFunction(FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
  {
    long startTime = System.currentTimeMillis();
    
    setupFunctions();
    
    FunctionData data = getFunctionData(def.getName());
    
    if (!def.isConcurrent())
    {
      lock(request, data.getExecutionLock());
    }

    boolean hasSpan = beginSpan(request, def);
    try
    {
      try
      {
        checkPermissions(def.getPermissions() != null ? def.getPermissions() : getPermissions(), caller, def);
        
        Log.CONTEXT_FUNCTIONS.debug("Trying to call function '" + def.getName() + "' of context '" + getPath() + "'");
        
        if (def.getPermissions() != null)
        {
          checkPermissions(def.getPermissions(), caller, def);
        }

        if (hasSpan)
        {
          traceOperation(CALL_FUNCTION);
        }

        TableFormat requiredInputFormat = def.getInputFormat();
        TableFormat requiredOutputFormat = def.getOutputFormat();
        
        parameters.validate(this, this.getContextManager(), caller);
        
        if (valueCheckingEnabled && requiredInputFormat != null && (caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_VALIDATION)))
        {
          String msg = parameters.conformMessage(requiredInputFormat);
          if (msg != null)
          {
            Log.CONTEXT_FUNCTIONS.debug("Invalid input format of function '" + def.getName() + "': " + msg);
            
            DataTable newParameters = new SimpleDataTable(def.getInputFormat(), true);
            DataTableReplication.copy(parameters, newParameters, true, true, true, true, true);
            parameters = newParameters;
            
            msg = parameters.conformMessage(requiredInputFormat);
            if (msg != null)
            {
              throw new ContextException("Invalid format: " + msg);
            }
          }
        }

        DataTable result = executeImplementation(data, caller, request, parameters);

        if (result.isInvalid())
        {
          throw new ContextException(result.getInvalidationMessage());
        }

        if (result.getRecordCount() != null && result.getRecordCount() == 0 && result.getFormat().getFieldCount() == 0)
        {
          result = result.cloneIfImmutable().setFormat(def.getOutputFormat());
        }
        
        if (valueCheckingEnabled && requiredOutputFormat != null && (caller == null || !caller.getProperties().containsKey(CALLER_CONTROLLER_PROPERTY_NO_VALIDATION)))
        {
          String msg = result.conformMessage(requiredOutputFormat);
          if (msg != null)
          {
            Log.CONTEXT_FUNCTIONS.debug("Invalid output format of function '" + def.getName() + "': " + msg);
            
            DataTable newResult = new SimpleDataTable(def.getOutputFormat(), true);
            DataTableReplication.copy(result, newResult, true, true, true, true, true);
            result = newResult;
            
            msg = result.conformMessage(requiredOutputFormat);
            if (msg != null)
            {
              throw new ContextException("Function '" + def.getName() + "' of context '" + getPath() + "' returned value of invalid format: " + msg);
            }
          }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (duration > LOW_PERFORMANCE_THRESHOLD)
        {
          Level level = duration > VERY_LOW_PERFORMANCE_THRESHOLD ? Level.INFO : Level.DEBUG;
          Log.PERFORMANCE.log(level, "Function '" + def + "' in context '" + getPath() + "' was executing for " + duration + " milliseconds");
        }
        
        logAsyncSessionExecutionAction(caller, request, CALL_FUNCTION, def.getName(), parameters, result, duration);
        return result;
      }
      catch (ContextException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new ContextException(MessageFormat.format(Cres.get().getString("conErrCallingFunc"), def.toString(), toString()) + ex.getMessage(), ex);
      }
    }
    finally
    {
      if (!def.isConcurrent())
      {
        data.getExecutionLock().unlock();
      }
      data.registerExecution();
      if (hasSpan)
      {
        endSpan();
      }
    }
  }

  private DataTable executeImplementation(FunctionData data, CallerController caller, RequestController request, DataTable parameters) throws IllegalAccessException, ContextException
  {
    DataTable result = executeImplementationMethod(data, caller, request, parameters);
    
    if (result != null)
    {
      return result;
    }
    
    FunctionDefinition def = data.getDefinition();
    
    if (def.getImplementation() != null)
    {
      result = def.getImplementation().execute(this, def, caller, request, parameters);
      
      if (result != null)
      {
        return result;
      }
      
      return getDefaultFunctionOutput(def);
    }
    
    result = callFunctionImpl(def, caller, request, parameters);
    
    if (result != null)
    {
      return result;
    }
    
    throw new ContextException(MessageFormat.format(Cres.get().getString("conFuncNotImpl"), def.getName(), getPath()));
  }
  
  private DataTable executeImplementationMethod(FunctionData data, CallerController caller, RequestController request, DataTable parameters)
      throws IllegalArgumentException, IllegalAccessException, ContextException
  {
    FunctionDefinition def = data.getDefinition();
    
    if (!data.isImplementationCached())
    {
      Class[] callerParams = { FunctionDefinition.class, CallerController.class, RequestController.class, DataTable.class };
      try
      {
        Method implementation = getClass().getMethod(IMPLEMENTATION_METHOD_PREFIX + def.getName(), callerParams);
        data.setImplementationMethod(implementation);
      }
      catch (NoSuchMethodException ex)
      {
        return null;
      }
      finally
      {
        data.setImplementationCached(true);
      }
    }
    
    Method implementation = data.getImplementationMethod();
    
    if (implementation != null)
    {
      try
      {
        DataTable result = (DataTable) implementation.invoke(this, new Object[] { def, caller, request, parameters });
        
        if (result != null)
        {
          return result;
        }
        
        return getDefaultFunctionOutput(def);
      }
      catch (InvocationTargetException ex)
      {
        Throwable cause = ex.getCause();

        throw new ContextException(cause.getMessage(), cause);
      }
    }
    
    return null;
  }
  
  private DataTable getDefaultFunctionOutput(FunctionDefinition def)
  {
    final TableFormat format = def.getOutputFormat();
    return format != null ? new SimpleDataTable(format, true) : new SimpleDataTable();
  }
  
  protected void setupFunctions() throws ContextException
  {
  }
  
  @Override
  public DataTable callFunction(String name, CallerController caller, RequestController request, DataTable parameters) throws ContextException
  {
    FunctionDefinition def = getAndCheckFunctionDefinition(name);
    return callFunction(def, caller, request, parameters);
  }
  
  @Override
  public DataTable callFunction(String name, CallerController caller, DataTable parameters) throws ContextException
  {
    return callFunction(name, caller, null, parameters);
  }
  
  @Override
  public DataTable callFunction(String name, DataTable parameters) throws ContextException
  {
    return callFunction(getAndCheckFunctionDefinition(name), null, null, parameters);
  }
  
  @Override
  public DataTable callFunction(String name) throws ContextException
  {
    FunctionDefinition def = getAndCheckFunctionDefinition(name);
    return callFunction(def, null, null, new SimpleDataTable(def.getInputFormat(), true));
  }
  
  @Override
  public DataTable callFunction(String name, CallerController caller) throws ContextException
  {
    FunctionDefinition def = getAndCheckFunctionDefinition(name);
    return callFunction(def, caller, null, new SimpleDataTable(def.getInputFormat(), true));
  }
  
  @Override
  public DataTable callFunction(String name, CallerController caller, Object... parameters) throws ContextException
  {
    FunctionDefinition def = getAndCheckFunctionDefinition(name);
    return callFunction(name, caller, new SimpleDataTable(def.getInputFormat(), parameters));
  }
  
  @Override
  public DataTable callFunction(String name, Object... parameters) throws ContextException
  {
    return callFunction(name, null, parameters);
  }
  
  protected DataTable callFunctionImpl(FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
  {
    return null;
  }
  
  private FunctionDefinition getAndCheckFunctionDefinition(String name) throws ContextException
  {
    setupFunctions();
    
    FunctionDefinition def = getFunctionDefinition(name);
    
    if (def == null)
    {
      throw new ContextException(MessageFormat.format(Cres.get().getString("conFuncNotAvailExt"), name, getPath()));
    }
    
    return def;
  }
  
  @Override
  public void addVariableDefinition(VariableDefinition def)
  {
    variableDataLock.writeLock().lock();

    try
    {
      final String normalizedVariableName = def.getName().toLowerCase(Locale.ENGLISH);
      
      if (getVariableDefinition(def.getName()) != null)
      {
        final VariableData variableData = this.variableData.get(normalizedVariableName);
        variableData.setDefinition(def);
        
        if (variableData.getValue() instanceof DataTable)
        {
          DataTable oldValue = (DataTable) variableData.getValue();
          DataTable resultingValue = new SimpleDataTable(def.getFormat());
          DataTableReplication.copy(oldValue, resultingValue, true, true, true, true, true);
          variableData.setValue(resultingValue);
        }
      }
      else
      {
        variableData.put(normalizedVariableName, new VariableData(def));
      }
    }
    finally
    {
      variableDataLock.writeLock().unlock();
    }
    if (setupComplete && fireUpdateEvents && !def.isHidden())
    {
      fireVariableAdded(def);
    }

    if (getContextManager() != null)
    {
      getContextManager().variableAdded((C) this, def);
    }
  }
  
  protected void fireVariableAdded(VariableDefinition def)
  {
    EventDefinition ed = getEventDefinition(E_VARIABLE_ADDED);
    if (ed != null)
    {
      fireEvent(ed.getName(), new SimpleDataTable(varDefToDataRecord(def, null)));
    }
  }
  
  @Override
  public void removeVariableDefinition(String name)
  {
    removeVariableDefinition(getVariableDefinition(name));
  }
  
  @Override
  public List<VariableDefinition> updateVariableDefinitions(Map<String, VariableDefinition> source, String baseGroup,
      boolean skipRemoval, boolean removeValues, Object owner)
  {

    Map<String, VariableDefinition> definitions = new HashMap<>(source);

    ImmutableList.Builder<VariableDefinition> builder = ImmutableList.builder();

    variableDataLock.readLock().lock();

    try
    {
      for (Iterator<VariableDefinition> iter = definitions.values().iterator(); iter.hasNext();)
      {
        VariableDefinition updated = iter.next();

        VariableDefinition existing = getVariableDefinition(updated.getName());

        if (existing != null && owner != null && owner != existing.getOwner())
        {
          onIllegalDefinitionUpdated(updated.getName());
          iter.remove();
        }
      }
    }
    finally
    {
      variableDataLock.readLock().unlock();
    }

    if (!skipRemoval)
    {

      variableDataLock.writeLock().lock();
      try
      {
        for (VariableDefinition vd : getVariableDefinitions(getContextManager().getCallerController(), baseGroup))
        {
          if (!definitions.containsKey(vd.getName()))
          {
            if (owner == null || owner == vd.getOwner())
            {
              if (removeValues)
              {
                removeValues(vd);
              }
              removeVariableDefinition(vd.getName());
            }
          }
        }
      }
      finally
      {
        variableDataLock.writeLock().unlock();
      }

    }

    variableDataLock.writeLock().lock();

    try
    {
      for (VariableDefinition vd : definitions.values())
      {
        VariableDefinition variableDefinition = getVariableDefinition(vd.getName());

        if (variableDefinition == null || (!variableDefinition.equals(vd) && owner == vd.getOwner()))
        {
          addVariableDefinition(vd);
          builder.add(vd);
        }
      }
    }
    finally
    {
      variableDataLock.writeLock().unlock();
    }

    return builder.build();
  }

  protected void removeValues(VariableDefinition vd)
  {
    throw new AggreGateRuntimeException("Cannot remove variable values since context is not a server context: " + this);
  }

  protected void onIllegalDefinitionUpdated(String definitionName)
  {
    Log.CONTEXT.warn("Illegal entity definition: " + definitionName);
  }

  private void removeVariableDefinition(VariableDefinition def)
  {
    if (def == null)
    {
      return;
    }
    
    VariableData data;
    
    variableDataLock.writeLock().lock();
    try
    {
      data = variableData.remove(def.getName().toLowerCase(Locale.ENGLISH));
      
      if (data == null)
      {
        return;
      }
    }
    finally
    {
      variableDataLock.writeLock().unlock();
    }
    
    data.getReadWriteLock().writeLock().lock();

    try
    {
      variableStatusesLock.writeLock().lock();
      try
      {
        if (variableStatuses != null)
        {
          variableStatuses.remove(def.getName());
          variableStatusesTable = null;
        }
      }
      finally
      {
        variableStatusesLock.writeLock().unlock();
      }
      
      if (setupComplete && fireUpdateEvents && !def.isHidden())
      {
        EventDefinition ed = getEventDefinition(E_VARIABLE_REMOVED);
        if (ed != null)
        {
          fireEvent(ed.getName(), def.getName());
        }
      }
      
      if (getContextManager() != null)
      {
        getContextManager().variableRemoved((C) this, def);
      }
    }
    finally
    {
      data.getReadWriteLock().writeLock().unlock();
    }
  }
  
  @Override
  public void addFunctionDefinition(FunctionDefinition def)
  {

    functionDataLock.writeLock().lock();
    try
    {
      final String normalizedFunctionName = def.getName().toLowerCase(Locale.ENGLISH);
      
      if (getFunctionDefinition(def.getName()) != null)
      {
        functionData.get(normalizedFunctionName).setDefinition(def);
      }
      else
      {
        functionData.put(normalizedFunctionName, new FunctionData(def));
      }
      
      if (setupComplete && fireUpdateEvents && !def.isHidden())
      {
        fireFunctionAdded(def);
      }
      
      if (getContextManager() != null)
      {
        getContextManager().functionAdded((C) this, def);
      }
    }
    finally
    {
      functionDataLock.writeLock().unlock();
    }
  }
  
  protected void fireFunctionAdded(FunctionDefinition def)
  {
    EventDefinition ed = getEventDefinition(E_FUNCTION_ADDED);
    if (ed != null)
    {
      fireEvent(ed.getName(), new SimpleDataTable(funcDefToDataRecord(def, null)));
    }
  }
  
  @Override
  public void removeFunctionDefinition(String name)
  {
    removeFunctionDefinition(getFunctionDefinition(name));
  }
  
  private void removeFunctionDefinition(FunctionDefinition def)
  {
    if (def == null)
    {
      return;
    }
    
    FunctionData data;
    
    functionDataLock.writeLock().lock();
    try
    {
      data = functionData.remove(def.getName().toLowerCase(Locale.ENGLISH));
    }
    finally
    {
      functionDataLock.writeLock().unlock();
    }
    
    data.getExecutionLock().lock();
    try
    {
      if (setupComplete && fireUpdateEvents && !def.isHidden())
      {
        EventDefinition ed = getEventDefinition(E_FUNCTION_REMOVED);
        if (ed != null)
        {
          fireEvent(ed.getName(), def.getName());
        }
      }
      
      if (getContextManager() != null)
      {
        getContextManager().functionRemoved((C) this, def);
      }
    }
    finally
    {
      data.getExecutionLock().unlock();
    }
  }
  
  @Override
  public List<FunctionDefinition> updateFunctionDefinitions(Map<String, Pair<FunctionDefinition, Boolean>> source,
      String baseGroup, boolean skipRemoval, Object owner)
  {

    Map<String, Pair<FunctionDefinition, Boolean>> definitions = new HashMap<>(source);

    ImmutableList.Builder<FunctionDefinition> builder = ImmutableList.builder();

    functionDataLock.writeLock().lock();

    try
    {

      for (FunctionDefinition existing : getFunctionDefinitions(getContextManager().getCallerController(), baseGroup))
      {
        boolean found = false;

        Pair<FunctionDefinition, Boolean> updatedPair = definitions.get(existing.getName());

        if (updatedPair != null)
        {
          FunctionDefinition updated = updatedPair.getFirst();

          if (owner != null && owner != existing.getOwner())
          {
            onIllegalDefinitionUpdated(updated.getName());
            definitions.remove(existing.getName());
            continue;
          }

          found = true;
        }

        if (!found && !skipRemoval)
        {
          if (owner == null || owner == existing.getOwner())
          {
            removeFunctionDefinition(existing.getName());

            ActionDefinition ad = getActionDefinition(existing.getName());
            if (ad != null)
            {
              removeActionDefinition(existing.getName());
            }
          }
        }
      }

      for (Pair<FunctionDefinition, Boolean> entry : definitions.values())
      {
        if (updateFunctionAndAction(entry.getFirst(), entry.getSecond(), owner))
        {
          builder.add(entry.getFirst());
        }
      }

      return builder.build();
    }
    finally
    {
      functionDataLock.writeLock().unlock();
    }
  }

  @Override
  public void addEventDefinition(EventDefinition def)
  {
    eventDataLock.writeLock().lock();
    try
    {
      final String normalizedEventName = def.getName().toLowerCase(Locale.ENGLISH);
      
      final EventDefinition existentDefinition = getEventDefinition(def.getName());
      if (existentDefinition != null)
      {
        eventData.get(normalizedEventName).setDefinition(def);
      }
      else
      {
        eventData.put(normalizedEventName, new EventData(def, this));
      }
      
      if (setupComplete && fireUpdateEvents && !def.isHidden())
      {
        fireEventAdded(def);
      }
      
      if (getContextManager() != null)
      {
        getContextManager().eventAdded((C) this, def);
      }
    }
    finally
    {
      eventDataLock.writeLock().unlock();
    }
  }
  
  protected boolean updateFunctionAndAction(FunctionDefinition fd, boolean processAction, Object owner)
  {
    FunctionDefinition existing = getFunctionDefinition(fd.getName());

    if (existing != null)
    {
      if (Util.equals(fd, existing) || !Objects.equals(existing.getOwner(), owner))
      {
        return false;
      }
    }

    addFunctionDefinition(fd);

    return true;
  }

  @Override
  public List<EventDefinition> updateEventDefinitions(Map<String, EventDefinition> source, String baseGroup, boolean skipRemoval, Object owner)
  {
    ImmutableList.Builder<EventDefinition> builder = ImmutableList.builder();

    Map<String, EventDefinition> definitions = new HashMap<>(source);

    eventDataLock.writeLock().lock();
    try
    {
      for (EventDefinition existing : getEventDefinitions(getContextManager().getCallerController(), baseGroup))
      {
        boolean found = false;

        EventDefinition updated = definitions.get(existing.getName());

        if (updated != null)
        {
          if (owner != null && owner != existing.getOwner())
          {
            onIllegalDefinitionUpdated(updated.getName());
            definitions.remove(existing.getName());
            continue;
          }

          found = true;
        }

        if (!found && !skipRemoval)
        {
          if (owner == null || owner == existing.getOwner())
          {
            removeEventDefinition(existing.getName());
          }
        }
      }

      for (EventDefinition ed : definitions.values())
      {
        if (updateContextEvent(ed, owner))
        {
          builder.add(ed);
        }
      }

      return builder.build();
    }
    finally
    {
      eventDataLock.writeLock().unlock();
    }
  }

  private boolean updateContextEvent(EventDefinition ed, Object owner)
  {
    EventDefinition existing = getEventDefinition(ed.getName());

    if (existing != null)
    {
      if (Util.equals(ed, existing) || !Objects.equals(owner, existing.getOwner()))
      {
        return false;
      }
    }

    addEventDefinition(ed);

    return true;
  }

  protected void fireEventAdded(EventDefinition def)
  {
    EventDefinition ed = getEventDefinition(E_EVENT_ADDED);
    if (ed != null)
    {
      fireEvent(ed.getName(), new SimpleDataTable(evtDefToDataRecord(def, null)));
    }
  }
  
  @Override
  public void removeEventDefinition(String name)
  {
    removeEventDefinition(getEventDefinition(name));
  }
  
  private void removeEventDefinition(EventDefinition def)
  {
    if (def == null)
    {
      return;
    }
    
    eventDataLock.writeLock().lock();
    try
    {
      if (eventData.remove(def.getName().toLowerCase(Locale.ENGLISH)) != null)
      {
        if (setupComplete && fireUpdateEvents && !def.isHidden())
        {
          EventDefinition ed = getEventDefinition(E_EVENT_REMOVED);
          if (ed != null)
          {
            fireEvent(ed.getName(), def.getName());
          }
        }
        
        if (getContextManager() != null)
        {
          getContextManager().eventRemoved((C) this, def);
        }
      }
    }
    finally
    {
      eventDataLock.writeLock().unlock();
    }
  }
  
  @Override
  public VariableData getVariableData(String name)
  {
    variableDataLock.readLock().lock();
    try
    {
      return getData(variableData, variableNameByAlias, name);
    }
    finally
    {
      variableDataLock.readLock().unlock();
    }
  }

  @Override
  public void addAlias(int entityType, String aliasName, String name)
  {
    String normalizedName = aliasName.toLowerCase(Locale.ENGLISH);
    switch (entityType)
    {
      case ContextUtils.ENTITY_VARIABLE:
        variableNameByAlias.put(normalizedName, name);
        break;
      case ContextUtils.ENTITY_FUNCTION:
        functionNameByAlias.put(normalizedName, name);
        break;
      case ContextUtils.ENTITY_EVENT:
        eventNameByAlias.put(normalizedName, name);
        break;
    }
  }
  
  @Override
  public VariableDefinition getVariableDefinition(String name)
  {
    VariableData data = getVariableData(name);
    return data != null ? data.getDefinition() : null;
  }
  
  @Override
  public VariableDefinition getVariableDefinition(String name, CallerController caller)
  {
    VariableDefinition def = getVariableDefinition(name);
    
    if (def == null)
    {
      return null;
    }
    
    Permissions readPermissions = def.getReadPermissions() != null ? def.getReadPermissions() : getPermissions();
    Permissions writePermissions = def.getWritePermissions() != null ? def.getWritePermissions() : getPermissions();
    
    writePermissions = new Permissions(writePermissions.getPermissions(), true);
    
    boolean readAccessGranted = checkPermissions(readPermissions, caller, this, def);
    boolean writeAccessGranted = checkPermissions(writePermissions, caller, this, def);
    
    return (readAccessGranted || writeAccessGranted) ? def : null;
  }
  
  @Override
  public FunctionData getFunctionData(String name)
  {
    functionDataLock.readLock().lock();
    try
    {
      return getData(functionData, functionNameByAlias, name);
    }
    finally
    {
      functionDataLock.readLock().unlock();
    }
  }
  
  @Override
  public FunctionDefinition getFunctionDefinition(String name)
  {
    FunctionData data = getFunctionData(name);
    return data != null ? data.getDefinition() : null;
  }
  
  @Override
  public FunctionDefinition getFunctionDefinition(String name, CallerController caller)
  {
    FunctionDefinition def = getFunctionDefinition(name);
    
    if (def == null)
    {
      return null;
    }
    
    Permissions permissions = def.getPermissions() != null ? def.getPermissions() : getPermissions();
    
    boolean accessGranted = checkPermissions(permissions, caller, this, def);
    
    return accessGranted ? def : null;
  }
  
  @Override
  public EventData getEventData(String name)
  {
    eventDataLock.readLock().lock();
    try
    {
      return getData(eventData, eventNameByAlias, name);
    }
    finally
    {
      eventDataLock.readLock().unlock();
    }
  }
  
  @Override
  public EventDefinition getEventDefinition(String name)
  {
    EventData ed = getEventData(name);
    return ed != null ? ed.getDefinition() : null;
  }
  
  private <T> T getData(Map<String, T> dataByName, Map<String, String> aliasMap, String name)
  {
    if (dataByName.containsKey(name.toLowerCase(Locale.ENGLISH)))
    {
      return dataByName.get(name.toLowerCase(Locale.ENGLISH));
    }
    String alias = aliasMap.get(name.toLowerCase(Locale.ENGLISH));
    return alias != null ? dataByName.get(alias.toLowerCase(Locale.ENGLISH)) : null;
  }

  @Override
  public EventDefinition getEventDefinition(String name, CallerController caller)
  {
    EventDefinition def = getEventDefinition(name);
    
    if (def == null)
    {
      return null;
    }
    
    Permissions permissions = def.getPermissions() != null ? def.getPermissions() : getPermissions();
    
    boolean accessGranted = checkPermissions(permissions, caller, this, def);
    
    return accessGranted ? def : null;
  }
  
  private EventDefinition getAndCheckEventDefinition(String name)
  {
    setupEvents();
    
    EventDefinition def = getEventDefinition(name);
    
    if (def == null)
    {
      throw new ContextRuntimeException(MessageFormat.format(Cres.get().getString("conEvtNotAvailExt"), name, getPath()));
    }
    
    return def;
  }
  
  protected void setupEvents()
  {
  }
  
  protected void postEvent(Event ev, EventDefinition ed, CallerController caller, FireEventRequestController request) throws ContextException
  {
  }
  
  protected void updateEvent(Event ev, EventDefinition ed, CallerController caller, FireEventRequestController request) throws ContextException
  {
  }
  
  protected Event fireEvent(EventDefinition ed, DataTable data, int level, Long id, Date creationtime, Integer listener, CallerController caller, FireEventRequestController request,
      Permissions permissions)
  {
    if (id == null)
    {
      id = EventUtils.generateEventId();
    }
    
    Event event = new Event(getPath(), ed, level == DEFAULT_EVENT_LEVEL ? ed.getLevel() : level, data, id, creationtime, permissions);
    
    if (request != null && request.getServerId() != null)
    {
      event.setServerID(request.getServerId());
    }

    return fireEvent(ed, event, listener, caller, request);
  }
  
  protected Event fireEvent(Event event)
  {
    return fireEvent(getAndCheckEventDefinition(event.getName()), event, null, null, null);
  }
  
  @Override
  public Event fireEvent(String name, FireEventRequestController request, Object... data)
  {
    EventDefinition ed = getAndCheckEventDefinition(name);
    return fireEvent(ed, new SimpleDataTable(ed.getFormat(), data), DEFAULT_EVENT_LEVEL, null, null, null, null, request, null);

  }

  protected Event fireEvent(EventDefinition ed, Event event, Integer listener, CallerController caller, FireEventRequestController request)
  {
    Logger logger = Log.CONTEXT_EVENTS;
    
    if (caller != null)
    {
      try
      {
        checkPermissions(ed.getFirePermissions() != null ? ed.getFirePermissions() : getPermissions(), caller, ed);
      }
      catch (ContextSecurityException ex)
      {
        throw new ContextRuntimeException(ex);
      }
    }
    
    EventProcessingRule rule = getEventProcessingRule(event);
    
    Expression prefilter = rule != null ? rule.getPrefilterExpression() : null;
    if (prefilter != null)
    {
      try
      {
        Evaluator evaluator = new Evaluator(getContextManager(), this, event.getData(), getEventProcessingCallerController());
        
        if (!evaluator.evaluateToBoolean(prefilter))
        {
          rule.addFiltered();
          
          if (logger.isDebugEnabled())
          {
            logger.debug("Event '" + ed + "' in context '" + getPath() + "' was suppressed by pre-filter");
          }
          
          return null;
        }
      }
      catch (Exception ex)
      {
        logger.info("Error processing pre-filter expression for event '" + ed + "' in context '" + getPath() + "': " + ex.getMessage(), ex);
      }
    }
    
    if (logger.isDebugEnabled())
    {
      logger.debug("Event fired: " + event);
    }
    
    event.setListener(listener);
    
    event.setSessionID(caller != null ? caller.getSessionIdCounter() : null);
    
    if (request != null)
    {
      event.setOriginator(request.getOriginator().getOriginatorObject());
    }
    
    EventData edata = getEventData(ed.getName());
    
    if (edata == null)
    {
      return null;
    }
    
    edata.registerFiredEvent();
    
    Expression deduplicator = rule != null ? rule.getDeduplicatorExpression() : null;
    if (deduplicator != null)
    {
      try
      {
        Evaluator evaluator = new Evaluator(getContextManager(), this, event.getData(), getEventProcessingCallerController());
        
        String deduplicationId = evaluator.evaluateToString(deduplicator);
        
        event.setDeduplicationId(deduplicationId);
      }
      catch (Exception ex)
      {
        logger.info("Error processing deduplicator expression for event '" + ed + "' in context '" + getPath() + "': " + ex.getMessage(), ex);
      }
    }
    
    if (event.getData().isInvalid())
    {
      throw new ContextRuntimeException(event.getData().getInvalidationMessage());
    }
    
    if (ed.getFormat() != null)
    {
      String msg = event.getData().conformMessage(ed.getFormat());
      if (msg != null)
      {
        logger.debug("Wrong format data for event '" + ed + "' in context '" + this + "': " + msg);
        DataTable newData = new SimpleDataTable(ed.getFormat(), true);
        DataTableReplication.copy(event.getData(), newData);
        event.setData(newData);
      }
    }
    
    processBindings(event);
    
    processEnrichments(event, rule, caller);
    
    Long customExpirationPeriod = null;
    
    if (request != null && request.getCustomExpirationPeriod() != null)
    {
      customExpirationPeriod = request.getCustomExpirationPeriod();
    }
    
    if (customExpirationPeriod != null)
    {
      if (customExpirationPeriod > 0)
      {
        event.setExpirationtime(new Date(System.currentTimeMillis() + customExpirationPeriod));
      }
      // Otherwise event won't be persisted
    }
    else
    {
      Long userDefinedExpirationPeriod = rule != null ? rule.getPeriod() : null;
      if (userDefinedExpirationPeriod != null && userDefinedExpirationPeriod > 0)
      {
        event.setExpirationtime(new Date(System.currentTimeMillis() + userDefinedExpirationPeriod));
      }
      // Otherwise event won't be persisted
    }
    
    final Integer customMemoryStorageSize = rule != null ? ((rule.getDeduplicator() != null && rule.getDeduplicator().length() > 0) ? rule.getQueue() : null) : null;
    
    Event processed = request != null ? request.process(event) : event;
    
    if (processed == null)
    {
      return null;
    }
    
    Event duplicate;
    
    edata.getDuplicateProcessingLock().readLock().lock();
    try
    {
      duplicate = edata.store(processed, customMemoryStorageSize);
      
      if (duplicate == null)
      {
        postEvent(event, ed, caller, request);
        
        if (rule != null)
        {
          rule.addSaved();
        }
      }
      else
      {
        edata.getDuplicateProcessingLock().readLock().unlock();
        edata.getDuplicateProcessingLock().writeLock().lock();
        try
        {
          updateEvent(duplicate, ed, caller, request);
          
          if (rule != null)
          {
            rule.addDuplicate();
          }
        }
        finally
        {
          edata.getDuplicateProcessingLock().writeLock().unlock();
          edata.getDuplicateProcessingLock().readLock().lock();
        }
      }
    }
    catch (ContextException ex)
    {
      throw new ContextRuntimeException(ex);
    }
    finally
    {
      edata.getDuplicateProcessingLock().readLock().unlock();
    }

    boolean hasSpan = beginSpan(request, ed);
    try
    {
      if (contextManager != null && (duplicate == null || rule == null || rule.isDuplicateDispatching()))
      {
        if (hasSpan)
        {
          traceOperation(FIRE_EVENT);
        }
        contextManager.queue(edata, event, request);
      }
    }
    finally
    {
      if (hasSpan)
      {
        endSpan();
      }
    }

    return duplicate != null ? duplicate : event;
  }
  
  public Event fireEvent(String name, int level, CallerController caller, FireEventRequestController request, Permissions permissions, DataTable data)
  {
    EventDefinition ed = getAndCheckEventDefinition(name);
    return fireEvent(ed, data, level, null, null, null, caller, request, permissions);
  }
  
  @Override
  public Event fireEvent(String name, DataTable data, int level, Long id, Date creationtime, Integer listener, CallerController caller, FireEventRequestController request)
  {
    return fireEvent(getAndCheckEventDefinition(name), data, level, id, creationtime, listener, caller, request, null);
  }
  
  @Override
  public Event fireEvent(String name, DataTable data)
  {
    return fireEvent(getAndCheckEventDefinition(name), data, DEFAULT_EVENT_LEVEL, null, null, null, null, null, null);
  }
  
  @Override
  public Event fireEvent(String name, CallerController caller, DataTable data)
  {
    return fireEvent(getAndCheckEventDefinition(name), data, DEFAULT_EVENT_LEVEL, null, null, null, caller, null, null);
  }
  
  @Override
  public Event fireEvent(String name, int level, DataTable data)
  {
    return fireEvent(getAndCheckEventDefinition(name), data, level, null, null, null, null, null, null);
  }
  
  @Override
  public Event fireEvent(String name, int level, CallerController caller, DataTable data)
  {
    return fireEvent(getAndCheckEventDefinition(name), data, level, null, null, null, caller, null, null);
  }
  
  @Override
  public Event fireEvent(String name, int level, CallerController caller, FireEventRequestController request, DataTable data)
  {
    return fireEvent(getAndCheckEventDefinition(name), data, level, null, null, null, caller, request, null);
  }

  @Override
  public Event fireEvent(String name)
  {
    EventDefinition ed = getAndCheckEventDefinition(name);
    return fireEvent(ed, new SimpleDataTable(ed.getFormat(), true), DEFAULT_EVENT_LEVEL, null, null, null, null, null, null);
  }
  
  @Override
  public Event fireEvent(String name, CallerController caller)
  {
    EventDefinition ed = getAndCheckEventDefinition(name);
    return fireEvent(ed, new SimpleDataTable(ed.getFormat(), true), DEFAULT_EVENT_LEVEL, null, null, null, caller, null, null);
  }
  
  @Override
  public Event fireEvent(String name, Object... data)
  {
    EventDefinition ed = getAndCheckEventDefinition(name);
    return fireEvent(ed, new SimpleDataTable(ed.getFormat(), data), DEFAULT_EVENT_LEVEL, null, null, null, null, null, null);
  }
  
  protected EventProcessingRule getEventProcessingRule(Event event)
  {
    return null;
  }
  
  protected void processBindings(Event event)
  {
  }
  
  private void processEnrichments(Event event, EventProcessingRule rule, CallerController caller)
  {
    if (rule == null || rule.getEnrichments() == null)
    {
      return;
    }
    
    Evaluator evaluator = new Evaluator(getContextManager(), this, event.getData(), getEventProcessingCallerController());
    
    for (EventEnrichmentRule enrichmentRule : rule.getEnrichments())
    {
      String name = enrichmentRule.getName();
      try
      {
        Object result = evaluator.evaluateToString(enrichmentRule.getEnrichmentExpression());
        
        if (result == null)
        {
          continue;
        }
        
        event.addEnrichment(new Enrichment(name, result.toString(), new Date(), caller != null ? caller.getUsername() : null));
      }
      catch (Exception ex)
      {
        Log.CONTEXT_EVENTS.error("Error adding enrichment '" + name + "' to event '" + event + "': " + ex);
      }
    }
  }
  
  protected CallerController getEventProcessingCallerController()
  {
    return getContextManager().getCallerController();
  }
  
  @Override
  public List<Event> getEventHistory(String name)
  {
    EventData ed = getEventData(name);
    
    if (ed == null)
    {
      throw new IllegalStateException(Cres.get().getString("conEvtNotAvail") + name);
    }
    
    return ed.getHistory();
  }
  
  private void lock(RequestController request, Lock lock) throws ContextException
  {
    Long lockTimeout = (request != null && request.getLockTimeout() != null) ? request.getLockTimeout() : null;
    
    if (lockTimeout != null)
    {
      try
      {
        if (!lock.tryLock(lockTimeout, java.util.concurrent.TimeUnit.MILLISECONDS))
        {
          throw new ContextException(Cres.get().getString("conLockFailed"));
        }
      }
      catch (InterruptedException ex)
      {
        throw new ContextException(Cres.get().getString("interrupted"));
      }
    }
    else
    {
      lock.lock();
    }
  }
  
  @Override
  public String toString()
  {
    String desc = getDescription();
    return desc != null ? desc : getPath();
  }
  
  @Override
  public String toDetailedString()
  {
    String description = getDescription();
    return description != null ? description + " (" + getPath() + ")" : getPath();
  }
  
  @Override
  public void accept(ContextVisitor visitor) throws ContextException
  {
    if (visitor.shouldVisit(this))
    {
      childrenLock.writeLock().lock();
      try
      {
        if (visitor.isConcurrent()) // should visit in parallel
        {
          if (visitor.isCurrentThenChildrenOrder()) // first visit, then accept
          {
            visitInParallel(visitor, this);
            traverseChildren(visitor, childrenList);
          }
          else // first accept, then visit
          {
            traverseChildren(visitor, childrenList);
            visitInParallel(visitor, this);
          }
        }
        else // should visit sequentially
        {
          if (visitor.isCurrentThenChildrenOrder()) // first visit, then accept
          {
            visitor.visit(this);
            traverseChildren(visitor, childrenList);
          }
          else // first accept, then visit
          {
            traverseChildren(visitor, childrenList);
            visitor.visit(this);
          }
        }
      }
      finally
      {
        childrenLock.writeLock().unlock();
      }
    }
  }

  private void traverseChildren(ContextVisitor visitor, List<C> childrenList) throws ContextException
  {
    for (Context child : childrenList)
    {
      child.accept(visitor);
    }
  }

  private void visitInParallel(ContextVisitor visitor, Context currentContext) throws ContextException
  {
    ExecutorService executorService = getContextManager().getExecutorService();

    int taskPortionSize = EXECUTOR_THREADS_PERCENT_FOR_VISITORS;

    if (executorService instanceof ThreadPoolExecutor)
    {
      ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
      taskPortionSize = executor.getMaximumPoolSize() / EXECUTOR_THREADS_PERCENT_FOR_VISITORS;
      taskPortionSize = taskPortionSize == 0 ? 1 : taskPortionSize;
    }

    Callable task = () -> {
      visitor.visit(currentContext);
      return null;
    };

    final boolean root = visitor.isStartContext();
    try
    {
      visitor.getTasks().add(task);
      if (visitor.getTasks().size() >= taskPortionSize || root)
      {
        executorService.invokeAll(visitor.getTasks());
        visitor.getTasks().clear();
      }
    }
    catch (InterruptedException ex)
    {
      throw new ContextException(ex.getMessage(), ex);
    }
  }
  
  protected EventDefinition getChangeEventDefinition()
  {
    return ED_CHANGE;
  }
  
  public DataTable getVvariables(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    DataTable ans = new SimpleDataTable(def.getFormat());
    for (VariableDefinition vardef : getVariableDefinitions(caller))
    {
      ans.addRecord(varDefToDataRecord(vardef, caller));
    }
    return ans;
  }
  
  protected synchronized String encodeFormat(TableFormat format, CallerController caller)
  {
    return format != null ? format.encode(false) : null;
  }
  
  protected synchronized TableFormat decodeFormat(String source, CallerController caller)
  {
    return source != null ? new TableFormat(source, new ClassicEncodingSettings(false)) : null;
  }
  
  /**
   * @return format cache optional taken from current {@link #contextManager}
   * @implNote The method is intended to be overridden in contexts where the format is obtained by other means
   */
  protected Optional<FormatCache> obtainFormatCache()
  {
    return (contextManager != null)
        ? contextManager.getFormatCache()
        : Optional.empty();
  }

  public DataRecord varDefToDataRecord(VariableDefinition vd)
  {
    return varDefToDataRecord(vd, null);
  }
  
  protected DataRecord varDefToDataRecord(VariableDefinition vd, CallerController caller)
  {
    DataRecord rec = new DataRecord(VARIABLE_DEFINITION_FORMAT);
    
    rec.setValue(FIELD_VD_NAME, vd.getName());
    rec.setValue(FIELD_VD_FORMAT, encodeFormat(vd.getFormat(), caller));
    rec.setValue(FIELD_VD_DESCRIPTION, vd.getDescription());
    rec.setValue(FIELD_VD_READABLE, vd.isReadable());
    rec.setValue(FIELD_VD_WRITABLE, vd.isWritable());
    rec.setValue(FIELD_VD_HELP, vd.getHelp());
    rec.setValue(FIELD_VD_GROUP, vd.getGroup());
    rec.setValue(FIELD_VD_ICON_ID, vd.getIconId());
    rec.setValue(FIELD_VD_HELP_ID, vd.getHelpId());
    rec.setValue(FIELD_VD_CACHE_TIME, vd.getRemoteCacheTime());
    rec.setValue(FIELD_VD_ADD_PREVIOUS_VALUE_TO_VARIABLE_UPDATE_EVENT, vd.isAddPreviousValueToVariableUpdateEvent());
    if (vd.getReadPermissions() != null)
      rec.setValue(FIELD_VD_READ_PERMISSIONS, vd.getReadPermissions().encode());
    if (vd.getWritePermissions() != null)
      rec.setValue(FIELD_VD_WRITE_PERMISSIONS, vd.getWritePermissions().encode());

    return rec;
  }
  
  public VariableDefinition varDefFromDataRecord(DataRecord rec)
  {
    return varDefFromDataRecord(rec, null);
  }
  
  private VariableDefinition varDefFromDataRecord(DataRecord rec, CallerController caller)
  {
    
    final String variable = rec.getString(FIELD_VD_NAME);
    
    boolean readable = rec.getBoolean(FIELD_VD_READABLE);
    
    boolean writable = rec.getBoolean(FIELD_VD_WRITABLE);
    
    TableFormat format;
    try
    {
      format = decodeFormat(rec.getString(FIELD_VD_FORMAT), caller);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error decoding format of variable '" + variable + "': " + ex.getMessage(), ex);
    }
    
    VariableDefinition def = new VariableDefinition(variable, format, readable, writable, rec.getString(FIELD_VD_DESCRIPTION), rec.getString(FIELD_VD_GROUP));
    
    def.setHelp(rec.getString(FIELD_VD_HELP));
    def.setIconId(rec.getString(FIELD_VD_ICON_ID));
    
    if (rec.hasField(FIELD_VD_HELP_ID))
    {
      def.setHelpId(rec.getString(FIELD_VD_HELP_ID));
    }
    
    if (rec.hasField(FIELD_VD_CACHE_TIME))
    {
      def.setRemoteCacheTime(rec.getLong(FIELD_VD_CACHE_TIME));
    }
    
    if (rec.hasField(FIELD_VD_ADD_PREVIOUS_VALUE_TO_VARIABLE_UPDATE_EVENT))
    {
      def.setAddPreviousValueToVariableUpdateEvent(rec.getBoolean(FIELD_VD_ADD_PREVIOUS_VALUE_TO_VARIABLE_UPDATE_EVENT));
    }

    if (rec.hasField(FIELD_VD_READ_PERMISSIONS))
    {
      def.setReadPermissions(new Permissions(rec.getString(FIELD_VD_READ_PERMISSIONS)));
    }

    if (rec.hasField(FIELD_VD_WRITE_PERMISSIONS))
    {
      def.setWritePermissions(new Permissions(rec.getString(FIELD_VD_WRITE_PERMISSIONS)));
    }

    return def;
  }
  
  public DataTable getVfunctions(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    DataTable ans = new SimpleDataTable(def.getFormat());
    for (FunctionDefinition funcdef : getFunctionDefinitions(caller))
    {
      ans.addRecord(funcDefToDataRecord(funcdef, caller));
    }
    return ans;
  }
  
  public DataRecord funcDefToDataRecord(FunctionDefinition fd)
  {
    return funcDefToDataRecord(fd, null);
  }
  
  protected DataRecord funcDefToDataRecord(FunctionDefinition fd, CallerController caller)
  {
    DataRecord rec = new DataRecord(FUNCTION_DEFINITION_FORMAT);
    rec.setValue(FIELD_FD_NAME, fd.getName());
    rec.setValue(FIELD_FD_INPUTFORMAT, encodeFormat(fd.getInputFormat(), caller));
    rec.setValue(FIELD_FD_OUTPUTFORMAT, encodeFormat(fd.getOutputFormat(), caller));
    rec.setValue(FIELD_FD_DESCRIPTION, fd.getDescription());
    rec.setValue(FIELD_FD_HELP, fd.getHelp());
    rec.setValue(FIELD_FD_GROUP, fd.getGroup());
    rec.setValue(FIELD_FD_ICON_ID, fd.getIconId());
    rec.setValue(FIELD_FD_CONCURRENT, fd.isConcurrent());
    if (fd.getPermissions() != null)
      rec.setValue(FIELD_FD_PERMISSIONS, fd.getPermissions().encode());
    
    return rec;
  }
  
  public FunctionDefinition funcDefFromDataRecord(DataRecord rec)
  {
    return funcDefFromDataRecord(rec, null);
  }
  
  private FunctionDefinition funcDefFromDataRecord(DataRecord rec, CallerController caller)
  {
    final String function = rec.getString(FIELD_FD_NAME);
    
    TableFormat inputFormat;
    try
    {
      inputFormat = decodeFormat(rec.getString(FIELD_FD_INPUTFORMAT), caller);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error decoding input format of function '" + function + "': " + ex.getMessage(), ex);
    }
    
    TableFormat outputFormat;
    try
    {
      outputFormat = decodeFormat(rec.getString(FIELD_FD_OUTPUTFORMAT), caller);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error decoding output format of function '" + function + "': " + ex.getMessage(), ex);
    }
    
    FunctionDefinition def = new FunctionDefinition(function, inputFormat, outputFormat, rec.getString(FIELD_FD_DESCRIPTION), rec.getString(FIELD_FD_GROUP));
    
    def.setHelp(rec.getString(FIELD_FD_HELP));
    def.setIconId(rec.getString(FIELD_FD_ICON_ID));
    
    if (rec.hasField(FIELD_FD_CONCURRENT) && rec.getBoolean(FIELD_FD_CONCURRENT) != null)
      def.setConcurrent(rec.getBoolean(FIELD_FD_CONCURRENT));
    
    if (rec.hasField(FIELD_FD_PERMISSIONS) && rec.getString(FIELD_FD_PERMISSIONS) != null)
      def.setPermissions(new Permissions(rec.getString(FIELD_FD_PERMISSIONS)));
    
    return def;
  }
  
  public DataTable getVevents(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    DataTable ans = new SimpleDataTable(def.getFormat());
    for (EventDefinition ed : getEventDefinitions(caller))
    {
      ans.addRecord(evtDefToDataRecord(ed, caller));
    }
    return ans;
  }
  
  public DataRecord evtDefToDataRecord(EventDefinition ed)
  {
    return evtDefToDataRecord(ed, null);
  }
  
  protected DataRecord evtDefToDataRecord(EventDefinition ed, CallerController caller)
  {
    DataRecord rec = new DataRecord(EVENT_DEFINITION_FORMAT)
        .addString(ed.getName())
        .addString(encodeFormat(ed.getFormat(), caller))
        .addString(ed.getDescription())
        .addString(ed.getHelp())
        .addInt(ed.getLevel())
        .addString(ed.getGroup())
        .addString(ed.getIconId());
    if (ed.getPermissions() != null)
    {
      rec.addString(ed.getPermissions().encode());
    }
    return rec;
  }
  
  public EventDefinition evtDefFromDataRecord(DataRecord rec)
  {
    return evtDefFromDataRecord(rec, null);
  }
  
  private EventDefinition evtDefFromDataRecord(DataRecord rec, CallerController caller)
  {
    final String event = rec.getString(FIELD_ED_NAME);
    
    TableFormat format;
    try
    {
      format = decodeFormat(rec.getString(FIELD_ED_FORMAT), caller);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error decoding format of event '" + event + "': " + ex.getMessage(), ex);
    }
    
    EventDefinition def = new EventDefinition(event, format, rec.getString(FIELD_ED_DESCRIPTION), rec.getString(FIELD_ED_GROUP));
    
    def.setLevel(rec.getInt(FIELD_ED_LEVEL));
    def.setHelp(rec.getString(FIELD_ED_HELP));
    def.setIconId(rec.getString(FIELD_ED_ICON_ID));
    if (rec.hasField(FIELD_ED_PERMISSIONS))
    {
      def.setPermissions(new Permissions(rec.getString(FIELD_ED_PERMISSIONS)));
    }
    return def;
  }
  
  public DataTable getVactions(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    DataTable ans = new SimpleDataTable(def.getFormat());
    for (ActionDefinition adef : getActionDefinitions(caller))
    {
      ans.addRecord(actDefToDataRecord(adef));
    }
    
    return ans;
  }
  
  public DataRecord actDefToDataRecord(ActionDefinition def)
  {
    DataTable resourceMasks = new SimpleDataTable(AbstractContext.RESOURCE_MASKS_FORMAT);
    if (def.getDropSources() != null)
    {
      for (ResourceMask resourceMask : def.getDropSources())
      {
        resourceMasks.addRecord().addString(resourceMask.toString());
      }
    }
    
    DataRecord rec = new DataRecord(AbstractContext.ACTION_DEF_FORMAT);
    rec.addString(def.getName());
    rec.addString(def.getDescription());
    rec.addString(def.getHelp());
    rec.addString(def.getAccelerator() == null ? null : def.getAccelerator().toString());
    rec.addDataTable(resourceMasks);
    rec.addBoolean(def.isHidden());
    rec.addBoolean(def.isEnabled());
    rec.addString(def.getIconId());
    rec.addString(def.getGroup());
    rec.addString(def.getExecutionGroup() == null ? null : def.getExecutionGroup().toString());
    rec.addBoolean(def.isDefault());
    if (def.getPermissions() != null)
      rec.addString(def.getPermissions().encode());
    return rec;
  }
  
  protected void executeTasks(List<Callable<Object>> tasks)
  {
    // The following is the minimum reasonable load factor as any lesser value would make tasks execute synchronously
    final double minimalLoadFactor = 0.7;
    executeTasks(tasks, minimalLoadFactor);
  }

  protected void executeTasks(List<Callable<Object>> tasks, double loadFactor)
  {
    if (tasks.isEmpty())
    {
      return;
    }
    try
    {
      if (isChildrenConcurrencyEnabled())
      {
        executeTasksConcurrently(tasks, loadFactor);
      }
      else
      {
        for (Callable task : tasks)
        {
          task.call();
        }
      }
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  /**
   * Tries to utilize underlying {@code ContextOperationExecutor} as much as possible but without occupying 100% of
   * its threads. This is important to prevent deadlocks which may arise if some tasks being executed need to wait
   * for other children tasks that cannot be finished due to pool overwhelming. The method prevents it by always
   * leaving some part of the threads free (depending on their number and {@code loadFactor} value) and executing
   * separate tasks (not bunches) right in the current thread.
   *
   * @param tasks
   *          a list of tasks to be executed
   * @param loadFactor
   *          a ratio of threads that can be utilized by this invocation, should be between 0.1 and 0.9
   * @throws Exception
   *           when concurrent execution is interrupted or single task invocation fails
   */
  protected void executeTasksConcurrently(List<Callable<Object>> tasks, double loadFactor) throws Exception
  {
    if (loadFactor < 0.1)
    {
      Log.CONTEXT.warn(format("Context tasks load factor is too low (%f), will be changed to 0.1", loadFactor));
      loadFactor = 0.1;
    }
    if (loadFactor > 0.9)
    {
      Log.CONTEXT.warn(format("Context tasks load factor is too high (%f), will be changed to 0.9", loadFactor));
      loadFactor = 0.9;
    }

    ThreadPoolExecutor executor = (ThreadPoolExecutor) getContextManager().getExecutorService();
    int taskCount = tasks.size();
    int maxWorkers = executor.getMaximumPoolSize();
    int lowerIndex = 0;
    List<Future<Object>> allTaskFutures = new ArrayList<>(taskCount);

    do
    {
      int curFreeWorkers = maxWorkers - executor.getActiveCount();
      int numOfTasksToSubmit = (int) (loadFactor * curFreeWorkers);

      if (numOfTasksToSubmit > 0)
      {
        int upperIndex = Math.min((lowerIndex + numOfTasksToSubmit), tasks.size());
        // lowerIndex is inclusive, upperIndex is exclusive
        List<Callable<Object>> tasksToSubmit = tasks.subList(lowerIndex, upperIndex);

        if (tasksToSubmit.size() != taskCount)
        {
          Log.CONTEXT.debug("Executing only " + tasksToSubmit.size() + " out of " + taskCount + " tasks concurrently " +
                  "due to load factor limit " + loadFactor);
        }

        List<Future<Object>> bunchFutures = tasksToSubmit.stream()
                .map(executor::submit)
                .collect(toList());
        allTaskFutures.addAll(bunchFutures);
        lowerIndex = upperIndex;
      }
      else // this may happen if all the workers are busy or loadFactor is too low
      {
        Callable<Object> currentTask = tasks.get(lowerIndex);
        Log.CONTEXT.debug(format("Due to loadFactor=%f and %d free workers, the task '%s' is going to be " +
            "executed synchronously", loadFactor, curFreeWorkers, currentTask));
        
        try
        {
          currentTask.call();
        }
        catch (Exception e)    // a single task must not prevent others from acting
        {
          Log.CONTEXT.warn("One of synchronously executed tasks failed", e);
        }
        
        lowerIndex++;
      }
    }
    while (lowerIndex < tasks.size());

    // now that we've submitted all the tasks (and possibly run some of them synchronously), we should wait for them to finish
    long startTime = System.currentTimeMillis();
    int waitTasksCount = 0;
    for (Future<Object> taskFuture : allTaskFutures) {
      if (taskFuture.isDone()) {
        continue;
      }

      try
      {
        taskFuture.get();
      }
      catch (ExecutionException e)    // a single task must not prevent others from acting
      {
        Log.CONTEXT.warn("One of concurrently executed tasks failed", e);
      }

      waitTasksCount++;
    }
    if (waitTasksCount > 0 && Log.CONTEXT.isDebugEnabled()) {
      Log.CONTEXT.debug(format("Waiting on %d unfinished tasks took %d ms", waitTasksCount, (System.currentTimeMillis()-startTime)));
    }
  }

  protected void enableStatus() throws ContextException
  {
    status = new ContextStatus();
  }
  
  @Override
  public ContextStatus getStatus()
  {
    return status;
  }
  
  public void setStatus(int status, String comment)
  {
    if (this.status == null)
      throw new IllegalStateException("Status is disabled");
    boolean statusChanged = this.status.getStatus() != status;
    boolean commentChanged = !Util.equals(this.status.getComment(), comment);
    
    int oldStatus = this.status.getStatus();
    
    this.status.setStatus(status);
    this.status.setComment(comment);
    
    if (statusChanged || commentChanged)
    {
      fireStatusChanged(status, comment, oldStatus);
    }
  }
  
  protected void fireStatusChanged(int status, String comment, int oldStatus)
  {
  }
  
  protected void enableVariableStatuses(boolean persistent)
  {
    VariableDefinition vd = new VariableDefinition(V_VARIABLE_STATUSES, VFT_VARIABLE_STATUSES, true, true);
    vd.setPersistent(persistent);
    vd.setLocalCachingMode(VariableDefinition.CACHING_NONE);
    vd.setGetter(new VariableGetter()
    {
      @Override
      public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
      {
        return getVariableStatusesTable();
      }
    });
    
    addVariableDefinition(vd);
    addEventDefinition(new EventDefinition(E_VARIABLE_STATUS_CHANGED, VFT_VARIABLE_STATUSES));

    FunctionDefinition fd = new FunctionDefinition(F_GET_VARIABLE_STATUS, FIFT_GET_VARIABLE_STATUS, VFT_VARIABLE_STATUSES, Cres.get().getString("conGetVariableStatus"));
    fd.setPermissions(getPermissions());
    fd.setImplementation(getVariableStatusImpl);

    addFunctionDefinition(fd);
  }
  
  private DataTable getVariableStatusesTable() throws ContextException
  {
    if (variableStatusesTable == null)
      return createVariableStatusesTable();
    
    return variableStatusesTable;
  }
  
  private DataTable createVariableStatusesTable() throws ContextException
  {
    variableStatusesLock.readLock().lock();
    try
    {
      DataTable table = new SimpleDataTable(VFT_VARIABLE_STATUSES);
      Map<String, VariableStatus> statuses = getVariableStatuses();
      
      for (String name : statuses.keySet())
      {
        VariableStatus vs = statuses.get(name);
        table.addRecord().addString(name).addString(vs.getStatus()).addString(vs.getComment());
      }
      
      variableStatusesTable = table;
      return table;
    }
    finally
    {
      variableStatusesLock.readLock().unlock();
    }
  }
  
  private Map<String, VariableStatus> getVariableStatuses() throws ContextException
  {
    ensureVariableStatuses();
    
    return unmodifiableMap(variableStatuses);
  }
  
  protected void ensureVariableStatuses() throws ContextException
  {
    if (variableStatuses == null)
    {
      variableStatuses = Collections.synchronizedMap(new LinkedHashMap());
      
      DataTable statuses = fetchVariableStatuses();
      
      for (DataRecord rec : statuses)
      {
        variableStatuses.put(rec.getString(VF_VARIABLE_STATUSES_NAME), VariableStatus.ofDataRecord(rec));
      }
    }
  }

  protected boolean isBelongedToTheGroup(String targetGroup, String entityGroup)
  {
    return entityGroup != null && (Util.equals(targetGroup, entityGroup) || entityGroup.startsWith(targetGroup + ContextUtils.ENTITY_GROUP_SEPARATOR));
  }

  protected DataTable fetchVariableStatuses() throws ContextException
  {
    return new SimpleDataTable(VFT_VARIABLE_STATUSES);
  }
  
  public void updateVariableStatus(String variable, VariableStatus status, boolean persistent) throws ContextException
  {
    VariableStatus old;
    boolean changed;
    
    variableStatusesLock.writeLock().lock();
    try
    {
      ensureVariableStatuses();
      
      old = variableStatuses.put(variable, status);
      
      changed = (old == null || !Util.equals(old.getStatus(), status.getStatus()));
      
      if (changed)
        variableStatusesTable = null;
    }
    finally
    {
      variableStatusesLock.writeLock().unlock();
    }
    
    if (changed)
    {
      variableStatusesUpdated = true;
      fireEvent(E_VARIABLE_STATUS_CHANGED, variable, status.getStatus(), status.getComment());
    }
    
    if (persistent)
    {
      saveVariableStatuses();
    }
  }
  
  protected void clearVariableStatuses() throws ContextException
  {
    variableStatusesLock.writeLock().lock();
    try
    {
      if (variableStatuses != null)
      {
        variableStatuses.clear();
        variableStatusesTable = null;
      }
    }
    finally
    {
      variableStatusesLock.writeLock().unlock();
    }
    saveVariableStatuses();
  }
  
  protected void saveVariableStatuses() throws ContextException
  {
    if (variableStatusesUpdated)
    {
      persistVariableStatuses(getVariableStatusesTable());
    }
    variableStatusesUpdated = false;
  }
  
  protected void persistVariableStatuses(DataTable statuses) throws ContextException
  {
    // Do nothing, statuses persistence may be supported by descendants
  }
  
  public VariableStatus getVariableStatus(VariableDefinition variableDefinition) throws ContextException
  {
    variableStatusesLock.readLock().lock();
    try
    {
      return getVariableStatuses().get(variableDefinition.getName());
    }
    finally
    {
      variableStatusesLock.readLock().unlock();
    }
  }
  
  public DataTable getVchildren(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    DataTable ans = new SimpleDataTable(def.getFormat());
    for (Context con : getChildren(caller))
    {
      DataRecord record = ans.addRecord();
      record.setValue(VF_CHILDREN_NAME, con.getName());
      record.setValue(VF_CHILDREN_IS_CONTAINER, con.isContainer());
    }
    return ans;
  }
  
  public DataTable getVinfo(VariableDefinition def, CallerController caller, RequestController request) throws ContextException
  {
    return createContextInfoTable();
  }
  
  protected DataTable createContextInfoTable()
  {
    return new SimpleDataTable(INFO_DEFINITION_FORMAT, getDescription(), getType(), getGroup(), getIconId(), getLocalRoot(true), getPeerRoot(),
        getLocalPrimaryRoot(), getRemoteRoot(), getRemotePath(), isMapped());
  }
  
  public DataTable callFgetCopyData(FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
  {
    DataTable result = new SimpleDataTable(def.getOutputFormat().clone());
    
    String group = parameters.rec().getString(VF_INFO_GROUP);
    
    List<Context> recipients = null;
    
    DataTable recipientsTable = parameters.rec().getDataTable(FIF_COPY_DATA_RECIPIENTS);
    
    if (recipientsTable != null)
    {
      // Оптимизация: используем ArrayList вместо LinkedList
      recipients = new ArrayList<Context>();
      
      for (DataRecord rec : recipientsTable)
      {
        Context recipient = getContextManager().get(rec.getString(FIF_COPY_DATA_RECIPIENTS_RECIPIENT), caller);
        
        if (recipient != null)
        {
          recipients.add(recipient);
        }
      }
    }
    
    for (VariableDefinition vd : getVariableDefinitions(caller))
    {
      if (group != null && !Util.equals(ContextUtils.getBaseGroup(vd.getGroup()), group))
      {
        continue;
      }
      
      if (group == null && vd.getGroup() == null)
      {
        continue;
      }
      
      if (!vd.isReadable())
      {
        continue;
      }
      
      if (vd.getFormat() == null || !vd.getFormat().isReplicated())
      {
        continue;
      }
      
      if (recipients != null)
      {
        boolean skip = true;
        
        for (Context recipient : recipients)
        {
          VariableDefinition rvd = recipient.getVariableDefinition(vd.getName());
          
          if (rvd != null && rvd.isWritable() && (rvd.getFormat() == null || rvd.getFormat().isReplicated()))
          {
            skip = false;
          }
        }
        
        if (skip)
        {
          continue;
        }
      }
      
      DataTable value = getVariable(vd.getName(), caller);
      
      TableFormat format = value.getFormat().clone();
      
      DataTable fields = new SimpleDataTable(FIFT_REPLICATE_FIELDS);
      
      for (FieldFormat ff : format)
      {
        if (ff.isNotReplicated())
        {
          ff.setReadonly(true);
        }
        
        if (!ff.isHidden() && !ff.isReadonly() && !ff.isNotReplicated())
        {
          fields.addRecord().addString(ff.getName()).addString(ff.toString()).addBoolean(true);
        }
      }
      
      result.addRecord().addString(vd.getName()).addString(vd.getDescription()).addBoolean(false).addDataTable(fields).addDataTable(value);
    }
    
    result.fixRecords();
    
    return result;
  }
  
  public DataTable callFcopy(FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
  {
    DataTable result = new SimpleDataTable(def.getOutputFormat());
    
    for (DataRecord rec : parameters)
    {
      if (!rec.getBoolean(FOF_COPY_DATA_REPLICATE))
      {
        continue;
      }
      
      String varName = rec.getString(FOF_COPY_DATA_NAME);
      String providedDesc = rec.getString(FOF_COPY_DATA_DESCRIPTION);
      DataTable varValue = rec.getDataTable(FOF_COPY_DATA_VALUE);
      
      VariableDefinition targetVd = getVariableDefinition(varName, caller);
      
      if (targetVd == null)
      {
        result.addRecord().addString(providedDesc).addBoolean(false).addString(Cres.get().getString("conVarNotAvailInTgt"));
        continue;
      }
      
      String varDesc = targetVd.getDescription();
      
      if (!targetVd.isWritable())
      {
        result.addRecord().addString(varDesc).addBoolean(false).addString(Cres.get().getString("conVarNotWritableInTgt"));
        continue;
      }
      
      DataTable tgtVal;
      
      try
      {
        tgtVal = getVariableClone(varName, caller);
      }
      catch (ContextException ex)
      {
        result.addRecord().addString(varDesc).addBoolean(false).addString(Cres.get().getString("conErrGettingTgtVar") + ex.getMessage());
        continue;
      }
      
      // Оптимизация: используем ArrayList вместо LinkedList
      List<String> fields = new ArrayList<String>();
      for (DataRecord fieldRec : rec.getDataTable(FOF_COPY_DATA_FIELDS))
      {
        if (fieldRec.getBoolean(FIF_REPLICATE_FIELDS_REPLICATE))
        {
          fields.add(fieldRec.getString(FIF_REPLICATE_FIELDS_NAME));
        }
      }
      
      Set<String> tableCopyErrors = replicateVariableOnCopy(varName, varValue, tgtVal, fields, caller);
      
      DataTableUtils.inlineData(tgtVal, getContextManager(), caller);
      
      try
      {
        setVariable(targetVd, caller, request, tgtVal);
      }
      catch (ContextException ex)
      {
        Log.CONTEXT_FUNCTIONS.warn("Error setting variable during context copy", ex);
        result.addRecord().addString(varDesc).addBoolean(false).addString(Cres.get().getString("conErrSettingTgtVar") + ex.getMessage());
        continue;
      }
      
      if (tableCopyErrors.size() > 0)
      {
        result.addRecord().addString(varDesc).addBoolean(false).addString(StringUtils.print(tableCopyErrors, "; "));
      }
      else
      {
        result.addRecord().addString(varDesc).addBoolean(true);
      }
    }
    
    return result;
  }
  
  public DataTable callFupdateVariable(FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
  {
    DataRecord rec = parameters.rec();
    
    String varName = rec.getString(V_UPDATE_VARIABLE);
    Expression expression = new Expression(rec.getString(V_UPDATE_VARIABLE_EXPRESSION));
    DataTable res = null;
    
    VariableData data = getVariableData(varName);
    
    if (data == null)
    {
      throw new ContextException(MessageFormat.format(Cres.get().getString("conVarNotAvailExt"), varName, getPath()));
    }
    
    boolean readLockedBySameThread = data.getReadWriteLock().getReadHoldCount() > 0;
    if (!readLockedBySameThread)
    {
      lock(request, data.getReadWriteLock().writeLock());
    }
    try
    {
      DataTable variableValue = getVariable(varName, caller);
      res = new Evaluator(getContextManager(), this, variableValue, caller).evaluateToDataTable(expression);
      setVariable(varName, caller, res);
    }
    catch (SyntaxErrorException ex)
    {
      throw new ContextException(ex);
    }
    catch (EvaluationException ex)
    {
      throw new ContextException(ex);
    }
    finally
    {
      if (!readLockedBySameThread)
      {
        data.getReadWriteLock().writeLock().unlock();
      }
    }
    
    return res;
  }
  
  public Set<String> replicateVariableOnCopy(String variableName, DataTable variableValue, DataTable targetValue, List<String> fields, CallerController caller)
  {
    return DataTableReplication.copy(variableValue, targetValue, false, false, true, true, false, fields);
  }
  
  public DataTable callFcopyToChildren(FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
  {
    return copyTo(def, caller, request, parameters, getChildren(caller));
  }
  
  protected DataTable copyTo(FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters, List<C> children)
  {
    DataTable result = new SimpleDataTable(def.getOutputFormat());
    
    for (Context child : children)
    {
      String conDesc = child.getDescription() != null ? child.getDescription() : child.getPath();
      DataTable conRes;
      
      try
      {
        conRes = child.callFunction(F_COPY, caller, request, parameters);
      }
      catch (ContextException ex)
      {
        result.addRecord().addString(conDesc).addString(null).addBoolean(false).addString(ex.getMessage());
        continue;
      }
      
      for (DataRecord rec : conRes)
      {
        result.addRecord().addString(conDesc).addString(rec.getString(FIELD_REPLICATE_VARIABLE)).addBoolean(rec.getBoolean(FIELD_REPLICATE_SUCCESSFUL))
            .addString(rec.getString(FIELD_REPLICATE_ERRORS));
      }
    }
    
    return result;
  }
  
  public boolean isDebuggingEvaluations()
  {
    return false;
  }

  public void processEvaluation(Evaluator evaluator, Expression expression, Reference holder, Object result, NodeEvaluationDetails details)
  {

  }

  public void processEvaluationError(Evaluator evaluator, Expression expression, com.tibbo.aggregate.common.expression.Reference holder, Exception error, NodeEvaluationDetails details)
  {

  }

  public boolean isInstallationAllowed(String installableName)
  {
    return true;
  }

  final FunctionImplementation lockImpl = (con, def, caller, request, parameters) -> new SimpleDataTable(FOFT_LOCK,
      propertiesLock.lock(caller, parameters.rec().getString(FIF_LOCK_PROPERTIES_EDITOR_UUID)));
  
  final FunctionImplementation unlockImpl = (con, def, caller, request, parameters) -> {
    final DataTable result = new SimpleDataTable(FOFT_UNLOCK);
    final DataRecord record = result.addRecord();
    record.addBoolean(propertiesLock.unlock(caller, parameters.rec().getString(FIF_UNLOCK_PROPERTIES_EDITOR_UUID)));
    return result;
  };
  
  final FunctionImplementation breakLockImpl = (con, def, caller, request, parameters) -> {
    propertiesLock.breakLock();
    return null;
  };
  
  final FunctionImplementation lockedByImpl = (con, def, caller, request, parameters) -> new SimpleDataTable(FOFT_LOCKED_BY,
      propertiesLock.lockedBy());
  
  final FunctionImplementation getVariableStatusImpl = (con, def, caller, request, parameters) -> {
    String name = parameters.rec().getString(V_VARIABLE_NAME);

    ensureVariableStatuses();

    VariableStatus variableStatus = variableStatuses.get(name);
    if (variableStatus == null)
    {
      return new SimpleDataTable(VFT_VARIABLE_STATUSES, name, "", "");
    }
    return new SimpleDataTable(VFT_VARIABLE_STATUSES, name, variableStatus.getStatus(), variableStatus.getComment());
  };

  protected void logAsyncSessionExecutionAction(CallerController caller, RequestController requestController,
      ContextOperationType operationType, String entityName,
      DataTable parameters, DataTable output, long duration) throws ContextException
  {
  }

  protected void validateVariableValueToSet(VariableDefinition variableDefinition, DataTable value) throws ContextException
  {
  }
  
  protected Map<String, VariableData> getVariableDataView()
  {
    return unmodifiableMap(variableData);
  }

  protected Map<String, FunctionData> getFunctionDataView()
  {
    return unmodifiableMap(functionData);
  }

  protected Map<String, EventData> getEventDataView()
  {
    return unmodifiableMap(eventData);
  }

  //<editor-fold desc="Structure (tracing) methods">

  /**
   * @return {@code true} if span was actually started
   */
  private boolean beginSpan(RequestController request, EntityDefinition entityDefinition)
  {
    if (request != null && request.isLoggerRequest())
    {
      return false;     // to prevent short-circuiting
    }

    Pinpoint source;
    if (request != null && request.obtainPinpoint().isPresent())      // explicitly specified pinpoint
    {
      source = request.obtainPinpoint().get();
    }
    else if (!currentTrace.get().isEmpty())                           // a pinpoint from previous call
    {
      Span lastSpan = currentTrace.get().peek();
      //noinspection DataFlowIssue                // the deque has been just examined on non-emptiness
      source = lastSpan.getTarget().copy();
    }
    else if (currentSource.get() != null)                             // pinpoint during device synchronization
    {
      source = currentSource.get();
    }
    else
    {
      return false;
    }

    Pinpoint target = newPinpointFor(getPath(), entityDefinition.getName())
                        .withScope((contextManager != null) ? contextManager.getScope() : "");
    Span newSpan = new Span(source, target);

    currentTrace.get().push(newSpan);

    return true;
  }

  private void endSpan()
  {
    currentTrace.get().pop();
  }

  private void traceOperation(ContextOperationType operType)
  {
    C root = getRoot();

    if (!root.isStarted() || root.isProxy())
    {
      // A too early attempt to locate StructureCollector may end up with erroneous caching of NoopCollector because
      // the providing plugin might not be loaded yet. And in case of proxy the same may happen due to absence of
      // PluginDirector for remote contexts (including root ones).
      // To prevent it, actual tracing is allowed only after finishing building of system context tree and for non-proxy
      // root contexts.
      return;
    }

    Span currentSpan = currentTrace.get().peek();
    //noinspection DataFlowIssue                // the deque has been just examined on non-emptiness
    Pinpoint source = currentSpan.getSource();
    Pinpoint target = currentSpan.getTarget();

    PluginDirector pluginDirector = root.getContextManager().getPluginDirector();

    ApplicationStructureLocator.obtainStructureCollector(pluginDirector)
                               .recordInteraction(source, target, operType);
  }
  //</editor-fold>

}
