package com.sunwayworld.emc.module.bp.bp.resource.impl;

import java.util.Map;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.resource.PfEmcSolResource;
import com.sunwayworld.emc.module.bp.bp.service.PfEmcSolService;
import com.sunwayworld.emc.module.bp.sol.resource.impl.EmcSolResourceImpl;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/23 10:06
 */
@RestController
@GikamBean
public class PfEmcSolResourceImpl extends EmcSolResourceImpl implements PfEmcSolResource {

    @Autowired
    private PfEmcSolService emcSolService;

    @Override
    @Log(value = "计算修约后的标准溶液浓度", type = LogType.SELECT)
    @RequestMapping(value = "/cala/concentration/{calibrationconcentration}/{revisionRule}", method = RequestMethod.GET)
    public Map<String, String> calaConcentration(@PathVariable String calibrationconcentration, @PathVariable String revisionRule) {
        return emcSolService.calaConcentration(calibrationconcentration,revisionRule);
    }

    @Override
    @Log(value = "标定溶液原始溶液设置无效结果", type = LogType.UPDATE)
    @RequestMapping(value = "/action/set-invalid", method = RequestMethod.PUT)
    public void setInvalidRecord(RestJsonWrapperBean wrapper) {
        this.emcSolService.setInvalidRecord(wrapper);
    }
    
    @Log(value = "废弃标定溶液记录", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/abandoned", method = RequestMethod.PUT)
    public void abandoned(RestJsonWrapperBean wrapper) {
    	emcSolService.abandoned(wrapper);
    }

}
