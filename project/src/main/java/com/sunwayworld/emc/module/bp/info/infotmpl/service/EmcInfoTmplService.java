package com.sunwayworld.emc.module.bp.info.infotmpl.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcTmplFieldItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

public interface EmcInfoTmplService extends GenericService<EmcInfoTmplBean, Long>, GenericSelectableService<EmcInfoTmplBean, Long> {
    EmcInfoTmplBean copy(Long tmplId);

    Page<EmcInfoTmplFieldBean> selectTmplFieldPaginationByTmplId(Long tmplId, RestJsonWrapperBean wrapper);

    Page<EmcInfoTmplFieldBean> selectTmplFieldPagination(RestJsonWrapperBean wrapper);

    List<EmcInfoTmplFieldBean> selectTmplFieldSelectableList();

    Page<EmcTmplFieldItemBean> selectTmplFieldItemPaginationByTmplId(RestJsonWrapperBean wrapper);

    Long insertTmplFieldItem(Long tmplId, RestJsonWrapperBean wrapper);

    void deleteTmplFieldItem(RestJsonWrapperBean wrapper);

    Page<EmcInfoTmplPointBean> selectTmplPointPaginationByTmplId(Long tmplId, RestJsonWrapperBean wrapper);

    Page<EmcInfoTmplPointBean> selectTmplPointPagination(RestJsonWrapperBean wrapper);

    void insertTmplPoint(RestJsonWrapperBean wrapper);

    void deleteTmplPoint(RestJsonWrapperBean wrapper);

    void updateTmplPoint(RestJsonWrapperBean wrapper);
}
