package com.tibbo.aggregate.common.action.command;

import java.net.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class Browse extends GenericActionCommand
{
  public static final String CF_BROWSE_URI = "uri";
  
  public static final TableFormat CFT_BROWSE = FieldFormat.create("<" + CF_BROWSE_URI + "><S>").setDescription(Cres.get().getString("url")).wrap();
  
  private URI url;
  
  public Browse()
  {
    super(ActionUtils.CMD_BROWSE, CFT_BROWSE, null);
  }
  
  public Browse(URI url)
  {
    super(ActionUtils.CMD_BROWSE, (String) null);
    this.url = url;
  }
  
  public Browse(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_BROWSE, title, parameters);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_BROWSE, new Object[] { url.toString() });
  }
  
  public URI getUrl()
  {
    return url;
  }
  
  public void setUrl(URI url)
  {
    this.url = url;
  }
  
}
