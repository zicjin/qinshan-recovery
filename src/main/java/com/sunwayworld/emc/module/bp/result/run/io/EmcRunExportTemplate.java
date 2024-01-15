package com.sunwayworld.emc.module.bp.result.run.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;

/**
 * @author zhengxy@sunwayworld.com
 */
@Component(value = "emcRunExportTemplate")
@GikamBean
public class EmcRunExportTemplate implements GenericTemplateService<String> {

    /** 唯一性标识，重复数+分析项+测试名 */
    protected static final String UNIQUE_KEYWORD = "%d$%s$%s";

    protected static final String REPEAT_NO_ITEM_KEYWORD = "%d-%s(%s)";

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcRunExportTemplate", "分析录入导入结果模版-" + System.currentTimeMillis());
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String id) {
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("RUNID", id).filter(MatchPattern.EQ)
                .match("SCRIPTFLAG", "0").filter(MatchPattern.EQ));

        writeExcel(orderTaskResultList, path);
    }

    protected void writeExcel(List<EmcOrderTaskResultBean> orderTaskResultList, Path path) {
        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString())) {

            Map<String, String> uniqueItemUnitMap = orderTaskResultList.stream().collect(Collectors.toMap(r -> String.format(UNIQUE_KEYWORD, r.getRepeatNo(), r.getItemAliasName(), r.getExt$Item("testname")),
                    r -> String.format(REPEAT_NO_ITEM_KEYWORD, r.getRepeatNo(), r.getItemAliasName(), Optional.ofNullable(r.getUnitName()).orElse("")),  (key1, key2) -> key2, LinkedHashMap::new));
            List<String> uniqueItemList = new ArrayList<>(uniqueItemUnitMap.keySet());

            HSSFSheet firstSheet = wb.getSheetAt(0);
            // 隐藏表格的第一列
            firstSheet.setColumnWidth(0, 0);
            HSSFCellStyle titleStyle = buildCellStyle(wb, IndexedColors.YELLOW.getIndex());
            HSSFCellStyle valueStyle = buildCellStyle(wb, IndexedColors.LIME.getIndex());
            HSSFCellStyle whiteStyle = buildCellStyle(wb, IndexedColors.WHITE.getIndex());

            // 创建第一行、第二行标题列
            AtomicInteger columnIndex = new AtomicInteger(0);
            uniqueItemUnitMap.forEach((uniqueItemName, itemUnit) -> {
                HSSFRow firstRow = Optional.ofNullable(firstSheet.getRow(0)).orElseGet(() -> firstSheet.createRow(0));
                HSSFCell testCell = Optional.ofNullable(firstRow.getCell(columnIndex.get() + 3)).orElseGet(() -> firstRow.createCell(columnIndex.get() + 3));
                testCell.setCellValue(uniqueItemName.substring(uniqueItemName.lastIndexOf("$") + 1));
                testCell.setCellStyle(titleStyle);

                HSSFRow secondRow = Optional.ofNullable(firstSheet.getRow(1)).orElseGet(() -> firstSheet.createRow(1));
                HSSFCell itemCell = Optional.ofNullable(secondRow.getCell(columnIndex.get() + 3)).orElseGet(() -> secondRow.createCell(columnIndex.get() + 3));
                itemCell.setCellValue(itemUnit);
                itemCell.setCellStyle(titleStyle);
                columnIndex.incrementAndGet();
            });

            HSSFPatriarch patriarch = firstSheet.createDrawingPatriarch();
            AtomicInteger markCommentRowIndex = new AtomicInteger(1);
            orderTaskResultList.sort(Comparator.comparing(r -> NumberUtils.parseLong(r.getExt$Item("cupNo"))));
            Map<String, List<EmcOrderTaskResultBean>> orderNoResultMap = orderTaskResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("cupNo"), LinkedHashMap::new, Collectors.toList()));

            int orderNoSize = orderNoResultMap.size();
            orderNoResultMap.forEach((cupNo, resultList) -> {
                // 标注行
                HSSFRow markCommentRow = Optional.ofNullable(firstSheet.getRow(markCommentRowIndex.incrementAndGet())).orElseGet(() -> firstSheet.createRow(markCommentRowIndex.get()));
                // 真实数据行
                HSSFRow realValueRow = Optional.ofNullable(firstSheet.getRow(markCommentRowIndex.get() + orderNoSize)).orElseGet(() -> firstSheet.createRow(markCommentRowIndex.get() + orderNoSize));
                markCommentRow.setZeroHeight(true);
                resultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getId));
                List<String> resultIdList = resultList.stream().map(r -> r.getId() + "").collect(Collectors.toList());
                resultList.forEach(result -> {
                	String name = "";
                	if(!result.getQcName().equals("N/A")) {
                		name = result.getQcName();
                	}else {
                		name = result.getExt$Item("ordercategory") + "_" + result.getExt$Item("monitorpointname");
                	}
                    List<String> fixedValueList = Arrays.asList(String.join(",", resultIdList), result.getOrderNo(), name);
                    IntStream.range(0, fixedValueList.size()).forEach(i -> {
                        HSSFCell markCell = Optional.ofNullable(markCommentRow.getCell(i)).orElseGet(() -> markCommentRow.createCell(i));
                        HSSFCell realCell = Optional.ofNullable(realValueRow.getCell(i)).orElseGet(() -> realValueRow.createCell(i));
                        markCell.setCellValue(fixedValueList.get(i));
                        realCell.setCellValue(fixedValueList.get(i));
                        realCell.setCellStyle(whiteStyle);
                    });

                    // 创建备注单元格
                    String uniqueItemName = String.format(UNIQUE_KEYWORD, result.getRepeatNo(), result.getItemAliasName(), result.getExt$Item("testname"));
                    IntStream.range(0, uniqueItemList.size()).filter(i -> uniqueItemList.get(i).equals(uniqueItemName)).findFirst().ifPresent(i -> {
                        HSSFCell markCell = Optional.ofNullable(markCommentRow.getCell(3 + i)).orElseGet(() -> markCommentRow.createCell(3 + i));
                        HSSFComment comment = Optional.ofNullable(markCell.getCellComment()).orElseGet(() -> patriarch.createComment(new HSSFClientAnchor(1, 1, 1, 1, (short) 3, 3, (short) 0, 0)));
                        comment.setString(new HSSFRichTextString(result.getId() + ""));
                        markCell.setCellComment(comment);
                        markCell.setCellValue(result.getId() + "");

                        HSSFCell valueCell = Optional.ofNullable(realValueRow.getCell(3 + i)).orElseGet(() -> realValueRow.createCell(3 + i));
                        valueCell.setCellValue(result.getOriginalResult());
                        valueCell.setCellStyle(valueStyle);
                    });

                });
            });

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    protected HSSFCellStyle buildCellStyle(HSSFWorkbook wb, short foregroundColor) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(foregroundColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        return style;
    }

}
