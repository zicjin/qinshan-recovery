/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class LcdpBizProcessPositionDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -6468344102070351710L;
    private int x;
    private int y;

    public LcdpBizProcessPositionDTO(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
