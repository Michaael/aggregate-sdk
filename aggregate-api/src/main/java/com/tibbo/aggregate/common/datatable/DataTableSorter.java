package com.tibbo.aggregate.common.datatable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DataTableSorter implements Iterable<SortOrder>
{
  private final List<SortOrder> orders = new LinkedList();
  
  public DataTableSorter(SortOrder... orders)
  {
    this.orders.addAll(Arrays.asList(orders));
  }
  
  public DataTableSorter(List<SortOrder> orders)
  {
    this.orders.addAll(orders);
  }
  
  public List<SortOrder> getOrders()
  {
    return orders;
  }
  
  public void addOrder(SortOrder order)
  {
    orders.add(order);
  }
  
  @Override
  public Iterator<SortOrder> iterator()
  {
    return orders.iterator();
  }
  
}
