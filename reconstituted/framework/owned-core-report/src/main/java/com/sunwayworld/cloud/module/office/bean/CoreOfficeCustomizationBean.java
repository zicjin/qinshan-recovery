/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_OFFICE_CUSTOMIZATION")
public class CoreOfficeCustomizationBean
extends AbstractPersistable<String>
implements Persistable<String> {
    @Transient
    private static final long serialVersionUID = 3933136396251482263L;
    @Id
    private String id;
    private String address;
    private String info;
    private String mail;
    private String name;
    private String www;
    private String logo;
    private String aboutLogo;
    private String downloadAs;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWww() {
        return this.www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAboutLogo() {
        return this.aboutLogo;
    }

    public void setAboutLogo(String aboutLogo) {
        this.aboutLogo = aboutLogo;
    }

    public String getDownloadAs() {
        return this.downloadAs;
    }

    public void setDownloadAs(String downloadAs) {
        this.downloadAs = downloadAs;
    }
}
