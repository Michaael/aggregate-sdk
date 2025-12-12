package com.tibbo.aggregate.common.action;

import java.util.*;

public class BatchContext
{
  private List<BatchEntry> entries = new LinkedList();
  private BatchEntry currentEntry;
  
  public BatchContext()
  {
    super();
  }
  
  void addBatchEntry(BatchEntry batchEntry)
  {
    if (batchEntry == null)
    {
      throw new NullPointerException();
    }
    
    entries.add(batchEntry);
  }
  
  public List<BatchEntry> getEntries()
  {
    return Collections.unmodifiableList(entries);
  }
  
  public BatchEntry getCurrentEntry()
  {
    return currentEntry;
  }
  
  public void markAsPerfomed(BatchEntry entry)
  {
    if (!entries.contains(entry))
    {
      throw new IllegalArgumentException("Entry '" + entry + "' is not on the list");
    }
    
    entry.setFulfilled(true);
  }
  
  protected void setCurrentEntry(BatchEntry currentEntry)
  {
    this.currentEntry = currentEntry;
  }
}
