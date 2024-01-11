package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexPreviewBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.resource.EmcIndexCategoryResource;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexPreviewService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcOrderIndexService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule(value = "指标分类")
@GikamBean
@RestController
public class EmcIndexCategoryResourceImpl implements EmcIndexCategoryResource {

    @Autowired
    private EmcIndexCategoryService indexCategoryService;

    @Autowired
    private EmcOrderIndexService orderIndexService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcIndexPreviewService indexPreviewService;

    @Override
    public EmcIndexCategoryService getService() {
        return indexCategoryService;
    }

    @Log(value = "启用指标分类", type = LogType.ACTIVATE)
    @Override
    @RequestMapping(value = "/index-categories/action/activate", method = RequestMethod.PUT)
    public void activateIndexCategory(RestJsonWrapperBean wrapper) {
        indexCategoryService.activate(wrapper);
    }

    @RequestMapping(value = "/index-categories/action/deactivate", method = RequestMethod.PUT)
    @Log(value = "停用指标分类", type = LogType.ACTIVATE)
    @Override
    public void deactivateIndexCategory(RestJsonWrapperBean wrapper) {
        indexCategoryService.deactivate(wrapper);
    }

    @RequestMapping(value = "/order-indexes/action/activate", method = RequestMethod.PUT)
    @Log(value = "启用样品指标", type = LogType.ACTIVATE)
    @Override
    public void activateOrderIndex(RestJsonWrapperBean wrapper) {
        orderIndexService.activate(wrapper);
    }

    @RequestMapping(value = "/order-indexes/action/deactivate", method = RequestMethod.PUT)
    @Log(value = "停用样品指标", type = LogType.ACTIVATE)
    @Override
    public void deactivateOrderIndex(RestJsonWrapperBean wrapper) {
        orderIndexService.deactivate(wrapper);
    }

    @Log(value = "新增指标分类", type = LogType.INSERT)
    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "复制样品指标", type = LogType.INSERT)
    @RequestMapping(value = "/order-indexes/action/copy", method = RequestMethod.POST)
    public void copyOrderIndex(RestJsonWrapperBean jsonWrapper) {
        orderIndexService.copy(jsonWrapper);
    }

    @Override
    @Log(value = "通过样品指标查询指标设置", type = LogType.SELECT)
    @RequestMapping(value = "/order-indexes/{indexId}/index-sets/queries", method = { RequestMethod.GET, RequestMethod.POST })
    public Page<EmcIndexSetBean> selectIndexSetPaginationByOrderIndex(@PathVariable Long indexId, RestJsonWrapperBean wrapper) {
        return indexSetService.selectPaginationByFilter(SearchFilter.instance().match("INDEXID", indexId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增指标设置", type = LogType.INSERT)
    @RequestMapping(value = "/{indexId}/{indexCategoryId}/{tmplId}/index-sets", method = RequestMethod.POST)
    public Long insertIndexSet(@PathVariable String indexId, @PathVariable Long indexCategoryId, @PathVariable Long tmplId ,RestJsonWrapperBean wrapper) {
        return indexSetService.insert(indexId, indexCategoryId, tmplId, wrapper);
    }

    @Override
    @Log(value = "指标设置查询", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/index-sets/queries", method = RequestMethod.GET)
    public Page<EmcIndexSetBean> selectIndexSetPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return indexSetService.selectPaginationByFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询指标设置详情", type = LogType.SELECT)
    @RequestMapping(value = "/index-sets/{id}", method = RequestMethod.GET)
    public EmcIndexSetBean selectIndexSetById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return indexSetService.selectDetail(id);
    }

    @Override
    @Log(value = "删除指标设置", type = LogType.DELETE)
    @RequestMapping(value = "/index-sets", method = RequestMethod.DELETE)
    public void deleteIndexSet(RestJsonWrapperBean wrapper) {
        indexSetService.delete(wrapper);
    }

    @Override
    @Log(value = "新增样品指标", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/order-indexes", method = RequestMethod.POST)
    public String insertItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return orderIndexService.insert(id, wrapper);
    }

    @Override
    @Log(value = "样品指标查询", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/order-indexes/queries", method = RequestMethod.POST)
    public Page<EmcOrderIndexBean> selectItemPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return orderIndexService.selectPaginationByFilter(SearchFilter.instance().match("INDEXCATEGORYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询样品指标详情", type = LogType.SELECT)
    @RequestMapping(value = "/order-indexes/{itemId}", method = RequestMethod.GET)
    public EmcOrderIndexBean selectItemById(@PathVariable String itemId) {
        return orderIndexService.selectDetail(itemId);
    }

    @Override
    @Log(value = "删除样品指标", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/order-indexes", method = RequestMethod.DELETE)
    public void deleteItem(RestJsonWrapperBean wrapper) {
        orderIndexService.delete(wrapper);
    }

    @Log(value = "同步", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/actions/sync", method = RequestMethod.PUT)
    public void syncIndexSet(RestJsonWrapperBean wrapper) {
        getService().sync(wrapper);
    }

    @Override
    @Log(value = "样品指标选择页面", type = LogType.SELECT)
    @RequestMapping(value = "/queries/order-index/choosable", method = RequestMethod.POST)
    public Page<EmcOrderIndexBean> selectOrderIndexChoosablePagination(RestJsonWrapperBean wrapper) {
        return orderIndexService.selectChoosablePagination(wrapper);
    }

    @Override
    @Log(value = "同步易标准数据", type = LogType.INSERT)
    @RequestMapping(value = "/estand-data/action/sync", method = RequestMethod.POST)
    public String estandSync() {
        return indexCategoryService.estandSync();
    }

    @Override
    @Log(value = "更新易标准数据", type = LogType.INSERT)
    @RequestMapping(value = "/estand-data/action/update", method = RequestMethod.POST)
    public String estandUpdate() {
        return indexCategoryService.estandUpdate();
    }

    @Override
    @Log(value = "指标预览查询", type = LogType.SELECT)
    @RequestMapping(value = "/index-preview/queries", method = RequestMethod.POST)
    public Page<EmcIndexPreviewBean> selectIndexPreviewPagination(RestJsonWrapperBean wrapper) {
        return indexPreviewService.selectPagination(wrapper);
    }

    @Override
    @Log(value = "指标添加", type = LogType.INSERT)
    @RequestMapping(value = "/index/insert", method = RequestMethod.POST)
    public void addIndex(RestJsonWrapperBean wrapper) {
        indexCategoryService.addIndex(wrapper);
    }

    @Override
    @Log(value = "监测方案页面修改指标项目页面", type = LogType.SELECT)
    @RequestMapping(value = "/queries/selectByProjScheme", method = RequestMethod.POST)
    public Page<EmcIndexCategoryBean> selectByProjScheme(RestJsonWrapperBean wrapper) {
        return indexCategoryService.selectByProjScheme(wrapper);
    }

    @Log(value = "同步易标准数据", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/estand-datas/action/sync", method = RequestMethod.POST)
    public void estandSync(RestJsonWrapperBean wrapper) {
        indexCategoryService.estandSync(wrapper);
    }

    @Override
    @Log(value = "样品登记修改指标项目页面", type = LogType.SELECT)
    @RequestMapping(value = "/queries/select-by-order", method = RequestMethod.POST)
    public Page<EmcIndexCategoryBean> selectByOrder(RestJsonWrapperBean wrapper) {
        return indexCategoryService.selectByOrder(wrapper);
    }
}
