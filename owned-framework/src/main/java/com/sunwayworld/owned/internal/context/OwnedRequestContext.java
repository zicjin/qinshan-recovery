package com.sunwayworld.owned.internal.context;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/** Thread-bound request bridge for owned compatibility helpers. */
public final class OwnedRequestContext {
    private static final ThreadLocal<ServletRequest> REQUEST = new ThreadLocal<ServletRequest>();
    private OwnedRequestContext() { }
    public static void setServletRequest(ServletRequest request) { REQUEST.set(request); }
    public static HttpServletRequest getHttpServletRequest() { return (HttpServletRequest) REQUEST.get(); }
    public static void removeServletRequest() { REQUEST.remove(); }
    public static Scope push(ServletRequest request) { ServletRequest previous=REQUEST.get(); REQUEST.set(request); return new Scope(previous); }
    public static final class Scope implements AutoCloseable {
        private final ServletRequest previous;
        private boolean closed;
        private Scope(ServletRequest previous) { this.previous=previous; }
        public void close() { if(!closed){closed=true;if(previous==null)REQUEST.remove();else REQUEST.set(previous);} }
    }
}
