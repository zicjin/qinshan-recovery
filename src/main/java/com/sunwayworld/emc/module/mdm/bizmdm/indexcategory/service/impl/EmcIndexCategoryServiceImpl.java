package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexItemService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexPreviewBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcIndexCategoryDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexPreviewService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcOrderIndexService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;

@Repository
@GikamBean
public class EmcIndexCategoryServiceImpl implements EmcIndexCategoryService {

    @Autowired
    private EmcIndexCategoryDao indexCategoryDao;

    @Autowired
    private EmcIndexSetService indexSetSerice;

    @Autowired
    private EmcOrderIndexService orderIndexService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private EmcIndexItemService indexItemService;

    @Autowired
    private EmcIndexPreviewService indexPreviewService;

    @Autowired
    private CoreRemoteCallRequestService coreRemoteCallRequestService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    private volatile boolean dealFlag = false; // 易标准方法同步标识

    @SuppressWarnings("unchecked")
    @Override
    public EmcIndexCategoryDao getDao() {
        return indexCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcIndexCategoryBean emcIndexCategoryBean = wrapper.parseUnique(EmcIndexCategoryBean.class);
        emcIndexCategoryBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcIndexCategoryBean);
        return emcIndexCategoryBean.getId();
    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void sync(RestJsonWrapperBean wrapper) {
        List<EmcIndexCategoryBean> indexCategoryList = wrapper.parse(EmcIndexCategoryBean.class);

        List<EmcIndexSetBean> indexSetFilterList = indexCategoryList.stream().map(e -> {
            EmcIndexSetBean indexSet = new EmcIndexSetBean();
            indexSet.setIndexCategoryId(e.getId());
            indexSet.setIndexItemId(null);
            return indexSet;

        }).collect(Collectors.toList());

        List<String> searchColNames = Arrays.asList(new String[] { "ID", "EVALINDEX" });

        List<String> selectColNames = Arrays.asList(new String[] { "IndexCategoryId", "IndexItemId" });

        List<EmcIndexSetBean> syncIndexSetList = indexSetSerice.getDao().selectList(indexSetFilterList, searchColNames, selectColNames);

        List<String> evalIndexList = syncIndexSetList.stream().map(e -> e.getEvalIndex()).collect(Collectors.toList());

        List<EmcIndexItemBean> indexItemList = indexItemService.getDao().selectListByOneColumnValues(evalIndexList, "indexItemName");

        List<EmcIndexSetBean> updateIndexSetList = new ArrayList<>();

        for (EmcIndexSetBean indexSet : syncIndexSetList) {

            Optional<EmcIndexItemBean> indexSetOptional = indexItemList.stream().filter(e -> e.getIndexItemName().equals(indexSet.getEvalIndex())).findFirst();

            if (indexSetOptional.isPresent()) {

                EmcIndexItemBean indexItem = indexSetOptional.get();

                indexSet.setIndexItemId(indexItem.getId());

                updateIndexSetList.add(indexSet);
            }

        }

        if (!updateIndexSetList.isEmpty()) {
            indexSetSerice.getDao().update(updateIndexSetList, "INDEXITEMID");
        }

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String estandSync() {
        if (dealFlag)
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_MESSAGE");
        dealFlag = true; // 更新同步标识

        String estandardUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.url"); // 易标准网址
        String accountId = ApplicationContextHelper.getEnvironment().getProperty("estandard.accountid"); // 企业易标准账户ID
        String testAbilityUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.testability.url"); // 易标准获取检测能力数据接口
        int pageSize = ApplicationContextHelper.getEnvironment().getProperty("estandard.pagesize", Integer.class, 30); // 每页返回多条记录
        long syncTimeout = ApplicationContextHelper.getEnvironment().getProperty("estandard.synctimeout", Long.class, 1200000L); // 同步易标准超时时间

        String syncUrl = new StringBuilder(estandardUrl).append(testAbilityUrl).append("?specType=1&accountid=").append(accountId).append("&size=").append(pageSize).append("&page=").toString();
        ExecutorService executors = null;
        try {
            String pageUrl = new StringBuilder(syncUrl).append("0").toString();
            String response = coreRemoteCallRequestService.processSyncRequest("1", pageUrl);
            JSONObject jsonEstandard = JSONArray.parseObject(response);
            String totalPages = jsonEstandard.getString("totalPages");
            if (totalPages == null) {
                throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_FAIL");
            }
            int totalPageNum = NumberUtils.parseInt(totalPages); // 总共多少页
            if (totalPageNum != 0) {
                List<EmcIndexCategoryBean> indexCategoryList = getDao().selectAll("ID", "STDNAME"); // 评价标准列表
                List<EmcOrderIndexBean> orderIndexList = orderIndexService.getDao().selectAll("ID", "INDEXCATEGORYID", "INDEX1", "INDEX2"); // 样品指标列表
                List<EmcIndexItemBean> indexItemList = indexItemService.getDao().selectAll("ID", "INDEXITEMNAME"); // 指标项目列表
                List<EmcIndexSetBean> indexSetList = indexSetService.getDao().selectAll("ID", "INDEXID", "INDEXCATEGORYID", "INDEXITEMID", "TEXTINDEXVALUE"); // 指标设置列表

                List<EmcIndexCategoryBean> indexCategoryAddList = new ArrayList<>();
                List<EmcOrderIndexBean> orderIndexAddList = new ArrayList<>();
                List<EmcIndexItemBean> indexItemAddList = new ArrayList<>();
                List<EmcIndexSetBean> indexSetAddList = new ArrayList<>();
                List<EmcPropertyDataBean> propertyDataList = new ArrayList<>();

                Map<String, Object> map = new HashMap<>();
                map.put("indexCategoryList", indexCategoryList);
                map.put("orderIndexList", orderIndexList);
                map.put("indexItemList", indexItemList);
                map.put("indexSetList", indexSetList);
                map.put("indexCategoryAddList", indexCategoryAddList);
                map.put("orderIndexAddList", orderIndexAddList);
                map.put("indexItemAddList", indexItemAddList);
                map.put("indexSetAddList", indexSetAddList);
                map.put("propertyDataList", propertyDataList);

                dealMethod(response, true, map); // 处理第一页数据
                int dealNum = totalPageNum - 1; // 处理页数（第一页已处理）
                if (dealNum > 0) {
                    int processSize = Runtime.getRuntime().availableProcessors(); // cpu线程数
                    int threads = (dealNum < processSize ? dealNum : processSize) + 1; // 执行任务线程数量
                    executors = Executors.newFixedThreadPool(threads);
                    exectorsExec(executors, threads, true, dealNum, syncUrl, map);
                    executors.awaitTermination(syncTimeout, TimeUnit.MILLISECONDS); // 设置超时时间，处理成功后，返回前端页面，提示同步成功
                }
                if (!indexItemAddList.isEmpty())
                    indexItemService.getDao().insert(indexItemAddList); // 指标项目列表
                batchInsert(indexCategoryAddList, orderIndexAddList, indexSetAddList, propertyDataList);
            }
        } catch (NetworkException e) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_TIMEOUT");
        } catch (InterruptedException e) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_FAIL");
        } finally {
            dealFlag = false; // 更新同步标识
            if (executors != null && !executors.isTerminated()) { // 终止线程池，释放资源
                executors.shutdownNow();
                throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_FAIL");
            }
        }
        return "{msg :\"EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_SUCCESS\"}"; // 同步成功提示信息;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String estandUpdate() {
        if (dealFlag)
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_MESSAGE");
        dealFlag = true; // 更新同步标识

        String estandardUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.url"); // 易标准网址
        String accountId = ApplicationContextHelper.getEnvironment().getProperty("estandard.accountid"); // 企业易标准账户ID
        String abilityUpdatesUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.abilityupdates.url"); // 易标准获取能力数据更新接口
        int pageSize = ApplicationContextHelper.getEnvironment().getProperty("estandard.pagesize", Integer.class, 30); // 每页返回多条记录
        long updateTimeout = ApplicationContextHelper.getEnvironment().getProperty("estandard.updatetimeout", Long.class, 600000L); // 更新易标准超时时间

        String updateUrl = new StringBuilder(estandardUrl).append(abilityUpdatesUrl).append("?specType=1&accountid=").append(accountId).append("&size=").append(pageSize).append("&page=").toString();
        ExecutorService executors = null;
        try {
            String pageUrl = new StringBuilder(updateUrl).append("0").toString();
            String response = coreRemoteCallRequestService.processSyncRequest("1", pageUrl);
            JSONObject jsonEstandard = JSONArray.parseObject(response);
            String totalPages = jsonEstandard.getString("pages");
            if (totalPages == null) {
                throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.UPDATE_FAIL");
            }
            int totalPageNum = NumberUtils.parseInt(totalPages); // 总共多少页
            if (totalPageNum != 0) {
                List<EmcIndexItemBean> indexItemList = indexItemService.getDao().selectAll("ID", "INDEXITEMNAME"); // 指标项目列表
                List<EmcIndexPreviewBean> indexPreviewList = indexPreviewService.getDao().selectAll("STDNAME", "INDEX1", "INDEX2", "INDEXITEMID", "TEXTINDEXVALUE"); // 指标预览列表
                List<EmcIndexItemBean> indexItemAddList = new ArrayList<>();
                List<EmcIndexPreviewBean> indexPreviewAddList = new ArrayList<>();

                Map<String, Object> map = new HashMap<>();
                map.put("indexItemList", indexItemList);
                map.put("indexPreviewList", indexPreviewList);
                map.put("indexItemAddList", indexItemAddList);
                map.put("indexPreviewAddList", indexPreviewAddList);

                dealMethod(response, false, map); // 处理第一页数据
                int dealNum = totalPageNum - 1; // 处理页数（第一页已处理）
                if (dealNum > 0) {
                    int processSize = Runtime.getRuntime().availableProcessors(); // cpu线程数
                    int threads = (dealNum < processSize ? dealNum : processSize) + 1; // 执行任务线程数量
                    executors = Executors.newFixedThreadPool(threads);
                    exectorsExec(executors, threads, false, dealNum, updateUrl, map);
                    executors.awaitTermination(updateTimeout, TimeUnit.MILLISECONDS); // 设置超时时间，处理成功后，返回前端页面，刷新页面
                }
                if (!indexItemAddList.isEmpty())
                    indexItemService.getDao().insert(indexItemAddList); // 指标项目表
                if (!indexPreviewAddList.isEmpty())
                    indexPreviewService.getDao().insert(indexPreviewAddList); // 指标预览表
            }
        } catch (NetworkException e) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.UPDATE_TIMEOUT");
        } catch (InterruptedException e) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.UPDATE_FAIL");
        } finally {
            dealFlag = false; // 更新同步标识
            if (executors != null && !executors.isTerminated()) { // 终止线程池，释放资源
                executors.shutdownNow();
                throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.UPDATE_FAIL");
            }
        }
        return "{msg :\"EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.UPDATE_SUCCESS\"}"; // 同步成功提示信息;
    }

    @Override
    // 指标预览添加评价标准表、样品指标表、指标设置表、动态配置表
    public void addIndex(RestJsonWrapperBean wrapper) {
        List<EmcIndexPreviewBean> indexPreviewList = wrapper.parse(EmcIndexPreviewBean.class);

        List<EmcIndexCategoryBean> indexCategoryList = getDao().selectAll("ID", "STDNAME"); // 评价标准列表
        List<EmcOrderIndexBean> orderIndexList = orderIndexService.getDao().selectAll("ID", "INDEXCATEGORYID", "INDEX1", "INDEX2"); // 样品指标列表
        List<EmcIndexSetBean> indexSetList = indexSetService.getDao().selectAll("ID", "INDEXID", "INDEXCATEGORYID", "INDEXITEMID", "TEXTINDEXVALUE"); // 指标设置列表

        List<EmcIndexCategoryBean> indexCategoryAddList = new ArrayList<>();
        List<EmcOrderIndexBean> orderIndexAddList = new ArrayList<>();
        List<EmcIndexSetBean> indexSetAddList = new ArrayList<>();
        List<EmcPropertyDataBean> propertyDataList = new ArrayList<>();

        indexPreviewList.forEach(indexPreview -> {
            String stdName = indexPreview.getStdName(); // 标准名称
            LocalDate expireDate = indexPreview.getExpireDate(); // 过期日期
            String index1 = indexPreview.getIndex1(); // 指标1
            String index2 = indexPreview.getIndex2(); // 指标2
            Long indexItemId = indexPreview.getIndexItemId(); // 指标项目
            String indexLowLimit = indexPreview.getIndexLowLimit(); // 指标低限
            String indexHighLimit = indexPreview.getIndexHighLimit(); // 指标高限
            String textIndexValue = indexPreview.getTextIndexValue(); // 文本指标值
            String unit = indexPreview.getUnit(); // 单位
            Long indexCategoryId; // 评价标准ID
            String orderIndexId; // 样品指标ID
            Long indexSetId; // 指标设置ID
            List<EmcIndexCategoryBean> indexCategoryFilterList = indexCategoryAddList.parallelStream().filter(e -> StringUtils.equals(stdName, e.getStdName())).collect(Collectors.toList()); // 先查询评价标准添加列表
            if (indexCategoryFilterList.isEmpty())
                indexCategoryFilterList = indexCategoryList.parallelStream().filter(e -> StringUtils.equals(stdName, e.getStdName())).collect(Collectors.toList()); // 在查询评价标准表
            if (indexCategoryFilterList.isEmpty()) {
                indexCategoryId = ApplicationContextHelper.getNextIdentity();
                indexCategoryAddList.add(indexCategory(indexCategoryId, stdName, expireDate));
                orderIndexId = ApplicationContextHelper.getNextSequence(orderIndexService.getDao().getTable());
                orderIndexAddList.add(orderIndex(orderIndexId, indexCategoryId, index1, index2));
                indexSetId = ApplicationContextHelper.getNextIdentity();
                indexSetAddList.add(indexSet(indexSetId, indexCategoryId, orderIndexId, indexItemId, indexLowLimit, indexHighLimit, textIndexValue, unit));
                propertyDataList.add(propertyData(indexSetId, indexCategoryId));
            } else {
                indexCategoryId = indexCategoryFilterList.get(0).getId();
                List<EmcOrderIndexBean> orderIndexFilterList = orderIndexAddList.parallelStream()
                        .filter(e -> indexCategoryId.equals(e.getIndexCategoryId()) && StringUtils.equals(index1, e.getIndex1()) && StringUtils.equals(index2, e.getIndex2()))
                        .collect(Collectors.toList()); // 先查询样品指标添加列表
                if (orderIndexFilterList.isEmpty())
                    orderIndexFilterList = orderIndexList.parallelStream()
                            .filter(e -> indexCategoryId.equals(e.getIndexCategoryId()) && StringUtils.equals(index1, e.getIndex1()) && StringUtils.equals(index2, e.getIndex2()))
                            .collect(Collectors.toList()); // 再查询样品指标列表
                if (orderIndexFilterList.isEmpty()) {
                    orderIndexId = ApplicationContextHelper.getNextSequence(orderIndexService.getDao().getTable());
                    orderIndexAddList.add(orderIndex(orderIndexId, indexCategoryId, index1, index2));
                    indexSetId = ApplicationContextHelper.getNextIdentity();
                    indexSetAddList.add(indexSet(indexSetId, indexCategoryId, orderIndexId, indexItemId, indexLowLimit, indexHighLimit, textIndexValue, unit));
                    propertyDataList.add(propertyData(indexSetId, indexCategoryId));
                } else {
                    orderIndexId = orderIndexFilterList.get(0).getId();
                    List<EmcIndexSetBean> indexSetFilterList = indexSetAddList.parallelStream().filter(e -> indexCategoryId.equals(e.getIndexCategoryId())
                            && StringUtils.equals(orderIndexId, e.getIndexId()) && indexItemId.equals(e.getIndexItemId()) && StringUtils.equals(textIndexValue, e.getTextIndexValue()))
                            .collect(Collectors.toList()); // 先查询指标设置添加列表
                    if (indexSetFilterList.isEmpty())
                        indexSetFilterList = indexSetList.parallelStream().filter(e -> indexCategoryId.equals(e.getIndexCategoryId()) && StringUtils.equals(orderIndexId, e.getIndexId())
                                && indexItemId.equals(e.getIndexItemId()) && StringUtils.equals(textIndexValue, e.getTextIndexValue())).collect(Collectors.toList()); // 再查询指标设置表
                    if (indexSetFilterList.isEmpty()) {
                        indexSetId = ApplicationContextHelper.getNextIdentity();
                        indexSetAddList.add(indexSet(indexSetId, indexCategoryId, orderIndexId, indexItemId, indexLowLimit, indexHighLimit, textIndexValue, unit));
                        propertyDataList.add(propertyData(indexSetId, indexCategoryId));
                    }
                }
            }
        });
        batchInsert(indexCategoryAddList, orderIndexAddList, indexSetAddList, propertyDataList);
    }

    // 远程接口调用
    public String getPage(String url) {
        String response = HttpClientManager.getInstance().sendHttpGet(url);
        return StringUtils.replace(response, "\n", "");
    }

    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    private void batchInsert(List<EmcIndexCategoryBean> indexCategoryAddList, List<EmcOrderIndexBean> orderIndexAddList, List<EmcIndexSetBean> indexSetAddList,
            List<EmcPropertyDataBean> propertyDataList) {
        if (!indexCategoryAddList.isEmpty())
            getDao().insert(indexCategoryAddList); // 评价标准列表

        if (!orderIndexAddList.isEmpty())
            orderIndexService.getDao().insert(orderIndexAddList); // 样品指标列表

        if (!indexSetAddList.isEmpty()) {
            indexSetService.getDao().insert(indexSetAddList); // 指标设置列表
            propertyDataService.getDao().insert(propertyDataList); // 属性模板配置列
        }
    }

    @Override
    public Page<EmcIndexCategoryBean> selectByProjScheme(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> projSchemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> projSchemeIdList = projSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        // 方案样品
        List<EmcProjOrderBean> projOrderList = projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", projSchemeIdList).filter(MatchPattern.OR));

        // 样品下包含的测试
        List<Long> testIdList = projOrderList.stream().map(e -> e.getTestId()).distinct().collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.selectListByFilter(SearchFilter.instance().match("testId", testIdList).filter(MatchPattern.OR));
        List<Long> indexItemIdList = indexTestItemList.stream().map(e -> e.getIndexItemId()).distinct().collect(Collectors.toList());

        // 只有选择了对应指标项目的指标分类才展示
        if (indexItemIdList.isEmpty()) {
            return new Pagination<>(new ArrayList<>());
        }
        List<EmcIndexSetBean> indexSetList = indexSetService.selectListByFilter(SearchFilter.instance().match("indexItemId", indexItemIdList).filter(MatchPattern.OR));
        if (!indexSetList.isEmpty()) {
            List<Long> indexCategoryIdList = indexSetList.stream().map(e -> e.getIndexCategoryId()).distinct().collect(Collectors.toList());
            return new Pagination<>(selectListByIds(indexCategoryIdList));
        } else {
            return new Pagination<>(new ArrayList<>());
        }
    }

    private void dealMethod(String response, boolean syncFlag, Map<String, Object> map) { // 处理易标准更新数据
        JSONObject responseJson = JSONObject.parseObject(response);
        JSONArray contents;
        if (syncFlag) { // 同步接口
            contents = responseJson.getJSONArray("content"); // 获取content数组
            if (contents != null) {
                contents.forEach(content -> {
                    JSONObject spec = ((JSONObject) content).getJSONObject("industrySpec"); // Spec标准信息
                    JSONArray indicators = ((JSONObject) content).getJSONArray("indicators"); // 产品标准指标项目信息、指标项目限量信息、指标项目检测方法信息
                    Long indexCategoryId = addIndexCategory(spec, map);
                    addIndex(indexCategoryId, indicators, map);
                });
            }
        } else { // 更新接口
            String estandardUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.url"); // 易标准网址
            String accountId = ApplicationContextHelper.getEnvironment().getProperty("estandard.accountid"); // 企业易标准账户ID
            String specallInfosUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.specallinfos.url"); // 标准ID（specid）获取标准所有信息
            String url = new StringBuilder(estandardUrl).append(specallInfosUrl).append("?accountid=").append(accountId).append("&specid=").toString();

            contents = responseJson.getJSONArray("records"); // 获取content数组
            if (contents != null) {
                contents.forEach(content -> {
                    JSONArray newSpecs = ((JSONObject) content).getJSONArray("newSpec"); // Spec更新信息
                    if (newSpecs != null) {
                        addIndexPreview(url, newSpecs, map);
                    }
                });
            }
        }
    }

    // 添加评价标准
    private Long addIndexCategory(JSONObject spec, Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<EmcIndexCategoryBean> indexCategoryList = (List<EmcIndexCategoryBean>) map.get("indexCategoryList");
        @SuppressWarnings("unchecked")
        List<EmcIndexCategoryBean> indexCategoryAddList = (List<EmcIndexCategoryBean>) map.get("indexCategoryAddList");

        String specName = spec.getString("name"); // 标准名称
        String specCodeFront = spec.getString("specCodeFront"); // 根据标准标号截取的
        StringBuilder stdNameSb = new StringBuilder("《").append(specName).append("》");
        if (!StringUtils.isEmpty(specCodeFront))
            stdNameSb.append(specCodeFront);
        String stdName = stdNameSb.toString();

        String abolishDate = spec.getString("abolishdate"); // 废止日期
        LocalDate expireDate;
        if (!StringUtils.isEmpty(abolishDate)) {
            expireDate = DateTimeUtils.parseLocalDate(abolishDate); // 废止日期
        } else {
            expireDate = null;
        }
        List<EmcIndexCategoryBean> indexCategoryFilterList = indexCategoryAddList.parallelStream().filter(e -> StringUtils.equals(stdName, e.getStdName())).collect(Collectors.toList()); // 先查询评价标准添加列表
        if (indexCategoryFilterList.isEmpty())
            indexCategoryFilterList = indexCategoryList.parallelStream().filter(e -> StringUtils.equals(stdName, e.getStdName())).collect(Collectors.toList()); // 再查询评价标准表
        if (indexCategoryFilterList.isEmpty()) { // 没有评价标准，增加评价标准
            // 增加评价标准
            Long indexCategoryId = ApplicationContextHelper.getNextIdentity();
            EmcIndexCategoryBean indexCategory = indexCategory(indexCategoryId, stdName, expireDate);
            indexCategoryAddList.add(indexCategory);
            return indexCategory.getId();
        }
        return indexCategoryFilterList.get(0).getId();
    }

    // 添加样品指标、指标设置
    private void addIndex(Long indexCategoryId, JSONArray indicators, Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<EmcOrderIndexBean> orderIndexList = (List<EmcOrderIndexBean>) map.get("orderIndexList");
        @SuppressWarnings("unchecked")
        List<EmcIndexItemBean> indexItemList = (List<EmcIndexItemBean>) map.get("indexItemList");
        @SuppressWarnings("unchecked")
        List<EmcIndexSetBean> indexSetList = (List<EmcIndexSetBean>) map.get("indexSetList");
        @SuppressWarnings("unchecked")
        List<EmcOrderIndexBean> orderIndexAddList = (List<EmcOrderIndexBean>) map.get("orderIndexAddList");
        @SuppressWarnings("unchecked")
        List<EmcIndexItemBean> indexItemAddList = (List<EmcIndexItemBean>) map.get("indexItemAddList");
        @SuppressWarnings("unchecked")
        List<EmcIndexSetBean> indexSetAddList = (List<EmcIndexSetBean>) map.get("indexSetAddList");
        @SuppressWarnings("unchecked")
        List<EmcPropertyDataBean> propertyDataList = (List<EmcPropertyDataBean>) map.get("propertyDataList");

        indicators.forEach(indicator -> {
            JSONObject specTest = ((JSONObject) indicator).getJSONObject("specTest"); // 指标项目信息
            String specTestName = specTest.getString("specTestName"); // 指标名称
            JSONArray values = ((JSONObject) indicator).getJSONArray("values"); // 指标项目限量信息
            values.forEach(value -> {
                String specProductName = ((JSONObject) value).getString("productName"); // 产品名称，syncFlag区分易标准同步和更新接口字段
                String specLevel = ((JSONObject) value).getString("level"); // 指标等级
                String low = ((JSONObject) value).getString("low"); // 低限
                String high = ((JSONObject) value).getString("high"); // 高限
                String limits = ((JSONObject) value).getString("limits"); // 限值
                String unit = ((JSONObject) value).getString("unit"); // 单位

                // 增加样品指标
                EmcOrderIndexBean orderIndex = new EmcOrderIndexBean();
                orderIndex.setIndexCategoryId(indexCategoryId);
                if (!StringUtils.isEmpty(specProductName))
                    orderIndex.setIndex1(specProductName); // 指标1
                if (!StringUtils.isEmpty(specLevel))
                    orderIndex.setIndex2(specLevel); // 指标2

                List<EmcOrderIndexBean> orderIndexFilterList = orderIndexAddList.parallelStream().filter(
                        e -> indexCategoryId.equals(e.getIndexCategoryId()) && StringUtils.equals(orderIndex.getIndex1(), e.getIndex1()) && StringUtils.equals(orderIndex.getIndex2(), e.getIndex2()))
                        .collect(Collectors.toList()); // 先查询样品指标添加列表
                if (orderIndexFilterList.isEmpty())
                    orderIndexFilterList = orderIndexList.parallelStream().filter(e -> indexCategoryId.equals(e.getIndexCategoryId()) && StringUtils.equals(orderIndex.getIndex1(), e.getIndex1())
                            && StringUtils.equals(orderIndex.getIndex2(), e.getIndex2())).collect(Collectors.toList()); // 再查询样品指标表

                if (orderIndexFilterList.isEmpty()) {
                    orderIndex.setId(ApplicationContextHelper.getNextSequence(orderIndexService.getDao().getTable()));
                    orderIndexAddList.add(orderIndex);
                } else {
                    orderIndex.setId(orderIndexFilterList.get(0).getId());
                }

                EmcIndexSetBean indexSet = new EmcIndexSetBean();
                indexSet.setIndexId(orderIndex.getId());
                indexSet.setIndexCategoryId(indexCategoryId);
                if (!StringUtils.isEmpty(low))
                    indexSet.setIndexLowLimit(StringUtils.replace(StringUtils.replace(low, "≥", ""), ">", "")); // 指标低限
                if (!StringUtils.isEmpty(high))
                    indexSet.setIndexHighLimit(StringUtils.replace(StringUtils.replace(high, "≤", ""), "<", "")); // 指标高限
                if (!StringUtils.isEmpty(limits))
                    indexSet.setTextIndexValue(limits);
                if (!StringUtils.isEmpty(unit))
                    indexSet.setUnit(unit);

                List<EmcIndexItemBean> indexItemFilterList = indexItemAddList.parallelStream().filter(e -> StringUtils.equals(specTestName, e.getIndexItemName())).collect(Collectors.toList()); // 先查询项目添加列表
                if (indexItemFilterList.isEmpty())
                    indexItemFilterList = indexItemList.parallelStream().filter(e -> StringUtils.equals(specTestName, e.getIndexItemName())).collect(Collectors.toList()); // 在查询指标项目表
                if (indexItemFilterList.isEmpty()) {
                    // 新增指标项目
                    EmcIndexItemBean indexItem = new EmcIndexItemBean();
                    indexItem.setIndexItemName(specTestName);
                    indexItem.setId(ApplicationContextHelper.getNextIdentity());
                    indexItemAddList.add(indexItem);

                    // 增加指标设置
                    indexSet.setId(ApplicationContextHelper.getNextIdentity());
                    indexSet.setIndexItemId(indexItem.getId()); // 指标项目
                    indexSetAddList.add(indexSet);
                    propertyDataList.add(propertyData(indexSet.getId(), indexCategoryId));
                } else {
                    indexSet.setIndexItemId(indexItemFilterList.get(0).getId()); // 指标项目

                    List<EmcIndexSetBean> indexSetFilterList = indexSetAddList.parallelStream()
                            .filter(e -> StringUtils.equals(orderIndex.getId(), e.getIndexId()) && indexCategoryId.equals(e.getIndexCategoryId())
                                    && indexSet.getIndexItemId().equals(e.getIndexItemId()) && StringUtils.equals(indexSet.getTextIndexValue(), e.getTextIndexValue()))
                            .collect(Collectors.toList()); // 先查询指标设置添加列表
                    if (indexSetFilterList.isEmpty())
                        indexSetFilterList = indexSetList.parallelStream()
                                .filter(e -> StringUtils.equals(orderIndex.getId(), e.getIndexId()) && indexCategoryId.equals(e.getIndexCategoryId())
                                        && indexSet.getIndexItemId().equals(e.getIndexItemId()) && StringUtils.equals(indexSet.getTextIndexValue(), e.getTextIndexValue()))
                                .collect(Collectors.toList()); // 在查询指标设置表

                    if (indexSetFilterList.isEmpty()) {
                        // 增加指标设置
                        indexSet.setId(ApplicationContextHelper.getNextIdentity());
                        indexSetAddList.add(indexSet);
                        propertyDataList.add(propertyData(indexSet.getId(), indexCategoryId));
                    }
                }
            });
        });
    }

    // 添加指标预览
    private void addIndexPreview(String url, JSONArray newSpecs, Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<EmcIndexItemBean> indexItemList = (List<EmcIndexItemBean>) map.get("indexItemList");
        @SuppressWarnings("unchecked")
        List<EmcIndexPreviewBean> indexPreviewList = (List<EmcIndexPreviewBean>) map.get("indexPreviewList");
        @SuppressWarnings("unchecked")
        List<EmcIndexItemBean> indexItemAddList = (List<EmcIndexItemBean>) map.get("indexItemAddList");
        @SuppressWarnings("unchecked")
        List<EmcIndexPreviewBean> indexPreviewAddList = (List<EmcIndexPreviewBean>) map.get("indexPreviewAddList");

        newSpecs.forEach(spec -> {
            String specName = ((JSONObject) spec).getString("specname"); // 获取标准名称
            String specCodeFront = ((JSONObject) spec).getString("specCodeFront"); // 根据标准标号截取的
            StringBuilder stdNameSb = new StringBuilder("《").append(specName).append("》");
            if (!StringUtils.isEmpty(specCodeFront))
                stdNameSb.append(specCodeFront);
            String stdName = stdNameSb.toString();

            String abolishDate = ((JSONObject) spec).getString("abolishdate"); // 废止日期
            LocalDate expireDate;
            if (!StringUtils.isEmpty(abolishDate)) {
                expireDate = DateTimeUtils.parseLocalDate(abolishDate); // 废止日期
            } else {
                expireDate = null;
            }

            String specId = ((JSONObject) spec).getString("specid");
            String pageUrl = new StringBuilder(url).append(specId).toString();
            String specallInfos = coreRemoteCallRequestService.processSyncRequest("1", pageUrl);
            JSONObject specallInfosJson = JSONObject.parseObject(specallInfos);
            JSONArray indicators = specallInfosJson.getJSONArray("indicators");
            if (indicators != null) {
                indicators.forEach(indicator -> {
                    JSONObject specTest = ((JSONObject) indicator).getJSONObject("specTest"); // 指标项目信息
                    String specTestName = specTest.getString("specTestName"); // 指标名称
                    JSONArray values = ((JSONObject) indicator).getJSONArray("values"); // 指标项目限量信息
                    values.forEach(value -> {
                        String specProductName = ((JSONObject) value).getString("spec_productName"); // 产品名称
                        String specLevel = ((JSONObject) value).getString("level"); // 指标等级
                        String low = ((JSONObject) value).getString("low"); // 低限
                        String high = ((JSONObject) value).getString("high"); // 高限
                        String limits = ((JSONObject) value).getString("limits"); // 限值
                        String unit = ((JSONObject) value).getString("unit"); // 单位

                        EmcIndexPreviewBean indexPreview = new EmcIndexPreviewBean();
                        indexPreview.setStdName(stdName);
                        indexPreview.setExpireDate(expireDate);
                        if (!StringUtils.isEmpty(specProductName))
                            indexPreview.setIndex1(specProductName); // 指标1
                        if (!StringUtils.isEmpty(specLevel))
                            indexPreview.setIndex2(specLevel); // 指标2
                        if (!StringUtils.isEmpty(low))
                            indexPreview.setIndexLowLimit(StringUtils.replace(StringUtils.replace(low, "≥", ""), ">", "")); // 指标低限
                        if (!StringUtils.isEmpty(high))
                            indexPreview.setIndexHighLimit(StringUtils.replace(StringUtils.replace(high, "≤", ""), "<", "")); // 指标高限
                        if (!StringUtils.isEmpty(limits))
                            indexPreview.setTextIndexValue(limits);
                        if (!StringUtils.isEmpty(unit))
                            indexPreview.setUnit(unit);

                        List<EmcIndexItemBean> indexItemFilterList = indexItemAddList.parallelStream().filter(e -> StringUtils.equals(specTestName, e.getIndexItemName())).collect(Collectors.toList()); // 先查询项目添加列表
                        if (indexItemFilterList.isEmpty())
                            indexItemFilterList = indexItemList.parallelStream().filter(e -> StringUtils.equals(specTestName, e.getIndexItemName())).collect(Collectors.toList()); // 在查询指标项目表
                        if (indexItemFilterList.isEmpty()) {
                            // 新增指标项目
                            EmcIndexItemBean indexItem = new EmcIndexItemBean();
                            indexItem.setIndexItemName(specTestName);
                            indexItem.setId(ApplicationContextHelper.getNextIdentity());
                            indexItemAddList.add(indexItem);

                            // 增加指标预览
                            indexPreview.setId(ApplicationContextHelper.getNextIdentity());
                            indexPreview.setIndexItemId(indexItem.getId()); // 指标项目
                            indexPreviewAddList.add(indexPreview);
                        } else {
                            indexPreview.setIndexItemId(indexItemFilterList.get(0).getId()); // 指标项目

                            List<EmcIndexPreviewBean> indexPreviewFilterList = indexPreviewAddList.parallelStream()
                                    .filter(e -> StringUtils.equals(indexPreview.getStdName(), e.getStdName()) && StringUtils.equals(indexPreview.getIndex1(), e.getIndex1())
                                            && StringUtils.equals(indexPreview.getIndex2(), e.getIndex2()) && indexPreview.getIndexItemId().equals(e.getIndexItemId())
                                            && StringUtils.equals(indexPreview.getTextIndexValue(), e.getTextIndexValue()))
                                    .collect(Collectors.toList());
                            if (indexPreviewFilterList.isEmpty())
                                indexPreviewFilterList = indexPreviewList.parallelStream()
                                        .filter(e -> StringUtils.equals(indexPreview.getStdName(), e.getStdName()) && StringUtils.equals(indexPreview.getIndex1(), e.getIndex1())
                                                && StringUtils.equals(indexPreview.getIndex2(), e.getIndex2()) && indexPreview.getIndexItemId().equals(e.getIndexItemId())
                                                && StringUtils.equals(indexPreview.getTextIndexValue(), e.getTextIndexValue()))
                                        .collect(Collectors.toList());

                            if (indexPreviewFilterList.isEmpty()) {
                                // 增加指标设置
                                indexPreview.setId(ApplicationContextHelper.getNextIdentity());
                                indexPreviewAddList.add(indexPreview);
                            }
                        }
                    });
                });
            }
        });
    }

    // 评价标准
    private EmcIndexCategoryBean indexCategory(Long id, String stdName, LocalDate expireDate) {
        EmcIndexCategoryBean indexCategory = new EmcIndexCategoryBean();
        indexCategory.setId(id);
        indexCategory.setStdName(stdName);
        indexCategory.setExpireDate(expireDate);
        return indexCategory;
    }

    // 样品指标
    private EmcOrderIndexBean orderIndex(String id, Long indexCategoryId, String index1, String index2) {
        EmcOrderIndexBean orderIndex = new EmcOrderIndexBean();
        orderIndex.setId(id);
        orderIndex.setIndexCategoryId(indexCategoryId);
        orderIndex.setIndex1(index1);
        orderIndex.setIndex2(index2);
        return orderIndex;
    }

    // 指标设置
    private EmcIndexSetBean indexSet(Long id, Long indexCategoryId, String indexId, Long indexItemId, String indexLowLimit, String indexHighLimit, String textIndexValue, String unit) {
        EmcIndexSetBean indexSet = new EmcIndexSetBean();
        indexSet.setId(id);
        indexSet.setIndexCategoryId(indexCategoryId);
        indexSet.setIndexId(indexId);
        indexSet.setIndexItemId(indexItemId);
        indexSet.setIndexLowLimit(indexLowLimit);
        indexSet.setIndexHighLimit(indexHighLimit);
        indexSet.setTextIndexValue(textIndexValue);
        indexSet.setUnit(unit);
        return indexSet;
    }

    // 属性配置
    private EmcPropertyDataBean propertyData(Long bizId, Long tmplId) {
        EmcPropertyDataBean propertyData = new EmcPropertyDataBean();
        propertyData.setId(ApplicationContextHelper.getNextIdentity());
        propertyData.setBizId(bizId);
        propertyData.setTmplId(tmplId);
        propertyData.setTableName(indexSetService.getDao().getTable());
        return propertyData;
    }

    private void exectorsExec(ExecutorService executors, int threads, boolean syncFlag, int dealNum, String url, Map<String, Object> map) {
        int avgNum = dealNum / (threads - 1); // 线程查询分页平均数
        int remainder = dealNum % (threads - 1); // 剩余分页数
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(threads * 2); // 创建大小为2threads的队列
        int lastEnd = 0; // 上一线程处理分页结束页
        int urlThreads = threads - 1; // 处理http线程数
        for (int i = 1; i < threads; i++) {
            int start = i == 1 ? 1 : lastEnd + 1; // 线程查询起始页
            int end = 0;
            if (remainder != 0) {
                end = avgNum * i + i; // 分配剩余分页数
                remainder--; // 减少剩余分页数
            } else {
                end = lastEnd + avgNum;
            }
            end = (i == urlThreads ? dealNum : end); // 线程查询结束页，最后一页页数是dealNum（第一页从0开始）
            lastEnd = end;
            EstandardDataProducer dataProducer = new EstandardDataProducer(start, end, url, queue);
            executors.execute(dataProducer);
        }
        EstandDataConsumer dataConsumer = new EstandDataConsumer(dealNum, queue, syncFlag, map); // 单线程处理数据
        executors.execute(dataConsumer);
        executors.shutdown(); // 任务执行结束后，关闭线程池
    }

    // HTTP查询数据时间较长，提前获取数据缓存到队列里
    private class EstandardDataProducer implements Runnable {
        int start;
        int num;
        String url;
        BlockingQueue<String> queue;

        public EstandardDataProducer(int start, int num, String url, BlockingQueue<String> queue) {
            this.start = start;
            this.num = num;
            this.url = url;
            this.queue = queue;
        }

        @Override
        public void run() {
            if (num != 0) {
                try {
                    for (; start <= num; start++) {
                        String pageUrl = new StringBuilder(url).append(start).toString();
                        String response = coreRemoteCallRequestService.processSyncRequest("1", pageUrl); // 易标准返回内容
                        queue.put(response);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class EstandDataConsumer implements Runnable {
        int totalPageNum; // 总分页数量
        BlockingQueue<String> queue;
        boolean syncFlag = true;
        Map<String, Object> map;

        public EstandDataConsumer(int totalPageNum, BlockingQueue<String> queue, boolean syncFlag, Map<String, Object> map) {
            this.totalPageNum = totalPageNum;
            this.queue = queue;
            this.syncFlag = syncFlag;
            this.map = map;
        }

        @Override
        public void run() {
            int count = 0; // 处理分页数量
            try {
                while (count < totalPageNum) {
                    String response = queue.take();
                    dealMethod(response, syncFlag, map); // 处理同步易标准返回的数据
                    count++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void estandSync(RestJsonWrapperBean wrapper) {
        List<EmcIndexCategoryBean> chooseIndexCategoryList = wrapper.parse(EmcIndexCategoryBean.class);
        List<Long> idList = chooseIndexCategoryList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcIndexCategoryBean> filterList = chooseIndexCategoryList.stream().filter(e -> !e.getStdName().contains("》") || e.getStdName().indexOf("》") == (e.getStdName().length() - 1))
                .collect(Collectors.toList());
        if (!filterList.isEmpty()) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.CHOOSE_ESTANDARD");
        }
        List<String> codeList = chooseIndexCategoryList.stream().map(e -> e.getStdName().split("》")[1]).collect(Collectors.toList());

        String specidorCodes = StringUtils.join(codeList, ",");
        String estandardUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.url"); // 易标准网址
        String accountId = ApplicationContextHelper.getEnvironment().getProperty("estandard.accountid"); // 企业易标准账户ID
        String testAbilityUrl = ApplicationContextHelper.getEnvironment().getProperty("estandard.testability.url"); // 易标准获取检测能力数据接口

        String syncUrl;
        try {
            syncUrl = new StringBuilder(estandardUrl).append(testAbilityUrl).append("?querytype=2&accountid=").append(accountId).append("&specidorcodes=")
                    .append(URLEncoder.encode(specidorCodes, "GBK")).toString();
        } catch (UnsupportedEncodingException e1) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_FAIL");
        }
        String response = coreRemoteCallRequestService.processSyncRequest("1", syncUrl);
        JSONObject jsonEstandard = JSONArray.parseObject(response);
        String totalPages = jsonEstandard.getString("totalPages");
        if (totalPages == null) {
            throw new CheckedException("EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.SYNC_FAIL");
        }

        List<EmcOrderIndexBean> orderIndexList = orderIndexService.getDao().selectListByOneColumnValues(idList, "INDEXCATEGORYID"); // 样品指标列表
        List<EmcIndexSetBean> indexSetList = indexSetService.getDao().selectListByOneColumnValues(idList, "INDEXCATEGORYID"); // 指标设置列表
        List<EmcIndexItemBean> indexItemList = indexItemService.getDao().selectAll("ID", "INDEXITEMNAME"); // 指标项目列表

        List<EmcOrderIndexBean> orderIndexAddList = new ArrayList<>();
        List<EmcIndexItemBean> indexItemAddList = new ArrayList<>();
        List<EmcIndexSetBean> indexSetAddList = new ArrayList<>();
        List<EmcPropertyDataBean> propertyDataList = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("indexCategoryList", chooseIndexCategoryList);
        map.put("orderIndexList", orderIndexList);
        map.put("indexItemList", indexItemList);
        map.put("indexSetList", indexSetList);
        map.put("orderIndexAddList", orderIndexAddList);
        map.put("indexItemAddList", indexItemAddList);
        map.put("indexSetAddList", indexSetAddList);
        map.put("propertyDataList", propertyDataList);

        dealWithData(response, map);
        if (!indexItemAddList.isEmpty()) {
            indexItemService.getDao().insert(indexItemAddList); // 指标项目列表
        }
        batchInsert(new ArrayList<>(), orderIndexAddList, indexSetAddList, propertyDataList);
        getDao().update((List<EmcIndexCategoryBean>) map.get("indexCategoryList"), "EXPIREDATE", "ESTANDARD");
    }

    @Override
    public Page<EmcIndexCategoryBean> selectByOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "orderId", Arrays.asList("id", "testId"));
        List<Long> testIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getTestId).distinct().collect(Collectors.toList());

        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.selectListByFilter(SearchFilter.instance().match("testId", testIdList).filter(MatchPattern.OR));
        List<Long> indexItemIdList = indexTestItemList.stream().map(EmcIndexTestItemBean::getIndexItemId).distinct().collect(Collectors.toList());

        // 只有选择了对应指标项目的指标分类才展示
        if (indexItemIdList.isEmpty()) {
            return new Pagination<>(new ArrayList<>());
        }

        List<EmcIndexSetBean> indexSetList = indexSetService.selectListByFilter(SearchFilter.instance().match("indexItemId", indexItemIdList).filter(MatchPattern.OR));
        if (!indexSetList.isEmpty()) {
            List<Long> indexCategoryIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexCategoryId).distinct().collect(Collectors.toList());
            return new Pagination<>(selectListByIds(indexCategoryIdList));
        }

        return new Pagination<>(new ArrayList<>());
    }

    @Override
    @Transactional
    public void copy(RestJsonWrapperBean wrapper) {
        String name = wrapper.getParamValue("name");
        if (StringUtils.isBlank(name)) {
            return;
        }
        List<EmcIndexCategoryBean> indexCategoryBeanList = wrapper.parse(EmcIndexCategoryBean.class);
        List<Long> indexCategoryIdList = indexCategoryBeanList.stream().map(EmcIndexCategoryBean::getId).distinct().collect(Collectors.toList());
        List<EmcIndexCategoryBean> oldIndexCategoryList = selectListByFilter(SearchFilter.instance().match("Id", indexCategoryIdList).filter(MatchPattern.OR));
        List<EmcOrderIndexBean> oldOrderIndexList = orderIndexService.selectListByFilter(SearchFilter.instance().match("indexCategoryId", indexCategoryIdList).filter(MatchPattern.OR));
        List<String> oldOrderIndexIdList = oldOrderIndexList.stream().map(EmcOrderIndexBean::getId).distinct().collect(Collectors.toList());
        List<EmcIndexSetBean> oldIndexSetList = indexSetService.selectListByFilter(SearchFilter.instance().match("indexId", oldOrderIndexIdList).filter(MatchPattern.OR));
        List<EmcIndexCategoryBean> newIndexCategoryList = new ArrayList<>();
        List<EmcOrderIndexBean> newOrderIndexList = new ArrayList<>();
        List<EmcIndexSetBean> newIndexSetList = new ArrayList<>();
        for (EmcIndexCategoryBean ic : oldIndexCategoryList) {
            EmcIndexCategoryBean newIndexCategory = new EmcIndexCategoryBean();
            BeanUtils.copyProperties(ic, newIndexCategory);
            Long icId = ApplicationContextHelper.getNextIdentity();
            newIndexCategory.setId(icId);
            newIndexCategory.setStdName(name);
            newIndexCategoryList.add(newIndexCategory);
            List<EmcOrderIndexBean> curOrderIndexList = oldOrderIndexList.stream().filter(e -> ObjectUtils.equals(ic.getId(), e.getIndexCategoryId())).collect(Collectors.toList());
            for (EmcOrderIndexBean oi : curOrderIndexList) {
                EmcOrderIndexBean newOrderIndex = new EmcOrderIndexBean();
                BeanUtils.copyProperties(oi, newOrderIndex);
                String oiId = ApplicationContextHelper.getNextSequence(orderIndexService.getDao().getTable());
                newOrderIndex.setId(oiId);
                newOrderIndex.setIndexCategoryId(icId);
                newOrderIndexList.add(newOrderIndex);
                List<EmcIndexSetBean> curIndexSetList = oldIndexSetList.stream().filter(e -> ObjectUtils.equals(oi.getId(), e.getIndexId())).collect(Collectors.toList());
                for (EmcIndexSetBean is : curIndexSetList) {
                    EmcIndexSetBean newIndexSet = new EmcIndexSetBean();
                    BeanUtils.copyProperties(is, newIndexSet);
                    Long isId = ApplicationContextHelper.getNextIdentity();
                    newIndexSet.setId(isId);
                    newIndexSet.setIndexId(oiId);
                    newIndexSet.setIndexCategoryId(icId);
                    newIndexSetList.add(newIndexSet);
                }
            }
        }
        getDao().insert(newIndexCategoryList);
        orderIndexService.getDao().insert(newOrderIndexList);
        indexSetService.getDao().insert(newIndexSetList);
    }

    @SuppressWarnings("unchecked")
    private void dealWithData(String response, Map<String, Object> map) { // 处理易标准更新数据
        JSONObject responseJson = JSONObject.parseObject(response);
        JSONArray contents = responseJson.getJSONArray("content"); // 获取content数组
        if (contents != null) {
            contents.forEach(content -> {
                JSONObject spec = ((JSONObject) content).getJSONObject("spec"); // Spec标准信息
                JSONArray indicators = ((JSONObject) content).getJSONArray("indicators"); // 产品标准指标项目信息、指标项目限量信息、指标项目检测方法信息
                List<EmcIndexCategoryBean> indexCategoryList = (List<EmcIndexCategoryBean>) map.get("indexCategoryList");

                String specName = spec.getString("name"); // 标准名称
                String specCodeFront = spec.getString("code"); // 根据标准标号截取的
                StringBuilder stdNameSb = new StringBuilder("《").append(specName).append("》");
                if (!StringUtils.isEmpty(specCodeFront))
                    stdNameSb.append(specCodeFront);
                String stdName = stdNameSb.toString();

                String abolishDate = spec.getString("abolishdate"); // 废止日期
                LocalDate expireDate;
                if (!StringUtils.isEmpty(abolishDate)) {
                    expireDate = DateTimeUtils.parseLocalDate(abolishDate); // 废止日期
                } else {
                    expireDate = null;
                }
                indexCategoryList.stream().forEach(e -> {
                    if (stdName.equals(e.getStdName())) {
                        e.setEStandard("1");
                        e.setExpireDate(expireDate == null ? e.getExpireDate() : expireDate);

                        List<EmcOrderIndexBean> orderIndexList = (List<EmcOrderIndexBean>) map.get("orderIndexList");
                        List<EmcIndexItemBean> indexItemList = (List<EmcIndexItemBean>) map.get("indexItemList");
                        List<EmcIndexSetBean> indexSetList = (List<EmcIndexSetBean>) map.get("indexSetList");
                        List<EmcOrderIndexBean> orderIndexAddList = (List<EmcOrderIndexBean>) map.get("orderIndexAddList");
                        List<EmcIndexItemBean> indexItemAddList = (List<EmcIndexItemBean>) map.get("indexItemAddList");
                        List<EmcIndexSetBean> indexSetAddList = (List<EmcIndexSetBean>) map.get("indexSetAddList");
                        List<EmcPropertyDataBean> propertyDataList = (List<EmcPropertyDataBean>) map.get("propertyDataList");

                        indicators.forEach(indicator -> {
                            JSONObject specTest = ((JSONObject) indicator).getJSONObject("specTest"); // 指标项目信息
                            String specTestName = ((JSONObject) specTest).getString("name");
                            JSONArray values = ((JSONObject) indicator).getJSONArray("values"); // 指标项目限量信息
                            values.forEach(value -> {
                                String specProductName = ((JSONObject) value).getString("productName"); // 产品名称，syncFlag区分易标准同步和更新接口字段
                                String specLevel = ((JSONObject) value).getString("level"); // 指标等级
                                String low = ((JSONObject) value).getString("low"); // 低限
                                String high = ((JSONObject) value).getString("high"); // 高限
                                String limits = ((JSONObject) value).getString("limits"); // 限值
                                String unit = ((JSONObject) value).getString("unit"); // 单位

                                // 增加样品指标
                                EmcOrderIndexBean orderIndex = new EmcOrderIndexBean();
                                orderIndex.setIndexName(specProductName);
                                orderIndex.setIndexCategoryId(e.getId());
                                if (!StringUtils.isEmpty(specProductName))
                                    orderIndex.setIndex1(specProductName); // 指标1
                                if (!StringUtils.isEmpty(specLevel))
                                    orderIndex.setIndex2(specLevel); // 指标2

                                List<EmcOrderIndexBean> orderIndexFilterList = orderIndexAddList.parallelStream().filter(m -> e.getId().equals(m.getIndexCategoryId())
                                        && StringUtils.equals(orderIndex.getIndex1(), m.getIndex1()) && StringUtils.equals(orderIndex.getIndex2(), m.getIndex2())).collect(Collectors.toList()); // 先查询样品指标添加列表
                                if (orderIndexFilterList.isEmpty())
                                    orderIndexFilterList = orderIndexList.parallelStream().filter(m -> e.getId().equals(m.getIndexCategoryId())
                                            && StringUtils.equals(orderIndex.getIndex1(), m.getIndex1()) && StringUtils.equals(orderIndex.getIndex2(), m.getIndex2())).collect(Collectors.toList()); // 再查询样品指标表

                                if (orderIndexFilterList.isEmpty()) {
                                    orderIndex.setId(ApplicationContextHelper.getNextSequence(orderIndexService.getDao().getTable()));
                                    orderIndexAddList.add(orderIndex);
                                } else {
                                    orderIndex.setId(orderIndexFilterList.get(0).getId());
                                }

                                EmcIndexSetBean indexSet = new EmcIndexSetBean();
                                indexSet.setIndexId(orderIndex.getId());
                                indexSet.setIndexCategoryId(e.getId());
                                indexSet.setEvalIndex(specTestName);
                                if (!StringUtils.isEmpty(low))
                                    indexSet.setIndexLowLimit(StringUtils.replace(StringUtils.replace(low, "≥", ""), ">", "")); // 指标低限
                                if (!StringUtils.isEmpty(high))
                                    indexSet.setIndexHighLimit(StringUtils.replace(StringUtils.replace(high, "≤", ""), "<", "")); // 指标高限
                                if (!StringUtils.isEmpty(limits))
                                    indexSet.setTextIndexValue(limits);
                                if (!StringUtils.isEmpty(unit))
                                    indexSet.setUnit(unit);

                                List<EmcIndexItemBean> indexItemFilterList = indexItemAddList.parallelStream().filter(m -> StringUtils.equals(specTestName, m.getIndexItemName()))
                                        .collect(Collectors.toList()); // 先查询项目添加列表
                                if (indexItemFilterList.isEmpty())
                                    indexItemFilterList = indexItemList.parallelStream().filter(m -> StringUtils.equals(specTestName, m.getIndexItemName())).collect(Collectors.toList()); // 在查询指标项目表
                                if (indexItemFilterList.isEmpty()) {
                                    // 新增指标项目
                                    EmcIndexItemBean indexItem = new EmcIndexItemBean();
                                    indexItem.setIndexItemName(specTestName);
                                    indexItem.setId(ApplicationContextHelper.getNextIdentity());
                                    indexItemAddList.add(indexItem);

                                    // 增加指标设置
                                    indexSet.setId(ApplicationContextHelper.getNextIdentity());
                                    indexSet.setIndexItemId(indexItem.getId()); // 指标项目
                                    indexSetAddList.add(indexSet);
                                    propertyDataList.add(propertyData(indexSet.getId(), e.getId()));
                                } else {
                                    indexSet.setIndexItemId(indexItemFilterList.get(0).getId()); // 指标项目

                                    List<EmcIndexSetBean> indexSetFilterList = indexSetAddList.parallelStream()
                                            .filter(m -> StringUtils.equals(orderIndex.getId(), m.getIndexId()) && e.getId().equals(m.getIndexCategoryId())
                                                    && indexSet.getIndexItemId().equals(m.getIndexItemId()) && StringUtils.equals(indexSet.getTextIndexValue(), m.getTextIndexValue()))
                                            .collect(Collectors.toList()); // 先查询指标设置添加列表
                                    if (indexSetFilterList.isEmpty())
                                        indexSetFilterList = indexSetList.parallelStream()
                                                .filter(m -> StringUtils.equals(orderIndex.getId(), m.getIndexId()) && e.getId().equals(m.getIndexCategoryId())
                                                        && indexSet.getIndexItemId().equals(m.getIndexItemId()) && StringUtils.equals(indexSet.getTextIndexValue(), m.getTextIndexValue()))
                                                .collect(Collectors.toList()); // 在查询指标设置表

                                    if (indexSetFilterList.isEmpty()) {
                                        // 增加指标设置
                                        indexSet.setId(ApplicationContextHelper.getNextIdentity());
                                        indexSetAddList.add(indexSet);
                                        propertyDataList.add(propertyData(indexSet.getId(), e.getId()));
                                    }
                                }
                            });
                        });
                    }
                });
            });
        }
    }
}
