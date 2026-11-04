/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import java.awt.Color;
import org.apache.poi.ss.usermodel.Cell;

public class UserCell
implements Comparable<UserCell> {
    private Cell cell;
    private int row;
    private int col;
    private boolean show;
    private String text = "";
    private Color color = null;

    public Cell getCell() {
        return this.cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

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

    public boolean isShow() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int compareTo(UserCell uc) {
        try {
            double meDouble = Double.parseDouble(this.getText().replaceAll("%", ""));
            double heDouble = Double.parseDouble(uc.getText().replaceAll("%", ""));
            if (meDouble < heDouble) {
                return -1;
            }
            if (meDouble > heDouble) {
                return 1;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return 0;
    }
}
