package com.tibbo.aggregate.common.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.command.EditData;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableBuilding;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public class StorageHelper
{
  public static final String CLASS_FIELD_INSTANCE_ID = "instance_id";
  public static final String CLASS_FIELD_AUTHOR = "author";
  public static final String CLASS_FIELD_CREATION_TIME = "creation_time";
  public static final String CLASS_FIELD_UPDATE_TIME = "update_time";
  
  public static final String MANY_TO_MANY_FIELD_RELATION_ID = "relation_id";
  public static final String MANY_TO_MANY_FIELD_LEFT_ID = "left_id";
  public static final String MANY_TO_MANY_FIELD_RIGTH_ID = "right_id";
  public static final String MANY_TO_MANY_TABLE_PREFIX = "rel_";
  public static final String PARENT_TABLE_FOREIGN_KEY_PREFIX = "parent_";
  
  public static final int SORT_ASCENDING = 0;
  public static final int SORT_DESCENDING = 1;
  
  public static final int VISIBILITY_DISABLED = 0;
  public static final int VISIBILITY_VISIBLE = 1;
  public static final int VISIBILITY_HIDDEN = 2;
  
  public static final String SESSION_ID = "id";
  
  public static final String F_STORAGE_OPEN = "storageOpen";
  public static final String F_STORAGE_CLOSE = "storageClose";
  public static final String F_STORAGE_GET = "storageGet";
  public static final String F_STORAGE_GET_DISTINCT = "storageGetDistinct";
  public static final String F_STORAGE_GET_EXTRA = "storageGetExtra";

  public static final String F_STORAGE_UPDATE = "storageUpdate";
  public static final String F_STORAGE_DELETE = "storageDelete";
  public static final String F_STORAGE_INSERT = "storageInsert";
  public static final String F_STORAGE_VIEWS = "storageViews";
  public static final String F_STORAGE_TABLES = "storageTables";
  public static final String F_STORAGE_COLUMNS = "storageColumns";
  public static final String F_STORAGE_RELATIONS = "storageRelations";
  public static final String F_STORAGE_FILTER = "storageFilter";
  public static final String F_STORAGE_SORTING = "storageSorting";
  public static final String F_STORAGE_CONSTRUCT = "storageConstruct";
  public static final String F_STORAGE_OPERATIONS = "storageOperations";
  public static final String F_STORAGE_CONSTRUCT_RELATION = "storageConstructRelation";
  public static final String F_STORAGE_DELETE_RELATION = "storageDeleteRelation";
  public static final String F_STORAGE_LINK_INSTANCES = "storageLinkInstances";
  public static final String F_STORAGE_UNLINK_INSTANCES = "storageUnlinkInstances";
  public static final String F_STORAGE_LINKED_INSTANCES_FILTER = "storageLinkedInstancesFilter";
  public static final String F_STORAGE_GET_FORMAT = "storageGetFormat";
  public static final String F_STORAGE_OPEN_TRANSATION = "storageOpenTransaction";
  public static final String F_STORAGE_CLOSE_TRANSATION = "storageCloseTransaction";
  public static final String F_STATE_TRANSITION = "stateTransition";

  public static final String F_DISTINCT_KEY = "key";
  public static final String F_DISTINCT_VALUE = "value";
  
  public static final String FIF_STORAGE_OPEN_ID = SESSION_ID;
  public static final String FIF_STORAGE_OPEN_VIEW = "view";
  public static final String FIF_STORAGE_OPEN_QUERY = "query";
  public static final String FIF_STORAGE_OPEN_TABLE = "table";
  public static final String FIF_STORAGE_OPEN_COLUMNS = "columns";
  public static final String FIF_STORAGE_OPEN_FILTER = "filter";
  public static final String FIF_STORAGE_OPEN_SORTING = "sorting";
  public static final String FIF_STORAGE_OPEN_GET_DATA = "getData";
  public static final String FIF_STORAGE_OPEN_LIMIT = "limit";
  public static final String FIF_STORAGE_OPEN_SMART_FILTER_EXPRESSION = "smartFilterExpression";
  public static final String FIF_STORAGE_OPEN_SEARCH = "search";
  public static final String FIF_STORAGE_OPEN_OFFSET = "offSet";
  
  public static final String FOF_STORAGE_OPEN_ID = SESSION_ID;
  public static final String FOF_STORAGE_OPEN_COUNT = "count";
  public static final String FOF_STORAGE_OPEN_DATA = "data";
  
  public static final String FIF_STORAGE_CLOSE_ID = SESSION_ID;
  
  public static final String FIF_STORAGE_GET_ID = SESSION_ID;
  public static final String FIF_STORAGE_GET_VIEW = "view";
  public static final String FIF_STORAGE_GET_FIRST = "first";
  public static final String FIF_STORAGE_GET_COUNT = "count";
  public static final String FIF_STORAGE_GET_SORTING = "sorting";
  public static final String FIF_STORAGE_GET_FILTER = "filter";
  public static final String FIF_STORAGE_GET_SEARCH = "search";
  public static final String FOF_STORAGE_GET_SIZE = "size";
  public static final String FOF_STORAGE_GET_DATA = "data";
  
  public static final String FIF_STORAGE_UPDATE_ID = SESSION_ID;
  public static final String FIF_STORAGE_UPDATE_ROW = "row";
  public static final String FIF_STORAGE_UPDATE_INSTANCE_ID = "instanceId";
  public static final String FIF_STORAGE_UPDATE_COLUMN = "column";
  public static final String FIF_STORAGE_UPDATE_VALUE = "value";
  public static final String FIF_STORAGE_UPDATE_TABLE = "table";
  public static final String FIF_STORAGE_UPDATE_FILTER = "filter";
  
  public static final String FOF_STORAGE_UPDATE_COUNT = "count";
  
  public static final String FIF_STORAGE_DELETE_ID = SESSION_ID;
  public static final String FIF_STORAGE_DELETE_ROWLIST = "rowList";
  public static final String FIF_STORAGE_DELETE_INSTANCE_ID_LIST = "instanceIdList";
  public static final String FIF_STORAGE_DELETE_TABLE = "table";
  public static final String FIF_STORAGE_DELETE_FILTER = "filter";
  
  public static final String FOF_STORAGE_DELETE_COUNT = "count";
  
  public static final String FIF_STORAGE_INSERT_ID = SESSION_ID;
  public static final String FIF_STORAGE_INSERT_VALUES = "values";
  public static final String FIF_STORAGE_INSERT_TABLE = "table";
  
  public static final String FOF_STORAGE_INSERT_ROW = "row";
  public static final String FOF_STORAGE_INSERT_INSTANCE_ID = "instanceId";
  
  public static final String FIF_STORAGE_COLUMNS_TABLE = "table";
  public static final String FIF_STORAGE_COLUMNS_COLUMNS = "columns";
  public static final String FIF_STORAGE_COLUMNS_LIFE_CYCLES = "lifeCycles";
  
  public static final String FIF_STORAGE_RELATIONS_TABLE = "table";
  public static final String FIF_STORAGE_RELATIONS_DASHBOARD_CLASS = "dashboardClass";
  
  public static final String FIF_STORAGE_FILTER_TABLE = "table";
  public static final String FIF_STORAGE_FILTER_FILTER = "filter";
  
  public static final String FIF_STORAGE_SORTING_TABLE = "table";
  public static final String FIF_STORAGE_SORTING_SORTING = "sorting";
  
  public static final String FIF_STORAGE_CONSTRUCT_TABLE = "table";
  public static final String FIF_STORAGE_CONSTRUCT_FIELDS = "fields";
  public static final String FIF_STORAGE_CONSTRUCT_LIFE_CYCLES = "lifeCycles";
  public static final String FIF_STORAGE_MANY_TO_MANY_RELATIONS = "manyToManyRelations";
  public static final String FIF_STORAGE_INHERITED_TABLES = "inheritedTables";
  
  public static final String FIF_STORAGE_CONSTRUCT_RELATION_TABLE = "table";
  public static final String FIF_STORAGE_CONSTRUCT_RELATION_PRIMARY_KEY_TYPE = "primaryKeyType";
  public static final String FIF_STORAGE_CONSTRUCT_RELATION_RELATED_TABLE = "relatedTable";
  public static final String FIF_STORAGE_CONSTRUCT_RELATION_RELATED_PRIMARY_KEY_TYPE = "relatedPrimaryKeyType";
  public static final String FIF_STORAGE_CONSTRUCT_RELATION_NAME = "name";
  public static final String FIF_STORAGE_CONSTRUCT_RELATION_CASCADE_DELETE = "cascadeDelete";
  
  public static final String FIF_STORAGE_DELETE_RELATION_TABLE = "table";
  public static final String FIF_STORAGE_DELETE_RELATION_PRIMARY_KEY_TYPE = "primaryKeyType";
  public static final String FIF_STORAGE_DELETE_RELATION_RELATED_TABLE = "relatedTable";
  public static final String FIF_STORAGE_DELETE_RELATION_RELATED_PRIMARY_KEY_TYPE = "relatedPrimaryKeyType";
  public static final String FIF_STORAGE_DELETE_RELATION_NAME = "name";
  
  public static final String FIF_STORAGE_OPERATIONS_TABLE = "table";
  public static final String FIF_STORAGE_OPERATIONS_COLUMN = "column";
  
  public static final String FIF_STORAGE_LINK_INSTANCES_TABLE = "table";
  public static final String FIF_STORAGE_LINK_INSTANCES_RELATED_TABLE = "relatedTable";
  public static final String FIF_STORAGE_LINK_INSTANCES_RELATION_NAME = "relationName";
  public static final String FIF_STORAGE_LINK_INSTANCES_RELATED_IDS = "relatedIds";
  
  public static final String FIF_STORAGE_LINK_INSTANCES_INSTANCE_ID = "instanceId";
  public static final String FIF_STORAGE_LINK_INSTANCES_RELATED_ID = "relatedId";
  
  public static final String FIF_STATE_TRANSITIONS_CLASS_CONTEXT = "contextName";
  public static final String FIF_STATE_TRANSITIONS_CYCLE_NAME = "cycleName";
  public static final String FIF_STATE_TRANSITIONS_CURRENT_STATE = "currentState";
  public static final String FIF_STATE_TRANSITIONS_NEXT_STATE = "nextState";
  public static final String FIF_STATE_TRANSITIONS_INSTANCE_ID = "instanceId";
  public static final String FIF_STATE_TRANSITIONS_SESSION_ID = "sessionId";
  
  public static final String FIF_STORAGE_LINKED_INSTANCES_FILTER_INSTANCE_ID = "instanceId";
  public static final String FIF_STORAGE_LINKED_INSTANCES_FILTER_TABLE = "table";
  public static final String FIF_STORAGE_LINKED_INSTANCES_FILTER_RELATED_TABLE = "relatedTable";
  public static final String FIF_STORAGE_LINKED_INSTANCES_FILTER_RELATION_NAME = "relationName";
  public static final String FIF_STORAGE_LINKED_INSTANCES_FILTER_RELATION_OWNER = "relationOwner";
  public static final String FIF_STORAGE_LINKED_INSTANCES_FILTER_GET_RELATED = "getRelated";
  
  public static final String FIF_STORAGE_GET_FORMAT_TABLE = "table";
  public static final String FIF_STORAGE_GET_FORMAT_VIEW = "view";

  public static final String FIF_STORAGE_GET_DISTINCT_COLUMN = "column";
  public static final String FIF_STORAGE_GET_DISTINCT_COUNT = "count";
  public static final String FIF_STORAGE_GET_DISTINCT_FILTER_VALUE = "regexp";
  public static final String FIF_STORAGE_GET_DISTINCT_FILTER_TABLE = "filterTable";

  public static final String FIF_STORAGE_GET_EXTRA_DATA_FIRST = FIF_STORAGE_GET_FIRST;
  public static final String FIF_STORAGE_GET_EXTRA_DATA_COUNT = FIF_STORAGE_GET_COUNT;
  public static final String FOF_STORAGE_GET_EXTRA_DATA_COLUMN = "column";
  public static final String FOF_STORAGE_GET_EXTRA_DATA_TABLE = "table";
  public static final String FOF_STORAGE_GET_EXTRA_DATA_DATA = "data";

  public static final String FOF_STORAGE_OPEN_TRANSACTION_ID = SESSION_ID;
  
  public static final String FIF_STORAGE_CLOSE_TRANSACTION_ID = SESSION_ID;
  public static final String FIF_STORAGE_CLOSE_TRANSACTION_COMMIT = "commit";
  
  public static final String FIELD_COLUMNS_NAME = "name";
  public static final String FIELD_COLUMNS_DESCRIPTION = "description";
  public static final String FIELD_COLUMNS_GROUP = "group";
  public static final String FIELD_COLUMNS_VISIBILITY = "visible";
  public static final String FIELD_COLUMNS_READONLY = "readonly";
  public static final String FIELD_COLUMNS_PRIMARY_KEY = "primaryKey";
  public static final String FIELD_COLUMNS_IS_CALCULATED_FIELD = "isCalculatedField";
  public static final String FIELD_COLUMNS_FIELD_DATATABLE = "dataTableField";
  
  public static final String FIELD_SORTING_COLUMN = "column";
  public static final String FIELD_SORTING_ORDER = "order";
  
  public static final TableFormat FIFT_STORAGE_OPEN = new TableFormat(1, 1);

  static
  {
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_ID + "><L><F=N><D=" + Cres.get().getString("id") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_VIEW + "><S><F=N><D=" + Cres.get().getString("view") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_QUERY + "><S><F=N><D=" + Cres.get().getString("query") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_TABLE + "><S><F=N><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_COLUMNS + "><T><F=N><D=" + Cres.get().getString("columns") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_FILTER + "><T><F=N><D=" + Cres.get().getString("filter") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_SORTING + "><T><F=N><D=" + Cres.get().getString("sorting") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_GET_DATA + "><B><D=" + Cres.get().getString("getData") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_LIMIT + "><I><F=N><D=" + Cres.get().getString("limit") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_OFFSET + "><I><F=N><D=" + Cres.get().getString("offset") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_SMART_FILTER_EXPRESSION + "><S><F=N><D=" + Cres.get().getString("smartFilterExpression") + ">");
    FIFT_STORAGE_OPEN.addField("<" + FIF_STORAGE_OPEN_SEARCH + "><S><F=N><D=" + Cres.get().getString("search") + ">");
  }
  
  public static final TableFormat FOFT_STORAGE_OPEN = new TableFormat(1, 1);
  
  static
  {
    FOFT_STORAGE_OPEN.addField("<" + FOF_STORAGE_OPEN_ID + "><L><D=" + Cres.get().getString("id") + ">");
    FOFT_STORAGE_OPEN.addField("<" + FOF_STORAGE_OPEN_COUNT + "><I><F=N><D=" + Cres.get().getString("count") + ">");
    FOFT_STORAGE_OPEN.addField("<" + FOF_STORAGE_OPEN_DATA + "><T><F=N><D=" + Cres.get().getString("data") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_CLOSE = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_CLOSE.addField("<" + FIF_STORAGE_CLOSE_ID + "><L><D=" + Cres.get().getString("id") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_GET = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_GET.addField("<" + FIF_STORAGE_GET_ID + "><L><D=" + Cres.get().getString("id") + ">");
    FIFT_STORAGE_GET.addField("<" + FIF_STORAGE_GET_VIEW + "><S><F=N><D=" + Cres.get().getString("view") + ">");
    FIFT_STORAGE_GET.addField("<" + FIF_STORAGE_GET_FIRST + "><I><D=" + Cres.get().getString("first") + ">");
    FIFT_STORAGE_GET.addField("<" + FIF_STORAGE_GET_COUNT + "><I><D=" + Cres.get().getString("count") + ">");
    FIFT_STORAGE_GET.addField("<" + FIF_STORAGE_GET_SORTING + "><T><F=N><D=" + Cres.get().getString("sorting") + ">");
    FIFT_STORAGE_GET.addField("<" + FIF_STORAGE_GET_FILTER + "><T><F=N><D=" + Cres.get().getString("filter") + ">");
    FIFT_STORAGE_GET.addField("<" + FIF_STORAGE_GET_SEARCH + "><S><F=N><D=" + Cres.get().getString("search") + ">");
  }
  
  public static final TableFormat FOFT_STORAGE_GET = new TableFormat(1, 1);
  
  static
  {
    FOFT_STORAGE_GET.addField("<" + FOF_STORAGE_GET_SIZE + "><I><F=N><D=" + Cres.get().getString("size") + ">");
    FOFT_STORAGE_GET.addField("<" + FOF_STORAGE_GET_DATA + "><T><F=N><D=" + Cres.get().getString("data") + ">");
  }

  public static final TableFormat FIFT_STORAGE_GET_DISTINCT = new TableFormat(1, 1);

  static
  {
    FIFT_STORAGE_GET_DISTINCT.addField("<" + FIF_STORAGE_GET_ID + "><L><D=" + Cres.get().getString("id") + ">");
    FIFT_STORAGE_GET_DISTINCT.addField("<" + FIF_STORAGE_GET_DISTINCT_COLUMN + "><S><D=" + Cres.get().getString("columnName") + ">");
    FIFT_STORAGE_GET_DISTINCT.addField("<" + FIF_STORAGE_GET_DISTINCT_COUNT + "><I><D=" + Cres.get().getString("count") + ">");
    FIFT_STORAGE_GET_DISTINCT.addField("<" + FIF_STORAGE_GET_DISTINCT_FILTER_VALUE + "><S><A=0><D=" + Cres.get().getString("filter") + ">");
    FIFT_STORAGE_GET_DISTINCT.addField("<" + FIF_STORAGE_GET_DISTINCT_FILTER_TABLE + "><T><F=N><D=" + Cres.get().getString("filterTable") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_UPDATE = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_UPDATE.addField("<" + FIF_STORAGE_UPDATE_ID + "><L><F=N><D=" + Cres.get().getString("id") + ">");
    FIFT_STORAGE_UPDATE.addField("<" + FIF_STORAGE_UPDATE_ROW + "><I><F=N><D=" + Cres.get().getString("row") + ">");
    FIFT_STORAGE_UPDATE.addField("<" + FIF_STORAGE_UPDATE_COLUMN + "><S><F=N><D=" + Cres.get().getString("column") + ">");
    FIFT_STORAGE_UPDATE.addField("<" + FIF_STORAGE_UPDATE_VALUE + "><T><D=" + Cres.get().getString("value") + ">");
    FIFT_STORAGE_UPDATE.addField("<" + FIF_STORAGE_UPDATE_TABLE + "><S><F=N><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_UPDATE.addField("<" + FIF_STORAGE_UPDATE_FILTER + "><T><F=N><D=" + Cres.get().getString("filter") + ">");
    FIFT_STORAGE_UPDATE.addField("<" + FIF_STORAGE_UPDATE_INSTANCE_ID + "><L><F=N><D=" + Cres.get().getString("instanceId") + ">");
  }
  
  public static final TableFormat FOFT_STORAGE_UPDATE = new TableFormat();
  
  static
  {
    FOFT_STORAGE_UPDATE.addField("<" + FOF_STORAGE_UPDATE_COUNT + "><I><D=" + Cres.get().getString("count") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_DELETE = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_DELETE.addField("<" + FIF_STORAGE_DELETE_ID + "><L><F=N><D=" + Cres.get().getString("id") + ">");
    FIFT_STORAGE_DELETE.addField("<" + FIF_STORAGE_DELETE_ROWLIST + "><T><F=N><D=" + Cres.get().getString("rowList") + ">");
    FIFT_STORAGE_DELETE.addField("<" + FIF_STORAGE_DELETE_TABLE + "><S><F=N><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_DELETE.addField("<" + FIF_STORAGE_DELETE_FILTER + "><T><F=N><D=" + Cres.get().getString("filter") + ">");
    FIFT_STORAGE_DELETE.addField("<" + FIF_STORAGE_DELETE_INSTANCE_ID_LIST + "><T><F=N><D=" + Cres.get().getString("instanceIdList") + ">");
  }
  
  public static final TableFormat FOFT_STORAGE_DELETE = new TableFormat();
  
  static
  {
    FOFT_STORAGE_DELETE.addField("<" + FOF_STORAGE_DELETE_COUNT + "><I><D=" + Cres.get().getString("count") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_INSERT = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_INSERT.addField("<" + FIF_STORAGE_INSERT_ID + "><L><F=N><D=" + Cres.get().getString("id") + ">");
    FIFT_STORAGE_INSERT.addField("<" + FIF_STORAGE_INSERT_VALUES + "><T><D=" + Cres.get().getString("values") + ">");
    FIFT_STORAGE_INSERT.addField("<" + FIF_STORAGE_INSERT_TABLE + "><S><F=N><D=" + Cres.get().getString("table") + ">");
  }
  
  public static final TableFormat FOFT_STORAGE_INSERT = new TableFormat();
  
  static
  {
    FOFT_STORAGE_INSERT.addField("<" + FOF_STORAGE_INSERT_INSTANCE_ID + "><S><D=" + Cres.get().getString("instanceId") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_COLUMNS = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_COLUMNS.addField("<" + FIF_STORAGE_COLUMNS_TABLE + "><S><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_COLUMNS.addField("<" + FIF_STORAGE_COLUMNS_COLUMNS + "><T><D=" + Cres.get().getString("columns") + ">");
    FIFT_STORAGE_COLUMNS.addField(FieldFormat.create(FIF_STORAGE_COLUMNS_LIFE_CYCLES, FieldFormat.DATATABLE_FIELD).setNullable(true));
  }
  
  public static final TableFormat FIFT_STORAGE_RELATIONS = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_RELATIONS.addField("<" + FIF_STORAGE_RELATIONS_TABLE + "><S><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_RELATIONS.addField("<" + FIF_STORAGE_RELATIONS_DASHBOARD_CLASS + "><S>");
  }
  
  public static final TableFormat FIFT_STORAGE_FILTER = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_FILTER.addField("<" + FIF_STORAGE_FILTER_TABLE + "><S><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_FILTER.addField("<" + FIF_STORAGE_FILTER_FILTER + "><T><D=" + Cres.get().getString("filter") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_SORTING = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_SORTING.addField("<" + FIF_STORAGE_SORTING_TABLE + "><S><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_SORTING.addField("<" + FIF_STORAGE_SORTING_SORTING + "><T><D=" + Cres.get().getString("sorting") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_CONSTRUCT = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_CONSTRUCT.addField("<" + FIF_STORAGE_CONSTRUCT_TABLE + "><S><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_CONSTRUCT.addField("<" + FIF_STORAGE_CONSTRUCT_FIELDS + "><T><F=N><D=" + Cres.get().getString("fields") + ">");
    FIFT_STORAGE_CONSTRUCT.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_LIFE_CYCLES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("lifeCycles")).setNullable(true));
    FIFT_STORAGE_CONSTRUCT.addField(FieldFormat.create(FIF_STORAGE_MANY_TO_MANY_RELATIONS, FieldFormat.DATATABLE_FIELD, Cres.get().getString("classManyToManyRelations")).setNullable(true));
    FIFT_STORAGE_CONSTRUCT.addField(FieldFormat.create(FIF_STORAGE_INHERITED_TABLES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("inheritedTables")).setNullable(true));
  }
  
  public static final TableFormat FIFT_STORAGE_OPERATIONS = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_OPERATIONS.addField("<" + FIF_STORAGE_OPERATIONS_TABLE + "><S><D=" + Cres.get().getString("table") + ">");
    FIFT_STORAGE_OPERATIONS.addField("<" + FIF_STORAGE_OPERATIONS_COLUMN + "><S><D=" + Cres.get().getString("column") + ">");
  }
  
  public static final TableFormat FIFT_STORAGE_CONSTRUCT_RELATION = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_CONSTRUCT_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("table")));
    FIFT_STORAGE_CONSTRUCT_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_PRIMARY_KEY_TYPE, FieldFormat.STRING_FIELD, Cres.get().getString("primaryKeyType")));
    FIFT_STORAGE_CONSTRUCT_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_RELATED_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("relatedTable")));
    FIFT_STORAGE_CONSTRUCT_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_RELATED_PRIMARY_KEY_TYPE, FieldFormat.STRING_FIELD, Cres.get().getString("relatedPrimaryKeyType")));
    FIFT_STORAGE_CONSTRUCT_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_NAME, FieldFormat.STRING_FIELD, Cres.get().getString("relationName")));
    FIFT_STORAGE_CONSTRUCT_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_CASCADE_DELETE, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("classManyToManyCascadeDelete")));
  }
  
  public static final TableFormat FIFT_STORAGE_DELETE_RELATION = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_DELETE_RELATION.addField(FieldFormat.create(FIF_STORAGE_DELETE_RELATION_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("table")));
    FIFT_STORAGE_DELETE_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_PRIMARY_KEY_TYPE, FieldFormat.STRING_FIELD, Cres.get().getString("primaryKeyType")));
    FIFT_STORAGE_DELETE_RELATION.addField(FieldFormat.create(FIF_STORAGE_DELETE_RELATION_RELATED_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("relatedTable")));
    FIFT_STORAGE_DELETE_RELATION.addField(FieldFormat.create(FIF_STORAGE_CONSTRUCT_RELATION_RELATED_PRIMARY_KEY_TYPE, FieldFormat.STRING_FIELD, Cres.get().getString("relatedPrimaryKeyType")));
    FIFT_STORAGE_DELETE_RELATION.addField(FieldFormat.create(FIF_STORAGE_DELETE_RELATION_NAME, FieldFormat.STRING_FIELD, Cres.get().getString("relationName")));
  }
  
  public static final TableFormat FORMAT_RELATED_IDS = new TableFormat();
  
  static
  {
    FORMAT_RELATED_IDS.addField(FieldFormat.create(FIF_STORAGE_LINK_INSTANCES_INSTANCE_ID, FieldFormat.STRING_FIELD, Cres.get().getString("instanceId")));
    FORMAT_RELATED_IDS.addField(FieldFormat.create(FIF_STORAGE_LINK_INSTANCES_RELATED_ID, FieldFormat.STRING_FIELD, Cres.get().getString("relatedId")));
  }
  
  public static final TableFormat FIFT_STORAGE_LINK_INSTANCES = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_LINK_INSTANCES.addField(FieldFormat.create(FIF_STORAGE_LINK_INSTANCES_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("table")));
    FIFT_STORAGE_LINK_INSTANCES.addField(FieldFormat.create(FIF_STORAGE_LINK_INSTANCES_RELATED_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("relatedTable")));
    FIFT_STORAGE_LINK_INSTANCES.addField(FieldFormat.create(FIF_STORAGE_LINK_INSTANCES_RELATION_NAME, FieldFormat.STRING_FIELD, Cres.get().getString("relationName")));
    FIFT_STORAGE_LINK_INSTANCES.addField(FieldFormat.create(FIF_STORAGE_LINK_INSTANCES_RELATED_IDS, FieldFormat.DATATABLE_FIELD, Cres.get().getString("relatedIds")));
  }
  
  public static final TableFormat FIFT_STORAGE_UNLINK_INSTANCES = FIFT_STORAGE_LINK_INSTANCES.clone();
  
  public static final TableFormat FIFT_STORAGE_LINKED_INSTANCES_FILTER = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_LINKED_INSTANCES_FILTER.addField(FieldFormat.create(FIF_STORAGE_LINKED_INSTANCES_FILTER_INSTANCE_ID, FieldFormat.STRING_FIELD, Cres.get().getString("instanceId")));
    FIFT_STORAGE_LINKED_INSTANCES_FILTER.addField(FieldFormat.create(FIF_STORAGE_LINKED_INSTANCES_FILTER_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("table")));
    FIFT_STORAGE_LINKED_INSTANCES_FILTER.addField(FieldFormat.create(FIF_STORAGE_LINKED_INSTANCES_FILTER_RELATED_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("relatedTable")));
    FIFT_STORAGE_LINKED_INSTANCES_FILTER.addField(FieldFormat.create(FIF_STORAGE_LINKED_INSTANCES_FILTER_RELATION_NAME, FieldFormat.STRING_FIELD, Cres.get().getString("relationName")));
    FIFT_STORAGE_LINKED_INSTANCES_FILTER.addField(FieldFormat.create(FIF_STORAGE_LINKED_INSTANCES_FILTER_RELATION_OWNER, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("relationOwner")));
    FIFT_STORAGE_LINKED_INSTANCES_FILTER.addField(FieldFormat.create(FIF_STORAGE_LINKED_INSTANCES_FILTER_GET_RELATED, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("getRelated")));
  }
  
  public static final TableFormat FOFT_STORAGE_LINKED_INSTANCES_FILTER = ViewFilterElement.FORMAT.clone();
  
  public static final TableFormat FIFT_STORAGE_GET_FORMAT = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_GET_FORMAT.addField(FieldFormat.create(FIF_STORAGE_GET_FORMAT_TABLE, FieldFormat.STRING_FIELD, Cres.get().getString("table"), null, true));
    FIFT_STORAGE_GET_FORMAT.addField(FieldFormat.create(FIF_STORAGE_GET_FORMAT_VIEW, FieldFormat.STRING_FIELD, Cres.get().getString("view"), null, true));
  }
  
  public static final TableFormat FOFT_STORAGE_OPEN_TRANSACTION = new TableFormat(1, 1);
  
  static
  {
    FOFT_STORAGE_OPEN_TRANSACTION.addField(FieldFormat.create(FOF_STORAGE_OPEN_TRANSACTION_ID, FieldFormat.LONG_FIELD, Cres.get().getString("id")));
  }
  
  public static final TableFormat FIFT_STORAGE_CLOSE_TRANSACTION = new TableFormat(1, 1);
  
  static
  {
    FIFT_STORAGE_CLOSE_TRANSACTION.addField(FieldFormat.create(FIF_STORAGE_CLOSE_TRANSACTION_ID, FieldFormat.LONG_FIELD, Cres.get().getString("id")));
    FIFT_STORAGE_CLOSE_TRANSACTION.addField(FieldFormat.create(FIF_STORAGE_CLOSE_TRANSACTION_COMMIT, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("commitTransaction")));
  }
  
  public static final TableFormat FORMAT_COLUMNS = new TableFormat(true);
  
  static
  {
    FORMAT_COLUMNS.addField("<" + FIELD_COLUMNS_NAME + "><S><F=EK><D=" + Cres.get().getString("name") + ">");
    FORMAT_COLUMNS.addField("<" + FIELD_COLUMNS_DESCRIPTION + "><S><F=R><D=" + Cres.get().getString("description") + ">");
    FORMAT_COLUMNS.addField("<" + FIELD_COLUMNS_GROUP + "><S><D=" + Cres.get().getString("group") + ">");
    
    FieldFormat ff = FieldFormat.create(FIELD_COLUMNS_VISIBILITY, FieldFormat.INTEGER_FIELD, Cres.get().getString("visibility"), StorageHelper.VISIBILITY_VISIBLE);
    ff.addSelectionValue(VISIBILITY_VISIBLE, Cres.get().getString("visible"));
    ff.addSelectionValue(VISIBILITY_HIDDEN, Cres.get().getString("hidden"));
    ff.addSelectionValue(VISIBILITY_DISABLED, Cres.get().getString("disabled"));
    FORMAT_COLUMNS.addField(ff);
    
    FORMAT_COLUMNS.addField("<" + FIELD_COLUMNS_READONLY + "><B><A=0><D=" + Cres.get().getString("readOnly") + ">");
    FORMAT_COLUMNS.addField("<" + FIELD_COLUMNS_PRIMARY_KEY + "><B><F=R><A=0><D=" + Cres.get().getString("primaryKey") + ">");
    
    FORMAT_COLUMNS.getField(FIELD_COLUMNS_GROUP).setReadonly(true);
    
    FORMAT_COLUMNS.addField(FieldFormat.create(FIELD_COLUMNS_IS_CALCULATED_FIELD, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("isCalculatedField")).setDefault(false));
    FORMAT_COLUMNS.addField(FieldFormat.create(FIELD_COLUMNS_FIELD_DATATABLE, FieldFormat.DATATABLE_FIELD, Cres.get().getString("fieldColumnDataTable")).setNullable(true).setDefault(null));
    
    FORMAT_COLUMNS.setNamingExpression(DefaultFunctions.PRINT + "({}, \"{" + FIELD_COLUMNS_VISIBILITY + "} ? {" + FIELD_COLUMNS_NAME + "} : null\", \", \")");
    FORMAT_COLUMNS.addBinding(FIELD_COLUMNS_FIELD_DATATABLE + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + FIELD_COLUMNS_IS_CALCULATED_FIELD + "}");
    FORMAT_COLUMNS.addBinding(FIELD_COLUMNS_FIELD_DATATABLE, "{" + FIELD_COLUMNS_IS_CALCULATED_FIELD + "} ? ( {" + FIELD_COLUMNS_FIELD_DATATABLE + "} == null ? " + DefaultFunctions.TABLE + "(\""
        + getTableForCalculatedField() + "\") : {" + FIELD_COLUMNS_FIELD_DATATABLE + "} ) : null");
    FORMAT_COLUMNS.addBinding(FIELD_COLUMNS_NAME + "#" + DataTableBindingProvider.PROPERTY_ENABLED, "{" + FIELD_COLUMNS_IS_CALCULATED_FIELD + "}");
  }

  public static final TableFormat FOFT_STORAGE_GET_DISTINCT = new TableFormat();

  static
  {
    FieldFormat<?> ff = FieldFormat.create(F_DISTINCT_VALUE, FieldFormat.STRING_FIELD);
    FOFT_STORAGE_GET_DISTINCT.addField(ff);
  }

  public static final TableFormat FOFT_STORAGE_INSTANCE_GET_DISTINCT = new TableFormat();

  static
  {
    FieldFormat<?> ff = FieldFormat.create(F_DISTINCT_KEY, FieldFormat.STRING_FIELD);
    FOFT_STORAGE_INSTANCE_GET_DISTINCT.addField(ff);
    ff = FieldFormat.create(F_DISTINCT_VALUE, FieldFormat.STRING_FIELD);
    FOFT_STORAGE_INSTANCE_GET_DISTINCT.addField(ff);
  }
  
  public static final TableFormat FIFT_STATE_TRANSITION = new TableFormat(1, 1)
  {
    {
      addField(FieldFormat.create(FIF_STATE_TRANSITIONS_CLASS_CONTEXT, FieldFormat.STRING_FIELD));
      addField(FieldFormat.create(FIF_STATE_TRANSITIONS_CYCLE_NAME, FieldFormat.STRING_FIELD));
      addField(FieldFormat.create(FIF_STATE_TRANSITIONS_CURRENT_STATE, FieldFormat.STRING_FIELD));
      addField(FieldFormat.create(FIF_STATE_TRANSITIONS_NEXT_STATE, FieldFormat.STRING_FIELD));
      addField(FieldFormat.create(FIF_STATE_TRANSITIONS_INSTANCE_ID, FieldFormat.STRING_FIELD));
      addField(FieldFormat.create(FIF_STATE_TRANSITIONS_SESSION_ID, FieldFormat.LONG_FIELD));
    }
  };

  public static final TableFormat FIFT_STORAGE_GET_EXTRA = new TableFormat(1, 1);

  static
  {
    FIFT_STORAGE_GET_EXTRA.addField("<" + FIF_STORAGE_GET_ID + "><L><D=" + Cres.get().getString("id") + ">");
    FIFT_STORAGE_GET_EXTRA.addField("<" + FIF_STORAGE_GET_EXTRA_DATA_FIRST + "><I><D=" + Cres.get().getString("first") + ">");
    FIFT_STORAGE_GET_EXTRA.addField("<" + FIF_STORAGE_GET_EXTRA_DATA_COUNT + "><I><D=" + Cres.get().getString("count") + ">");
  }

  public static final TableFormat TF_STORAGE_GET_EXTRA_DATA_DATA = new TableFormat();

  public static final String F_STORAGE_GET_EXTRA_DATA_SOURCE = "source";
  public static final String F_STORAGE_GET_EXTRA_DATA_VALUE = "value";

  static
  {
    FieldFormat<?> ff = FieldFormat.create(F_STORAGE_GET_EXTRA_DATA_SOURCE, FieldFormat.STRING_FIELD);
    TF_STORAGE_GET_EXTRA_DATA_DATA.addField(ff);

    ff = FieldFormat.create(F_STORAGE_GET_EXTRA_DATA_VALUE, FieldFormat.STRING_FIELD);
    ff.setNullable(true);
    TF_STORAGE_GET_EXTRA_DATA_DATA.addField(ff);
  }

  public static final TableFormat FOFT_STORAGE_GET_EXTRA = new TableFormat();

  static
  {
    FieldFormat<?> ff = FieldFormat.create(FOF_STORAGE_GET_EXTRA_DATA_COLUMN, FieldFormat.STRING_FIELD);
    FOFT_STORAGE_GET_EXTRA.addField(ff);

    ff = FieldFormat.create(FOF_STORAGE_GET_EXTRA_DATA_DATA, FieldFormat.DATATABLE_FIELD, Cres.get().getString("column"), new SimpleDataTable(TF_STORAGE_GET_EXTRA_DATA_DATA));
    FOFT_STORAGE_GET_EXTRA.addField(ff);
  }

  private static String getTableForCalculatedField()
  {
    TableFormat cloneFormat = DataTableBuilding.FIELDS_FORMAT.clone().setMaxRecords(1).setMinRecords(1);
    // Оптимизация: используем ArrayList вместо LinkedList
    cloneFormat.getField(FIELD_COLUMNS_NAME).setHidden(true).setValidators(new ArrayList<>());
    String replaceFirst = cloneFormat.encode(true).replaceAll("\\\\", "\\\\\\\\");
    return replaceFirst.replaceAll("\"", "\\\\\"");
  }
  
  public static final TableFormat FORMAT_SORTING = new TableFormat();
  
  static
  {
    FORMAT_SORTING.addField("<" + FIELD_SORTING_COLUMN + "><S><F=E><D=" + Cres.get().getString("viewColumnOrExpression") + ">");
    
    FieldFormat ff = FieldFormat.create("<" + FIELD_SORTING_ORDER + "><I><D=" + Cres.get().getString("sortOrder") + ">");
    ff.addSelectionValue(SORT_ASCENDING, Cres.get().getString("ascending"));
    ff.addSelectionValue(SORT_DESCENDING, Cres.get().getString("descending"));
    FORMAT_SORTING.addField(ff);
    
    FORMAT_SORTING.setReorderable(true);
    
    FORMAT_SORTING.setNamingExpression(DefaultFunctions.PRINT + "({}, \"{" + FIELD_SORTING_COLUMN + "} + ' ' + {" + FIELD_SORTING_ORDER + "#" + DefaultReferenceResolver.SELECTION_VALUE_DESCRIPTION
        + "}\", \", \")");
  }
  
  public static final TableFormat FORMAT_ROWLIST = new TableFormat(true);
  
  static
  {
    FORMAT_ROWLIST.addField(FieldFormat.create("row", FieldFormat.INTEGER_FIELD, Cres.get().getString("row")));
  }
  
  public static final TableFormat FORMAT_INSTANCE_ID_LIST = new TableFormat(true);

  static
  {
    FORMAT_INSTANCE_ID_LIST.addField(FieldFormat.create(CLASS_FIELD_INSTANCE_ID, FieldFormat.LONG_FIELD, Cres.get().getString("instanceId")));
  }

  public static final TableFormat FOFT_ADD_ROW_ACTION = new TableFormat(1, 1);
  
  static
  {
    FOFT_ADD_ROW_ACTION.addField(FieldFormat.create(EditData.CF_STORAGE_CONTEXT, StringFieldFormat.STRING_FIELD));
  }
  
  public static final TableFormat FOFT_REMOVE_OR_UPDATE_ROW_ACTION = new TableFormat();
  
  static
  {
    FOFT_REMOVE_OR_UPDATE_ROW_ACTION.addField(FieldFormat.create(EditData.CF_INLUDE_RECORD, FieldFormat.BOOLEAN_FIELD).setDefault(true));
    FOFT_REMOVE_OR_UPDATE_ROW_ACTION.addField(FieldFormat.create(EditData.CF_STORAGE_CONTEXT, StringFieldFormat.STRING_FIELD).setNullable(true));
    FOFT_REMOVE_OR_UPDATE_ROW_ACTION.addField(FieldFormat.create(EditData.CF_RECORD_INDEX, FieldFormat.INTEGER_FIELD).setNullable(true));
    FOFT_REMOVE_OR_UPDATE_ROW_ACTION.addField(FieldFormat.create(CLASS_FIELD_INSTANCE_ID, FieldFormat.LONG_FIELD).setNullable(true));
    FOFT_REMOVE_OR_UPDATE_ROW_ACTION.addField(FieldFormat.create(EditData.CF_RECORD_DESCRIPTION, StringFieldFormat.STRING_FIELD).setNullable(true));
  }
  
  private static final String GROUP_STORAGE = ContextUtils.createGroup(ContextUtils.GROUP_DEFAULT, Cres.get().getString("storage"));
  
  public static final FunctionDefinition FD_STORAGE_OPEN = new FunctionDefinition(F_STORAGE_OPEN, FIFT_STORAGE_OPEN, FOFT_STORAGE_OPEN, Cres.get().getString("storageOpen"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_CLOSE = new FunctionDefinition(F_STORAGE_CLOSE, FIFT_STORAGE_CLOSE, TableFormat.EMPTY_FORMAT, Cres.get().getString("storageClose"),
      GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_GET = new FunctionDefinition(F_STORAGE_GET, FIFT_STORAGE_GET, FOFT_STORAGE_GET, Cres.get().getString("storageGet"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_GET_DISTINCT = new FunctionDefinition(F_STORAGE_GET_DISTINCT, FIFT_STORAGE_GET_DISTINCT, FOFT_STORAGE_GET_DISTINCT,
      Cres.get().getString("storageGetDistinct"), GROUP_STORAGE);

  public static final FunctionDefinition FD_STORAGE_GET_EXTRA = new FunctionDefinition(F_STORAGE_GET_EXTRA, FIFT_STORAGE_GET_EXTRA, FOFT_STORAGE_GET_EXTRA,
          Cres.get().getString("storageGetExtraData"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_UPDATE = new FunctionDefinition(F_STORAGE_UPDATE, FIFT_STORAGE_UPDATE, FOFT_STORAGE_UPDATE, Cres.get().getString("storageUpdate"),
      GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_DELETE = new FunctionDefinition(F_STORAGE_DELETE, FIFT_STORAGE_DELETE, FOFT_STORAGE_DELETE, Cres.get().getString("storageDelete"),
      GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_INSERT = new FunctionDefinition(F_STORAGE_INSERT, FIFT_STORAGE_INSERT, FOFT_STORAGE_INSERT, Cres.get().getString("storageInsert"),
      GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_VIEWS = new FunctionDefinition(F_STORAGE_VIEWS, TableFormat.EMPTY_FORMAT, DataTableBuilding.SELECTION_VALUES_FORMAT,
      Cres.get().getString("storageViews"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_TABLES = new FunctionDefinition(F_STORAGE_TABLES, TableFormat.EMPTY_FORMAT, DataTableBuilding.SELECTION_VALUES_FORMAT,
      Cres.get().getString("storageTables"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_COLUMNS = new FunctionDefinition(F_STORAGE_COLUMNS, FIFT_STORAGE_COLUMNS, FORMAT_COLUMNS, Cres.get().getString("storageColumns"),
      GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_RELATIONS = new FunctionDefinition(F_STORAGE_RELATIONS, FIFT_STORAGE_RELATIONS, FORMAT_COLUMNS);
  public static final FunctionDefinition FD_STORAGE_FILTER = new FunctionDefinition(F_STORAGE_FILTER, FIFT_STORAGE_FILTER, null);
  public static final FunctionDefinition FD_STORAGE_SORTING = new FunctionDefinition(F_STORAGE_SORTING, FIFT_STORAGE_SORTING, null);
  public static final FunctionDefinition FD_STORAGE_CONSTRUCT = new FunctionDefinition(F_STORAGE_CONSTRUCT, FIFT_STORAGE_CONSTRUCT, TableFormat.EMPTY_FORMAT);
  public static final FunctionDefinition FD_STORAGE_OPERATIONS = new FunctionDefinition(F_STORAGE_OPERATIONS, FIFT_STORAGE_OPERATIONS, DataTableBuilding.SELECTION_VALUES_FORMAT);
  
  public static final FunctionDefinition FD_STORAGE_CONSTRUCT_RELATION = new FunctionDefinition(F_STORAGE_CONSTRUCT_RELATION, FIFT_STORAGE_CONSTRUCT_RELATION, TableFormat.EMPTY_FORMAT);
  public static final FunctionDefinition FD_STORAGE_DELETE_RELATION = new FunctionDefinition(F_STORAGE_DELETE_RELATION, FIFT_STORAGE_DELETE_RELATION, TableFormat.EMPTY_FORMAT);
  public static final FunctionDefinition FD_STORAGE_LINK_INSTANCES = new FunctionDefinition(F_STORAGE_LINK_INSTANCES, FIFT_STORAGE_LINK_INSTANCES, TableFormat.EMPTY_FORMAT);
  public static final FunctionDefinition FD_STORAGE_UNLINK_INSTANCES = new FunctionDefinition(F_STORAGE_UNLINK_INSTANCES, FIFT_STORAGE_UNLINK_INSTANCES, TableFormat.EMPTY_FORMAT);
  public static final FunctionDefinition FD_STORAGE_LINKED_INSTANCES_FILTER = new FunctionDefinition(F_STORAGE_LINKED_INSTANCES_FILTER, FIFT_STORAGE_LINKED_INSTANCES_FILTER,
      FOFT_STORAGE_LINKED_INSTANCES_FILTER);
  public static final FunctionDefinition FD_STORAGE_GET_FORMAT = new FunctionDefinition(F_STORAGE_GET_FORMAT, FIFT_STORAGE_GET_FORMAT, null,
      Cres.get().getString("storageGetFormat"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_OPEN_TRANSACTION = new FunctionDefinition(F_STORAGE_OPEN_TRANSATION, null, FOFT_STORAGE_OPEN_TRANSACTION,
      Cres.get().getString("storageOpenTransaction"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STORAGE_CLOSE_TRANSACTION = new FunctionDefinition(F_STORAGE_CLOSE_TRANSATION, FIFT_STORAGE_CLOSE_TRANSACTION, null,
      Cres.get().getString("storageCloseTransaction"), GROUP_STORAGE);
  public static final FunctionDefinition FD_STATE_TRANSITION = new FunctionDefinition(F_STATE_TRANSITION, FIFT_STATE_TRANSITION, TableFormat.EMPTY_FORMAT);
  
  public static List<FunctionDefinition> getBaseFunctionDefinitions()
  {
    return Arrays.asList(FD_STORAGE_OPEN, FD_STORAGE_CLOSE, FD_STORAGE_GET, FD_STORAGE_UPDATE, FD_STORAGE_DELETE, FD_STORAGE_INSERT, FD_STORAGE_TABLES,
        FD_STORAGE_COLUMNS, FD_STORAGE_FILTER, FD_STORAGE_SORTING, FD_STORAGE_OPERATIONS, FD_STORAGE_CONSTRUCT, FD_STORAGE_CONSTRUCT_RELATION,
        FD_STORAGE_DELETE_RELATION, FD_STORAGE_LINK_INSTANCES, FD_STORAGE_UNLINK_INSTANCES, FD_STORAGE_LINKED_INSTANCES_FILTER);
  }
  
  private static final Random STORAGE_SESSION_ID_GENERATOR = new Random();
  
  public static long generateViewSessionId()
  {
    return Math.abs(STORAGE_SESSION_ID_GENERATOR.nextLong());
  }
  
  public static final String E_CLASS_INSTANCE_CREATED = "classInstanceCreated";
  public static final String E_CLASS_INSTANCE_CHANGED = "classInstanceChanged";
  public static final String E_CLASS_INSTANCE_DELETED = "classInstanceDeleted";
  public static final String E_CLASS_INSTANCE_COMMENTED = "classInstanceCommented";
  
  public static final String FIELD_EVENT_INSTANCE_ID = "instanceId";
  public static final String FIELD_EVENT_INSTANCE_DESCRIPTION = "instanceDescription";
  public static final String FIELD_EVENT_INSTANCE = "instance";
  public static final String FIELD_EVENT_FIELD_NAME = "fieldName";
  public static final String FIELD_EVENT_FIELD_DESCRIPTION = "fieldDescription";
  public static final String FIELD_EVENT_OLD_VALUE = "oldValue";
  public static final String FIELD_EVENT_NEW_VALUE = "newValue";
  public static final String FIELD_EVENT_MODIFICATION_AUTHOR = "modificationAuthor";
  public static final String FIELD_EVENT_AUTHOR = "author";
  public static final String FIELD_EVENT_COMMENT = "comment";
  
  public static final String KEY_EVENT_INSTANCE_ID = "instanceId";
  public static final String KEY_EVENT_CLASS_NAME = "class_name";
  
  public static final TableFormat EFT_CLASS_INSTANCE_CREATED = new TableFormat();
  
  static
  {
    EFT_CLASS_INSTANCE_CREATED.addField(FieldFormat.create(FIELD_EVENT_INSTANCE_ID, FieldFormat.STRING_FIELD));
    EFT_CLASS_INSTANCE_CREATED.addField(FieldFormat.create(FIELD_EVENT_INSTANCE_DESCRIPTION, FieldFormat.STRING_FIELD).setNullable(true));
    EFT_CLASS_INSTANCE_CREATED.addField(FieldFormat.create(FIELD_EVENT_MODIFICATION_AUTHOR, FieldFormat.STRING_FIELD).setNullable(true));
  }
  
  public static final TableFormat EFT_CLASS_INSTANCE_DELETED = EFT_CLASS_INSTANCE_CREATED.clone();
  
  static
  {
    EFT_CLASS_INSTANCE_DELETED.addField(FieldFormat.create(FIELD_EVENT_INSTANCE, FieldFormat.DATATABLE_FIELD).setNullable(true));
  }
  
  public static final TableFormat EFT_CLASS_INSTANCE_CHANGED = EFT_CLASS_INSTANCE_CREATED.clone();
  
  static
  {
    EFT_CLASS_INSTANCE_CHANGED.addField(FieldFormat.create(FIELD_EVENT_FIELD_NAME, FieldFormat.STRING_FIELD));
    EFT_CLASS_INSTANCE_CHANGED.addField(FieldFormat.create(FIELD_EVENT_FIELD_DESCRIPTION, FieldFormat.STRING_FIELD).setNullable(true));
    EFT_CLASS_INSTANCE_CHANGED.addField(FieldFormat.create(FIELD_EVENT_OLD_VALUE, FieldFormat.STRING_FIELD).setNullable(true));
    EFT_CLASS_INSTANCE_CHANGED.addField(FieldFormat.create(FIELD_EVENT_NEW_VALUE, FieldFormat.STRING_FIELD).setNullable(true));
  }
  
  public static final TableFormat EFT_CLASS_INSTANCE_COMMENT = new TableFormat(1, 1);
  
  static
  {
    EFT_CLASS_INSTANCE_COMMENT.addField(FieldFormat.create(FIELD_EVENT_INSTANCE_ID, FieldFormat.STRING_FIELD).setNullable(true).setHidden(true));
    EFT_CLASS_INSTANCE_COMMENT.addField(FieldFormat.create(FIELD_EVENT_AUTHOR, FieldFormat.STRING_FIELD).setNullable(true).setHidden(true));
    EFT_CLASS_INSTANCE_COMMENT.addField(FieldFormat.create(FIELD_EVENT_COMMENT, FieldFormat.STRING_FIELD));
    EFT_CLASS_INSTANCE_COMMENT.addBinding(FIELD_EVENT_INSTANCE_ID, makeExpression(FIELD_EVENT_INSTANCE_ID));
    EFT_CLASS_INSTANCE_COMMENT.addBinding(FIELD_EVENT_AUTHOR, makeExpression(FIELD_EVENT_AUTHOR));
  }
  
  public static String makeExpression(String field)
  {
    return "{" + field + "} == null ? {env/" + field + "} : {" + field + "}";
  }
  
  public static String getPrimaryKeyName(Context storageContext, CallerController caller, String storageTable)
  {
    DataTable columns = null;
    
    if (storageTable == null)
    {
      return null;
    }
    
    DataRecord inputFields = new DataRecord(StorageHelper.FIFT_STORAGE_COLUMNS);
    inputFields.setValue(StorageHelper.FIF_STORAGE_COLUMNS_TABLE, storageTable);
    
    try
    {
      columns = storageContext.callFunction(StorageHelper.F_STORAGE_COLUMNS, caller, inputFields.wrap());
    }
    catch (ContextException ex)
    {
      return null;
    }
    
    if (columns != null)
    {
      DataRecord rec = columns.select(StorageHelper.FIELD_COLUMNS_PRIMARY_KEY, true);
      if (rec != null && rec.hasField(StorageHelper.FIELD_COLUMNS_NAME))
      {
        return rec.getString(StorageHelper.FIELD_COLUMNS_NAME);
      }
    }
    
    return null;
  }
}
