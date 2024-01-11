package com.sunwayworld.emc.module.item.homeattachment.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

@RequestMapping("/secure/emc/module/item/home-attachments/page")
public interface EmcHomeAttachmentController extends GenericEditListPageController {

    @RequestMapping("/choose-date-detail")
    String chooseDateDetailPage();
}
