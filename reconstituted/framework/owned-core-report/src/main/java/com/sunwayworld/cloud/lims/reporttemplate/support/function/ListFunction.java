package com.sunwayworld.cloud.lims.reporttemplate.support.function;

import com.deepoove.poi.data.HyperlinkTextRenderData;
import com.deepoove.poi.data.NumberingRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.ReflectUtils;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ListFunction {
   default <V> List<LimsTemplateDataMap<V>> subList(List<LimsTemplateDataMap<V>> list, int start, int maxSize, boolean supplementNotEnough) {
      if (CollectionUtils.isEmpty(list)) {
         return new LimsTemplateDataList<>();
      } else {
         LimsTemplateDataList<LimsTemplateDataMap<V>> expList = new LimsTemplateDataList<>();
         if (start > list.size() - 1) {
            if (supplementNotEnough) {
               for (int i = 0; i < maxSize; i++) {
                  expList.add(new LimsTemplateDataMap<>());
               }
            }

            return expList;
         } else {
            int endIndex = maxSize + start;
            if (supplementNotEnough && endIndex > list.size()) {
               endIndex = maxSize + start;
            } else {
               endIndex = Math.min(endIndex, list.size());
            }

            for (int i = start; i < endIndex; i++) {
               LimsTemplateDataMap<V> item = i < list.size() ? list.get(i) : new LimsTemplateDataMap<>();
               expList.add(item);
            }

            return expList;
         }
      }
   }

   default <T> LimsTemplateDataList<LimsTemplateDataList<T>> group(List<T> list, int maxCount) {
      if (!CollectionUtils.isEmpty(list) && maxCount != 0) {
         int limit = (list.size() + maxCount - 1) / maxCount;
         return Stream.<Integer>iterate(0, n -> n + 1)
            .limit(limit)
            .parallel()
            .map(a -> {
               LimsTemplateDataList<T> group = new LimsTemplateDataList<>();
               list.stream().skip(a * maxCount).limit(maxCount).forEach(group::add);
               return group;
            })
            .collect(Collectors.toCollection(() -> new LimsTemplateDataList<LimsTemplateDataList<T>>()));
      } else {
         return new LimsTemplateDataList<>();
      }
   }

   default <T> LimsTemplateDataList<LimsTemplateDataList<T>> groupBy(List<T> list, String... groupFieldArray) {
      return !CollectionUtils.isEmpty(list) && groupFieldArray != null
         ? list.stream()
            .collect(Collectors.groupingBy(data -> {
               StringBuilder sb = new StringBuilder();

               for (String field : groupFieldArray) {
                  sb.append(ObjectUtils.toString(ReflectUtils.getPropertyValue((T)data, field))).append("-");
               }

               return sb.substring(0, sb.length() - 1);
            }, LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)))
            .entrySet()
            .stream()
            .map(Entry::getValue)
            .collect(Collectors.toCollection(LimsTemplateDataList::new))
         : new LimsTemplateDataList<>();
   }

   default LimsTemplateDataList<LimsTemplateDataMap> cross(String keyField, String valueField, List<LimsTemplateDataMap> list, String... groupField) {
      if (!CollectionUtils.isEmpty(list) && !StringUtils.isEmpty(keyField) && !StringUtils.isEmpty(valueField)) {
         Map<String, List<Object>> group = list.stream().collect(Collectors.groupingBy(data -> {
            StringBuilder sb = new StringBuilder();
            if (groupField != null && groupField.length != 0) {
               for (String field : groupField) {
                  sb.append(ObjectUtils.toString(ReflectUtils.getPropertyValue(data, field))).append("-");
               }

               return sb.toString();
            } else {
               return ReflectUtils.getPropertyValue(data, "id");
            }
         }, LinkedHashMap::new, Collectors.toList()));
         String[] titleFieldArray = StringUtils.split(keyField, "|");
         return group.values().stream().map(valueList -> {
            LimsTemplateDataMap<String> dataMap = new LimsTemplateDataMap<>();
            if (groupField != null && groupField.length != 0) {
               for (String field : groupField) {
                  dataMap.put(field, ReflectUtils.getPropertyValue(valueList.get(0), field));
               }

               valueList.forEach(valueData -> {
                  StringBuilder sb = new StringBuilder();

                  for (String tField : titleFieldArray) {
                     String title = ObjectUtils.toString(ReflectUtils.getPropertyValue(valueData, tField));
                     if (!StringUtils.isEmpty(title)) {
                        sb.append(title).append("-");
                     }
                  }

                  if (!StringUtils.isEmpty(sb.toString())) {
                     String key = sb.substring(0, sb.length() - 1);
                     dataMap.put(key, ReflectUtils.getPropertyValue(valueData, valueField));
                  }
               });
            } else {
               dataMap.put("id", ReflectUtils.getPropertyValue(valueList.get(0), "id"));
            }

            return dataMap;
         }).collect(Collectors.toCollection(LimsTemplateDataList::new));
      } else {
         return new LimsTemplateDataList<>();
      }
   }

   default LimsTemplateDataList<LimsTemplateDataMap<Object>> cross(
      LimsTemplateDataList<LimsTemplateDataMap<Object>> list, String subField, String keyField, String valueField
   ) {
      return !CollectionUtils.isEmpty(list) && !StringUtils.isEmpty(keyField) && !StringUtils.isEmpty(valueField) ? list.stream().map(item -> {
         LimsTemplateDataMap<Object> newItem = new LimsTemplateDataMap<>();
         newItem.putAll(item);
         LimsTemplateDataList<LimsTemplateDataMap> subDataList = item.get(subField);
         if (!CollectionUtils.isEmpty(subDataList)) {
            LimsTemplateDataMap<Object> subMap = subDataList.stream().collect(Collectors.toMap(subData -> {
               String key = ObjectUtils.toString(subData.get(keyField));
               return StringUtils.isEmpty(key) ? "-" : key;
            }, subData -> Optional.ofNullable(subData.get(valueField)).orElse(""), (oldValue, newValue) -> newValue, LimsTemplateDataMap::new));
            newItem.putAll(subMap);
         }

         return newItem;
      }).collect(Collectors.toCollection(LimsTemplateDataList::new)) : new LimsTemplateDataList<>();
   }

   default <T> LimsTemplateDataList<LimsTemplateDataList<T>> groupL(List<T> list, int subListCount) {
      if (!CollectionUtils.isEmpty(list) && subListCount != 0) {
         int listSize = list.size();
         int maxCount = listSize / subListCount + (listSize % subListCount > 0 ? 1 : 0);
         return this.group(list, maxCount);
      } else {
         return new LimsTemplateDataList<>();
      }
   }

   default <T> LimsTemplateDataList<T> find(LimsTemplateDataList<T> list, String field, Object value) {
      if (CollectionUtils.isEmpty(list)) {
         return list;
      } else {
         String[] fieldArray = StringUtils.split(field, "_");
         String finalField = fieldArray[0];
         MatchPattern pattern = fieldArray.length == 1 ? MatchPattern.EQ : MatchPattern.getPatternIfPossible(fieldArray[1].toUpperCase());
         List<String> valueList = new ArrayList<>();
         if (pattern.equals(MatchPattern.OR)) {
            valueList = Arrays.asList(StringUtils.split(ObjectUtils.toString(value), ","));
         }

         List<String> finalValueList = valueList;
         return list.stream()
            .filter(
               data -> {
                  Object dataValue = ReflectUtils.getPropertyValue((T)data, finalField);
                  if (ObjectUtils.equals(value, "notNull")) {
                     return dataValue != null;
                  } else if (fieldArray.length == 1) {
                     return ObjectUtils.equals(dataValue, value);
                  } else {
                     boolean find;
                     switch (pattern) {
                        case OR:
                           find = finalValueList.stream()
                              .anyMatch(
                                 findValue -> ConvertUtils.canConvert(findValue, dataValue.getClass())
                                    ? ObjectUtils.equals(dataValue, ConvertUtils.convert(findValue, dataValue.getClass()))
                                    : false
                              );
                           break;
                        case SC:
                        default:
                           find = StringUtils.containsIgnoreCase(ObjectUtils.toString(dataValue), ObjectUtils.toShortString(value));
                     }

                     return find;
                  }
               }
            )
            .collect(Collectors.toCollection(LimsTemplateDataList::new));
      }
   }

   default LimsTemplateDataMap findFirst(LimsTemplateDataList<LimsTemplateDataMap> list, String field, Object value) {
      if (CollectionUtils.isEmpty(list)) {
         return new LimsTemplateDataMap();
      } else {
         String[] fieldArray = StringUtils.split(field, "_");
         String finalField = fieldArray[0];
         MatchPattern pattern = fieldArray.length == 1 ? MatchPattern.EQ : MatchPattern.getPatternIfPossible(fieldArray[1].toUpperCase());
         List<String> valueList = new ArrayList<>();
         if (pattern.equals(MatchPattern.OR)) {
            valueList = Arrays.asList(StringUtils.split(ObjectUtils.toString(value), ","));
         }

         List<String> finalValueList = valueList;
         return list.stream()
            .filter(
               data -> {
                  Object dataValue = ReflectUtils.getPropertyValue(data, finalField);
                  if (ObjectUtils.equals(value, "notNull")) {
                     return dataValue != null;
                  } else if (fieldArray.length == 1) {
                     return ObjectUtils.equals(dataValue, value);
                  } else {
                     boolean find;
                     switch (pattern) {
                        case OR:
                           find = finalValueList.stream()
                              .anyMatch(
                                 findValue -> ConvertUtils.canConvert(findValue, dataValue.getClass())
                                    ? ObjectUtils.equals(dataValue, ConvertUtils.convert(findValue, dataValue.getClass()))
                                    : false
                              );
                           break;
                        case SC:
                        default:
                           find = StringUtils.containsIgnoreCase(ObjectUtils.toString(dataValue), ObjectUtils.toShortString(value));
                     }

                     return find;
                  }
               }
            )
            .findFirst()
            .orElse(new LimsTemplateDataMap());
      }
   }

   default String joinF(List<? extends Serializable> list, String field, String... pattern) {
      return CollectionUtils.isEmpty(list)
         ? null
         : this.join(list.stream().map(obj -> ObjectUtils.toString(ReflectUtils.getPropertyValue(obj, field))).collect(Collectors.toList()), pattern);
   }

   default String joinD(List<? extends Serializable> list, String field, String... pattern) {
      return CollectionUtils.isEmpty(list)
         ? null
         : this.join(list.stream().map(obj -> ObjectUtils.toString(ReflectUtils.getPropertyValue(obj, field))).distinct().collect(Collectors.toList()), pattern);
   }

   default String join(List<? extends Serializable> list, String... pattern) {
      if (CollectionUtils.isEmpty(list)) {
         return null;
      } else {
         String join = pattern.length > 0 ? pattern[0] : ",";
         if (CollectionUtils.isEmpty(list)) {
            return pattern.length > 1 ? pattern[1] : "";
         } else {
            return StringUtils.join(list, join);
         }
      }
   }

   default <T> String joinG(LimsTemplateDataList<T> list, String field, String... pattern) {
      if (CollectionUtils.isEmpty(list)) {
         return null;
      } else {
         if (pattern.length > 0) {
            String var10000 = pattern[0];
         } else {
            String var13 = ",";
         }

         if (CollectionUtils.isEmpty(list)) {
            return pattern.length > 1 ? pattern[1] : "";
         } else {
            String subJoin = pattern.length > 2 ? pattern[2] : "~";
            List<Long> valueList = list.stream()
               .map(obj -> NumberUtils.parseLong(ReflectUtils.getPropertyValue((T)obj, field)))
               .distinct()
               .sorted(Comparator.comparing(Function.identity()))
               .collect(Collectors.toList());
            Long lastValue = null;
            List<List<Long>> tempList = new ArrayList<>();
            List<Long> dataList = new ArrayList<>();

            for (Long value : valueList) {
               if (lastValue == null) {
                  lastValue = value;
                  dataList.add(value);
                  tempList.add(dataList);
               } else {
                  if (!value.equals(lastValue + 1L)) {
                     dataList = new ArrayList<>();
                     tempList.add(dataList);
                  }

                  dataList.add(value);
                  lastValue = value;
               }
            }

            List<String> groupList = tempList.stream()
               .map(subList -> subList.size() == 1 ? subList.get(0) + "" : subList.get(0) + subJoin + subList.get(subList.size() - 1))
               .collect(Collectors.toList());
            return this.join(groupList, pattern);
         }
      }
   }

   default NumberingRenderData nr(String split, List<? extends Serializable> list, String... fieldArray) {
      if (!CollectionUtils.isEmpty(list) && !ObjectUtils.isEmpty(fieldArray)) {
         String splitString = StringUtils.isEmpty(split) ? "" : split;
         List<TextRenderData> dataList = list.stream().map(obj -> {
            StringBuilder sb = new StringBuilder();
            if (fieldArray.length == 0) {
               return new TextRenderData(ObjectUtils.toString(obj));
            } else {
               for (String field : fieldArray) {
                  sb.append(ObjectUtils.toString(ReflectUtils.getPropertyValue(obj, field))).append(splitString);
               }

               return new TextRenderData(sb.substring(0, sb.length() - splitString.length()));
            }
         }).collect(Collectors.toList());
         return new NumberingRenderData(dataList.toArray(new TextRenderData[0]));
      } else {
         return null;
      }
   }

   default NumberingRenderData nrD(String split, List<? extends Serializable> list, String... fieldArray) {
      if (!CollectionUtils.isEmpty(list) && !ObjectUtils.isEmpty(fieldArray)) {
         String splitString = StringUtils.isEmpty(split) ? "" : split;
         List<TextRenderData> dataList = list.stream().map(obj -> {
            if (fieldArray.length == 0) {
               return ObjectUtils.toString(obj);
            } else {
               StringBuilder sb = new StringBuilder();

               for (String field : fieldArray) {
                  sb.append(ObjectUtils.toString(ReflectUtils.getPropertyValue(obj, field))).append(splitString);
               }

               return sb.substring(0, sb.length() - splitString.length());
            }
         }).distinct().map(TextRenderData::new).collect(Collectors.toList());
         return new NumberingRenderData(dataList.toArray(new TextRenderData[0]));
      } else {
         return null;
      }
   }

   default NumberingRenderData nrL(List<CoreFileBean> list) {
      return this.nrL(list, true);
   }

   default NumberingRenderData nrL(List<CoreFileBean> list, boolean showUrl) {
      if (CollectionUtils.isEmpty(list)) {
         return null;
      } else {
         List<HyperlinkTextRenderData> dataList = list.stream().map(coreFile -> {
            String url = LimsFileUtils.getFileDownLoadUrl(coreFile);
            String text = coreFile.getName() + (showUrl ? "\n" + url : "");
            return new HyperlinkTextRenderData(text, url);
         }).collect(Collectors.toList());
         return new NumberingRenderData(dataList.toArray(new HyperlinkTextRenderData[0]));
      }
   }

   default String equals(String str1, String str2) {
      return String.valueOf(StringUtils.equals(str1, str2));
   }
}
