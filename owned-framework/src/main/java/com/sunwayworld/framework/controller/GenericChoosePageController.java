package com.sunwayworld.framework.controller;

import com.sunwayworld.framework.exception.NotImplementedException;

@org.springframework.stereotype.Controller
public interface GenericChoosePageController {
    @org.springframework.web.bind.annotation.RequestMapping("/choose-list")
    String choosePage();
    @org.springframework.web.bind.annotation.RequestMapping("/simple-choose-list")
    default String simpleChoosePage() { throw new NotImplementedException(); }
}
