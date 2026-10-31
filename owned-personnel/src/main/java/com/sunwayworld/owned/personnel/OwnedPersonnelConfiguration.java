package com.sunwayworld.owned.personnel;

import com.sunwayworld.emc.module.mdm.emp.emp.resource.impl.EmcEmpResourceImpl;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityConfiguration;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.core.env.Environment;

/** Main/runtime wiring for the unchanged personnel resource contract. */
@AutoConfigureAfter(OwnedIdentityConfiguration.class)
@ConditionalOnBean({DataSource.class, JdbcPersonnelIdentity.class, JdbcPersonnelIdentityProvider.class,
        OwnedIdentityHttpBoundary.class})
public class OwnedPersonnelConfiguration {
    @Bean
    public OwnedPersonnelReadDao ownedPersonnelReadDao(DataSource dataSource, JdbcPersonnelIdentity identity,
            JdbcPersonnelIdentityProvider provider, OwnedIdentityHttpBoundary boundary, AuditEventSink audit,
            Environment environment) {
        return new OwnedPersonnelReadDao(dataSource, identity, provider, boundary, audit, environment);
    }

    @Bean
    public OwnedPersonnelReadService ownedPersonnelReadService(OwnedPersonnelReadDao dao) {
        return new OwnedPersonnelReadService(dao);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(OwnedPersonnelAbilityResourceController.class)
    public OwnedPersonnelAbilityResourceController ownedPersonnelAbilityResourceController(
            OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        return new OwnedPersonnelAbilityResourceController(boundary, dataSource);
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(OwnedPersonnelWriteCapabilityController.class)
    public OwnedPersonnelWriteCapabilityController ownedPersonnelWriteCapabilityController(OwnedPersonnelReadDao dao) {
        return new OwnedPersonnelWriteCapabilityController(dao);
    }

    /** Register the original controller class; no competing personnel URI exists. */
    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(EmcEmpResourceImpl.class)
    public EmcEmpResourceImpl emcEmpResource() {
        return new EmcEmpResourceImpl();
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(OwnedPersonnelExceptionHandler.class)
    public OwnedPersonnelExceptionHandler ownedPersonnelExceptionHandler() {
        return new OwnedPersonnelExceptionHandler();
    }

    @RestControllerAdvice(assignableTypes = {EmcEmpResourceImpl.class, OwnedPersonnelTrainingController.class})
    static final class OwnedPersonnelExceptionHandler {
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<String> denied(AccessDeniedException failure) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
        }
        @ExceptionHandler(UnsupportedOperationException.class)
        public ResponseEntity<String> unsupported(UnsupportedOperationException failure) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERSONNEL.UNSUPPORTED\"}");
        }
        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<String> failed(IllegalStateException failure) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERSONNEL.READ_FAILED\"}");
        }
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> invalid(IllegalArgumentException failure) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"GIKAM.PERSONNEL.INVALID_REQUEST\"}");
        }
    }
}
