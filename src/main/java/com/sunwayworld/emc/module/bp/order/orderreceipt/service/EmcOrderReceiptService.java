package com.sunwayworld.emc.module.bp.order.orderreceipt.service;

import org.springframework.web.bind.annotation.PathVariable;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月14日
 */
public interface EmcOrderReceiptService {

    Page<EmcQcCategoryBean> selectQcCategoryList(@PathVariable String category, RestJsonWrapperBean wrapper);

    /**
     * 复制为QC样
     * @param wrapper
     */
    void copyQcOrder(RestJsonWrapperBean wrapper);

    /**
     * 删除QC样
     * @param wrapper
     */
    void deleteQcOrder(RestJsonWrapperBean wrapper);

    /**
     * 匹配样品条形码
     * @param wrapper
     * @return
     */
    Integer matchBarcode(RestJsonWrapperBean wrapper);

    /**
     * 样品留样
     * @param wrapper
     */
    void retainOrder(RestJsonWrapperBean wrapper);

    /**
     * 删除样品
     * @param wrapper
     */
    void deleteOrder(RestJsonWrapperBean wrapper);

}
