package com.sunwayworld.framework.controller;
@org.springframework.stereotype.Controller
public interface GenericEditListPageController {
    @org.springframework.web.bind.annotation.RequestMapping("/edit-list")
    String editListPage();
}
