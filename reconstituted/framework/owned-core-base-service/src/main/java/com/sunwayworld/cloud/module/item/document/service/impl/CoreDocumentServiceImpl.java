/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.document.service.impl;

import com.sunwayworld.cloud.module.item.document.bean.CoreDocumentBean;
import com.sunwayworld.cloud.module.item.document.persistent.dao.CoreDocumentDao;
import com.sunwayworld.cloud.module.item.document.service.CoreDocumentService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreDocumentServiceImpl
implements CoreDocumentService {
    @Autowired
    private CoreDocumentDao documentDao;

    @Override
    public CoreDocumentBean selectOne(String table, String itemId, String documentType) {
        CoreDocumentBean condition = new CoreDocumentBean();
        condition.setTargetId(table + "$" + itemId);
        condition.setTargetType(documentType);
        return this.documentDao.selectOne(condition, new String[0]);
    }

    @Override
    @Transactional
    public void update(List<CoreDocumentBean> documentList) {
        this.documentDao.update(documentList, new String[0]);
    }

    @Override
    public <ID> void insert(String table, ID itemId, String documentType) {
        this.insert(table, itemId, documentType, null);
    }

    @Override
    public CoreDocumentBean selectOne(Long id) {
        return (CoreDocumentBean)this.documentDao.selectById(id);
    }

    @Override
    public void save(RestJsonWrapperBean wrapper) {
        List<CoreDocumentBean> itemList = wrapper.parse(CoreDocumentBean.class);
        if (!itemList.isEmpty()) {
            this.documentDao.updateIfChanged(itemList);
        }
    }

    @Override
    public List<CoreDocumentBean> selectListByTargetId(List<String> batchTargetId) {
        List documentList = batchTargetId.stream().map(t -> {
            CoreDocumentBean document = new CoreDocumentBean();
            document.setTargetId((String)t);
            return document;
        }).collect(Collectors.toList());
        return this.documentDao.selectList(documentList, Collections.emptyList(), Collections.emptyList(), new Order[0]);
    }

    @Override
    public void batchDeleteByTargetId(String ... batchTargetId) {
        List documentList = Arrays.stream(batchTargetId).map(t -> {
            CoreDocumentBean document = new CoreDocumentBean();
            document.setTargetId((String)t);
            return document;
        }).collect(Collectors.toList());
        this.documentDao.deleteBy(documentList, new String[]{"TARGETID"});
    }

    @Override
    public <ID> void insert(String table, ID itemId, String documentType, String content) {
        CoreDocumentBean document = new CoreDocumentBean();
        document.setId(ApplicationContextHelper.getNextIdentity());
        document.setTargetId(table + "$" + itemId);
        document.setTargetType(documentType);
        document.setContent(content);
        this.documentDao.insert(document);
    }

    @Override
    public <ID> void save(String table, ID itemId, String documentType, String content) {
        if (ObjectUtils.isEmpty(content)) {
            return;
        }
        CoreDocumentBean searchItem = new CoreDocumentBean();
        searchItem.setTargetId(table + "$" + itemId);
        searchItem.setTargetType(documentType);
        CoreDocumentBean document = new CoreDocumentBean();
        document.setContent(content);
        this.documentDao.update(document, searchItem);
    }

    @Override
    public <ID> void delete(String table, ID itemId, String documentType) {
        CoreDocumentBean document = new CoreDocumentBean();
        document.setTargetId(table + "$" + itemId);
        if (documentType != null) {
            document.setTargetType(documentType);
        }
        this.documentDao.deleteBy(document, "TARGETID", "TARGETTYPE");
    }

    @Override
    @Async
    public <ID> void deleteAsync(String table, ID itemId) {
        this.delete(table, itemId, null);
    }
}
