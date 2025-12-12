package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.ComponentLocation;

public class GenericActionCommand implements ActionCommand
{
  private String type;
  private DataTable parameters;
  private String title;
  private boolean interactive = true;
  private boolean last;
  private boolean batchEntry;
  private RequestIdentifier requestId;
  private ComponentLocation componentLocation;
  
  private TableFormat responseFormat;
  public static final String CF_COMPONENT_LOCATION = "componentLocation";
  
  /**
   * This constructor is used by specific implementations.
   */
  protected GenericActionCommand(String type, TableFormat requestFormat, TableFormat responseFormat)
  {
    this.responseFormat = responseFormat;
    setType(type);
    parameters = new SimpleDataTable(requestFormat);
  }
  
  /**
   * This constructor is used by specific implementations.
   */
  protected GenericActionCommand(String type, String title)
  {
    setType(type);
    this.title = title;
  }
  
  /**
   * This constructor is used by specific implementations.
   */
  public GenericActionCommand(String type, String title, DataTable parameters, TableFormat format)
  {
    this(type, title);
    
    if (parameters == null)
    {
      return;
    }
    
    try
    {
      DataTableConversion.populateBeanFromRecord(this, parameters.rec(), format, true);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  /*
   * Use this constructor to create a named startup action execution parameter.
   */
  public GenericActionCommand(String type, String title, DataTable parameters)
  {
    this(type, title);
    setParameters(parameters);
    setRequestId(new RequestIdentifier(type));
    setInteractive(false);
  }
  
  public void setType(String type)
  {
    if (type == null || type.length() == 0)
    {
      throw new IllegalArgumentException("Action command type is null or zero-length");
    }
    
    this.type = type;
  }
  
  public GenericActionResponse createDefaultResponse()
  {
    TableFormat format = responseFormat;
    
    if (format == null)
    {
      final GenericActionCommand command = ActionCommandRegistry.getCommand(getType());
      format = command != null ? command.getResponseFormat() : (getParameters() != null ? getParameters().getFormat() : null);
    }
    
    DataTable responseTable = format != null ? new SimpleDataTable(format, true) : null;
    
    return new GenericActionResponse(responseTable);
  }
  
  public void setParameters(DataTable parameters)
  {
    this.parameters = parameters;
  }
  
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  public void setLast(boolean last)
  {
    this.last = last;
  }
  
  public void setBatchEntry(boolean batchEntry)
  {
    this.batchEntry = batchEntry;
  }
  
  public void setRequestId(RequestIdentifier requestId)
  {
    this.requestId = requestId;
  }
  
  public String getType()
  {
    return type;
  }
  
  public DataTable getParameters()
  {
    return parameters != null ? parameters : constructParameters();
  }
  
  protected DataTable constructParameters()
  {
    return null;
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public boolean isLast()
  {
    return last;
  }
  
  public boolean isBatchEntry()
  {
    return batchEntry;
  }
  
  public RequestIdentifier getRequestId()
  {
    return requestId;
  }
  
  public boolean isResponseValid(ActionResponse actionRequest)
  {
    return true;
  }
  
  public TableFormat getResponseFormat()
  {
    return responseFormat;
  }
  
  public void setInteractive(boolean interactive)
  {
    this.interactive = interactive;
  }
  
  public boolean isInteractive()
  {
    return interactive;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (batchEntry ? 1231 : 1237);
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + (last ? 1231 : 1237);
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
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
    if (getClass() != obj.getClass())
    {
      return false;
    }
    GenericActionCommand other = (GenericActionCommand) obj;
    if (batchEntry != other.batchEntry)
    {
      return false;
    }
    if (type == null)
    {
      if (other.type != null)
      {
        return false;
      }
    }
    else if (!type.equals(other.type))
    {
      return false;
    }
    if (last != other.last)
    {
      return false;
    }
    if (parameters == null)
    {
      if (other.parameters != null)
      {
        return false;
      }
    }
    else if (!parameters.equals(other.parameters))
    {
      return false;
    }
    if (requestId == null)
    {
      if (other.requestId != null)
      {
        return false;
      }
    }
    else if (!requestId.equals(other.requestId))
    {
      return false;
    }
    if (title == null)
    {
      if (other.title != null)
      {
        return false;
      }
    }
    else if (!title.equals(other.title))
    {
      return false;
    }
    return true;
  }
  
  @Override
  public ActionCommand clone()
  {
    try
    {
      return (ActionCommand) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new AssertionError();
    }
  }
  
  @Override
  public String toString()
  {
    return "[type=" + type + ", title=" + title + ", id=" + requestId + "]";
  }

  public ComponentLocation getComponentLocation() {
    if (componentLocation != null) {
      return componentLocation;
    }

    if (parameters == null
            || !parameters.hasField(CF_COMPONENT_LOCATION)
    ) {
      return null;
    }

    componentLocation = ComponentLocation.fromDataTable(this.parameters.rec().getDataTable(GenericActionCommand.CF_COMPONENT_LOCATION));
    return componentLocation;
  }

  public void setComponentLocation(ComponentLocation componentLocation) {
    this.componentLocation = componentLocation;
  }
}
