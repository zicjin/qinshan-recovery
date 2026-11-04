/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.core.support;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class CoreMonitorHelper {
    private static final Long CPU_SLEEP_TIME = 500L;

    public static CoreMonitorServerBean getServerInfo() {
        CoreMonitorServerBean server = new CoreMonitorServerBean();
        String serverId = CoreMonitorHelper.getServerId();
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        String osName = os.getFamily() + " " + os.getVersionInfo().getVersion();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        CentralProcessor cpu = hardwareAbstractionLayer.getProcessor();
        String cpuInfo = cpu.getProcessorIdentifier().getName();
        Long memory = hardwareAbstractionLayer.getMemory().getTotal();
        List<OSFileStore> oSFileStoreList = systemInfo.getOperatingSystem().getFileSystem().getFileStores();
        Long disk = oSFileStoreList.stream().collect(Collectors.summingLong(s -> s.getTotalSpace()));
        server.setId(serverId);
        server.setOsName(osName);
        server.setCpu(cpuInfo);
        server.setCpuCoreQty(NumberUtils.parseLong(cpu.getLogicalProcessorCount()));
        server.setMemory(FormatUtil.formatBytes(memory));
        server.setDisk(FormatUtil.formatBytes(disk));
        return server;
    }

    public static CoreMonitorServerRuntimeBean getServerRuntimeInfo() {
        CoreMonitorServerRuntimeBean coreServerRuntime = new CoreMonitorServerRuntimeBean();
        String serverId = CoreMonitorHelper.getServerId();
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        CentralProcessor cpu = hardwareAbstractionLayer.getProcessor();
        long[] ticks = cpu.getSystemCpuLoadTicks();
        try {
            Thread.sleep(CPU_SLEEP_TIME);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        double serverCpuUtilization = cpu.getSystemCpuLoadBetweenTicks(ticks) * 100.0;
        GlobalMemory memory = hardwareAbstractionLayer.getMemory();
        double serverRamUtilization = (1.0 - (double)memory.getAvailable() / (double)memory.getTotal()) * 100.0;
        List<OSFileStore> fileStoreList = systemInfo.getOperatingSystem().getFileSystem().getFileStores();
        Long usableSpace = fileStoreList.stream().collect(Collectors.summingLong(s -> s.getUsableSpace()));
        Long totalSpace = fileStoreList.stream().collect(Collectors.summingLong(s -> s.getTotalSpace()));
        double serverDiskUtilization = (1.0 - (double)usableSpace.longValue() / (double)totalSpace.longValue()) * 100.0;
        coreServerRuntime.setServerId(serverId);
        coreServerRuntime.setServerCpuUtilization(NumberUtils.parseLong(serverCpuUtilization));
        coreServerRuntime.setServerRamUtilization(NumberUtils.parseLong(serverRamUtilization));
        coreServerRuntime.setServerDiskUtilization(NumberUtils.parseLong(serverDiskUtilization));
        return coreServerRuntime;
    }

    public static CoreMonitorAppBean getAppInfo() {
        CoreMonitorAppBean coreApp = new CoreMonitorAppBean();
        String serverId = CoreMonitorHelper.getServerId();
        String appId = CoreMonitorHelper.getAppId();
        Long port = NumberUtils.parseLong(ApplicationContextHelper.getEnvironment().getProperty("server.port"));
        coreApp.setId(appId);
        coreApp.setServerId(serverId);
        coreApp.setPort(port);
        return coreApp;
    }

    public static CoreMonitorAppRuntimeBean getAppRuntimeInfo() {
        CoreMonitorAppRuntimeBean coreAppRuntime = new CoreMonitorAppRuntimeBean();
        String serverId = CoreMonitorHelper.getServerId();
        String appId = CoreMonitorHelper.getAppId();
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        int logicalProcessorCount = systemInfo.getHardware().getProcessor().getLogicalProcessorCount();
        String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        OSProcess process = operatingSystem.getProcess(NumberUtils.parseInt(PID));
        Long memory = process.getResidentSetSize();
        Long totalMemory = systemInfo.getHardware().getMemory().getTotal();
        double appRamUtilization = (double)memory.longValue() / (double)totalMemory.longValue() * 100.0;
        try {
            Thread.sleep(CPU_SLEEP_TIME);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        OSProcess delayProcess = operatingSystem.getProcess(NumberUtils.parseInt(PID));
        double appCpuUtilization = delayProcess.getProcessCpuLoadBetweenTicks(process) * 100.0 / (double)logicalProcessorCount;
        coreAppRuntime.setAppId(appId);
        coreAppRuntime.setServerId(serverId);
        coreAppRuntime.setAppCpuUtilization(NumberUtils.parseLong(appCpuUtilization));
        coreAppRuntime.setAppRamUtilization(NumberUtils.parseLong(appRamUtilization));
        return coreAppRuntime;
    }

    public static String getStackInfo() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        int pid = Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
        String cmdStr = "jstack -l " + pid;
        return SystemUtils.execAndReturn(cmdStr);
    }

    public static String buildMessageInfo(String messageSource, String sourceName, String category, String level) {
        StringBuilder info = new StringBuilder();
        info.append(messageSource).append(sourceName).append(category).append(level).append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return info.toString();
    }

    public static String getServerId() {
        return ApplicationContextHelper.getEnvironment().getProperty("sunway.app.host.name");
    }

    public static String getAppId() {
        return ApplicationContextHelper.getEnvironment().getProperty("spring.application.name");
    }
}
