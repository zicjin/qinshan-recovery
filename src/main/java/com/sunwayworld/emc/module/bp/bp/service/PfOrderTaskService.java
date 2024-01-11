package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface PfOrderTaskService extends EmcOrderTaskService {

    void updateWeighingDate(RestJsonWrapperBean wrapper);

    void updateWeighingEndDate(RestJsonWrapperBean wrapper);

    void updateVocBottle(RestJsonWrapperBean wrapper);

    void batchAutoUpdateVocBottle(RestJsonWrapperBean wrapper);

    void updateBatchNo(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUniqueOrderTaskList(RestJsonWrapperBean wrapper);

    void batchUpdateForOrder(RestJsonWrapperBean wrapper);

    void batchUpdateOrderTask(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUniqueOrderList(RestJsonWrapperBean wrapper);

    List<EmcOrderTaskResultBean> getInsertOrderTaskResultList(EmcOrderTaskBean orderTask, List<EmcItemBean> itemList, EmcOrderBean order);

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
