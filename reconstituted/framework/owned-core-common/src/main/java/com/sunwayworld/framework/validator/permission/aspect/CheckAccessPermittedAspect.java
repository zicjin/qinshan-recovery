/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.permission.aspect;

import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.request.bean.CoreRequestUrlBean;
import com.sunwayworld.module.admin.request.service.CoreRequestUrlService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value=100)
@ConditionalOnProperty(name={"sunway.api.check-access-permission"}, havingValue="true")
public class CheckAccessPermittedAspect {
    @Autowired
    private CoreRequestUrlService requestUrlService;
    @Autowired
    private CoreMenuService menuService;

    @Around(value="within(com.sunwayworld..resource..*)")
    public Object check(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) {
            return pjp.proceed();
        }
        String requestUrl = (String)request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern");
        if (StringUtils.isBlank(requestUrl)) {
            return pjp.proceed();
        }
        String method = request.getMethod();
        List<CoreRequestUrlBean> requestUrlList = this.getRequestUrlList(this.requestUrlService.selectListByRequestUrl(requestUrl), method);
        if (CollectionUtils.isEmpty(requestUrlList)) {
            return pjp.proceed();
        }
        List<CoreMenuBean> currentUserMenuList = this.menuService.selectCurrentUserMenuList();
        if (requestUrlList.stream().noneMatch(u -> currentUserMenuList.stream().anyMatch(m -> m.getId().equals(u.getMenuId())))) {
            throw new ApplicationWarningException("GIKAM.PERMISSION.UNAUTHORIZED", method);
        }
        return pjp.proceed();
    }

    private List<CoreRequestUrlBean> getRequestUrlList(List<CoreRequestUrlBean> matchUrlList, String method) {
        return matchUrlList.stream().filter(e -> StringUtils.isBlank(e.getMethod()) || e.getMethod().equals(method)).collect(Collectors.toList());
    }
}
