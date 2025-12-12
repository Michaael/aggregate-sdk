package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;

public class WebWindowLocation extends AggreGateBean
{
  public static final String F_BROWSER_TAB = "browserTab";
  
  public static final int BROWSER_TAB_CURRENT = 0;
  public static final int BROWSER_TAB_NEW = 1;
  
  public static TableFormat FORMAT = new TableFormat(1, 1);
  
  static
  {
    FieldFormat ff = FieldFormat.create("<" + F_BROWSER_TAB + "><I><A=" + BROWSER_TAB_CURRENT +
        "><D=" + Cres.get().getString("dashboardLocationBrowserTab") + "><H=" + Cres.get().getString("dashboardLocationBrowserTabHelp") + ">");
    ff.addSelectionValue(BROWSER_TAB_CURRENT, Cres.get().getString("dashboardLocationCurrentBrowserTab"));
    ff.addSelectionValue(BROWSER_TAB_NEW, Cres.get().getString("dashboardLocationNewBrowserTab"));
    FORMAT.addField(ff);
  }
  
  private Integer browserTab;
  
  public WebWindowLocation()
  {
    super(FORMAT);
  }
  
  public WebWindowLocation(DataRecord data)
  {
    super(FORMAT, data);
  }
  
  public Integer getBrowserTab()
  {
    return browserTab;
  }
  
  public void setBrowserTab(Integer browserTab)
  {
    this.browserTab = browserTab;
  }
  
  @Override
  public String toString()
  {
    return "Web Window Location [browserTab=" + browserTab + "]";
  }
}
