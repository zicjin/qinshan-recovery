package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LimsFixedLoopMultiRowTemplateRenderData extends AbstractMergeTableRenderData implements Serializable {
   private int loopRowCount;
   private int fixedColumnCount;
   private int dataStartColumnIndex;
   private String rowGroupField;
   private String columnGroupField;
   private LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> groupDataList;

   public LimsFixedLoopMultiRowTemplateRenderData(
      LimsTemplateDataList<LimsTemplateDataMap> dataList,
      int loopRowCount,
      int fixedColumnCount,
      int dataStartColumnIndex,
      String rowGroupField,
      String columnGroupField
   ) {
      super(dataList);
      this.loopRowCount = loopRowCount;
      this.fixedColumnCount = fixedColumnCount;
      this.dataStartColumnIndex = dataStartColumnIndex;
      this.rowGroupField = rowGroupField;
      this.columnGroupField = columnGroupField;
      this.groupDataList = this.groupBy(dataList);
   }

   public int getLoopRowCount() {
      return this.loopRowCount;
   }

   public int getFixedColumnCount() {
      return this.fixedColumnCount;
   }

   public int getDataStartColumnIndex() {
      return this.dataStartColumnIndex;
   }

   public String getRowGroupField() {
      return this.rowGroupField;
   }

   public String getColumnGroupField() {
      return this.columnGroupField;
   }

   public LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> getGroupDataList() {
      return this.groupDataList;
   }

   private LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> groupBy(LimsTemplateDataList<LimsTemplateDataMap> dataList) {
      if (!CollectionUtils.isEmpty(dataList) && this.loopRowCount != 0 && !Objects.isNull(this.rowGroupField)) {
         LinkedHashMap<String, LimsTemplateDataList<LimsTemplateDataMap>> originalGroupMap = dataList.stream()
            .collect(
               Collectors.groupingBy(
                  map -> ObjectUtils.toString(map.get(this.columnGroupField)), LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)
               )
            );
         LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> originalGroupValueList = new LimsTemplateDataList<>(originalGroupMap.values());
         int listSize = (int)Math.ceil((double)originalGroupValueList.size() / this.fixedColumnCount);
         LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> partitionList = new LimsTemplateDataList<>(listSize);
         IntStream.range(0, listSize).forEach(index -> {
            int start = index * this.fixedColumnCount;
            int end = Math.min(start + this.fixedColumnCount, originalGroupValueList.size());
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
