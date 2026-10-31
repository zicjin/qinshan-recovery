package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.EmcDataCorrelationService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/data-correlations")
@GikamBean
public interface EmcDataCorrelationResource extends GenericResource<EmcDataCorrelationService, EmcDataCorrelationBean, Long> {
    Page<EmcDataCorrelationItemBean> selectDataCorrelationItemByCorrelationId(@PathVariable Long id, RestJsonWrapperBean wrapper);
    Page<EmcDataCorrelationItemBean> selectChooseableDataCorrelationItem(@PathVariable Long id, RestJsonWrapperBean wrapper);
    void updateDataCorrelationItem(@PathVariable Long id, RestJsonWrapperBean wrapper);
}
