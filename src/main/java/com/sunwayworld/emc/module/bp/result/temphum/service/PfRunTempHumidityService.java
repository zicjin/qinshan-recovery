package com.sunwayworld.emc.module.bp.result.temphum.service;



import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
public interface PfRunTempHumidityService extends GenericService<PfRunTempHumidityBean, Long> {

    void synTempHumidity(RestJsonWrapperBean wrapper);
    
    void synAllTempHumidity(RestJsonWrapperBean wrapper);

    void batchUpdateTempOrder(RestJsonWrapperBean wrapper);

}
