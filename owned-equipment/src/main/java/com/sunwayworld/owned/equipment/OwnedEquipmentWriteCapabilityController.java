package com.sunwayworld.owned.equipment;

import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;

/** Capability probe for the explicitly isolated equipment write seam. */
@RestController
public final class OwnedEquipmentWriteCapabilityController {
    private final Environment environment;
    private final OwnedIdentityHttpBoundary boundary;
    private final OwnedEquipmentReadDao dao;

    public OwnedEquipmentWriteCapabilityController(Environment environment, OwnedIdentityHttpBoundary boundary, OwnedEquipmentReadDao dao) {
        this.environment = environment; this.boundary = boundary; this.dao = dao;
    }

    @GetMapping(value = "/owned/equipment/write-available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> available(HttpServletRequest request) {
        try {
            requireIsolatedManager(request);
            return ResponseEntity.ok().header("Cache-Control", "no-store").body("{\"available\":true}");
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (Exception failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.EQUIPMENT.WRITE_UNAVAILABLE\"}");
        }
    }

    @GetMapping(value = "/owned/equipment/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> categories(HttpServletRequest request) {
        try {
            requireIsolatedManager(request);
            return ResponseEntity.ok().header("Cache-Control", "no-store").body(dao.availableCategories());
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(java.util.Collections.singletonMap("message", "GIKAM.PERMISSION.UNAUTHORIZED"));
        } catch (Exception failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.singletonMap("message", "GIKAM.EQUIPMENT.WRITE_UNAVAILABLE"));
        }
    }

    private void requireIsolatedManager(HttpServletRequest request) throws Exception {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.equipment.write.mode"))) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.EQUIPMENT_MANAGE);
    }
}
