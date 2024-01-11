package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhengxy@sunwayworld.com 2023年09月08日
 */
public interface PfEquiptAcqScriptController extends EmcEquiptAcqScriptController {

    @RequestMapping("/eas-result-choose-list")
    String easResultChoosePage();
}
