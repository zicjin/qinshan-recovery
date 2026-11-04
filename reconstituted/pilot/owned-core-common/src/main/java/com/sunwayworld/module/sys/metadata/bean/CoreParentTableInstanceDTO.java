/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreParentTableInstanceDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 6507466274261941254L;
    private String sourceId;
    private String table;
    private String id;

    public String getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public static long getSerialversionuid() {
        return 6507466274261941254L;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
