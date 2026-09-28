package com.sunwayworld.emc.framework.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;

/**
 * The private file filter validates a signed URL but does not map a personnel
 * signature back to the menu permission that revealed it. Keep that boundary
 * aligned with the personnel query page without changing other file domains.
 */
public class PersonnelSignatureFileFilter extends OncePerRequestFilter {

    private static final String FILE_RESOURCE_PREFIX = "/core/module/item/files/";
    private static final String FILE_QUERY_PATH = "/core/module/item/files/queries";
    private static final String EMC_FILE_RESOURCE_PREFIX = "/secure/emc/module/mdm/file/files/";
    private static final String EMC_FILE_QUERY_PATH = "/secure/emc/module/mdm/file/files/queries";
    private static final String PAGE_OFFICE_PREFIX = "/open/page-office/";
    private static final String COVER_PAGE_OFFICE_PREFIX = "/open/cover/page-office/";
    private static final String PACK_PATH = "/core/module/item/files/action/pack";
    private static final String FILE_CONTENT_PREFIX = "/files/";
    private static final String PERSONNEL_TARGET_PREFIX = "T_CORE_USER$";
    private static final String PERSONNEL_MENU_ID = "100001";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CoreFileService coreFileService;
    private final CoreMenuService menuService;

    public PersonnelSignatureFileFilter(CoreFileService coreFileService, CoreMenuService menuService) {
        this.coreFileService = coreFileService;
        this.menuService = menuService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = pathWithinApplication(request);
        return !path.startsWith(FILE_RESOURCE_PREFIX)
                && !path.startsWith(EMC_FILE_RESOURCE_PREFIX)
                && !path.startsWith(PAGE_OFFICE_PREFIX)
                && !path.startsWith(COVER_PAGE_OFFICE_PREFIX)
                && !path.startsWith(FILE_CONTENT_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = pathWithinApplication(request);
        // FileSecureFilter sees the raw servlet URI. Check the normalized
        // content path before the anonymous short-circuit so matrix parameters
        // or duplicate slashes cannot expose a personnel attachment.
        if (path.startsWith(FILE_CONTENT_PREFIX)
                && denyIfPersonnelFileWithoutMenu(findRequestedFile(request), response)) {
            return;
        }

        if (isPageOfficeRequest(path)) {
            if (!hasAuthenticatedPrincipal()) {
                deny(response);
                return;
            }
            if (denyIfPersonnelFileWithoutMenu(findRequestedFile(request), response)) {
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (!hasAuthenticatedPrincipal()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPackRequest(request, path)) {
            CachedBodyRequest cachedRequest = new CachedBodyRequest(request);
            if (!hasPersonnelMenu() && !isExplicitlyNonPersonnelPack(path, cachedRequest.getBody())) {
                deny(response);
                return;
            }
            filterChain.doFilter(cachedRequest, response);
            return;
        }

        if (isFileQuery(request, path)) {
            CachedBodyRequest cachedRequest = new CachedBodyRequest(request);
            if (!hasPersonnelMenu() && !isExplicitlyNonPersonnelQuery(cachedRequest.getBody())) {
                deny(response);
                return;
            }
            filterChain.doFilter(cachedRequest, response);
            return;
        }

        if (denyIfPersonnelFileWithoutMenu(findRequestedFile(request), response)) {
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean denyIfPersonnelFileWithoutMenu(CoreFileBean file, HttpServletResponse response) throws IOException {
        if (file != null && file.getTargetId() != null && file.getTargetId().startsWith(PERSONNEL_TARGET_PREFIX)
                && !hasPersonnelMenu()) {
            deny(response);
            return true;
        }
        return false;
    }

    private CoreFileBean findRequestedFile(HttpServletRequest request) {
        String path = pathWithinApplication(request);
        if (path.startsWith(FILE_RESOURCE_PREFIX) || path.startsWith(EMC_FILE_RESOURCE_PREFIX)) {
            String prefix = path.startsWith(FILE_RESOURCE_PREFIX)
                    ? FILE_RESOURCE_PREFIX : EMC_FILE_RESOURCE_PREFIX;
            String[] segments = path.substring(prefix.length()).split("/");
            if (segments.length > 0 && segments[0].matches("[0-9]+")) {
                return findFileById(segments[0]);
            }
            if (segments.length > 1 && "sunwayoffices".equals(segments[0])) {
                return findFileById(segments[1]);
            }
            return null;
        }

        if (isPageOfficeRequest(path)) {
            for (String parameter : Arrays.asList("fileId", "businessId", "sourceFileId")) {
                CoreFileBean file = findFileById(request.getParameter(parameter));
                if (file != null) {
                    return file;
                }
            }
            String fileUrl = request.getParameter("fileUrl");
            if (fileUrl != null && fileUrl.startsWith(FILE_CONTENT_PREFIX)) {
                return findFileByContentPath(fileUrl);
            }
            return null;
        }

        return findFileByContentPath(path);
    }

    private CoreFileBean findFileByContentPath(String path) {
        if (!path.startsWith(FILE_CONTENT_PREFIX)) {
            return null;
        }
        String relativePath = path.substring(FILE_CONTENT_PREFIX.length());
        String[] segments = relativePath.split("/", 4);
        if (segments.length != 4 || segments[2].isEmpty()) {
            return null;
        }
        // /files/{scope}/{date}/{md5Name}/{fileName}; the filename may be URL encoded.
        String md5Name = segments[2];
        return coreFileService.selectFirstByFilter(
                SearchFilter.instance().match("MD5NAME", md5Name).filter(MatchPattern.SEQ));
    }

    private boolean isFileQuery(HttpServletRequest request, String path) {
        return "POST".equals(request.getMethod())
                && (isQueryPath(path, FILE_QUERY_PATH) || isQueryPath(path, EMC_FILE_QUERY_PATH));
    }

    private boolean isPackRequest(HttpServletRequest request, String path) {
        return "POST".equals(request.getMethod())
                && (PACK_PATH.equals(path) || path.startsWith(PACK_PATH + "/"));
    }

    private boolean isPageOfficeRequest(String path) {
        return path.startsWith(PAGE_OFFICE_PREFIX) || path.startsWith(COVER_PAGE_OFFICE_PREFIX);
    }

    private boolean isQueryPath(String path, String queryPath) {
        return queryPath.equals(path) || path.startsWith(queryPath + "/");
    }

    /**
     * Generic file queries can include a signed URL in every returned row. For a user
     * without the personnel menu, only a concrete non-personnel target is safe to pass
     * through; a missing or flexible target could enumerate personnel signatures.
     */
    private boolean isExplicitlyNonPersonnelQuery(byte[] body) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(body);
            JsonNode filter = queryFilter(root);
            if (filter == null || !filter.isObject()) {
                return false;
            }

            boolean hasConcreteTarget = false;
            for (String name : Arrays.asList("targetId", "targetId_SEQ", "targetId_IN")) {
                JsonNode targetId = filter.get(name);
                if (targetId == null || targetId.isNull() || isBlank(targetId)) {
                    continue;
                }
                hasConcreteTarget = true;
                if (!targetsAreNonPersonnel(targetId)) {
                    return false;
                }
            }
            return hasConcreteTarget;
        } catch (IOException ignored) {
            return false;
        }
    }

    private boolean isExplicitlyNonPersonnelPack(String path, byte[] body) {
        // Packing can resolve arbitrary file IDs and create a new bearer URL.
        // Keep it restricted to the same personnel-management boundary; the
        // first-acceptance low-privilege role has no safe generic pack scope.
        return false;
    }

    private JsonNode queryFilter(JsonNode root) {
        if (root == null) {
            return null;
        }
        JsonNode parameter = root.get("p");
        JsonNode wrappedFilter = parameter == null ? null : parameter.get("f");
        return wrappedFilter == null ? root.get("f") : wrappedFilter;
    }

    private boolean isBlank(JsonNode targetId) {
        return targetId.isTextual() && targetId.asText().trim().isEmpty();
    }

    private boolean targetsAreNonPersonnel(JsonNode targetId) {
        if (targetId.isTextual()) {
            boolean found = false;
            for (String value : targetId.asText().split(",")) {
                String trimmed = value.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                found = true;
                if (trimmed.startsWith(PERSONNEL_TARGET_PREFIX)) {
                    return false;
                }
            }
            return found;
        }
        if (!targetId.isArray() || targetId.isEmpty()) {
            return false;
        }
        for (JsonNode item : targetId) {
            if (!item.isTextual() || isBlank(item) || !targetsAreNonPersonnel(item)) {
                return false;
            }
        }
        return true;
    }

    private CoreFileBean findFileById(String id) {
        if (id == null || !id.matches("[0-9]+")) {
            return null;
        }
        try {
            return coreFileService.selectByIdIfPresent(Long.valueOf(id));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void deny(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
    }

    private boolean hasAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            return true;
        }
        String loginUserId = LocalContextHelper.getLoginUserId();
        return loginUserId != null && !loginUserId.trim().isEmpty();
    }

    private boolean hasPersonnelMenu() {
        List<CoreMenuBean> menus = menuService.selectCurrentUserMenuList();
        if (menus == null) {
            return false;
        }
        Set<String> ids = menus.stream().map(CoreMenuBean::getId).collect(Collectors.toSet());
        return ids.contains(PERSONNEL_MENU_ID);
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        String path = requestUri.startsWith(contextPath) ? requestUri.substring(contextPath.length()) : requestUri;
        return normalizePath(path);
    }

    /**
     * Match the route after servlet path normalization. Without this, matrix
     * parameters and duplicate slashes can bypass a prefix check while Spring
     * still maps the request to the protected controller.
     */
    private String normalizePath(String path) {
        String[] segments = path.split("/", -1);
        StringBuilder normalized = new StringBuilder(path.length());
        for (String segment : segments) {
            int matrixParameter = segment.indexOf(';');
            if (matrixParameter >= 0) {
                segment = segment.substring(0, matrixParameter);
            }
            if (segment.isEmpty()) {
                continue;
            }
            normalized.append('/').append(segment);
        }
        return normalized.length() == 0 ? "/" : normalized.toString();
    }

    private static final class CachedBodyRequest extends HttpServletRequestWrapper {

        private final byte[] body;

        private CachedBodyRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.body = readBody(request.getInputStream());
        }

        private byte[] getBody() {
            return body;
        }

        @Override
        public ServletInputStream getInputStream() {
            return new CachedBodyInputStream(body);
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
        }

        private static byte[] readBody(InputStream input) throws IOException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        }
    }

    private static final class CachedBodyInputStream extends ServletInputStream {

        private final ByteArrayInputStream input;

        private CachedBodyInputStream(byte[] body) {
            this.input = new ByteArrayInputStream(body);
        }

        @Override
        public int read() {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            return input.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("ReadListener must not be null");
            }
            try {
                if (!isFinished()) {
                    listener.onDataAvailable();
                }
                if (isFinished()) {
                    listener.onAllDataRead();
                }
            } catch (IOException e) {
                listener.onError(e);
            }
        }
    }
}
