package com.sunwayworld.owned.equipment;

import com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl.EmcEquiptResourceImpl;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

/** Errors are scoped to the actual original equipment resource. */
@RestControllerAdvice(assignableTypes={EmcEquiptResourceImpl.class, OwnedEquipmentCategoryMaintController.class})
public final class OwnedEquipmentExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> denied(AccessDeniedException failure) { return error(HttpStatus.FORBIDDEN,"GIKAM.PERMISSION.UNAUTHORIZED"); }
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> unsupported(UnsupportedOperationException failure) { return error(HttpStatus.NOT_IMPLEMENTED,"GIKAM.EQUIPMENT.UNSUPPORTED"); }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> invalid(IllegalArgumentException failure) { return error(HttpStatus.BAD_REQUEST,"GIKAM.EQUIPMENT.INVALID_REQUEST"); }
    private static ResponseEntity<String> error(HttpStatus status,String message) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\""+message+"\"}");
    }
}
