package com.sunwayworld.emc.module.bp.info.infotmpl.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcTmplFieldItemBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */
@RequestMapping("/secure/emc/module/bp/info/info-tmpls")
public interface EmcInfoTmplResource extends GenericResource<EmcInfoTmplService, EmcInfoTmplBean, Long>, 
                                             GenericSelectableResource<EmcInfoTmplService, EmcInfoTmplBean, Long> {
    EmcInfoTmplBean copy(Long tmplId, RestJsonWrapperBean wrapper);
    
    Page<EmcInfoTmplFieldBean> selectTmplFieldPaginationByTmplId(Long id, RestJsonWrapperBean wrapper);
    
    Page<EmcInfoTmplFieldBean> selectTmplFieldPagination(RestJsonWrapperBean wrapper);
    
    List<EmcInfoTmplFieldBean> selectTmplFieldSelectableList();
    
    Page<EmcTmplFieldItemBean> selectTmplFieldItemPaginationByTmplId(RestJsonWrapperBean wrapper);
    
    void deleteTmplFieldItem(RestJsonWrapperBean wrapper);
    
    Page<EmcInfoTmplPointBean> selectTmplPointPaginationByTmplId(Long tmplId, RestJsonWrapperBean wrapper);
    
    Page<EmcInfoTmplPointBean> selectTmplPointPagination(RestJsonWrapperBean wrapper);
    
    void insertTmplPoint(RestJsonWrapperBean wrapper);
    
    void deleteTmplPoint(RestJsonWrapperBean wrapper);

    void updateTmplPoint(RestJsonWrapperBean wrapper);

	Long insertTmplFieldItem(Long tmplId, RestJsonWrapperBean wrapper);
}