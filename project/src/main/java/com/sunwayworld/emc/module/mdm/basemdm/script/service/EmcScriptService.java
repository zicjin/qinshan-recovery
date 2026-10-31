package com.sunwayworld.emc.module.mdm.basemdm.script.service;

import com.sunwayworld.emc.module.mdm.basemdm.script.bean.EmcScriptBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcScriptService extends GenericService<EmcScriptBean, String> {
    
    String insert(RestJsonWrapperBean wrapper);
}
