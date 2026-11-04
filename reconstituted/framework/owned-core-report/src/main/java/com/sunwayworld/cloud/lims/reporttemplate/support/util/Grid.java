/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import java.awt.Color;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class Grid {
    private boolean show;
    private int row;
    private int col;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean bold;
    private String text;
    private java.awt.Font font = new java.awt.Font("\u5fae\u8f6f\u96c5\u9ed1", 0, 12);
    private Color bgColor = null;
    private Color ftColor = null;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
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

    public boolean isShow() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getBgColor() {
        return this.bgColor;
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public void setBgColor(org.apache.poi.ss.usermodel.Color color) {
        this.bgColor = this.poiColor2awtColor(color);
    }

    public void setBgColor(Color color) {
        this.bgColor = color;
    }

    public Color getFtColor() {
        return this.ftColor;
    }

    public void setFtColor(org.apache.poi.ss.usermodel.Color color) {
        this.ftColor = this.poiColor2awtColor(color);
    }

    public java.awt.Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        if (font != null) {
            this.font = new java.awt.Font(font.getFontName(), 1, font.getFontHeight() / 20);
        }
    }

    private Color poiColor2awtColor(org.apache.poi.ss.usermodel.Color color) {
        HSSFColor hc;
        short[] s;
        Color awtColor = null;
        if (color instanceof XSSFColor) {
            XSSFColor xc = (XSSFColor)color;
            String rgbHex = xc.getARGBHex();
            if (rgbHex != null) {
                awtColor = new Color(Integer.parseInt(rgbHex.substring(2), 16));
            }
        } else if (color instanceof HSSFColor && (s = (hc = (HSSFColor)color).getTriplet()) != null) {
            awtColor = new Color(s[0], s[1], s[2]);
        }
        return awtColor;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public VerticalAlignment getVerticalAlignment() {
        return this.verticalAlignment;
    }
}
