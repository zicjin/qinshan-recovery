package com.sunwayworld.owned.internal.smoke;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Minimal observable endpoint used only by the owned bootstrap smoke test. */
@RestController
public class OwnedRuntimeSmokeController {
    @GetMapping("/owned-runtime/health")
    public String health() { return "owned-runtime"; }
}
