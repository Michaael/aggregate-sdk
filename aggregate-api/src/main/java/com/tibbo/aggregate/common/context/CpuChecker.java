package com.tibbo.aggregate.common.context;

import java.lang.management.*;
import java.util.*;

import com.tibbo.aggregate.common.*;

public class CpuChecker
{
    private static final String CPU_CHECKER = "CpuChecker";

    private static final Map<Long, Long> threadCpuTime = new HashMap();
    private static final Map<Long, Double> threadCpuLoad = new HashMap();
    private static Thread thread;

    public static void start()
    {
        if (thread == null)
        {
            Runnable task = new CpuCheckerTask();
            thread = new Thread(task, CPU_CHECKER);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private static class CpuCheckerTask implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    ThreadMXBean tmb = ManagementFactory.getThreadMXBean();

                    if (tmb.isThreadCpuTimeSupported() && tmb.isThreadCpuTimeEnabled())
                    {
                        Map<Long, Long> oldCpuTime = new HashMap(threadCpuTime);
                        Map<Long, Long> deltas = new HashMap();

                        threadCpuTime.clear();
                        threadCpuLoad.clear();

                        long[] threads = tmb.getAllThreadIds();

                        long totalDelta = 0;

                        for (long threadId : threads)
                        {
                            long newThreadTime = tmb.getThreadCpuTime(threadId);

                            if (newThreadTime == -1)
                            {
                                continue;
                            }

                            Long oldThreadTime = oldCpuTime.get(threadId);

                            if (oldThreadTime != null)
                            {
                                long delta = newThreadTime - oldThreadTime;

                                totalDelta += delta;

                                deltas.put(threadId, delta);
                            }

                            threadCpuTime.put(threadId, newThreadTime);
                        }

                        for (Map.Entry<Long, Long> entry : deltas.entrySet())
                        {
                            Long cpuTime = entry.getValue();

                            Double cpuLoad = (cpuTime != null && totalDelta > 0) ? cpuTime.doubleValue() / new Long(totalDelta).doubleValue() : null;

                            if (cpuLoad != null)
                            {
                                threadCpuLoad.put(entry.getKey(), cpuLoad * 100);
                            }
                        }
                    }

                    Thread.sleep(1000);

                } catch (Throwable t)
                {
                    Log.CORE.fatal(t.getMessage(), t);
                }
            }
        }
    }

    public static Map<Long, Double> getThreadCpuLoad()
    {
        return threadCpuLoad;
    }
}
