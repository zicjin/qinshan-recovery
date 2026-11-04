package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LimsFixedMultiColumnTemplateRenderData extends AbstractMergeTableRenderData implements Serializable {
   private static final int DEFAULT_COLUMN_SIZE = 2;
   private int columnSize;
   private int partitionSize;
   private String groupField;
   private String uniqueField;
   private String subUniqueField;
   private String valueField;
   private LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> groupPartitionList;
   private String showAvg;

   public LimsFixedMultiColumnTemplateRenderData(
      LimsTemplateDataList<LimsTemplateDataMap> dataList, int partitionSize, String groupField, String uniqueField, String subUniqueField, String valueField
   ) {
      super(dataList);
      this.partitionSize = partitionSize;
      this.groupField = groupField;
      this.uniqueField = uniqueField;
      this.subUniqueField = subUniqueField;
      this.valueField = valueField;
      this.columnSize = 2;
      this.showAvg = "0";
      this.groupPartitionList = this.groupBy(dataList);
   }

   public LimsFixedMultiColumnTemplateRenderData(
      LimsTemplateDataList<LimsTemplateDataMap> dataList,
      int partitionSize,
      String groupField,
      String uniqueField,
      String subUniqueField,
      String valueField,
      String showAvg
   ) {
      super(dataList);
      this.partitionSize = partitionSize;
      this.groupField = groupField;
      this.uniqueField = uniqueField;
      this.subUniqueField = subUniqueField;
      this.valueField = valueField;
      this.columnSize = 2;
      this.showAvg = showAvg;
      this.groupPartitionList = this.groupBy(dataList);
   }

   public int getColumnSize() {
      return this.columnSize;
   }

   public int getPartitionSize() {
      return this.partitionSize;
   }

   public String getGroupField() {
      return this.groupField;
   }

   public String getUniqueField() {
      return this.uniqueField;
   }

   public String getSubUniqueField() {
      return this.subUniqueField;
   }

   public String getValueField() {
      return this.valueField;
   }

   public LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> getGroupPartitionList() {
      return this.groupPartitionList;
   }

   public String getShowAvg() {
      return this.showAvg;
   }

   private LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> groupBy(LimsTemplateDataList<LimsTemplateDataMap> dataList) {
      if (!CollectionUtils.isEmpty(dataList) && this.partitionSize != 0 && !Objects.isNull(this.groupField)) {
         LinkedHashMap<String, LimsTemplateDataList<LimsTemplateDataMap>> originalGroupMap = dataList.stream()
            .collect(
               Collectors.groupingBy(
                  map -> ObjectUtils.toString(map.get(this.groupField)), LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)
               )
            );
         LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> originalGroupValueList = new LimsTemplateDataList<>(originalGroupMap.values());
         int listSize = (int)Math.ceil((double)originalGroupValueList.size() / this.partitionSize);
         LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> partitionList = new LimsTemplateDataList<>(listSize);
         IntStream.range(0, listSize).forEach(index -> {
            int start = index * this.partitionSize;
            int end = Math.min(start + this.partitionSize, originalGroupValueList.size());
            List<LimsTemplateDataList<LimsTemplateDataMap>> curDataList = originalGroupValueList.subList(start, end);
            LimsTemplateDataList<LimsTemplateDataMap> curPartitionList = new LimsTemplateDataList<>();
            curDataList.forEach(curPartitionList::addAll);
            partitionList.add(curPartitionList);
         });
         return partitionList;
      } else {
         return new LimsTemplateDataList<>();
      }
   }
}
