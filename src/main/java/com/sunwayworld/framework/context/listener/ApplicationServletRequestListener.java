package com.sunwayworld.framework.context.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@WebListener
@Component
public class ApplicationServletRequestListener implements ServletRequestListener {
    private static final Logger logger = LogManager.getLogger(ApplicationServletRequestListener.class);
    
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        Runtime run = Runtime.getRuntime();
        
        long max = run.maxMemory() / 1048576; // 最大内存
        long total = run.totalMemory() / 1048576; // 已分配内存
        long free = run.freeMemory() / 1048576; // 已分配内存中剩余空间
        
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        
        long oldTotal = (long) request.getAttribute("SYS_MEMORY_TOTAL");
        long oldFree = (long) request.getAttribute("SYS_MEMORY_FREE");
        
        /*logger.info("请求路径：{} 最大内存：{} 已分配内存：{} -> {} / {} 已用内存：{} -> {} / {} ",
                request.getRequestURI(),
                max,
                oldTotal, total, (total - oldTotal),
                (oldTotal - oldFree), (total - free), ((total - free) - (oldTotal - oldFree)));*/
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        Runtime run = Runtime.getRuntime();
        
        long total = run.totalMemory() / 1048576; // 已分配内存
        long free = run.freeMemory() / 1048576; // 已分配内存中剩余空间
        
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        
        request.setAttribute("SYS_MEMORY_TOTAL", total);
        request.setAttribute("SYS_MEMORY_FREE", free);
    }
}