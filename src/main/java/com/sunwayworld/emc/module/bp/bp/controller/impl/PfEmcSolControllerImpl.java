package com.sunwayworld.emc.module.bp.bp.controller.impl;

import com.sunwayworld.emc.module.bp.bp.controller.PfEmcSolController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/27 14:59
 */
@Controller
@GikamBean
public class PfEmcSolControllerImpl implements PfEmcSolController {
    @Log(value = "标定原始溶液批量录入", type = LogType.CONTROLLER)
    @Override
    public String solRecordBatchPage() {
        return "emc/module/bp/sols/emc-sol-record-batch";
    }

    @Log(value = "标定原始溶液无效结果")
    @Override
    public String solInvalidResultPage() {
        return "/emc/module/bp/sols/emc-sol-detail-record-invalid-result";
    }
}
