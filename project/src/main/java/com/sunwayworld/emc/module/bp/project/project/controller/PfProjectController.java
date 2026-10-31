package com.sunwayworld.emc.module.bp.project.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhengxy@sunwayworld.com 2021年11月24日
 */
@RequestMapping("/secure/emc/module/bp/project/projects/page")
public interface PfProjectController extends EmcProjectController {

    @RequestMapping("/shuttle-choose-list")
    String shuttleChoosePage();

    @RequestMapping("/report-choose-list")
    String reportFileChoosePage();

    @RequestMapping("/project-annex-list")
    String projectAnnexListPage();

    @RequestMapping("/deliver-annex-list")
    String deliverAnnexListPage();

    @RequestMapping("/report-line-choose-list")
    String projectLineChoosePage();

    @RequestMapping("/proj-order-choose-list")
    String projOrderChoosePage();

    @RequestMapping("/order-itemgroup-choose-list")
    String orderItemGroupChoosePage();

    @RequestMapping("/proj-order-order-choose-list")
    String projOrderOrderChoosePage();

    @RequestMapping("/order-itemgroup-order-choose-list")
    String orderItemGroupOrderChoosePage();

    @RequestMapping("/update-order-index")
    String updateOrderIndexPage();
}
