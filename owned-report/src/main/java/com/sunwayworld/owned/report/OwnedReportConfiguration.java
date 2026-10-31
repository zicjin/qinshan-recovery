package com.sunwayworld.owned.report;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Owned report adapter wiring; no third-party report runtime is shipped. */
@AutoConfigureAfter(OwnedIdentityConfiguration.class)
@ConditionalOnBean({DataSource.class, JdbcPersonnelIdentity.class, JdbcPersonnelIdentityProvider.class, OwnedIdentityHttpBoundary.class})
public class OwnedReportConfiguration {
    @Bean public OwnedReportReadDao ownedReportReadDao(DataSource source) { return new OwnedReportReadDao(source); }
    @Bean public OwnedReportReplayClient ownedReportReplayClient(org.springframework.core.env.Environment environment) { return new OwnedReportReplayClient(environment); }
    @Bean public OwnedReportReads ownedReportReads(OwnedReportReadDao dao, OwnedIdentityHttpBoundary boundary, JdbcPersonnelIdentityProvider provider, AuditEventSink audit) { return new OwnedReportReads(dao,boundary,provider,audit); }
    @Bean public OwnedReportExceptionHandler ownedReportExceptionHandler() { return new OwnedReportExceptionHandler(); }

    @RestControllerAdvice(assignableTypes = OwnedReportController.class)
    public static final class OwnedReportExceptionHandler {
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<String> denied(AccessDeniedException ignored) {
            return body(HttpStatus.FORBIDDEN, "REPORT_UNAUTHORIZED");
        }
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> invalid(IllegalArgumentException failure) {
            return body(HttpStatus.BAD_REQUEST, "REPORT_INVALID_REQUEST");
        }
        @ExceptionHandler(java.sql.SQLException.class)
        public ResponseEntity<String> database(java.sql.SQLException failure) {
            return body(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT_READ_FAILED");
        }
        @ExceptionHandler(UnsupportedOperationException.class)
        public ResponseEntity<String> unsupported(UnsupportedOperationException failure) {
            return body(HttpStatus.NOT_IMPLEMENTED, "REPORT_DISABLED");
        }
        private static ResponseEntity<String> body(HttpStatus status, String code) {
            return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\":\"failure\",\"code\":\"" + code + "\"}");
        }
    }
}
