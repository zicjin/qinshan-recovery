package com.sunwayworld.emc.module.mdm.file.file.service;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
public interface EmcFileVersionService extends GenericService<EmcFileVersionBean, Long> {

    Long insert(Long id, RestJsonWrapperBean wrapper);
}
