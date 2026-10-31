package com.sunwayworld.owned.internal.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Source-owned entrypoint for the original post-login workspace URL.
 *
 * The static index remains the project-owned page host.  An authenticated root
 * request follows the same redirect shape observed on sss2 supplier runtime;
 * the workspace path then forwards to that host without introducing an
 * /owned-web replacement URL.
 */
@Controller
public final class OwnedWorkspaceShellController {
    @GetMapping("/")
    public ModelAndView root(HttpServletRequest request) {
        return authenticated(request)
                ? new ModelAndView("redirect:/module/index/workspaces")
                : new ModelAndView("forward:/index.html");
    }

    @GetMapping("/module/index/workspaces")
    public ModelAndView workspaces(HttpServletRequest request) {
        return authenticated(request)
                ? new ModelAndView("forward:/workspace.html")
                : new ModelAndView("redirect:/");
    }

    private static boolean authenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null
                && session.getAttribute(OwnedIdentityHttpBoundary.SESSION_ATTRIBUTE) != null;
    }
}
