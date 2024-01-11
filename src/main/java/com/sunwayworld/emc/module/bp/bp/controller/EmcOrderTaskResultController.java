package com.sunwayworld.emc.module.bp.bp.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/secure/emc/module/bp/bp/order-task-results/page")
public interface EmcOrderTaskResultController {
    @RequestMapping("/data-summary")
    String dataSummaryPage();
}
