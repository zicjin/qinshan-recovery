package com.sunwayworld.emc.module.mdm.bizmdm.method.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.EmcTestCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

@GikamBean
@Component(value = "emcMethodImportFileFunction")
public class EmcMethodImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcMethodService methodService;
    
    @Autowired
    private EmcTestCategoryService testCategoryService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();
        FileRepresentation fr = FileRepresentation.of(file);
        importMethodByPOI(FileHelper.getAbsolutePath(fr).toFile().getAbsolutePath());
        return importResultRepresentation;
    }

    public void importMethodByPOI(String filePath) {
        HSSFWorkbook wb = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            wb = new HSSFWorkbook(is);

            // 第一张表格
            HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
            // 总行数
            int realRowCount = sheet.getPhysicalNumberOfRows();

            List<EmcMethodBean> methodList = new ArrayList<>();

            // 校验数据
            RestJsonWrapperBean wrapper = new RestJsonWrapperBean();
            wrapper.setParamValue("path", filePath);
            wrapper.setParamValue("category", EmcConstant.METHOD_CATEGORIES);
            List<CoreCodeBean> coreCodeList = methodService.selectCoreCodeByCodeNames(wrapper);

            List<EmcMethodBean> existList = methodService.selectAll();
            List<EmcTestCategoryBean> testCategoryList =  testCategoryService.selectAll();
            for (int i = 1; i < realRowCount; i++) {
                EmcMethodBean newbean = new EmcMethodBean();
                Row row = sheet.getRow(i);
                String methodName = ExcelHelper.getCellValue(row.getCell(0), String.class);
                String methodNo = ExcelHelper.getCellValue(row.getCell(1), String.class);
                String source = ExcelHelper.getCellValue(row.getCell(5), String.class);

                // 已有的数据跳过导入，使用并发流，加快速度
                boolean flag = existList.parallelStream()
                        .anyMatch(a -> ObjectUtils.equals(methodNo, a.getMethodNo()) && ObjectUtils.equals(methodName, a.getMethodName()) && ObjectUtils.equals(source, a.getSource()));
                if (flag) {
                    continue;
                }

                // 设置字段
                newbean.setId(ApplicationContextHelper.getNextIdentity());
                newbean.setMethodName(methodName);
                newbean.setMethodNo(ExcelHelper.getCellValue(row.getCell(1), String.class));
                testCategoryList.stream().filter(a -> a.getTestCategoryDesc().equals(ExcelHelper.getCellValue(row.getCell(2), String.class))).forEach(e -> {
                    newbean.setMonitorCategory(e.getId() + "");
                });
                coreCodeList.stream().filter(a -> a.getCodeName().equals(ExcelHelper.getCellValue(row.getCell(3), String.class)) && a.getCodeCategoryId().equals("methodCategory")).forEach(e -> {
                    newbean.setMethodCategory(e.getCode());
                });
                newbean.setYear(ExcelHelper.getCellValue(row.getCell(4), String.class));
                newbean.setSource(source);
                newbean.setEffectiveDate(DateTimeUtils.parseLocalDate(ExcelHelper.getCellValue(row.getCell(6), String.class)));
                newbean.setExpirationDate(DateTimeUtils.parseLocalDate(ExcelHelper.getCellValue(row.getCell(7), String.class)));
                newbean.setPublishOrgName(ExcelHelper.getCellValue(row.getCell(8), String.class));
                newbean.setPublishDate(DateTimeUtils.parseLocalDate(ExcelHelper.getCellValue(row.getCell(9), String.class)));
                coreCodeList.stream().filter(a -> a.getCodeName().equals(ExcelHelper.getCellValue(row.getCell(10), String.class)) && a.getCodeCategoryId().equals("methodVersion")).forEach(e -> {
                    newbean.setVersion(e.getCode());
                });
                newbean.setRemark(ExcelHelper.getCellValue(row.getCell(11), String.class));
                coreCodeList.stream().filter(a -> a.getCodeName().equals(ExcelHelper.getCellValue(row.getCell(12), String.class)) && a.getCodeCategoryId().equals("runStatus")).forEach(e -> {
                    newbean.setRunStatus(e.getCode());
                });
                newbean.setReplacedMethodId(ExcelHelper.getCellValue(row.getCell(13), long.class));
                newbean.setReplaceMethodId(ExcelHelper.getCellValue(row.getCell(14), long.class));
                newbean.setReplacedContent(ExcelHelper.getCellValue(row.getCell(15), String.class));
                newbean.setReplaceContent(ExcelHelper.getCellValue(row.getCell(16), String.class));
                methodList.add(newbean);
            }

            // 按方法编号进行数据去重
            methodList = methodList.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMethodName() + "#" + o.getMethodNo() + "#" + o.getSource() + "#" +o.getMonitorCategory()))),
                            ArrayList::new));
            if (methodList.size() > 0) {
                methodService.getDao().insert(methodList);
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
    }

}
