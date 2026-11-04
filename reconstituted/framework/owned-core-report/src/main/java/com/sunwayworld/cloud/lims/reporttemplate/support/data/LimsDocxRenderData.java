/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.deepoove.poi.data.DocxRenderData;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class LimsDocxRenderData
extends DocxRenderData {
    private Boolean breakPage;
    private Integer pageBreakLevel = 7;

    public LimsDocxRenderData(File docx) {
        super(docx);
    }

    public LimsDocxRenderData(File docx, List<?> renderDatas) {
        super(docx, renderDatas);
    }

    public LimsDocxRenderData(InputStream inputStream) {
        super(inputStream);
    }

    public LimsDocxRenderData(InputStream inputStream, List<?> renderDatas) {
        super(inputStream, renderDatas);
    }

    public LimsDocxRenderData(byte[] input, List<?> renderDatas) {
        super(input, renderDatas);
    }

    public Boolean getBreakPage() {
        return this.breakPage;
    }

    public void setBreakPage(Boolean breakPage) {
        this.breakPage = breakPage;
    }

    public Integer getPageBreakLevel() {
        return this.pageBreakLevel;
    }

    public void setPageBreakLevel(Integer pageBreakLevel) {
        this.pageBreakLevel = pageBreakLevel;
    }
}
