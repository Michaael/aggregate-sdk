package com.tibbo.aggregate.common.view;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;

public class StorageGraphHelper
{
  public static final String F_GRAPH_OPEN = "graphOpen";
  public static final String F_GRAPH_CLOSE = "graphClose";
  public static final String F_GRAPH_GET = "graphGet";
  public static final String F_GRAPH_INSERT_NODE = "graphInsertNode";
  public static final String F_GRAPH_UPDATE_NODE = "graphUpdateNode";
  public static final String F_GRAPH_DELETE_NODE = "graphDeleteNode";
  public static final String F_GRAPH_INSERT_EDGE = "graphInsertEdge";
  public static final String F_GRAPH_UPDATE_EDGE = "graphUpdateEdge";
  public static final String F_GRAPH_DELETE_EDGE = "graphDeleteEdge";
  
  public static final String FIF_GRAPH_OPEN_QUERY = "query";
  public static final String FOF_GRAPH_OPEN_ID = "id";
  
  public static final String FIF_GRAPH_CLOSE_ID = "id";
  
  public static final String FIF_GRAPH_GET_ID = "id";
  public static final String FIF_GRAPH_GET_NODE_ID = "nodeId";
  public static final String FIF_GRAPH_GET_DEPTH = "depth";
  
  public static final String FOF_GRAPH_GET_NODES = "nodes";
  public static final String FOF_GRAPH_GET_LINKS = "links";
  
  public static final String FIF_GRAPH_INSERT_NODE_ID = "id";
  public static final String FIF_GRAPH_INSERT_NODE_LABEL = "label";
  public static final String FIF_GRAPH_INSERT_NODE_PROPERTIES = "properties";
  
  public static final String FOF_GRAPH_INSERT_NODE_NODE_ID = "nodeId";
  
  public static final String FIF_GRAPH_UPDATE_NODE_ID = "id";
  public static final String FIF_GRAPH_UPDATE_NODE_NODE_ID = "nodeId";
  public static final String FIF_GRAPH_UPDATE_NODE_PROPERTIES = "properties";
  
  public static final String FIF_GRAPH_DELETE_NODE_ID = "id";
  public static final String FIF_GRAPH_DELETE_NODE_NODE_ID = "nodeId";
  
  public static final String FIF_GRAPH_INSERT_EDGE_ID = "id";
  public static final String FIF_GRAPH_INSERT_EDGE_LABEL = "label";
  public static final String FIF_GRAPH_INSERT_EDGE_OUT_NODE_ID = "outNodeId";
  public static final String FIF_GRAPH_INSERT_EDGE_IN_NODE_ID = "inNodeId";
  public static final String FIF_GRAPH_INSERT_EDGE_PROPERTIES = "properties";
  
  public static final String FOF_GRAPH_INSERT_EDGE_EDGE_ID = "edgeId";
  
  public static final String FIF_GRAPH_UPDATE_EDGE_ID = "id";
  public static final String FIF_GRAPH_UPDATE_EDGE_EDGE_ID = "edgeId";
  public static final String FIF_GRAPH_UPDATE_EDGE_PROPERTIES = "properties";
  
  public static final String FIF_GRAPH_DELETE_EDGE_ID = "id";
  public static final String FIF_GRAPH_DELETE_EDGE_EDGE_ID = "edgeId";
  
  public static final String FIELD_NODE_PROPERTIES_PROPERTY = "property";
  public static final String FIELD_NODE_PROPERTIES_VALUE = "value";
  
  public static final String FIELD_NODE_ID = "id";
  public static final String FIELD_NODE_TYPE = "type";
  public static final String FIELD_NODE_PROPERTIES = "properties";
  
  public static final String FIELD_LINK_PROPERTIES_PROPERTY = "property";
  public static final String FIELD_LINK_PROPERTIES_VALUE = "value";
  
  public static final String FIELD_LINK_TYPE = "type";
  public static final String FIELD_LINK_SOURCE = "source";
  public static final String FIELD_LINK_TARGET = "target";
  public static final String FIELD_LINK_PROPERTIES = "properties";
  
  public static final TableFormat FORMAT_NODE_PROPERTIES = new TableFormat();
  static
  {
    FORMAT_NODE_PROPERTIES.addField(FieldFormat.create(FIELD_NODE_PROPERTIES_PROPERTY, FieldFormat.STRING_FIELD, Cres.get().getString("property")));
    FORMAT_NODE_PROPERTIES.addField(FieldFormat.create(FIELD_NODE_PROPERTIES_VALUE, FieldFormat.DATATABLE_FIELD, Cres.get().getString("value")));
  }
  
  public static final TableFormat FORMAT_NODE = new TableFormat();
  static
  {
    FORMAT_NODE.addField(FieldFormat.create(FIELD_NODE_ID, FieldFormat.STRING_FIELD, Cres.get().getString("id")));
    FORMAT_NODE.addField(FieldFormat.create(FIELD_NODE_TYPE, FieldFormat.STRING_FIELD, Cres.get().getString("type")));
    FORMAT_NODE.addField(FieldFormat.create(FIELD_NODE_PROPERTIES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("properties"), new SimpleDataTable(FORMAT_NODE_PROPERTIES)));
  }
  
  public static final TableFormat FORMAT_LINK_PROPERTIES = new TableFormat();
  static
  {
    FORMAT_LINK_PROPERTIES.addField(FieldFormat.create(FIELD_LINK_PROPERTIES_PROPERTY, FieldFormat.STRING_FIELD, Cres.get().getString("property")));
    FORMAT_LINK_PROPERTIES.addField(FieldFormat.create(FIELD_LINK_PROPERTIES_VALUE, FieldFormat.DATATABLE_FIELD, Cres.get().getString("value")));
  }
  
  public static final TableFormat FORMAT_LINK = new TableFormat();
  static
  {
    FORMAT_LINK.addField(FieldFormat.create(FIELD_LINK_TYPE, FieldFormat.STRING_FIELD, Cres.get().getString("type")));
    FORMAT_LINK.addField(FieldFormat.create(FIELD_LINK_SOURCE, FieldFormat.STRING_FIELD, Cres.get().getString("source")));
    FORMAT_LINK.addField(FieldFormat.create(FIELD_LINK_TARGET, FieldFormat.STRING_FIELD, Cres.get().getString("target")));
    FORMAT_LINK.addField(FieldFormat.create(FIELD_LINK_PROPERTIES, FieldFormat.DATATABLE_FIELD, Cres.get().getString("properties"), new SimpleDataTable(FORMAT_LINK_PROPERTIES)));
  }
  
  public static final TableFormat FIFT_GRAPH_OPEN = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_OPEN.addField(new StringFieldFormat(FIF_GRAPH_OPEN_QUERY).setNullable(true).setDescription(Cres.get().getString("query")));
  }
  
  public static final TableFormat FOFT_GRAPH_OPEN = new TableFormat(1, 1);
  static
  {
    FOFT_GRAPH_OPEN.addField(new LongFieldFormat(FOF_GRAPH_OPEN_ID).setDescription(Cres.get().getString("sessionId")));
  }
  
  public static final TableFormat FIFT_GRAPH_CLOSE = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_CLOSE.addField(new LongFieldFormat(FIF_GRAPH_CLOSE_ID).setDescription(Cres.get().getString("id")));
  }
  
  public static final TableFormat FIFT_GRAPH_GET = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_GET.addField(new LongFieldFormat(FIF_GRAPH_GET_ID).setDescription(Cres.get().getString("id")));
    FIFT_GRAPH_GET.addField(new StringFieldFormat(FIF_GRAPH_GET_NODE_ID).setNullable(true).setDescription(Cres.get().getString("nodeId")));
    FIFT_GRAPH_GET.addField(new IntFieldFormat(FIF_GRAPH_GET_DEPTH).setDescription(Cres.get().getString("depth")));
  }
  
  public static final TableFormat FOFT_GRAPH_GET = new TableFormat(1, 1);
  
  public static final TableFormat FIFT_GRAPH_INSERT_NODE = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_INSERT_NODE.addField(new LongFieldFormat(FIF_GRAPH_INSERT_NODE_ID).setDescription(Cres.get().getString("id")));
    FIFT_GRAPH_INSERT_NODE.addField(new StringFieldFormat(FIF_GRAPH_INSERT_NODE_LABEL).setDescription(Cres.get().getString("label")));
    FIFT_GRAPH_INSERT_NODE.addField(new DataTableFieldFormat(FIF_GRAPH_INSERT_NODE_PROPERTIES).setDescription(Cres.get().getString("properties")));
  }
  
  public static final TableFormat FOFT_GRAPH_INSERT_NODE = new TableFormat(1, 1);
  static
  {
    FOFT_GRAPH_INSERT_NODE.addField(new StringFieldFormat(FOF_GRAPH_INSERT_NODE_NODE_ID).setDescription(Cres.get().getString("nodeId")));
  }
  
  public static final TableFormat FIFT_GRAPH_UPDATE_NODE = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_UPDATE_NODE.addField(new LongFieldFormat(FIF_GRAPH_UPDATE_NODE_ID).setDescription(Cres.get().getString("id")));
    FIFT_GRAPH_UPDATE_NODE.addField(new StringFieldFormat(FIF_GRAPH_UPDATE_NODE_NODE_ID).setDescription(Cres.get().getString("nodeId")));
    FIFT_GRAPH_UPDATE_NODE.addField(new DataTableFieldFormat(FIF_GRAPH_UPDATE_NODE_PROPERTIES).setDescription(Cres.get().getString("properties")));
  }
  
  public static final TableFormat FIFT_GRAPH_DELETE_NODE = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_DELETE_NODE.addField(new LongFieldFormat(FIF_GRAPH_DELETE_NODE_ID).setDescription(Cres.get().getString("id")));
    FIFT_GRAPH_DELETE_NODE.addField(new StringFieldFormat(FIF_GRAPH_DELETE_NODE_NODE_ID).setDescription(Cres.get().getString("nodeId")));
  }
  
  public static final TableFormat FIFT_GRAPH_INSERT_EDGE = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_INSERT_EDGE.addField(new LongFieldFormat(FIF_GRAPH_INSERT_EDGE_ID).setDescription(Cres.get().getString("id")));
    FIFT_GRAPH_INSERT_EDGE.addField(new StringFieldFormat(FIF_GRAPH_INSERT_EDGE_LABEL).setDescription(Cres.get().getString("label")));
    FIFT_GRAPH_INSERT_EDGE.addField(new StringFieldFormat(FIF_GRAPH_INSERT_EDGE_OUT_NODE_ID).setDescription(Cres.get().getString("outNodeId")));
    FIFT_GRAPH_INSERT_EDGE.addField(new StringFieldFormat(FIF_GRAPH_INSERT_EDGE_IN_NODE_ID).setDescription(Cres.get().getString("inNodeId")));
    FIFT_GRAPH_INSERT_EDGE.addField(new DataTableFieldFormat(FIF_GRAPH_INSERT_EDGE_PROPERTIES).setDescription(Cres.get().getString("properties")));
  }
  
  public static final TableFormat FOFT_GRAPH_INSERT_EDGE = new TableFormat(1, 1);
  static
  {
    FOFT_GRAPH_INSERT_EDGE.addField(new StringFieldFormat(FOF_GRAPH_INSERT_EDGE_EDGE_ID).setDescription(Cres.get().getString("edgeId")));
  }
  
  public static final TableFormat FIFT_GRAPH_UPDATE_EDGE = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_UPDATE_EDGE.addField(new LongFieldFormat(FIF_GRAPH_UPDATE_EDGE_ID).setDescription(Cres.get().getString("id")));
    FIFT_GRAPH_UPDATE_EDGE.addField(new StringFieldFormat(FIF_GRAPH_UPDATE_EDGE_EDGE_ID).setDescription(Cres.get().getString("edgeId")));
    FIFT_GRAPH_UPDATE_EDGE.addField(new DataTableFieldFormat(FIF_GRAPH_UPDATE_EDGE_PROPERTIES).setDescription(Cres.get().getString("properties")));
  }
  
  public static final TableFormat FIFT_GRAPH_DELETE_EDGE = new TableFormat(1, 1);
  static
  {
    FIFT_GRAPH_DELETE_EDGE.addField(new LongFieldFormat(FIF_GRAPH_DELETE_EDGE_ID).setDescription(Cres.get().getString("id")));
    FIFT_GRAPH_DELETE_EDGE.addField(new StringFieldFormat(FIF_GRAPH_DELETE_EDGE_EDGE_ID).setDescription(Cres.get().getString("edgeId")));
  }
  
  private static final String GROUP_GRAPH = ContextUtils.createGroup(ContextUtils.GROUP_DEFAULT, Cres.get().getString("graph"));
  
  public static final FunctionDefinition FD_GRAPH_OPEN = new FunctionDefinition(F_GRAPH_OPEN, FIFT_GRAPH_OPEN, FOFT_GRAPH_OPEN, Cres.get().getString("graphOpen"), GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_CLOSE = new FunctionDefinition(F_GRAPH_CLOSE, FIFT_GRAPH_CLOSE, TableFormat.EMPTY_FORMAT, Cres.get().getString("graphClose"), GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_GET = new FunctionDefinition(F_GRAPH_GET, FIFT_GRAPH_GET, FOFT_GRAPH_GET, Cres.get().getString("graphGet"), GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_INSERT_NODE = new FunctionDefinition(F_GRAPH_INSERT_NODE, FIFT_GRAPH_INSERT_NODE, FOFT_GRAPH_INSERT_NODE, Cres.get().getString("graphInsertNode"),
      GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_UPDATE_NODE = new FunctionDefinition(F_GRAPH_UPDATE_NODE, FIFT_GRAPH_UPDATE_NODE, TableFormat.EMPTY_FORMAT, Cres.get().getString("graphUpdateNode"),
      GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_DELETE_NODE = new FunctionDefinition(F_GRAPH_DELETE_NODE, FIFT_GRAPH_DELETE_NODE, TableFormat.EMPTY_FORMAT, Cres.get().getString("graphDeleteNode"),
      GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_INSERT_EDGE = new FunctionDefinition(F_GRAPH_INSERT_EDGE, FIFT_GRAPH_INSERT_EDGE, FOFT_GRAPH_INSERT_EDGE, Cres.get().getString("graphInsertEdge"),
      GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_UPDATE_EDGE = new FunctionDefinition(F_GRAPH_UPDATE_EDGE, FIFT_GRAPH_UPDATE_EDGE, TableFormat.EMPTY_FORMAT, Cres.get().getString("graphUpdateEdge"),
      GROUP_GRAPH);
  public static final FunctionDefinition FD_GRAPH_DELETE_EDGE = new FunctionDefinition(F_GRAPH_DELETE_EDGE, FIFT_GRAPH_DELETE_EDGE, TableFormat.EMPTY_FORMAT, Cres.get().getString("graphDeleteEdge"),
      GROUP_GRAPH);
  
  public static List<FunctionDefinition> getGraphFunctionDefinitions()
  {
    return Arrays.asList(FD_GRAPH_OPEN, FD_GRAPH_CLOSE, FD_GRAPH_GET, FD_GRAPH_INSERT_NODE, FD_GRAPH_UPDATE_NODE, FD_GRAPH_DELETE_NODE, FD_GRAPH_INSERT_EDGE, FD_GRAPH_UPDATE_EDGE,
        FD_GRAPH_DELETE_EDGE);
  }
}
