package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
public interface PfOrderService extends EmcOrderService {

    void copyOrderItemGroup(List<EmcOrderItemGroupBean> copiedOrderItemGroupList, EmcOrderBean copiedOrder, Long newOrderId, List<EmcOrderItemGroupBean> newOrderItemGroupList);

    void updateScanStatus(RestJsonWrapperBean jsonWrapper);

    void undoScanned(RestJsonWrapperBean jsonWrapper);

    void syncNewOrder(RestJsonWrapperBean wrapper);

    void syncNewTest(RestJsonWrapperBean wrapper);

    void deleteSyncOrder(RestJsonWrapperBean wrapper);

    void deleteSyncTest(RestJsonWrapperBean wrapper);

    void deleteSyncTest(List<EmcOrderItemGroupBean> orderItemGroupList);

    void fixDuplicateOrderTask(RestJsonWrapperBean wrapper);

    void afterDeleteTest(RestJsonWrapperBean wrapper);

    void subpackageTest(RestJsonWrapperBean wrapper);

    void cancelSubpackageTest(RestJsonWrapperBean wrapper);

    void editQc(RestJsonWrapperBean wrapper);

    void editParentOrderNo(RestJsonWrapperBean wrapper);

    void resetParentOrderNo(RestJsonWrapperBean wrapper);

    void changeIsConvert(RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupBean> selectItemGroupPagationByProjId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupBean> selectItemGroupPagationByOrderId(Long id, RestJsonWrapperBean wrapper);

    void addPhFlag(RestJsonWrapperBean wrapper);

    void delPhFlag(RestJsonWrapperBean wrapper);

    Page<EmcOrderContainerBean> selectContainerPagination(RestJsonWrapperBean wrapper);

    void addParamOrder(RestJsonWrapperBean wrapper);

    void deleteParamOrder(RestJsonWrapperBean wrapper);

    void updateOrderIndex(RestJsonWrapperBean wrapper);

    Long updateItemGroupOrderContainer(RestJsonWrapperBean wrapper);

    Long syncOrderItemGroupItem(RestJsonWrapperBean wrapper);

    void delCheckProjectOrderItemGroup(RestJsonWrapperBean wrapper);

    void delOrderItemGroup(RestJsonWrapperBean wrapper);
}
