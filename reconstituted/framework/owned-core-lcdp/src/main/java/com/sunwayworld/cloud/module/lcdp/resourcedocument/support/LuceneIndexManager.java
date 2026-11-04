/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcedocument.support;

import com.sunwayworld.cloud.module.lcdp.resourcedocument.bean.LcdpResourceDocumentBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexManager {
    private static final Logger logger = LogManager.getLogger(LuceneIndexManager.class);
    private static final int LUCENE_MAX_FIELD_LENGTH = 10000;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addIndex(LcdpResourceDocumentBean resourceDocument) {
        IndexWriter writer = null;
        try {
            if (!LuceneIndexManager.isDocumentEmpty(resourceDocument)) {
                writer = LuceneIndexManager.getIndexWriter();
                List<Document> list = LuceneIndexManager.getDocument(resourceDocument);
                for (Document doc : list) {
                    writer.addDocument(doc);
                }
            }
        }
        catch (Exception e) {
            logger.error("===========>lucene addIndex error:" + e.getMessage());
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.error("===========>lucene IndexWriter close error:" + e.getMessage());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addIndex(List<LcdpResourceDocumentBean> resourceDocumentList) {
        IndexWriter writer = null;
        try {
            writer = LuceneIndexManager.getIndexWriter();
            for (LcdpResourceDocumentBean resourceDocument : resourceDocumentList) {
                if (LuceneIndexManager.isDocumentEmpty(resourceDocument)) continue;
                List<Document> list = LuceneIndexManager.getDocument(resourceDocument);
                for (Document doc : list) {
                    writer.addDocument(doc);
                }
            }
        }
        catch (Exception e) {
            logger.error("===========>lucene addIndex error:" + e.getMessage());
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.error("===========>lucene IndexWriter close error:" + e.getMessage());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateIndex(LcdpResourceDocumentBean resourceDocument) {
        IndexWriter writer = null;
        try {
            writer = LuceneIndexManager.getIndexWriter();
            if (!LuceneIndexManager.isDocumentEmpty(resourceDocument)) {
                List<Document> list = LuceneIndexManager.getDocument(resourceDocument);
                for (Document doc : list) {
                    writer.deleteDocuments(new Term(doc.get("id"), resourceDocument.getId()));
                    writer.addDocument(doc);
                }
            }
        }
        catch (Exception e) {
            logger.error("===========>lucene updateIndex error:" + e.getMessage());
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.error("===========>lucene IndexWriter close error:" + e.getMessage());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateIndex(List<LcdpResourceDocumentBean> resourceDocumentList) {
        IndexWriter writer = null;
        try {
            writer = LuceneIndexManager.getIndexWriter();
            for (LcdpResourceDocumentBean resourceDocument : resourceDocumentList) {
                List<Document> list = LuceneIndexManager.getDocument(resourceDocument);
                for (Document doc : list) {
                    writer.deleteDocuments(new Term(doc.get("id"), resourceDocument.getId()));
                    writer.addDocument(doc);
                }
            }
        }
        catch (Exception e) {
            logger.error("===========>lucene updateIndex error:" + e.getMessage());
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.error("===========>lucene IndexWriter close error:" + e.getMessage());
                }
            }
        }
    }

    public static void deleteIndex(String indexId) {
        IndexWriter writer = null;
        try {
            writer = LuceneIndexManager.getIndexWriter();
            writer.deleteDocuments(new Term("id", indexId));
        }
        catch (Exception e) {
            logger.error("===========>lucene deleteIndex error:" + e.getMessage());
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.error("===========>lucene IndexWriter close error:" + e.getMessage());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void deleteIndex(List<String> indexIdList) {
        IndexWriter writer = null;
        try {
            writer = LuceneIndexManager.getIndexWriter();
            for (String indexId : indexIdList) {
                writer.deleteDocuments(new Term("id", indexId));
            }
        }
        catch (Exception e) {
            logger.error("===========>lucene deleteIndex error:" + e.getMessage());
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.error("===========>lucene IndexWriter close error:" + e.getMessage());
                }
            }
        }
    }

    public static List<Document> getDocument(LcdpResourceDocumentBean resourceDocument) {
        ArrayList<Document> list = new ArrayList<Document>();
        String content = resourceDocument.getResourceContent();
        if (StringUtils.isEmpty(content)) {
            return list;
        }
        int contentLen = content.length();
        if (content.getBytes().length < 32766) {
            Document doc = new Document();
            doc.add(new TextField("id", resourceDocument.getId(), Field.Store.YES));
            doc.add(new TextField("resourceName", resourceDocument.getResourceName(), Field.Store.YES));
            doc.add(new TextField("resourcePath", resourceDocument.getResourcePath(), Field.Store.YES));
            doc.add(new TextField("resourceCategory", resourceDocument.getResourceCategory(), Field.Store.YES));
            doc.add(new StringField("resourceContent", resourceDocument.getResourceContent(), Field.Store.YES));
            doc.add(new StringField("ignorCaseContent", resourceDocument.getResourceContent().toLowerCase(), Field.Store.YES));
            doc.add(new TextField("version", String.valueOf(resourceDocument.getVersion()), Field.Store.YES));
            doc.add(new TextField("type", String.valueOf(resourceDocument.getType()), Field.Store.YES));
            doc.add(new TextField("createdById", String.valueOf(resourceDocument.getCreatedById()), Field.Store.YES));
            list.add(doc);
        } else {
            int count = contentLen / 10000;
            for (int i = 0; i < count; ++i) {
                Document doc = new Document();
                doc.add(new TextField("id", resourceDocument.getId() + "_" + i, Field.Store.YES));
                doc.add(new TextField("resourceName", resourceDocument.getResourceName(), Field.Store.YES));
                doc.add(new TextField("resourcePath", resourceDocument.getResourcePath(), Field.Store.YES));
                doc.add(new TextField("resourceCategory", resourceDocument.getResourceCategory(), Field.Store.YES));
                doc.add(new StringField("resourceContent", content.substring(i * 10000, (i + 1) * 10000), Field.Store.YES));
                doc.add(new StringField("ignorCaseContent", content.toLowerCase().substring(i * 10000, (i + 1) * 10000), Field.Store.YES));
                doc.add(new TextField("version", String.valueOf(resourceDocument.getVersion()), Field.Store.YES));
                doc.add(new TextField("type", String.valueOf(resourceDocument.getType()), Field.Store.YES));
                doc.add(new TextField("createdById", String.valueOf(resourceDocument.getCreatedById()), Field.Store.YES));
                list.add(doc);
            }
            if (contentLen % 10000 != 0) {
                Document doc = new Document();
                doc.add(new TextField("id", resourceDocument.getId() + "_" + count, Field.Store.YES));
                doc.add(new TextField("resourceName", resourceDocument.getResourceName(), Field.Store.YES));
                doc.add(new TextField("resourcePath", resourceDocument.getResourcePath(), Field.Store.YES));
                doc.add(new TextField("resourceCategory", resourceDocument.getResourceCategory(), Field.Store.YES));
                doc.add(new StringField("resourceContent", content.substring(count * 10000, contentLen), Field.Store.YES));
                doc.add(new StringField("ignorCaseContent", content.toLowerCase().substring(count * 10000, contentLen), Field.Store.YES));
                doc.add(new TextField("version", String.valueOf(resourceDocument.getVersion()), Field.Store.YES));
                doc.add(new TextField("type", String.valueOf(resourceDocument.getType()), Field.Store.YES));
                doc.add(new TextField("createdById", String.valueOf(resourceDocument.getCreatedById()), Field.Store.YES));
                list.add(doc);
            }
        }
        return list;
    }

    public static IndexWriter getIndexWriter() throws Exception {
        FSDirectory dir = FSDirectory.open(Paths.get(LuceneIndexManager.getIndexPath(), new String[0]));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, config);
        return writer;
    }

    public static String getIndexPath() {
        File indexFile = new File("");
        String path = null;
        try {
            path = indexFile.getCanonicalPath() + "/index";
        }
        catch (IOException e) {
            logger.error("===========>lucene getIndexPath error:" + e.getMessage());
        }
        return path;
    }

    public static boolean isDocumentEmpty(LcdpResourceDocumentBean document) {
        if (document == null) {
            return true;
        }
        if (StringUtils.isEmpty(document.getId()) || StringUtils.isEmpty(document.getResourceName()) || StringUtils.isEmpty(document.getResourcePath())) {
            return true;
        }
        if (StringUtils.isEmpty(document.getResourceContent()) || StringUtils.isEmpty(document.getResourceCategory()) || document.getVersion() == null) {
            return true;
        }
        return StringUtils.isEmpty(document.getCreatedById()) || StringUtils.isEmpty(document.getType());
    }

    public static String getCompResourceId(String id) {
        if (StringUtils.isEmpty(id)) {
            return id;
        }
        if (id.indexOf("$$") < 0) {
            return id;
        }
        return id.split("\\$\\$")[0];
    }

    public static String getCompId(String id) {
        if (StringUtils.isEmpty(id)) {
            return id;
        }
        if (id.indexOf("$$") < 0) {
            return id;
        }
        return id.split("\\$\\$")[1];
    }
}
