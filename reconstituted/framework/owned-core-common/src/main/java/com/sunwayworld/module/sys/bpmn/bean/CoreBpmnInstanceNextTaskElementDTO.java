/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.io.Serializable;
import java.util.List;
import org.dom4j.Element;

public class CoreBpmnInstanceNextTaskElementDTO<ID extends Serializable>
extends AbstractBaseData {
    private static final long serialVersionUID = -2166409763550593072L;
    private ID id;
    private Element sourceElement;
    private List<Element> nextTaskElementList;

    public ID getId() {
        return this.id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public Element getSourceElement() {
        return this.sourceElement;
    }

    public void setSourceElement(Element sourceElement) {
        this.sourceElement = sourceElement;
    }

    public List<Element> getNextTaskElementList() {
        return this.nextTaskElementList;
    }

    public void setNextTaskElementList(List<Element> nextTaskElementList) {
        this.nextTaskElementList = nextTaskElementList;
    }
}
