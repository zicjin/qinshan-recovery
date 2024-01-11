package com.sunwayworld.emc.module.bp.info.infotmpl.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

public interface EmcInfoTmplPointService extends GenericService<EmcInfoTmplPointBean, Long> {
    void insertMul(RestJsonWrapperBean jsonWrapper);
    
    void deleteMul(RestJsonWrapperBean jsonWrapper);
    
    void updateMul(RestJsonWrapperBean jsonWrapper);
    
    List<EmcInfoTmplPointBean> copy(Pair<Long, Long> infoTmplIdPair);
}
