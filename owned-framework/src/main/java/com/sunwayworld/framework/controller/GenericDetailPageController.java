package com.sunwayworld.framework.controller;
@org.springframework.stereotype.Controller
public interface GenericDetailPageController {
    @org.springframework.web.bind.annotation.RequestMapping("/detail")
    String detailPage();
}
