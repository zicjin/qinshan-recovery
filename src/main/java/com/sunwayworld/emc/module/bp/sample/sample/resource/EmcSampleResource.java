package com.sunwayworld.emc.module.bp.sample.sample.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/sample/samples")
public interface EmcSampleResource extends GenericResource<EmcSampleService, EmcSampleBean, Long>, GenericAuditableResource<EmcSampleService, EmcSampleBean, Long> {

    void addOrder(Long id, RestJsonWrapperBean wrapper);

    void updateOrderSampleDateTime(Long id, RestJsonWrapperBean wrapper);

    void deleteOrder(RestJsonWrapperBean wrapper);

    void changeResult(RestJsonWrapperBean wrapper);

    void accept(RestJsonWrapperBean wrapper);

    void mergeSample(RestJsonWrapperBean wrapper);

    Page<EmcItemBean> selectEditItem(Long id);

    Long editItem(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectAllPagination(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskBySampleId(Long id, RestJsonWrapperBean wrapper);

    void batchInputOrderTaskResult(RestJsonWrapperBean wrapper);

    void batchUpdateFileBizCategory(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectAssignSamplePagination(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskBySample(RestJsonWrapperBean wrapper);
    
    void bizUndo(RestJsonWrapperBean wrapper);

    Long resultInput(RestJsonWrapperBean wrapper);

    void acceptedUndo(RestJsonWrapperBean wrapper);

}
