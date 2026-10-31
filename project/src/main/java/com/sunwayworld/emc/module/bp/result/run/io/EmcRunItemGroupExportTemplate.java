/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.bp.result.run.io;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
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

@Component(value="emcRunItemGroupExportTemplate")
@GikamBean
public class EmcRunItemGroupExportTemplate
implements GenericTemplateService<String> {
    private static final String UNIQUE_KEYWORD = "%d$%s$%s";
    private static final String REPEAT_NO_ITEM_KEYWORD = "%d-%s(%s)";
    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;
    @Autowired
    private EmcProjectService projectService;
    @Autowired
    private EmcTestService testService;
    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<Pair<String, String>>();
        Pair<String, String> templatePair = Pair.of("emcRunItemGroupExportTemplate", "\u5206\u6790\u5f55\u5165(\u591a\u56e0\u5b50)\u5bfc\u5165\u7ed3\u679c\u6a21\u7248-" + System.currentTimeMillis());
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String id) {
        List<EmcOrderTaskResultBean> orderTaskResultBeforeList = this.orderTaskResultService.selectListByFilter(SearchFilter.instance().match("RUNID", id).filter(MatchPattern.EQ).match("SCRIPTFLAG", "0").filter(MatchPattern.EQ), new Order[0]);
        List<Long> projIdList = orderTaskResultBeforeList.stream().filter(r -> !ObjectUtils.isEmpty(r.getProjId())).map(EmcOrderTaskResultBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projList = this.projectService.getDao().selectListByIds(projIdList, new Order[0]);
        List<Long> testIdList = orderTaskResultBeforeList.stream().map(EmcOrderTaskResultBean::getTestId).distinct().collect(Collectors.toList());
        List<EmcTestBean> testList = this.testService.getDao().selectListByIds(testIdList, new Order[0]);
        List<Long> orderTaskIdList = orderTaskResultBeforeList.stream().map(EmcOrderTaskResultBean::getOrderTaskId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = this.orderTaskService.getDao().selectListByIds(orderTaskIdList, new Order[0]);
        List<Long> exisedOrderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultBeforeList.stream().filter(res -> exisedOrderTaskIdList.contains(res.getOrderTaskId())).collect(Collectors.toList());
        orderTaskResultList.forEach(res -> {
            List<EmcProjectBean> matchProjList = projList.stream().filter(p -> ObjectUtils.equals(res.getProjId(), p.getId())).collect(Collectors.toList());
            EmcTestBean test = testList.stream().filter(t -> ObjectUtils.equals(res.getTestId(), t.getId())).collect(Collectors.toList()).get(0);
            List<EmcOrderTaskBean> ordertaskList = orderTaskList.stream().filter(ok -> ObjectUtils.equals(res.getOrderTaskId(), ok.getId())).collect(Collectors.toList());
            if (!matchProjList.isEmpty()) {
                res.setExt$Item("projno", matchProjList.get(0).getProjNo());
                res.setExt$Item("testname", test.getTestName());
                if (!ordertaskList.isEmpty()) {
                    res.setExt$Item("cupNo", ordertaskList.get(0).getCupNo().toString());
                }
            }
        });
        this.writeExcel(orderTaskResultList, path);
    }

    protected void writeExcel(List<EmcOrderTaskResultBean> orderTaskResultList, Path path) {
        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString());){
            orderTaskResultList.sort(Comparator.comparing(r -> NumberUtils.parseLong(r.getExt$Item("cupNo"))));
            List<String> cupNoList = orderTaskResultList.stream().map(e -> e.getExt$Item("cupno")).distinct().collect(Collectors.toList());
            HSSFSheet firstSheet = wb.getSheetAt(0);
            firstSheet.setColumnWidth(0, 0);
            HSSFCellStyle titleStyle = this.buildCellStyle(wb, IndexedColors.YELLOW.getIndex());
            HSSFCellStyle valueStyle = this.buildCellStyle(wb, IndexedColors.LIME.getIndex());
            HSSFCellStyle whiteStyle = this.buildCellStyle(wb, IndexedColors.WHITE.getIndex());
            AtomicInteger columnIndex = new AtomicInteger(0);
            for (String cupNo : cupNoList) {
                EmcOrderTaskResultBean cupNoResult = orderTaskResultList.stream().filter(e -> ObjectUtils.equals(e.getExt$Item("cupno"), cupNo)).findFirst().orElse(null);
                orderTaskResultList.stream().filter(e -> ObjectUtils.equals(e.getExt$Item("cupno"), cupNo)).findFirst().orElse(null);
                HSSFRow firstRow = Optional.ofNullable(firstSheet.getRow(0)).orElseGet(() -> firstSheet.createRow(0));
                HSSFCell qcCell = Optional.ofNullable(firstRow.getCell(columnIndex.get() + 3)).orElseGet(() -> firstRow.createCell(columnIndex.get() + 3));
                qcCell.setCellValue(cupNoResult.getQcName());
                qcCell.setCellStyle(titleStyle);
                HSSFRow secondRow = Optional.ofNullable(firstSheet.getRow(1)).orElseGet(() -> firstSheet.createRow(1));
                HSSFCell orderNoCell = Optional.ofNullable(secondRow.getCell(columnIndex.get() + 3)).orElseGet(() -> secondRow.createCell(columnIndex.get() + 3));
                orderNoCell.setCellValue(cupNoResult.getOrderNo());
                orderNoCell.setCellStyle(titleStyle);
                HSSFRow thirdRow = Optional.ofNullable(firstSheet.getRow(2)).orElseGet(() -> firstSheet.createRow(2));
                HSSFCell pointCell = Optional.ofNullable(thirdRow.getCell(columnIndex.get() + 3)).orElseGet(() -> thirdRow.createCell(columnIndex.get() + 3));
                pointCell.setCellValue(cupNoResult.getExt$Item("monitorpointname"));
                pointCell.setCellStyle(titleStyle);
                columnIndex.incrementAndGet();
            }
            HSSFPatriarch patriarch = firstSheet.createDrawingPatriarch();
            AtomicInteger markCommentRowIndex = new AtomicInteger(2);
            Map<String, List<EmcOrderTaskResultBean>> itemResultMap = orderTaskResultList.stream().collect(Collectors.groupingBy(r -> String.format(UNIQUE_KEYWORD, r.getRepeatNo(), r.getItemAliasName(), r.getExt$Item("testname")), LinkedHashMap::new, Collectors.toList()));
            int itemSize = itemResultMap.size();
            itemResultMap.forEach((key, resultList) -> {
                HSSFRow markCommentRow = Optional.ofNullable(firstSheet.getRow(markCommentRowIndex.incrementAndGet())).orElseGet(() -> firstSheet.createRow(markCommentRowIndex.get()));
                HSSFRow realValueRow = Optional.ofNullable(firstSheet.getRow(markCommentRowIndex.get() + itemSize)).orElseGet(() -> firstSheet.createRow(markCommentRowIndex.get() + itemSize));
                markCommentRow.setZeroHeight(true);
                resultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getId));
                List<String> resultIdList = resultList.stream().map(r -> r.getId() + "").collect(Collectors.toList());
                resultList.forEach(result -> {
                    List<String> fixedValueList = Arrays.asList(String.join((CharSequence)",", resultIdList), result.getExt$Item("testname"), String.format(REPEAT_NO_ITEM_KEYWORD, result.getRepeatNo(), result.getItemAliasName(), Optional.ofNullable(result.getUnitName()).orElse("")));
                    IntStream.range(0, fixedValueList.size()).forEach(i -> {
                        HSSFCell markCell = Optional.ofNullable(markCommentRow.getCell(i)).orElseGet(() -> markCommentRow.createCell(i));
                        HSSFCell realCell = Optional.ofNullable(realValueRow.getCell(i)).orElseGet(() -> realValueRow.createCell(i));
                        markCell.setCellValue((String)fixedValueList.get(i));
                        realCell.setCellValue((String)fixedValueList.get(i));
                        realCell.setCellStyle(whiteStyle);
                    });
                    IntStream.range(0, cupNoList.size()).filter(i -> ((String)cupNoList.get(i)).equals(result.getExt$Item("cupno"))).findFirst().ifPresent(i -> {
                        HSSFCell markCell = Optional.ofNullable(markCommentRow.getCell(3 + i)).orElseGet(() -> markCommentRow.createCell(3 + i));
                        HSSFComment comment = Optional.ofNullable(markCell.getCellComment()).orElseGet(() -> patriarch.createComment(new HSSFClientAnchor(1, 1, 1, 1, (short)4, 4, (short)0, 0)));
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
        }
        catch (IOException e2) {
            throw new FileException(e2);
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
