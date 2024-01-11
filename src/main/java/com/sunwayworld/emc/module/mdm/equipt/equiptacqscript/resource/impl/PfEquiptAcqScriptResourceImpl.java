package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.resource.PfEquiptAcqScriptResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasResultService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年09月08日
 */
@GikamBean
@RestController
public class PfEquiptAcqScriptResourceImpl extends EmcEquiptAcqScriptResourceImpl implements PfEquiptAcqScriptResource {

    @Autowired
    private EmcEasResultService easResultService;

    @Override
    @Log(value = "查询仪器采集结果参数", type = LogType.SELECT)
    public Page<EmcEasResultBean> selectEasResultPagination(RestJsonWrapperBean wrapper) {
        return easResultService.selectPagination(wrapper);
    }
}
