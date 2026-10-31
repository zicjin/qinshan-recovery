package com.sunwayworld.owned.internal.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Read-only GenericResource status seam for the first equipment query wave. */
@RestController
@ConditionalOnBean(OwnedIdentityHttpBoundary.class)
public final class OwnedEquipmentResourceStatusController {
    private static final Set<String> EQUIPMENT_RESOURCES = new HashSet<String>(Arrays.asList(
            "equipt-crves-detail", "equipt-crves", "equipt-dioxin-sets", "equipt-flow-sets", "equipt-sets",
            "equipt-standard-sets", "equipt-sw-sets", "equipt-temp-sets"));

    private final OwnedIdentityHttpBoundary boundary;

    public OwnedEquipmentResourceStatusController(OwnedIdentityHttpBoundary boundary) {
        this.boundary = boundary;
    }

    @GetMapping(path = "/secure/emc/module/bp/{resource}/info/status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request, @PathVariable("resource") String resource) {
        if (!EQUIPMENT_RESOURCES.contains(resource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\":404,\"error\":\"Not Found\"}");
        }
        try {
            boundary.requirePermission(request.getSession(false),
                    com.sunwayworld.owned.internal.security.OwnedIdentityProvider.EQUIPMENT_READ);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"ext$\":{}}");
        } catch (org.springframework.security.access.AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}");
        } catch (java.sql.SQLException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}");
        }
    }
}
