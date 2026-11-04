package com.sunwayworld.framework.context;

import com.sunwayworld.framework.exception.NotUniqueException;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import com.sunwayworld.module.sys.metadata.bean.CoreParentTableInstanceDTO;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

public class ApplicationContextHelper {
   private static volatile CoreConstantService constantService;
   private static volatile CoreSequenceService sequenceService;
   private static volatile CoreTableService tableService;
   private static volatile SqlSessionTemplate sqlSessionTemplate;
   private static volatile Boolean stressTesting = null;
   private static volatile Boolean loggingCountOnlyEnabled = null;

   public static native String getAppId();

   public static native LocalDate getLicenseDeadline();

   public static native int getLicenseCountdownDays();

   public static native int getNumberOfOnlineUsers();

   public static native int getLoginUserLimitation();

   public static native ApplicationContext getApplicationContext();

   public static final Environment getEnvironment() {
      return getApplicationContext().getEnvironment();
   }

   public static final <T> T getBean(Class<T> clazz) {
      return getApplicationContext().getBean(clazz);
   }

   public static final <T> T getBeanIfPresent(Class<T> clazz) {
      return getApplicationContext().getBeanProvider(clazz).getIfAvailable();
   }

   public static final <T> T getBean(String name) {
      Class<T> gikamClass = ClassUtils.getGikamClass(name);
      if (gikamClass == null) {
         return null;
      } else {
         Collection<T> collection = getApplicationContext().getBeansOfType(gikamClass).values();
         if (collection.size() > 1) {
            throw new NotUniqueException(name);
         } else {
            return collection.isEmpty() ? null : collection.iterator().next();
         }
      }
   }

   public static final <T> T getBeanIfPresent(String name) {
      return getApplicationContext().containsBean(name) ? getBean(name) : null;
   }

   public static final <T extends GenericCloudResource<?, ?>> T getFeignClientByTable(String table) {
      return GikamBeanContext.getFeignClientByTable(table);
   }

   public static final <T extends GenericService<?, ?>> T getServiceByTable(String table) {
      return GikamBeanContext.getServiceByTable(table);
   }

   public static final boolean isProfileActivated(String profile) {
      return ArrayUtils.contains(getApplicationContext().getEnvironment().getActiveProfiles(), profile);
   }

   public static final String getConstantValue(String id) {
      return getConstantValue(id, "");
   }

   public static final String getConstantValue(String id, String defaultValue) {
      CoreConstantBean constant = getCoreConstantService().selectByIdIfPresent(id);
      return constant == null ? defaultValue : constant.getConstantValue();
   }

   public static final <ID extends Serializable> List<CoreParentTableInstanceDTO> getAncientTableInstanceList(String table, List<ID> idList) {
      if (tableService == null) {
         synchronized (ApplicationContextHelper.class) {
            if (tableService == null) {
               tableService = getApplicationContext().getBean(CoreTableService.class);
            }
         }
      }

      return tableService.selectAncientTableInstanceList(table, idList);
   }

   public static final String getNextSequence(String id) {
      return getNextSequenceList(id, 1).get(0);
   }

   public static final String getNextSequence(String id, String key) {
      return getNextSequenceList(id, key, 1).get(0);
   }

   public static List<String> getNextSequenceList(String id, int qty) {
      return getNextSequenceList(id, "", qty);
   }

   public static List<String> getNextSequenceList(String id, String key, int qty) {
      return getCoreSequenceService().getNextSequenceList(id, key, qty);
   }

   public static final Long getNextSequenceNumber(String id) {
      return getNextSequenceNumberList(id, 1).get(0);
   }

   public static List<Long> getNextSequenceNumberList(String id, int qty) {
      return getCoreSequenceService().getNextIdentityList(id, qty);
   }

   public static final Long getNextIdentity() {
      return GikamIdHelper.nextId();
   }

   public static final List<Long> getNextIdentityList(int qty) {
      List<Long> idList = new ArrayList<>();

      for (int i = 0; i < qty; i++) {
         idList.add(GikamIdHelper.nextId());
      }

      return idList;
   }

   public static final List<Long> getNextOrderNoList(int qty) {
      return getNextIdentityList(qty);
   }

   public static final Long getNextOrderNo() {
      return getNextIdentity();
   }

   public static final String getServerAddress() {
      return "http://" + getEnvironment().getProperty("server.address", "localhost") + ":" + getEnvironment().getProperty("server.port");
   }

   public static SqlSessionTemplate getMybatisSqlSessionTemplate() {
      if (sqlSessionTemplate == null) {
         synchronized (ApplicationContextHelper.class) {
            if (sqlSessionTemplate == null) {
               sqlSessionTemplate = getApplicationContext().getBean(SqlSessionTemplate.class);
            }
         }
      }

      return sqlSessionTemplate;
   }

   public static boolean stressTesting() {
      if (stressTesting == null) {
         synchronized (ApplicationContextHelper.class) {
            if (stressTesting == null) {
               stressTesting = getEnvironment().getProperty("sunway.testing.stress.enabled", Boolean.class, false);
            }
         }
      }

      return stressTesting;
   }

   public static boolean loggingCountOnlyEnabled() {
      if (loggingCountOnlyEnabled == null) {
         synchronized (ApplicationContextHelper.class) {
            if (loggingCountOnlyEnabled == null) {
               loggingCountOnlyEnabled = getEnvironment().getProperty("sunway.logging.count-only.enabled", Boolean.class, false);
            }
         }
      }

      return loggingCountOnlyEnabled;
   }

   private static CoreSequenceService getCoreSequenceService() {
      if (sequenceService == null) {
         synchronized (ApplicationContextHelper.class) {
            if (sequenceService == null) {
               sequenceService = getApplicationContext().getBean(CoreSequenceService.class);
            }
         }
      }

      return sequenceService;
   }

   private static CoreConstantService getCoreConstantService() {
      if (constantService == null) {
         synchronized (ApplicationContextHelper.class) {
            if (constantService == null) {
               constantService = getApplicationContext().getBean(CoreConstantService.class);
            }
         }
      }

      return constantService;
   }
}
