package com.sunwayworld.emc.module.bp.sample.sample.resource;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author luoc@sunwayworld.com 2021/3/26
 */
@RequestMapping("/secure/emc/module/bp/sample/samples")
public interface PfSampleResource extends EmcSampleResource {

    String selectCategoryCode(RestJsonWrapperBean wrapper);

    void syncTestItem(RestJsonWrapperBean wrapper);

    String submitReminds(RestJsonWrapperBean wrapper);

    void submitDelivery(RestJsonWrapperBean wrapper);

    void invalidWeightResult(RestJsonWrapperBean wrapper);

    void undoAccept(RestJsonWrapperBean wrapper);

    void syncAssignDate(RestJsonWrapperBean wrapper);

    void confirmAddTest(RestJsonWrapperBean wrapper);

    String passReminds(RestJsonWrapperBean wrapper);

    void handSample(RestJsonWrapperBean wrapper);

    void partHandover(RestJsonWrapperBean wrapper);

    EmcSampleBean selectSampleRecordDetail(RestJsonWrapperBean wrapper);
}
