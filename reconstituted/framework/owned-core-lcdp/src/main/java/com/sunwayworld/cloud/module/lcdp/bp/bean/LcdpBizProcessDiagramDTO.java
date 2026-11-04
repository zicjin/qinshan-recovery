/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.bean;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessPositionDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LcdpBizProcessDiagramDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -4034941937152704299L;
    private String id;
    private String shape;
    private int width;
    private int height;
    private String label;
    private String source;
    private String target;
    private LcdpBizProcessPositionDTO position;
    private List<LcdpBizProcessPositionDTO> vertices = new ArrayList<LcdpBizProcessPositionDTO>();
    private Map<String, String> data = new HashMap<String, String>();

    public void addVertices(LcdpBizProcessPositionDTO vertice) {
        this.vertices.add(vertice);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShape() {
        return this.shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public LcdpBizProcessPositionDTO getPosition() {
        return this.position;
    }

    public void setPosition(LcdpBizProcessPositionDTO position) {
        this.position = position;
    }

    public List<LcdpBizProcessPositionDTO> getVertices() {
        return this.vertices;
    }

    public void setVertices(List<LcdpBizProcessPositionDTO> vertices) {
        this.vertices = vertices;
    }

    public Map<String, String> getData() {
        return this.data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public void addData(String auditUser) {
        this.data.put("auditUser", auditUser);
    }
}
