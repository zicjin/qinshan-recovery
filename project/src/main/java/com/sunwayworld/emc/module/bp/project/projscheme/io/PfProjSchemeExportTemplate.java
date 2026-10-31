package com.sunwayworld.emc.module.bp.project.projscheme.io;

import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="pfProjSchemeExportTemplate")
public class PfProjSchemeExportTemplate
implements GenericTemplateService<String> {
    @Autowired
    private EmcProjSchemeService projSchemeService;

    public String getDownloadUrl(String templateCode, String id) {
        String fileName = "\u76d1\u6d4b\u65b9\u6848\u4fe1\u606f\u6a21\u7248_" + DateTimeUtils.formatLocalDate((LocalDate)LocalDate.now());
        FilePathDTO fr = FilePathDTO.of((String)FileScope.temp.name(), (String)("" + System.currentTimeMillis()), (String)UUID.randomUUID().toString(), (String)(fileName + ".xls"));
        try {
            Files.createDirectories(FilePathManager.getLocalDirPath((FilePathDTO)fr), new FileAttribute[0]);
            URL resource = this.getClass().getResource("");
            if (FileUtils.isInJarURL((URL)resource)) {
                InputStream is = this.getClass().getResourceAsStream("templates/" + templateCode + ".xls");
                Files.copy(is, FilePathManager.getLocalPath((FilePathDTO)fr), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path templatePath = new File(resource.getPath()).toPath().resolve("templates").resolve(templateCode + ".xls");
                Files.copy(templatePath, FilePathManager.getLocalPath((FilePathDTO)fr), StandardCopyOption.REPLACE_EXISTING);
            }
            this.initTemplateData(FilePathManager.getLocalPath((FilePathDTO)fr), id);
            CoreFileManager fileManager = (CoreFileManager)ApplicationContextHelper.getBean(CoreFileManager.class);
            fileManager.upload(fr, FilePathManager.getLocalPath((FilePathDTO)fr));
        }
        catch (IOException ioe) {
            throw new FileException((Throwable)ioe);
        }
        return FilePathManager.getUrl((FilePathDTO)fr);
    }

    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<Pair<String, String>>();
        Pair templatePair = Pair.of((Object)"pfProjSchemeExportTemplate", (Object)"\u76d1\u6d4b\u65b9\u6848\u4fe1\u606f\u6a21\u7248");
        templatePairList.add(templatePair);
        return templatePairList;
    }

    public void initTemplateData(Path path, String projectId) {
        List<EmcProjSchemeBean> schemeList = this.projSchemeService.getDao().selectListByOneColumnValue((Object)projectId, "projId", new Order[0]);
        try (HSSFWorkbook wb = new HSSFWorkbook((InputStream)new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString());){
            HSSFSheet firstSheet = wb.getSheetAt(0);
            AtomicInteger rowIndex = new AtomicInteger(2);
            List<String> fieldNames = Arrays.asList("folderNo", "monitorPointNo", "monitorPointName");
            schemeList.forEach(scheme -> {
                HSSFRow dataRow = Optional.ofNullable(firstSheet.getRow(rowIndex.get())).orElseGet(() -> firstSheet.createRow(rowIndex.get()));
                HSSFCell numCell = Optional.ofNullable(dataRow.getCell(0)).orElseGet(() -> dataRow.createCell(0));
                numCell.setCellValue((double)(rowIndex.get() - 1));
                IntStream.range(0, fieldNames.size()).forEach(cellIndex -> {
                    HSSFCell dataCell = Optional.ofNullable(dataRow.getCell(cellIndex + 1)).orElseGet(() -> dataRow.createCell(cellIndex + 1));
                    Object fieldValue = BeanUtils.getPropertyValue((Object)scheme, (String)((String)fieldNames.get(cellIndex)));
                    dataCell.setCellValue(ObjectUtils.isEmpty((Object)fieldValue) ? "" : fieldValue.toString());
                });
                rowIndex.incrementAndGet();
            });
            wb.write((OutputStream)fos);
        }
        catch (IOException e) {
            throw new FileException((Throwable)e);
        }
    }
}
