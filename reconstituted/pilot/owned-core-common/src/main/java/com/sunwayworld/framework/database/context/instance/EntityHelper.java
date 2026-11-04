package com.sunwayworld.framework.database.context.instance;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.List;

public abstract class EntityHelper {
   private static final String[] CREATED_BY_COLUMNS = new String[]{"createdById", "createdByName", "createdByOrgId", "createdByOrgName", "createdTime"};

   public static boolean isColumnActivated(Persistable<?> item, EntityColumnContext columnContext) {
      BeanPropertyDescriptor propertyDescriptor = columnContext.getPropertyDescriptor();
      if (BeanPropertyListener.class.isAssignableFrom(item.getClass())) {
         return propertyDescriptor.isExt$BeanProperty()
            ? ((BeanPropertyListener)item).getChangedPropertyEventList().stream().anyMatch(e -> columnContext.getColumnName().equalsIgnoreCase(e.getName()))
            : ((BeanPropertyListener)item).getChangedPropertyEventList().stream().anyMatch(e -> e.getPropertyDescriptor().equals(propertyDescriptor));
      } else {
         return propertyDescriptor.isExt$BeanProperty() ? item.getExt$().containsKey(columnContext.getColumnName()) : columnContext.getValue(item) != null;
      }
   }

   public static void sortColumnContextInstance(List<EntityColumnContextInstance> columnContextInstanceList) {
      columnContextInstanceList.sort((c1, c2) -> {
         if (isIdInstance(c1)) {
            return -1;
         } else {
            return isIdInstance(c2) ? 1 : c1.getColumnContext().getColumnName().compareToIgnoreCase(c2.getColumnContext().getColumnName());
         }
      });
   }

   public static void sortColumnContext(List<EntityColumnContext> columnContextList) {
      columnContextList.sort((c1, c2) -> {
         if (isIdContext(c1)) {
            return -1;
         } else {
            return isIdContext(c2) ? 1 : c1.getColumnName().compareToIgnoreCase(c2.getColumnName());
         }
      });
   }

   public static boolean isIdColumn(String column) {
      return "id".equalsIgnoreCase(column);
   }

   public static boolean isCreatedByColumn(String column) {
      return ArrayUtils.containsIgnoreCase(CREATED_BY_COLUMNS, column);
   }

   public static boolean isIdInstance(EntityColumnContextInstance columnContextInstance) {
      return isIdColumn(columnContextInstance.getColumnContext().getColumnName());
   }

   public static boolean isIdContext(EntityColumnContext columnContext) {
      return isIdColumn(columnContext.getColumnName());
   }

   public static boolean isColumnUpdatable(String column) {
      return !isIdColumn(column) && !ArrayUtils.containsIgnoreCase(CREATED_BY_COLUMNS, column);
   }

   public static boolean isColumnUpdatable(EntityColumnContext columnContext) {
      return isColumnUpdatable(columnContext.getColumnName());
   }

   public static boolean isColumnUpdatable(EntityColumnContextInstance columnContextInstance) {
      return isColumnUpdatable(columnContextInstance.getColumnContext().getColumnName());
   }

   public static boolean isColumnUpdated(EntityContextInstance contextInstance) {
      return contextInstance.getColumnContextInstanceList().stream().anyMatch(c -> c.isActive() && isColumnUpdatable(c));
   }

   public static void assignCreatedElement(Insertable<?> item) {
      item.setCreatedById(LocalContextHelper.getLoginUserId());
      item.setCreatedByName(LocalContextHelper.getLoginUserName());
      item.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
      item.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
      item.setCreatedTime(LocalDateTime.now());
   }

   public static void assignCreatedElementIfNecessary(Insertable<?> item) {
      if (StringUtils.isEmpty(item.getCreatedById())) {
         item.setCreatedById(LocalContextHelper.getLoginUserId());
      }

      if (StringUtils.isEmpty(item.getCreatedByName())) {
         item.setCreatedByName(LocalContextHelper.getLoginUserName());
      }

      if (StringUtils.isEmpty(item.getCreatedByOrgId())) {
         item.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
      }

      if (StringUtils.isEmpty(item.getCreatedByOrgName())) {
         item.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
      }

      if (item.getCreatedTime() == null) {
         item.setCreatedTime(LocalDateTime.now());
      }
   }

   public static boolean isItemUpdated(Persistable<?> item) {
      if (item == null) {
         return false;
      } else {
         EntityContext context = DatabaseManager.getEntityContext((Class<? extends Persistable<?>>)item.getClass());
         return context.getColumnContextList().stream().filter(c -> isColumnUpdatable(c)).anyMatch(c -> isColumnActivated(item, c));
      }
   }
}
