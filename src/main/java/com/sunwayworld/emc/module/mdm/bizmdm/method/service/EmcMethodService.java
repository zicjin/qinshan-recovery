package com.sunwayworld.emc.module.mdm.bizmdm.method.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

public interface EmcMethodService extends GenericService<EmcMethodBean, Long>, 
                                          GenericActivatableService<EmcMethodBean, Long>, 
                                          GenericChoosableService<EmcMethodBean, Long>,
                                          GenericSelectableService<EmcMethodBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    void updateVersionInfo(Long id);

    Long selectReportConfigLineIdById(Long id);

    List<Long> checkUsed(RestJsonWrapperBean wrapper);

    Page<EmcMethodBean> getEstandard(RestJsonWrapperBean wrapper);

    void estandSync(RestJsonWrapperBean wrapper);
    
    List<CoreFileBean> selectMethodFile(RestJsonWrapperBean wrapper);

    List<CoreCodeBean> selectCoreCodeByCodeNames(RestJsonWrapperBean wrapper);
    
    void methodCopy(RestJsonWrapperBean wrapper);
}
