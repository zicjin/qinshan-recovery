/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.io.Serializable;

public class WaterMarkBean
implements Serializable {
    private String text;
    private CoreFileBean pic;
    private String level;
    private String watermarkType;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CoreFileBean getPic() {
        return this.pic;
    }

    public void setPic(CoreFileBean pic) {
        this.pic = pic;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWatermarkType() {
        return this.watermarkType;
    }

    public void setWatermarkType(String watermarkType) {
        this.watermarkType = watermarkType;
    }
}
