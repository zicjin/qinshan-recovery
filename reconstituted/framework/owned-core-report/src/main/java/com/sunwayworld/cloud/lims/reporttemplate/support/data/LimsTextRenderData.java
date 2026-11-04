/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;

public class LimsTextRenderData
extends TextRenderData {
    private Boolean checkBox;
    private Boolean checked;
    private String separator;
    private Boolean isFirst;

    public LimsTextRenderData(String text) {
        this.text = text;
    }

    public LimsTextRenderData(String color, String text) {
        this.style = Style.builder().buildColor(color).build();
        this.text = text;
    }

    public LimsTextRenderData(String text, Style style) {
        this.style = style;
        this.text = text;
    }

    public LimsTextRenderData(String text, boolean checkBox, boolean checked) {
        this.text = text;
        this.checkBox = checkBox;
        this.checked = checked;
    }

    public Boolean getCheckBox() {
        return this.checkBox;
    }

    public void setCheckBox(Boolean checkBox) {
        this.checkBox = checkBox;
    }

    public Boolean getChecked() {
        return this.checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getSeparator() {
        return this.separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Boolean getFirst() {
        return this.isFirst;
    }

    public void setFirst(Boolean first) {
        this.isFirst = first;
    }
}
