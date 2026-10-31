package com.sunwayworld.owned.attachments;
import com.sunwayworld.emc.module.item.homeattachment.resource.impl.EmcHomeAttachmentResourceImpl;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice(assignableTypes = {EmcHomeAttachmentResourceImpl.class, OwnedAttachmentContentController.class, OwnedAttachmentDownloadPageController.class})
public final class OwnedAttachmentExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> denied(AccessDeniedException failure) { return reply(403, "GIKAM.PERMISSION.UNAUTHORIZED"); }
    @ExceptionHandler(OwnedAttachmentReadDao.MissingFile.class)
    public ResponseEntity<String> missing(RuntimeException failure) { return reply(404, "GIKAM.ATTACHMENT.MISSING"); }
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> unsupported(RuntimeException failure) { return reply(501, "GIKAM.ATTACHMENT.UNSUPPORTED"); }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> invalid(RuntimeException failure) { return reply(400, "GIKAM.ATTACHMENT.INVALID_REQUEST"); }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> failed(RuntimeException failure) { return reply(500, "GIKAM.ATTACHMENT.READ_FAILED"); }
    private static ResponseEntity<String> reply(int status, String message) { return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"" + message + "\"}"); }
}
