/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.license.resource.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.license.resource.CoreLicenseResource;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class CoreLicenseResourceImpl
implements CoreLicenseResource {
    @Override
    public ResponseEntity<Map<String, Object>> getLicenseCountdownDays() {
        return removed("/core/module/sys/licenses");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getNumberOfOnlineUsers() {
        return removed("/core/module/sys/licenses/number-of-online-users");
    }

    private ResponseEntity<Map<String, Object>> removed(String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put("path", path);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
