/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.repository;

import com.sunwayworld.cloud.module.dms.bean.CoreFileDocumentBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix="spring.elasticsearch.rest", name={"enabled"}, havingValue="true")
public interface CoreFileDocumentRepository
extends ElasticsearchRepository<CoreFileDocumentBean, Long> {
}
