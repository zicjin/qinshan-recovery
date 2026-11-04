/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.thymeleaf.templateresource;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.thymeleaf.templateresource.GikamBaseResourceTemplateResource;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import com.sunwayworld.module.design.studio.service.CoreStudioAdvSearchConfigService;
import com.sunwayworld.module.design.studio.service.CoreStudioPageService;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.springframework.core.io.Resource;

public class GikamDesignStudioResourceTemplateResource
extends GikamBaseResourceTemplateResource {
    public GikamDesignStudioResourceTemplateResource(Resource resource, String characterEncoding) {
        super(resource, characterEncoding);
    }

    @Override
    public Reader reader() throws IOException {
        InputStream inputStream = this.getResource().getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader((InputStream)new BufferedInputStream(inputStream), this.getCharacterEncoding()));){
            CoreStudioAdvSearchConfigService configService;
            List<CoreStudioAdvSearchConfigBean> configList;
            String requestUri = ServletUtils.getRequestUri();
            StringBuilder sb = new StringBuilder();
            CoreStudioPageService studioPageService = ApplicationContextHelper.getBean(CoreStudioPageService.class);
            String json = studioPageService.selectJsonByPageUrl(requestUri);
            if (!StringUtils.isBlank(json)) {
                sb.append("<script>Gikam.setCompParam('").append(json).append("');</script>");
            }
            if (LocalContextHelper.isUserLogin() && !CollectionUtils.isEmpty(configList = (configService = ApplicationContextHelper.getBean(CoreStudioAdvSearchConfigService.class)).getCurrentUserConfigListByUrl(requestUri))) {
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
            StringReader stringReader = new StringReader(sb.toString());
            return stringReader;
        }
    }
}
