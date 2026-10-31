package com.sunwayworld.emc.module.mdm.bizmdm.test.resource.impl;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.*;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.test.resource.EmcTestMethodResource;
import com.sunwayworld.emc.module.mdm.bizmdm.test.validator.EmcItemGroupDeleteValidator;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@LogModule("测试方法")
@RestController
@GikamBean
public class EmcTestMethodResourceImpl implements EmcTestMethodResource {
    @Autowired
    private EmcTestMethodService testMethodService;
    
    @Autowired
    @Lazy
    private EmcTestService testService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcItemQcIndexService itemQcIndexService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcItemResultService itemResultService;

    @Autowired
    private EmcTestmethodBeforehandleService testmethodBeforehandleService;

    @Autowired
    private EmcTestMethodEquiptService testMethodEquiptService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;
    
    @Autowired
    private EmcQcItemService emcQcItemService;

    @Override
    public EmcTestMethodService getService() {
        return testMethodService;
    }

    @Override
    @Log(value = "查询测试方法分析项详情", type = LogType.SELECT)
    @RequestMapping(value = "/items/{itemId}", method = RequestMethod.GET)
    public EmcItemBean selectItemDetail(@PathVariable Long itemId) {
        return itemService.selectDetail(itemId);
    }

    @Log(value = "新增测试分析项", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/items", method = RequestMethod.POST)
    public Long insertItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return itemService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除测试分析项", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/items", method = RequestMethod.DELETE)
    public void deleteItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        itemService.delete(wrapper);
    }

    @Log(value = "通过测试方法查询分析项", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/items/queries", method = RequestMethod.POST)
    public Page<EmcItemBean> selectItemPagationByTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return itemService.selectPaginationByFilter(SearchFilter.instance().match("TESTMETHODID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "新增分析项组", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/item-groups", method = RequestMethod.POST)
    public Long insertItemGroup(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return itemGroupService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除分析项组", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/item-groups", method = RequestMethod.DELETE)
    @ValidateDataWith(EmcItemGroupDeleteValidator.class)
    public void deleteItemGroup(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        itemGroupService.delete(wrapper);
    }

    @Log(value = "通过测试方法查询分析项组", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/item-groups/queries", method = RequestMethod.POST)
    public Page<EmcItemGroupBean> selectItemGroupPagationByTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return itemGroupService.selectPaginationByFilter(SearchFilter.instance().match("TESTMETHODID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "新增测试关联仪器", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/equipts", method = RequestMethod.POST)
    public void insertEquipt(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodEquiptService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除测试关联仪器", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/equipts", method = RequestMethod.DELETE)
    public void deleteEquipt(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodEquiptService.deleteEquipt(wrapper);
    }

    @Log(value = "设置默认关联仪器", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/equipts/action/set-default", method = RequestMethod.PUT)
    public void setDefaultEquipt(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodEquiptService.setDefault(id, wrapper);
    }

    @Log(value = "通过测试方法查询关联仪器", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/equipts/queries", method = RequestMethod.POST)
    public Page<EmcEquiptTestMethodBean> selectEquiptPagationByTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return testMethodEquiptService.selectEquiptPagationByTestMethod(id, wrapper);
    }

    @Log(value = "新增QC指标", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/items/{itemId}/qc-indexes", method = RequestMethod.POST)
    public Long insertItemQcIndex(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        return itemQcIndexService.insert(itemId, wrapper);
    }

    @Override
    @Log(value = "删除QC指标", type = LogType.DELETE)
    @RequestMapping(value = "/items/{itemId}/qc-indexes", method = RequestMethod.DELETE)
    public void deleteItemQcIndex(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        itemQcIndexService.delete(wrapper);
    }

    @Log(value = "通过测试分析项查询QC指标", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/items/{itemId}/qc-indexes/queries", method = RequestMethod.POST)
    public Page<EmcItemQcIndexBean> selectQcIndexPagationByItem(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        return itemQcIndexService.selectPaginationByFilter(SearchFilter.instance().match("ITEMID", itemId).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "新增分段修约", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/items/{itemId}/revisions", method = RequestMethod.POST)
    public Long insertItemRevision(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        return itemRevisionService.insert(itemId, wrapper);
    }

    @Override
    @Log(value = "删除分段修约", type = LogType.DELETE)
    @RequestMapping(value = "/items/{itemId}/revisions", method = RequestMethod.DELETE)
    public void deleteItemRevision(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        itemRevisionService.delete(wrapper);
    }

    @Log(value = "通过测试分析项查询分段修约", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/items/{itemId}/revisions/queries", method = RequestMethod.POST)
    public Page<EmcItemRevisionBean> selectRevisionPagationByItem(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        return itemRevisionService.selectPaginationByFilter(SearchFilter.instance().match("ITEMID", itemId).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "新增测试样品类型", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/order-categorys", method = RequestMethod.POST)
    public Long insertOrderCategory(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return testMethodOrderCategoryService.insert(id, wrapper);
    }

    @Override
    @Log(value = "删除测试样品类型", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/order-categorys", method = RequestMethod.DELETE)
    public void deleteOrderCategory(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        testMethodOrderCategoryService.delete(wrapper);
    }

    @Log(value = "通过测试方法查询测试样品类型", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/order-categorys/queries", method = RequestMethod.POST)
    public Page<EmcTestMethodOrderCategoryBean> selectOrderCategoryPagationByTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return testMethodOrderCategoryService.selectPaginationByFilter(SearchFilter.instance().match("TESTMETHODID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "分析项查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/items/queries", method = RequestMethod.POST)
    public Page<EmcItemBean> selectItemPagination(RestJsonWrapperBean wrapper) {
        return itemService.selectPagination(wrapper);
    }

    @Log(value = "分析项选择页查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/items/queries/choosable", method = RequestMethod.POST)
    public Page<EmcItemBean> selectItemChoosablePagination(RestJsonWrapperBean wrapper) {
        return itemService.selectChoosablePagination(wrapper);
    }

    @Log(value = "分析项组选择页查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/item-groups/queries/choosable", method = RequestMethod.POST)
    public Page<EmcItemGroupBean> selectItemGroupChoosablePagination(RestJsonWrapperBean wrapper) {
        return itemGroupService.selectChoosablePagination(wrapper);
    }

    @Log(value = "组分析项选择页查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/group-items/queries/choosable", method = RequestMethod.POST)
    public Page<EmcGroupItemBean> selectGroupItemChoosablePagination(RestJsonWrapperBean wrapper) {
        return testMethodService.selectGroupItemChoosablePagination(wrapper);
    }

    @Log(value = "分析项结果选择页查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/item-results/queries/choosable", method = RequestMethod.POST)
    public Page<EmcItemResultBean> selectItemResultChoosablePagination(RestJsonWrapperBean wrapper) {
        return itemResultService.selectPagination(wrapper);
    }

    @Log(value = "新增分析项组分析项", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/item-groups/{itemGroupId}/group-items", method = RequestMethod.POST)
    public void insertGroupItem(@PathVariable Long itemGroupId, RestJsonWrapperBean wrapper) {
        groupItemService.insert(itemGroupId, wrapper);
    }

    @Override
    @Log(value = "删除分析项组分析项", type = LogType.DELETE)
    @RequestMapping(value = "/item-groups/{itemGroupId}/group-items", method = RequestMethod.DELETE)
    public void deleteGroupItem(@PathVariable Long itemGroupId, RestJsonWrapperBean wrapper) {
        groupItemService.delete(wrapper);
    }

    @Log(value = "通过分析项组查询分析项组分析项", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/item-groups/{itemGroupId}/group-items/queries", method = RequestMethod.POST)
    public Page<EmcGroupItemBean> selectGroupItemPagationByItemGroup(@PathVariable Long itemGroupId, RestJsonWrapperBean wrapper) {
        return groupItemService.selectPaginationByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupId).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "分析项启用", type = LogType.ACTIVATE)
    @RequestMapping(value = "/items/action/activate", method = RequestMethod.PUT)
    public void activateItems(RestJsonWrapperBean wrapper) {
        itemService.activate(wrapper);
    }

    @Log(value = "分析项停用", type = LogType.ACTIVATE)
    @RequestMapping(value = "/items/action/deactivate", method = RequestMethod.PUT)
    public void deactivateItems(RestJsonWrapperBean wrapper) {
        itemService.deactivate(wrapper);
    }

    @Override
    @Log(value = "复制分析项", type = LogType.INSERT)
    @RequestMapping(value = "/items/action/copy", method = RequestMethod.POST)
    public void copyItem(RestJsonWrapperBean jsonWrapper) {
        itemService.copy(jsonWrapper);
    }

    @Override
    @Log(value = "复制到分析项", type = LogType.INSERT)
    @RequestMapping(value = "/items/action/copyto", method = RequestMethod.POST)
    public void copyItemTo(RestJsonWrapperBean jsonWrapper) {
        itemService.copyTo(jsonWrapper);
    }

    @Log(value = "新增分析项结果", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/items/{itemId}/results", method = RequestMethod.POST)
    public Long insertItemResult(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        return itemResultService.insert(itemId, wrapper);
    }

    @Override
    @Log(value = "删除分析项结果", type = LogType.DELETE)
    @RequestMapping(value = "/items/{itemId}/results", method = RequestMethod.DELETE)
    public void deleteItemResult(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        itemResultService.delete(wrapper);
    }

    @Log(value = "通过测试分析项查询分析项结果", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/items/{itemId}/results/queries", method = RequestMethod.POST)
    public Page<EmcItemResultBean> selectResultPagationByItem(@PathVariable Long itemId, RestJsonWrapperBean wrapper) {
        return itemResultService.selectPaginationByFilter(SearchFilter.instance().match("ITEMID", itemId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "复制测试方法", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        testMethodService.copy(jsonWrapper);
    }

    @Override
    @Log(value = "移动测试方法", type = LogType.UPDATE)
    @RequestMapping(value = "/action/move", method = RequestMethod.PUT)
    public void move(RestJsonWrapperBean jsonWrapper) {
        testMethodService.move(jsonWrapper);
    }

    @Override
    @Log(value = "复制到关联仪器", type = LogType.INSERT)
    @RequestMapping(value = "/equipts/action/copyto", method = RequestMethod.POST)
    public void copyEquiptTo(RestJsonWrapperBean jsonWrapper) {
        testMethodEquiptService.copyTo(jsonWrapper);
    }

    @Override
    @Log(value = "测试库编辑页查询", type = LogType.SELECT)
    @RequestMapping(value = "/tests/queries/raw", method = RequestMethod.POST)
    public Page<EmcTestBean> selectTestRawPagination(RestJsonWrapperBean wrapper) {
        return getService().selectTestRawPagination(wrapper);
    }

    @Override
    @Log(value = "新增测试库", type = LogType.INSERT)
    @RequestMapping(value = "/tests", method = RequestMethod.POST)
    public Long insertTest(RestJsonWrapperBean wrapper) {
        return getService().insertTest(wrapper);
    }

    @Override
    @Log(value = "删除测试库", type = LogType.DELETE)
    @RequestMapping(value = "/tests", method = RequestMethod.DELETE)
    public void deleteTest(RestJsonWrapperBean wrapper) {
        getService().deleteTest(wrapper);
    }

    @Override
    @Log(value = "测试库选择页查询", type = LogType.SELECT)
    @RequestMapping(value = "/tests/queries/choosable", method = RequestMethod.POST)
    public Page<EmcTestBean> selectTestChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectTestChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "测试库下拉框数据查询", type = LogType.SELECT)
    @RequestMapping(value = "/tests/queries/selectable", method = RequestMethod.GET)
    public List<EmcTestBean> selectTestSelectableList() {
        return getService().selectTestSelectableList();
    }

    @Override
    @Log(value = "测试库下拉框数据查询", type = LogType.SELECT)
    @RequestMapping(value = "/tests/items/queries/selectable", method = RequestMethod.GET)
    public List<EmcItemBean> selectTestItemSelectableList() {
        return getService().selectTestItemSelectableList();
    }

    @Log(value = "复制分析项组", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/item-groups/copy", method = RequestMethod.POST)
    public void copyItemGroup(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        itemGroupService.copy(id, wrapper);
    }

    @Log(value = "验证数据唯一性", type = LogType.VALIDATE)
    @RequestMapping(value = { "/methods/{testMethodId}/action/validate-unique" }, method = RequestMethod.POST)
    @Override
    public RestValidationResultBean validateItemNameUnique(RestJsonWrapperBean wrapper, @PathVariable Long testMethodId) {
        return itemService.validateItemNameUnique(wrapper, testMethodId);
    }

    @Log(value = "校验分析项组是否被使用", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/item-groups/action/check-used", method = RequestMethod.POST)
    public List<Long> itemGroupCheckUsed(RestJsonWrapperBean wrapper) {
        return itemGroupService.checkUsed(wrapper);
    }

    @Log(value = "校验测试是否被使用", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/tests/action/check-used", method = RequestMethod.POST)
    public List<Long> testCheckUsed(RestJsonWrapperBean wrapper) {
        return testMethodService.testCheckUsed(wrapper);
    }

    @Log(value = "校验测试方法是否被使用", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/action/check-used", method = RequestMethod.POST)
    public List<Long> testMethodCheckUsed(RestJsonWrapperBean wrapper) {
        return testMethodService.checkUsed(wrapper);
    }

    @Log(value = "复制QC指标到其他测试", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/items/qc-indexes/copy-to", method = RequestMethod.POST)
    public void itemQcIndexCopyTo(RestJsonWrapperBean wrapper) {
        itemQcIndexService.copyTo(wrapper);
    }

    @Override
    @Log(value = "复制有机分析项", type = LogType.INSERT)
    @RequestMapping(value = "/item-groups/action/copy-organic", method = RequestMethod.POST)
    public Long copyOrganicItemGroup(RestJsonWrapperBean wrapper) {
        return itemGroupService.copyOrganicItemGroup(wrapper);
    }

    @Override
    @Log(value = "修改有机分析组名", type = LogType.UPDATE)
    @RequestMapping(value = "/item-groups/action/edit-organic-group", method = RequestMethod.POST)
    public void editOrganicItemGroup(RestJsonWrapperBean wrapper) {
        itemGroupService.editOrganicItemGroup(wrapper);
    }

    @Override
    @Log(value = "复制有机公式到其他分析项组", type = LogType.UPDATE)
    @RequestMapping(value = "/item-groups/{itemGroupId}/action/copy-organic-formula-to", method = RequestMethod.POST)
    public void copyOrganicFormulaTo(@PathVariable Long itemGroupId, RestJsonWrapperBean wrapper) {
        itemGroupService.copyOrganicFormulaTo(itemGroupId, wrapper);
    }

    @Override
    @Log(value = "复制分析项分段修约", type = LogType.INSERT)
    @RequestMapping(value = "/items/revisions/action/copy", method = RequestMethod.POST)
    public void copyItemRevision(RestJsonWrapperBean wrapper) {
        itemRevisionService.copyItemRevision(wrapper);
    }

    @Override
    @Log(value = "复制分段修约到其他分析项", type = LogType.INSERT)
    @RequestMapping(value = "/items/revisions/action/copy-to", method = RequestMethod.POST)
    public void copyItemRevisionTo(RestJsonWrapperBean wrapper) {
        itemRevisionService.copyItemRevisionTo(wrapper);
    }

    @Log(value = "容器维护", type = LogType.SELECT)
    @RequestMapping(value = {"/container-maintenance/queries/raw"}, method = {RequestMethod.POST})
    public Page<EmcTestMethodOrderCategoryBean> selectMaintenancePage(RestJsonWrapperBean wrapper) {
        return testMethodOrderCategoryService.selectMaintenancePage(wrapper);
    }
    
    @Log(value = "验证数据唯一性", type = LogType.VALIDATE)
    @RequestMapping(value = { "tests/{id}/action/validate-unique",
            "tests/{id}/action/validate-unique/{columnName}/{columnValue}",
            "tests/action/validate-unique/{columnName}/{columnValue}"}, method = RequestMethod.POST)
    public RestValidationResultBean validateUnique(@PathVariable(required = false) Long id, @PathVariable(required = false) String columnName, @PathVariable(required = false) String columnValue) {
        return testService.validateUnique(id, columnName, columnValue);
    }

    @Override
    @Log(value = "自动生成分析项组", type = LogType.INSERT)
    @RequestMapping(value = {"/action/gene-item-group"}, method = {RequestMethod.POST})
    public void geneItemGroup(RestJsonWrapperBean wrapper) {
        testMethodService.geneItemGroup(wrapper);
    }

    @Override
    @Log(value = "测试管理锁定", type = LogType.UPDATE)
    @RequestMapping(value = {"/action/lock"}, method = {RequestMethod.POST})
    public void lock(RestJsonWrapperBean wrapper) {
        testMethodService.lock(wrapper);
    }

    @Override
    @Log(value = "测试管理解锁", type = LogType.UPDATE)
    @RequestMapping(value = {"/action/unlock"}, method = {RequestMethod.POST})
    public void unlock(RestJsonWrapperBean wrapper) {
        testMethodService.unlock(wrapper);
    }

    @Override
    @Log(value = "测试管理版本历史", type = LogType.SELECT)
    @RequestMapping(value = {"/log/queries"}, method = {RequestMethod.POST})
    public Page<EmcTestMethodLogBean> selectLogPagination(RestJsonWrapperBean wrapper) {
        return testMethodService.selectLogPagination(wrapper);
    }
    
    @Log(value = "复制公式到", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/copy-formula-to", method = RequestMethod.POST)
    public void copyFormulaTo(RestJsonWrapperBean wrapper) {
        testMethodService.copyFormulaTo(wrapper);
    }
    
    @Log(value = "新增质控分析项", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/qc-items", method = RequestMethod.POST)
    public Long insertQcItem(RestJsonWrapperBean wrapper) {
        return emcQcItemService.insert(wrapper);
    }

    @Override
    @Log(value = "删除质控分析项", type = LogType.DELETE)
    @RequestMapping(value = "/qc-items", method = RequestMethod.DELETE)
    public void deleteQcItem(RestJsonWrapperBean wrapper) {
        emcQcItemService.delete(wrapper);
    }

    @Log(value = "查询质控分析项", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/qc-items/queries", method = RequestMethod.POST)
    public Page<EmcQcItemBean> selectQcItemPagation(RestJsonWrapperBean wrapper) {
        return emcQcItemService.selectPagination(wrapper);
    }
    
    @Override
    @Log(value = "分析录入-批录QC", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-qcindex", method = RequestMethod.POST)
    public void batchUpdateQcIndex(RestJsonWrapperBean wrapper) {
    	testMethodService.batchUpdateQcIndex(wrapper);
    }

    @Log(value = "新增测试方法前处理", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/testmethods/{testMethodId}/beforehandles", method = RequestMethod.POST)
    public Long insertTestMethodBeforehandle(@PathVariable Long testMethodId, RestJsonWrapperBean wrapper) {
        return testmethodBeforehandleService.insert(testMethodId, wrapper);
    }

    @Override
    @Log(value = "删除测试方法前处理", type = LogType.DELETE)
    @RequestMapping(value = "/testmethods/{testMethodId}/beforehandles", method = RequestMethod.DELETE)
    public void deleteTestMethodBeforehandle(@PathVariable Long testMethodId, RestJsonWrapperBean wrapper) {
        testmethodBeforehandleService.delete(wrapper);
    }

    @Log(value = "通过测试方法查询前处理", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/testmethods/{testMethodId}/beforehandles/queries", method = RequestMethod.POST)
    public Page<EmcTestmethodBeforehandleBean> selectTestMethodBeforehandlePagationByTestMethod(@PathVariable Long testMethodId, RestJsonWrapperBean wrapper) {
        return testmethodBeforehandleService.selectPaginationByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodId).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "测试方法前处理选择页查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/beforehandles/queries/choosable", method = RequestMethod.POST)
    public Page<EmcTestmethodBeforehandleBean> selectTestMethodBeforehandleChoosablePagination(RestJsonWrapperBean wrapper) {
        return testmethodBeforehandleService.selectPagination(wrapper);
    }
}
