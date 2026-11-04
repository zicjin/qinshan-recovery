/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.thymeleaf.templateresource;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.thymeleaf.templateresource.GikamBaseResourceTemplateResource;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigLineBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftLineBean;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigService;
import com.sunwayworld.module.admin.page.service.CoreAdminPageDraftLineService;
import com.sunwayworld.module.admin.page.service.CoreAdminPageDraftService;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import com.sunwayworld.module.design.studio.service.CoreStudioAdvSearchConfigService;
import com.sunwayworld.module.sys.page.bean.CorePageConfigDTO;
import com.sunwayworld.module.sys.page.service.CorePageConfigService;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;

public class GikamPageConfigResourceTemplateResource
extends GikamBaseResourceTemplateResource {
    public GikamPageConfigResourceTemplateResource(Resource resource, String characterEncoding) {
        super(resource, characterEncoding);
    }

    @Override
    public Reader reader() throws IOException {
        InputStream inputStream = this.getResource().getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader((InputStream)new BufferedInputStream(inputStream), this.getCharacterEncoding()));){
            CoreStudioAdvSearchConfigService configService;
            List<CoreStudioAdvSearchConfigBean> configList;
            String url = ServletUtils.getRequestUri();
            StringBuilder sb = new StringBuilder();
            if (LocalContextHelper.isUserLogin() && !CollectionUtils.isEmpty(configList = (configService = ApplicationContextHelper.getBean(CoreStudioAdvSearchConfigService.class)).getCurrentUserConfigListByUrl(url))) {
                sb.append("<script>Gikam.component.Grid.setAdvSearchConfig([");
                int j = configList.size();
                for (int i = 0; i < j; ++i) {
                    CoreStudioAdvSearchConfigBean config = configList.get(i);
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append("{blockId:'").append(config.getBlockId()).append("',url:'").append(config.getUrl()).append("',json:'").append(config.getJson()).append("'}");
                }
                sb.append("]);</script>");
            }
            br.lines().forEach(l -> {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append((String)l);
            });
            String content = this.getRevisedContent(sb.toString());
            CorePageConfigService pageConfigService = ApplicationContextHelper.getBean(CorePageConfigService.class);
            CorePageConfigDTO config = pageConfigService.selectCurrentPageConfig(url);
            if (!config.getGrid().isEmpty()) {
                content = "<script>Gikam.setCompParam('" + JSON.toJSONString(config) + "');</script>" + content;
            }
            StringReader stringReader = new StringReader(content);
            return stringReader;
        }
    }

    private String getRevisedContent(String content) {
        block4: {
            String requestUri;
            String menuId;
            block3: {
                HttpServletRequest request = ServletUtils.getCurrentRequest();
                if (request == null) {
                    return content;
                }
                menuId = request.getHeader("sunway-menu");
                requestUri = ServletUtils.getRequestUri(request);
                boolean pageConfig = "1".equals(request.getHeader("sunway-page-config"));
                if (!pageConfig) break block3;
                CoreAdminPageDraftBean draft = ApplicationContextHelper.getBean(CoreAdminPageDraftService.class).selectDraftIfPresent(menuId, requestUri);
                if (draft == null) break block4;
                CoreAdminPageDraftLineBean filter = new CoreAdminPageDraftLineBean();
                filter.setDraftId(draft.getId());
                List<CoreAdminPageDraftLineBean> draftLineList = ApplicationContextHelper.getBean(CoreAdminPageDraftLineService.class).selectList(filter, new Order[0]);
                for (CoreAdminPageDraftLineBean draftLine : draftLineList) {
                    content = this.getRevisedContent(content, draftLine.getJsUri(), draftLine.getContent());
                }
                break block4;
            }
            List<CoreAdminPageConfigLineBean> configLineList = ApplicationContextHelper.getBean(CoreAdminPageConfigService.class).selectActivatedConfigLineList(menuId, requestUri);
            for (CoreAdminPageConfigLineBean configLine : configLineList) {
                content = this.getRevisedContent(content, configLine.getJsUri(), configLine.getContent());
            }
        }
        return content;
    }

    private String getRevisedContent(String content, String jsUri, String jsContent) {
        int index = content.indexOf(jsUri);
        if (index > 0) {
            int newLineIndex = content.indexOf("\n", index);
            int bracketsIndex = content.indexOf("]", index);
            StringBuilder sb = new StringBuilder(content.substring(0, index - 1)).append(content.substring(content.indexOf("\n", Math.min(newLineIndex, bracketsIndex))));
            content = sb.toString();
            int splitIndex = sb.toString().indexOf("]);");
            int offset = "]);".length();
            return content.substring(0, splitIndex + offset) + "\n" + "/*start" + jsUri + "*/\n" + jsContent + "\n" + "/*end" + jsUri + "*/\n" + content.substring(splitIndex + offset);
        }
        return content;
    }
}
