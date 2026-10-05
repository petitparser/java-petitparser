package org.petitparser.benchmark;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Runner harness for benchmarks tracking elapsed time and heap allocations.
 */
public class BenchmarkRunner {

  private BenchmarkRunner() {}

  private static final Object THREAD_MX_BEAN;
  private static final MethodHandle GET_THREAD_ALLOCATED_BYTES_MH;

  static {
    Object bean = null;
    MethodHandle mh = null;
    try {
      Class<?> factoryClass = Class.forName("java.lang.management.ManagementFactory");
      Method getMxBean = factoryClass.getMethod("getThreadMXBean");
      bean = getMxBean.invoke(null);
      if (bean != null) {
        Class<?> sunBeanClass = Class.forName("com.sun.management.ThreadMXBean");
        if (sunBeanClass.isInstance(bean)) {
          Method isSupported = sunBeanClass.getMethod("isThreadAllocatedMemorySupported");
          Boolean supported = (Boolean) isSupported.invoke(bean);
          if (Boolean.TRUE.equals(supported)) {
            Method isEnabled = sunBeanClass.getMethod("isThreadAllocatedMemoryEnabled");
            Boolean enabled = (Boolean) isEnabled.invoke(bean);
            if (!Boolean.TRUE.equals(enabled)) {
              Method setEnabled = sunBeanClass.getMethod("setThreadAllocatedMemoryEnabled", boolean.class);
              setEnabled.invoke(bean, true);
            }
            Method getBytes = sunBeanClass.getMethod("getThreadAllocatedBytes", long.class);
            mh = java.lang.invoke.MethodHandles.lookup().unreflect(getBytes).bindTo(bean);
          }
        }
      }
    } catch (Throwable ignored) {
      bean = null;
      mh = null;
    }
    THREAD_MX_BEAN = bean;
    GET_THREAD_ALLOCATED_BYTES_MH = mh;
  }

  public static boolean isThreadAllocationTrackingSupported() {
    return GET_THREAD_ALLOCATED_BYTES_MH != null;
  }

  public static long getThreadAllocatedBytes() {
    if (GET_THREAD_ALLOCATED_BYTES_MH != null) {
      try {
        return (long) GET_THREAD_ALLOCATED_BYTES_MH.invokeExact(Thread.currentThread().getId());
      } catch (Throwable ignored) {
      }
    }
    Runtime runtime = Runtime.getRuntime();
    return runtime.totalMemory() - runtime.freeMemory();
  }

  public static BenchmarkResult run(String name, int warmupIterations, int measurementIterations, Runnable task) {
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(task, "task must not be null");

    // Warmup phase
    for (int i = 0; i < warmupIterations; i++) {
      task.run();
    }

    // Measurement phase
    System.gc();
    long startAllocated = getThreadAllocatedBytes();
    long startNanos = System.nanoTime();

    for (int i = 0; i < measurementIterations; i++) {
      task.run();
    }

    long elapsedNanos = System.nanoTime() - startNanos;
    long endAllocated = getThreadAllocatedBytes();
    long allocated = endAllocated >= startAllocated ? endAllocated - startAllocated : 0;

    return new BenchmarkResult(name, measurementIterations, elapsedNanos, allocated);
  }
}
