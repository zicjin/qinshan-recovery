/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.context;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ColumnContext
implements Serializable {
    private static final long serialVersionUID = 1534955388738109831L;
    private static final List<String> IMMUTABLE_COLUMN_LIST = Arrays.asList("id", "createdbyid", "createdbyname", "createdbyorgid", "createdbyorgname", "createdtime");
    private String columnName;
    private String rawColumnName;
    private String dataType;
    private boolean clob = false;
    private boolean blob = false;
    private String defaultValue;
    private int precision;
    private int scale;
    private Class<?> type;
    private boolean notNull = false;
    private boolean timestamp = false;

    public boolean isImmutable() {
        return IMMUTABLE_COLUMN_LIST.contains(this.columnName.toLowerCase());
    }

    public Object parse(Object rawValue) {
        if (rawValue == null) {
            return null;
        }
        if (this.type.equals(rawValue.getClass())) {
            return rawValue;
        }
        if (Long.class.equals(this.type)) {
            if (ObjectUtils.isEmpty(rawValue)) {
                return null;
            }
            return NumberUtils.parseLong(rawValue);
        }
        if (Double.class.equals(this.type)) {
            if (ObjectUtils.isEmpty(rawValue)) {
                return null;
            }
            return NumberUtils.parseDouble(rawValue);
        }
        if (LocalDate.class.isAssignableFrom(this.type)) {
            return DateTimeUtils.parseLocalDate(rawValue.toString());
        }
        if (LocalTime.class.isAssignableFrom(this.type)) {
            return DateTimeUtils.parseLocalTime(rawValue.toString());
        }
        if (LocalDateTime.class.isAssignableFrom(this.type)) {
            return DateTimeUtils.parseLocalDateTime(rawValue.toString());
        }
        return ObjectUtils.toString(rawValue);
    }

    public Object getValue(Object entityItem) {
        if (entityItem == null) {
            return null;
        }
        if (entityItem instanceof Map) {
            return this.parse(CollectionUtils.getValueIgnorecase((Map)entityItem, this.columnName));
        }
        BeanPropertyDescriptor pd = BeanPropertyDescriptor.of(ClassUtils.getRawType(entityItem.getClass()), this.columnName);
        if (pd != null) {
            return this.parse(pd.getPropertyValue(entityItem));
        }
        String value = ((Persistable)entityItem).getExt$().get(this.columnName);
        return this.parse(value);
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getRawColumnName() {
        return this.rawColumnName;
    }

    public void setRawColumnName(String rawColumnName) {
        this.rawColumnName = rawColumnName;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isClob() {
        return this.clob;
    }

    public void setClob(boolean clob) {
        this.clob = clob;
    }

    public boolean isBlob() {
        return this.blob;
    }

    public void setBlob(boolean blob) {
        this.blob = blob;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
    }
}
