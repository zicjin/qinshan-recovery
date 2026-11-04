/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.monitorconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.monitorconfig.service.CoreEquiptMonitorConfigService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketConnect {
    private static final Logger log = LoggerFactory.getLogger(SocketConnect.class);
    private static final Map<String, String> WORKMAP = new HashMap<String, String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start(String ip, int port, String code, Double threshold) {
        SocketChannel client = null;
        Float current = Float.valueOf(0.0f);
        String workStatus = "0";
        int reconnection = 3;
        try {
            Selector selector = Selector.open();
            client = this.connect(selector, ip, port);
            byte[] bytes = this.hexToByte(code);
            block11: while (reconnection > 0) {
                ByteBuffer sendbuffer = ByteBuffer.allocate(40);
                ByteBuffer readBuffer = ByteBuffer.allocate(40);
                sendbuffer.put(bytes);
                sendbuffer.flip();
                client.write(sendbuffer);
                if (!client.isOpen()) break;
                int select = selector.select(1000L);
                if (select == 0) continue;
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isReadable()) continue;
                    SocketChannel sc = (SocketChannel)key.channel();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    readBuffer.clear();
                    while (sc.read(readBuffer) > 0) {
                        bos.write(readBuffer.array());
                        readBuffer.clear();
                    }
                    if (bos.size() <= 0) continue;
                    byte[] b = bos.toByteArray();
                    String hex = this.byteToHex(b);
                    String error = hex.substring(2, 4);
                    if ("83".equals(error)) {
                        log.info("error");
                        continue block11;
                    }
                    current = Float.valueOf(Float.intBitsToFloat(Integer.valueOf(hex.substring(6, 14), 16)));
                    reconnection = 0;
                }
            }
            --reconnection;
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            if ((double)current.floatValue() > threshold) {
                workStatus = "1";
            }
            CoreEquiptMonitorConfigService coreEquiptMonitorConfigService = ApplicationContextHelper.getBean(CoreEquiptMonitorConfigService.class);
            if (StringUtils.isEmpty(WORKMAP.get(code))) {
                WORKMAP.put(code, workStatus);
                coreEquiptMonitorConfigService.updateEquiptWorkStatus(code);
                String lastWorkStatus = coreEquiptMonitorConfigService.selectLastWorkStatusByCode(code);
                if ("1".equals(workStatus)) {
                    if (!"1".equals(lastWorkStatus)) {
                        coreEquiptMonitorConfigService.insertMonitorLogByCode(code);
                    }
                } else if ("1".equals(lastWorkStatus)) {
                    coreEquiptMonitorConfigService.updateMonitorLogByCode(code);
                }
            } else if (!workStatus.equals(WORKMAP.get(code))) {
                WORKMAP.put(code, workStatus);
                coreEquiptMonitorConfigService.updateEquiptWorkStatus(code);
                if ("1".equals(workStatus)) {
                    coreEquiptMonitorConfigService.insertMonitorLogByCode(code);
                } else {
                    coreEquiptMonitorConfigService.updateMonitorLogByCode(code);
                }
            }
            if (client != null) {
                try {
                    client.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private byte[] hexToByte(String hex) {
        int m = 0;
        int n = 0;
        hex = hex.replaceAll(" ", "");
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; ++i) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte)intVal;
        }
        return ret;
    }

    private String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; ++n) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append(strHex.length() == 1 ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }

    private SocketChannel connect(Selector selector, String ip, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(ip, port);
        SocketChannel client = SocketChannel.open(address);
        client.configureBlocking(false);
        client.register(selector, 1);
        return client;
    }
}
