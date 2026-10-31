package com.sunwayworld.emc.module.bp.info.infotmpl.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcTmplFieldItemBean;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

public interface EmcTmplFieldItemService extends GenericService<EmcTmplFieldItemBean, Long> {
    
    List<EmcTmplFieldItemBean> copy(List<Pair<Long, Long>> infoTmplFieldIdPairList);

	Long insert(Long tmplId, RestJsonWrapperBean wrapper);
    
}
