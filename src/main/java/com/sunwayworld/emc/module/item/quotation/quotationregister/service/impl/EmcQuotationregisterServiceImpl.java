package com.sunwayworld.emc.module.item.quotation.quotationregister.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.quotation.quotationinformation.bean.EmcQuotationinformationBean;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.service.EmcQuotationinformationService;
import com.sunwayworld.emc.module.item.quotation.quotationregister.bean.EmcQuotationregisterBean;
import com.sunwayworld.emc.module.item.quotation.quotationregister.dao.EmcQuotationregisterDao;
import com.sunwayworld.emc.module.item.quotation.quotationregister.service.EmcQuotationregisterService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcMonitorItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceParamDTO;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;

@Repository
@GikamBean
public class EmcQuotationregisterServiceImpl implements EmcQuotationregisterService {

    @Autowired
    private EmcQuotationregisterDao emcQuotationregisterDao;

    @Autowired
    private EmcQuotationinformationService quotationinformationService;

    @Autowired
     private CoreSequenceService sequenceService;

    @Autowired
    private CoreReportConfigLineService reportConfigLineService;

    @Autowired
    private EmcContractService contractService;

    @Autowired
    private EmcMonitorItemService monitorItemService;

    private static final String[] CONTRACT_CATEGORY_CODES = { "entrust", "sample", "consult" };
    private static final String[] CONTRACT_NO_KEYS = { "WT", "SY", "ZX" };
    //private static final String CONTRACT_NO_PATTERN = "%s%s%s%s";
    //private static final String[] CONTRACT_REVIEW_CATEGORIES = { "reviewContent", "reviewConclusion", "reviewRemark" };

    @Override
    @SuppressWarnings("unchecked")
    public EmcQuotationregisterDao getDao() {
        return emcQuotationregisterDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        //报价单
        Long quotationRegisterId = ApplicationContextHelper.getNextIdentity();
        EmcQuotationregisterBean emcQuotationregister = jsonWrapper.parseUnique(EmcQuotationregisterBean.class);
        emcQuotationregister.setId(quotationRegisterId);
        emcQuotationregister.setQuotationNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        emcQuotationregister.setCorePersonCode(LocalContextHelper.getLoginUserId());
        emcQuotationregister.setCorePersonName(LocalContextHelper.getLoginUserName());

        //检测费用
        List<EmcQuotationinformationBean> informationList = new ArrayList<>();
//        EmcQuotationinformationBean organizationBean = new EmcQuotationinformationBean();
//        organizationBean.setId(ApplicationContextHelper.getNextIdentity());
//        organizationBean.setQuotationRegisterId(quotationRegisterId);
//        organizationBean.setTestName("检测报告编制费(15%)");
//        organizationBean.setType("test");
        EmcQuotationinformationBean testTotalBean = new EmcQuotationinformationBean();
        testTotalBean.setId(ApplicationContextHelper.getNextIdentity());
        testTotalBean.setQuotationRegisterId(quotationRegisterId);
        testTotalBean.setTestName("检测费用合计");
        testTotalBean.setType("test");
        informationList.add(testTotalBean);
//        informationList.add(organizationBean);

        //总费用
        EmcQuotationinformationBean totalBean = new EmcQuotationinformationBean();
        totalBean.setId(ApplicationContextHelper.getNextIdentity());
        totalBean.setQuotationRegisterId(quotationRegisterId);
        totalBean.setTotalDiscount("100%");
        totalBean.setType("total");
        informationList.add(totalBean);

        quotationinformationService.getDao().insert(informationList);
        getDao().insert(emcQuotationregister);
        return emcQuotationregister.getId();
    }

    /*@Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    @Audit(AuditConstant.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcQuotationregisterBean> emcQuotationregisterList = wrapper.parse(this.getDao().getType());
        getDao().deleteBy(emcQuotationregisterList);

        String sequenceId = getDao().getTable();
        CoreSequenceBean sequence = sequenceService.selectById(sequenceId);
        String jsonParam = sequence.getJsonParam();
        JSONArray array = JSONObject.parseArray(jsonParam);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String key = obj.getString("n");
            if (key != null) {
                Integer n = Integer.parseInt(key) - 1;// 重置sequence当前业务类型编号的最大值。
                obj.put("n", StringUtils.leftPad(String.valueOf(n), 5));
            }
        }

        sequence.setJsonParam(JSON.toJSONString(array));
        sequenceService.getDao().update(sequence, "JSONPARAM");

    }*/

    @Override
    public void addDiscount(RestJsonWrapperBean wrapper) {
        String testDiscount = wrapper.getFilterValue("testDiscount");
        List<EmcQuotationinformationBean> informationList = wrapper.parse(EmcQuotationinformationBean.class);
        informationList.stream().filter(e -> !e.getTestName().contains("报告编制费") && !e.getTestName().contains("检测费用合计")).forEach(e -> {
            e.setTestDiscount(testDiscount);
            e.setTestSampleDiscount(testDiscount);
            e.setTestAnalysisDiscount(testDiscount);
        });
        quotationinformationService.getDao().updateIfChanged(informationList);
    }

    @Override
    public void calculation(RestJsonWrapperBean wrapper) {
        List<EmcQuotationregisterBean> registerList = wrapper.parse(EmcQuotationregisterBean.class);
        List<EmcQuotationinformationBean> updateInformation = new ArrayList<>();
        registerList.forEach(e -> {
            List<EmcQuotationinformationBean> informationList = quotationinformationService.getDao().selectListByOneColumnValue(e.getId(),"QUOTATIONREGISTERID");

            //所有检测费用
            List<EmcQuotationinformationBean> testAllInformationList = informationList.stream().filter(m -> "test".equals(m.getType())).collect(Collectors.toList());
            //检测报告编制费
            EmcQuotationinformationBean testRepInformation = testAllInformationList.stream().filter(m -> m.getTestName().contains("检测报告编制费")).findFirst().orElse(null);
            //检测费用合计
            EmcQuotationinformationBean testOrganizationInformation = testAllInformationList.stream().filter(m -> "检测费用合计".equals(m.getTestName())).findFirst().orElse(null);
            //检测费用
            List<EmcQuotationinformationBean> testInformationList = testAllInformationList.stream().filter(m -> !"检测费用合计".equals(m.getTestName()) && !m.getTestName().contains("检测报告编制费")).collect(Collectors.toList());

            //附加费用
            List<EmcQuotationinformationBean> addInformationList = informationList.stream().filter(m -> "add".equals(m.getType())).collect(Collectors.toList());

            //总费用
            EmcQuotationinformationBean totalInformation = informationList.stream().filter(m -> "total".equals(m.getType())).findFirst().orElse(null);

            //计算检测费用合计
            Double testRep = 1.0;//编制费
            Double totalDiscount = Double.parseDouble(totalInformation.getTotalDiscount().replaceAll("%",""))/100;//总价折扣
            Double testTotalSamplePrice = 0.0;//总标准采样单价
            Double testTotalSamplePriceAfterCount = 0.0;//总调整后采样单价
            Double testTotalAnalysisPrice = 0.0;//总标准分析单价
            Double testTotalAnalysisPriceAfterCount = 0.0;//总调整分析单价
            if(!ObjectUtils.isEmpty(testRepInformation) && !ObjectUtils.isEmpty(testRepInformation.getTestName())){
                String testName = testRepInformation.getTestName();
                testRep = Double.parseDouble(testName.substring(testName.indexOf("(")+1,testName.indexOf(")")).replaceAll("%",""))/100;
            }
            for(EmcQuotationinformationBean k:testInformationList){
                Double testSamplePrice = ObjectUtils.isEmpty(k.getTestSamplePrice())?0.0:Double.parseDouble(k.getTestSamplePrice());//标准采样单价
                Double testSampleDiscount = ObjectUtils.isEmpty(k.getTestSampleDiscount())?0.0:Double.parseDouble(k.getTestSampleDiscount().replaceAll("%",""))/100;//采样折扣
                Double testSamplePriceAfterCount = testSamplePrice * testSampleDiscount;//调整后采样单价

                Double testAnalysisPrice = ObjectUtils.isEmpty(k.getTestAnalysisPrice())?0.0:Double.parseDouble(k.getTestAnalysisPrice());//标准分析单价
                Double testAnalysisDiscount = ObjectUtils.isEmpty(k.getTestAnalysisDiscount())?0.0:Double.parseDouble(k.getTestAnalysisDiscount().replaceAll("%",""))/100;//分析折扣
                Double testAnalysisPriceAfterCount = testAnalysisPrice * testAnalysisDiscount;//调整后分析单价

                String monitorNum = k.getTestMonitorNum();//点位数
                String testDayNum = k.getTestDayNum();//天数
                String testFrequency = k.getTestFrequency();//频次

                if(!ObjectUtils.isEmpty(monitorNum) && !ObjectUtils.isEmpty(testDayNum) && !ObjectUtils.isEmpty(testFrequency)){
                    testTotalSamplePrice += testSamplePrice * Double.parseDouble(monitorNum) * Double.parseDouble(testDayNum) * Double.parseDouble(testFrequency);
                    Double testSamplePriceAfterCountTemp = 0.0;
                    testSamplePriceAfterCountTemp = testSamplePriceAfterCount * Double.parseDouble(monitorNum) * Double.parseDouble(testDayNum) * Double.parseDouble(testFrequency);
                    testTotalSamplePriceAfterCount += testSamplePriceAfterCountTemp;
                    testTotalAnalysisPrice += testAnalysisPrice * Double.parseDouble(monitorNum) * Double.parseDouble(testDayNum) * Double.parseDouble(testFrequency);
                    Double testAnalysisPriceAfterCountTemp = 0.0;
                    testAnalysisPriceAfterCountTemp = testAnalysisPriceAfterCount * Double.parseDouble(monitorNum) * Double.parseDouble(testDayNum) * Double.parseDouble(testFrequency);
                    testTotalAnalysisPriceAfterCount += testAnalysisPriceAfterCountTemp;

                    k.setTotalTestPriceAfterCount(ObjectUtils.toString(testSamplePriceAfterCountTemp + testAnalysisPriceAfterCountTemp));
                }
                k.setTestSamplePriceAfterCount(testSamplePriceAfterCount.toString());
                k.setTestAnalysisPriceAfterCount(testAnalysisPriceAfterCount.toString());
                updateInformation.add(k);
            };
            if(!ObjectUtils.isEmpty(testRepInformation) && !ObjectUtils.isEmpty(testRep) && !ObjectUtils.isEmpty(testTotalAnalysisPriceAfterCount) && !ObjectUtils.isEmpty(testTotalSamplePriceAfterCount) && !ObjectUtils.isEmpty(testTotalAnalysisPrice) && !ObjectUtils.isEmpty(testTotalSamplePrice)){
//                testRepInformation.setTestAnalysisPrice(ObjectUtils.toString(testTotalAnalysisPrice * testRep));
//                testRepInformation.setTestAnalysisPriceAfterCount(ObjectUtils.toString(testTotalAnalysisPriceAfterCount * testRep));
//                testRepInformation.setTestSamplePrice(ObjectUtils.toString(testTotalSamplePrice * testRep));
//                testRepInformation.setTestSamplePriceAfterCount(ObjectUtils.toString(testTotalSamplePriceAfterCount * testRep));
                testRepInformation.setTotalTestPriceAfterCount(ObjectUtils.toString(testTotalAnalysisPriceAfterCount * testRep + testTotalSamplePriceAfterCount * testRep));

                testTotalAnalysisPrice = testTotalAnalysisPrice * (1+testRep);
                testTotalSamplePrice = testTotalSamplePrice * (1+testRep);
                testTotalAnalysisPriceAfterCount = testTotalAnalysisPriceAfterCount * (1+testRep);
                testTotalSamplePriceAfterCount = testTotalSamplePriceAfterCount * (1+testRep);
                updateInformation.add(testRepInformation);
            }
//            testOrganizationInformation.setTestAnalysisPrice(ObjectUtils.toString(testTotalAnalysisPrice));
//            testOrganizationInformation.setTestSamplePrice(ObjectUtils.toString(testTotalSamplePrice));
//            testOrganizationInformation.setTestAnalysisPriceAfterCount(ObjectUtils.toString(testTotalAnalysisPriceAfterCount));
//            testOrganizationInformation.setTestSamplePriceAfterCount(ObjectUtils.toString(testTotalSamplePriceAfterCount));
            testOrganizationInformation.setTotalTestPriceAfterCount(ObjectUtils.toString(testTotalAnalysisPriceAfterCount + testTotalSamplePriceAfterCount));
            updateInformation.add(testOrganizationInformation);
            //计算附加费用
            Double addTotalPrice = 0.0;//总附加费用
            for(EmcQuotationinformationBean k:addInformationList){
                if(!ObjectUtils.isEmpty(k.getAddUnitPrice()) && !ObjectUtils.isEmpty(k.getAddNumber())){
                    Double price = Double.parseDouble(k.getAddUnitPrice()) * Double.parseDouble(k.getAddNumber());
                    k.setAddPrice(ObjectUtils.toString(price));
                    addTotalPrice += price;
                    updateInformation.add(k);
                }
            };
            //总费用
            totalInformation.setTotalTestPrice(NumberUtils.rounding(ObjectUtils.toString(testTotalAnalysisPrice + testTotalSamplePrice),"FDA,2"));
            totalInformation.setTotalTestPriceAfterCount(NumberUtils.rounding(ObjectUtils.toString(testTotalAnalysisPriceAfterCount + testTotalSamplePriceAfterCount),"FDA,2"));
            totalInformation.setTotalAddPrice(NumberUtils.rounding(ObjectUtils.toString(addTotalPrice),"FDA,2"));
            totalInformation.setAddPrice(NumberUtils.rounding(ObjectUtils.toString(addTotalPrice),"FDA,2"));
            totalInformation.setTotalPriceAfterTotalCount(NumberUtils.rounding(ObjectUtils.toString((testTotalAnalysisPriceAfterCount + testTotalSamplePriceAfterCount)*totalDiscount),"FDA,2"));
            totalInformation.setTotalPrice(NumberUtils.rounding(ObjectUtils.toString((testTotalAnalysisPriceAfterCount + testTotalSamplePriceAfterCount)*totalDiscount + addTotalPrice),"FDA,2"));
            totalInformation.setTotalTaxAmount(NumberUtils.rounding(ObjectUtils.toString(Double.parseDouble(totalInformation.getTotalPrice())*0.06),"FDA,2"));
            totalInformation.setTotalPriceAfterTax(NumberUtils.rounding(ObjectUtils.toString(Double.parseDouble(totalInformation.getTotalPrice())*1.06),"FDA,2"));
            totalInformation.setTotalCost(NumberUtils.rounding(ObjectUtils.toString(Double.parseDouble(totalInformation.getTotalTestPrice()) * 0.25 + addTotalPrice + Double.parseDouble(totalInformation.getTotalTaxAmount())),"FDA,2"));
            totalInformation.setTotalProfit(NumberUtils.rounding(ObjectUtils.toString(Double.parseDouble(totalInformation.getTotalPriceAfterTax()) - Double.parseDouble(totalInformation.getTotalCost())),"FDA,2"));
//            if(!CollectionUtils.isEmpty(testInformationList)){
//                totalInformation.setTotalDiscount(testInformationList.get(0).getTestSampleDiscount());
//            }

            //报价单登记
            if(!CollectionUtils.isEmpty(testInformationList)){
                e.setTotalDiscount(totalDiscount.toString());
                e.setMonitorTmplName(testInformationList.get(0).getMonitorTmplName());
            }
            e.setTotalPrice(totalInformation.getTotalPriceAfterTax());
            getDao().updateIfChanged(e);

            updateInformation.add(totalInformation);

        });
        quotationinformationService.getDao().updateIfChanged(updateInformation);
    }

    @Override
    public void addContract(RestJsonWrapperBean wrapper) {
        List<EmcQuotationregisterBean> registerList = wrapper.parse(EmcQuotationregisterBean.class);
        List<EmcContractBean> insertContractList = new ArrayList<>();
        List<EmcMonitorItemBean> insertMonitorItemList = new ArrayList<>();
        registerList.forEach(e -> {
            List<EmcQuotationinformationBean> informationList = quotationinformationService.getDao().selectListByOneColumnValue(e.getId(),"QUOTATIONREGISTERID");
            //检测项目
            List<EmcQuotationinformationBean> testInformationList = informationList.stream().filter(m -> "test".equals(m.getType()))
                    .filter(m -> !"检测费用合计".equals(m.getTestName()) && !"检测报告编制费(15%)".equals(m.getTestName())).collect(Collectors.toList());

            EmcContractBean contracBean = new EmcContractBean();
            Long contracId = ApplicationContextHelper.getNextIdentity();
            contracBean.setId(contracId);
            //LocalDate registerDate = LocalDate.now();

            String contractCode = CONTRACT_NO_KEYS[Arrays.asList(CONTRACT_CATEGORY_CODES).indexOf(e.getContractCategory())];
            String nextSequenceNo = getSequenceNo(contractCode);
//            String contractNo = String.format(CONTRACT_NO_PATTERN, (year + "").substring(2), contractCode, StringUtils.leftPad(month + "", 2), nextSequenceNo.substring(2));

            contracBean.setContractNo(nextSequenceNo);
            contracBean.setReviewer(LocalContextHelper.getLoginUserId());

            //甲方乙方
            contracBean.setExt$Item("ENTRUSTORGNAME", e.getCustomerName());
            contracBean.setExt$Item("ENTRUSTORGADDRESS", e.getCustomerAddress());
            contracBean.setExt$Item("ENTRUSTORGPHONE", e.getCustomerPhone());
            contracBean.setExt$Item("CONTRACTORNAME", e.getCompanyName());
            contracBean.setExt$Item("CONTRACTORADDRESS", e.getCompanyAddress());
            contracBean.setReviewer(LocalContextHelper.getLoginUserId());
            // 查询合同报表，默认取第一个
            List<CoreReportConfigLineBean> reportConfigLineList = reportConfigLineService.getDao().selectListByOneColumnValue("contract", "REPORTCONFIGID", Collections.singletonList("ID"));
            if (!reportConfigLineList.isEmpty()) {
                contracBean.setExt$Item("REPORTCONFIGLINEID", reportConfigLineList.get(0).getId() + "");
            }
            insertContractList.add(contracBean);

            //检测项目
            testInformationList.forEach(m -> {
                EmcMonitorItemBean monitorItemBean = new EmcMonitorItemBean();
                monitorItemBean.setContractId(contracId);
                monitorItemBean.setId(ApplicationContextHelper.getNextIdentity());
                monitorItemBean.setTestId(Long.parseLong(m.getTestId()));
                monitorItemBean.setMethodId(Long.parseLong(m.getMethodId()));
                monitorItemBean.setExt$Item("folderNum", m.getTestMonitorNum());
                monitorItemBean.setExt$Item("frequency", m.getTestFrequency());
                monitorItemBean.setExt$Item("sampleNum", m.getTestDayNum());
                insertMonitorItemList.add(monitorItemBean);

            });
        });
        contractService.getDao().insert(insertContractList);
        monitorItemService.getDao().insert(insertMonitorItemList);
    }

    private String getSequenceNo(String key) {
        String tableName = getDao().getTable();
        String sequenceId = tableName;
        CoreSequenceBean sequence = sequenceService.getDao().selectByIdIfPresent(sequenceId);
        if (sequence == null) {
            CoreSequenceBean basicSequence = sequenceService.getDao().selectByIdIfPresent(tableName);
            sequence = new CoreSequenceBean();
            sequence.setId(sequenceId);
            sequence.setPattern(basicSequence.getPattern());

            String nextSequence = ApplicationContextHelper.getNextSequence(tableName, key);
            List<CoreSequenceParamDTO> paramList = new ArrayList<>();
            paramList.add(getNewSequenceParam(basicSequence.getPattern(), key, 1));
            sequence.setJsonParam(JSON.toJSONString(paramList));

            basicSequence.setJsonParam(null);
            sequenceService.getDao().insert(sequence);
            sequenceService.getDao().update(basicSequence, "JSONPARAM");

            return nextSequence;
        }

        return ApplicationContextHelper.getNextSequence(sequenceId, key);
    }

    private CoreSequenceParamDTO getNewSequenceParam(String pattern, String key, int qty) {
        CoreSequenceParamDTO param = new CoreSequenceParamDTO();

        List<String> substringList = StringUtils.substringsBetween(pattern, "[", "]");

        substringList.forEach(s -> {
            if ("key".equals(s)) {
                param.setKey(key);
            } else if ("yyyy".equals(s)) {
                param.setYyyy(String.format("%04d", LocalDate.now().getYear()));
            } else if ("yy".equals(s)) {
                param.setYy(String.format("%02d", LocalDate.now().getYear() % 100));
            } else if ("mm".equals(s)) {
                param.setMm(String.format("%02d", LocalDate.now().getMonthValue()));
            } else if ("dd".equals(s)) {
                param.setDd(String.format("%02d", LocalDate.now().getDayOfMonth()));
            } else if (StringUtils.repeatOf(s, "n")) {
                param.setN(qty);
            }
        });

        return param;
    }
}
