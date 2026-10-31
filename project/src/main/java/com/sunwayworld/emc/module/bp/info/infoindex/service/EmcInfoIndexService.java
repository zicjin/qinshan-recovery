package com.sunwayworld.emc.module.bp.info.infoindex.service;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategorySectionBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexCategoryBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

public interface EmcInfoIndexService extends GenericService<EmcInfoIndexBean, Long>, GenericChoosableService<EmcInfoIndexBean, Long> {
    /**
     * 新增评价标准指标
     */
    Long insert(RestJsonWrapperBean wrapper);

    /**
     * 根据评价标准指标查询评价标准指标分类
     * 
     * @param indexId
     * @param wrapper
     * @return
     */
    Page<EmcInfoIndexCategoryBean> selectIndexCategoryPaginationByIndexId(Long indexId, RestJsonWrapperBean wrapper);

    /**
     * 新增评价标准指标分类
     * 
     * @param indexId
     * @param wrapper
     * @return
     */
    Long insertIndexCategory(Long indexId, RestJsonWrapperBean wrapper);

    /**
     * 删除评价标准指标分类
     * 
     * @param wrapper
     */
    void deleteIndexCategory(RestJsonWrapperBean wrapper);

    /**
     * 复制评价标准指标分类
     * 
     * @param categoryId
     * @return
     */
    EmcInfoIndexCategoryBean copyIndexCategory(Long categoryId);

    /**
     * 根据分类查询分类指标值
     * 
     * @param categoryId
     * @param wrapper
     * @return
     */
    Page<EmcCategoryIndexBean> selectCategoryIndexPaginationByCategoryId(Long categoryId, RestJsonWrapperBean wrapper);

    /**
     * 新增分类指标值
     * 
     * @param categoryId
     * @param wrapper
     * @return
     */
    Long insertCategoryIndex(Long categoryId, RestJsonWrapperBean wrapper);

    /**
     * 删除分类指标值
     * 
     * @param wrapper
     */
    void deleteCategoryIndex(RestJsonWrapperBean wrapper);

    /**
     * 根据分类查询分类断面属性
     * 
     * @param categoryId
     * @param wrapper
     * @return
     */
    Page<EmcCategorySectionBean> selectCategorySectionPaginationByCategoryId(Long categoryId, RestJsonWrapperBean wrapper);

    /**
     * 新增分类断面属性
     * 
     * @param categoryId
     * @param wrapper
     * @return
     */
    Long insertCategorySection(Long categoryId, RestJsonWrapperBean wrapper);

    /**
     * 删除分类断面属性
     * 
     * @param wrapper
     */
    void deleteCategorySection(RestJsonWrapperBean wrapper);

}
