package com.tibbo.aggregate.common.data;

import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.security.*;
import com.tibbo.aggregate.common.util.*;

public class Event implements Cloneable
{
  public final static long DEFAULT_EVENT_EXPIRATION_PERIOD = 100 * TimeHelper.DAY_IN_MS; // Milliseconds

  private Long id;
  private final Date instantiationtime = new Date();
  private Date creationtime;
  private Date expirationtime;
  private String context;
  private String name;
  private List<Acknowledgement> acknowledgements = new LinkedList();
  private DataTable data = null;
  private Integer listener;
  private int level;
  private Permissions permissions;
  private int count = 1;
  private List<Enrichment> enrichments = new LinkedList();

  private Object originator;
  private String deduplicationId;
  private Long sessionID;
  private String serverID = null;

  public Event()
  {
    setCreationtime(new Date(System.currentTimeMillis()));
  }

  public Event(String context, EventDefinition def, int level, DataTable data, Long id, Date creationtime, Permissions permissions)
  {
    this();
    init(context, def.getName(), level, data, id);
    this.name = def.getName();
    this.permissions = permissions;

    if (creationtime != null)
    {
      this.creationtime = creationtime;
    }

    if (def.getExpirationPeriod() > 0)
    {
      setExpirationtime(new Date(System.currentTimeMillis() + def.getExpirationPeriod()));
    }
  }

  public Event(String context, String name, int level, DataTable data, Long id)
  {
    this();
    init(context, name, level, data, id);
  }

  private void init(String context, String name, int level, DataTable data, Long id)
  {
    this.context = context;
    this.name = name;
    this.level = level;
    this.data = data;
    this.id = id;
  }

  public Long getId()
  {
    return id;
  }

  public Date getInstantiationtime()
  {
    return instantiationtime;
  }

  public Date getCreationtime()
  {
    return creationtime;
  }

  public String getContext()
  {
    return context;
  }

  public String getName()
  {
    return name;
  }

  public Date getExpirationtime()
  {
    return expirationtime;
  }

  public DataTable getAcknowledgementsTable()
  {
    try
    {
      return DataTableConversion.beansToTable(acknowledgements, Acknowledgement.FORMAT, false);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public DataTable getEnrichmentsTable()
  {
    try
    {
      return DataTableConversion.beansToTable(enrichments, Enrichment.FORMAT, false);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public void setAcknowledgementsTable(DataTable data)
  {
    try
    {
      acknowledgements = DataTableConversion.beansFromTable(data, Acknowledgement.class, Acknowledgement.FORMAT, false);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public void setEnrichmentsTable(DataTable data)
  {
    try
    {
      enrichments = DataTableConversion.beansFromTable(data, Enrichment.class, Enrichment.FORMAT, false);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public void addAcknowledgement(Acknowledgement ack)
  {
    acknowledgements.add(ack);
  }

  public List<Acknowledgement> getAcknowledgements()
  {
    return acknowledgements;
  }

  public void addEnrichment(Enrichment enrichment)
  {
    enrichments.add(enrichment);
  }

  public List<Enrichment> getEnrichments()
  {
    return enrichments;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public void setCreationtime(Date creationtime)
  {
    this.creationtime = creationtime;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setContext(String context)
  {
    this.context = context;
  }

  public void setExpirationtime(Date expirationtime)
  {
    this.expirationtime = expirationtime;
  }

  public void setData(DataTable data)
  {
    this.data = data;
  }

  public void setListener(Integer listener)
  {
    this.listener = listener;
  }

  public void setLevel(int level)
  {
    this.level = level;
  }

  public void setOriginator(Object originator)
  {
    this.originator = originator;
  }

  public DataTable getData()
  {
    return data;
  }

  public Integer getListener()
  {
    return listener;
  }

  public int getLevel()
  {
    return level;
  }

  public Permissions getPermissions()
  {
    return permissions;
  }

  public void setPermissions(Permissions permissions)
  {
    this.permissions = permissions;
  }

  public Object getOriginator()
  {
    return originator;
  }

  public int getCount()
  {
    return count;
  }

  public void setCount(int count)
  {
    this.count = count;
  }

  public String getDeduplicationId()
  {
    return deduplicationId;
  }

  public void setDeduplicationId(String deduplicationId)
  {
    this.deduplicationId = deduplicationId;
  }

  public Long getSessionID()
  {
    return sessionID;
  }

  public void setSessionID(Long sessionID)
  {
    this.sessionID = sessionID;
  }

  public String getServerID()
  {
    return serverID;
  }

  public void setServerID(String serverID)
  {
    this.serverID = serverID;
  }

  @Override
  public Event clone()
  {
    try
    {
      Event clone = (Event) super.clone();
      clone.acknowledgements = (List) CloneUtils.deepClone(acknowledgements);
      clone.enrichments = (List) CloneUtils.deepClone(enrichments);
      return clone;
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Event other = (Event) obj;
    if (id == null || other.id == null)
    {
      return false;
    }
    else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public String toString()
  {
    String dataSting = (data != null ? !data.isSimple() ? data.dataAsString() : data.toString() : "no data");
    return "Event '" + name + "' in context '" + context + "' with ID " + id + ": " + dataSting + (listener != null ? ", for listener '" + listener + "'" : "");
  }
}