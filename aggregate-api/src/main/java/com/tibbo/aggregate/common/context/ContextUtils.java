package com.tibbo.aggregate.common.context;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.tibbo.aggregate.common.action.ActionDefinition;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.util.StringUtils;

public class ContextUtils
{
  public static final String CONTEXT_NAME_PATTERN = "\\w*";
  public static final String CONTEXT_PATH_PATTERN = "[\\w|\\.]+";
  public static final String CONTEXT_MASK_PATTERN = "[\\w|\\.|\\*]*";
  public static final String CONTEXT_TYPE_PATTERN = "[\\w|\\.]+";
  public static final String ENTITY_NAME_PATTERN = "\\w+";
  public static final String IDENTIFIER_PATTERN = "\\w*";
  
  private static final String CONTEXT_CLASS_SUFFIX = "Context";
  
  public final static String CONTEXT_NAME_SEPARATOR = ".";
  public final static String CONTEXT_TYPE_SEPARATOR = ".";
  public final static String CONTEXT_GROUP_MASK = "*";
  public final static String ENTITY_GROUP_MASK = "*";
  public final static String CONTEXT_TYPE_ANY = "*";
  public final static String ENTITY_ANY = "";
  public final static String ENTITY_GROUP_SEPARATOR = "|";
  public final static String MASK_LIST_SEPARATOR = " ";
  
  public final static String GROUP_DEFAULT = "default";
  public final static String GROUP_SYSTEM = "system";
  public final static String GROUP_REMOTE = "remote";
  public final static String GROUP_CUSTOM = "custom";
  public static final String GROUP_STATUS = "status";
  public static final String GROUP_CONTEXT_DATA = "contextData";
  public static final String GROUP_ACCESS = "access";
  
  public static final int ENTITY_ANY_TYPE = 0;
  public static final int ENTITY_VARIABLE = 1;
  public static final int ENTITY_FUNCTION = 2;
  public static final int ENTITY_EVENT = 4;
  public static final int ENTITY_ACTION = 8;
  public static final int ENTITY_INSTANCE = 100;
  
  public static final int ENTITY_GROUP_SHIFT = 200;
  public static final int ENTITY_VARIABLE_GROUP = ENTITY_VARIABLE + ENTITY_GROUP_SHIFT; // 201
  public static final int ENTITY_FUNCTION_GROUP = ENTITY_FUNCTION + ENTITY_GROUP_SHIFT; // 202
  public static final int ENTITY_EVENT_GROUP = ENTITY_EVENT + ENTITY_GROUP_SHIFT; // 204
  public static final int ENTITY_ACTION_GROUP = ENTITY_ACTION + ENTITY_GROUP_SHIFT; // 208
  
  public final static String USERNAME_PATTERN = "%";
  
  public final static String VARIABLES_GROUP_DS_SETTINGS = "ds_settings";
  
  public static final String ENTITY_GROUP_SUFFIX = ".*";
  
  public static final String SRV_MORE_CONTEXT = "srvMoreContext";
  
  public static final Set<String> RESERVED_CONTEXT_NAMES = new HashSet<>();
  
  private static final Pattern MASK_ENDED_BY_DOT_AND_STAR_PATTERN = Pattern.compile("(.*)\\.\\*$");
  
  {
    RESERVED_CONTEXT_NAMES.add(SRV_MORE_CONTEXT);
  }
  
  public static final Set<String> CONTEXT_TYPES = new TreeSet<String>();
  
  public static String userContextPath(String username)
  {
    return createName(Contexts.CTX_USERS, username);
  }
  
  public static String deviceServersContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DEVICESERVERS);
  }
  
  public static String dsGroupsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DSGROUPS);
  }
  
  public static String dsGroupContextPath(String owner, String name)
  {
    return createName(dsGroupsContextPath(owner), name);
  }
  
  public static String deviceGroupsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DEVGROUPS);
  }
  
  public static String deviceGroupContextPath(String owner, String name)
  {
    return createName(deviceGroupsContextPath(owner), name);
  }
  
  public static String groupContextPath(String username, String containerContextName, String name)
  {
    return createName(groupsContextPath(username, containerContextName), name);
  }
  
  public static String groupsContextPath(String username, String containerContextName)
  {
    return createName(userContextPath(username), groupsContextName(containerContextName));
  }
  
  public static String groupsContextName(String containerContextName)
  {
    return containerContextName + "_" + Contexts.CTX_GROUPS;
  }
  
  public static String alertContextPath(String owner, String name)
  {
    return createName(alertsContextPath(owner), name);
  }
  
  public static String alertsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_ALERTS);
  }
  
  public static String jobsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_JOBS);
  }
  
  public static String jobContextPath(String owner, String name)
  {
    return createName(jobsContextPath(owner), name);
  }
  
  public static String queriesContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_QUERIES);
  }
  
  public static String queryContextPath(String owner, String name)
  {
    return createName(queriesContextPath(owner), name);
  }
  
  public static String compliancePoliciesContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_COMPLIANCE_POLICIES);
  }
  
  public static String compliancePolicyContextPath(String owner, String name)
  {
    return createName(compliancePoliciesContextPath(owner), name);
  }
  
  public static String reportsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_REPORTS);
  }
  
  public static String reportContextPath(String owner, String name)
  {
    return createName(reportsContextPath(owner), name);
  }
  
  public static String trackersContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_TRACKERS);
  }
  
  public static String trackerContextPath(String owner, String name)
  {
    return createName(trackersContextPath(owner), name);
  }
  
  public static String commonDataContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_COMMON_DATA);
  }
  
  public static String commonTableContextPath(String owner, String name)
  {
    return createName(commonDataContextPath(owner), name);
  }
  
  public static String eventFiltersContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_FILTERS);
  }
  
  public static String eventFilterContextPath(String owner, String name)
  {
    return createName(eventFiltersContextPath(owner), name);
  }
  
  public static String widgetContextPath(String owner, String name)
  {
    return createName(widgetsContextPath(owner), name);
  }
  
  public static String widgetsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_WIDGETS);
  }
  
  public static String processControlContextPath(String owner, String name)
  {
    return createName(processesControlContextPath(owner), name);
  }
  
  public static String processesControlContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_PROCESS_CONTROL);
  }
  
  public static String machineLearningContextPath(String owner, String name)
  {
    return createName(machineLearningContextPath(owner), name);
  }
  
  public static String machineLearningContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_MACHINE_LEARNING);
  }
  
  public static String dashboardsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DASHBOARDS);
  }
  
  public static String dashboardContextPath(String owner, String name)
  {
    return createName(dashboardsContextPath(owner), name);
  }
  
  public static String autorunActionsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_AUTORUN);
  }
  
  public static String autorunActionContextName(String owner, String name)
  {
    return createName(autorunActionsContextPath(owner), name);
  }
  
  public static String favouritesContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_FAVOURITES);
  }
  
  public static String favouriteContextPath(String owner, String name)
  {
    return createName(favouritesContextPath(owner), name);
  }
  
  public static String scriptContextPath(String owner, String name)
  {
    return createName(scriptsContextPath(owner), name);
  }
  
  public static String scriptsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_SCRIPTS);
  }
  
  public static String modelsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_MODELS);
  }
  
  public static String modelContextPath(String owner, String name)
  {
    return createName(modelsContextPath(owner), name);
  }
  
  public static String eventCorrelatorsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_CORRELATORS);
  }
  
  public static String eventCorrelatorContextPath(String owner, String name)
  {
    return createName(eventCorrelatorsContextPath(owner), name);
  }
  
  public static String classesContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_CLASSES);
  }
  
  public static String classContextPath(String owner, String name)
  {
    return createName(classesContextPath(owner), name);
  }
  
  public static String workflowsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_WORKFLOWS);
  }
  
  public static String workflowContextPath(String owner, String name)
  {
    return createName(workflowsContextPath(owner), name);
  }
  
  public static String deviceServerContextPath(String owner, String name)
  {
    return createName(deviceServersContextPath(owner), name);
  }
  
  public static String pluginGlobalConfigContextPath(String pluginId)
  {
    return createName(Contexts.CTX_PLUGINS_CONFIG, pluginIdToContextName(pluginId));
  }
  
  public static String pluginsUserConfigContextPath(String username)
  {
    return createName(userContextPath(username), Contexts.CTX_PLUGINS_CONFIG);
  }
  
  public static String pluginUserConfigContextPath(String username, String pluginId)
  {
    return createName(userContextPath(username), Contexts.CTX_PLUGINS_CONFIG, pluginIdToContextName(pluginId));
  }
  
  public static String pluginConfigContextPath(String owner, String name)
  {
    return createName(deviceServersContextPath(owner), name, Contexts.CTX_PLUGIN_CONFIG);
  }
  
  public static String devicesContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DEVICES);
  }
  
  public static String deviceContextPath(String owner, String device)
  {
    return createName(devicesContextPath(owner), device);
  }

  public static String uiComponentContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_UI_COMPONENTS);
  }

  public static String uiComponentContextPath(String owner, String device)
  {
    return createName(uiComponentContextPath(owner), device);
  }
  
  public static String applicationContextPath(String owner, String name)
  {
    return createName(applicationsContextPath(owner), name);
  }
  
  public static String applicationsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_APPLICATIONS);
  }
  
  public static String getAggregationContainer(String path)
  {
    // users\\.[\\w]+\\.[\\w]+
    Pattern pattern = Pattern.compile(Contexts.CTX_USERS + "\\" + CONTEXT_NAME_SEPARATOR + "[\\w]+"
        + "\\" + CONTEXT_NAME_SEPARATOR + "[\\w]+");
    
    Matcher matcher = pattern.matcher(path);
    if (matcher.find())
      return matcher.group();
    return null;
  }
  
  public static String replaceUsernameToPattern(String path)
  {
    String regex = Contexts.CTX_USERS + "\\" + CONTEXT_NAME_SEPARATOR + "[\\w]+";
    
    String replacement = Contexts.CTX_USERS + CONTEXT_NAME_SEPARATOR + USERNAME_PATTERN;
    return path.replaceFirst(regex, replacement);
  }
  
  public static String removeContextNameFromPath(String path)
  {
    return path.replaceFirst("\\" + CONTEXT_NAME_SEPARATOR + "[\\w]+$", "");
  }
  
  public static String createName(String... parts)
  {
    StringBuffer res = new StringBuffer();
    
    for (int i = 0; i < parts.length; i++)
    {
      if (i > 0)
      {
        res.append(CONTEXT_NAME_SEPARATOR);
      }
      
      res.append(parts[i]);
    }
    
    return res.toString();
  }
  
  public static String createGroup(String... parts)
  {
    StringBuffer res = new StringBuffer();
    
    for (int i = 0; i < parts.length; i++)
    {
      if (i == parts.length - 1 && parts[i] == null)
      {
        break;
      }
      
      if (i > 0)
      {
        res.append(ENTITY_GROUP_SEPARATOR);
      }
      
      res.append(parts[i]);
    }
    
    return res.toString();
  }
  
  public static String pluginIdToContextName(String pluginId)
  {
    return pluginId.replace(".", "_").replace("-", "");
  }
  
  public static String getChildFullName(String parent, String childShortName)
  {
    if (parent.equals(Contexts.CTX_ROOT))
    {
      return childShortName;
    }
    else
    {
      return parent + CONTEXT_NAME_SEPARATOR + childShortName;
    }
  }
  
  public static String contextPathToContextName(String path)
  {
    return path.replace(CONTEXT_NAME_SEPARATOR.charAt(0), '_'); // "_".charAt(0));
  }
  
  public static List<Context> expandMaskListToContexts(String masks, ContextManager contextManager)
  {
    return expandMaskListToContexts(masks, contextManager, null, false);
  }
  
  public static List<Context> expandMaskListToContexts(String masks, ContextManager contextManager, CallerController caller)
  {
    return expandMaskListToContexts(masks, contextManager, caller, false);
  }
  
  public static List<Context> expandMaskListToContexts(String masks, ContextManager contextManager, CallerController caller, boolean useVisibleChildren)
  {
    List<Context> result = new LinkedList();
    
    List<String> maskList = StringUtils.split(masks, MASK_LIST_SEPARATOR.charAt(0));
    
    for (String mask : maskList)
    {
      List<Context> contexts = expandMaskToContexts(mask, contextManager, caller, useVisibleChildren);
      
      result.addAll(contexts);
    }
    
    return result;
  }
  
  public static List<Context> expandMaskToContexts(String mask, ContextManager contextManager)
  {
    return expandMaskToContexts(mask, contextManager, null, false);
  }
  
  public static List<Context> expandMaskToContexts(String mask, ContextManager contextManager, CallerController caller)
  {
    return expandMaskToContexts(mask, contextManager, caller, false);
  }
  
  public static List<Context> expandMaskToContexts(String mask, ContextManager contextManager, CallerController caller, boolean useVisibleChildren)
  {
    List<Context> res = new LinkedList();
    
    List<String> paths = expandMaskToPaths(mask, contextManager, caller, useVisibleChildren);
    
    for (String path : paths)
    {
      Context con = contextManager.get(path, caller);
      if (con != null)
      {
        res.add(con);
      }
    }
    
    return res;
  }
  
  public static List<String> expandMaskToPaths(String mask, ContextManager contextManager)
  {
    return expandMaskToPaths(mask, contextManager, null, false);
  }
  
  public static List<String> expandMaskToPaths(String mask, ContextManager contextManager, CallerController caller)
  {
    return expandMaskToPaths(mask, contextManager, caller, false);
  }
  
  public static List<String> expandMaskToPaths(String mask, ContextManager contextManager, CallerController caller, boolean useVisibleChildren)
  {
    if (mask == null)
    {
      return emptyList();
    }
    
    List<String> result = new LinkedList();
    
    List<String> parts = StringUtils.split(mask, CONTEXT_NAME_SEPARATOR.charAt(0));
    
    for (int i = 0; i < parts.size(); i++)
    {
      if (parts.get(i).equals(CONTEXT_GROUP_MASK))
      {
        StringBuffer head = new StringBuffer();
        
        for (int j = 0; j < i; j++)
        {
          if (j > 0)
          {
            head.append(CONTEXT_NAME_SEPARATOR);
          }
          head.append(parts.get(j));
        }
        
        StringBuffer tail = new StringBuffer();
        
        for (int j = i + 1; j < parts.size(); j++)
        {
          tail.append(CONTEXT_NAME_SEPARATOR);
          tail.append(parts.get(j));
        }
        
        List<String> res = expandMaskPart(head.toString(), tail.toString(), contextManager, caller, useVisibleChildren);
        result.addAll(res);
        return result;
      }
    }
    
    if (contextManager.get(mask, caller) != null)
    {
      result.add(mask);
    }
    
    return result;
  }
  
  private static List<String> expandMaskPart(String head, String tail, ContextManager contextManager, CallerController caller, boolean useVisibleChildren)
  {
    // logger.debug("Expanding context mask part '" + head + " * " + tail + "'");
    
    List<String> result = new LinkedList<>();
    
    Context con = contextManager.get(head, caller);
    
    if (con == null)
    {
      return result;
    }
    
    if (con.isMapped())
    {
      final List<Context> mappedChildren = con.getMappedChildren(caller);
      for (Context child : mappedChildren)
      {
        result.add(child.getPath());
      }
    }
    else
    {
      List<Context> children = useVisibleChildren ? con.getVisibleChildren(caller) : con.getChildren(caller);
      for (Context child : children)
      {
        if (useVisibleChildren)
        {
          Context realChild = con.getChild(child.getName());
          
          if (realChild == null || !realChild.getPath().equals(child.getPath()))
          {
            List<String> res = expandMaskToPaths(child.getPath() + tail, contextManager, caller, useVisibleChildren);
            result.addAll(res);
            continue;
          }
        }
        
        result.addAll(expandMaskToPaths(head + CONTEXT_NAME_SEPARATOR + child.getName() + tail, contextManager, caller, useVisibleChildren));
      }
    }
    
    return result;
  }
  
  public static List<Context> findChildren(String rootsMask, final Class contextClass, ContextManager manager, CallerController caller, boolean resolveGroups)
  {
    return findChildren(rootsMask, getTypeForClass(contextClass), manager, caller, resolveGroups);
  }
  
  public static List<Context> findChildren(String rootsMask, final String type, ContextManager manager, CallerController caller, boolean resolveGroups)
  {
    final List<Context> res = new ArrayList();
    
    ContextVisitor visitor = new DefaultContextVisitor()
    {
      @Override
      public void visit(Context context)
      {
        if (isDerivedFrom(context.getType(), type))
        {
          res.add(context);
        }
      }
    };
    
    List<Context> roots = expandMaskToContexts(rootsMask, manager, caller);
    
    for (Context root : roots)
    {
      try
      {
        acceptFinder(root, visitor, caller, resolveGroups);
      }
      catch (Throwable ex)
      {
        throw new ContextRuntimeException(ex.getMessage(), ex);
      }
    }
    
    return res;
  }
  
  private static void acceptFinder(Context context, ContextVisitor visitor, CallerController caller, boolean resolveGroups) throws ContextException
  {
    visitor.visit(context);
    
    List<Context> children = resolveGroups ? context.getMappedChildren(caller) : context.getChildren(caller);
    for (Context child : children)
    {
      acceptFinder(child, visitor, caller, resolveGroups);
    }
  }
  
  public static boolean matchesToMask(String mask, String name)
  {
    return matchesToMask(mask, name, false, false);
  }
  
  public static boolean matchesToType(String type, Collection<String> requiredTypes)
  {
    if (requiredTypes == null || requiredTypes.isEmpty() || type == null)
    {
      return true;
    }
    
    if (requiredTypes.contains(CONTEXT_TYPE_ANY))
    {
      return true;
    }
    
    for (String contextType : requiredTypes)
    {
      if (isDerivedFrom(type, contextType))
      {
        return true;
      }
    }
    
    return false;
  }
  
  public static boolean matchesToType(Collection<String> types, Collection<String> requiredTypes)
  {
    boolean result = true;
    
    for (String type : types)
    {
      result &= matchesToType(type, requiredTypes);
    }
    
    return result;
  }
  
  public static boolean matchesToMaskWithGroups(String mask, String context, boolean contextMayExtendMask, boolean maskMayExtendContext, ContextManager contextManager)
  {
    if (!matchesToMask(mask, context, contextMayExtendMask, maskMayExtendContext))
    {
      // Если у нас звёздочка в конце
      
      Matcher matcher = MASK_ENDED_BY_DOT_AND_STAR_PATTERN.matcher(mask);
      if (matcher.matches())
      {
        if (CharMatcher.is(CONTEXT_GROUP_MASK.charAt(0)).countIn(mask) == 1)
        {
          String group = matcher.group(1);
          
          Context con = contextManager.get(group, contextManager.getCallerController());
          
          if (con == null)
          {
            return false;
          }
          
          if (con.isMapped())
          {
            return con.hasMappedChild(context, contextManager.getCallerController());
          }
          else
          {
            return false;
          }
        }
        else
        {
          List<String> paths = ContextUtils.expandMaskToPaths(mask, contextManager, contextManager.getCallerController());
          return paths.contains(context);
        }
      }
      else
      {
        return false;
      }
    }
    return true;
  }
  
  public static boolean matchesToMask(String mask, String context, boolean contextMayExtendMask, boolean maskMayExtendContext)
  {
    if (mask == null || context == null)
    {
      return true;
    }
    
    if (!isMask(mask))
    {
      if (contextMayExtendMask && maskMayExtendContext)
      {
        int length = Math.min(mask.length(), context.length());
        return mask.substring(0, length).equals(context.substring(0, length));
      }
      else
      {
        boolean equals = mask.equals(context);
        
        if (maskMayExtendContext)
        {
          return equals || (mask.length() > context.length() && mask.startsWith(context) && mask.charAt(context.length()) == CONTEXT_NAME_SEPARATOR.charAt(0));
        }
        else if (contextMayExtendMask)
        {
          return equals || (context.length() > mask.length() && context.startsWith(mask) && context.charAt(mask.length()) == CONTEXT_NAME_SEPARATOR.charAt(0));
        }
        else
        {
          return equals;
        }
      }
    }
    
    List<String> maskParts = StringUtils.split(mask, CONTEXT_NAME_SEPARATOR.charAt(0));
    List<String> nameParts = StringUtils.split(context, CONTEXT_NAME_SEPARATOR.charAt(0));
    
    if (maskParts.size() > nameParts.size() && !maskMayExtendContext)
    {
      return false;
    }
    
    if (maskParts.size() < nameParts.size() && !contextMayExtendMask)
    {
      return false;
    }
    
    if (straightMatching(maskParts, nameParts))
    {
      return true;
    }
    return false;
  }
  
  private static boolean straightMatching(List<String> maskParts, List<String> nameParts)
  {
    for (int i = 0; i < Math.min(maskParts.size(), nameParts.size()); i++)
    {
      if (maskParts.get(i).equals(CONTEXT_GROUP_MASK) && !nameParts.get(i).equals(CONTEXT_GROUP_MASK))
      {
        continue;
      }
      else
      {
        if (!maskParts.get(i).equals(nameParts.get(i)))
        {
          return false;
        }
      }
    }
    return true;
  }
  
  public static boolean masksIntersect(String mask1, String mask2, boolean mask2MayExtendMask1, boolean mask1MayExtendMask2)
  {
    List<String> mask1Parts = StringUtils.split(mask1, CONTEXT_NAME_SEPARATOR.charAt(0));
    List<String> mask2Parts = StringUtils.split(mask2, CONTEXT_NAME_SEPARATOR.charAt(0));
    
    if (mask1Parts.size() > mask2Parts.size() && !mask1MayExtendMask2)
    {
      return false;
    }
    
    if (mask1Parts.size() < mask2Parts.size() && !mask2MayExtendMask1)
    {
      return false;
    }
    
    for (int i = 0; i < Math.min(mask1Parts.size(), mask2Parts.size()); i++)
    {
      if (mask1Parts.get(i).equals(CONTEXT_GROUP_MASK))
      {
        continue;
      }
      else if (mask2Parts.get(i).equals(CONTEXT_GROUP_MASK))
      {
        continue;
      }
      else
      {
        if (!mask1Parts.get(i).equals(mask2Parts.get(i)))
        {
          return false;
        }
      }
    }
    
    return true;
  }
  
  public static boolean isRelative(String name)
  {
    return name.startsWith(CONTEXT_NAME_SEPARATOR);
  }
  
  public static boolean isMask(String name)
  {
    if (name == null)
    {
      return false;
    }
    return name.indexOf(CONTEXT_GROUP_MASK.charAt(0)) > -1;
  }
  
  public static boolean isValidContextType(String s)
  {
    return CONTEXT_TYPE_ANY.equals(s) || Pattern.matches(CONTEXT_TYPE_PATTERN, s);
  }
  
  public static boolean isValidContextName(String s)
  {
    if (s == null)
    {
      return false;
    }
    
    return Pattern.matches(CONTEXT_NAME_PATTERN, s) && !RESERVED_CONTEXT_NAMES.contains(s);
  }
  
  public static boolean isValidContextMask(String s)
  {
    if (s == null)
    {
      return false;
    }
    
    return Pattern.matches(CONTEXT_MASK_PATTERN, s);
  }
  
  public static boolean isValidIdentifier(String s)
  {
    if (s == null)
    {
      return false;
    }
    
    return Pattern.matches(IDENTIFIER_PATTERN, s);
  }
  
  public static boolean isDerivedFrom(String childType, String parentType)
  {
    StringTokenizer pst = new StringTokenizer(parentType, CONTEXT_TYPE_SEPARATOR);
    StringTokenizer cst = new StringTokenizer(childType, CONTEXT_TYPE_SEPARATOR);
    
    if (cst.countTokens() < pst.countTokens())
    {
      return false;
    }
    
    while (pst.hasMoreTokens())
    {
      if (!pst.nextToken().equals(cst.nextToken()))
      {
        return false;
      }
    }
    
    return true;
  }
  
  public static String getParentPath(String path)
  {
    if (isRelative(path))
    {
      throw new IllegalArgumentException("Cannot find parent of a relative path: " + path);
    }
    
    int index = path.lastIndexOf(CONTEXT_NAME_SEPARATOR);
    
    return index != -1 ? path.substring(0, index) : null;
  }
  
  public static String getContextName(String path)
  {
    if (isRelative(path))
    {
      throw new IllegalArgumentException("Cannot find parent of a relative path: " + path);
    }
    
    int index = path.lastIndexOf(CONTEXT_NAME_SEPARATOR);
    
    return index != -1 ? path.substring(index + 1) : null;
  }
  
  /**
   * Returns base group name. Useful for composite group names that contain several group names delimited with group separator symbol.
   */
  public static String getBaseGroup(String group)
  {
    if (group == null)
    {
      return null;
    }
    
    int index = group.indexOf(ENTITY_GROUP_SEPARATOR.charAt(0));
    return index == -1 ? group : group.substring(0, index);
  }

  public static String getActualGroup(String group)
  {
    if (group == null)
    {
      return null;
    }

    int index = group.lastIndexOf(ENTITY_GROUP_SEPARATOR.charAt(0));
    return index == -1 ? group : group.substring(index + 1);
  }

  public static String getVisualGroup(String group)
  {
    if (group == null)
    {
      return null;
    }
    
    int index = group.indexOf(ENTITY_GROUP_SEPARATOR.charAt(0));
    return index == -1 ? null : group.substring(index + 1);
  }
  
  public static String getBaseType(String type)
  {
    StringTokenizer st = new StringTokenizer(type, CONTEXT_TYPE_SEPARATOR);
    return st.nextToken();
  }
  
  public static String getSubtype(String type)
  {
    if (type == null)
    {
      return null;
    }
    
    int index = type.lastIndexOf(CONTEXT_TYPE_SEPARATOR.charAt(0));
    return index == -1 ? null : type.substring(index + 1, type.length());
  }
  
  public static String getTypeForClass(Class clazz)
  {
    String name = clazz.getSimpleName();
    return getTypeForClassSimpleName(name);
  }
  
  public static String getTypeForClassSimpleName(String name)
  {
    if (name.length() == 0)
    {
      return name; // Testing environment only
    }

    name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1, name.length());
    if (name.endsWith(CONTEXT_CLASS_SUFFIX))
    {
      name = name.substring(0, name.length() - CONTEXT_CLASS_SUFFIX.length());
    }
    return name;
  }
  
  public static Map<String, String> getEventFields(String mask, String event, CallerController caller, ContextManager contextManager)
  {
    List<Context> contexts = expandMaskToContexts(mask, contextManager, caller);
    
    Map<String, String> fields = new LinkedHashMap();
    
    for (Context con : contexts)
    {
      EventData edata = con.getEventData(event);
      if (edata != null)
      {
        TableFormat rf = edata.getDefinition().getFormat();
        if (rf != null)
        {
          for (FieldFormat ff : rf)
          {
            fields.put(ff.getName(), ff.toString());
          }
        }
      }
    }
    
    return fields;
  }
  
  public static Map<String, String> getVariableFields(String mask, String variable, CallerController caller, ContextManager contextManager)
  {
    List<Context> contexts = expandMaskToContexts(mask, contextManager, caller);
    
    Map<String, String> fields = new LinkedHashMap();
    
    for (Context con : contexts)
    {
      VariableDefinition vd = con.getVariableDefinition(variable);
      if (vd != null)
      {
        TableFormat rf = vd.getFormat();
        if (rf != null)
        {
          for (FieldFormat ff : rf)
          {
            if (ff.isHidden())
            {
              continue;
            }
            fields.put(ff.getName(), ff.toString());
          }
        }
      }
    }
    return fields;
  }
  
  public static ActionDefinition getDefaultActionDefinition(Context context, CallerController caller)
  {
    List<ActionDefinition> actions = context.getActionDefinitions(caller);
    
    for (ActionDefinition def : actions)
    {
      if (def.isDefault())
      {
        return def;
      }
    }
    
    return null;
  }
  
  public static String createType(Class clazz, String deviceType)
  {
    return getTypeForClass(clazz) + CONTEXT_TYPE_SEPARATOR + deviceType;
  }
  
  public static boolean isValidContextNameChar(char c)
  {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
  }
  
  public static void changeVariable(Context aContext, String variableNameString, ModifyVariable modifications) throws ContextException
  {
    final ContextManager cm = aContext.getContextManager();
    DataTable variableValue = aContext.getVariableClone(variableNameString, cm.getCallerController());
    modifications.execute(variableValue);
    aContext.setVariable(variableNameString, cm.getCallerController(), variableValue);
  }
  
  public interface ModifyVariable
  {
    void execute(DataTable variableValueTable);
  }
  
  public static String getGroupName(String entityName)
  {
    if (entityName == null)
    {
      return null;
    }
    
    if (entityName.endsWith(ENTITY_GROUP_SUFFIX))
    {
      String group = entityName.substring(0, entityName.length() - ENTITY_GROUP_SUFFIX.length());
      return group;
    }
    
    return null;
  }

  public static void registerType(String type)
  {
    synchronized (ContextUtils.class)
    {
      CONTEXT_TYPES.add(type);
    }
  }
  
}
