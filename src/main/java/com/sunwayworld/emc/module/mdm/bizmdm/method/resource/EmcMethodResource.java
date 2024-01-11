package com.sunwayworld.emc.module.mdm.bizmdm.method.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@RequestMapping("/secure/emc/module/mdm/bizmdm/methods")
public interface EmcMethodResource extends GenericResource<EmcMethodService, EmcMethodBean, Long>, 
                                           GenericActivatableResource<EmcMethodService, EmcMethodBean, Long>,
                                           GenericChoosableResource<EmcMethodService, EmcMethodBean, Long>,
                                           GenericSelectableResource<EmcMethodService, EmcMethodBean, Long> {

    Long insertVersion(Long id, RestJsonWrapperBean wrapper);

    Page<EmcMethodVersionBean> selectVersionPagationByMethod(Long id, RestJsonWrapperBean wrapper);

    void deleteVersion(Long id, RestJsonWrapperBean wrapper);

    void updateVersionInfo(Long id, RestJsonWrapperBean wrapper);

    EmcMethodVersionBean selectMethodVersionDetail(Long id);
    
    List<Long> methodCheckUsed(RestJsonWrapperBean wrapper);

    Page<EmcMethodBean> getEstandard(RestJsonWrapperBean wrapper);

    void estandSync(RestJsonWrapperBean wrapper);
}
