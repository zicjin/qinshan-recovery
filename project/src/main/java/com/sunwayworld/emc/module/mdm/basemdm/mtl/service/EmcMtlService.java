package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午6:16:27
 */
public interface EmcMtlService extends GenericService<EmcMtlBean, String>,
                                       GenericChoosableService<EmcMtlBean, String> {

    void updateStockQty(String id);

    void generateMtlNo(EmcMtlBean mtl);

    String exportFile(RestJsonWrapperBean wrapper);

    String exportGeneralFile(RestJsonWrapperBean wrapper);
}
