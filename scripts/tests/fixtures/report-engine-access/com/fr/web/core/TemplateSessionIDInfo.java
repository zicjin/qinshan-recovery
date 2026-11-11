package com.fr.web.core;

import com.fr.stable.web.SessionProvider;
import java.util.HashMap;
import java.util.Map;

/** Distinguishes raw request parameters from merged execution parameters. */
public abstract class TemplateSessionIDInfo implements SessionProvider {
    public final Map<String, Object> rawRequestParameters = new HashMap<>();
    public Map<String, Object> getURLParaMap() { return rawRequestParameters; }
}
