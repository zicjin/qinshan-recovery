package com.sunwayworld.emc.module.bp.bp.io;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年10月22日
 */
@Component(value = "pfOrderExportTemplate")
public class PfOrderExportTemplate implements GenericTemplateService<String> {

    @Autowired
    private EmcOrderService orderService;
    
    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("pfOrderExportTemplate", "样品信息导入模版");
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public String getDownloadUrl(String templateCode, String id) {
        // 首先根据id查询项目，然后得到文件名称
        String fileName = "项目编号+受检单位";
        EmcProjectBean projectBean = projectService.selectById(Long.parseLong(id));
        String projNo = projectBean.getProjNo();
        String entrustOrgName  = projectBean.getEntrustOrgName();
        if(ObjectUtils.isEmpty(entrustOrgName) || entrustOrgName.equals("/")){
            fileName = projNo;
        }else{
            fileName = projNo +"-" + entrustOrgName;
        }

        FilePathDTO fr = CoreFileUtils.toFilePath(FileScope.temp, fileName + ".xls");
        try {
            Files.createDirectories(FilePathManager.getLocalPath(fr));

            URL resource = this.getClass().getResource("");
            if (FileUtils.isInJarURL(resource)) {
                InputStream is = this.getClass().getResourceAsStream("templates/" + templateCode + ".xls");
                Files.copy(is, FilePathManager.getLocalPath(fr), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Path templatePath = new File(resource.getPath()).toPath().resolve("templates").resolve(templateCode + ".xls");
                Files.copy(templatePath, FilePathManager.getLocalPath(fr), StandardCopyOption.REPLACE_EXISTING);
            }

            initTemplateData(FilePathManager.getLocalPath(fr), id);

            File exportTemplateFile = FilePathManager.getLocalPath(fr).toFile();

            CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);

            coreFileManager.upload(fr, exportTemplateFile);

        } catch (IOException ioe) {
            throw new FileException(ioe);
        }

        return FilePathManager.getUrl(fr);
    }
    
    @Override
    public void initTemplateData(Path path, String projectId) {
        //List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(projectId, "projId");
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("projId", projectId).filter(MatchPattern.OR));

        orderList.sort(Comparator.comparing(EmcOrderBean::getOrderNo));

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "orderId", "schemeName", "testName"));
        Map<Long, List<EmcOrderItemGroupBean>> orderItemGroupMap = orderItemGroupList.stream().collect(Collectors.groupingBy(EmcOrderItemGroupBean::getOrderId));
        Map<Long, String> orderTestNameMap = new HashMap<>(orderItemGroupMap.size());
        orderItemGroupMap.forEach((orderId, groupList) -> {
            groupList.sort(Comparator.comparing(EmcOrderItemGroupBean::getSchemeName));
            Map<String, List<EmcOrderItemGroupBean>> schemeGroupMap = groupList.stream().collect(Collectors.groupingBy(EmcOrderItemGroupBean::getSchemeName, LinkedHashMap::new, Collectors.toList()));
            List<String> testNameList = new ArrayList<>(schemeGroupMap.size());
            schemeGroupMap.forEach((scheme, curGroupList) -> {
                curGroupList.sort(Comparator.comparing(EmcOrderItemGroupBean::getId).reversed());
                String testName = curGroupList.stream().filter(group -> !StringUtils.isEmpty(group.getTestName())).map(EmcOrderItemGroupBean::getTestName).collect(Collectors.joining("、"));
                testNameList.add(String.join("", scheme, "(", testName, ")"));
            });
            orderTestNameMap.put(orderId, String.join("、", testNameList));
        });

        List<String> cellFieldList = Arrays.asList("orderNo", "labelNo", "clientOrderName", "sampleAddress", "orderCategory", "orderDesc");
        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString())) {

            HSSFSheet firstSheet = wb.getSheetAt(0);
            IntStream.range(0, orderList.size()).forEach(rowIndex -> {
                EmcOrderBean order = orderList.get(rowIndex);
                List<BeanPropertyDescriptor> descriptors = BeanPropertyHelper.getBeanPropertyDescriptorList(EmcOrderBean.class);
                HSSFRow row = Optional.ofNullable(firstSheet.getRow(rowIndex + 1)).orElseGet(() -> firstSheet.createRow(rowIndex + 1));

                AtomicInteger cellIndex = new AtomicInteger(1);
                cellFieldList.forEach(fieldName -> {
                    int cellIndexValue = cellIndex.getAndIncrement();
                    HSSFCell cell = Optional.ofNullable(row.getCell(cellIndexValue)).orElseGet(() -> row.createCell(cellIndexValue));
                    descriptors.stream().filter(d -> StringUtils.equals(d.getName(), fieldName)).findFirst().ifPresent(d -> {
                        cell.setCellValue(Optional.ofNullable(d.getPropertyValue(order)).orElse("").toString());
                    });
                });

                int volumeCellIndex = cellIndex.getAndIncrement();
                HSSFCell volumeRow = Optional.ofNullable(row.getCell(volumeCellIndex)).orElseGet(() -> row.createCell(volumeCellIndex));
                volumeRow.setCellValue(order.getExt$Item("volume"));

                int remarkCellIndex = cellIndex.getAndIncrement();
                HSSFCell remarkRow = Optional.ofNullable(row.getCell(remarkCellIndex)).orElseGet(() -> row.createCell(remarkCellIndex));
                remarkRow.setCellValue(order.getRemark());

                
                int qcNameCellIndex = cellIndex.getAndIncrement();
                HSSFCell qcNameRow = Optional.ofNullable(row.getCell(qcNameCellIndex)).orElseGet(() -> row.createCell(qcNameCellIndex));
                qcNameRow.setCellValue(order.getQcName());

                int senderCellIndex = cellIndex.getAndIncrement();
                HSSFCell senderRow = Optional.ofNullable(row.getCell(senderCellIndex)).orElseGet(() -> row.createCell(senderCellIndex));
                senderRow.setCellValue(order.getExt$Item("sender"));
                
                int checkerCellIndex = cellIndex.getAndIncrement();
                HSSFCell checkerRow = Optional.ofNullable(row.getCell(checkerCellIndex)).orElseGet(() -> row.createCell(checkerCellIndex));
                checkerRow.setCellValue(order.getExt$Item("checker"));
                
        		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        		LocalDate sendDate = order.getSendDate();
        		int sendDateCellIndex = cellIndex.getAndIncrement();
                HSSFCell sendDateRow = Optional.ofNullable(row.getCell(sendDateCellIndex)).orElseGet(() -> row.createCell(sendDateCellIndex));
        		if(sendDate!=null){ 
                    sendDateRow.setCellValue(sendDate.format(fmt));	
        		}else{
                    sendDateRow.setCellValue("");	
        		}

                int packingCellIndex = cellIndex.getAndIncrement();
                HSSFCell packingRow = Optional.ofNullable(row.getCell(packingCellIndex)).orElseGet(() -> row.createCell(packingCellIndex));
                packingRow.setCellValue(order.getExt$Item("packing"));

                int isBlankCellIndex = cellIndex.getAndIncrement();
                HSSFCell isBlankRow = Optional.ofNullable(row.getCell(isBlankCellIndex)).orElseGet(() -> row.createCell(isBlankCellIndex));
                isBlankRow.setCellValue(order.getExt$Item("isblank"));

                HSSFCell testRow = Optional.ofNullable(row.getCell(cellIndex.get())).orElseGet(() -> row.createCell(cellIndex.get()));
                testRow.setCellValue(Optional.ofNullable(orderTestNameMap.get(order.getId())).orElse(""));
            });

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
