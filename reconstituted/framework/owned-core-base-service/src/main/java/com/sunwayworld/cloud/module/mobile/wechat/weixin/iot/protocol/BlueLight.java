/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.protocol;

import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Charsets;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BlueLight {
    public Head head;
    public String body;
    private static final char MAGIC = '\ufecf';
    private static final short HEAD_LENGTH = 12;
    private static Charset CHARSET = Charsets.UTF_8;

    public static BlueLight build(CmdId cmdId, String respText, short seq) {
        BlueLight light = new BlueLight();
        light.body = respText;
        light.head = new Head();
        byte[] b = respText == null ? new byte[]{} : respText.getBytes(CHARSET);
        light.head.magic = (char)65231;
        light.head.version = 1;
        light.head.length = (short)(12 + b.length);
        light.head.cmdId = cmdId.value();
        light.head.seq = seq;
        light.head.errorCode = 0;
        return light;
    }

    public byte[] toBytes() {
        byte[] b = this.body == null ? new byte[]{} : this.body.getBytes(CHARSET);
        ByteBuffer buf = ByteBuffer.allocate(this.head.length);
        buf.putChar(this.head.magic);
        buf.putShort(this.head.version);
        buf.putShort(this.head.length);
        buf.putShort(this.head.cmdId);
        buf.putShort(this.head.seq);
        buf.putShort(this.head.errorCode);
        buf.put(b);
        buf.flip();
        return buf.array();
    }

    public static BlueLight parse(byte[] reqBytes) {
        ByteBuffer buf = ByteBuffer.wrap(reqBytes);
        char magic = buf.getChar();
        if (magic != '\ufecf') {
            return null;
        }
        Head h = new Head();
        h.magic = (char)65231;
        h.version = buf.getShort();
        h.length = buf.getShort();
        h.cmdId = buf.getShort();
        h.seq = buf.getShort();
        h.errorCode = buf.getShort();
        int bodyLen = h.length - 12;
        byte[] bodyBytes = new byte[bodyLen];
        buf.get(bodyBytes);
        String b = new String(bodyBytes, CHARSET);
        BlueLight light = new BlueLight();
        light.head = h;
        light.body = b;
        return light;
    }

    public String toString() {
        return "BlueLight [body=" + this.body + ", head=" + this.head + "]";
    }

    static char[] bytesToChars(byte[] bytes) {
        char[] cs = new char[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            cs[i] = (char)bytes[i];
        }
        return cs;
    }

    static byte[] charsToBytes(char[] chars) {
        byte[] bs = new byte[chars.length];
        for (int i = 0; i < chars.length; ++i) {
            bs[i] = (byte)chars[i];
        }
        return bs;
    }

    protected static String bytesToHex(byte[] b) {
        char[] hexDigit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder buf = new StringBuilder();
        for (int j = 0; j < b.length; ++j) {
            buf.append(hexDigit[b[j] >> 4 & 0xF]);
            buf.append(hexDigit[b[j] & 0xF]);
        }
        return buf.toString();
    }

    public static enum CmdId {
        SEND_TEXT_REQ(1),
        SEND_TEXT_RESP(4097),
        OPEN_LIGHT_PUSH(8193),
        CLOSE_LIGHT_PUSH(8194);

        private short value;

        private CmdId(int v) {
            this.value = (short)v;
        }

        public short value() {
            return this.value;
        }
    }

    public static class Head {
        public char magic;
        public short version;
        public short length;
        public short cmdId;
        public short seq;
        public short errorCode;

        public String toString() {
            return "Head [magic=" + this.magic + ", version=" + this.version + ", length=" + this.length + ", cmdId=" + this.cmdId + ", seq=" + this.seq + ", errorCode=" + this.errorCode + "]";
        }
    }
}
