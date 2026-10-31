package com.sunwayworld.owned.internal.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Serves the original supplier workspace iframe pages under their historical
 * non-secure URLs.  These pages are captured from the read-only supplier
 * runtime and packaged as frontend inputs; this controller only restores the
 * route seam in the same Spring application.
 */
@Controller
public final class OwnedWorkspacePanelController {
    private final OwnedIdentityHttpBoundary identity;

    public OwnedWorkspacePanelController(OwnedIdentityHttpBoundary identity) {
        if (identity == null) throw new IllegalArgumentException("workspace identity is required");
        this.identity = identity;
    }

    @GetMapping(path = "/module/index/workspaces/download-attachment",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public byte[] downloadAttachment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return page(request, response, "download-attachment.html");
    }

    @GetMapping(path = "/module/index/workspaces/report",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public byte[] report(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return page(request, response, "report.html");
    }

    @GetMapping(path = "/module/index/workspaces/notice-panel",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public byte[] noticePanel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return page(request, response, "notice-panel.html");
    }

    @GetMapping(path = "/module/index/workspaces/todo-panel",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public byte[] todoPanel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return page(request, response, "todo-panel.html");
    }

    @GetMapping(path = "/module/index/workspaces/todo",
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public byte[] overdueTodo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return page(request, response, "todo-overdue.html");
    }

    private byte[] page(HttpServletRequest request, HttpServletResponse response, String fileName) throws Exception {
        // These are same-origin iframe documents in the original workspace.
        // Keep the same-origin framing contract for this narrow page family.
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        identity.currentPrincipal(request.getSession(false));
        ClassPathResource resource = new ClassPathResource("static/workspace-panels/" + fileName);
        if (!resource.exists()) throw new IOException("missing imported workspace panel: " + fileName);
        try (InputStream input = resource.getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toByteArray();
        }
    }
}
