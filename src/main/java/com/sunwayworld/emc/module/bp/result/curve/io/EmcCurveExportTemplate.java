package com.sunwayworld.emc.module.bp.result.curve.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

@Component(value = "emcCurveExportTemplate")
public class EmcCurveExportTemplate implements GenericExportTemplate<String> {

    /** 表头动态列开始下标（模板中已有固定的3列） */
    private static final int DYNAMIC_COLUMN_START_INDEX = 3;

    private static final int DYNAMIC_ROW_START_INDEX = 1;

    private static final String STAND_QTY = "标准物量";
    private static final String RN6 = "标液加入量";
    private static final String ORIGINAL_RESULT = "原始结果";

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        // 此方法可以不用实现，因为下面重写了getDownLoadTemplateUrl方法
        return null;
    }

    @Override
    public String getDownLoadTemplateUrl(String templateCode, String id) {
        // 导出的Excel模板名称格式：点位编号 + 当前时间
        EmcFolderBean folder = folderService.selectById(NumberUtils.parseLong(id));
        String dateStr = DateTimeUtils.formatLocalDate(LocalDate.now());
        String fileName = Optional.ofNullable(folder).map(f -> f.getFolderNo() + "_" + dateStr).orElse(dateStr);

        FileRepresentation fr = FileRepresentation.of(FileScope.temp.name(), "" + System.currentTimeMillis(), UUID.randomUUID().toString(), fileName + ".xls");
        try {
            Files.createDirectories(FileHelper.getAbsoluteDirPath(fr));

            URL resource = this.getClass().getResource("");
            if (FileUtils.isInJarURL(resource)) {
                InputStream is = this.getClass().getResourceAsStream("templates/" + templateCode + ".xls");
                Files.copy(is, FileHelper.getAbsolutePath(fr), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path templatePath = new File(resource.getPath()).toPath().resolve("templates").resolve(templateCode + ".xls");
                Files.copy(templatePath, FileHelper.getAbsolutePath(fr), StandardCopyOption.REPLACE_EXISTING);
            }

            initTemplateData(FileHelper.getAbsolutePath(fr), id);
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }

        return FileHelper.getUrl(fr);
    }

    @Override
    public void initTemplateData(Path path, String id) {
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("FOLDERID", id).filter(MatchPattern.EQ));
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("FOLDERID", id).filter(MatchPattern.EQ));

        List<String> itemNameList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemName).distinct().collect(Collectors.toList());

        // 过滤出指标名称为曲线校准的结果
        List<EmcOrderTaskResultBean> curveTaskResultList = orderTaskResultList.stream().filter(e -> "NUMERIC_CON_NOB".equals(e.getExt$Item("setname"))
                || "NUMERIC_CON".equals(e.getExt$Item("setname"))).collect(Collectors.toList());

        // 过滤出不存在公式的结果（不存在公式的才需要手动录入）
        List<EmcOrderTaskResultBean> noScriptTaskResultList = orderTaskResultList.stream().filter(e -> Constant.NO.equals(e.getScriptFlag())).collect(Collectors.toList());

        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString())) {

            // 默认获取第一个Sheet表格
            HSSFSheet firstSheet = wb.getSheetAt(0);
            // 隐藏表格的第一列
            firstSheet.setColumnWidth(0, 0);
            HSSFRow firstRow = Optional.ofNullable(firstSheet.getRow(0)).orElseGet(() -> firstSheet.createRow(0));

            List<String> dynamicColumnList = new ArrayList<>();
            itemNameList.forEach(n -> {
                curveTaskResultList.stream().filter(r -> StringUtils.equals(r.getItemName(), n)).findFirst().ifPresent(r -> {
                    dynamicColumnList.add(n + STAND_QTY);
                    dynamicColumnList.add(RN6);
                });
                noScriptTaskResultList.stream().filter(r -> StringUtils.equals(r.getItemName(), n)).findFirst().ifPresent(r -> dynamicColumnList.add(n + ORIGINAL_RESULT));
            });

            // 创建表头动态列
            CellStyle headerCellStyle = buildCellStyle(wb, IndexedColors.YELLOW.getIndex());
            IntStream.range(0, dynamicColumnList.size()).forEach(i -> {
                Cell cell = Optional.ofNullable(firstRow.getCell(DYNAMIC_COLUMN_START_INDEX + i)).orElseGet(() -> firstRow.createCell(DYNAMIC_COLUMN_START_INDEX + i));
                cell.setCellValue(dynamicColumnList.get(i));
                cell.setCellStyle(headerCellStyle);
            });

            // 构建表体行数据
            List<List<String>> rowDataList = new ArrayList<>(orderTaskList.size());
            IntStream.range(0, orderTaskList.size()).forEach(i -> {
                EmcOrderTaskBean orderTask = orderTaskList.get(i);
                List<String> rowData = new ArrayList<>();
                rowData.add(orderTask.getId() + "");
                rowData.add(orderTask.getExt$().get("orderno"));
                rowData.add(orderTask.getQcCategoryCode());

                List<EmcOrderTaskResultBean> currentTaskResultList = orderTaskResultList.stream().filter(r -> ObjectUtils.equals(r.getOrderTaskId(), orderTask.getId())).collect(Collectors.toList());
                dynamicColumnList.forEach(c -> currentTaskResultList.stream().filter(r -> c.contains(r.getItemName())).findFirst().ifPresent(r -> {
                        if (c.contains(ORIGINAL_RESULT)) {
                            rowData.add(r.getOriginalResult());
                        } else if (c.contains(STAND_QTY)) {
                            rowData.add(r.getStandQty());
                            rowData.add(r.getRn6());
                        }
                    })
                );

                rowDataList.add(rowData);
            });

            CellStyle dataCellStyle = buildCellStyle(wb, IndexedColors.LIME.getIndex());
            IntStream.range(0, rowDataList.size()).forEach(i -> {
                List<String> rowData = rowDataList.get(i);
                Row row = Optional.ofNullable(firstSheet.getRow(DYNAMIC_ROW_START_INDEX + i)).orElseGet(() -> firstSheet.createRow(DYNAMIC_ROW_START_INDEX + i));
                IntStream.range(0, rowData.size()).forEach(j -> {
                    Cell cell = Optional.ofNullable(row.getCell(j)).orElse(row.createCell(j));
                    if (j >= DYNAMIC_COLUMN_START_INDEX) {
                        cell.setCellStyle(dataCellStyle);
                    }
                    cell.setCellValue(Optional.ofNullable(rowData.get(j)).orElse(""));
                });
            });

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    private CellStyle buildCellStyle(HSSFWorkbook wb, short bg) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(bg);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 设置单元格为文本格式
        HSSFDataFormat dataFormat = wb.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat("@"));

        return cellStyle;
    }

}
