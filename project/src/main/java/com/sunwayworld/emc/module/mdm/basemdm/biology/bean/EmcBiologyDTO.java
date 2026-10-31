package com.sunwayworld.emc.module.mdm.basemdm.biology.bean;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class EmcBiologyDTO extends AbstractTreeNode {

    private static final long serialVersionUID = 5974818253554217682L;
    private String id; // ID
    private String speciesName; // 物种名称

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

}