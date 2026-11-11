package com.sunwayworld.owned.report.engine;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/** Portal authentication and per-menu template/session boundaries around the isolated report webapp. */
public final class ReportEngineAccessFilter implements Filter {
    public static final String TEMPLATE = "kf_htrwtjb/LIMS-客服-委托汇总表.cpt";
    public static final String CONTRACT_TEMPLATE = "kf_htxxb/LIMS-客服-合同汇总表.cpt";
    private static final Map<String,String> MENUS;
    static {
        Map<String,String> menus=new HashMap<>();
        menus.put(TEMPLATE,"20250800005"); menus.put(CONTRACT_TEMPLATE,"20250800006");
        MENUS=Collections.unmodifiableMap(menus);
    }
    private static final String OWNER = "owned.portal.report.owner.v1";
    private static final Set<String> OPERATIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "widget", "fr_dialog", "fr_paramstpl", "page_content", "get_pages", "page", "close")));
    private static final Set<String> EMBEDDED = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "finereport.css", "toolbar.css", "finereport.main.js", "finereport.chart.js", "finereport.plugin.js")));
    private static final Set<String> CLOUD = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "polyfill.min.js", "fp.min.js", "fine.mark.init.js")));

    interface IdentitySource { Identity authenticate(HttpServletRequest request, HttpServletResponse response); }
    static final class Identity {
        final String name, owner;
        final Set<String> templates;
        Identity(String name, String owner) { this(name,owner,MENUS.keySet()); }
        Identity(String name,String owner,Set<String> templates) { this.name=name;this.owner=owner;this.templates=templates; }
    }
    // CREATE runs on Fine's executor. Correlate its raw request-parameter map
    // with a one-use server nonce; merged execution parameters omit this value.
    private static final String NONCE="__owned_portal_report_nonce";
    private static final java.security.SecureRandom RANDOM=new java.security.SecureRandom();
    private static final class Pending {
        final Identity identity;
        final String template;
        final long expires=System.nanoTime()+java.util.concurrent.TimeUnit.MINUTES.toNanos(5);
        Pending(Identity identity,String template) { this.identity=identity;this.template=template; }
    }
    private final java.util.concurrent.ConcurrentMap<String,Pending> pending=new java.util.concurrent.ConcurrentHashMap<>();
    private final IdentitySource identities;
    private volatile ClassLoader engineLoader;
    private volatile Method findSession, getAttribute, setAttribute, relativePath, requestParameters;

    public ReportEngineAccessFilter() { this(ReportEngineAccessFilter::portalIdentity); }
    ReportEngineAccessFilter(IdentitySource identities) { this.identities=identities; }

    public void configureEngine(ClassLoader loader) throws ReflectiveOperationException {
        Class<?> provider=loader.loadClass("com.fr.stable.web.SessionProvider");
        findSession=loader.loadClass("com.fr.web.core.SessionPoolManager").getMethod("getSessionIDInfor",String.class,Class.class);
        getAttribute=provider.getMethod("getAttribute",String.class);
        setAttribute=provider.getMethod("setAttribute",String.class,Object.class);
        relativePath=provider.getMethod("getRelativePath");
        requestParameters=loader.loadClass("com.fr.web.core.TemplateSessionIDInfo").getMethod("getURLParaMap");
        engineLoader=loader;
    }

    public Consumer<String> sessionCreated() {
        return sid -> {
            try {
                Object report=session(sid);
                if(report==null || !target(report)) return;
                Object parameters=requestParameters.invoke(report);
                Object nonce=parameters instanceof Map ? ((Map<?,?>)parameters).get(NONCE) : null;
                if(!(nonce instanceof String) || !((String)nonce).matches("[A-Za-z0-9_-]{43}")) {
                    return;
                }
                Pending request=pending.get(nonce);
                if(request==null || !request.template.equals(templatePath(report))) return;
                synchronized(report) {
                    if(request.expires<System.nanoTime()) {pending.remove(nonce,request);return;}
                    Object existing=getAttribute.invoke(report,OWNER);
                    if(existing!=null && !request.identity.owner.equals(existing)) throw new SecurityException();
                    if(!pending.remove(nonce,request)) return;
                    setAttribute.invoke(report,OWNER,request.identity.owner);
                }
            } catch(ReflectiveOperationException failure) {
                throw new IllegalStateException("Report session ownership unavailable");
            }
        };
    }

    private Object session(String sid) throws ReflectiveOperationException {
        return findSession.invoke(null,sid,engineLoader.loadClass("com.fr.stable.web.SessionProvider"));
    }
    private String templatePath(Object report) throws ReflectiveOperationException {
        Object path=relativePath.invoke(report);
        if(!(path instanceof String)) return null;
        String value=(String)path;
        if(value.startsWith("reportlets/")) value=value.substring("reportlets/".length());
        return MENUS.containsKey(value)?value:null;
    }
    private boolean target(Object report) throws ReflectiveOperationException { return templatePath(report)!=null; }
    private boolean owns(String sid,Identity identity) throws ReflectiveOperationException {
        Object report=session(sid);
        return report!=null && identity.templates.contains(templatePath(report)) && identity.owner.equals(getAttribute.invoke(report,OWNER));
    }
    private static String single(HttpServletRequest request,String key) {
        String[] values=request.getParameterValues(key);
        if (values==null) return null;
        if (values.length!=1 || values[0]==null || values[0].indexOf('\0')>=0) throw new IllegalArgumentException();
        return values[0];
    }
    private static String sessionId(HttpServletRequest request) {
        String parameter=single(request,"sessionID");
        java.util.Enumeration<String> headers=request.getHeaders("sessionID");
        String header=null;
        if (headers!=null && headers.hasMoreElements()) {
            header=headers.nextElement();
            if (headers.hasMoreElements() || header==null || header.isEmpty()) throw new IllegalArgumentException();
        }
        if (parameter!=null && header!=null && !parameter.equals(header)) throw new SecurityException();
        return parameter!=null ? parameter : header;
    }
    private static void validateQueryContainer(HttpServletRequest request) {
        String value=single(request,"__parameters__");
        if(value==null) return;
        if(value.length()>32768) throw new SecurityException();
        // Fine encodes non-ASCII strings and even array delimiters as hex
        // brackets in this form field. Decode through its normal public codec.
        try {
            ClassLoader loader=Thread.currentThread().getContextClassLoader();
            String decoded=(String)loader.loadClass("com.fr.stable.CodeUtils").getMethod("decodeText",String.class).invoke(null,value);
            com.alibaba.fastjson.JSONObject parameters=com.alibaba.fastjson.JSON.parseObject(decoded);
            if(parameters==null) throw new SecurityException();
            for(String key:parameters.keySet()) {
                if(!key.matches("[A-Za-z][A-Za-z0-9_]{0,63}")) throw new SecurityException();
                Object supplied=parameters.get(key);
                if(supplied instanceof Map) throw new SecurityException();
                if(supplied instanceof java.util.List) {
                    for(Object item:(java.util.List<?>)supplied) if(!(item instanceof String || item instanceof Number || item instanceof Boolean)) throw new SecurityException();
                } else if(supplied!=null && !(supplied instanceof String || supplied instanceof Number || supplied instanceof Boolean)) throw new SecurityException();
                String normalized=key.toLowerCase(java.util.Locale.ROOT);
                if(normalized.equals("sessionid") || normalized.equals("viewlet") || normalized.equals("reportlet")
                    || normalized.equals("formlet") || normalized.equals("logintype") || normalized.equals("op")
                    || normalized.equals("__parameters__") || normalized.equals(NONCE)) throw new SecurityException();
            }
        } catch(ReflectiveOperationException | com.alibaba.fastjson.JSONException failure) { throw new SecurityException(); }
    }
    private static boolean resource(String value) {
        return value!=null && value.startsWith("/com/fr/web/") && !value.contains("..")
            && !value.contains("\\") && !value.contains("%") && !value.contains(":");
    }
    private static boolean resourceRequest(HttpServletRequest request,String path,String op) {
        if (!"GET".equals(request.getMethod())) return false;
        if ("/decision/resources".equals(path)) return resource(single(request,"path"));
        if ("/decision/v1/cloud/file".equals(path)) return CLOUD.contains(single(request,"resource"));
        if (!"/decision/view/report".equals(path)) return false;
        if ("emb".equals(op)) return EMBEDDED.contains(single(request,"resource"));
        if ("resource".equals(op)) return resource(single(request,"resource"));
        return "toolbar_icon".equals(op) && "toolbar-image.png".equals(single(request,"id"));
    }

    @Override public void init(FilterConfig config) { }
    @Override public void destroy() { pending.clear(); }
    @Override public void doFilter(ServletRequest raw,ServletResponse output,FilterChain chain) throws IOException,ServletException {
        HttpServletRequest request=(HttpServletRequest)raw;
        HttpServletResponse response=(HttpServletResponse)output;
        if (engineLoader==null) { response.sendError(503); return; }
        Identity identity;
        ClassLoader previous=Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            identity=identities.authenticate(request,response);
        } catch (AuthenticationException rejected) { response.sendError(401); return;
        } catch (SecurityException rejected) { response.sendError(403); return;
        } catch (RuntimeException unavailable) { response.sendError(503); return;
        } finally { Thread.currentThread().setContextClassLoader(previous); }
        if (identity==null) { response.sendError(401); return; }
        try {
            String path=request.getRequestURI().substring(request.getContextPath().length());
            String viewlet=single(request,"viewlet"), op=single(request,"op"), sid=sessionId(request);
            // The original engine accepts form parameters. Decode them exactly once
            // through Servlet APIs; never reinterpret an encoded viewlet or ID.
            if (single(request,"sessionid")!=null || single(request,"loginType")!=null) throw new SecurityException();
            if (viewlet!=null && !identity.templates.contains(viewlet)) throw new SecurityException();
            for(String name:request.getParameterMap().keySet()) {
                if ("__owned_portal_report_nonce".equalsIgnoreCase(name) || (sid==null && "__parameters__".equalsIgnoreCase(name))) throw new SecurityException();
            }
            boolean initial="/decision/view/report".equals(path) && "GET".equals(request.getMethod()) && identity.templates.contains(viewlet) && op==null && sid==null;
            validateQueryContainer(request);
            boolean ownedSession=sid!=null && sid.matches("[A-Za-z0-9-]{16,128}") && owns(sid,identity);
            boolean continuation=ownedSession && (("/decision/view/report".equals(path) && OPERATIONS.contains(op))
                || ("POST".equals(request.getMethod()) && "/decision/preview/info/collect".equals(path)));
            boolean telemetry=("GET".equals(request.getMethod()) && ("/decision/detect/enable".equals(path) || "/decision/esd/last/updated".equals(path)))
                || ("POST".equals(request.getMethod()) && "/decision/report/check/frontEndException".equals(path));
            if (!(initial || continuation || resourceRequest(request,path,op) || telemetry)) throw new SecurityException();
            final String nonce;
            if(initial) {
                pending.entrySet().removeIf(entry -> entry.getValue().expires<System.nanoTime());
                byte[] bytes=new byte[32]; RANDOM.nextBytes(bytes);
                nonce=java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                synchronized(pending) {
                    if(pending.size()>=1024 || pending.putIfAbsent(nonce,new Pending(identity,viewlet))!=null) throw new IllegalStateException("Report request capacity reached");
                }
            } else {nonce=null;}
            final Principal principal=() -> identity.name;
            HttpServletRequest wrapper=new HttpServletRequestWrapper(request) {
                @Override public String getParameter(String name) { return NONCE.equals(name)?nonce:super.getParameter(name); }
                @Override public String[] getParameterValues(String name) { return NONCE.equals(name)?(nonce==null?null:new String[]{nonce}):super.getParameterValues(name); }
                @Override public Map<String,String[]> getParameterMap() {
                    Map<String,String[]> values=new HashMap<>(super.getParameterMap());values.remove(NONCE);
                    if(nonce!=null) values.put(NONCE,new String[]{nonce});return Collections.unmodifiableMap(values);
                }
                @Override public java.util.Enumeration<String> getParameterNames() { return Collections.enumeration(getParameterMap().keySet()); }
                @Override public Principal getUserPrincipal() { return principal; }
                @Override public String getRemoteUser() { return identity.name; }
            };
            chain.doFilter(wrapper,response);
        } catch (IllegalArgumentException | SecurityException rejected) {
            if (!response.isCommitted()) response.sendError(403);
        } catch (ReflectiveOperationException failure) {
            throw new ServletException("Report session ownership unavailable",failure);
        }
    }

    private static Identity portalIdentity(HttpServletRequest request,HttpServletResponse response) {
        HttpServletRequest oldRequest=LocalContextHelper.getHttpServletRequest();
        HttpServletResponse oldResponse=LocalContextHelper.getHttpServletResponse();
        SecurityContext oldContext=SecurityContextHolder.getContext();
        try {
            LocalContextHelper.setServletRequest(request); LocalContextHelper.setServletResponse(response);
            String token=AuthenticationHelper.getLoginUserToken();
            Authentication authentication=AuthenticationHelper.getCacheAuthentication(token);
            if (authentication==null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken
                    || !(authentication.getPrincipal() instanceof LoginUser)) return null;
            String offline=AuthenticationHelper.getOfflineCategory(token);
            if ("OFFLINE".equals(offline) || "OFFLINE_PRELOGIN".equals(offline)) return null;
            LoginUser user=(LoginUser)authentication.getPrincipal();
            SecurityContext context=SecurityContextHolder.createEmptyContext(); context.setAuthentication(authentication); SecurityContextHolder.setContext(context);
            DefaultUserDetailsChecker.check(user);
            Set<String> templates=new HashSet<>();
            ApplicationContextHelper.getBean(CoreMenuService.class).selectCurrentUserMenuList().forEach(menu -> MENUS.forEach((template,id) -> {if(id.equals(menu.getId())) templates.add(template);}));
            if(templates.isEmpty()) throw new SecurityException();
            String owner=hash(token+"\n"+user.getUsername()+"\n"+user.getOrgId()+"\n"+user.getRoleId());
            return new Identity(user.getUsername(),owner,Collections.unmodifiableSet(templates));
        } finally {
            SecurityContextHolder.setContext(oldContext);
            if(oldRequest==null) LocalContextHelper.removeServletRequest(); else LocalContextHelper.setServletRequest(oldRequest);
            if(oldResponse==null) LocalContextHelper.removeServletResponse(); else LocalContextHelper.setServletResponse(oldResponse);
        }
    }
    private static String hash(String value) {
        try {
            byte[] bytes=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result=new StringBuilder(); for(byte b:bytes) result.append(String.format("%02x",b&255)); return result.toString();
        } catch(java.security.NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }
}
