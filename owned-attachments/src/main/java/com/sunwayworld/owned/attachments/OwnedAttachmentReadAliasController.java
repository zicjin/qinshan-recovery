package com.sunwayworld.owned.attachments;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Completes the read-only GenericResource aliases missing from the original attachment bean. */
@RestController
@ConditionalOnProperty(name = "owned.attachments.enabled", havingValue = "true")
@ConditionalOnBean({OwnedAttachmentReadService.class, OwnedIdentityHttpBoundary.class})
public final class OwnedAttachmentReadAliasController {
    private static final String PREFIX = "/secure/emc/module/item/home-attachments";
    private final OwnedAttachmentReadService service;
    private final OwnedIdentityHttpBoundary identity;

    public OwnedAttachmentReadAliasController(OwnedAttachmentReadService service, OwnedIdentityHttpBoundary identity) {
        this.service = service; this.identity = identity;
    }

    @PostMapping(path = PREFIX + "/queries/searchable", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchable(HttpServletRequest request, @RequestBody(required = false) String body) {
        OwnedPrincipal principal = identity.currentPrincipal(request.getSession(false));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(JSON.toJSONString(((OwnedAttachmentReadDao) service.getDao()).readPage(new RestJsonWrapperBean(body == null ? "{}" : body))));
    }

    @GetMapping(path = PREFIX + "/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        identity.currentPrincipal(request.getSession(false));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"ext$\":{}}");
    }
}
