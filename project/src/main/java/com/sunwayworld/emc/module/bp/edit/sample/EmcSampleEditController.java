package com.sunwayworld.emc.module.bp.edit.sample;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.framework.controller.GenericEditListPageController;

/**
 * 现场数据修改
 * @author zhangsy@sunwayworld.com
 * @date 2019-11-21
 */
@RequestMapping("/secure/emc/module/bp/edit/sample-edits/page")
public interface EmcSampleEditController extends GenericEditListPageController {
    
    @RequestMapping("/cond/query")
    String queryByCondPage();
    
}
