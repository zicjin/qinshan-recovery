package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.item.consume.consume.resource.impl.EmcConsumeResourceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes={EmcConsumeResourceImpl.class, OwnedMdmStockReadController.class})
public final class OwnedConsumablesExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class) public ResponseEntity<String> denied(AccessDeniedException e) { return error(HttpStatus.FORBIDDEN,"GIKAM.PERMISSION.UNAUTHORIZED"); }
    @ExceptionHandler(UnsupportedOperationException.class) public ResponseEntity<String> unsupported(UnsupportedOperationException e) { return error(HttpStatus.NOT_IMPLEMENTED,"GIKAM.CONSUMABLES.UNSUPPORTED"); }
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<String> invalid(IllegalArgumentException e) { return error(HttpStatus.BAD_REQUEST,"GIKAM.CONSUMABLES.INVALID_REQUEST"); }
    @ExceptionHandler(IllegalStateException.class) public ResponseEntity<String> failed(IllegalStateException e) { return error(HttpStatus.INTERNAL_SERVER_ERROR,"GIKAM.CONSUMABLES.READ_FAILED"); }
    private static ResponseEntity<String> error(HttpStatus status,String message) { return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\""+message+"\"}"); }
}
