package com.tibbo.aggregate.common.widget.runtime;

import java.awt.*;
import java.io.*;
import java.util.concurrent.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.util.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.engine.*;

public interface SwingWidgetEngine
{
  WidgetTemplate getWidget();
  
  WidgetViewer getViewer();
  
  ContextManager getServerContextManager();
  
  String getSubwidgetTemplateString(Context subWidgetContext) throws ContextException, ParserConfigurationException, SAXException, IOException;
  
  Context getDefaultServerContext();
  
  Context getWidgetContext();
  
  CallerController getCallerController();
  
  boolean isWebWidget();
  
  WidgetBindingProcessor getBindingProcessor();
  
  RemoteConnector getConnector();
  
  ReferredActionExecutor getActionExecutor();
  
  Window getMainFrame();
  
  ExecutorService getExecutorService();
  
  void resetMainComponent();
  
  void addAfterEngineStopped(Runnable runnable);
  
}
