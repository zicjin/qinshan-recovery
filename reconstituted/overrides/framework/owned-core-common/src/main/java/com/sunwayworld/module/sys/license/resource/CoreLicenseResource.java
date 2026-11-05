/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.license.resource;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/core/module/sys/licenses"})
public interface CoreLicenseResource {
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLicenseCountdownDays();

    @GetMapping(value={"/number-of-online-users"})
    public ResponseEntity<Map<String, Object>> getNumberOfOnlineUsers();
}
