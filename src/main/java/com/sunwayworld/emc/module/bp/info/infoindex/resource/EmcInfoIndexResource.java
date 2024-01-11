package com.sunwayworld.emc.module.bp.info.infoindex.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategorySectionBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexCategoryBean;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcInfoIndexService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */
@RequestMapping("/secure/emc/module/bp/info/info-indexs")
public interface EmcInfoIndexResource extends GenericResource<EmcInfoIndexService, EmcInfoIndexBean, Long>, GenericChoosableResource<EmcInfoIndexService, EmcInfoIndexBean, Long> {

    Page<EmcInfoIndexCategoryBean> selectIndexCategoryPaginationByIndexId(Long indexId, RestJsonWrapperBean wrapper);

    Long insertIndexCategory(Long indexId, RestJsonWrapperBean wrapper);

    void deleteIndexCategory(RestJsonWrapperBean wrapper);

    EmcInfoIndexCategoryBean copyIndexCategory(Long categoryId);

    Page<EmcCategoryIndexBean> selectCategoryIndexPaginationByCategoryId(Long categoryId, RestJsonWrapperBean wrapper);

    Long insertCategoryIndex(Long categoryId, RestJsonWrapperBean wrapper);

    void deleteCategoryIndex(RestJsonWrapperBean wrapper);

    Page<EmcCategorySectionBean> selectCategorySectionPaginationByCategoryId(Long categoryId, RestJsonWrapperBean wrapper);

    Long insertCategorySection(Long categoryId, RestJsonWrapperBean wrapper);

    void deleteCategorySection(RestJsonWrapperBean wrapper);
}
