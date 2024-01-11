package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMpcTmplBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */

public interface EmcMonitorPointCategoryService
        extends GenericService<EmcMonitorPointCategoryBean, String>, GenericSelectableService<EmcMonitorPointCategoryBean, String>, GenericChoosableService<EmcMonitorPointCategoryBean, String> {
    Page<EmcMpcTmplBean> selectTmplPaginationByMpcId(String id, RestJsonWrapperBean wrapper);

    public Long insertTmpl(String id, RestJsonWrapperBean wrapper);

    public void deleteTmpl(String id, RestJsonWrapperBean wrapper);

    List<EmcMonitorPointCategoryBean> selectFilterMonitorTmplGroupSelectableList();

    List<EmcMpcTmplBean> selectTmplSelectableList();
}
