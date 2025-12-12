package com.tibbo.aggregate.common.expression.parser;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;

public class NodeEvaluationDetails
{
  private static final String FIELD_NODE = "node";
  private static final String FIELD_RESULT = "result";
  private static final String FIELD_TIMESTAMP = "timestamp";
  private static final String FIELD_QUALITY = "quality";
  private static final String FIELD_CHILDREN = "children";
  
  private static final String FIELD_RESULT_VALUE = "value";
  
  public static TableFormat FORMAT = new TableFormat();
  
  static
  {
    FORMAT.addField("<" + FIELD_NODE + "><S><F=N><" + Cres.get().getString("node") + ">");
    FORMAT.addField("<" + FIELD_RESULT + "><T><F=N><" + Cres.get().getString("result") + ">");
    FORMAT.addField("<" + FIELD_TIMESTAMP + "><D><F=NA><" + Cres.get().getString("timestamp") + ">");
    FORMAT.addField("<" + FIELD_QUALITY + "><I><F=NA>A<" + Cres.get().getString("quality") + ">");
    FORMAT.addField("<" + FIELD_CHILDREN + "><T><F=N><" + Cres.get().getString("children") + ">");
    
    FORMAT.setNamingExpression("print({}, \"{" + FIELD_NODE + "}\", \", \")");
  }
  
  private String nodeImage;
  private AttributedObject nodeResult;
  
  private List<NodeEvaluationDetails> children = new LinkedList();
  
  public NodeEvaluationDetails()
  {
  }
  
  public String getNodeImage()
  {
    return nodeImage;
  }
  
  public void setNodeImage(String nodeImage)
  {
    this.nodeImage = nodeImage;
  }
  
  public AttributedObject getNodeResult()
  {
    return nodeResult;
  }
  
  public void setNodeResult(AttributedObject nodeResult)
  {
    this.nodeResult = nodeResult;
  }
  
  public List<NodeEvaluationDetails> getChildren()
  {
    return children;
  }
  
  public void setChildren(List<NodeEvaluationDetails> children)
  {
    this.children = children;
  }
  
  public void addChild(NodeEvaluationDetails child)
  {
    children.add(child);
  }
  
  public DataTable toDataTable()
  {
    return toDataRecord().wrap();
  }
  
  public DataRecord toDataRecord()
  {
    DataRecord dr = new DataRecord(FORMAT);
    dr.addString(getNodeImage());
    
    AttributedObject nr = getNodeResult();
    
    dr.addDataTable((nr != null && nr.getValue() != null) ? DataTableUtils.wrapToTable(Collections.singletonMap(FIELD_RESULT_VALUE, nr.getValue())) : null);
    dr.addDate(nr != null ? nr.getTimestamp() : null);
    dr.addInt(nr != null ? nr.getQuality() : null);
    
    if (children.size() > 0)
    {
      DataTable kids = new SimpleDataTable(FORMAT);
      
      for (NodeEvaluationDetails ned : children)
      {
        kids.addRecord(ned.toDataRecord());
      }
      
      dr.addDataTable(kids);
    }
    else
    {
      dr.addDataTable(null);
    }
    
    return dr;
  }
}
