package com.tibbo.aggregate.common.context;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ContextSortingHelper
{
  private static final int MAX_INDEX = Integer.MAX_VALUE / 2;
  
  // Step between context index values
  private static final int MULTIPLICATOR = 100;
  
  private static final List<String> CONTEXTS_ORDER = new LinkedList();
  static
  {
    CONTEXTS_ORDER.add(Contexts.CTX_DEVICES);
    CONTEXTS_ORDER.add(Contexts.CTX_DEVGROUPS);
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_USERS);
    CONTEXTS_ORDER.add(Contexts.CTX_APPLICATIONS);
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_MODELS);
    CONTEXTS_ORDER.add(Contexts.CTX_DASHBOARDS);
    CONTEXTS_ORDER.add(Contexts.CTX_ALERTS);
    CONTEXTS_ORDER.add(Contexts.CTX_FILTERS);
    CONTEXTS_ORDER.add(Contexts.CTX_CLASSES);
    CONTEXTS_ORDER.add(Contexts.CTX_QUERIES);
    CONTEXTS_ORDER.add(Contexts.CTX_JOBS);
    CONTEXTS_ORDER.add(Contexts.CTX_WORKFLOWS);
    CONTEXTS_ORDER.add(Contexts.CTX_MACHINE_LEARNING);
    CONTEXTS_ORDER.add(Contexts.CTX_CORRELATORS);
    CONTEXTS_ORDER.add(Contexts.CTX_UI_COMPONENTS);
    CONTEXTS_ORDER.add(Contexts.CTX_SCRIPTS);
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_REPORTS);
    CONTEXTS_ORDER.add(Contexts.CTX_AUTORUN);
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_PLUGINS_CONFIG);
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_PROCESS_CONTROL);
    // --------------------------------
    CONTEXTS_ORDER.add("ipsla");
    CONTEXTS_ORDER.add(Contexts.CTX_COMPLIANCE_POLICIES);
    // --------------------------------
    CONTEXTS_ORDER.add("organizations");
    CONTEXTS_ORDER.add("cardholders");
    CONTEXTS_ORDER.add("shifts");
    CONTEXTS_ORDER.add("timezones");
    CONTEXTS_ORDER.add("accessPolicies");
    CONTEXTS_ORDER.add("antiPassbackAreas");
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_DEVICESERVERS);
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_COMMON_DATA);
    CONTEXTS_ORDER.add(Contexts.CTX_WIDGETS);
    CONTEXTS_ORDER.add(Contexts.CTX_TRACKERS);
    CONTEXTS_ORDER.add(Contexts.CTX_FAVOURITES);
    // --------------------------------
    // --------------------------------
    CONTEXTS_ORDER.add(Contexts.CTX_ADMINISTRATION);
    CONTEXTS_ORDER.add(Contexts.CTX_CONFIG);
    CONTEXTS_ORDER.add(Contexts.CTX_DEBUG);
    CONTEXTS_ORDER.add(Contexts.CTX_EVENTS);
    CONTEXTS_ORDER.add(Contexts.CTX_SCHEDULER);
    CONTEXTS_ORDER.add(Contexts.CTX_UTILITIES);
  }
  
  public static Integer getIndex(String context)
  {
    if (!CONTEXTS_ORDER.contains(context))
    {
      final String groupsSuffix = ContextUtils.groupsContextName("");
      if (context.endsWith(groupsSuffix))
        return getIndex(context.replace(groupsSuffix, ""));
      
      return null;
    }
    
    return MAX_INDEX - (CONTEXTS_ORDER.indexOf(context) * MULTIPLICATOR);
  }
  
  public static List<String> getOrderedContextList()
  {
    return Collections.unmodifiableList(CONTEXTS_ORDER);
  }
}
