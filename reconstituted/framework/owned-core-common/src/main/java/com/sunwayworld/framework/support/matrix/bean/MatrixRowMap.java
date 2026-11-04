package com.sunwayworld.framework.support.matrix.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.support.matrix.MatrixHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class MatrixRowMap extends HashMap<String, Object> {
   private static final long serialVersionUID = 9098143462818701262L;
   private static final String ID_KEY = "row_id";
   @JSONField(
      serialize = false
   )
   private Predicate<FilterParamPattern> predicate;
   private final Map<String, Map<String, Object>> ext$ = new CaseInsensitiveLinkedMap<>();

   public MatrixRowMap() {
      this.put("ext$", this.ext$);
   }

   public <V> void putRowId(String table, V id) {
      super.put("row_id", table.toUpperCase() + "$" + id);
   }

   public <V> V getId() {
      return (V)this.get("row_id");
   }

   public <T> void putItem(T item) {
      Map<String, Object> map = BeanUtils.deeplyToMap(item);
      this.putAll(map);
   }

   public <T> void putRotateItem(String table, T item) {
      Map<String, Object> map = BeanUtils.deeplyToMap(item);
      Object id = CollectionUtils.getValueIgnorecase(map, "id");
      map.forEach((k, v) -> this.putRotateValue(table, id, k, v));
   }

   public <PK, V> void putRotateValue(String table, PK id, String field, V value) {
      this.put(MatrixHelper.getRotatedFieldName(table, id, field), value);
   }

   public <PK, V> void putRotateExtValue(String table, PK id, String field, String targetField, V targetValue) {
      String fieldName = MatrixHelper.getRotatedFieldName(table, id, field) + "_ext$";
      if (this.ext$.containsKey(fieldName)) {
         this.ext$.get(fieldName).put(targetField, targetValue);
      } else {
         Map<String, Object> rotateExtMap = new CaseInsensitiveLinkedMap<>();
         rotateExtMap.put(targetField, targetValue);
         this.ext$.put(fieldName, rotateExtMap);
      }
   }

   public boolean match(FilterParamPattern filterPattern) {
      return this.predicate != null ? this.predicate.test(filterPattern) : filterPattern.match(this);
   }

   public void setPredicate(Predicate<FilterParamPattern> predicate) {
      this.predicate = predicate;
   }
}
