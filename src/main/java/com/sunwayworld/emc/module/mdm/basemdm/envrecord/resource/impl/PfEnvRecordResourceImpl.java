package com.sunwayworld.emc.module.mdm.basemdm.envrecord.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.resource.PfEnvRecordResource;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.PfEnvRecordService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2022/01/19
 */
@LogModule(value = "环境记录")
@RestController
@GikamBean
public class PfEnvRecordResourceImpl extends EmcEnvRecordResourceImpl implements PfEnvRecordResource {

    @Autowired
    private PfEnvRecordService pfEnvRecordService;

    @Override
    public PfEnvRecordService getService() {
        return pfEnvRecordService;
    }


    @Override
    @Log(value = "批量新增环境记录", type = LogType.INSERT)
    @RequestMapping(value = "/action/batch-insert-env", method = RequestMethod.POST)
    public void batchInsertEnv(RestJsonWrapperBean wrapper){
        this.getService().batchInsertEnv(wrapper);
    }




}
