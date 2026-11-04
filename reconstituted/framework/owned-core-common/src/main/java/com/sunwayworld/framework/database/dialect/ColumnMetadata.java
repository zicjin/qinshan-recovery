/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.dialect;

public class ColumnMetadata {
    private String columnName;
    private DataType dataType;
    private Integer dataLength;
    private Integer dataScale;
    private boolean nullable = true;
    private String comments;

    private ColumnMetadata() {
    }

    public static ColumnMetadata of(String columnName, String typeName) {
        return ColumnMetadata.of(columnName, DataType.getByName(typeName));
    }

    public static ColumnMetadata of(String columnName, DataType dataType) {
        ColumnMetadata instance = new ColumnMetadata();
        instance.setColumnName(columnName);
        instance.setDataType(dataType);
        instance.setDataLength(dataType.getDefaultLength());
        instance.setDataScale(dataType.getDefaultScale());
        return instance;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Integer getDataLength() {
        return this.dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    public Integer getDataScale() {
        return this.dataScale;
    }

    public void setDataScale(Integer dataScale) {
        this.dataScale = dataScale;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public static enum DataType {
        STRING(96, 0),
        INTEGER(0, 0),
        DOUBLE(16, 6),
        FLOAT(16, 6),
        CLOB(0, 0),
        DATE(0, 0),
        DATETIME(0, 0);

        private int defaultLength;
        private int defaultScale;

        private DataType(int defaultLength, int defaultScale) {
            this.defaultLength = defaultLength;
            this.defaultScale = defaultScale;
        }

        public int getDefaultLength() {
            return this.defaultLength;
        }

        public void setDefaultLength(int defaultLength) {
            this.defaultLength = defaultLength;
        }

        public int getDefaultScale() {
            return this.defaultScale;
        }

        public void setDefaultScale(int defaultScale) {
            this.defaultScale = defaultScale;
        }

        public static DataType getByName(String name) {
            if ("localdate".equals(name)) {
                return DATE;
            }
            if ("localdatetime".equals(name)) {
                return DATETIME;
            }
            for (DataType dataType : DataType.values()) {
                if (!dataType.name().equalsIgnoreCase(name)) continue;
                return dataType;
            }
            return null;
        }
    }
}
