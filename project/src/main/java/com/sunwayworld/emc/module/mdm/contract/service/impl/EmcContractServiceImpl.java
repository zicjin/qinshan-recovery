package com.sunwayworld.emc.module.mdm.contract.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcExpenseCategoryBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewBasicInfoBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewContentBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcContractDao;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcExpenseCategoryService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcMonitorItemService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcReviewBasicInfoService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcReviewContentService;
import com.sunwayworld.emc.module.mdm.contract.validator.EmcContractAddTestItemValidator;
import com.sunwayworld.emc.module.mdm.contract.validator.EmcContractStartProcessValidator;
import com.sunwayworld.emc.module.mdm.contract.validator.EmcContractUndoValidator;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.audit.constant.AuditConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import com.sunwayworld.module.sys.code.service.CoreCodeCategoryService;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceParamDTO;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;

@Repository
@GikamBean
public class EmcContractServiceImpl implements EmcContractService {
    
    private static final String[] CONTRACT_CATEGORY_CODES = { "entrust", "sample", "consult" };
    private static final String[] CONTRACT_NO_KEYS = { "WT", "SY", "ZX" };
    private static final String CONTRACT_NO_PATTERN = "%s%s%s%s";
    private static final String[] CONTRACT_REVIEW_CATEGORIES = { "reviewContent", "reviewConclusion", "reviewRemark" };

    @Autowired
    private EmcContractDao emcContractDao;

    @Autowired
    private EmcReviewContentService emcReviewContentService;

    @Autowired
    private EmcExpenseCategoryService emcExpenseCategoryService;

    @Autowired
    private EmcMonitorItemService emcMonitorItemService;

    @Autowired
    private EmcReviewBasicInfoService reviewBasicInfoService;
    
    @Autowired
    private EmcClientService clientService;

    @Autowired
    private EmcMonitorItemService monitorItemService;

    @Autowired
    private EmcReviewContentService reviewContentService;

    @Autowired
    private CoreSequenceService sequenceService;

    @Autowired
    private CoreReportConfigLineService reportConfigLineService;

    @Autowired
    private CoreCodeCategoryService coreCodeCategoryService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcContractDao getDao() {
        return emcContractDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcContractBean emcContract = jsonWrapper.parseUnique(EmcContractBean.class);
        emcContract.setId(ApplicationContextHelper.getNextIdentity());

        String registerDateStr = emcContract.getExt$Item("registerdate");
        LocalDate registerDate = DateTimeUtils.parseLocalDate(registerDateStr);
        int year = registerDate.getYear();
        int month = registerDate.getMonth().getValue();

        String contractCategory = emcContract.getExt$Item("contractcategory");
        String contractCode = CONTRACT_NO_KEYS[Arrays.asList(CONTRACT_CATEGORY_CODES).indexOf(contractCategory)];
        String nextSequenceNo = getSequenceNo(registerDate.getYear(), contractCode);
        String contractNo = String.format(CONTRACT_NO_PATTERN, (year + "").substring(2), contractCode, StringUtils.leftPad(month + "", 2), nextSequenceNo.substring(2));

        emcContract.setContractNo(contractNo);

        // 根据客户标记查询乙方单位，默认取第一个
        EmcClientBean filterClient = new EmcClientBean();
        filterClient.setExt$Item("clientTag", "contractor");
        EmcClientBean contractor = clientService.getDao().selectFirstIfPresent(filterClient, Arrays.asList("CLIENTTAG"),
                Arrays.asList("ID", "CLIENTNAME", "ADDRESS", "CREDITCODE", "OPENINGBANK", "BANKACCOUNT", "PHONE"));
        if (contractor != null) {
            emcContract.setExt$Item("contractororgid", contractor.getId() + "");
            emcContract.setExt$Item("contractorname", contractor.getClientName());
            emcContract.setExt$Item("contractoraddress", contractor.getAddress());
            emcContract.setExt$Item("contractorlicense", contractor.getCreditCode());
            emcContract.setExt$Item("contractorbank", contractor.getExt$Item("openingbank"));
            emcContract.setExt$Item("contractoraccount", contractor.getExt$Item("bankaccount"));
            emcContract.setExt$Item("contractorphone", contractor.getPhone());
        }
        emcContract.setExt$Item("audit", Constant.NO);
        emcContract.setExt$Item("subpackage", Constant.NO);
        emcContract.setExt$Item("nonstandardmethod", Constant.NO);
        emcContract.setExt$Item("resultevaluation", Constant.NO);
        emcContract.setReviewer(LocalContextHelper.getLoginUserId());

        // 查询合同报表，默认取第一个
        List<CoreReportConfigLineBean> reportConfigLineList = reportConfigLineService.getDao().selectListByOneColumnValue("contract", "REPORTCONFIGID", Collections.singletonList("ID"));
        if (!reportConfigLineList.isEmpty()) {
            emcContract.setExt$Item("REPORTCONFIGLINEID", reportConfigLineList.get(0).getId() + "");
        }

        getDao().insert(emcContract);

        return emcContract.getId();
    }

    @Override
    public Page<EmcMonitorItemBean> selectMonitorItemPagination(Long contractId, RestJsonWrapperBean wrapper) {
        return monitorItemService.selectPaginationByFilter(SearchFilter.instance().match("CONTRACTID", contractId).filter(MatchPattern.SEQ), wrapper);
    }
    
    @Override
    public Page<EmcReviewContentBean> selectReviewContentPaginationByContractId(Long contractId, RestJsonWrapperBean wrapper) {
        return emcReviewContentService.selectPaginationByFilter(SearchFilter.instance().match("CONTRACTID", contractId).filter(MatchPattern.SEQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertExpenseCategory(Long contractId, RestJsonWrapperBean wrapper) {
        emcExpenseCategoryService.insert(contractId, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteExpenseCategory(RestJsonWrapperBean wrapper) {
        emcExpenseCategoryService.delete(wrapper);
    }

    @Override
    public Page<EmcExpenseCategoryBean> selectExpenseCategoryPaginationByContractId(Long contractId, RestJsonWrapperBean wrapper) {
        return emcExpenseCategoryService.selectPaginationByFilter(SearchFilter.instance().match("CONTRACTID", contractId).filter(MatchPattern.SEQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    @ValidateDataWith(EmcContractAddTestItemValidator.class)
    public void insertMonitorItem(RestJsonWrapperBean wrapper) {
        emcMonitorItemService.insert(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteMonitorItem(RestJsonWrapperBean wrapper) {
        emcMonitorItemService.delete(wrapper);
    }

    @Override
    public Page<EmcMonitorItemBean> selectMonitorItemPagination(Long contractId, Long expenseCategoryId, RestJsonWrapperBean wrapper) {
        return emcMonitorItemService.selectPaginationByFilter(
                SearchFilter.instance().match("CONTRACTID", contractId).filter(MatchPattern.SEQ).match("EXPENSECATEGORYID", expenseCategoryId).filter(MatchPattern.SEQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyExpenseCategory(RestJsonWrapperBean wrapper) {
        emcExpenseCategoryService.copyExpenseCategory(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyContract(RestJsonWrapperBean wrapper) {
        List<EmcContractBean> contractList = wrapper.parse(EmcContractBean.class);
        if (!contractList.isEmpty()) {
            List<Long> contractIdList = contractList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcContractBean> allContractList = getDao().selectListByIds(contractIdList);
            List<EmcReviewContentBean> allReviewContentList = emcReviewContentService.getDao().selectListByOneColumnValues(contractIdList, "CONTRACTID");
            List<EmcExpenseCategoryBean> allExpenseCategoryList = emcExpenseCategoryService.getDao().selectListByOneColumnValues(contractIdList, "CONTRACTID");

            List<Long> expenseCategoryIdList = allExpenseCategoryList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcMonitorItemBean> allMonitorItemList = emcMonitorItemService.getDao().selectListByOneColumnValues(expenseCategoryIdList, "EXPENSECATEGORYID");

            List<EmcContractBean> insertContractList = new ArrayList<>();
            List<EmcReviewContentBean> insertReviewContentList = new ArrayList<>();
            List<EmcExpenseCategoryBean> insertExpenseCategoryList = new ArrayList<>();
            List<EmcMonitorItemBean> insertMonitorItemList = new ArrayList<>();

            allContractList.stream().forEach(e -> {
                Long oldId = e.getId();
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setContractNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
                insertContractList.add(e);
                allReviewContentList.stream().forEach(m -> {
                    if (oldId.equals(m.getContractId())) {
                        m.setId(ApplicationContextHelper.getNextIdentity());
                        m.setContractId(e.getId());
                        insertReviewContentList.add(m);
                    }
                });

                allExpenseCategoryList.stream().forEach(m -> {
                    Long oldExpenseCategoryId = m.getId();
                    if (oldId.equals(m.getContractId())) {
                        m.setId(ApplicationContextHelper.getNextIdentity());
                        m.setContractId(e.getId());
                        insertExpenseCategoryList.add(m);

                        allMonitorItemList.stream().forEach(n -> {
                            if (oldExpenseCategoryId.equals(n.getExpenseCategoryId())) {
                                n.setId(ApplicationContextHelper.getNextIdentity());
                                n.setExpenseCategoryId(m.getId());
                                n.setContractId(e.getId());
                                insertMonitorItemList.add(n);
                            }
                        });
                    }
                });
            });

            getDao().insert(insertContractList);
            emcReviewContentService.getDao().insert(insertReviewContentList);
            emcExpenseCategoryService.getDao().insert(insertExpenseCategoryList);
            emcMonitorItemService.getDao().insert(insertMonitorItemList);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void calculateTotalPrice(Long id, RestJsonWrapperBean wrapper) {
        EmcContractBean contract = selectById(id);
        List<EmcExpenseCategoryBean> expenseCategoryList = emcExpenseCategoryService.selectListByFilter(SearchFilter.instance().match("CONTRACTID", id).filter(MatchPattern.SEQ));
        if (!expenseCategoryList.isEmpty()) {
            List<Long> expenseCategoryIdList = expenseCategoryList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcMonitorItemBean> monitorItemList = emcMonitorItemService.getDao().selectListByOneColumnValues(expenseCategoryIdList, "EXPENSECATEGORYID");
            if (!monitorItemList.isEmpty()) {
                Collection<List<EmcMonitorItemBean>> groupList = monitorItemList.stream().collect(Collectors.groupingBy(EmcMonitorItemBean::getExpenseCategoryId)).values();

                groupList.stream().forEach(e -> {
                    Double sumPrice = e.stream().mapToDouble(m -> NumberUtils.parseDouble(m.getPrice())).sum();
                    expenseCategoryList.stream().forEach(p -> {
                        if (e.get(0).getExpenseCategoryId().equals(p.getId())) {
                            p.setTotalPrice(sumPrice.toString());
                        }
                    });
                });

                Double totalPrice = expenseCategoryList.stream().mapToDouble(e -> NumberUtils.parseDouble(e.getTotalPrice())).sum();
                contract.setTotalPrice(totalPrice.toString());

                getDao().update(contract, "TOTALPRICE");
                emcExpenseCategoryService.getDao().update(expenseCategoryList, "TOTALPRICE");
            }
        }
    }

    @Override
    public EmcReviewBasicInfoBean selectReviewBasicInfo(Long id) {
        List<EmcReviewBasicInfoBean> basicInfoList = reviewBasicInfoService.getDao().selectListByOneColumnValue(id, "CONTRACTID");
        if (basicInfoList.isEmpty()) {
            return null;
        }

        return basicInfoList.get(0);
    }
    
    private String getSequenceNo(int year, String key) {
        String tableName = getDao().getTable();
        String sequenceId = tableName + "_" + year;
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
