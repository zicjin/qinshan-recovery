package com.sunwayworld.emc.module.bp.info.infodata.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigBean;
import com.sunwayworld.emc.module.bp.info.infoconfig.service.EmcInfoConfigService;
import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataBean;
import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataItemBean;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataItemService;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataService;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplFieldService;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

@Component
public class EmcInfoDataImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcInfoDataService infoDataService;

    @Autowired
    private EmcInfoDataItemService infoDataItemService;

    @Autowired
    private EmcInfoConfigService infoConfigService;

    @Autowired
    private EmcInfoTmplFieldService infoTmplFieldService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();
        FileRepresentation fr = FileRepresentation.of(file);

        // 获取主表id
        Long infoConfigId = NumberUtils.parseLong(file.getTargetId().split("\\$")[1]);
        // 导入综合信息数据
        List<List<Map<String, Object>>> emcInfoList = extractInfoDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), infoConfigId, 0, 0, 1, EmcInfoDataBean.class);
        List<EmcInfoDataBean> emcInfoDataList = emcInfoList.get(0).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcInfoDataBean.class)).collect(Collectors.toList());
        List<EmcInfoDataItemBean> emcInfoDataItemList = emcInfoList.get(1).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcInfoDataItemBean.class)).collect(Collectors.toList());
        if (!emcInfoDataList.isEmpty() && !emcInfoDataItemList.isEmpty()) {
            infoDataService.getDao().insert(emcInfoDataList);
            infoDataItemService.getDao().insert(emcInfoDataItemList);
        } else {
            // 存在错误信息
            String errorFileName = "综合信息数据导入错误信息.xls";
            String url = ExcelHelper.writeImportFailureListAsExcel(FileHelper.getAbsolutePath(fr).toString(), emcInfoDataList, 0, 1, errorFileName);
            importResultRepresentation.setUrl(url);
        }
        return importResultRepresentation;
    }

    // 私有方法
    private <T extends BaseData> List<List<Map<String, Object>>> extractInfoDataFromExcel(String filePath, Long infoConfigId, Object sheetIndexOrSheetName, int headerRowIndex, int dataFromRowIndex,
            Class<T> clazz) {
        HSSFWorkbook wb = null;
        try (InputStream is = new BufferedInputStream(new FileInputStream(filePath));) {
            wb = new HSSFWorkbook(is);
            // 第一张表格
            HSSFSheet sheet = String.class.equals(sheetIndexOrSheetName.getClass()) ? wb.getSheet((String) sheetIndexOrSheetName) : wb.getSheetAt((int) sheetIndexOrSheetName);
            HSSFRow row = null;

            // 原始上传excel解析之后的数据
            List<Map<String, Object>> resultList = new ArrayList<>();
            // 转换列之后的数据分析项的数据
            List<Map<String, Object>> itemList = new ArrayList<>();

            row = sheet.getRow(headerRowIndex);
            int columnQty = row.getPhysicalNumberOfCells();

            int offset = 0;

            // 拼接从上传的excel表头的标题条件字符串
            StringBuilder builder = new StringBuilder("");
            // 上传的excel表头的标题数组
            String[] excelHeaders = new String[columnQty];
            for (int i = offset; i < columnQty; i++) {
                String cellValue = row.getCell(i).getStringCellValue();
                if (cellValue != null) {
                    String cellValueString = StringUtils.trim(cellValue.toString());
                    if (!StringUtils.isEmpty(cellValueString)) {
                        builder.append("," + cellValueString);
                        excelHeaders[i] = cellValueString;
                    }
                }
            }
            // 获取配置项的字段模板id
            EmcInfoConfigBean emcInfoConfig = infoConfigService.selectById(infoConfigId);
            RestJsonWrapperBean wrapper = new RestJsonWrapperBean();
            wrapper.setFilterValue("infoTmplId_SEQ", String.valueOf(emcInfoConfig.getInfoTmplId()));
            wrapper.setFilterValue("fieldTitle_IN", builder.toString().substring(1));

            // 根据字段模板id和字段标题查询字段的名称
            List<EmcInfoTmplFieldBean> emcInfoTmplFieldList = infoTmplFieldService.selectList(wrapper.extractMapFilter());

            String[] columnNames = new String[emcInfoTmplFieldList.size()];
            String[] columnHeaders = new String[emcInfoTmplFieldList.size()];
            // 需要列转换的所有字段和字段名集合
            List<String[]> convertColList = new ArrayList<>();
            // 该模板配置的所有联合主键字段名
            List<String> priKeyColumns = new ArrayList<>();
            // 监测点名称字段
            String monitorPointColName = "";
            // 监测日期名称字段
            String monitorDateColName = "";
            // 监测日期名称表头
            String monitorDateColHeader = "";
            // 导入时间
            LocalDateTime importTime = LocalDateTime.now();

            for (int i = 0; i < emcInfoTmplFieldList.size(); i++) {
                EmcInfoTmplFieldBean fieldTmpl = emcInfoTmplFieldList.get(i);
                columnNames[i] = StringUtils.trim(StringUtils.lowerCase(fieldTmpl.getField()));
                columnHeaders[i] = fieldTmpl.getFieldTitle().trim();
                if (Objects.nonNull(fieldTmpl.getPrimaryIndex()) && fieldTmpl.getPrimaryIndex() == EmcConstant.ONE) {
                    priKeyColumns.add(columnNames[i]);
                }
                if ((EmcConstant.EQUIPT_ACQ_FILE_STATUS_DONE).equals(fieldTmpl.getTransform())) {
                    String[] convertFields = new String[2];
                    convertFields[0] = StringUtils.trim(StringUtils.lowerCase(fieldTmpl.getField()));
                    convertFields[1] = fieldTmpl.getFieldTitle();
                    convertColList.add(convertFields);
                }
                if (StringUtils.isEmpty(monitorPointColName) && "MonitorProject".equals(fieldTmpl.getPrimaryName())) {
                    monitorPointColName = columnNames[i];
                }
                if (StringUtils.isEmpty(monitorDateColName) && "MonitorDate".equals(fieldTmpl.getPrimaryName())) {
                    monitorDateColName = columnNames[i];
                    monitorDateColHeader = columnHeaders[i];
                }
            }

            int rowId = dataFromRowIndex;
            while (true) {
                row = sheet.getRow(rowId++);

                if (row == null || StringUtils.isEmpty(ExcelHelper.getCellValue(row.getCell(0), String.class))) {
                    break;
                }

                Map<String, Object> map = new HashMap<String, Object>();
                int index = 0;
                for (int cellIndex = 0; cellIndex < excelHeaders.length; cellIndex++) {
                    HSSFCell cell = row.getCell(cellIndex);
                    // 取出当前cell的值和对应Javabean类的属性放入到map中
                    if (excelHeaders[cellIndex] != null && excelHeaders[cellIndex].length() > 0 && index < columnNames.length) { // 该列值在对应的java对象中有值
                        BeanPropertyDescriptor propertyDescriptor = BeanPropertyDescriptor.of(clazz, columnNames[cellIndex]);

                        Class<?> targetType = (propertyDescriptor == null ? String.class : propertyDescriptor.getPropertyType());

                        if (ExcelHelper.canConvertCellValue(cell, targetType)) {
                            Object value = ExcelHelper.getCellValue(cell, clazz, columnNames[index]);
                            String excelHeader = excelHeaders[cellIndex];
                            // 获取此单元格对应的字段模板的bean的属性名
                            int dataIndex = -1;
                            for (int i = 0; i < columnHeaders.length; i++) {
                                if (StringUtils.equals(excelHeader, columnHeaders[i])) {
                                    dataIndex = i;
                                    index++;
                                    break;
                                }
                            }
                            if (!ObjectUtils.isEmpty(value) && dataIndex > -1) {
                                map.put(columnNames[dataIndex], value);
                            }
                        } else {
                            updateValidationImpMsg(map, "第" + (cellIndex + 1) + "列数据错误，不能转换为数据类型【" + targetType.getSimpleName() + "】");
                        }

                    }
                }
                if (!map.isEmpty()) {
                    map.put("id", ApplicationContextHelper.getNextIdentity());
                    map.put("infoConfigId", infoConfigId);
                    map.put("createdTime", importTime);
                    if (Objects.nonNull(map.get(monitorDateColName))) {
                        LocalDate monitorDate = null;
                        try {
                            monitorDate= DateTimeUtils.parseLocalDate(String.valueOf(map.get(monitorDateColName)));
                        } catch (ConvertFailedException e) {
                            throw new CheckedException("EXCEL文档" + monitorDateColHeader + "列存在不为日期类型的数据");
                        }
                        map.put("monitorDate", monitorDate);
                    }
                    resultList.add(map);
                }

            }

            // 如果一行数据除了序号皆为空，则删除此行数据
            boolean isEmpty = true;
            for (int i = resultList.size() - 1; i >= 0; i--) {
                for (String key : resultList.get(i).keySet()) {
                    Object value = resultList.get(i).get(key);
                    if (!ObjectUtils.isEmpty(value)) {
                        isEmpty = false;
                    }
                }
                if (isEmpty) {
                    resultList.remove(i);
                }
            }
            List<Map<String, Object>> uniqueList = resultList.stream().distinct().collect(Collectors.toList());

            List<EmcInfoDataBean> infoDataList = infoDataService.selectListByFilter(SearchFilter.instance().match("INFOCONFIGID", infoConfigId).filter(MatchPattern.EQ), Order.asc("ID"));
            // 根据联合主键判断上传的重复数据需要删除的综合信息数据集合
            List<EmcInfoDataBean> deleteInfoDataList = new ArrayList<>();
            List<EmcInfoDataItemBean> deleteInfoDataItemList = new ArrayList<>();

            StringBuilder priKeyValue = new StringBuilder("");
            StringBuilder excelKeyValue = new StringBuilder("");
            infoDataList.forEach(e -> {
                priKeyValue.setLength(0);
                priKeyColumns.stream().forEach(x -> {
                    priKeyValue.append(String.valueOf(ReflectionUtils.getFieldValue(e, x)));
                });
                boolean match = uniqueList.stream().anyMatch(m -> {
                    excelKeyValue.setLength(0);
                    priKeyColumns.stream().forEach(x -> {
                        excelKeyValue.append(String.valueOf(m.get(x)));
                    });
                    return StringUtils.equals(excelKeyValue.toString(), priKeyValue.toString());
                });

                if (match) {
                    deleteInfoDataList.add(e);
                    EmcInfoDataItemBean emcInfoDataItem = new EmcInfoDataItemBean();
                    emcInfoDataItem.setInfoConfigId(e.getInfoConfigId());
                    emcInfoDataItem.setInfoDataId(e.getId());
                    deleteInfoDataItemList.add(emcInfoDataItem);
                }

            });

            infoDataItemService.getDao().deleteBy(deleteInfoDataItemList, "INFOCONFIGID", "INFODATAID");
            infoDataService.getDao().deleteBy(deleteInfoDataList);

            for (String[] convertCols : convertColList) {
                for (Map<String, Object> m : uniqueList) {
                    Map<String, Object> emcInfoDataItemMap = new HashMap<>();
                    emcInfoDataItemMap.put("id", ApplicationContextHelper.getNextIdentity());
                    emcInfoDataItemMap.put("infoConfigId", infoConfigId);
                    emcInfoDataItemMap.put("infoDataId", NumberUtils.parseLong(m.get("id")));
                    emcInfoDataItemMap.put("monitorPointName", String.valueOf(m.get(monitorPointColName)));
                    emcInfoDataItemMap.put("itemName", convertCols[1]);
                    emcInfoDataItemMap.put("result", String.valueOf(m.get(convertCols[0])));
                    emcInfoDataItemMap.put("monitorDate", DateTimeUtils.parseLocalDate(String.valueOf(m.get(monitorDateColName))));
                    itemList.add(emcInfoDataItemMap);
                }
            }

            // 存放需要插入综合信息数据表和综合信息数据分析项的数据集合
            List<List<Map<String, Object>>> dataList = new ArrayList<>();
            dataList.add(uniqueList);
            dataList.add(itemList);

            return dataList;

        } catch (IOException ioe) {
            throw new FileException(ioe);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }
        }
    }

    // 私有方法
    private static void updateValidationImpMsg(Map<String, Object> map, String msg) {
        String origMsg = (String) map.get(Constant.VALIDATION_IMP_MSG);

        if (StringUtils.isEmpty(origMsg)) {
            map.put(Constant.VALIDATION_IMP_MSG, msg);
        } else {
            map.put(Constant.VALIDATION_IMP_MSG, origMsg + "；" + msg);
        }
    }

}
