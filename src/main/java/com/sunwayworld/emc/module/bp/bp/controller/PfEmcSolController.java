package com.sunwayworld.emc.module.bp.bp.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/27 14:58
 */
@RequestMapping("/secure/emc/module/bp/sols/page")
public interface PfEmcSolController {
    @RequestMapping("/sol-record-batch")
    String solRecordBatchPage();

    @RequestMapping("/invalid-results")
    String solInvalidResultPage();

}
