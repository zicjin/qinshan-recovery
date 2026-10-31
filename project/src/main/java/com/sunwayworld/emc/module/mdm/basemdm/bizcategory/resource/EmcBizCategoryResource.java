package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryProjectBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizClassBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/biz-categories")
public interface EmcBizCategoryResource extends GenericResource<EmcBizCategoryService, EmcBizCategoryBean, Long>, GenericSelectableResource<EmcBizCategoryService, EmcBizCategoryBean, Long>, GenericChoosableResource<EmcBizCategoryService, EmcBizCategoryBean, Long > {

    void insertProject(Long id, RestJsonWrapperBean wrapper);

    Page<EmcBizCategoryProjectBean> selectBizCategoryProjectPaginationByBizCategoryId(Long id, RestJsonWrapperBean wrapper);

    void deleteProject(Long id, RestJsonWrapperBean wrapper);

    Page<EmcBizCategoryProjectBean> selectBizCategoryProjectChoosablePagination(RestJsonWrapperBean wrapper);
    
    List<EmcBizCategoryProjectBean> selectProjectSelectableList();

    void insertBizClass(RestJsonWrapperBean wrapper);

    void deleteBizClass(RestJsonWrapperBean wrapper);

    Page<EmcBizClassBean> selectBizClass(RestJsonWrapperBean wrapper);

    List<EmcBizClassBean> selectBizClassSelectableList();

}
