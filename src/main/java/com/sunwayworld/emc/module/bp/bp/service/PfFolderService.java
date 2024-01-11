package com.sunwayworld.emc.module.bp.bp.service;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年07月19日
 */
public interface PfFolderService extends EmcFolderService {

    String generateFolderNo(EmcProjectBean project);
    
    String submitRemind (RestJsonWrapperBean wrapper);

}
