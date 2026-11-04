/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.matrix.bean;

import com.sunwayworld.framework.support.matrix.MatrixHelper;
import java.io.Serializable;
import java.util.Map;

public class MatrixHeaderBean
implements Serializable {
    private static final long serialVersionUID = 2467652929645073683L;
    private String field;
    private String title;
    private Double heightTitle;
    private String javaType = String.class.getSimpleName();
    private String type;
    private String category;
    private Map<String, Object> param;
    private boolean editor = true;
    private boolean readonly = false;

    private MatrixHeaderBean() {
    }

    public static MatrixHeaderBean getInstance(String field, String title) {
        MatrixHeaderBean header = new MatrixHeaderBean();
        header.setField(field);
        header.setTitle(title);
        return header;
    }

    public static <PK> MatrixHeaderBean getRotateInstance(String table, PK id, String field, String title) {
        MatrixHeaderBean header = new MatrixHeaderBean();
        header.setField(MatrixHelper.getRotatedFieldName(table, id, field));
        header.setTitle(title);
        return header;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getField() {
        return this.field;
    }

    public String getTitle() {
        return this.title;
    }

    public String getJavaType() {
        return this.javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public static long getSerialversionuid() {
        return 2467652929645073683L;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isEditor() {
        return this.editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }

    public Double getHeightTitle() {
        return this.heightTitle;
    }

    public void setHeightTitle(Double heightTitle) {
        this.heightTitle = heightTitle;
    }
}
