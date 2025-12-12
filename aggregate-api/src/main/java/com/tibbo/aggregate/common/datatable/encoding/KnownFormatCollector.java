package com.tibbo.aggregate.common.datatable.encoding;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KnownFormatCollector
{
  private final Map<Integer, Boolean> formatIds;
  
  public KnownFormatCollector()
  {
    formatIds = new ConcurrentHashMap<>();
  }
  
  public KnownFormatCollector(Map<Integer, Boolean> formatIds)
  {
    this.formatIds = new ConcurrentHashMap<>(formatIds);
  }
  
  public Map<Integer, Boolean> getFormatIds()
  {
    return new HashMap<>(formatIds);
  }
  
  public boolean isKnown(int formatId)
  {
    return formatIds.containsKey(formatId);
  }
  
  public boolean isMarked(int formatId)
  {
    Boolean marked = formatIds.get(formatId);
    
    return marked != null ? marked : false;
  }
  
  public void makeKnown(int formatId, boolean mark)
  {
    formatIds.put(formatId, mark);
  }
  
  public void markAll()
  {
    // Trick required to avoid inability to upgrade a read lock
    Set<Integer> idsToMark = new LinkedHashSet();
    
    for (Entry<Integer, Boolean> entry : formatIds.entrySet())
    {
      if (!entry.getValue())
      {
        idsToMark.add(entry.getKey());
      }
    }
    
    if (idsToMark.size() == 0)
    {
      return;
    }
    
    for (Integer id : idsToMark)
    {
      formatIds.put(id, true);
    }
  }
}
