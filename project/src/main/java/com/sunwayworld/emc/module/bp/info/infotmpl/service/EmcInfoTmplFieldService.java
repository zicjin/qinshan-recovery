package com.sunwayworld.emc.module.bp.info.infotmpl.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

public interface EmcInfoTmplFieldService extends GenericService<EmcInfoTmplFieldBean, Long>,
                                                 GenericSelectableService<EmcInfoTmplFieldBean, Long> {
    /**
     * 初始模板字段数据
     * @param infoTmplId
     */
    void initData(Long infoTmplId);
    
    /**
     * 根据新旧模板编码复制字段项
     * @return 复制后新旧模板字段项编码的映射
     */
    List<Pair<Long, Long>> copy(Pair<Long, Long> infoTmplIdMap);
}
