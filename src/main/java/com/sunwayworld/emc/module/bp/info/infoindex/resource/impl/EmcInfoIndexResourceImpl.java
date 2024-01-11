package com.sunwayworld.emc.module.bp.info.infoindex.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategorySectionBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexCategoryBean;
import com.sunwayworld.emc.module.bp.info.infoindex.resource.EmcInfoIndexResource;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcInfoIndexService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

@LogModule("评价标准表")
@RestController
@GikamBean
public class EmcInfoIndexResourceImpl implements EmcInfoIndexResource {
    @Autowired
    private EmcInfoIndexService infoIndexService;

    @Override
    public EmcInfoIndexService getService() {
        return infoIndexService;
    }
    
    @Override
    @Log(value = "新增评价标准", type = LogType.INSERT)
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "查询评价标准分类", type = LogType.SELECT)
    @RequestMapping(value = "/{indexId}/categorys/queries", method = RequestMethod.POST)
    public Page<EmcInfoIndexCategoryBean> selectIndexCategoryPaginationByIndexId(@PathVariable Long indexId, RestJsonWrapperBean wrapper) {
        return getService().selectIndexCategoryPaginationByIndexId(indexId, wrapper);
    }

    @Override
    @Log(value = "新增评价标准分类", type = LogType.INSERT)
    @RequestMapping(value = "/{indexId}/categorys", method = RequestMethod.POST)
    public Long insertIndexCategory(@PathVariable Long indexId, RestJsonWrapperBean wrapper) {
        return getService().insertIndexCategory(indexId, wrapper);
    }

    @Override
    @Log(value = "删除评价标准分类", type = LogType.DELETE)
    @RequestMapping(value = "/categorys", method = RequestMethod.DELETE)
    public void deleteIndexCategory(RestJsonWrapperBean wrapper) {
        getService().deleteIndexCategory(wrapper);
    }

    @Override
    @Log(value = "复制评价标准分类", type = LogType.INSERT)
    @RequestMapping(value = "/{categoryId}/categorys/copy", method = RequestMethod.POST)
    public EmcInfoIndexCategoryBean copyIndexCategory(@PathVariable Long categoryId) {
        return getService().copyIndexCategory(categoryId);
    }

    @Override
    @Log(value = "查询评价标准分类指标值", type = LogType.SELECT)
    @RequestMapping(value = "/{categoryId}/categoryindexs/queries", method = RequestMethod.POST)
    public Page<EmcCategoryIndexBean> selectCategoryIndexPaginationByCategoryId(@PathVariable Long categoryId, RestJsonWrapperBean wrapper) {
        return getService().selectCategoryIndexPaginationByCategoryId(categoryId, wrapper);
    }

    @Override
    @Log(value = "新增评价标准分类指标值", type = LogType.INSERT)
    @RequestMapping(value = "/{categoryId}/categoryindexs", method = RequestMethod.POST)
    public Long insertCategoryIndex(@PathVariable Long categoryId, RestJsonWrapperBean wrapper) {
        return getService().insertCategoryIndex(categoryId, wrapper);
    }

    @Override
    @Log(value = "删除评价标准分类指标值", type = LogType.DELETE)
    @RequestMapping(value = "/categoryindexs", method = RequestMethod.DELETE)
    public void deleteCategoryIndex(RestJsonWrapperBean wrapper) {
        getService().deleteCategoryIndex(wrapper);
    }

    @Override
    @Log(value = "查询评价标准分类断面属性", type = LogType.SELECT)
    @RequestMapping(value = "/{categoryId}/categorysections/queries", method = RequestMethod.POST)
    public Page<EmcCategorySectionBean> selectCategorySectionPaginationByCategoryId(@PathVariable Long categoryId, RestJsonWrapperBean wrapper) {
        return getService().selectCategorySectionPaginationByCategoryId(categoryId, wrapper);
    }

    @Override
    @Log(value = "新增评价标准分类断面属性", type = LogType.INSERT)
    @RequestMapping(value = "/{categoryId}/categorysections", method = RequestMethod.POST)
    public Long insertCategorySection(@PathVariable Long categoryId, RestJsonWrapperBean wrapper) {
        return getService().insertCategorySection(categoryId, wrapper);
    }

    @Override
    @Log(value = "删除评价标准分类断面属性", type = LogType.DELETE)
    @RequestMapping(value = "/categorysections", method = RequestMethod.DELETE)
    public void deleteCategorySection(RestJsonWrapperBean wrapper) {
        getService().deleteCategorySection(wrapper);
    }
}
