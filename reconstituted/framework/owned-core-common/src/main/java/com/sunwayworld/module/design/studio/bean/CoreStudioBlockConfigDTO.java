/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.bean;

import com.sunwayworld.module.design.studio.bean.CoreStudioBlockFormDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockGridDTO;
import java.io.Serializable;
import java.util.Map;

public class CoreStudioBlockConfigDTO
implements Serializable {
    private static final long serialVersionUID = 4128968645749345314L;
    private Map<String, CoreStudioBlockFormDTO> form;
    private Map<String, CoreStudioBlockGridDTO> grid;

    public Map<String, CoreStudioBlockFormDTO> getForm() {
        return this.form;
    }

    public void setForm(Map<String, CoreStudioBlockFormDTO> form) {
        this.form = form;
    }

    public Map<String, CoreStudioBlockGridDTO> getGrid() {
        return this.grid;
    }

    public void setGrid(Map<String, CoreStudioBlockGridDTO> grid) {
        this.grid = grid;
    }
}
