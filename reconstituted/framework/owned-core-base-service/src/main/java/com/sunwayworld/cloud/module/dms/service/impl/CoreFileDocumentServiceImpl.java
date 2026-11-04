/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.service.impl;

import com.sunwayworld.cloud.module.dms.bean.CoreFileDocumentBean;
import com.sunwayworld.cloud.module.dms.repository.CoreFileDocumentRepository;
import com.sunwayworld.cloud.module.dms.service.CoreFileDocumentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.PageRequest;
import com.sunwayworld.framework.es.ElasticSearchManager;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.metadata.bean.CoreParentTableInstanceDTO;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreFileDocumentServiceImpl
implements CoreFileDocumentService {
    @Autowired
    private CoreFileManager fileManager;
    @Autowired
    private CoreFileService fileService;
    @Autowired
    private CoreTableService tableService;
    @Autowired(required=false)
    private CoreFileDocumentRepository documentRepository;
    @Autowired(required=false)
    private ElasticSearchManager elasticSearchManager;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Long insertByFile(Long fileId, @Nullable String category) {
        CoreFileBean file = (CoreFileBean)this.fileService.selectById(fileId);
        File downloadFile = null;
        try {
            downloadFile = FileUtils.createTempFile(file.getFileExt());
            this.fileManager.download(file, downloadFile.toPath());
            CoreFileDocumentBean document = new CoreFileDocumentBean();
            document.setId(file.getId());
            if (!StringUtils.isEmpty(file.getTargetId())) {
                String[] splits = StringUtils.split(file.getTargetId(), "$");
                List<CoreParentTableInstanceDTO> instanceList = this.tableService.selectAncientTableInstanceList(splits[0], Arrays.asList(splits[1]));
                if (instanceList.isEmpty()) {
                    document.setServiceName(I18nHelper.getMessage(splits[0] + ".SERVICE_NAME", new String[0]));
                    document.setServiceTable(splits[0]);
                    document.setServiceId(splits[1]);
                } else {
                    CoreParentTableInstanceDTO instance = instanceList.get(0);
                    document.setServiceName(I18nHelper.getMessage(instance.getTable() + ".SERVICE_NAME", new String[0]));
                    document.setServiceTable(instance.getTable());
                    document.setServiceId(instance.getId());
                }
            }
            document.setUrl(this.fileManager.getDownloadUrl(file));
            document.setCategory(category);
            document.setDocumentName(file.getName());
            document.setOriginalDocumentName(file.getName());
            document.setCreatedById(file.getCreatedById());
            document.setCreatedByName(file.getCreatedByName());
            document.setCreatedTime(file.getCreatedTime());
            document.setDocumentContent(this.getDocumentContent(downloadFile));
            this.documentRepository.save(document);
            Long l = document.getId();
            return l;
        }
        finally {
            FileUtils.deleteQuietly(downloadFile);
        }
    }

    @Override
    public Long updateByFile(Long fileId, String category) {
        this.deleteByFile(fileId);
        return this.insertByFile(fileId, category);
    }

    @Override
    public void deleteByFile(Long fileId) {
        this.documentRepository.deleteById(fileId);
    }

    @Override
    public Page<CoreFileDocumentBean> selectPagination(PageRequest pageRequest, @Nullable String category, String keywords) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder shouldBuilder = QueryBuilders.boolQuery();
        shouldBuilder.should(((MatchQueryBuilder)QueryBuilders.matchQuery("documentName", keywords).boost(2.0f)).minimumShouldMatch(this.elasticSearchManager.getDefaultMatchingMinRatio())).should(((MatchQueryBuilder)QueryBuilders.matchQuery("documentContent", keywords).boost(1.0f)).minimumShouldMatch(this.elasticSearchManager.getDefaultMatchingMinRatio()));
        if (!StringUtils.isBlank(category) && !"root".equals(category)) {
            queryBuilder.must(QueryBuilders.matchQuery("category", category));
            queryBuilder.must(shouldBuilder);
        } else {
            queryBuilder = shouldBuilder;
        }
        nativeSearchQueryBuilder.withQuery(queryBuilder);
        nativeSearchQueryBuilder.withHighlightBuilder(this.elasticSearchManager.getHighlightBuilder("documentName", "documentContent"));
        nativeSearchQueryBuilder.withPageable(pageRequest);
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        return this.elasticSearchManager.searchPagination(nativeSearchQuery, CoreFileDocumentBean.class);
    }

    private String getDocumentContent(File file) {
        int limit = IndexSettings.MAX_ANALYZED_OFFSET_SETTING.getDefault(Settings.EMPTY);
        String content = FileUtils.extractTextFromFile(file);
        if (StringUtils.isBlank(content) || content.length() <= limit) {
            return content;
        }
        return content.substring(0, limit);
    }
}
