package com.sunwayworld.emc.module.mdm.basemdm.envrecord.service;



import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author licm@sunwayworld.com 2022/01/19
 */
public interface PfEnvRecordService extends EmcEnvRecordService {

	void batchInsertEnv(RestJsonWrapperBean wrapper);

}
