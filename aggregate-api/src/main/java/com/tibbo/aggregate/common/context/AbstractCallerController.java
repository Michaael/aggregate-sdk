package com.tibbo.aggregate.common.context;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.security.PermissionCache;
import com.tibbo.aggregate.common.security.Permissions;
import com.tibbo.aggregate.common.util.WatchdogHolder;
import org.apache.logging.log4j.util.Strings;

public abstract class AbstractCallerController implements CallerController
{
  private static final int CACHE_EXPIRATION_PERIOD = 1000;
  
  private static final AtomicLong SESSION_ID_COUNTER = new AtomicLong(0L);
  
  private static final Set<CallerController> CONTROLLERS = Collections.newSetFromMap(new WeakHashMap());

  private static final ReentrantReadWriteLock CONTROLLERS_LOCK = new ReentrantReadWriteLock();
  
  private static final String FIELD_LOCKED_CONTEXTS_CONTEXT_PATH = "contextPath";
  private static final String FIELD_LOCKED_CONTEXTS_CONTEXT_DESCRIPTION = "contextDescription";
  private static final TableFormat FORMAT_LOCKED_CONTEXTS = new TableFormat();
  static
  {
    FORMAT_LOCKED_CONTEXTS.addField(FieldFormat.create(FIELD_LOCKED_CONTEXTS_CONTEXT_PATH, FieldFormat.STRING_FIELD, Cres.get().getString("conContextPath")));
    FORMAT_LOCKED_CONTEXTS.addField(FieldFormat.create(FIELD_LOCKED_CONTEXTS_CONTEXT_DESCRIPTION, FieldFormat.STRING_FIELD, Cres.get().getString("description")).setNullable(true));
  }
  
  private String username;
  
  private String inheritedUsername;
  
  private String login;
  
  private final CallerData callerData;
  
  private boolean loggedIn = false;
  
  private Type type;
  
  private String address;
  
  private final Date creationTime = new Date();
  
  private final Map<String, Object> properties = new HashMap<>();
  
  private Long lastCacheOperationTime = null;
  
  private Map<String, Reference<Context>> cache;
  
  private final Set<Context> lockedContexts = new HashSet<>();
  
  private final Long sessionIdCounter;
  
  private String sessionId;
  
  private Long tokenExpirationPeriod;
  
  private final List<LogoutListener> logoutListeners = new LinkedList<>();
  
  public AbstractCallerController(CallerData callerData)
  {
    this.callerData = callerData;
    
    resetCache();

    CONTROLLERS_LOCK.writeLock().lock();

    CONTROLLERS.add(this);

    CONTROLLERS_LOCK.writeLock().unlock();
    
    sessionIdCounter = SESSION_ID_COUNTER.getAndIncrement();
  }
  
  @Override
  public boolean isLoggedIn()
  {
    return loggedIn;
  }
  
  protected void setLoggedIn(boolean loggedIn)
  {
    this.loggedIn = loggedIn;
  }
  
  @Override
  public boolean isPermissionCheckingEnabled()
  {
    return true;
  }
  
  @Override
  public PermissionCache getPermissionCache()
  {
    return null;
  }
  
  @Override
  public CallerData getCallerData()
  {
    return callerData;
  }
  
  @Override
  public Map<String, Object> getProperties()
  {
    return properties;
  }
  
  @Override
  public void sendFeedback(int level, String message)
  {
    // Do nothing
  }
  
  public static List<CallerController> getControllers()
  {
    List<CallerController> list = new LinkedList<>();

    CONTROLLERS_LOCK.readLock().lock();

    for (CallerController cc : CONTROLLERS)
    {
      list.add(cc);
    }
    CONTROLLERS_LOCK.readLock().unlock();

    return Collections.unmodifiableList(list);
  }
  
  @Override
  public String toString()
  {
    return (type != null ? type : getClass().getName()) + " (" + (loggedIn ? "logged in" : "not logged in") + ")";
  }
  
  @Override
  public Permissions getPermissions()
  {
    return null;
  }
  
  @Override
  public String getUsername()
  {
    return username;
  }
  
  protected void setUsername(String username)
  {
    this.username = username;
  }
  
  @Override
  public String getLogin()
  {
    return login != null ? login : getEffectiveUsername();
  }
  
  public void setLogin(String login)
  {
    this.login = login;
  }
  
  @Override
  public String getInheritedUsername()
  {
    return inheritedUsername;
  }
  
  public void setInheritedUsername(String inheritedUsername)
  {
    this.inheritedUsername = inheritedUsername;
  }
  
  @Override
  public String getEffectiveUsername()
  {
    String inheritedUsername = getInheritedUsername();
    return Strings.isNotEmpty(inheritedUsername) ? inheritedUsername : getUsername();
  }
  
  @Override
  public Type getType()
  {
    return type;
  }
  
  public void setType(Type type)
  {
    this.type = type;
  }
  
  @Override
  public String getAddress()
  {
    return address;
  }
  
  public void setAddress(String address)
  {
    this.address = address;
  }
  
  @Override
  public Date getCreationTime()
  {
    return creationTime;
  }
  
  public String getSessionId()
  {
    return sessionId;
  }

  public void setSessionId(String sessionId)
  {
    this.sessionId = sessionId;
  }
  
  @Override
  public Long getTokenExpirationPeriod()
  {
    return tokenExpirationPeriod;
  }
  
  public void setTokenExpirationPeriod(Long tokenExpirationPeriod)
  {
    this.tokenExpirationPeriod = tokenExpirationPeriod;
  }
  
  @Override
  public void login(String username, String inheritedUsername, Permissions permissons) throws ContextException
  {
    setUsername(username);
    
    resetCache();
  }
  
  @Override
  public final void logout()
  {
    synchronized (logoutListeners)
    {
      logoutListeners.forEach(logoutListener -> logoutListener.beforeLogout(getSessionId()));
    }
    
    logoutInternal();
    
    synchronized (logoutListeners)
    {
      logoutListeners.forEach(logoutListener -> logoutListener.afterLogout(getSessionId()));
    }
  }
  
  protected void logoutInternal()
  {
    if (callerData != null)
    {
      callerData.cleanup();
    }
    
    resetCache();
  }
  
  public void addLogoutListener(LogoutListener logoutListener)
  {
    synchronized (logoutListeners)
    {
      logoutListeners.add(logoutListener);
    }
  }
  
  public void removeLogoutListener(LogoutListener logoutListener)
  {
    synchronized (logoutListeners)
    {
      logoutListeners.remove(logoutListener);
    }
  }
  
  @Override
  public Long getSessionIdCounter()
  {
    return sessionIdCounter;
  }
  
  @Override
  public Context lookup(String path)
  {
    if (!isPermissionCheckingEnabled())
    {
      return null;
    }
    
    long currentTime = System.currentTimeMillis();
    
    if (resetCache(currentTime))
    {
      return null;
    }
    
    if (lastCacheOperationTime == null)
    {
      lastCacheOperationTime = currentTime;
    }
    
    Reference<Context> ref = cache.get(path);
    
    Context con = ref != null ? ref.get() : null;
    
    if (con == null)
    {
      return null;
    }
    
    if (con.getContextManager() == null || !con.hasParent(con.getContextManager().getRoot()))
    {
      return null;
    }
    
    return con;
  }
  
  @Override
  public void cache(String path, Context context)
  {
    if (!isPermissionCheckingEnabled())
    {
      return;
    }
    
    if (!WatchdogHolder.getInstance().isEnoughMemory())
    {
      resetCache();
      return;
    }
    
    long currentTime = System.currentTimeMillis();
    
    resetCache(currentTime);
    
    cache.put(path, new WeakReference(context));
    
    if (lastCacheOperationTime == null)
    {
      lastCacheOperationTime = currentTime;
    }
  }
  
  private boolean resetCache(long currentTime)
  {
    if (lastCacheOperationTime != null && currentTime - lastCacheOperationTime > CACHE_EXPIRATION_PERIOD)
    {
      resetCache();
      return true;
    }
    
    return false;
  }
  
  private void resetCache()
  {
    cache = new ConcurrentHashMap<>();
    lastCacheOperationTime = null;
  }
  
  @Override
  public void addLockedContext(Context context)
  {
    lockedContexts.add(context);
  }
  
  @Override
  public void removeLockedContext(Context context)
  {
    lockedContexts.remove(context);
  }
  
  @Override
  public DataTable createLockedContextsTable()
  {
    final DataTable result = new SimpleDataTable(FORMAT_LOCKED_CONTEXTS);
    
    final Set<Context> clone = new HashSet<>(lockedContexts);
    for (Context context : clone)
      result.addRecord(context.getPath(), context.getDescription());
    
    result.sort(FIELD_LOCKED_CONTEXTS_CONTEXT_PATH, true);
    
    return result;
  }
  
  @Override
  public void unlockAllContexts()
  {
    final Set<Context> clone = new HashSet<>(lockedContexts);
    for (Context context : clone)
    {
      try
      {
        context.callFunction(AbstractContext.F_BREAK_LOCK, this);
      }
      catch (ContextException e)
      {
        Log.CONTEXT.warn("An error occurred when trying to unlock context '" + context + "' locked by caller '" + this + "'");
      }
    }
  }
  
  @Override
  public boolean isWeb()
  {
    return Type.WEB_SOCKET.equals(getType());
  }
  
  @Override
  public boolean isConnectionTerminatable()
  {
    return getUsername() != null && !isHeadless() && isLoggedIn();
  }
}
