package com.sunwayworld.emc.module.mdm.bizmdm.method.validator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

@Repository
public class EmcCoreCodeEmptyValidator implements DataValidator{

    @Autowired
    private CoreCodeService coreCodeService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        String filePath = wrapper.getParamValue("path");
        List<String> category = Arrays.asList(wrapper.getParamValue("category").split(","));
        HSSFWorkbook wb = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            wb = new HSSFWorkbook(is);

            // 第一张表格
            HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
            // 总行数
            int realRowCount = sheet.getPhysicalNumberOfRows();
            List<String> codeNameList = new ArrayList<String>();
            // 拿到关于方法类型,检测类别,版本的数据
            for (int i = 1; i < realRowCount; i++) {
                Row row = sheet.getRow(i);
                if (StringUtils.isEmpty(ExcelHelper.getCellValue(row.getCell(0), String.class)) || StringUtils.isEmpty(ExcelHelper.getCellValue(row.getCell(2), String.class))) {
                    this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.METHODS.TIP.IMPORT_EMPTY");
                    return false;
                }
                codeNameList.add(ExcelHelper.getCellValue(row.getCell(3), String.class));
                codeNameList.add(ExcelHelper.getCellValue(row.getCell(10), String.class));
                codeNameList.add(ExcelHelper.getCellValue(row.getCell(12), String.class));

            }

            codeNameList = codeNameList.stream().filter(a -> !StringUtils.isEmpty(a)).distinct().collect(Collectors.toList());
            List<CoreCodeBean> coreCodeList = coreCodeService.getDao().selectListByOneColumnValues(codeNameList, "codeName").stream().filter(f -> category.contains(f.getCodeCategoryId()))
                    .collect(Collectors.toList());
            if (codeNameList.size() > coreCodeList.size()) {
                // 下拉框数据正确性校验
                this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.METHODS.TIP.METHOD_NOTEXIST");
                return false;
            }

        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (Exception ex) {
                }
            }
        }

        return true;
    }
}
