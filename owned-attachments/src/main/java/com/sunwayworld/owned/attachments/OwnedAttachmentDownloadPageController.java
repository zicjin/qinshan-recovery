package com.sunwayworld.owned.attachments;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import java.io.*;

/** Narrow page adapter for the unchanged historical download-list fragment. */
@Controller
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "owned.attachments.enabled", havingValue = "true")
public final class OwnedAttachmentDownloadPageController {
    private final OwnedIdentityHttpBoundary identity;
    public OwnedAttachmentDownloadPageController(OwnedIdentityHttpBoundary identity) { this.identity = identity; }
    @RequestMapping(value = "/secure/emc/module/index/workspace/download-attachment", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public byte[] page(HttpServletRequest request) throws Exception {
        identity.currentPrincipal(request.getSession(false));
        // This fragment only initializes the original business script. Its
        // supplier workspace include is hosted by the owned application shell.
        try (InputStream input = new ClassPathResource("templates/emc/module/index/workspace/emc-home-attachment-download-list.html").getInputStream();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096]; int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toByteArray();
        }
    }
}
