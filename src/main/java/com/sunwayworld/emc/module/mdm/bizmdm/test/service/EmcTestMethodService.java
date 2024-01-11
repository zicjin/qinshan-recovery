package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcTestMethodService extends GenericService<EmcTestMethodBean, Long>, 
                                              GenericActivatableService<EmcTestMethodBean, Long>, 
                                              GenericChoosableService<EmcTestMethodBean, Long>,
                                              GenericSelectableService<EmcTestMethodBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);

    void move(RestJsonWrapperBean wrapper);

    Page<EmcTestBean> selectTestRawPagination(RestJsonWrapperBean wrapper);

    Long insertTest(RestJsonWrapperBean wrapper);

    void deleteTest(RestJsonWrapperBean wrapper);

    Page<EmcTestBean> selectTestChoosablePagination(RestJsonWrapperBean wrapper);

    Page<EmcGroupItemBean> selectGroupItemChoosablePagination(RestJsonWrapperBean wrapper);

    List<EmcTestBean> selectTestSelectableList();

    List<EmcItemBean> selectTestItemSelectableList();

    List<Long> testCheckUsed(RestJsonWrapperBean wrapper);

    List<Long> checkUsed(RestJsonWrapperBean wrapper);

    void geneItemGroup(RestJsonWrapperBean wrapper);

    void lock(RestJsonWrapperBean wrapper);

    void unlock(RestJsonWrapperBean wrapper);

    Page<EmcTestMethodLogBean> selectLogPagination(RestJsonWrapperBean wrapper);

    void copyFormulaTo(RestJsonWrapperBean wrapper);
    
    void batchUpdateQcIndex(RestJsonWrapperBean wrapper);

}
