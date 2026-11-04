/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.controller.CoreFileController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreFileControllerImpl
implements CoreFileController {
    @Override
    @Log(value="\u8df3\u8f6c\u81f3\u9644\u4ef6\u4e0a\u4f20\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/upload"})
    public String uploadPage() {
        return "module/item/files/fileUpload";
    }

    @Override
    @Log(value="\u8df3\u8f6c\u81f3\u6587\u4ef6\u9884\u89c8\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/view"})
    public String viewPage() {
        return "module/item/files/view";
    }

    @Override
    @Log(value="App\u7aef\u8df3\u8f6c\u81f3\u6587\u4ef6\u9884\u89c8\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/viewByApp"})
    public String viewByAppPage() {
        return "module/item/files/viewByApp";
    }

    @Override
    @Log(value="\u8df3\u8f6c\u81f3\u6253\u5f00\u6587\u4ef6\u64cd\u4f5c\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/open-file"})
    public String openFilePage() {
        return "module/item/files/open-file";
    }

    @Override
    @Log(value="\u8df3\u8f6c\u81f3\u7518\u7279\u56fe\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/dhtmlx-gantt"})
    public String dhtmlxGanttPage() {
        return "module/item/files/dhtmlx-gantt";
    }

    @Override
    @Log(value="\u8df3\u8f6c\u81f3\u5316\u5b66\u5206\u5b50\u5f0f\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/chem-doodle-web"})
    public String chemDoodleWebPage() {
        return "module/item/files/chem-doodle-web";
    }

    @Override
    @Log(value="\u8df3\u8f6c\u81f3\u5316\u5b66\u5206\u5b50\u5f0f\u7f16\u8f91\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/chem-doodle"})
    public String chemDoodlePage() {
        return "module/item/files/chem-doodle";
    }

    @Override
    @Log(value="\u8df3\u8f6c\u81f3BPMN\u9884\u89c8\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/bpmn-diagram-viewer"})
    public String bpmnDiagramViewerPage() {
        return "module/item/files/bpmn-diagram-viewer";
    }

    @Override
    @Log(value="\u8df3\u8f6c\u81f3BPMN\u7f16\u8f91\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/bpmn-diagram-designer"})
    public String bpmnDiagramDesignerPage() {
        return "module/item/files/bpmn-diagram-designer";
    }

    @Override
    @Log(value="SVG\u663e\u793a\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/svg-viewer"})
    public String svgViwerPage() {
        return "module/item/files/svg-viewer";
    }

    @Override
    @Log(value="Eln\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/spread-eln"})
    public String spreadElnPage() {
        return "module/gikam/eln/index";
    }

    @Override
    @Log(value="pdf\u6279\u6ce8\u9884\u89c8\u9875\u9762", type=LogType.CONTROLLER)
    @RequestMapping(value={"/pdf-comment-blank"})
    public String pdfCommentBlankPage() {
        return "module/item/files/pdf-comment-blank";
    }
}
