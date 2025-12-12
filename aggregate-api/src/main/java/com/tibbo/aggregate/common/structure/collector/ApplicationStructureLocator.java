package com.tibbo.aggregate.common.structure.collector;

import static java.lang.String.format;

import com.google.common.annotations.VisibleForTesting;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.plugin.PluginDirector;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * A facility to find currently available {@link ApplicationStructureCollector} and cache it afterward
 *
 * @author Vladimir Plizga
 * @since AGG-10879
 */
public class ApplicationStructureLocator
{
  /**
   * The ID of the {@code application-structure} plugin which is responsible for handling data gathered from the core.
   */
  @VisibleForTesting
  static final String APPLICATION_STRUCTURE_PLUGIN_ID = "com.tibbo.linkserver.plugin.context.structure";

  private static ApplicationStructureCollector structureCollector = null;

  /**
   * Tries to locate a suitable implementation of {@link ApplicationStructureCollector} among currently active plugins
   * and returns the cached one upon finding.
   * <br/>
   * Should not be called before all the plugins have been loaded.
   *
   * @param pluginDirector a source of plugins to load the collector on behalf of
   * @return current active collector or a stub one if not found
   */
  public static ApplicationStructureCollector obtainStructureCollector(PluginDirector pluginDirector) 
  {
    if (structureCollector != null)
    {
      return structureCollector;
    }

    ClassLoader pluginClassLoader = null;
    try
    {
      pluginClassLoader = pluginDirector.getPluginClassLoader(APPLICATION_STRUCTURE_PLUGIN_ID);
    }
    catch (IllegalArgumentException e)
    {
      Log.STRUCTURE.debug("Plugin 'context-structure' is not available. No structure info will be collected.");
      structureCollector = NoopStructureCollector.INSTANCE;
    }
    catch (Exception e)
    {
      Log.STRUCTURE.error(format("Unable to find plugin with id '%s'. No info will be collected.", 
          APPLICATION_STRUCTURE_PLUGIN_ID), e);
      structureCollector = NoopStructureCollector.INSTANCE;
    }
    
    return (structureCollector != null)
        ? structureCollector
        : obtainStructureCollector(pluginClassLoader);
  }

  /**
   * Tries to locate a suitable implementation of {@link ApplicationStructureCollector} among currently active plugins
   * and returns the cached one upon finding.
   * <br/>
   * Should not be called before all the plugins have been loaded.
   *
   * @param pluginClassLoader structure plugin class loader
   * @return current active collector or a stub one if not found
   */
  public static ApplicationStructureCollector obtainStructureCollector(ClassLoader pluginClassLoader)
  {
    if (structureCollector != null)
    {
      return structureCollector;
    }

    synchronized (ApplicationStructureLocator.class)
    {
      if (structureCollector == null)
      {
        try
        {
          Iterator<ApplicationStructureCollector> collectorsIterator =
              ServiceLoader.load(ApplicationStructureCollector.class, pluginClassLoader)
                           .iterator();

          if (collectorsIterator.hasNext())
          {
            structureCollector = collectorsIterator.next();
            Log.STRUCTURE.info(format("Class '%s' is chosen as application structure collector",
                structureCollector.getClass().getName()));

            if (collectorsIterator.hasNext())
            {
              Log.STRUCTURE.warn(format("Plugin '%s' provides more than one implementation of '%s'. Only the first one " +
                      "will be used: '%s'", APPLICATION_STRUCTURE_PLUGIN_ID, ApplicationStructureCollector.class.getSimpleName(),
                  structureCollector.getClass().getName()));
            }
          }

          if (structureCollector == null)
          {
            structureCollector = NoopStructureCollector.INSTANCE;
            Log.STRUCTURE.info("No application structure collectors found so no info will be collected");
          }

        }
        catch (Exception e)
        {
          Log.STRUCTURE.error("Unable to locate structure collector. No info will be collected.", e);
          structureCollector = NoopStructureCollector.INSTANCE;
        }
      }
    }

    return structureCollector;
  }
}
