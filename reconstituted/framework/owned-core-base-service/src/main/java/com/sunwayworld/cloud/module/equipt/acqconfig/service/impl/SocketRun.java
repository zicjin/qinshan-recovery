/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.acqconfig.service.CoreEquiptAcqConfigService;
import com.sunwayworld.framework.utils.NumberUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketRun
implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SocketRun.class);
    public static final List<String> IPPORTLIST = new ArrayList<String>();
    public static final Map<String, Socket> SOCKETMAP = new HashMap<String, Socket>();
    private String ipPort;
    private Socket socket;
    private CoreEquiptAcqConfigService acqConfigService;
    private String end;

    public SocketRun(String ipPort, Socket socket, CoreEquiptAcqConfigService acqConfigService) {
        this.ipPort = ipPort;
        this.socket = socket;
        this.acqConfigService = acqConfigService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            int len;
            InputStream inputStream = this.socket.getInputStream();
            byte[] bys = new byte[1024];
            String listenString = "";
            boolean isAnswer = false;
            while ((len = inputStream.read(bys)) != -1) {
                String readText = new String(bys, 0, len, "GB2312");
                listenString = listenString + readText;
                if (bys[0] == 5 && len == 1 || isAnswer) {
                    isAnswer = true;
                    OutputStream out = this.socket.getOutputStream();
                    byte[] bs = new byte[]{6};
                    try {
                        out.write(bs);
                        out.flush();
                        if (this.end == null || !"everyTime".equals(this.end)) {
                            bs[0] = 3;
                            out.write(bs);
                            out.flush();
                            bs[0] = 15;
                            out.write(bs);
                            out.flush();
                        }
                        log.info("\u53d1\u900106", (Object)len, (Object)readText);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isAnswer && 4 == bys[0] && len == 1 || readText.charAt(readText.length() - 1) == '\u0004') {
                    isAnswer = false;
                    this.acqConfigService.pushPortMessage(this.ipPort, listenString);
                    log.info("Send {} length: {}", (Object)listenString, (Object)listenString.length());
                    listenString = "";
                }
                if (!isAnswer && !"".equals(listenString) && (this.end != null && "LRC".equals(this.end) && SocketRun.endOfLRC(listenString) || this.end == null && (listenString.endsWith("\r") || listenString.endsWith("\n") || listenString.endsWith("\r\n")) || this.end != null && ("everyTime".equals(this.end) || SocketRun.endOfLRC(listenString) || listenString.endsWith(this.end) || listenString.endsWith(this.end + "\r") || listenString.endsWith(this.end + "\n") || listenString.endsWith(this.end + "\r\n")))) {
                    this.acqConfigService.pushPortMessage(this.ipPort, listenString);
                    log.info("Send {} length: {}", (Object)listenString, (Object)listenString.length());
                    listenString = "";
                }
                log.info("Read {} bytes: {}", (Object)len, (Object)readText);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            SocketRun socketRun = this;
            synchronized (socketRun) {
                IPPORTLIST.remove(this.ipPort);
                SOCKETMAP.remove(this.ipPort);
                try {
                    if (this.socket != null) {
                        this.socket.close();
                    }
                }
                catch (IOException iOException) {}
            }
        }
    }

    private static boolean endOfLRC(String str) {
        if (null == str) {
            return false;
        }
        int bracketsIndex = str.lastIndexOf("]");
        int commaIndex = str.lastIndexOf(",");
        return bracketsIndex != -1 && commaIndex != -1 && commaIndex < bracketsIndex && NumberUtils.isNumber(str.substring(commaIndex + 1, bracketsIndex).trim());
    }
}
