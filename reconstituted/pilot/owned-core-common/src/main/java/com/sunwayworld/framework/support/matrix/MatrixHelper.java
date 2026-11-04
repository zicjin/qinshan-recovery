package com.sunwayworld.framework.support.matrix;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.matrix.bean.MatrixRowMap;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MatrixHelper {
   public static final String DELIMITER = "$";

   public static final <PK> String getRotatedFieldName(String table, PK id, String field) {
      return table.toUpperCase() + "$" + field.toUpperCase() + "$" + id;
   }

   public static final boolean isMatrixContent(List<String> contentList) {
      JSONObject jo = JSON.parseObject(contentList.get(0));
      return jo.keySet().stream().anyMatch(k -> k.contains("$"));
   }

   public static final MapperParameter getStandardMapperParameter(MapperParameter parameter) {
      MapperParameter standardMapperParameter = new MapperParameter();
      List<Pair<String, String>> orderParamList = (List<Pair<String, String>>)parameter.get("orderParams");
      if (orderParamList != null) {
         List<Pair<String, String>> filteredOrderParamList = orderParamList.stream().filter(o -> !o.getFirst().contains("$")).collect(Collectors.toList());
         if (!filteredOrderParamList.isEmpty()) {
            standardMapperParameter.put("orderParams", filteredOrderParamList);
         }
      }

      FilterParamPattern[] patterns = (FilterParamPattern[])parameter.get("filterParamPatterns");
      if (patterns != null) {
         List<FilterParamPattern> standardPatternList = Arrays.stream(patterns)
            .filter(p -> p.getMap().keySet().stream().noneMatch(k -> k.contains("$")))
            .collect(Collectors.toList());
         if (!standardPatternList.isEmpty()) {
            standardMapperParameter.put("filterParamPatterns", standardPatternList.toArray(new FilterParamPattern[0]));
         }
      }

      parameter.entrySet().stream().filter(e -> !"orderParams".equals(e.getKey()) && !"filterParamPatterns".equals(e.getKey())).forEach(e -> {
         if (!e.getKey().contains("$")) {
            standardMapperParameter.put(e.getKey(), e.getValue());
         }
      });
      return standardMapperParameter;
   }

   public static final List<FilterParamPattern> getRotatedFilterParamList(MapperParameter parameter) {
      FilterParamPattern[] patterns = (FilterParamPattern[])parameter.get("filterParamPatterns");
      return patterns != null
         ? Arrays.stream(patterns).filter(p -> p.getMap().keySet().stream().anyMatch(k -> k.contains("$"))).collect(Collectors.toList())
         : CollectionUtils.emptyList();
   }

   public static final List<Pair<String, String>> getRotatedOrderParamList(MapperParameter parameter) {
      List<Pair<String, String>> orderParamList = (List<Pair<String, String>>)parameter.get("orderParams");
      if (orderParamList != null) {
         List<Pair<String, String>> filteredOrderParamList = orderParamList.stream().filter(o -> o.getFirst().contains("$")).collect(Collectors.toList());
         if (!filteredOrderParamList.isEmpty()) {
            return filteredOrderParamList;
         }
      }

      return CollectionUtils.emptyList();
   }

   public static final List<MatrixRowMap> getFilteredRowList(MapperParameter parameter, List<MatrixRowMap> rowList) {
      List<FilterParamPattern> patternList = getRotatedFilterParamList(parameter);
      return patternList.isEmpty() ? rowList : rowList.stream().filter(r -> patternList.stream().allMatch(p -> r.match(p))).collect(Collectors.toList());
   }

   public static final void sortRowList(MapperParameter parameter, List<MatrixRowMap> rowList) {
      List<Pair<String, String>> orderParamList = getRotatedOrderParamList(parameter);
      if (!orderParamList.isEmpty()) {
         rowList.sort((r1, r2) -> {
            for (Pair<String, String> orderParam : orderParamList) {
               Object v1 = CollectionUtils.getValueIgnorecase(r1, orderParam.getFirst());
               Object v2 = CollectionUtils.getValueIgnorecase(r2, orderParam.getFirst());
               if (v1 == null) {
                  if (v2 != null) {
                     if ("asc".equalsIgnoreCase(orderParam.getSecond())) {
                        return 1;
                     }

                     return -1;
                  }
               } else if ("asc".equalsIgnoreCase(orderParam.getSecond())) {
                  if (v2 == null) {
                     return -1;
                  }

                  if (Number.class.isAssignableFrom(v1.getClass())) {
                     if (NumberUtils.toDouble(v1) > NumberUtils.toDouble(v2)) {
                        return -1;
                     }

                     return 1;
                  }
               } else {
                  if (v2 == null) {
                     return 1;
                  }

                  if (Number.class.isAssignableFrom(v1.getClass())) {
                     if (NumberUtils.toDouble(v1) > NumberUtils.toDouble(v2)) {
                        return 1;
                     }

                     return -1;
                  }
               }
            }

            return 0;
         });
      }
   }
}
