package com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.service;





import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.bean.PfEquiptTempBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
public interface PfEquiptTempService extends GenericService<PfEquiptTempBean, Long>,GenericChoosableService<PfEquiptTempBean, Long> 
{
	Long insert(RestJsonWrapperBean wrapper);
    
	void batchInsertTemp(RestJsonWrapperBean wrappe);
}
