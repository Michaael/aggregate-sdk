package com.tibbo.aggregate.common.datatable;

import java.util.*;

public class DataTableQuery implements Iterable<QueryCondition>
{
  private final List<QueryCondition> conditions = new LinkedList();
  
  public DataTableQuery(QueryCondition... conditions)
  {
    this.conditions.addAll(Arrays.asList(conditions));
  }
  
  public List<QueryCondition> getConditions()
  {
    return conditions;
  }
  
  public void addCondition(QueryCondition condition)
  {
    conditions.add(condition);
  }
  
  @Override
  public Iterator<QueryCondition> iterator()
  {
    return conditions.iterator();
  }
}
