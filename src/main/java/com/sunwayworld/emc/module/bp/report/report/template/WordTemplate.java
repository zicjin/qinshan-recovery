package com.sunwayworld.emc.module.bp.report.report.template;

import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.PfWord2007Utils;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateBasicDTO;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2020年09月02日
 */
public interface WordTemplate {

    /**
     * 获取Word模板名称
     * @return 模板名称
     */
    String getTemplateName();

    /**
     * 构建基本信息
     * @return
     */
    WordTemplateBasicDTO buildBasicInformation();

    /**
     * 获取Word文档对象
     * @return
     */
    XWPFDocument getDocument();

    /**
     * 创建检测项目表
     */
    void createMonitorItemTable();

    /**
     * 创建检测结果表
     */
    void createResultTable();

    /**
     * 生成Word文档
     */
    default void generate() {
        setBasicInformation(getDocument(), buildBasicInformation());
        createMonitorItemTable();
        createResultTable();
        clean();
        format();
    }

    /**
     * 设置基本信息
     * @param document
     * @param templateBasicDTO
     */
    default void setBasicInformation(XWPFDocument document, WordTemplateBasicDTO templateBasicDTO) {
        Map<String, Object> basicMap = WordTemplateUtils.toMap(templateBasicDTO);
        WordTemplateUtils.mapNullToString(basicMap, EmcConstant.NULL_REPLACED_VALUE);

        basicMap.forEach((key, value) -> {
            if (StringUtils.equals(String.valueOf(value), "\\")) {
                basicMap.put(key, "\\\\");
            }
        });

        Word2007Utils.replaceAll(document, basicMap);
    }

    /**
     * 清除文档中的占位符
     */
    default void clean() {
        Word2007Utils.removeKey(this.getDocument(), EmcConstant.MONITOR_TABLE_PLACEHOLDER);
        Word2007Utils.removeKey(this.getDocument(), EmcConstant.DYNAMIC_TABLE_PLACEHOLDER);
        Word2007Utils.removeKey(this.getDocument(), EmcConstant.NEW_PAGE_START_PLACEHOLDER);
    }

    /**
     * 格式化Word文档中的上下标
     */
    default void format() {
        PfWord2007Utils.formatUnit(getDocument());
    }

}
