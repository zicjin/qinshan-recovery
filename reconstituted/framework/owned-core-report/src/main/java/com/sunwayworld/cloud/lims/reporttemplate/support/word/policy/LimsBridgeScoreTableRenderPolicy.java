/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.render.LimsCustomerRender;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.FrUtil;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordUtil2007;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class LimsBridgeScoreTableRenderPolicy
implements LimsCustomerRender {
    private String prefix;
    private String suffix;
    private static final String TITLE_SPLIT_TAG = ",";
    private static final Logger logger = LogManager.getLogger(LimsBridgeScoreTableRenderPolicy.class);

    public LimsBridgeScoreTableRenderPolicy() {
        this("[", "]");
    }

    public LimsBridgeScoreTableRenderPolicy(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void render(ElementTemplate eleTemplate, Object renderData, XWPFTemplate template) {
        if (renderData != null) {
            CoreReportConfigLineBean coreReportConfigLineBean = PersistableHelper.mapToPersistable((LimsTemplateDataMap)renderData, CoreReportConfigLineBean.class);
            Map<String, String> ext$ = coreReportConfigLineBean.getExt$();
            String targetid = ext$.remove("targetid");
            ext$.put("id", coreReportConfigLineBean.getId().toString());
            CoreFileService coreFileService = ApplicationContextHelper.getBean(CoreFileService.class);
            CoreFileBean frFileBean = (CoreFileBean)coreFileService.selectFileListByTargetIds(targetid).stream().findFirst().orElseThrow(() -> new InvalidDataException("\u62a5\u8868\u9644\u4ef6\u4e0d\u5b58\u5728"));
            CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
            coreFileManager.download(frFileBean);
            File excelTemp = FileUtils.createTempFile("xlsx");
            FrUtil.createExcel(coreReportConfigLineBean, frFileBean, excelTemp, ext$);
            System.err.println(excelTemp);
            RunTemplate runTemplate = (RunTemplate)eleTemplate;
            XWPFRun run = runTemplate.getRun();
            XWPFTableCell picCell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
            List<XWPFParagraph> picCellParagraphs = picCell.getParagraphs();
            for (int k = picCellParagraphs.size() - 1; k > 0; --k) {
                picCell.removeParagraph(k);
            }
            XWPFParagraph picPara = picCellParagraphs.get(0);
            if (picPara.getRuns() == null || picPara.getRuns().isEmpty()) {
                picPara.createRun();
            }
            List<XWPFRun> picRuns = picPara.getRuns();
            for (int k = picRuns.size() - 1; k > 0; --k) {
                picPara.removeRun(k);
            }
            picRuns.get(0).setText("", 0);
            try {
                WordUtil2007.insertTab(picRuns.get(0), excelTemp.getAbsolutePath(), "sheet1");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
