package com.sunwayworld.owned.personnel;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/** Capability probe for the explicitly isolated personnel master write seam. */
@RestController
public final class OwnedPersonnelWriteCapabilityController {
    private final OwnedPersonnelReadDao personnel;

    public OwnedPersonnelWriteCapabilityController(OwnedPersonnelReadDao personnel) {
        this.personnel = personnel;
    }

    @GetMapping(value = "/owned/personnel/main-write-available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> available() {
        try {
            personnel.requireMainWriteAvailable();
            return ResponseEntity.ok().header("Cache-Control", "no-store").body("{\"available\":true}");
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (Exception failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERSONNEL.WRITE_UNAVAILABLE\"}");
        }
    }

    @GetMapping(value = "/owned/personnel/unassigned-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> unassignedAccounts() {
        try {
            return ResponseEntity.ok().header("Cache-Control", "no-store").body(personnel.unassignedAccounts());
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (Exception failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERSONNEL.WRITE_UNAVAILABLE\"}");
        }
    }

    @GetMapping(value = "/owned/personnel/{id}/unassigned-tests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> unassignedTests(@PathVariable("id") String employee,
            @RequestParam(value = "n", defaultValue = "1") int page,
            @RequestParam(value = "s", defaultValue = "20") int size,
            @RequestParam(value = "name", required = false) String name) {
        try {
            return ResponseEntity.ok().header("Cache-Control", "no-store").body(personnel.unassignedTests(employee, page, size, name));
        } catch (AccessDeniedException denied) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        } catch (IllegalArgumentException invalid) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERSONNEL.INVALID_REQUEST\"}");
        } catch (Exception failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERSONNEL.WRITE_UNAVAILABLE\"}");
        }
    }
}
