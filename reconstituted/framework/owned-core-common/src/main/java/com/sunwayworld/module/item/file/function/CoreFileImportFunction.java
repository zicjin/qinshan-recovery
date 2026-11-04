/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.function;

import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.BiPredicate;
import java.util.function.Function;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public interface CoreFileImportFunction
extends Function<CoreFileBean, CoreFileImportResultDTO>,
BiPredicate<CoreFileBean, String> {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    default public boolean test(CoreFileBean coreFile, String service) {
        Path path = CoreFileUtils.getLocalPath(coreFile);
        try (InputStream is = Files.newInputStream(path, new OpenOption[0]);
             Workbook wb = WorkbookFactory.create(is);){
            HSSFSheet sheet = (HSSFSheet)wb.getSheetAt(0);
            Row row = sheet.getRow(0);
            Cell cell = row.getCell(0);
            Comment comment = cell.getCellComment();
            String tempName = comment.getString().getString();
            if (service.substring(0, service.lastIndexOf("ImportFileFunction")).indexOf(tempName) < 0) {
                if (service.substring(0, service.lastIndexOf("FileImportFunction")).indexOf(tempName) < 0) return false;
            }
            boolean bl = true;
            return bl;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
