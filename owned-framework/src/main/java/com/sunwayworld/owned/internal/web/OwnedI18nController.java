package com.sunwayworld.owned.internal.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned replacement for the Gikam bootstrap message catalogue route.
 * The catalogue is a read-only resource captured from the sss2 supplier
 * baseline; it contains UI messages only and does not implement licensing.
 */
@RestController
public final class OwnedI18nController {
    private static final String ZH_CN = "zh-cn";

    @GetMapping(path = "/open/core/module/sys/i18n-messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> messages(
            @RequestParam(value = "locale", required = false, defaultValue = ZH_CN) String locale) {
        String normalized = normalize(locale);
        if (!ZH_CN.equals(normalized)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{}");
        }
        try {
            Resource resource = new ClassPathResource("owned/i18n/zh-cn.json");
            String body = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        } catch (IOException failure) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("{}");
        }
    }

    private static String normalize(String locale) {
        if (locale == null || locale.trim().isEmpty()) return ZH_CN;
        return locale.trim().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
