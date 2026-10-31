package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.framework.data.page.Page;

/**
 * @author zhengxy@sunwayworld.com 2021年06月15日
 */
public interface PfPropertyFieldService extends EmcPropertyFieldService {

    Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorTmplId(String monitorTmplId);
}
