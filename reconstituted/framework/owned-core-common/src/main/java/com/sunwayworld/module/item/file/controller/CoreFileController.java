/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/item/files/page"})
public interface CoreFileController {
    public String uploadPage();

    public String viewPage();

    public String viewByAppPage();

    public String openFilePage();

    public String dhtmlxGanttPage();

    public String chemDoodleWebPage();

    public String chemDoodlePage();

    public String bpmnDiagramViewerPage();

    public String bpmnDiagramDesignerPage();

    public String svgViwerPage();

    public String spreadElnPage();

    public String pdfCommentBlankPage();
}
