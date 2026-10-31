package com.sunwayworld.owned.personnel;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Status projection for the original personnel GenericResource entrypoint. */
@RestController
public final class OwnedPersonnelStatusController {
    private final OwnedIdentityHttpBoundary boundary;
    public OwnedPersonnelStatusController(OwnedIdentityHttpBoundary boundary) { this.boundary = boundary; }
    @GetMapping(path = "/secure/emc/module/mdm/emp/emps/info/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.PERSONNEL_READ); return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
        catch (SQLException failure) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERSONNEL.READ_FAILED\"}"); }
    }
}
