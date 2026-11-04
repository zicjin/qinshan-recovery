/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreAiTemplateScanTitleDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -8222459071257718013L;
    private String id;
    private String originWordsLen;
    private String words;
    private Long top;
    private Long left;
    private Long width;
    private Long height;
    private String error;

    public String getOriginWordsLen() {
        return this.originWordsLen;
    }

    public void setOriginWordsLen(String originWordsLen) {
        this.originWordsLen = originWordsLen;
    }

    public String getWords() {
        return this.words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public Long getTop() {
        return this.top;
    }

    public void setTop(Long top) {
        this.top = top;
    }

    public Long getLeft() {
        return this.left;
    }

    public void setLeft(Long left) {
        this.left = left;
    }

    public Long getWidth() {
        return this.width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return this.height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
