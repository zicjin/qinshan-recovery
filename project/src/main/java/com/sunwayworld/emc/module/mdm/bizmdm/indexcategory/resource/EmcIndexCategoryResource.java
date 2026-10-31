package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexPreviewBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

@RequestMapping("/secure/emc/module/mdm/bizmdm/index-categories")
public interface EmcIndexCategoryResource extends GenericResource<EmcIndexCategoryService, EmcIndexCategoryBean, Long>, GenericSelectableResource<EmcIndexCategoryService, EmcIndexCategoryBean, Long> {

    Long insertIndexSet(String indexId, Long indexCategoryId, Long tmplId, RestJsonWrapperBean wrapper);

    Page<EmcIndexSetBean> selectIndexSetPaginationById(Long id, RestJsonWrapperBean wrapper);

    EmcIndexSetBean selectIndexSetById(Long indexId, RestJsonWrapperBean wrapper);

    void deleteIndexSet(RestJsonWrapperBean wrapper);

    void copyOrderIndex(RestJsonWrapperBean jsonWrapper);

    Page<EmcIndexSetBean> selectIndexSetPaginationByOrderIndex(Long indexId, RestJsonWrapperBean wrapper);

    void activateIndexCategory(RestJsonWrapperBean wrapper);

    void deactivateIndexCategory(RestJsonWrapperBean wrapper);

    void activateOrderIndex(RestJsonWrapperBean wrapper);

    void deactivateOrderIndex(RestJsonWrapperBean wrapper);

    String insertItem(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderIndexBean> selectItemPaginationById(Long id, RestJsonWrapperBean wrapper);

    EmcOrderIndexBean selectItemById(String itemId);

    void deleteItem(RestJsonWrapperBean wrapper);

    void syncIndexSet(RestJsonWrapperBean wrapper);

    Page<EmcOrderIndexBean> selectOrderIndexChoosablePagination(RestJsonWrapperBean wrapper);

    String estandSync();

    String estandUpdate();

    Page<EmcIndexPreviewBean> selectIndexPreviewPagination(RestJsonWrapperBean wrapper);

    void addIndex(RestJsonWrapperBean wrapper);

    Page<EmcIndexCategoryBean> selectByProjScheme(RestJsonWrapperBean wrapper);

    void estandSync(RestJsonWrapperBean wrapper);

    Page<EmcIndexCategoryBean> selectByOrder(RestJsonWrapperBean wrapper);

}
