package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:45:35
 */
public interface EmcStockConcentService extends GenericService<EmcStockConcentBean, Long> {

    List<EmcStockConcentBean> selectLimitList(List<EmcStockConcentBean> filter);

    void stockConcentCalculate(RestJsonWrapperBean wrapper);
}
