package com.sunwayworld.emc.module.bp.project.projscheme.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

@Component(value = "pfProjSchemeExportTemplate")
public class PfProjSchemeExportTemplate implements GenericExportTemplate<String> {

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Override
    public String getDownLoadTemplateUrl(String templateCode, String id) {
        String fileName = "监测方案信息模版_" + DateTimeUtils.formatLocalDate(LocalDate.now());
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
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("pfProjSchemeExportTemplate", "监测方案信息模版");
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String projectId) {
        List<EmcProjSchemeBean> schemeList = projSchemeService.getDao().selectListByOneColumnValue(projectId, "projId");

        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString())) {

            HSSFSheet firstSheet = wb.getSheetAt(0);
            AtomicInteger rowIndex = new AtomicInteger(2);
            List<String> fieldNames = Arrays.asList("folderNo", "monitorPointNo", "monitorPointName");
            schemeList.forEach(scheme -> {
                HSSFRow dataRow = Optional.ofNullable(firstSheet.getRow(rowIndex.get())).orElseGet(() -> firstSheet.createRow(rowIndex.get()));
                HSSFCell numCell = Optional.ofNullable(dataRow.getCell(0)).orElseGet(() -> dataRow.createCell(0));
                numCell.setCellValue(rowIndex.get() - 1);
                IntStream.range(0, fieldNames.size()).forEach(cellIndex -> {
                    HSSFCell dataCell = Optional.ofNullable(dataRow.getCell(cellIndex + 1)).orElseGet(() -> dataRow.createCell(cellIndex + 1));
                    Object fieldValue = BeanUtils.getPropertyValue(scheme, fieldNames.get(cellIndex));
                    dataCell.setCellValue(ObjectUtils.isEmpty(fieldValue) ? "" : fieldValue.toString());
                });

                rowIndex.incrementAndGet();
            });

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
