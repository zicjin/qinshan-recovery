/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.bean;

import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorePageConfigDTO
implements Serializable {
    private static final long serialVersionUID = -316108098303546019L;
    private Map<String, CorePageGridFieldConfigDTO> grid = new HashMap<String, CorePageGridFieldConfigDTO>();

    public Map<String, CorePageGridFieldConfigDTO> getGrid() {
        return this.grid;
    }

    public void setGrid(Map<String, CorePageGridFieldConfigDTO> grid) {
        this.grid = grid;
    }

    public void addGrid(String gridId, CorePageGridFieldConfigBean config) {
        CorePageGridFieldConfigDTO dto = this.grid.get(gridId);
        if (dto == null) {
            dto = new CorePageGridFieldConfigDTO();
            dto.addColumn(config);
            this.grid.put(gridId, dto);
        } else {
            dto.addColumn(config);
        }
    }

    public static class CorePageGridFieldConfigDTO
    implements Serializable {
        private static final long serialVersionUID = -1749777835254967046L;
        private List<CorePageGridFieldConfigBean> columns = new ArrayList<CorePageGridFieldConfigBean>();

        public List<CorePageGridFieldConfigBean> getColumns() {
            return this.columns;
        }

        public void setColumns(List<CorePageGridFieldConfigBean> columns) {
            this.columns = columns;
        }

        public void addColumn(CorePageGridFieldConfigBean config) {
            this.columns.add(config);
        }
    }
}
