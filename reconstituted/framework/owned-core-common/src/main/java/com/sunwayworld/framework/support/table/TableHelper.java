package com.sunwayworld.framework.support.table;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.table.bean.TableCellBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TableHelper {
   public static final <T extends AbstractBaseData> List<TableRowBean> parseTableRowList(List<T> tableCellList, TableCellDescriptor<T> descriptor) {
      if (tableCellList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<String> pivotList = descriptor.getPivotList();
         List<String> extList = descriptor.getExtList();
         if (pivotList.isEmpty()) {
            return CollectionUtils.emptyList();
         } else {
            List<TableRowBean> rowList = new ArrayList<>();

            for (T c : tableCellList) {
               TableCellBean cell = new TableCellBean();
               cell.setKey(descriptor.getParseCellKeyFunc().apply(c));
               cell.setValue(descriptor.getParseCellValueFunc().apply(c));
               cell.setExt$Item("$field", descriptor.getCellFieldName());
               cell.setExt$Item("id", descriptor.getParseIdFunc().apply(c));

               for (String ext : extList) {
                  String extValue = ConvertUtils.convert(BeanUtils.getPropertyValue(c, ext), String.class);
                  cell.setExt$Item(ext, extValue);
               }

               StringBuilder pivotText = new StringBuilder();
               Map<String, Object> pivotValueMap = new HashMap<>();

               for (String p : pivotList) {
                  String pValue = ConvertUtils.convert(BeanUtils.getPropertyValue(c, p), String.class);
                  pivotText.append(p).append(":").append(pValue).append(";");
                  pivotValueMap.put("pivot_" + p, pValue);
               }

               TableRowBean row = rowList.stream().filter(r -> r.getPivotText().equals(pivotText.toString())).findFirst().orElse(null);
               if (null == row) {
                  TableRowBean nextRow = new TableRowBean();
                  nextRow.setPivotText(pivotText.toString());
                  nextRow.put(cell.getKey(), cell.getValue());
                  nextRow.setExt$Item(cell.getKey() + "_" + "ext$", cell.getExt$());
                  nextRow.setExt$(pivotValueMap);
                  rowList.add(nextRow);
               } else {
                  row.put(cell.getKey(), cell.getValue());
                  row.setExt$Item(cell.getKey() + "_" + "ext$", cell.getExt$());
                  row.setExt$(pivotValueMap);
               }
            }

            return rowList;
         }
      }
   }
}
