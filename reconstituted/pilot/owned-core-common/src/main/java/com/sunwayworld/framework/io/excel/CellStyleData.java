/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel;

import java.io.Serializable;
import java.util.Objects;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

public class CellStyleData
implements Serializable {
    private static final long serialVersionUID = 8606005065183285812L;
    private IndexedColors fillForegroundColor;
    private FillPatternType fillPattern;
    private IndexedColors fontColor;

    public IndexedColors getFillForegroundColor() {
        return this.fillForegroundColor;
    }

    public void setFillForegroundColor(IndexedColors fillForegroundColor) {
        this.fillForegroundColor = fillForegroundColor;
    }

    public FillPatternType getFillPattern() {
        return this.fillPattern;
    }

    public void setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
    }

    public IndexedColors getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(IndexedColors fontColor) {
        this.fontColor = fontColor;
    }

    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }
        if (this == target) {
            return true;
        }
        if (!(target instanceof CellStyleData)) {
            return false;
        }
        CellStyleData cellStyle = (CellStyleData)target;
        if (!Objects.equals((Object)this.getFillForegroundColor(), (Object)cellStyle.getFillForegroundColor())) {
            return false;
        }
        if (!Objects.equals((Object)this.getFillPattern(), (Object)cellStyle.getFillPattern())) {
            return false;
        }
        return Objects.equals((Object)this.getFontColor(), (Object)cellStyle.getFontColor());
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.fillForegroundColor, this.fillPattern, this.fontColor});
    }
}
