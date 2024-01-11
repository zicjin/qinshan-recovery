package com.sunwayworld.emc.module.mdm.basemdm.mtl.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.controller.EmcMtlController;

public interface PfMtlController extends EmcMtlController {

    @RequestMapping("/new-stock-choose-list")
    String newStockChoosePage();
    
    @RequestMapping("/mtl-check-search-list")
    String mtlCheckSearchList();

    @RequestMapping("/qc-std-stock-choose-list")
    String qcStdStockChoosePage();
}

