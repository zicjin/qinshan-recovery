package com.sunwayworld.emc.module.mdm.basemdm.envrecord.resource;



import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;


/**
 * @author licm@sunwayworld.com 2022/01/19
 */

@RequestMapping("/secure/emc/module/mdm/basemdm/env-records")
public interface PfEnvRecordResource extends EmcEnvRecordResource {

	void batchInsertEnv(RestJsonWrapperBean wrapper);

}
