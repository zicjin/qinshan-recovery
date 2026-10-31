package com.sunwayworld.owned.internal.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Same-application session heartbeat used by the original Gikam shell. */
@RestController
public final class OwnedKeepOnlineController {
    @PostMapping(path = "/core/module/sys/statuses/action/keep-online")
    public ResponseEntity<Void> keepOnline(HttpServletRequest request) {
        // The supplier endpoint intentionally returns an empty 200 response.
        // Session validity is enforced by the subsequent protected calls; the
        // heartbeat itself must not create a second authentication seam.
        return ResponseEntity.ok().build();
    }
}
