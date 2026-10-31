package com.sunwayworld.framework.support.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface Insertable<ID extends Serializable> extends Persistable<ID> {
    String getCreatedById(); void setCreatedById(String value);
    String getCreatedByName(); void setCreatedByName(String value);
    LocalDateTime getCreatedTime(); void setCreatedTime(LocalDateTime value);
    String getCreatedByOrgId(); void setCreatedByOrgId(String value);
    String getCreatedByOrgName(); void setCreatedByOrgName(String value);
}
