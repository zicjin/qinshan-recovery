package com.fr.stable.web;

/** Minimal public API fixture. No third-party implementation is included. */
public interface SessionProvider {
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
    String getRelativePath();
    java.util.Map<String, Object> getOriginalParameterMap();
}
