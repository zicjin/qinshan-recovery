/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Orderable;

@Table(value="T_CORE_SPECIAL_CHARACTER")
public class CoreSpecialCharacterBean
extends AbstractInsertable<Long>
implements Orderable<Long> {
    @Transient
    private static final long serialVersionUID = -7809970474926930883L;
    @Id
    private Long id;
    private String specialCharacter;
    private String category;
    private String characterCode;
    private String pronunciation;
    private String remark;
    @NotNull(defaultValue="1")
    private Long orderNo;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialCharacter() {
        return this.specialCharacter;
    }

    public void setSpecialCharacter(String specialCharacter) {
        this.specialCharacter = specialCharacter;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCharacterCode() {
        return this.characterCode;
    }

    public void setCharacterCode(String characterCode) {
        this.characterCode = characterCode;
    }

    public String getPronunciation() {
        return this.pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
