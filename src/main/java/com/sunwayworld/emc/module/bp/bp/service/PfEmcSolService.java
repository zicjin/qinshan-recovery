package com.sunwayworld.emc.module.bp.bp.service;

import java.util.Map;

import com.sunwayworld.emc.module.bp.sol.service.EmcSolService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/22 16:18
 */
public interface PfEmcSolService extends EmcSolService {
     Map<String, String> calaConcentration(String calibrationconcentration, String revisionRule);
     void setInvalidRecord(RestJsonWrapperBean wrapper);
     
     void abandoned(RestJsonWrapperBean wrapper);

}
