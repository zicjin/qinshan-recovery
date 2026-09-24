package com.sunwayworld.emc.module.mdm.bizmdm.test.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.resource.PfTestMethodResource;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.PfTestMethodService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
@LogModule("测试方法")
@RestController
@GikamBean
public class PfTestMethodResourceImpl extends EmcTestMethodResourceImpl implements PfTestMethodResource {

    @Autowired
    @Lazy
    private EmcTestService testService;

    @Autowired
    private PfTestMethodService testMethodService;

    @Autowired
    private EmcItemQcIndexService itemQcIndexService;

    @Override
    @Log(value = "验证测试名称唯一性", type = LogType.VALIDATE)
    public RestValidationResultBean validateTestUnique(RestJsonWrapperBean wrapper) {
        return testService.validateUnique(wrapper);
    }

    @Override
    @Log(value = "通过测试方法查询默认QC", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/default-qcs/queries", method = RequestMethod.POST)
    public Page<EmcTestMethodDefaultQcBean> selectDefaultQcPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return testMethodService.selectDefaultQcPagination(id, wrapper);
    }

    @Override
    @Log(value = "新增默认QC", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/default-qcs", method = RequestMethod.POST)
    public void insertDefaultQc(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodService.insertDefaultQc(id, wrapper);
    }

    @Override
    @Log(value = "删除默认QC", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/default-qcs", method = RequestMethod.DELETE)
    public void deleteDefaultQc(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodService.deleteDefaultQc(id, wrapper);
    }

    @Override
    @Log(value = "通过测试方法查询关联测试", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/related-test-methods/queries", method = RequestMethod.POST)
    public Page<EmcRelatedTestMethodBean> selectRelatedTestMethodPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return testMethodService.selectRelatedTestMethodPagination(id, wrapper);
    }

    @Override
    @Log(value = "新增关联测试", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/related-test-method", method = RequestMethod.POST)
    public void insertRelatedTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodService.insertRelatedTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "删除关联测试", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/related-test-method", method = RequestMethod.DELETE)
    public void deleteRelatedTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodService.deleteRelatedTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "批量新增关联测试", type = LogType.INSERT)
    @RequestMapping(value = "/action/batch-add-related-test-method", method = RequestMethod.POST)
    public void batchInsertRelatedTestMethod(RestJsonWrapperBean wrapper) {
        testMethodService.batchInsertRelatedTestMethod(wrapper);
    }

    @Override
    @Log(value = "复制测试方法", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy-by-category", method = RequestMethod.POST)
    public void copyByCategory(RestJsonWrapperBean jsonWrapper) {
        testMethodService.copyByCategory(jsonWrapper);
    }

    @Override
    @Log(value = "复制默认QC到", type = LogType.INSERT)
    @RequestMapping(value = "/action/default-qc-copy-to", method = RequestMethod.POST)
    public void copyDefaultQcTo(RestJsonWrapperBean wrapper) {
        testMethodService.copyDefaultQcTo(wrapper);
    }

    @Override
    @Log(value = "同步修改TESTID", type = LogType.INSERT)
    @RequestMapping(value = "/action/sync-test-id", method = RequestMethod.POST)
    public void syncTestId(RestJsonWrapperBean wrapper) {
        testMethodService.syncTestId(wrapper);
    }

    @Override
    @Log(value = "前处理复制到其它测试", type = LogType.INSERT)
    @RequestMapping(value = "/before-handle/action/copy-to", method = RequestMethod.POST)
    public void beforeHandleCopyTo(RestJsonWrapperBean wrapper) {
        testMethodService.beforeHandleCopyTo(wrapper);
    }

    @Override
    @Log(value = "公式附件复制到其它测试", type = LogType.INSERT)
    @RequestMapping(value = "/script-file/action/copy-to", method = RequestMethod.POST)
    public void scriptFileCopyTo(RestJsonWrapperBean wrapper) {
        testMethodService.scriptFileCopyTo(wrapper);
    }

    @Log(value = "批量删除QC指标", type = LogType.DELETE)
    @RequestMapping(value = "/items/action/qc-index", method = RequestMethod.DELETE)
    public Long delQcIndex(RestJsonWrapperBean wrapper) {
        testMethodService.delQcIndex(wrapper);
        return 1L;
    }

    @Log(value = "批量删除分段修约", type = LogType.DELETE)
    @RequestMapping(value = "/items/action/revision", method = RequestMethod.DELETE)
    public Long delRevision(RestJsonWrapperBean wrapper) {
        testMethodService.delRevision(wrapper);
        return 1L;
    }

    @Log(value = "查询QC指标", type = LogType.SELECT)
    @RequestMapping(value = "/items/qc-indexes/queries", method = RequestMethod.POST)
    public Page<EmcItemQcIndexBean> selectQcIndexPagation(RestJsonWrapperBean wrapper) {
        return itemQcIndexService.selectPaginationByFilter(SearchFilter.instance(), wrapper);
    }
}
