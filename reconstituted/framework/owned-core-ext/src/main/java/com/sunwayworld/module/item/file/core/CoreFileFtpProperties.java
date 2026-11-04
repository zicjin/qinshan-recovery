/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="sunway.config.file.ftp")
public class CoreFileFtpProperties {
    private String ip;
    private int port;
    private String user;
    private String password;
    private String folder;

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
