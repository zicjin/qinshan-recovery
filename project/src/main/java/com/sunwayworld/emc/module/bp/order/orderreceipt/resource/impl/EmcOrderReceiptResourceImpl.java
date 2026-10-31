package com.sunwayworld.emc.module.bp.order.orderreceipt.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.order.orderreceipt.resource.EmcOrderReceiptResource;
import com.sunwayworld.emc.module.bp.order.orderreceipt.service.EmcOrderReceiptService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月14日
 */
@LogModule(value = "样品接收")
@RestController
@GikamBean
public class EmcOrderReceiptResourceImpl implements EmcOrderReceiptResource {

    @Autowired
    private EmcOrderReceiptService orderReceiptService;

    public EmcOrderReceiptService getService() {
        return this.orderReceiptService;
    }

    @Override
    @Log(value = "查询QC类型列表数据", type = LogType.SELECT)
    public Page<EmcQcCategoryBean> selectQcCategoryList(String category, RestJsonWrapperBean wrapper) {
        return this.getService().selectQcCategoryList(category, wrapper);
    }

    @Override
    @Log(value = "复制为QC样", type = LogType.INSERT)
    public void copyQcOrder(RestJsonWrapperBean wrapper) {
        this.getService().copyQcOrder(wrapper);
    }

    @Override
    @Log(value = "删除QC样", type = LogType.DELETE)
    public void deleteQcOrder(RestJsonWrapperBean wrapper) {
        this.getService().deleteQcOrder(wrapper);
    }

    @Override
    @Log(value = "匹配条形码", type = LogType.INSERT)
    public Integer matchBarcode(RestJsonWrapperBean wrapper) {
        return this.getService().matchBarcode(wrapper);
    }

    @Override
    @Log(value = "留样", type = LogType.INSERT)
    public void retainOrder(RestJsonWrapperBean wrapper) {
        this.getService().retainOrder(wrapper);
    }

    @Override
    @Log(value = "删除样品", type = LogType.DELETE)
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        this.getService().deleteOrder(wrapper);
    }
}
