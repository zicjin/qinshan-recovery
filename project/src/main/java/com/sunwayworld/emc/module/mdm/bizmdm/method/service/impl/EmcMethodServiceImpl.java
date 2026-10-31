package com.sunwayworld.emc.module.mdm.bizmdm.method.service.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.dao.EmcMethodDao;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodVersionService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.validator.EmcCoreCodeEmptyValidator;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */
@GikamBean
@Repository
public class EmcMethodServiceImpl implements EmcMethodService {
    @Autowired
    private EmcMethodDao emcMethodDao;

    @Autowired
    private EmcMethodVersionService methodVersionService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcProjOrderService projOrderService;

    @Autowired
    private CoreRemoteCallRequestService coreRemoteCallRequestService;
    
    @Autowired
    @Lazy
    private EmcTestMethodService emcTestMethodService;
    

    @Autowired
    private CoreFileService fileService;
    
    @Autowired
    private CoreCodeService coreCodeService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMethodDao getDao() {
        return emcMethodDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcMethodBean emcMethod = jsonWrapper.parseUnique(EmcMethodBean.class);
        emcMethod.setId(ApplicationContextHelper.getNextSequenceNumber(getDao().getTable()));
        getDao().insert(emcMethod);
        return emcMethod.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateVersionInfo(Long id) {
        EmcMethodBean method = getDao().selectById(id);
        List<EmcMethodVersionBean> versionList = methodVersionService.getDao().selectListByOneColumnValue(id, "METHODID");
        if (!versionList.isEmpty()) {
            EmcMethodVersionBean maxVersion = versionList.stream().max(Comparator.comparingLong(EmcMethodVersionBean::getVersionNo)).get();
            method.setMaxVersionId(maxVersion.getId());
            method.setMaxVersionNo(maxVersion.getVersionNo());
            method.setTestMethodBasis(maxVersion.getTestMethodBasis());
            method.setMaxElnTmplId(maxVersion.getElnTmplId());
            getDao().update(method, "MAXVERSIONID", "MAXVERSIONNO", "TESTMETHODBASIS", "MAXELNTMPLID");
        }
    }

    @Override
    public Long selectReportConfigLineIdById(Long id) {
        return getDao().selectReportConfigLineIdById(id);
    }

    @Override
    public List<Long> checkUsed(RestJsonWrapperBean wrapper) {
        List<Long> methodIdList = wrapper.parseId(Long.class);

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(methodIdList, "METHODID");

        if (orderItemGroupList.isEmpty()) {
            List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(methodIdList, "METHODID");
            return projOrderList.stream().map(p -> p.getId()).collect(Collectors.toList());
        }

        return orderItemGroupList.stream().map(g -> g.getId()).collect(Collectors.toList());
    }

    // 远程接口调用
    public String getPage(String url) {
        String response = HttpClientManager.getInstance().sendHttpGet(url);
        return StringUtils.replace(response, "\n", "");
    }

    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    private void batchInsert(List<EmcMethodBean> methodAddList, List<EmcMethodVersionBean> methodVersionAddList, List<Long> methodIdList) {
        if (!methodAddList.isEmpty()) {
            getDao().insert(methodAddList);
        }
        if (!methodVersionAddList.isEmpty()) {
            methodVersionService.getDao().insert(methodVersionAddList);
            updateMethodVersion(methodIdList);
        }
    }

    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    // 更新方法标准，最大方法版本ID、版本号、检测方法及依据
    private void updateMethodVersion(List<Long> methodIdList) {
        List<EmcMethodBean> methodFilterList = getDao().selectListByIds(methodIdList);
        List<EmcMethodVersionBean> methodVersionFilterList = methodVersionService.getDao().selectListByOneColumnValues(methodIdList, "METHODID");
        if (!methodFilterList.isEmpty()) {
            List<EmcMethodBean> methodUpdateList = new ArrayList<>();
            methodFilterList.forEach(method -> {
                Long methodId = method.getId();
                Optional<EmcMethodVersionBean> methodVersionOptional = methodVersionFilterList.stream().filter(e -> methodId.equals(e.getMethodId()))
                        .max(Comparator.comparingLong(EmcMethodVersionBean::getVersionNo));
                if (methodVersionOptional.isPresent()) {
                    EmcMethodVersionBean methodVersion = methodVersionOptional.get();
                    method.setMaxVersionId(methodVersion.getId());
                    method.setMaxVersionNo(methodVersion.getVersionNo());
                    method.setTestMethodBasis(methodVersion.getTestMethodBasis());
                    method.setMaxElnTmplId(methodVersion.getElnTmplId());
                    methodUpdateList.add(method);
                }
            });
            if (!methodUpdateList.isEmpty())
                getDao().update(methodUpdateList, "MAXVERSIONID", "MAXVERSIONNO", "TESTMETHODBASIS", "MAXELNTMPLID");
        }
    }

    private void dealMethod(String response, boolean syncFlag, List<EmcMethodBean> methodList, List<EmcMethodVersionBean> methodVersionList, List<EmcMethodBean> methodAddList,
            List<EmcMethodVersionBean> methodVersionAddList, List<Long> methodIdList) { // 处理易标准更新数据
        JSONObject responseJson = JSONObject.parseObject(response);
        if (syncFlag) { // syncFlag：true同步易标准数据，false更新易标准数据
            JSONArray contents = responseJson.getJSONArray("content"); // 获取content数组
            if (contents != null) {
                contents.forEach(content -> {
                    JSONObject spec = ((JSONObject) content).getJSONObject("spec"); // Spec标准信息
                    dealData(spec, syncFlag, methodList, methodVersionList, methodAddList, methodVersionAddList, methodIdList);
                });
            }
        }
    }

    private void dealData(JSONObject spec, boolean syncFlag, List<EmcMethodBean> methodList, List<EmcMethodVersionBean> methodVersionList, List<EmcMethodBean> methodAddList,
            List<EmcMethodVersionBean> methodVersionAddList, List<Long> methodIdList) {
        String specCode; // 获取标准编号
        String specName; // 获取标准名称
        if (syncFlag) { // syncFlag 区分易标准同步和更新接口字段
            specCode = spec.getString("code");
            specName = spec.getString("name");
        } else {
            specCode = spec.getString("speccode");
            specName = spec.getString("specname");
        }

        String specCodeFront = spec.getString("code"); // 根据标准标号截取的
        String methodName;
        String methodBasis;
        StringBuilder methodBasisSb = new StringBuilder("《").append(specName).append("》");
        if (StringUtils.isEmpty(specCodeFront)) {
            methodName = specCode;
        } else {
            methodName = specCodeFront;
            methodBasisSb.append(specCodeFront);
        }
        methodBasis = methodBasisSb.toString();
        String abolishDate = spec.getString("abolishdate"); // 废止日期
        LocalDateTime expireDate = null;
        if (!StringUtils.isEmpty(abolishDate) && syncFlag) { // 获取能力数据接口这个字段精确到日期，获取最新的检测能力查新接口精确到时分秒，两个接口返回的格式不一致
            abolishDate = abolishDate + " 00:00:00";
            expireDate = DateTimeUtils.parseLocalDateTime(abolishDate); // 废止日期
        } else if (!StringUtils.isEmpty(abolishDate)) {
            expireDate = DateTimeUtils.parseLocalDateTime(abolishDate); // 废止日期
        }

        List<EmcMethodBean> methodFilterList;
        methodFilterList = methodAddList.parallelStream().filter(e -> StringUtils.equals(methodName, e.getMethodName())).collect(Collectors.toList()); // 先查询方法添加列表
        if (methodFilterList.isEmpty()) {
            methodFilterList = methodList.parallelStream().filter(e -> StringUtils.equals(methodName, e.getMethodName())).collect(Collectors.toList()); // 再查询方法表
        } else {
            return;
        }
        EmcMethodBean method;
        if (methodFilterList.isEmpty()) { // 没有方法，增加方法表和方法版本表数据
            Long methodId = ApplicationContextHelper.getNextSequenceNumber(getDao().getTable());
            // 增加方法表
            method = new EmcMethodBean();
            method.setId(methodId);
            method.setMethodName(methodName);
            method.setExt$Item("expiredate", expireDate == null ? null : expireDate.toString());
            method.setExt$Item("versionno", getMaxVersionNo(methodId, methodVersionList, methodVersionAddList).toString());
            method.setExt$Item("versionbasis", methodBasis);
            method.setExt$Item("status", EmcConstant.ESTANDARD_INSERT);
            methodAddList.add(method);
            methodIdList.add(methodId);
        } else { // 方法表有数据后，增加方法版本表
            method = methodFilterList.get(0);
            if (EmcConstant.ESTANDARD_INSERT.equals(method.getExt$Item("status"))) {
                return;
            }
            Long methodId = method.getId();
            List<EmcMethodVersionBean> methodVersionFilterList;
            methodVersionFilterList = methodVersionAddList.parallelStream().filter(e -> methodId.equals(e.getMethodId()) && StringUtils.equals(methodBasis, e.getTestMethodBasis()))
                    .collect(Collectors.toList()); // 先查询方法版本添加列表
            if (methodVersionFilterList.isEmpty()) {
                methodVersionFilterList = methodVersionList.parallelStream().filter(e -> methodId.equals(e.getMethodId()) && StringUtils.equals(methodBasis, e.getTestMethodBasis()))
                        .collect(Collectors.toList()); // 再查询方法版本表
                if (methodVersionFilterList.isEmpty()) {
                    method.setExt$Item("expiredate", expireDate == null ? null : expireDate.toString());
                    method.setExt$Item("versionno", getMaxVersionNo(methodId, methodVersionList, methodVersionAddList).toString());
                    method.setExt$Item("versionbasis", methodBasis);
                    method.setExt$Item("status", EmcConstant.ESTANDARD_UPDATE);
                    methodAddList.add(method);
                    methodIdList.add(methodId);
                }
            }
        }
    }

    private Long getMaxVersionNo(Long methodId, List<EmcMethodVersionBean> methodVersioList, List<EmcMethodVersionBean> methodVersionAddList) {
        Optional<EmcMethodVersionBean> methodVersionOptional = methodVersionAddList.stream().filter(e -> methodId.equals(e.getMethodId()))
                .max(Comparator.comparingLong(EmcMethodVersionBean::getVersionNo)); // 先查询添加列表
        if (!methodVersionOptional.isPresent())
            methodVersionOptional = methodVersioList.stream().filter(e -> methodId.equals(e.getMethodId())).max(Comparator.comparingLong(EmcMethodVersionBean::getVersionNo)); // 再查询方法版本表

        EmcMethodVersionBean methodVersion;
        if (methodVersionOptional.isPresent()) {
            methodVersion = methodVersionOptional.get();
            return methodVersion.getVersionNo() + 1L;
        } else {
            return 1L;
        }
    }

    @Override
    public Page<EmcMethodBean> getEstandard(RestJsonWrapperBean wrapper) {

        String estandardUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.url"); // 易标准网址
        String accountId = ApplicationContextHelper.getEnvironment().getProperty("estandard.accountid"); // 企业易标准账户ID
        String testAbilityUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.testability.url"); // 易标准获取能力数据接口
        
        if(StringUtils.isEmpty(estandardUrl) || StringUtils.isEmpty(accountId) || StringUtils.isEmpty(testAbilityUrl)) {
            
            throw new UnexpectedException(I18nHelper.getMessage("GIKAM.EXCEPTION.ESTANDARD_ERROR"));
        }
        int pageSize = 50; // 每页返回多条记录
        String syncUrl = new StringBuilder(estandardUrl).append(testAbilityUrl).append("?querytype=1&accountid=").append(accountId).append("&size=").append(pageSize).append("&page=0").toString();
        String response = coreRemoteCallRequestService.processSyncRequest("2", syncUrl);

        JSONObject jsonEstandard = JSONArray.parseObject(response);
        String totalPages = jsonEstandard.getString("totalPages");
        if (totalPages == null) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.METHODS.TIP.SYNC_FAIL");
        }
        int totalPageNum = NumberUtils.parseInt(totalPages); // 总共多少页

        if (totalPageNum != 0) {
            List<EmcMethodBean> methodList = getDao().selectAll("ID", "METHODNAME"); // 方法表
            List<EmcMethodVersionBean> methodVersionList = methodVersionService.getDao().selectAll("ID", "METHODID", "VERSIONNO", "TESTMETHODBASIS", "ELNTMPLID"); // 方法版本表

            List<EmcMethodBean> methodAddList = new ArrayList<>();
            List<EmcMethodVersionBean> methodVersionAddList = new ArrayList<>();
            List<Long> methodIdList = new ArrayList<>();

            dealMethod(response, true, methodList, methodVersionList, methodAddList, methodVersionAddList, methodIdList); // 处理第一页数据
            methodAddList.stream().map(e -> e.getMethodName()).distinct().collect(Collectors.toList());
            CollectionUtils.sort(methodAddList, Comparator.comparing(EmcMethodBean::getId));
            return new Pagination<>(totalPageNum, 50, 1, methodAddList);

        }
        return null;
    }

    @Transactional
    public void estandSync(RestJsonWrapperBean wrapper) {
        List<EmcMethodBean> methodList = wrapper.parse(EmcMethodBean.class);
        List<EmcMethodBean> insertMethodList = new ArrayList<>();
        List<EmcMethodVersionBean> methodVersionAddList = new ArrayList<>();
        methodList.stream().forEach(e -> {
            if (EmcConstant.ESTANDARD_INSERT.equals(e.getExt$Item("status"))) {
                insertMethodList.add(e);
            }
            EmcMethodVersionBean methodVersion = new EmcMethodVersionBean();
            methodVersion.setId(ApplicationContextHelper.getNextIdentity());
            methodVersion.setMethodId(e.getId());
            methodVersion.setExpireDate(DateTimeUtils.parseLocalDateTime(e.getExt$Item("expiredate")));
            methodVersion.setVersionNo(NumberUtils.parseLong(e.getExt$Item("versionno")));
            methodVersion.setTestMethodBasis(e.getExt$Item("versionbasis"));
            methodVersionAddList.add(methodVersion);
        });
        getDao().insert(insertMethodList);
        methodVersionService.getDao().insert(methodVersionAddList);
        updateMethodVersion(methodList.stream().map(e -> e.getId()).collect(Collectors.toList()));
    }
    
    @Override
    @Transactional
    public List<CoreFileBean> selectMethodFile(RestJsonWrapperBean wrapper) {
        String targetId = getDao().getTable() + '$' + String.valueOf(wrapper.getParamValue("id"));
        String bizCategory = wrapper.getParamValue("bizCategory");
        List<CoreFileBean> coreFileList = fileService.selectListByFilter(SearchFilter.instance().match("TARGETID", targetId).filter(MatchPattern.SEQ));
        List<CoreFileBean> coreMethodFile = coreFileList.stream().filter(f -> f.getBizCategory().equals(bizCategory)).collect(Collectors.toList());
        return coreMethodFile;
    }

    @Override
    @Transactional
    @ValidateDataWith(EmcCoreCodeEmptyValidator.class)
    public List<CoreCodeBean> selectCoreCodeByCodeNames(RestJsonWrapperBean wrapper) {
        String filePath = wrapper.getParamValue("path");
        List<String> category = Arrays.asList(wrapper.getParamValue("category").split(","));
        List<CoreCodeBean> coreCodeList = new ArrayList<CoreCodeBean>();
        HSSFWorkbook wb = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            wb = new HSSFWorkbook(is);

            // 第一张表格
            HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
            // 总行数
            int realRowCount = sheet.getPhysicalNumberOfRows();
            List<String> codeNameList = new ArrayList<String>();
            // 拿到关于方法类型,检测类别,版本,运行状态的数据
            for (int i = 1; i < realRowCount; i++) {
                Row row = sheet.getRow(i);
                codeNameList.add(ExcelHelper.getCellValue(row.getCell(3), String.class));
                codeNameList.add(ExcelHelper.getCellValue(row.getCell(10), String.class));
                codeNameList.add(ExcelHelper.getCellValue(row.getCell(12), String.class));

            }
            codeNameList = codeNameList.stream().filter(a -> !StringUtils.isEmpty(a)).distinct().collect(Collectors.toList());
            coreCodeList = coreCodeService.getDao().selectListByOneColumnValues(codeNameList, "codeName").stream().filter(f -> category.contains(f.getCodeCategoryId()))
                    .collect(Collectors.toList());

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

        return coreCodeList;
    }

    @Override
    @Transactional
    public void methodCopy(RestJsonWrapperBean wrapper) {
        // method源,methodBean对象
        EmcMethodBean method = wrapper.parseUnique(EmcMethodBean.class);
        EmcMethodBean methodBean = getDao().selectByIdIfPresent(method.getId());
        methodBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(methodBean);

    }
}
