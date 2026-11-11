package com.sunwayworld.emc.framework.security;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.io.filter.FileSecureFilter;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.FilterChain;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

/** Actual doFilter chain, with only DAO/menu/signature dependencies replaced. */
public final class TempFileContentGuardCheck {
    private static final String HEX32 = "0123456789abcdef0123456789abcdef";
    private static final String UUID36 = "2977bb87-23d5-488d-92ea-5be89e793d80";
    private static final String NAME = "%E5%BF%AB%E9%80%92%E5%8D%95%E5%8F%B7.xlsx";
    private static final byte[] BYTES = "actual-file-handler-bytes".getBytes(StandardCharsets.UTF_8);
    private static int checks;

    public static void main(String[] args) throws Exception {
        GenericApplicationContext context = new GenericApplicationContext();
        context.refresh();
        ApplicationContextHelper.setApplicationContext(context);
        org.springframework.context.support.StaticMessageSource messages = new org.springframework.context.support.StaticMessageSource();
        messages.setUseCodeAsDefaultMessage(true);
        new com.sunwayworld.framework.i18n.I18nHelper().setMessageSource(messages);
        try {
            // These come from actual producers, not an invented generic path grammar.
            String[] allowed = {
                "/files/temp/1794414561974/"+UUID36+"/"+NAME, // LCDP/GenericTemplateService
                "/files/temp/20261112/"+UUID36+"/"+NAME, // ExcelHelper/CoreFileUtils
                "/files/temp/20261112/0123456789abcdef/archive.zip", // CoreFileService.pack
                "/files/temp/20261112/"+HEX32+"/code.png", // QRCodeGenerator
                "/files/temp/1794414561974/01234567/composed.pdf", // PfArchiveFileService
                "/files/temp/01234567/archive.pdf", // EmcArchiveFileService
                "/files/secure/20261112/"+HEX32+"/"+NAME
            };
            for (String path : allowed) {
                perform(path, false, false, true, false, null, true, false);
                perform(path, false, false, true, true, "approved-signature", true, false);
                perform(path, false, false, true, true, null, false, false);
                perform(path, false, false, true, true, "wrong-signature", false, false);
                perform(path, false, false, false, true, null, false, false);
                perform(path, false, false, false, true, "wrong-signature", false, false);
            }
            // A temp-shaped URL does not bypass personnel association or menu checks.
            for (String path : allowed) {
                perform(path, true, false, true, false, null, false, true);
                perform(path, true, false, false, true, "approved-signature", false, true);
                perform(path, true, true, true, true, "approved-signature", true, true);
            }
            String[] denied = {
                "/files/temp/1794414561974/not-a-uuid/"+NAME,
                "/files/temp/20261112/01234567/"+NAME,
                "/files/temp/1794414561974/0123456789abcdef/"+NAME,
                "/files/temp/"+UUID36+"/"+NAME,
                "/files/secure/1794414561974/"+UUID36+"/"+NAME,
                "/files/temp//1794414561974/"+UUID36+"/"+NAME,
                "/files/temp;ignored/1794414561974/"+UUID36+"/"+NAME,
                "/files/te%6dp/1794414561974/"+UUID36+"/"+NAME,
                "/files/temp/1794414561974/"+UUID36+"/%2e%2e%2fsecret",
                "/files/temp/1794414561974/"+UUID36+"/../secret",
                "/files/temp/1794414561974/"+UUID36+"/sub/file.xlsx",
                "/files/temp/1794414561974/"+UUID36+"/%5csecret"
            };
            for (String path : denied) perform(path, false, false, true, false, null, false, false);
            perform("/files/temp/1794414561974/"+UUID36+"/%252e%252e%252fsecret", false, false, true, false, null, false, false);
            perform("/files/temp/1794414561974/"+UUID36+"/%00", false, false, true, false, null, false, false);
            // HEAD and a servlet context still use the actual filters and metadata boundary.
            perform("/files/temp/1794414561974/"+UUID36+"/report(checked).xlsx", false, false, true,
                    true, "approved-signature", true, true, "HEAD", "/lims");
            perform("/files/temp/01234567/report.xlsx", true, false, true,
                    true, "approved-signature", false, true, "HEAD", "/lims");
            System.out.println("TempFileContentGuardCheck PASS ("+checks+" servlet-chain checks)");
        } finally {
            SecurityContextHolder.clearContext();
            LocalContextHelper.removeServletRequest();
            ApplicationContextHelper.setApplicationContext(null);
            context.close();
        }
    }

    private static void perform(String path, boolean personnel, boolean hasMenu, boolean authenticated,
            boolean signatureEnabled, String signature, boolean allowed, boolean mustLookup) throws Exception {
        perform(path, personnel, hasMenu, authenticated, signatureEnabled, signature, allowed, mustLookup, "GET", "");
    }

    private static void perform(String path, boolean personnel, boolean hasMenu, boolean authenticated,
            boolean signatureEnabled, String signature, boolean allowed, boolean mustLookup,
            String httpMethod, String contextPath) throws Exception {
        AtomicInteger lookups = new AtomicInteger();
        CoreFileService files = (CoreFileService) Proxy.newProxyInstance(TempFileContentGuardCheck.class.getClassLoader(),
                new Class<?>[]{CoreFileService.class}, (proxy, method, arguments) -> {
                    if ("selectFirstByFilter".equals(method.getName())) {
                        lookups.incrementAndGet();
                        String[] parts = org.springframework.web.util.UriUtils.decode(path, StandardCharsets.UTF_8).split("/", -1);
                        String expected = parts[parts.length - 2];
                        SearchFilter query = (SearchFilter) arguments[0];
                        if (query.getStdFilterList().size() != 1
                                || query.getStdFilterList().get(0).getMap().values().stream().noneMatch(expected::equals)) {
                            throw new AssertionError("file metadata lookup does not use original irregularName");
                        }
                        if (!personnel) return null;
                        CoreFileBean file = new CoreFileBean();
                        file.setTargetId("T_CORE_USER$fixture");
                        return file;
                    }
                    throw new AssertionError("unexpected file service invocation: "+method.getName());
                });
        CoreMenuService menus = (CoreMenuService) Proxy.newProxyInstance(TempFileContentGuardCheck.class.getClassLoader(),
                new Class<?>[]{CoreMenuService.class}, (proxy, method, arguments) -> {
                    if ("selectCurrentUserMenuList".equals(method.getName())) {
                        if (!hasMenu) return Collections.emptyList();
                        CoreMenuBean menu = new CoreMenuBean(); menu.setId("100001");
                        return Collections.singletonList(menu);
                    }
                    throw new AssertionError("unexpected menu service invocation");
                });
        CoreFileManager manager = (CoreFileManager) Proxy.newProxyInstance(TempFileContentGuardCheck.class.getClassLoader(),
                new Class<?>[]{CoreFileManager.class}, (proxy, method, arguments) -> {
                    if ("getSecurity".equals(method.getName())) return "approved-signature";
                    throw new AssertionError("unexpected signature dependency invocation");
                });
        FileSecureFilter signed = new FileSecureFilter();
        ReflectionTestUtils.setField(signed, "fileManager", manager);
        ReflectionTestUtils.setField(signed, "accessPermissionEnabled", signatureEnabled);
        PersonnelSignatureFileFilter filter = new PersonnelSignatureFileFilter(files, menus);
        MockHttpServletRequest request = new MockHttpServletRequest(httpMethod, contextPath+path);
        request.setContextPath(contextPath);
        ReflectionTestUtils.setField(com.sunwayworld.framework.utils.ServletUtils.class, "contextPath", contextPath);
        if (signature != null) request.addParameter("security", signature);
        MockHttpServletResponse response = new MockHttpServletResponse();
        if (authenticated) SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("fixture-user", "unused", Collections.emptyList()));
        else SecurityContextHolder.clearContext();
        LocalContextHelper.setServletRequest(request);
        AtomicBoolean reached = new AtomicBoolean();
        FilterChain handler = (req, res) -> { reached.set(true); res.getOutputStream().write(BYTES); };
        try {
            filter.doFilter(request, response, (req, res) -> signed.doFilter(req, res, handler));
        } catch (ApplicationRuntimeException denied) {
            if (allowed) throw new AssertionError("valid source-produced path rejected by signature filter: "+path, denied);
        }
        if (allowed != reached.get() || (allowed && !Arrays.equals(BYTES, response.getContentAsByteArray()))) {
            throw new AssertionError("file-chain decision/bytes mismatch: allowed="+allowed+", status="+response.getStatus()+", path="+path);
        }
        if (!allowed && Arrays.equals(BYTES, response.getContentAsByteArray())) throw new AssertionError("denied content leaked");
        if (mustLookup && lookups.get() == 0) throw new AssertionError("personnel metadata lookup bypassed: "+path);
        checks++;
        SecurityContextHolder.clearContext();
        LocalContextHelper.removeServletRequest();
    }
}
