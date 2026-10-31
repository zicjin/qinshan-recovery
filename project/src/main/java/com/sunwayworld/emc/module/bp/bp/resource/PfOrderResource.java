package com.sunwayworld.emc.module.bp.bp.resource;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.resource.EmcOrderResource;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.data.page.Page;


/**
 * @author licm@sunwayworld.com
 * @date 2021-11-09
 */
@RequestMapping("/secure/emc/module/bp/bp/orders")
public interface PfOrderResource extends EmcOrderResource{

    void updateScanStatus(RestJsonWrapperBean jsonWrapper);

    void undoScanned(RestJsonWrapperBean jsonWrapper);

    void syncNewOrder(RestJsonWrapperBean wrapper);

    void syncNewTest(RestJsonWrapperBean wrapper);

    void deleteSyncOrder(RestJsonWrapperBean wrapper);

    void deleteSyncTest(RestJsonWrapperBean wrapper);

    void afterDeleteTest(RestJsonWrapperBean wrapper);

    void fixDuplicateOrderTask(RestJsonWrapperBean wrapper);

    void subpackageTest(RestJsonWrapperBean wrapper);

    void cancelSubpackageTest(RestJsonWrapperBean wrapper);

    void editQc(RestJsonWrapperBean wrapper);

    void editParentOrderNo(RestJsonWrapperBean wrapper);

    void resetParentOrderNo(RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupBean> selectItemGroupPagationByProjId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupBean> selectItemGroupPagationByOrderId(Long id, RestJsonWrapperBean wrapper);

    void changeIsConvert(RestJsonWrapperBean wrapper);

    void addPhFlag(RestJsonWrapperBean wrapper);

    void delPhFlag(RestJsonWrapperBean wrapper);

    Page<EmcOrderContainerBean> selectContainerPagination(RestJsonWrapperBean wrapper);

    Page<EmcOrderContainerBean> selectOrderReceiveContainerList(RestJsonWrapperBean wrapper);

    void addParamOrder(RestJsonWrapperBean wrapper);

    void deleteParamOrder(RestJsonWrapperBean wrapper);

    void updateOrderIndex(RestJsonWrapperBean wrapper);
}
