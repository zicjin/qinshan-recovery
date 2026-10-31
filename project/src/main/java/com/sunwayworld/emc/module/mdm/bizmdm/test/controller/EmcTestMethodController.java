package com.sunwayworld.emc.module.mdm.bizmdm.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@RequestMapping("/secure/emc/module/mdm/bizmdm/tests/page")
public interface EmcTestMethodController extends GenericEditListPageController, GenericChoosePageController {
    @RequestMapping("/update-list")
    public String updateListPage();

    @RequestMapping("/detail")
    public String detailPage();

    @RequestMapping("/item-detail")
    public String itemDetailPage();
    
    @RequestMapping("/item-update-detail")
    public String itemUpdateDetailPage();

    @RequestMapping("/item-choose-list")
    public String itemChoosePage();

    @RequestMapping("/item-group-choose-list")
    public String itemGroupChoosePage();

    @RequestMapping("/item-result-choose-list")
    public String itemResultChoosePage();

    @RequestMapping("/script-detail")
    String scriptPage();

    @RequestMapping("/item-result-list")
    String resultListPage();

    @RequestMapping("/item-qcindex-revision-list")
    String qcindexRevisionListPage();

    @RequestMapping("/copy")
    String copyPage();

    @RequestMapping("/item-copy")
    String copyItemPage();

    @RequestMapping("/item-batch")
    String batchItemPage();

    @RequestMapping("/test-edit-list")
    String testEditListPage();

    @RequestMapping("/test-choose-list")
    String testChoosePage();

    @RequestMapping("/item-copy-to")
    String itemCopyToPage();

    @RequestMapping("/group-item-choose-list")
    String groupItemChoosePage();

    @RequestMapping("/tc-test-method-choose-list")
    String tcTestMethodChoosePage();

    @RequestMapping("/check-method-edit-test-method-choose-list")
    public String checkMethodEditChoosePage();
    
    @RequestMapping("/method-param-detail")
    String methodParamDetailPage();
    
    @RequestMapping("/result-run/method-param-detail")
    String resultRunMethodParamDetailPage();
    
    @RequestMapping("/test-method-log")
    String testMethodLogPage();
    
    @RequestMapping("/qcindex-batch")
    String batchQcIndexPage();

    @RequestMapping("/beforehandle-choose-list")
    public String testMethodBeforeHandleChoosePage();

    @RequestMapping("/beforehandle-edit-list")
    public String testMethodBeforeHandleEditPage();

}
