package com.sunwayworld.owned.internal.web;

import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public credential-transport key route used by the existing login page. */
@RestController
@ConditionalOnBean(OwnedRsaLoginCredentialDecoder.class)
public final class OwnedSecurityKeyController {
    private final OwnedRsaLoginCredentialDecoder decoder;

    public OwnedSecurityKeyController(OwnedRsaLoginCredentialDecoder decoder) {
        this.decoder = Objects.requireNonNull(decoder, "decoder");
    }

    @GetMapping(path = "/open/security/public-key", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> publicKey() {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(decoder.getPublicKey());
    }
}
