package com.sunwayworld.emc.module.bp.sample.sample.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcSampleService extends GenericService<EmcSampleBean, Long>, GenericAuditableService<EmcSampleBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    void accept(RestJsonWrapperBean jsonWrapper);

    void insertByProject(List<Long> projectIdList);

    void insertByProject(List<Long> projectIdList, String status);

    void addOrder(Long id, RestJsonWrapperBean wrapper);

    void updateOrderSampleDateTime(Long id, RestJsonWrapperBean wrapper);

    void deleteOrder(RestJsonWrapperBean wrapper);

    void changeResult(RestJsonWrapperBean wrapper);

    void mergeSample(RestJsonWrapperBean wrapper);

    Page<EmcItemBean> selectEditItem(Long id);

    Long editItem(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectSampleOverduePagination(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectAllPagination(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectSamplePagination(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectMobileScanner(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectDetailLabel(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectLabelDetail(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskBySampleId(Long id, RestJsonWrapperBean wrapper);

    void batchInputOrderTaskResult(RestJsonWrapperBean wrapper);

    void batchUpdateFileBizCategory(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectAssignSamplePagination(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskBySample(RestJsonWrapperBean wrapper);
    
    void bizUndo(RestJsonWrapperBean wrapper);

    Long resultInput(RestJsonWrapperBean wrapper);

    void acceptedUndo(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectTodoSampleList(RestJsonWrapperBean wrapper);

    Page<EmcSampleBean> selectTodoSampleOrderReceiptList(RestJsonWrapperBean wrapper);

}
