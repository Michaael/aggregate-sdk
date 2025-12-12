package com.tibbo.aggregate.common.action.command;

import java.util.*;
import java.util.regex.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public class ShowHtmlSnippet extends GenericActionCommand
{
  public static final Pattern EXPRESSION_PATTERN = Pattern.compile("<e>(.|\\n)*?</e>");
  
  public static final int TYPE_FRAME = 0;
  public static final int TYPE_EXPRESSION = 1;
  public static final int TYPE_HTML = 2;
  
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_KEY = "key";
  public static final String CF_TYPE = "snippetType";
  public static final String CF_URL = "url";
  public static final String CF_EXPRESSION = "expression";
  public static final String CF_HTML = "html";
  public static final String CF_CHECK_HTML_VALIDITY = "checkHtmlValidity";
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  public static final String CF_RESOURCE_BUNDLE = "resourceBundle";
  
  public static final TableFormat CFT_SHOW_HTML_SNIPPET = new TableFormat(1, 1);
  
  static
  {
    FieldFormat ff = FieldFormat.create(CF_TYPE, FieldFormat.INTEGER_FIELD, Cres.get().getString("wHtmlSnippetType"));
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    ff.setSelectionValues(snippetTypes());
    
    ff = FieldFormat.create(CF_URL, FieldFormat.STRING_FIELD, Cres.get().getString("wURL"));
    ff.setNullable(true);
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    ff = FieldFormat.create("<" + CF_LOCATION + "><T><F=N>");
    ff.setDefault(new WindowLocation().toDataTable());
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    ff = FieldFormat.create("<" + CF_DASHBOARD + "><T><F=N>");
    ff.setDefault(new DashboardProperties().toDataTable());
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    CFT_SHOW_HTML_SNIPPET.addField("<" + CF_KEY + "><S><F=NH><D=" + Cres.get().getString("key") + ">");
    
    ff = FieldFormat.create("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=N>");
    ff.setDefault(new DashboardsHierarchyInfo().toDataTable());
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    ff = FieldFormat.create(CF_EXPRESSION, FieldFormat.STRING_FIELD, Cres.get().getString("wHtmlSnippetExpression"));
    ff.setNullable(true);
    ff.setEditor(StringFieldFormat.EDITOR_EXPRESSION);
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    ff = FieldFormat.create(CF_HTML, FieldFormat.STRING_FIELD, Cres.get().getString("wHtmlSnippetHtml"));
    ff.setNullable(true);
    ff.setEditor(StringFieldFormat.EDITOR_TEXT);
    ff.setEditorOptions(StringFieldFormat.TEXT_EDITOR_MODE_HTML);
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    ff = FieldFormat.create("<" + CF_RESOURCE_BUNDLE + "><S><F=NRH>");
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    ff = FieldFormat.create(CF_CHECK_HTML_VALIDITY, FieldFormat.BOOLEAN_FIELD, Cres.get().getString("wHtmlSnippetCheckHtmlValidity"), true);
    CFT_SHOW_HTML_SNIPPET.addField(ff);
    
    CFT_SHOW_HTML_SNIPPET.addBinding(CF_URL + '#' + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + CF_TYPE + "} != " + TYPE_FRAME);
    CFT_SHOW_HTML_SNIPPET.addBinding(CF_EXPRESSION + '#' + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + CF_TYPE + "} != " + TYPE_EXPRESSION);
    CFT_SHOW_HTML_SNIPPET.addBinding(CF_HTML + '#' + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + CF_TYPE + "} != " + TYPE_HTML);
    CFT_SHOW_HTML_SNIPPET.addBinding(CF_CHECK_HTML_VALIDITY + '#' + DataTableBindingProvider.PROPERTY_HIDDEN, "{" + CF_TYPE + "} != " + TYPE_HTML);
    
  }
  
  private static Map snippetTypes()
  {
    final HashMap hashMap = new HashMap();
    hashMap.put(TYPE_FRAME, Cres.get().getString("wHtmlSnippetTypeFrame"));
    hashMap.put(TYPE_EXPRESSION, Cres.get().getString("wHtmlSnippetTypeExpression"));
    hashMap.put(TYPE_HTML, Cres.get().getString("wHtmlSnippetTypeHtml"));
    return hashMap;
  }
  
  private WindowLocation location;
  private DashboardProperties dashboard;
  private String key;
  private String url;
  private Integer snippetType;
  private String html;
  private Boolean checkHtmlValidity;
  private String expression;
  private DashboardsHierarchyInfo dhInfo;
  private String resourceBundle;
  
  public String getResourceBundle()
  {
    return resourceBundle;
  }
  
  public void setResourceBundle(String resourceBundle)
  {
    this.resourceBundle = resourceBundle;
  }
  
  public ShowHtmlSnippet()
  {
    super(ActionUtils.CMD_SHOW_HTML_SNIPPET, CFT_SHOW_HTML_SNIPPET, null);
  }
  
  public ShowHtmlSnippet(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_HTML_SNIPPET, title, parameters, CFT_SHOW_HTML_SNIPPET);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    DataRecord rec = new DataRecord(CFT_SHOW_HTML_SNIPPET);
    rec.setValue(CF_TYPE, getSnippetType());
    rec.setValue(CF_URL, getUrl());
    rec.setValue(CF_LOCATION, getLocation() != null ? getLocation().toDataTable() : null);
    rec.setValue(CF_DASHBOARD, getDashboard() != null ? getDashboard().toDataTable() : null);
    rec.setValue(CF_KEY, getKey());
    rec.setValue(CF_DASHBOARDS_HIERARCHY_INFO, getDashboardsHierarchyInfo() != null ? getDashboardsHierarchyInfo().toDataTable() : null);
    rec.setValue(CF_HTML, getHtml());
    rec.setValue(CF_CHECK_HTML_VALIDITY, getCheckHtmlValidity());
    rec.setValue(CF_EXPRESSION, getExpression());
    rec.setValue(CF_RESOURCE_BUNDLE, getResourceBundle());
    return rec.wrap();
  }
  
  public WindowLocation getLocation()
  {
    return location;
  }
  
  public void setLocation(WindowLocation location)
  {
    this.location = location;
  }
  
  public DashboardProperties getDashboard()
  {
    return dashboard;
  }
  
  public void setDashboard(DashboardProperties dashboard)
  {
    this.dashboard = dashboard;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public String getUrl()
  {
    return url;
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  public DashboardsHierarchyInfo getDashboardsHierarchyInfo()
  {
    return dhInfo;
  }
  
  public void setDashboardsHierarchyInfo(DashboardsHierarchyInfo dhInfo)
  {
    this.dhInfo = dhInfo;
  }
  
  public Integer getSnippetType()
  {
    return snippetType;
  }
  
  public void setSnippetType(Integer snippetType)
  {
    this.snippetType = snippetType;
  }
  
  public String getHtml()
  {
    return html;
  }
  
  public void setHtml(String html)
  {
    this.html = html;
  }
  
  public String getExpression()
  {
    return expression;
  }
  
  public void setExpression(String expression)
  {
    this.expression = expression;
  }
  
  public Boolean getCheckHtmlValidity()
  {
    return checkHtmlValidity;
  }
  
  public void setCheckHtmlValidity(Boolean checkHtmlValidity)
  {
    this.checkHtmlValidity = checkHtmlValidity;
  }
}
