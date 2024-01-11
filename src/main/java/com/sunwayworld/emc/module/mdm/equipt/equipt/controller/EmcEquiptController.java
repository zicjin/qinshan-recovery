package com.sunwayworld.emc.module.mdm.equipt.equipt.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 容器类型 Controller
 *
 */
@RequestMapping("/secure/emc/module/mdm/equipt/equipts/page")
public interface EmcEquiptController extends GenericEditListPageController, GenericDetailPageController, GenericChoosePageController {

    String planAddPage();

    String perCheckAddPage();

    String recordAddPage();

    String perCheckRecordAddPage();

    String useRecordUpdatePage();

    String batchUpdateRecordDetailPage();

    String useRecordEditListPage();

    String caliChoosePage();
}
