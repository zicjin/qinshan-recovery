package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyFieldService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年06月15日
 */
@Repository
@GikamBean
public class PfPropertyFieldServiceImpl extends EmcPropertyFieldServiceImpl implements PfPropertyFieldService {

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Override
    public Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorTmplId(String monitorTmplId) {
        EmcMonitorTmplBean monitorTmpl = monitorTmplService.selectById(monitorTmplId);
        String pointPropertyTmplId = monitorTmpl.getExt$Item("pointPropertyTmplId");
        if (StringUtils.isEmpty(pointPropertyTmplId)) {
            return new Pagination<>();
        }

        List<EmcPropertyFieldBean> propertyFieldList = getDao().selectListByOneColumnValue(pointPropertyTmplId, "TMPLID", Order.asc("ORDERNO"), Order.desc("ID"));
        if (propertyFieldList.isEmpty()) {
            return new Pagination<>();
        }

        return new Pagination<>(propertyFieldList);
    }
}
