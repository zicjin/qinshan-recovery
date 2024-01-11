package com.sunwayworld.emc.module.item.homeattachment.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

/**
 * @author jinx@sunwayworld.com
 * @date 2020-03-05
 */
@Table("T_EMC_HOME_ATTA_PERMISSION")
public class EmcHomeAttachmentPermissionBean extends AbstractPersistable<Long> implements Persistable<Long> {

    @Transient
    private static final long serialVersionUID = 3377626449381486310L;

    @Id
    private Long id;// 主键

    private Long attachmentId;// 附件信息id

    private String userId;// 可见人编码

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
