/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import java.time.LocalDateTime;

@Table(value="T_CORE_DV_COEFFICIENT")
public class CoreCoefficientBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    private static final long serialVersionUID = 7997057895592307660L;
    @Id
    private Long id;
    private Double coefficientA;
    private Double coefficientA2;
    private Double coefficientA3;
    private Double coefficientB3;
    private Double coefficientB4;
    private Double coefficientB5;
    private Double coefficientB6;
    private Double coefficientD1;
    private Double coefficientD2;
    private Double coefficientD3;
    private Double coefficientD4;
    private Double coefficientC4;
    private Double coefficientC4Divisor;
    private Double coefficientLowerD2;
    private Double coefficientLowerD2Divisor;
    private String createdById;
    private String createdByName;
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Double getCoefficientA() {
        return this.coefficientA;
    }

    public void setCoefficientA(Double coefficientA) {
        this.coefficientA = coefficientA;
    }

    public Double getCoefficientA2() {
        return this.coefficientA2;
    }

    public void setCoefficientA2(Double coefficientA2) {
        this.coefficientA2 = coefficientA2;
    }

    public Double getCoefficientA3() {
        return this.coefficientA3;
    }

    public void setCoefficientA3(Double coefficientA3) {
        this.coefficientA3 = coefficientA3;
    }

    public Double getCoefficientB3() {
        return this.coefficientB3;
    }

    public void setCoefficientB3(Double coefficientB3) {
        this.coefficientB3 = coefficientB3;
    }

    public Double getCoefficientB4() {
        return this.coefficientB4;
    }

    public void setCoefficientB4(Double coefficientB4) {
        this.coefficientB4 = coefficientB4;
    }

    public Double getCoefficientB5() {
        return this.coefficientB5;
    }

    public void setCoefficientB5(Double coefficientB5) {
        this.coefficientB5 = coefficientB5;
    }

    public Double getCoefficientB6() {
        return this.coefficientB6;
    }

    public void setCoefficientB6(Double coefficientB6) {
        this.coefficientB6 = coefficientB6;
    }

    public Double getCoefficientD1() {
        return this.coefficientD1;
    }

    public void setCoefficientD1(Double coefficientD1) {
        this.coefficientD1 = coefficientD1;
    }

    public Double getCoefficientD2() {
        return this.coefficientD2;
    }

    public void setCoefficientD2(Double coefficientD2) {
        this.coefficientD2 = coefficientD2;
    }

    public Double getCoefficientD3() {
        return this.coefficientD3;
    }

    public void setCoefficientD3(Double coefficientD3) {
        this.coefficientD3 = coefficientD3;
    }

    public Double getCoefficientD4() {
        return this.coefficientD4;
    }

    public void setCoefficientD4(Double coefficientD4) {
        this.coefficientD4 = coefficientD4;
    }

    public Double getCoefficientC4() {
        return this.coefficientC4;
    }

    public void setCoefficientC4(Double coefficientC4) {
        this.coefficientC4 = coefficientC4;
    }

    public Double getCoefficientC4Divisor() {
        return this.coefficientC4Divisor;
    }

    public void setCoefficientC4Divisor(Double coefficientC4Divisor) {
        this.coefficientC4Divisor = coefficientC4Divisor;
    }

    public Double getCoefficientLowerD2() {
        return this.coefficientLowerD2;
    }

    public void setCoefficientLowerD2(Double coefficientLowerD2) {
        this.coefficientLowerD2 = coefficientLowerD2;
    }

    public Double getCoefficientLowerD2Divisor() {
        return this.coefficientLowerD2Divisor;
    }

    public void setCoefficientLowerD2Divisor(Double coefficientLowerD2Divisor) {
        this.coefficientLowerD2Divisor = coefficientLowerD2Divisor;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
