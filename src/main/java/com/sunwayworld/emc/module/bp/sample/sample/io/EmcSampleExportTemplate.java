package com.sunwayworld.emc.module.bp.sample.sample.io;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Component(value = "emcSampleExportTemplate")
public class EmcSampleExportTemplate implements GenericTemplateService<String> {

    /** 唯一性标识，重复数+分析项+测试名 */
    private static final String UNIQUE_KEYWORD = "%d$%s$%s";

    private static final String REPEAT_NO_ITEM_KEYWORD = "%d-%s(%s)";

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcOrderService orderService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcSampleExportTemplate", "现场数据录入-" + DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HHmm"));
        templatePairList.add(templatePair);
        return templatePairList;
    }

    //对于现场采样，导出excel存在超过256列的情况，因此由xls改为xlsx：
    //由于当前项目核心的版本为9版本，没有getTemplateExtension可以继承来修改文件类型，则只能继承getDownLoadTemplateUrl方法，只对现场采样做单独处理
    @Override
    public String getDownloadUrl(String templateCode, String id) {
        List<Pair<String, String>> templatePairList = this.getTemplatePairList();

        Pair<String, String> templatePair = (Pair)Objects.requireNonNull(templatePairList.stream().filter((t) -> {
            return ((String)t.getFirst()).equals(templateCode);
        }).findFirst().orElse(null));
        FilePathDTO fr = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), UUID.randomUUID().toString(), (String)templatePair.getSecond() + ".xlsx");

        try {
            Files.createDirectories(FilePathManager.getLocalDirPath(fr));
            URL resource = this.getClass().getResource("");
            if (FileUtils.isInJarURL(resource)) {
                InputStream is = this.getClass().getResourceAsStream("templates/" + templateCode + ".xlsx");
                Files.copy(is, FilePathManager.getLocalPath(fr), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            } else {
                Path templatePath = (new File(resource.getPath())).toPath().resolve("templates").resolve(templateCode + ".xlsx");
                Files.copy(templatePath, FilePathManager.getLocalPath(fr), StandardCopyOption.REPLACE_EXISTING);
            }

            this.initTemplateData(FilePathManager.getLocalPath(fr), id);
            File exportTemplateFile = FilePathManager.getLocalPath(fr).toFile();
            CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
            fileManager.upload(fr, exportTemplateFile);
        } catch (IOException var9) {
            throw new FileException(var9);
        }

        return FilePathManager.getUrl(fr);
    }

    @Override
    public void initTemplateData(Path path, String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        String[] idPairs = id.split("-");
        String sampleId = idPairs[1];
        List<Long> folderIdList = Arrays.stream(idPairs[0].split(",")).map(NumberUtils::parseLong).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(sampleId, "sampleId");
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance()
                .match("FOLDERID", folderIdList).filter(MatchPattern.OR)
                .match("ANALYSISTYPE", "sampling").filter(MatchPattern.EQ)
                .match("ORDERID", orderIdList).filter(MatchPattern.OR)
                .match("SCRIPTFLAG", "0").filter(MatchPattern.EQ));

        writeExcel(orderTaskResultList, path);
    }

    private void writeExcel(List<EmcOrderTaskResultBean> orderTaskResultList, Path path) {
        try (XSSFWorkbook wb = new XSSFWorkbook(Files.newInputStream(Paths.get(path.toString())));
             FileOutputStream fos = new FileOutputStream(path.toString())) {

            Map<String, String> uniqueItemUnitMap = orderTaskResultList.stream().collect(Collectors.toMap(r -> String.format(UNIQUE_KEYWORD, r.getRepeatNo(), r.getItemAliasName(), r.getExt$Item("testname")),
                    r -> String.format(REPEAT_NO_ITEM_KEYWORD, r.getRepeatNo(), r.getItemAliasName(), Optional.ofNullable(r.getUnitName()).orElse("")),  (key1, key2) -> key2, LinkedHashMap::new));
            List<String> uniqueItemList = new ArrayList<>(uniqueItemUnitMap.keySet());

            XSSFSheet firstSheet = wb.getSheetAt(0);
            // 隐藏表格的第一列
            firstSheet.setColumnWidth(0, 0);
            XSSFCellStyle titleStyle = buildCellStyle(wb, IndexedColors.YELLOW.getIndex());
            XSSFCellStyle valueStyle = buildCellStyle(wb, IndexedColors.LIME.getIndex());
            XSSFCellStyle whiteStyle = buildCellStyle(wb, IndexedColors.WHITE.getIndex());
            // 创建第一行、第二行标题列
            AtomicInteger columnIndex = new AtomicInteger(0);
            uniqueItemUnitMap.forEach((uniqueItemName, itemUnit) -> {
                Row firstRow = Optional.ofNullable(firstSheet.getRow(0)).orElseGet(() -> firstSheet.createRow(0));
                Cell testCell = Optional.ofNullable(firstRow.getCell(columnIndex.get() + 3)).orElseGet(() -> firstRow.createCell(columnIndex.get() + 3));
                testCell.setCellValue(uniqueItemName.substring(uniqueItemName.lastIndexOf("$") + 1));
                testCell.setCellStyle(titleStyle);

                Row secondRow = Optional.ofNullable(firstSheet.getRow(1)).orElseGet(() -> firstSheet.createRow(1));
                Cell itemCell = Optional.ofNullable(secondRow.getCell(columnIndex.get() + 3)).orElseGet(() -> secondRow.createCell(columnIndex.get() + 3));
                itemCell.setCellValue(itemUnit);
                itemCell.setCellStyle(titleStyle);
                columnIndex.incrementAndGet();
            });

            XSSFDrawing patriarch = firstSheet.createDrawingPatriarch();
            AtomicInteger markCommentRowIndex = new AtomicInteger(1);
            TreeMap<String, List<EmcOrderTaskResultBean>> orderNoResultMap = orderTaskResultList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderNo, TreeMap::new, Collectors.toList()));
            int orderNoSize = orderNoResultMap.size();
            orderNoResultMap.forEach((orderNo, resultList) -> {
                // 标注行
                Row markCommentRow = Optional.ofNullable(firstSheet.getRow(markCommentRowIndex.incrementAndGet())).orElseGet(() -> firstSheet.createRow(markCommentRowIndex.get()));
                // 真实数据行
                Row realValueRow = Optional.ofNullable(firstSheet.getRow(markCommentRowIndex.get() + orderNoSize)).orElseGet(() -> firstSheet.createRow(markCommentRowIndex.get() + orderNoSize));
                markCommentRow.setZeroHeight(true);
                resultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getId));
                List<String> resultIdList = resultList.stream().map(r -> r.getId() + "").collect(Collectors.toList());
                resultList.forEach(result -> {
                    List<String> fixedValueList = Arrays.asList(String.join(",", resultIdList), orderNo, result.getExt$Item("monitorpointname"));
                    IntStream.range(0, fixedValueList.size()).forEach(i -> {
                        Cell markCell = Optional.ofNullable(markCommentRow.getCell(i)).orElseGet(() -> markCommentRow.createCell(i));
                        Cell realCell = Optional.ofNullable(realValueRow.getCell(i)).orElseGet(() -> realValueRow.createCell(i));
                        markCell.setCellValue(fixedValueList.get(i));
                        realCell.setCellValue(fixedValueList.get(i));
                        realCell.setCellStyle(whiteStyle);
                    });

                    // 创建备注单元格
                    String uniqueItemName = String.format(UNIQUE_KEYWORD, result.getRepeatNo(), result.getItemAliasName(), result.getExt$Item("testname"));
                    IntStream.range(0, uniqueItemList.size()).filter(i -> uniqueItemList.get(i).equals(uniqueItemName)).findFirst().ifPresent(i -> {
                        Cell markCell = Optional.ofNullable(markCommentRow.getCell(3 + i)).orElseGet(() -> markCommentRow.createCell(3 + i));
                        Comment comment1 =  markCell.getCellComment();
                        XSSFComment comment = Optional.ofNullable((XSSFComment)markCell.getCellComment()).orElseGet(() -> patriarch.createCellComment(new XSSFClientAnchor(1, 1, 1, 1,  3, 3,  0, 0)));
                        comment.setString(new XSSFRichTextString(result.getId() + ""));
                        markCell.setCellComment(comment);
                        markCell.setCellValue(result.getId() + "");
                        Cell valueCell = Optional.ofNullable(realValueRow.getCell(3 + i)).orElseGet(() -> realValueRow.createCell(3 + i));
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

    private XSSFCellStyle buildCellStyle(XSSFWorkbook wb, short foregroundColor) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(foregroundColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        return style;
    }

}
