package com.sunwayworld.emc.framework.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;

/**
 * Enforces existing menu permissions for the first-acceptance query domains.
 * The private framework's global URL guard cannot run against this recovered
 * Oracle schema because it compares the CLOB URL column with '='.
 */
public class QueryAccessFilter extends OncePerRequestFilter {

    private static final String EMPLOYEE_PATH = "/secure/emc/module/mdm/emp/emps/";
    private static final String EQUIPMENT_PATH = "/secure/emc/module/mdm/equipt/equipts/";
    private static final String CONSUMABLE_PATH = "/secure/emc/module/item/consume/consumes/";

    private static final Set<String> EMPLOYEE_MENUS = Collections.singleton("100001");
    private static final Set<String> EQUIPMENT_MANAGEMENT_MENUS = Collections.singleton("101501");
    private static final Set<String> EQUIPMENT_QUERY_MENUS = Collections.singleton("20251000001");
    private static final Set<String> CONSUMABLE_MANAGEMENT_MENUS = Collections.singleton("430201");
    private static final Set<String> CONSUMABLE_QUERY_MENUS = Collections.singleton("20250800003");

    private final CoreMenuService menuService;

    public QueryAccessFilter(CoreMenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = pathWithinApplication(request);
        return !path.startsWith(EMPLOYEE_PATH)
                && !path.startsWith(EQUIPMENT_PATH)
                && !path.startsWith(CONSUMABLE_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!hasAuthenticatedPrincipal()) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = pathWithinApplication(request);
        Set<String> currentMenuIds = currentMenuIds();
        boolean permitted;
        if (path.startsWith(EMPLOYEE_PATH)) {
            permitted = containsAny(currentMenuIds, EMPLOYEE_MENUS);
        } else if (path.startsWith(EQUIPMENT_PATH)) {
            permitted = containsAny(currentMenuIds, EQUIPMENT_MANAGEMENT_MENUS)
                    || (isReadOnlyRequest(request) && containsAny(currentMenuIds, EQUIPMENT_QUERY_MENUS));
        } else {
            permitted = containsAny(currentMenuIds, CONSUMABLE_MANAGEMENT_MENUS)
                    || (isReadOnlyRequest(request) && containsAny(currentMenuIds, CONSUMABLE_QUERY_MENUS));
        }

        if (!permitted) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");
            return;
        }
        filterChain.doFilter(request, response);
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

    private Set<String> currentMenuIds() {
        List<CoreMenuBean> menus = menuService.selectCurrentUserMenuList();
        if (menus == null) {
            return Collections.emptySet();
        }
        return menus.stream().map(CoreMenuBean::getId).collect(Collectors.toSet());
    }

    private boolean isReadOnlyRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String path = pathWithinApplication(request);
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            String relative = path.startsWith(EQUIPMENT_PATH) ? path.substring(EQUIPMENT_PATH.length())
                    : path.startsWith(CONSUMABLE_PATH) ? path.substring(CONSUMABLE_PATH.length()) : "";
            if ("page/edit-list".equals(relative)) {
                return "1".equals(request.getParameter("readonly"));
            }
            return !relative.isEmpty() && relative.indexOf('/') < 0;
        }
        return "POST".equals(method) && path.contains("/queries");
    }

    private boolean containsAny(Collection<String> actual, Set<String> expected) {
        for (String menuId : expected) {
            if (actual.contains(menuId)) {
                return true;
            }
        }
        return false;
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        return requestUri.startsWith(contextPath) ? requestUri.substring(contextPath.length()) : requestUri;
    }
}
