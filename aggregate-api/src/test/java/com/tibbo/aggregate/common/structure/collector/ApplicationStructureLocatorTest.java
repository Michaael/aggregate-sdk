package com.tibbo.aggregate.common.structure.collector;

import static com.tibbo.aggregate.common.structure.collector.ApplicationStructureLocator.APPLICATION_STRUCTURE_PLUGIN_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.tibbo.aggregate.common.plugin.PluginDirector;
import org.java.plugin.PluginLifecycleException;
import org.junit.jupiter.api.Test;

class ApplicationStructureLocatorTest
{

  @Test
  void obtainStructureCollectorWithDirector() throws PluginLifecycleException
  {
    // given
    PluginDirector directorMock = mock(PluginDirector.class);
    when(directorMock.getPluginClassLoader(APPLICATION_STRUCTURE_PLUGIN_ID))
        .thenReturn(Thread.currentThread().getContextClassLoader());

    // when 1
    ApplicationStructureCollector collector1 = ApplicationStructureLocator.obtainStructureCollector(directorMock);

    // then 1
    assertEquals(NoopStructureCollector.INSTANCE, collector1);
    verify(directorMock).getPluginClassLoader(APPLICATION_STRUCTURE_PLUGIN_ID);

    // when 2
    ApplicationStructureCollector collector2 = ApplicationStructureLocator.obtainStructureCollector(directorMock);

    // then 2
    assertSame(collector1, collector2);
    verifyNoMoreInteractions(directorMock);
  }
  
}