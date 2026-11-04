/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.support;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpInitJavaScriptDTO;
import com.sunwayworld.framework.utils.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class LcdpScriptTemplateHelper {
    public static String generateBpActionScriptTemplate(long bpActionId) {
        StringBuilder script = new StringBuilder();
        script.append(" import com.sunwayworld.framework.utils.SpringUtils;\n");
        script.append(" import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;\n");
        script.append(" import com.sunwayworld.framework.context.ApplicationContextHelper;\n");
        script.append(" import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;\n");
        script.append(" import com.sunwayworld.framework.support.base.service.MapService;\n");
        script.append(" import org.springframework.stereotype.Repository;\n");
        script.append(" import org.springframework.transaction.annotation.Transactional;\n");
        script.append("  \n");
        script.append(" import com.sunwayworld.framework.utils.*;\n");
        script.append(" import java.util.*;\n");
        script.append("  \n");
        script.append(" @Repository\n");
        script.append(" public class LcdpBpActionHandle" + bpActionId + " implements MapService {\n");
        script.append("  \n");
        script.append("     /**\n");
        script.append("      * \u4e1a\u52a1\u5de5\u4f5c\u6d41\u52a8\u4f5c\u6267\u884c\u811a\u672c\n");
        script.append("      * @param bizDataList -\u4e1a\u52a1\u6570\u636e \n");
        script.append("      * @param bpAction -\u6d41\u7a0b\u8282\u70b9\u52a8\u4f5c \n");
        script.append("      * @param comment -\u5ba1\u6838\u610f\u89c1 \n");
        script.append("      * @param jsonWrapper \n");
        script.append("      */\n");
        script.append("     @Transactional\n");
        script.append("     public void execute(List<Map<String, Object>> bizDataList, LcdpBizProcessNodeActionBean bpAction, String comment, RestJsonWrapperBean jsonWrapper) {\n");
        script.append("  \n");
        script.append("     }\n");
        script.append("  \n");
        script.append(" }");
        return script.toString();
    }

    public static String generateBpCheckScriptTemplate(long bpActionId) {
        StringBuilder script = new StringBuilder();
        script.append(" import com.sunwayworld.framework.utils.SpringUtils;\n");
        script.append(" import com.sunwayworld.cloud.module.lcdp.base.LcdpResultDTO;\n");
        script.append(" import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;\n");
        script.append(" import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;\n");
        script.append(" import com.sunwayworld.framework.context.ApplicationContextHelper;\n");
        script.append(" import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;\n");
        script.append(" import com.sunwayworld.framework.support.base.service.MapService;\n");
        script.append(" import org.springframework.stereotype.Repository;\n");
        script.append(" import org.springframework.transaction.annotation.Transactional;\n");
        script.append("  \n");
        script.append(" import com.sunwayworld.framework.utils.*;\n");
        script.append(" import java.util.*;\n");
        script.append("  \n");
        script.append(" @Repository\n");
        script.append(" public class LcdpBpActionCheck" + bpActionId + " implements MapService {\n");
        script.append("  \n");
        script.append("     /**\n");
        script.append("      * \u4e1a\u52a1\u5de5\u4f5c\u6d41\u52a8\u4f5c\u6821\u9a8c\u811a\u672c\n");
        script.append("      *\n");
        script.append("      * @param bizDataList -\u4e1a\u52a1\u6570\u636e\n");
        script.append("      * @param bpAction    -\u6d41\u7a0b\u8282\u70b9\u52a8\u4f5c\n");
        script.append("      * @param comment     -\u5ba1\u6838\u610f\u89c1\n");
        script.append("      * @param jsonWrapper\n");
        script.append("      * @return LcdpResultDTO\n");
        script.append("      * \u65b9\u6cd5\u8fd4\u56de\u503cLcdpResultDTO\u5bf9\u8c61\n");
        script.append("      * --code:200-\u6821\u9a8c\u901a\u8fc7\uff0c500-\u6821\u9a8c\u4e0d\u901a\u8fc7\n");
        script.append("      * --message:\u6821\u9a8c\u63d0\u793a\u4fe1\u606f\n");
        script.append("      */\n");
        script.append("     @Transactional\n");
        script.append("     public LcdpResultDTO check(List<Map<String, Object>> bizDataList, LcdpBizProcessNodeActionBean bpAction, String comment, RestJsonWrapperBean jsonWrapper) {\n");
        script.append("  \n");
        script.append("         return new LcdpResultDTO(LcdpConstant.RESULT_CODE_SUCCESS, LcdpConstant.RESULT_MESSAGE_SUCCESS);\n");
        script.append("     }\n");
        script.append("  \n");
        script.append(" }");
        return script.toString();
    }

    public static String generateBpCheckScriptJsTemplate() {
        StringBuilder script = new StringBuilder();
        script.append("//bizData\u4e3a\u63d0\u4ea4\u7684\u4e1a\u52a1\u6570\u636e\uff0cselectBpAction\u4e3a\u9009\u4e2d\u7684\u8282\u70b9\u52a8\u4f5c");
        script.append("  \n");
        script.append("  \n");
        script.append("  \n");
        script.append("  \n");
        script.append(" return true; \n");
        return script.toString();
    }

    public static String generateJavaScript(String className, String tableName, String nameSpace, String bpFlag) {
        TemplateEngine engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("thymeleaf/script-tmpl/");
        resolver.setSuffix(".java");
        resolver.setTemplateMode("TEXT");
        resolver.setCharacterEncoding("UTF-8");
        engine.setTemplateResolver(resolver);
        Context context = new Context();
        LcdpInitJavaScriptDTO initJavaScriptDTO = LcdpInitJavaScriptDTO.of(className, tableName, nameSpace);
        context.setVariable("initJavaScriptDTO", initJavaScriptDTO);
        String content = tableName == null ? engine.process("javaUtilsScriptTemplate", (IContext)context) : (bpFlag == null ? engine.process("javaScriptTemplate", (IContext)context) : engine.process("javaBpScriptTemplate", (IContext)context));
        return content;
    }

    public static String generateViewJavaScript(String className, String tableName, String nameSpace) {
        TemplateEngine engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("thymeleaf/script-tmpl/");
        resolver.setSuffix(".java");
        resolver.setTemplateMode("TEXT");
        engine.setTemplateResolver(resolver);
        Context context = new Context();
        LcdpInitJavaScriptDTO initJavaScriptDTO = LcdpInitJavaScriptDTO.of(className, tableName, nameSpace);
        context.setVariable("initJavaScriptDTO", initJavaScriptDTO);
        String content = engine.process("javaViewScriptTemplate", (IContext)context);
        return content;
    }

    public static String generateMapper(String mapperPath, String tableName, String mapperTmplName) {
        TemplateEngine engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("thymeleaf/mapper-tmpl/");
        resolver.setSuffix(".txt");
        resolver.setTemplateMode("TEXT");
        resolver.setCharacterEncoding("UTF-8");
        engine.setTemplateResolver(resolver);
        Context context = new Context();
        mapperPath = StringUtils.replaceLast(mapperPath, mapperTmplName, "") + "Mapper";
        context.setVariable("mapperPath", mapperPath);
        context.setVariable("tableName", tableName);
        String content = engine.process(mapperTmplName, (IContext)context);
        return content;
    }
}
