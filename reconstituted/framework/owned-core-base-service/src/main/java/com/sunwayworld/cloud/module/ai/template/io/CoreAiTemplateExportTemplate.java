/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.io;

import com.sunwayworld.cloud.module.ai.surport.ImageUtils;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateBean;
import com.sunwayworld.cloud.module.ai.template.service.CoreAiTemplateService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component(value="coreAiTemplateExportTemplate")
public class CoreAiTemplateExportTemplate
implements GenericTemplateService<String> {
    @Autowired
    @Lazy
    private CoreAiTemplateService coreAiTemplateService;
    private static final String PUBLISH = "\u5df2\u53d1\u5e03";
    private static final String NO_PUBLISH = "\u672a\u53d1\u5e03";

    @Override
    public String getTemplateExtension() {
        return "xlsx";
    }

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        ArrayList<Pair<String, String>> templatePairList = new ArrayList<Pair<String, String>>();
        Pair<String, String> templatePair = Pair.of("coreAiTemplateExportTemplate", "ocr\u6a21\u677f");
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String id) {
        String[] ids = id.split(",");
        ArrayList<Long> idList = new ArrayList<Long>();
        for (String templateId : ids) {
            idList.add(NumberUtils.parseLong(templateId));
        }
        List<CoreAiTemplateBean> coreAiTemplateList = this.coreAiTemplateService.selectListByIds(idList);
        this.writeExcel(coreAiTemplateList, path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeExcel(List<CoreAiTemplateBean> coreAiTemplateList, Path path) {
        XSSFWorkbook wb = null;
        FileOutputStream fos = null;
        try {
            wb = new XSSFWorkbook(new FileInputStream(path.toString()));
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFCellStyle style = wb.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            for (int i = 0; i < coreAiTemplateList.size(); ++i) {
                String base64;
                Cell publishedFlagCell;
                CoreAiTemplateBean coreAiTemplate = coreAiTemplateList.get(i);
                Row row = sheet.getRow(i + 1) == null ? sheet.createRow(i + 1) : sheet.getRow(i + 1);
                Cell nameCell = row.getCell(0) == null ? row.createCell(0) : row.getCell(0);
                nameCell.setCellValue(coreAiTemplate.getName() == null ? "" : coreAiTemplate.getName());
                nameCell.setCellStyle(style);
                Cell publishTimeCell = row.getCell(1) == null ? row.createCell(1) : row.getCell(1);
                publishTimeCell.setCellValue(ObjectUtils.isEmpty(coreAiTemplate.getActivatedTime()) ? "" : DateTimeUtils.formatLocalDateTime(coreAiTemplate.getActivatedTime()));
                publishTimeCell.setCellStyle(style);
                Cell modifyTimeCell = row.getCell(2) == null ? row.createCell(2) : row.getCell(2);
                modifyTimeCell.setCellValue(ObjectUtils.isEmpty(coreAiTemplate.getModifyTime()) ? "" : DateTimeUtils.formatLocalDateTime(coreAiTemplate.getModifyTime()));
                modifyTimeCell.setCellStyle(style);
                Cell cell = publishedFlagCell = row.getCell(3) == null ? row.createCell(3) : row.getCell(3);
                if (StringUtils.isBlank(coreAiTemplate.getActivatedFlag())) {
                    publishedFlagCell.setCellValue(NO_PUBLISH);
                } else if (coreAiTemplate.getActivatedFlag().equals("1")) {
                    publishedFlagCell.setCellValue(PUBLISH);
                } else {
                    publishedFlagCell.setCellValue(NO_PUBLISH);
                }
                publishedFlagCell.setCellStyle(style);
                Cell infoCell = row.getCell(4) == null ? row.createCell(4) : row.getCell(4);
                infoCell.setCellValue(coreAiTemplate.getInfo() == null ? "" : coreAiTemplate.getInfo());
                infoCell.setCellStyle(style);
                Cell picNameCell = row.getCell(5) == null ? row.createCell(5) : row.getCell(5);
                picNameCell.setCellValue(coreAiTemplate.getPicName() == null ? "" : coreAiTemplate.getPicName());
                picNameCell.setCellStyle(style);
                Cell widthCell = row.getCell(6) == null ? row.createCell(6) : row.getCell(6);
                widthCell.setCellValue(String.valueOf(coreAiTemplate.getWidth()) == null ? "" : String.valueOf(coreAiTemplate.getWidth()));
                widthCell.setCellStyle(style);
                Cell heightCell = row.getCell(7) == null ? row.createCell(7) : row.getCell(7);
                heightCell.setCellValue(String.valueOf(coreAiTemplate.getHeight()) == null ? "" : String.valueOf(coreAiTemplate.getHeight()));
                heightCell.setCellStyle(style);
                File picFile = null;
                try {
                    picFile = this.coreAiTemplateService.downloadPicFile(coreAiTemplate);
                    base64 = ImageUtils.fileToBase64(picFile);
                }
                catch (Throwable throwable) {
                    FileUtils.deleteQuietly(picFile);
                    throw throwable;
                }
                FileUtils.deleteQuietly(picFile);
                int m = 0;
                while (base64.length() > 0) {
                    Cell picCell = row.getCell(8 + m) == null ? row.createCell(8 + m) : row.getCell(8 + m);
                    picCell.setCellValue(base64.substring(0, base64.length() <= Short.MAX_VALUE ? base64.length() : Short.MAX_VALUE));
                    picCell.setCellStyle(style);
                    base64 = base64.length() <= Short.MAX_VALUE ? "" : base64.substring(Short.MAX_VALUE);
                    ++m;
                }
            }
            fos = new FileOutputStream(path.toString());
            wb.write(fos);
        }
        catch (IOException e) {
            throw new FileException(e);
        }
        finally {
            if (wb != null) {
                try {
                    wb.close();
                }
                catch (Exception exception) {}
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException iOException) {}
            }
        }
    }
}
