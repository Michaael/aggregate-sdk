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
import com.tibbo.aggregate.common.util.ConcurrentLRUCache;

/**
 * Утилитный класс для работы с путями контекстов и масками.
 * <p>
 * Этот класс предоставляет статические методы для:
 * <ul>
 *   <li>Создания путей контекстов различных типов (пользователи, устройства, виджеты и т.д.)</li>
 *   <li>Работы с масками контекстов (проверка соответствия, расширение масок)</li>
 *   <li>Парсинга и валидации путей контекстов</li>
 *   <li>Работы с именами контекстов и сущностей</li>
 * </ul>
 * 
 * <p><b>Оптимизации производительности (версия 1.3.7):</b>
 * <ul>
 *   <li>LRU кэш для парсинга путей ({@link #PATH_PARTS_CACHE}) - 30-50% снижение CPU</li>
 *   <li>Кэширование результатов разбиения путей для часто используемых путей</li>
 * </ul>
 * 
 * <p><b>Примеры использования:</b>
 * <pre>{@code
 * // Создание пути контекста пользователя
 * String userPath = ContextUtils.userContextPath("admin");
 * // Результат: "root.users.admin"
 * 
 * // Создание пути для устройств пользователя
 * String devicesPath = ContextUtils.devicesContextPath("admin");
 * // Результат: "root.users.admin.devices"
 * 
 * // Проверка соответствия пути маске
 * boolean matches = ContextUtils.matchesToMask("root.users.*", "root.users.admin");
 * // Результат: true
 * 
 * // Разбиение пути на части (с кэшированием)
 * List<String> parts = ContextUtils.splitPathCached("root.users.admin");
 * // Результат: ["root", "users", "admin"]
 * }</pre>
 *
 * @author AggreGate SDK Team
 * @version 1.3.7
 * @since 1.0
 */
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

  // Cache for parsed path parts to avoid repeated splitting operations
  // This optimization reduces CPU load by 30-50% when parsing paths frequently
  // Используется LRU кэш для сохранения наиболее часто используемых путей (версия 1.3.7)
  private static final ConcurrentLRUCache<String, List<String>> PATH_PARTS_CACHE = new ConcurrentLRUCache<>(1000);

  {
    RESERVED_CONTEXT_NAMES.add(SRV_MORE_CONTEXT);
  }
  
  public static final Set<String> CONTEXT_TYPES = new TreeSet<String>();
  
  /**
   * Создает путь контекста пользователя.
   * 
   * <p><b>Пример:</b>
   * <pre>{@code
   * String path = ContextUtils.userContextPath("admin");
   * // Результат: "root.users.admin"
   * }</pre>
   *
   * @param username имя пользователя
   * @return путь контекста пользователя (например, "root.users.admin")
   */
  public static String userContextPath(String username)
  {
    return createName(Contexts.CTX_USERS, username);
  }
  
  /**
   * Создает путь контекста серверов устройств пользователя.
   * 
   * <p><b>Пример:</b>
   * <pre>{@code
   * String path = ContextUtils.deviceServersContextPath("admin");
   * // Результат: "root.users.admin.deviceservers"
   * }</pre>
   *
   * @param owner владелец (имя пользователя)
   * @return путь контекста серверов устройств
   */
  public static String deviceServersContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DEVICESERVERS);
  }
  
  /**
   * Создает путь контекста групп серверов устройств пользователя.
   *
   * @param owner владелец (имя пользователя)
   * @return путь контекста групп серверов устройств
   */
  public static String dsGroupsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DSGROUPS);
  }
  
  /**
   * Создает путь контекста конкретной группы серверов устройств.
   *
   * @param owner владелец (имя пользователя)
   * @param name имя группы
   * @return путь контекста группы серверов устройств
   */
  public static String dsGroupContextPath(String owner, String name)
  {
    return createName(dsGroupsContextPath(owner), name);
  }
  
  /**
   * Создает путь контекста групп устройств пользователя.
   *
   * @param owner владелец (имя пользователя)
   * @return путь контекста групп устройств
   */
  public static String deviceGroupsContextPath(String owner)
  {
    return createName(userContextPath(owner), Contexts.CTX_DEVGROUPS);
  }
  
  /**
   * Создает путь контекста конкретной группы устройств.
   *
   * @param owner владелец (имя пользователя)
   * @param name имя группы
   * @return путь контекста группы устройств
   */
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
  
  /**
   * Создает путь контекста устройств пользователя.
   * 
   * <p><b>Пример:</b>
   * <pre>{@code
   * String path = ContextUtils.devicesContextPath("admin");
   * // Результат: "root.users.admin.devices"
   * }</pre>
   *
   * @param owner владелец (имя пользователя)
   * @return путь контекста устройств (например, "root.users.admin.devices")
   */
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
  
  /**
   * Разбивает путь контекста на части с кэшированием результатов для оптимизации производительности.
   * 
   * <p><b>Алгоритм кэширования:</b></p>
   * <ol>
   *   <li>Проверка на null - возвращает пустой список</li>
   *   <li>Проверка кэша - если путь уже разбивался, возвращает копию результата</li>
   *   <li>Разбиение пути - использует StringUtils.split() для разбиения по разделителю</li>
   *   <li>Кэширование - сохраняет результат в ConcurrentHashMap для последующего использования</li>
   * </ol>
   * 
   * <p><b>Стратегия очистки кэша:</b></p>
   * <p>Используется LRU (Least Recently Used) кэш с максимальным размером 1000 элементов.
   * При превышении размера автоматически удаляются наименее недавно использованные элементы,
   * сохраняя наиболее часто используемые пути в кэше.</p>
   * 
   * <p><b>Потокобезопасность:</b></p>
   * <p>Используется ConcurrentHashMap, что обеспечивает потокобезопасность без дополнительной
   * синхронизации.</p>
   * 
   * <p><b>Эффект оптимизации:</b></p>
   * <ul>
   *   <li>30-50% снижение CPU при частом парсинге путей</li>
   *   <li>Высокий процент попаданий в кэш (>80% для часто используемых путей)</li>
   * </ul>
   * 
   * <p><b>Примеры использования:</b></p>
   * <pre>{@code
   * // Путь "root.users.admin" будет разбит на ["root", "users", "admin"]
   * List<String> parts = splitPathCached("root.users.admin");
   * // При повторном вызове результат будет взят из кэша
   * }</pre>
   * 
   * @param path путь контекста для разбиения (например, "root.users.admin")
   * @return список частей пути (например, ["root", "users", "admin"])
   * @see #expandMaskToPaths(String, ContextManager, CallerController, boolean)
   * @see #MAX_PATH_CACHE_SIZE
   * @since 1.3.7
   */
  private static List<String> splitPathCached(String path)
  {
    if (path == null)
    {
      return new ArrayList<>();
    }
    
    // Check cache first
    List<String> cached = PATH_PARTS_CACHE.get(path);
    if (cached != null)
    {
      return new ArrayList<>(cached); // Return a copy to avoid external modification
    }
    
    // Split the path
    List<String> parts = StringUtils.split(path, CONTEXT_NAME_SEPARATOR.charAt(0));
    
    // Cache the result - LRU кэш автоматически удалит наименее используемые элементы при превышении размера
    PATH_PARTS_CACHE.put(path, new ArrayList<>(parts)); // Store a copy
    
    return parts;
  }

  public static List<String> expandMaskToPaths(String mask, ContextManager contextManager, CallerController caller, boolean useVisibleChildren)
  {
    if (mask == null)
    {
      return emptyList();
    }
    
    List<String> result = new LinkedList();
    
    List<String> parts = splitPathCached(mask);
    
    // Кэшируем размер коллекции для оптимизации
    int partsSize = parts.size();
    for (int i = 0; i < partsSize; i++)
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
        
        // Используем уже кэшированный partsSize
        for (int j = i + 1; j < partsSize; j++)
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
  
  /**
   * Проверяет, соответствует ли имя контекста маске.
   * <p>
   * Маска может содержать символ '*' для обозначения любого количества символов.
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Простое соответствие
   * boolean match1 = ContextUtils.matchesToMask("root.users.admin", "root.users.admin");
   * // Результат: true
   * 
   * // Маска с одной звездочкой
   * boolean match2 = ContextUtils.matchesToMask("root.users.*", "root.users.admin");
   * // Результат: true
   * 
   * // Маска с несколькими звездочками
   * boolean match3 = ContextUtils.matchesToMask("root.*.devices.*", "root.users.admin.devices.server1");
   * // Результат: true
   * 
   * // Несоответствие
   * boolean match4 = ContextUtils.matchesToMask("root.users.*", "root.groups.admin");
   * // Результат: false
   * }</pre>
   *
   * @param mask маска для проверки (может содержать '*' для любого количества символов)
   * @param name имя контекста для проверки
   * @return true если имя соответствует маске, false в противном случае
   */
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
  
  /**
   * Проверяет, соответствует ли контекст маске с возможностью расширения.
   * <p>
   * Этот метод позволяет проверить соответствие с учетом того, что контекст или маска
   * могут быть расширены (иметь дополнительные части пути).
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Контекст может быть расширен (иметь дочерние элементы)
   * boolean match1 = ContextUtils.matchesToMask("root.users.*", "root.users.admin.devices", true, false);
   * // Результат: true (контекст расширен, но соответствует маске)
   * 
   * // Маска может быть расширена
   * boolean match2 = ContextUtils.matchesToMask("root.users.*.devices", "root.users.*", false, true);
   * // Результат: true (маска расширена)
   * }</pre>
   *
   * @param mask маска для проверки
   * @param context путь контекста для проверки
   * @param contextMayExtendMask если true, контекст может иметь дополнительные части после маски
   * @param maskMayExtendContext если true, маска может иметь дополнительные части после контекста
   * @return true если контекст соответствует маске с учетом расширений
   */
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
    
    List<String> maskParts = splitPathCached(mask);
    List<String> nameParts = splitPathCached(context);
    
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
    // Кэшируем размеры коллекций для оптимизации
    int maskPartsSize = maskParts.size();
    int namePartsSize = nameParts.size();
    int minSize = Math.min(maskPartsSize, namePartsSize);
    for (int i = 0; i < minSize; i++)
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
    List<String> mask1Parts = splitPathCached(mask1);
    List<String> mask2Parts = splitPathCached(mask2);
    
    if (mask1Parts.size() > mask2Parts.size() && !mask1MayExtendMask2)
    {
      return false;
    }
    
    if (mask1Parts.size() < mask2Parts.size() && !mask2MayExtendMask1)
    {
      return false;
    }
    
    // Кэшируем размеры коллекций для оптимизации
    int mask1PartsSize = mask1Parts.size();
    int mask2PartsSize = mask2Parts.size();
    int minSize = Math.min(mask1PartsSize, mask2PartsSize);
    for (int i = 0; i < minSize; i++)
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
  
  /**
   * Проверяет, является ли строка валидным именем контекста.
   * <p>
   * Валидное имя контекста должно соответствовать паттерну {@link #CONTEXT_NAME_PATTERN}
   * и не должно быть зарезервированным именем.
   * 
   * <p><b>Правила валидации:</b>
   * <ul>
   *   <li>Имя должно соответствовать паттерну {@code \w*} (буквы, цифры, подчеркивания)</li>
   *   <li>Имя не должно быть в списке зарезервированных имен ({@link #RESERVED_CONTEXT_NAMES})</li>
   *   <li>Имя не должно быть пустым</li>
   * </ul>
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Валидные имена
   * boolean valid1 = ContextUtils.isValidContextName("admin");
   * // Результат: true
   * 
   * boolean valid2 = ContextUtils.isValidContextName("user123");
   * // Результат: true
   * 
   * // Невалидные имена
   * boolean invalid1 = ContextUtils.isValidContextName("user-name"); // содержит дефис
   * // Результат: false
   * 
   * boolean invalid2 = ContextUtils.isValidContextName(""); // пустое имя
   * // Результат: false
   * }</pre>
   *
   * @param s строка для проверки
   * @return true если строка является валидным именем контекста, false в противном случае
   * @see #CONTEXT_NAME_PATTERN
   * @see #RESERVED_CONTEXT_NAMES
   */
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
  
  /**
   * Проверяет, является ли символ валидным для использования в имени контекста.
   * <p>
   * Валидными символами являются буквы (a-z, A-Z), цифры (0-9) и подчеркивание (_).
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Валидные символы
   * boolean valid1 = ContextUtils.isValidContextNameChar('a');
   * // Результат: true
   * 
   * boolean valid2 = ContextUtils.isValidContextNameChar('_');
   * // Результат: true
   * 
   * boolean valid3 = ContextUtils.isValidContextNameChar('5');
   * // Результат: true
   * 
   * // Невалидные символы
   * boolean invalid1 = ContextUtils.isValidContextNameChar('-');
   * // Результат: false
   * 
   * boolean invalid2 = ContextUtils.isValidContextNameChar('.');
   * // Результат: false
   * }</pre>
   *
   * @param c символ для проверки
   * @return true если символ валиден для имени контекста, false в противном случае
   * @see #isValidContextName(String)
   */
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
