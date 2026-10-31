package com.sunwayworld.emc.module.bp.bp.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/23 10:03
 */
@RequestMapping("/secure/emc/module/bp/sols")
public interface PfEmcSolResource {
    Map<String, String> calaConcentration(String calibrationconcentration, String revisionRule);
    void setInvalidRecord(RestJsonWrapperBean wrapper);
    void abandoned(RestJsonWrapperBean wrapper);

}
