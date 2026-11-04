package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LimsExpandTableFirstTemplateRenderData extends AbstractMergeTableRenderData implements Serializable {
   private int columnMaxExpandNum;
   private int columnStartExpandIndex;
   private String columnGroupField;
   private int rowStartExpandIndex;
   private String rowGroupField;
   private LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> splitDataList;

   public LimsExpandTableFirstTemplateRenderData(
      LimsTemplateDataList<LimsTemplateDataMap> dataList, String columnGroupField, int rowStartExpandIndex, String rowGroupField
   ) {
      super(dataList);
      this.columnGroupField = columnGroupField;
      this.columnStartExpandIndex = 1;
      this.rowStartExpandIndex = rowStartExpandIndex;
      this.rowGroupField = rowGroupField;
      this.columnMaxExpandNum = 3;
      this.splitDataList = this.split(dataList);
   }

   public int getColumnStartExpandIndex() {
      return this.columnStartExpandIndex;
   }

   public void setColumnStartExpandIndex(int columnStartExpandIndex) {
      this.columnStartExpandIndex = columnStartExpandIndex;
   }

   public int getColumnMaxExpandNum() {
      return this.columnMaxExpandNum;
   }

   public void setColumnMaxExpandNum(int columnMaxExpandNum) {
      this.columnMaxExpandNum = columnMaxExpandNum;
   }

   public String getColumnGroupField() {
      return this.columnGroupField;
   }

   public void setColumnGroupField(String columnGroupField) {
      this.columnGroupField = columnGroupField;
   }

   public int getRowStartExpandIndex() {
      return this.rowStartExpandIndex;
   }

   public void setRowStartExpandIndex(int rowStartExpandIndex) {
      this.rowStartExpandIndex = rowStartExpandIndex;
   }

   public String getRowGroupField() {
      return this.rowGroupField;
   }

   public void setRowGroupField(String rowGroupField) {
      this.rowGroupField = rowGroupField;
   }

   public LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> getSplitDataList() {
      return this.splitDataList;
   }

   public void setSplitDataList(LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> splitDataList) {
      this.splitDataList = splitDataList;
   }

   private LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> split(LimsTemplateDataList<LimsTemplateDataMap> dataList) {
      if (!CollectionUtils.isEmpty(dataList) && this.columnMaxExpandNum != 0 && !Objects.isNull(this.columnGroupField)) {
         LinkedHashMap<String, LimsTemplateDataList<LimsTemplateDataMap>> groupDataMap = dataList.stream()
            .collect(
               Collectors.groupingBy(
                  map -> ObjectUtils.toString(map.get(this.columnGroupField)), LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)
               )
            );
         LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> groupDataList = new LimsTemplateDataList<>(groupDataMap.values());
         int listSize = (int)Math.ceil((double)groupDataList.size() / this.columnMaxExpandNum);
         LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> splitDataList = new LimsTemplateDataList<>(listSize);
         IntStream.range(0, listSize).forEach(index -> {
            int start = index * this.columnMaxExpandNum;
            int end = Math.min(start + this.columnMaxExpandNum, groupDataList.size());
            List<LimsTemplateDataList<LimsTemplateDataMap>> curGroupDataList = groupDataList.subList(start, end);
            LimsTemplateDataList<LimsTemplateDataMap> curDataList = new LimsTemplateDataList<>();
            curGroupDataList.forEach(curDataList::addAll);
            splitDataList.add(curDataList);
         });
         return splitDataList;
      } else {
         return new LimsTemplateDataList<>();
      }
   }
}
