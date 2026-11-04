/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreStudioGridDTO
implements Serializable {
    private static final long serialVersionUID = -4091983480284003887L;
    private String gridId;
    private String pageId;
    private List<CoreStudioGridColumnDTO> columnList = new ArrayList<CoreStudioGridColumnDTO>();

    public String getGridId() {
        return this.gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public List<CoreStudioGridColumnDTO> getColumnList() {
        return this.columnList;
    }

    public void setColumnList(List<CoreStudioGridColumnDTO> columnList) {
        this.columnList = columnList;
    }

    public void addColumn(CoreStudioGridColumnDTO column) {
        this.columnList.add(column);
    }

    public static class CoreStudioGridColumnDTO
    implements Serializable {
        private static final long serialVersionUID = -3608197047590873026L;
        private String field;
        private String title;
        private String hidden;

        public static CoreStudioGridColumnDTO of(String field, String title, String hidden) {
            CoreStudioGridColumnDTO column = new CoreStudioGridColumnDTO();
            column.setField(field);
            column.setTitle(title);
            column.setHidden(hidden);
            return column;
        }

        public String getField() {
            return this.field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHidden() {
            return this.hidden;
        }

        public void setHidden(String hidden) {
            this.hidden = hidden;
        }
    }
}
