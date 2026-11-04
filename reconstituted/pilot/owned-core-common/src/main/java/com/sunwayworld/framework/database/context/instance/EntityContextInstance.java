package com.sunwayworld.framework.database.context.instance;

import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityContextInstance implements Serializable {
   private static final long serialVersionUID = 2036905136662907022L;
   private EntityContext context;
   private EntityColumnContextInstance idInstance;
   private List<EntityColumnContextInstance> columnContextInstanceList;

   private EntityContextInstance() {
   }

   public static <T extends Persistable<?>> EntityContextInstance updatable(EntityContext context, T item, String... activeColumns) {
      return of(context, item, EntityContextInstance.EntityContextInstanceCategory.UPDATABLE, activeColumns);
   }

   public static <T extends Persistable<?>> EntityContextInstance insertable(EntityContext context, T item, String... activeColumns) {
      return of(context, item, EntityContextInstance.EntityContextInstanceCategory.INSERTABLE, activeColumns);
   }

   public static <T extends Persistable<?>> EntityContextInstance instance(EntityContext context, T item, String... activeColumns) {
      return of(context, item, EntityContextInstance.EntityContextInstanceCategory.DEFAULT, activeColumns);
   }

   private static <T extends Persistable<?>> EntityContextInstance of(
      EntityContext context, T item, EntityContextInstance.EntityContextInstanceCategory category, String... activeColumns
   ) {
      EntityContextInstance instance = new EntityContextInstance();
      instance.context = Optional.ofNullable(context)
         .orElseGet(() -> DatabaseManager.getEntityContext((Class<? extends Persistable<?>>)ClassUtils.getRawType(item.getClass())));
      instance.columnContextInstanceList = new ArrayList<>();
      boolean isEmpty = BeanUtils.isEmpty(item);
      instance.context.getColumnContextList().forEach(c -> {
         EntityColumnContextInstance columnContextInstance = EntityColumnContextInstance.of(c, item);
         if (ArrayUtils.isEmpty(activeColumns)) {
            if (category.equals(EntityContextInstance.EntityContextInstanceCategory.UPDATABLE)) {
               columnContextInstance.setActive(EntityHelper.isColumnActivated(item, c) && EntityHelper.isColumnUpdatable(c));
            } else {
               columnContextInstance.setActive(EntityHelper.isColumnActivated(item, c));
            }
         } else {
            columnContextInstance.setActive(ArrayUtils.containsIgnoreCase(activeColumns, c.getColumnName()));
         }

         if (EntityHelper.isIdInstance(columnContextInstance)) {
            instance.idInstance = columnContextInstance;
         }

         if (category.equals(EntityContextInstance.EntityContextInstanceCategory.INSERTABLE)) {
            instance.columnContextInstanceList.add(columnContextInstance);
         } else if (columnContextInstance.isValueAssigned()) {
            instance.columnContextInstanceList.add(columnContextInstance);
         } else if (isEmpty) {
            instance.columnContextInstanceList.add(columnContextInstance);
         }
      });
      EntityHelper.sortColumnContextInstance(instance.columnContextInstanceList);
      return instance;
   }

   public EntityContext getContext() {
      return this.context;
   }

   public EntityColumnContextInstance getIdInstance() {
      return this.idInstance;
   }

   public List<EntityColumnContextInstance> getColumnContextInstanceList() {
      return this.columnContextInstanceList;
   }

   private static enum EntityContextInstanceCategory {
      UPDATABLE,
      INSERTABLE,
      DEFAULT;
   }
}
