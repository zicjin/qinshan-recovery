package com.sunwayworld.emc.module.mdm.contract.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhengxy@sunwayworld.com 2023年11月08日
 */
public interface PfContractController extends EmcContractController {

    @RequestMapping("/split-choose-list")
    String splitContractChoosePage();
}
