/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printer.bean;

import java.io.Serializable;

public class CorePrinterPropertiesDTO
implements Serializable {
    private static final long serialVersionUID = 6254434052398190305L;
    private Long fileId;
    private String fileName;
    private String printerName;
    private Integer copies;
    private String layout;
    private String paperSize;
    private String bothSidesPrint;
    private String bothSidesPrintMethod;

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPrinterName() {
        return this.printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public Integer getCopies() {
        return this.copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public String getLayout() {
        return this.layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getPaperSize() {
        return this.paperSize;
    }

    public void setPaperSize(String paperSize) {
        this.paperSize = paperSize;
    }

    public String getBothSidesPrint() {
        return this.bothSidesPrint;
    }

    public void setBothSidesPrint(String bothSidesPrint) {
        this.bothSidesPrint = bothSidesPrint;
    }

    public String getBothSidesPrintMethod() {
        return this.bothSidesPrintMethod;
    }

    public void setBothSidesPrintMethod(String bothSidesPrintMethod) {
        this.bothSidesPrintMethod = bothSidesPrintMethod;
    }
}
