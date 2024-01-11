package com.sunwayworld.emc.module.bp.mobile.bean;

import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author zhengxy@sunwayworld.com 2020年09月09日
 */
public class EmcMobileFileDTO extends CoreFileBean {

    private static final long serialVersionUID = -4675106883383838211L;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
