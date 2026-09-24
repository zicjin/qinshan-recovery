package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.bean.EmcMtlCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.service.EmcMtlCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午6:20:19
 */
@Repository
@GikamBean
public class EmcMtlServiceImpl implements EmcMtlService {

    @Autowired
    private EmcMtlDao mtlDao;
    
    @Autowired
    @Lazy
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    private CoreFileManager coreFileManager;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcMtlDao getDao() {
        return mtlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public String insert(RestJsonWrapperBean wrapper) {
        
        EmcMtlBean emcMtl = wrapper.parseUnique(EmcMtlBean.class);
        emcMtl.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        //赋值所属单位信息
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        emcMtl.setOrgId(parentOrgId);
        if (emcMtl.getMtlNo() == null) {
            generateMtlNo(emcMtl);
        }
        
        getDao().insert(emcMtl);
        
        //添加物质维护
        EmcMtlMaintainBean emcMtlMaintain = new EmcMtlMaintainBean();
        emcMtlMaintain.setCategory(emcMtl.getMtlName());
        emcMtlMaintain.setMtlName(emcMtl.getMtlName());
        emcMtlMaintain.setMtlId(emcMtl.getId());
        emcMtlMaintain.setId(ApplicationContextHelper.getNextIdentity());
        mtlMaintainService.getDao().insert(emcMtlMaintain);
        
        return emcMtl.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE) 
    public void updateStockQty(String id) {
        getDao().updateStockQty(id);
    }

    @Override
    public void generateMtlNo(EmcMtlBean emcMtl) {
        String reagentCategory = emcMtl.getExt$Item("REAGENTCATEGORY");
        String tagetHead = "G";
        String tagetNo = "";
        String batchTargetNo = "";
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR)).substring(2);
        if (reagentCategory != null && reagentCategory != "") {
            if (reagentCategory.equals("QC")) {
                tagetHead = "ZK-QC";
            } else if (reagentCategory.equals("RS")) {
                tagetHead = "ZK-RS";
            } else {
                tagetHead = "ZK-RW";
            }
        }
        tagetNo = tagetHead + year;
        List<EmcMtlBean> mtlQCList = selectListByFilter(SearchFilter.instance().match("REAGENTCATEGORY", reagentCategory).filter(MatchPattern.SEQ).match("mtlNo", tagetHead).filter(MatchPattern.SB));
        if (tagetNo != null) {
            int num = mtlQCList.stream().filter(e -> e.getMtlNo().length() > 4 && NumberUtils.isNumber(e.getMtlNo().substring(e.getMtlNo().length() - 4))).map(e -> Integer.parseInt(e.getMtlNo().substring(e.getMtlNo().length() - 4))).max(Comparator.naturalOrder()).orElse(0);
            num++;
            batchTargetNo = tagetNo + "-" + String.format("%04d", num);
        }
        emcMtl.setMtlNo(batchTargetNo);
    }

    @Override
    public String exportFile(RestJsonWrapperBean wrapper) {
        EmcMtlCategoryService emcMtlCategoryService = ApplicationContextHelper.getBean(EmcMtlCategoryService.class);
        Map<Long, String> mtlCategory = emcMtlCategoryService.selectAll().stream().collect(Collectors.toMap(EmcMtlCategoryBean::getId, EmcMtlCategoryBean::getMtlCategoryName));
        EmcStockService stockService = ApplicationContextHelper.getBean(EmcStockService.class);
        EmcStockLogService stockLogService = ApplicationContextHelper.getBean(EmcStockLogService.class);
        EmcStockConcentService stockConcentService = ApplicationContextHelper.getBean(EmcStockConcentService.class);
        int batchSize = 500;
        List<EmcMtlBean> list = new ArrayList<>();
        List<EmcStockBean> stockList = new ArrayList<>();
        List<EmcStockLogBean> stockLogList = new ArrayList<>();
        List<EmcStockConcentBean> stockCentList = new ArrayList<>();
        List<String> idList = wrapper.parseId(String.class);
        int mtlTotalSize = idList.size();
        if (mtlTotalSize <= batchSize) {
            list = selectListByIds(idList);
            stockList = stockService.getDao().selectListByOneColumnValues(idList, "MTLID");
        } else {
            int totalBatches = (mtlTotalSize + batchSize - 1) / batchSize;
            for (int i = 0; i < totalBatches; ++i) {
                int start = i * batchSize;
                int end = Math.min(start + batchSize, mtlTotalSize);
                List<String> batchMtlIdList = idList.subList(start, end);
                list.addAll(selectListByIds(batchMtlIdList));
                stockList.addAll(stockService.getDao().selectListByOneColumnValues(batchMtlIdList, "MTLID"));
            }
        }
        List<Long> stockIdList = stockList.stream().map(e -> e.getId()).collect(Collectors.toList());
        int totalSize = stockIdList.size();
        if (totalSize <= batchSize) {
            stockLogList = stockLogService.getDao().selectListByOneColumnValues(stockIdList, "stockId");
            stockCentList = stockConcentService.getDao().selectListByOneColumnValues(stockIdList, "stockId");
        } else {
            int totalBatches = (totalSize + batchSize - 1) / batchSize;
            for (int i = 0; i < totalBatches; ++i) {
                int start = i * batchSize;
                int end = Math.min(start + batchSize, totalSize);
                List<Long> batchStockIdList = stockIdList.subList(start, end);
                stockLogList.addAll(stockLogService.getDao().selectListByOneColumnValues(batchStockIdList, "stockId"));
                stockCentList.addAll(stockConcentService.getDao().selectListByOneColumnValues(batchStockIdList, "stockId"));
            }
        }
        Map<String, String> mtlNoMap = list.stream().collect(Collectors.toMap(EmcMtlBean::getId, EmcMtlBean::getMtlNo));
        Map<String, String> mtlNameMap = list.stream().collect(Collectors.toMap(EmcMtlBean::getId, EmcMtlBean::getMtlName));
        Map<Long, String> stockMtlIdMap = stockList.stream().collect(Collectors.toMap(EmcStockBean::getId, EmcStockBean::getMtlId));
        Map<Long, String> batchNoMap = stockList.stream().collect(Collectors.toMap(EmcStockBean::getId, EmcStockBean::getBatchNo));
        list.forEach(e -> {
            if (e.getMtlCategoryId() != null) {
                e.setExt$Item("mtlcategory", mtlCategory.get(e.getMtlCategoryId()));
            }
        });
        stockList.forEach(e -> {
            e.setExt$Item("mtlno", mtlNoMap.get(e.getMtlId()));
            e.setExt$Item("mtlname", mtlNameMap.get(e.getMtlId()));
        });
        stockLogList.forEach(e -> {
            e.setExt$Item("mtlno", mtlNoMap.get(stockMtlIdMap.get(e.getStockId())));
            e.setExt$Item("mtlname", mtlNameMap.get(stockMtlIdMap.get(e.getStockId())));
            e.setExt$Item("batchno", batchNoMap.get(e.getStockId()));
        });
        stockCentList.forEach(e -> {
            e.setExt$Item("mtlno", mtlNoMap.get(stockMtlIdMap.get(e.getStockId())));
            e.setExt$Item("mtlname", mtlNameMap.get(stockMtlIdMap.get(e.getStockId())));
        });
        Long time = System.currentTimeMillis();
        String destDir = SystemUtils.getWorkingPath().toString() + File.separator + "files" + File.separator + "temp" + File.separator + time + File.separator;
        File destPath = new File(destDir);
        if (!destPath.exists()) {
            destPath.mkdirs();
        }
        String filePath = destDir + DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyyMMddHHmmss") + ".xlsx";
        final File dataClientFile = new File(filePath);
        FileUtils.createFile(dataClientFile);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet1 = wb.createSheet("材料管理");
        String[] mtlTitles = new String[]{"批号", "实验室编号", "材料名称", "库存数量", "材料类型", "危害性", "试剂类别"};
        String[] mtlField = new String[]{"mtlno", "mtllabno", "mtlname", "stockqty", "ext$.mtlcategory", "harmfulness", "ext$.reagentcategory"};
        Row filedRow = sheet1.createRow(0);
        for (int j = 0; j < mtlTitles.length; ++j) {
            Cell cell = filedRow.createCell(j);
            cell.setCellValue(mtlTitles[j]);
        }
        for (int i = 0; i < list.size(); ++i) {
            Row dataRow = sheet1.createRow(i + 1);
            for (int k = 0; k < mtlField.length; ++k) {
                Cell cell = dataRow.createCell(k);
                String field = mtlField[k].replace("ext$.", "");
                Object s = BeanUtils.getPropertyValue(list.get(i), field);
                if (s != null) {
                    cell.setCellValue(s.toString());
                }
            }
        }
        Sheet sheet2 = wb.createSheet("库存");
        String[] stockTitles = new String[]{"批号", "材料名称", "内部唯一性编号", "试剂类别", "标准级别", "当前数量", "数量单位", "库存提醒", "失效日期", "失效提醒(天)", "存放地点", "入库时间", "库存状态", "规格", "生产商", "生产日期", "保存条件"};
        String[] stockFileds = new String[]{"mtlno", "mtlname", "batchno", "ext$.reagentcategory", "ext$.standardlevel", "stockqty", "unitname", "lowlimit", "expiredate", "reminddays", "ext$.storagelocation", "ext$.stocktime", "ext$.stockstatus", "ext$.stockspecification", "verdorname", "ext$.productiondate", "ext$.storagecondition"};
        Row stockFiledRow = sheet2.createRow(0);
        for (int j = 0; j < stockTitles.length; ++j) {
            Cell cell = stockFiledRow.createCell(j);
            cell.setCellValue(stockTitles[j]);
        }
        for (int i = 0; i < stockList.size(); ++i) {
            Row dataRow = sheet2.createRow(i + 1);
            for (int k = 0; k < stockFileds.length; ++k) {
                Cell cell = dataRow.createCell(k);
                String field = stockFileds[k].replace("ext$.", "");
                Object s = BeanUtils.getPropertyValue(stockList.get(i), field);
                if (s != null) {
                    if (StringUtils.equals(field, "expiredate")) {
                        cell.setCellValue((s + "").substring(0, 10));
                        continue;
                    }
                    if (StringUtils.equals(field, "stocktime")) {
                        cell.setCellValue((s + "").substring(0, 10));
                        continue;
                    }
                    cell.setCellValue(s.toString());
                }
            }
        }
        Sheet sheet3 = wb.createSheet("历史记录");
        String[] logTitles = new String[]{"批号", "内部唯一性编号", "材料名称", "操作类型", "操作前数量", "操作后数量", "单价", "领用人", "领取科室", "操作人", "操作科室", "操作时间", "描述", "领用时间", "定值时间", "操作数量", "还库人", "还库科室"};
        String[] logFields = new String[]{"mtlno", "batchno", "mtlname", "logtype", "previousqty", "qty", "price", "ext$.receivedbyname", "ext$.orgname", "createdbyname", "createdbyorgname", "createdtime", "logcontent", "ext$.receivetime", "ext$.setvaluedate", "ext$.operateqty", "ext$.returnbyname", "ext$.returnbyorgname"};
        Row logFiledRow = sheet3.createRow(0);
        for (int j = 0; j < logTitles.length; ++j) {
            Cell cell = logFiledRow.createCell(j);
            cell.setCellValue(logTitles[j]);
        }
        for (int i = 0; i < stockLogList.size(); ++i) {
            Row dataRow = sheet3.createRow(i + 1);
            for (int k = 0; k < logFields.length; ++k) {
                Cell cell = dataRow.createCell(k);
                String field = logFields[k].replace("ext$.", "");
                Object s = BeanUtils.getPropertyValue(stockLogList.get(i), field);
                if (s != null) {
                    if (StringUtils.equals(field, "createdtime") || StringUtils.equals(field, "receivetime")) {
                        String dateString = s.toString();
                        LocalDateTime dateTime = StringUtils.containsIgnoreCase(dateString, "t") ? LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : DateTimeUtils.parseLocalDateTime(dateString);
                        cell.setCellValue(DateTimeUtils.formatLocalDateTime(dateTime));
                        continue;
                    }
                    cell.setCellValue(s.toString());
                }
            }
        }
        Sheet sheet4 = wb.createSheet("浓度");
        String[] concentTitles = new String[]{"批号", "材料名称", "分类", "标准值", "不确定度", "不确定度%", "浓度单位", "备注", "低限", "高限", "修约规则"};
        String[] concentFields = new String[]{"mtlno", "mtlname", "category", "stdvalue", "uncertainty", "uncertaintypercent", "concentunitname", "remark", "lowlimit", "highlimit", "revisionrule"};
        Row concentFiledRow = sheet4.createRow(0);
        for (int j = 0; j < concentTitles.length; ++j) {
            Cell cell = concentFiledRow.createCell(j);
            cell.setCellValue(concentTitles[j]);
        }
        for (int i = 0; i < stockCentList.size(); ++i) {
            Row dataRow = sheet4.createRow(i + 1);
            for (int k = 0; k < concentFields.length; ++k) {
                Cell cell = dataRow.createCell(k);
                String field = concentFields[k].replace("ext$.", "");
                Object s = BeanUtils.getPropertyValue(stockCentList.get(i), field);
                if (s != null) {
                    cell.setCellValue(s.toString());
                }
            }
        }
        setCellWidthAutoFit(sheet1, mtlTitles);
        setCellWidthAutoFit(sheet2, stockTitles);
        setCellWidthAutoFit(sheet3, logTitles);
        setCellWidthAutoFit(sheet4, concentTitles);
        try {
            FileOutputStream out = new FileOutputStream(dataClientFile);
            wb.write(out);
            out.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        FilePathDTO fr = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), dataClientFile.getName());
        coreFileManager.upload(fr, dataClientFile);
        String downLoadurl = coreFileManager.getDownloadUrl(fr);
        ExecutorHelper.submit(new TimerTask(){

            @Override
            public void run() {
                dataClientFile.delete();
            }
        }, 600000L);
        return downLoadurl;
    }

    @Override
    public String exportGeneralFile(RestJsonWrapperBean wrapper) {
        EmcMtlCategoryService emcMtlCategoryService = ApplicationContextHelper.getBean(EmcMtlCategoryService.class);
        Map<Long, String> mtlCategory = emcMtlCategoryService.selectAll().stream().collect(Collectors.toMap(EmcMtlCategoryBean::getId, EmcMtlCategoryBean::getMtlCategoryName));
        EmcStockService stockService = ApplicationContextHelper.getBean(EmcStockService.class);
        EmcStockLogService stockLogService = ApplicationContextHelper.getBean(EmcStockLogService.class);
        EmcStockConcentService stockConcentService = ApplicationContextHelper.getBean(EmcStockConcentService.class);
        int batchSize = 500;
        List<EmcMtlBean> list = new ArrayList<>();
        List<EmcStockBean> stockList = new ArrayList<>();
        List<EmcStockLogBean> stockLogList = new ArrayList<>();
        List<EmcStockConcentBean> stockCentList = new ArrayList<>();
        List<String> idList = wrapper.parseId(String.class);
        int mtlTotalSize = idList.size();
        if (mtlTotalSize <= batchSize) {
            list = selectListByIds(idList);
            stockList = stockService.getDao().selectListByOneColumnValues(idList, "MTLID");
        } else {
            int totalBatches = (mtlTotalSize + batchSize - 1) / batchSize;
            for (int i = 0; i < totalBatches; ++i) {
                int start = i * batchSize;
                int end = Math.min(start + batchSize, mtlTotalSize);
                List<String> batchMtlIdList = idList.subList(start, end);
                list.addAll(selectListByIds(batchMtlIdList));
                stockList.addAll(stockService.getDao().selectListByOneColumnValues(batchMtlIdList, "MTLID"));
            }
        }
        List<Long> stockIdList = stockList.stream().map(e -> e.getId()).collect(Collectors.toList());
        int totalSize = stockIdList.size();
        if (totalSize <= batchSize) {
            stockLogList = stockLogService.getDao().selectListByOneColumnValues(stockIdList, "stockId");
            stockCentList = stockConcentService.getDao().selectListByOneColumnValues(stockIdList, "stockId");
        } else {
            int totalBatches = (totalSize + batchSize - 1) / batchSize;
            for (int i = 0; i < totalBatches; ++i) {
                int start = i * batchSize;
                int end = Math.min(start + batchSize, totalSize);
                List<Long> batchStockIdList = stockIdList.subList(start, end);
                stockLogList.addAll(stockLogService.getDao().selectListByOneColumnValues(batchStockIdList, "stockId"));
                stockCentList.addAll(stockConcentService.getDao().selectListByOneColumnValues(batchStockIdList, "stockId"));
            }
        }
        Map<String, String> mtlNoMap = list.stream().collect(Collectors.toMap(EmcMtlBean::getId, e -> e.getMtlNo() == null ? "" : e.getMtlNo()));
        Map<String, String> mtlNameMap = list.stream().collect(Collectors.toMap(EmcMtlBean::getId, e -> e.getMtlName() == null ? "" : e.getMtlName()));
        Map<Long, String> stockMtlIdMap = stockList.stream().collect(Collectors.toMap(EmcStockBean::getId, EmcStockBean::getMtlId));
        list.forEach(e -> {
            if (e.getMtlCategoryId() != null) {
                e.setExt$Item("mtlcategory", mtlCategory.get(e.getMtlCategoryId()));
            }
        });
        stockList.forEach(e -> {
            e.setExt$Item("mtlno", mtlNoMap.get(e.getMtlId()));
            e.setExt$Item("mtlname", mtlNameMap.get(e.getMtlId()));
        });
        stockLogList.forEach(e -> {
            e.setExt$Item("mtlno", mtlNoMap.get(stockMtlIdMap.get(e.getStockId())));
            e.setExt$Item("mtlname", mtlNameMap.get(stockMtlIdMap.get(e.getStockId())));
        });
        stockCentList.forEach(e -> {
            e.setExt$Item("mtlno", mtlNoMap.get(stockMtlIdMap.get(e.getStockId())));
            e.setExt$Item("mtlname", mtlNameMap.get(stockMtlIdMap.get(e.getStockId())));
        });
        Long time = System.currentTimeMillis();
        String destDir = SystemUtils.getWorkingPath().toString() + File.separator + "files" + File.separator + "temp" + File.separator + time + File.separator;
        File destPath = new File(destDir);
        if (!destPath.exists()) {
            destPath.mkdirs();
        }
        String filePath = destDir + DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyyMMddHHmmss") + ".xlsx";
        final File dataClientFile = new File(filePath);
        FileUtils.createFile(dataClientFile);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet1 = wb.createSheet("材料管理");
        String[] mtlTitles = new String[]{"批号", "材料名称", "规格型号", "库存数量", "材料类型", "Cas号"};
        String[] mtlField = new String[]{"mtlno", "mtlname", "mtlno", "mtlname", "specification", "stockqty", "mtlcategoryid", "harmfulness"};
        Row filedRow = sheet1.createRow(0);
        for (int j = 0; j < mtlTitles.length; ++j) {
            Cell cell = filedRow.createCell(j);
            cell.setCellValue(mtlTitles[j]);
        }
        for (int i = 0; i < list.size(); ++i) {
            Row dataRow = sheet1.createRow(i + 1);
            for (int k = 0; k < mtlField.length; ++k) {
                Cell cell = dataRow.createCell(k);
                String field = mtlField[k].replace("ext$.", "");
                Object s = BeanUtils.getPropertyValue(list.get(i), field);
                if (s != null) {
                    cell.setCellValue(s.toString());
                }
            }
        }
        Sheet sheet2 = wb.createSheet("库存");
        String[] stockTitles = new String[]{"批号", "材料名称", "库存批号", "存放位置", "当前数量", "数量单位", "失效日期", "失效提醒(天)", "库存提醒", "规格", "纯度", "生产日期", "生产商", "保存条件"};
        String[] stockFileds = new String[]{"mtlno", "mtlname", "batchno", "ext$.storagelocation", "stockqty", "unitname", "expiredate", "reminddays", "lowlimit", "ext$.stockspecification", "fineness", "ext$.productiondate", "vendorid", "ext$.storagecondition"};
        Row stockFiledRow = sheet2.createRow(0);
        for (int j = 0; j < stockTitles.length; ++j) {
            Cell cell = stockFiledRow.createCell(j);
            cell.setCellValue(stockTitles[j]);
        }
        for (int i = 0; i < stockList.size(); ++i) {
            Row dataRow = sheet2.createRow(i + 1);
            for (int k = 0; k < stockFileds.length; ++k) {
                Cell cell = dataRow.createCell(k);
                String field = stockFileds[k].replace("ext$.", "");
                Object s = BeanUtils.getPropertyValue(stockList.get(i), field);
                if (s != null) {
                    if (StringUtils.equals(field, "expiredate")) {
                        cell.setCellValue((s + "").substring(0, 10));
                        continue;
                    }
                    if (StringUtils.equals(field, "stocktime")) {
                        cell.setCellValue((s + "").substring(0, 10));
                        continue;
                    }
                    cell.setCellValue(s.toString());
                }
            }
        }
        Sheet sheet3 = wb.createSheet("历史记录");
        String[] logTitles = new String[]{"批号", "材料名称", "操作类型", "操作数量", "操作前数量", "操作后数量", "单价", "操作人", "操作科室", "操作时间", "描述", "领用时间", "定值时间", "领用人", "领取科室", "还库人", "还库科室"};
        String[] logFields = new String[]{"mtlno", "mtlname", "logtype", "ext$.operateqty", "previousqty", "qty", "price", "createdbyname", "createdbyorgname", "createdtime", "logcontent", "ext$.receivetime", "ext$.setvaluedate", "ext$.receivedbyname", "ext$.orgname", "ext$.returnbyname", "ext$.returnbyorgname"};
        Row logFiledRow = sheet3.createRow(0);
        for (int j = 0; j < logTitles.length; ++j) {
            Cell cell = logFiledRow.createCell(j);
            cell.setCellValue(logTitles[j]);
        }
        for (int i = 0; i < stockLogList.size(); ++i) {
            Row dataRow = sheet3.createRow(i + 1);
            for (int k = 0; k < logFields.length; ++k) {
                Cell cell = dataRow.createCell(k);
                String field = logFields[k].replace("ext$.", "");
                Object s = BeanUtils.getPropertyValue(stockLogList.get(i), field);
                if (s != null) {
                    if (StringUtils.equals(field, "createdtime") || StringUtils.equals(field, "receivetime")) {
                        String dateString = s.toString();
                        LocalDateTime dateTime = StringUtils.containsIgnoreCase(dateString, "t") ? LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : DateTimeUtils.parseLocalDateTime(dateString);
                        cell.setCellValue(DateTimeUtils.formatLocalDateTime(dateTime));
                        continue;
                    }
                    cell.setCellValue(s.toString());
                }
            }
        }
        setCellWidthAutoFit(sheet1, mtlTitles);
        setCellWidthAutoFit(sheet2, stockTitles);
        setCellWidthAutoFit(sheet3, logTitles);
        try {
            FileOutputStream out = new FileOutputStream(dataClientFile);
            wb.write(out);
            out.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        FilePathDTO fr = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), StringUtils.randomUUID(16), dataClientFile.getName());
        coreFileManager.upload(fr, dataClientFile);
        String downLoadurl = coreFileManager.getDownloadUrl(fr);
        ExecutorHelper.submit(new TimerTask(){

            @Override
            public void run() {
                dataClientFile.delete();
            }
        }, 600000L);
        return downLoadurl;
    }

    private void setCellWidthAutoFit(Sheet sheet, String[] headers) {
        for (int i = 0; i < headers.length; ++i) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, width + 2048);
        }
    }
}
