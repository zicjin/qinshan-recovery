package com.sunwayworld.emc.module.bp.sample.sample.service;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

import java.util.List;

/**
 * @author luoc@sunwayworld.com 2021/3/26
 */
public interface PfSampleService extends EmcSampleService {

    String selectCategoryCode(RestJsonWrapperBean wrapper);

    void syncTestItem(RestJsonWrapperBean wrapper);

    String submitReminds(RestJsonWrapperBean wrapper);

    void submitDelivery(RestJsonWrapperBean wrapper);

    void invalidWeightResult(RestJsonWrapperBean wrapper);

    void undoAccept(RestJsonWrapperBean wrapper);

    void syncAssignDate(RestJsonWrapperBean wrapper);

    void confirmAddTest(RestJsonWrapperBean wrapper);

    String passReminds(RestJsonWrapperBean wrapper);

    List<EmcSampleBean> handSample(RestJsonWrapperBean wrapper);

    void partHandover(RestJsonWrapperBean wrapper);

    EmcSampleBean selectSampleRecordDetail(RestJsonWrapperBean wrapper);

    void moveTask(RestJsonWrapperBean wrapper);

    void equiptCollect(RestJsonWrapperBean wrapper);

    void withDraw(RestJsonWrapperBean wrapper);

    void undoHandSample(RestJsonWrapperBean wrapper);

    String exportOriginalRecordExcelZip(RestJsonWrapperBean wrapper);
}
