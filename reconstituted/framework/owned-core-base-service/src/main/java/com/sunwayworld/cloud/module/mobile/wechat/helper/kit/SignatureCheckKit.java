/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.kit;

import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HashKit;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

public class SignatureCheckKit {
    public static final SignatureCheckKit me = new SignatureCheckKit();

    public boolean checkSignature(String signature, String token, String timestamp, String nonce) {
        Object[] array = new String[]{token, timestamp, nonce};
        Arrays.sort(array);
        String tempStr = (String)array[0] + (String)array[1] + (String)array[2];
        tempStr = HashKit.sha1(tempStr);
        return tempStr.equalsIgnoreCase(signature);
    }

    public boolean checkSignature(HttpServletRequest r, String token) {
        return this.checkSignature(r.getParameter("signature"), token, r.getParameter("timestamp"), r.getParameter("nonce"));
    }
}
