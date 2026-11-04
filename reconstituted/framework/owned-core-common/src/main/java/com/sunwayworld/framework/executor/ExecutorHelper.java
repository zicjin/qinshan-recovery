package com.sunwayworld.framework.executor;

import com.sunwayworld.framework.data.ObjectHolder;
import com.sunwayworld.framework.exception.TaskException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.executor.core.TraceCallable;
import com.sunwayworld.framework.executor.core.TraceRunnable;
import com.sunwayworld.framework.executor.core.TransactionAwareCallable;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Order(0)
@Component
public class ExecutorHelper {
   private static ThreadPoolExecutor executor;
   private static DataSource dataSource;

   @Autowired
   public void setExecutor(ThreadPoolExecutor executor) {
      ExecutorHelper.executor = executor;
   }

   @Autowired
   public void setDataSource(DataSource dataSource) {
      ExecutorHelper.dataSource = dataSource;
   }

   public static final <T> Future<T> submit(Callable<T> task) {
      return executor.submit(TraceCallable.of(task));
   }

   public static final <T> T submitAndGet(Callable<T> task) {
      try {
         return executor.submit(TraceCallable.of(task)).get();
      } catch (ExecutionException | InterruptedException var2) {
         throw new ApplicationRuntimeException(var2);
      }
   }

   public static final <T> List<T> transactionAwareSubmit(List<Callable<T>> taskList) {
      return transactionAwareSubmit(taskList, null);
   }

   public static final <T> List<T> transactionAwareSubmit(List<Callable<T>> taskList, Function<Callable<T>, String> messageProducer) {
      Connection connection = null;

      try {
         ConnectionHolder connectionHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource(dataSource);
         if (connectionHolder == null) {
            connection = dataSource.getConnection();
         } else {
            connection = connectionHolder.getConnection();
         }

         ObjectHolder objectHolder = new ObjectHolder(connection);

         List var6;
         try {
            List<Future<T>> futureList = taskList.stream()
               .map(c -> executor.submit(new TransactionAwareCallable<>(objectHolder.get(), (Callable<T>)c)))
               .collect(Collectors.toList());
            var6 = futureList.stream().map(f -> {
               try {
                  return f.get();
               } catch (ExecutionException | InterruptedException var4x) {
                  if (messageProducer != null) {
                     String message = messageProducer.apply((Callable<T>)ReflectionUtils.getFieldValue(f, "callable"));
                     throw new ApplicationRuntimeException(var4x, message + var4x.getMessage());
                  } else {
                     throw new ApplicationRuntimeException(var4x);
                  }
               }
            }).collect(Collectors.toList());
         } finally {
            objectHolder.setObject(null);
         }

         return var6;
      } catch (Exception var13) {
         try {
            connection.rollback();
         } catch (SQLException var11) {
            throw new ApplicationRuntimeException(var11);
         }

         if (var13 instanceof ApplicationRuntimeException) {
            throw (ApplicationRuntimeException)var13;
         } else {
            throw new ApplicationRuntimeException(var13);
         }
      }
   }

   public static final void submit(Runnable task) {
      executor.execute(TraceRunnable.of(task));
   }

   public static final void submit(TimerTask task, long delayMilliSeconds) {
      Timer timer = new Timer();
      timer.schedule(task, delayMilliSeconds);
   }

   public static final <T> T submit(Callable<T> task, long timeoutMilliSeconds) {
      Future<T> future = submit(task);

      try {
         return future.get(timeoutMilliSeconds, TimeUnit.MILLISECONDS);
      } catch (Exception var7) {
         try {
            future.cancel(true);
         } catch (Exception var6) {
         }

         throw new TaskException(task.toString(), var7);
      }
   }

   public static final void submitAfterCommitTransaction(Runnable task) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
         @Override
         public void afterCommit() {
            ExecutorHelper.submit(task);
         }
      });
   }
}
