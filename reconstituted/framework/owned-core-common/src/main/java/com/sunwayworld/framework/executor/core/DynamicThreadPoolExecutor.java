package com.sunwayworld.framework.executor.core;

import com.sunwayworld.framework.utils.SystemUtils;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

public class DynamicThreadPoolExecutor extends ThreadPoolExecutor {
   private static final Logger logger = LogManager.getLogger(DynamicThreadPoolExecutor.class);
   private static final int ADJUSTMENT = 5;
   private final int minPoolSize;
   private final int maxPoolSize;
   private final int minThreshold;
   private final int maxThreshold;
   public static AtomicInteger optimizerCounter = new AtomicInteger(0);
   @Value("${sunway.monitor.threadpool.log.enabled:true}")
   private boolean enabled;

   public DynamicThreadPoolExecutor(int minPoolSize, int maxPoolSize, int minThreshold, int maxThreshold, long keepAliveTime) {
      super(minPoolSize, minPoolSize, keepAliveTime, TimeUnit.SECONDS, new PriorityBlockingQueue<>(), new NamedThreadFactory());
      super.setKeepAliveTime(30L, TimeUnit.SECONDS);
      this.minThreshold = minThreshold;
      this.maxThreshold = maxThreshold;
      if (this.minThreshold > this.maxThreshold) {
         throw new IllegalArgumentException();
      } else {
         this.minPoolSize = minPoolSize;
         this.maxPoolSize = maxPoolSize;
         if (this.minPoolSize > this.maxPoolSize) {
            throw new IllegalArgumentException();
         }
      }
   }

   public int getMinThreshold() {
      return this.minThreshold;
   }

   public int getMaxThreshold() {
      return this.maxThreshold;
   }

   public int getMinPoolSize() {
      return this.minPoolSize;
   }

   public int getMaxPoolSize() {
      return this.maxPoolSize;
   }

   @Override
   public Future<?> submit(Runnable task) {
      optimizerCounter.incrementAndGet();
      return super.submit(this.getWrappedRunnable(task));
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      optimizerCounter.incrementAndGet();
      return super.submit(this.getWrappedRunnable(task), result);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      optimizerCounter.incrementAndGet();
      return super.submit(this.getWrappedCallable(task));
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      optimizerCounter.incrementAndGet();
      return super.invokeAny(this.getWrappedCallableList(tasks));
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      optimizerCounter.incrementAndGet();
      return super.invokeAny(this.getWrappedCallableList(tasks), timeout, unit);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      optimizerCounter.incrementAndGet();
      return super.invokeAll(this.getWrappedCallableList(tasks));
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      optimizerCounter.incrementAndGet();
      return super.invokeAll(this.getWrappedCallableList(tasks), timeout, unit);
   }

   @Override
   public void execute(Runnable task) {
      optimizerCounter.incrementAndGet();
      super.execute(this.getWrappedRunnable(task));
   }

   @Scheduled(
      fixedDelay = 1000L
   )
   public void optimizePool() {
      if (optimizerCounter.getAndSet(0) > 0) {
         int poolSize = this.getPoolSize();
         if (poolSize >= this.getMinPoolSize()) {
            int cpuRatio = SystemUtils.getCpuRatio();
            if (cpuRatio < this.getMinThreshold()) {
               if (this.getQueue().size() > 0 && poolSize < this.getMaxPoolSize()) {
                  int newSize = Math.min(poolSize + 5, this.getMaxPoolSize());
                  if (newSize != this.getCorePoolSize()) {
                     this.setCorePoolSize(newSize);
                  }

                  if (newSize != this.getMaximumPoolSize()) {
                     this.setMaximumPoolSize(newSize);
                  }
               }
            } else if (cpuRatio >= this.getMaxThreshold()) {
               int newSizex = Math.max(poolSize - 5, this.getMinPoolSize());
               if (newSizex != this.getCorePoolSize()) {
                  this.setCorePoolSize(newSizex);
               }

               if (newSizex != this.getMaximumPoolSize()) {
                  this.setMaximumPoolSize(newSizex);
               }
            }
         }

         if (this.enabled) {
            this.printPoolSize();
         }
      }
   }

   private void printPoolSize() {
      int cpuRatio = SystemUtils.getCpuRatio();
      if (cpuRatio >= 50) {
         StringBuilder sb = new StringBuilder("Pool:(size:")
            .append(this.getPoolSize())
            .append(" range:")
            .append(this.getCorePoolSize())
            .append("~")
            .append(this.getMaximumPoolSize())
            .append(" queueSize:")
            .append(this.getQueue().size())
            .append(") Config:(min:")
            .append(this.getMinPoolSize())
            .append(" max:")
            .append(this.getMaxPoolSize())
            .append(") cpuRatio:")
            .append(SystemUtils.getCpuRatio());
         logger.info(sb.toString());
      }
   }

   private Runnable getWrappedRunnable(Runnable runnable) {
      return (Runnable)(runnable instanceof TraceRunnable ? runnable : TraceRunnable.of(runnable));
   }

   private <T> Callable<T> getWrappedCallable(Callable<T> callable) {
      return (Callable<T>)(callable instanceof TraceCallable ? callable : TraceCallable.of(callable));
   }

   private <T> Collection<? extends Callable<T>> getWrappedCallableList(Collection<? extends Callable<T>> tasks) {
      return tasks.stream().map(c -> this.getWrappedCallable((Callable<T>)c)).collect(Collectors.toList());
   }
}
