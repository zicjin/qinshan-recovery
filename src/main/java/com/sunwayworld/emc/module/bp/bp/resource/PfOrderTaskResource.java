package com.sunwayworld.emc.module.bp.bp.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月12日
 */
@RequestMapping("/secure/emc/module/bp/bp/order-tasks")
public interface PfOrderTaskResource extends EmcOrderTaskResource {

    void updateWeighingDate(RestJsonWrapperBean wrapper);

    void updateWeighingEndDate(RestJsonWrapperBean wrapper);

    void updateVocBottle(RestJsonWrapperBean wrapper);

    void batchAutoUpdateVocBottle(RestJsonWrapperBean wrapper);

    void updateBatchNo(RestJsonWrapperBean wrapper);

    void batchUpdateForOrder(RestJsonWrapperBean wrapper);

    void batchUpdateOrderTask(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUniqueOrderPagination(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUniqueItemGroupPagination(RestJsonWrapperBean wrapper);

    String selectStockNoByOrderTask(RestJsonWrapperBean wrapper);

    String selectMatchStockTestMethodId(RestJsonWrapperBean wrapper);

    String selectOverTest(RestJsonWrapperBean wrapper);

    void updateOilFilmNo(RestJsonWrapperBean wrapper);

    void syncOilFilmno(RestJsonWrapperBean wrapper);

    void syncNoEquiptIdTask(RestJsonWrapperBean wrapper);

    void subpackageTest(RestJsonWrapperBean wrapper);

    void cancelSubpackageTest(RestJsonWrapperBean wrapper);

    void updateSampleBottle(RestJsonWrapperBean wrapper);

    void batchAutoUpdateSampleBottle(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectContainerInfo(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectTaskGroupByTest(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectByRunQc(String runId, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderContainerInfo(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskPagination(RestJsonWrapperBean wrapper);
}
