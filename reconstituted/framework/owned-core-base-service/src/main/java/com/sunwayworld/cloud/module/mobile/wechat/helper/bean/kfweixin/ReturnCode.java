/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.bean.kfweixin;

import java.util.HashMap;
import java.util.Map;

public class ReturnCode {
    private static final Map<Integer, String> errCodeToErrMsg = new HashMap<Integer, String>(){
        {
            this.put(-1, "\u7cfb\u7edf\u7e41\u5fd9");
            this.put(0, "\u8bf7\u6c42\u6210\u529f");
            this.put(40001, "\u83b7\u53d6access_token\u65f6AppSecret\u9519\u8bef\uff0c\u6216\u8005access_token\u65e0\u6548");
            this.put(40002, "\u4e0d\u5408\u6cd5\u7684\u51ed\u8bc1\u7c7b\u578b");
            this.put(40003, "\u4e0d\u5408\u6cd5\u7684OpenID");
            this.put(40004, "\u4e0d\u5408\u6cd5\u7684\u5a92\u4f53\u6587\u4ef6\u7c7b\u578b");
            this.put(40005, "\u4e0d\u5408\u6cd5\u7684\u6587\u4ef6\u7c7b\u578b");
            this.put(40006, "\u4e0d\u5408\u6cd5\u7684\u6587\u4ef6\u5927\u5c0f");
            this.put(40007, "\u4e0d\u5408\u6cd5\u7684\u5a92\u4f53\u6587\u4ef6id");
            this.put(40008, "\u4e0d\u5408\u6cd5\u7684\u6d88\u606f\u7c7b\u578b");
            this.put(40009, "\u4e0d\u5408\u6cd5\u7684\u56fe\u7247\u6587\u4ef6\u5927\u5c0f");
            this.put(40010, "\u4e0d\u5408\u6cd5\u7684\u8bed\u97f3\u6587\u4ef6\u5927\u5c0f");
            this.put(40011, "\u4e0d\u5408\u6cd5\u7684\u89c6\u9891\u6587\u4ef6\u5927\u5c0f");
            this.put(40012, "\u4e0d\u5408\u6cd5\u7684\u7f29\u7565\u56fe\u6587\u4ef6\u5927\u5c0f");
            this.put(40013, "\u4e0d\u5408\u6cd5\u7684APPID");
            this.put(40014, "\u4e0d\u5408\u6cd5\u7684access_token");
            this.put(40015, "\u4e0d\u5408\u6cd5\u7684\u83dc\u5355\u7c7b\u578b");
            this.put(40016, "\u4e0d\u5408\u6cd5\u7684\u6309\u94ae\u4e2a\u6570");
            this.put(40017, "\u4e0d\u5408\u6cd5\u7684\u6309\u94ae\u7c7b\u578b");
            this.put(40018, "\u4e0d\u5408\u6cd5\u7684\u6309\u94ae\u540d\u5b57\u957f\u5ea6");
            this.put(40019, "\u4e0d\u5408\u6cd5\u7684\u6309\u94aeKEY\u957f\u5ea6");
            this.put(40020, "\u4e0d\u5408\u6cd5\u7684\u6309\u94aeURL\u957f\u5ea6");
            this.put(40021, "\u4e0d\u5408\u6cd5\u7684\u83dc\u5355\u7248\u672c\u53f7");
            this.put(40022, "\u4e0d\u5408\u6cd5\u7684\u5b50\u83dc\u5355\u7ea7\u6570");
            this.put(40023, "\u4e0d\u5408\u6cd5\u7684\u5b50\u83dc\u5355\u6309\u94ae\u4e2a\u6570");
            this.put(40024, "\u4e0d\u5408\u6cd5\u7684\u5b50\u83dc\u5355\u6309\u94ae\u7c7b\u578b");
            this.put(40025, "\u4e0d\u5408\u6cd5\u7684\u5b50\u83dc\u5355\u6309\u94ae\u540d\u5b57\u957f\u5ea6");
            this.put(40026, "\u4e0d\u5408\u6cd5\u7684\u5b50\u83dc\u5355\u6309\u94aeKEY\u957f\u5ea6");
            this.put(40027, "\u4e0d\u5408\u6cd5\u7684\u5b50\u83dc\u5355\u6309\u94aeURL\u957f\u5ea6");
            this.put(40028, "\u4e0d\u5408\u6cd5\u7684\u81ea\u5b9a\u4e49\u83dc\u5355\u4f7f\u7528\u7528\u6237");
            this.put(40029, "\u4e0d\u5408\u6cd5\u7684oauth_code");
            this.put(40030, "\u4e0d\u5408\u6cd5\u7684refresh_token");
            this.put(40036, "\u4e0d\u5408\u6cd5\u7684\u6a21\u677fid\u957f\u5ea6");
            this.put(40037, "\u4e0d\u5408\u6cd5\u7684\u6a21\u677fid");
            this.put(40039, "\u4e0d\u5408\u6cd5\u7684URL\u957f\u5ea6");
            this.put(40048, "\u4e0d\u5408\u6cd5\u7684URL\u57df\u540d");
            this.put(40054, "\u4e0d\u5408\u6cd5\u7684\u5b50\u83dc\u5355\u6309\u94ae url \u57df\u540d");
            this.put(40055, "\u4e0d\u5408\u6cd5\u7684\u83dc\u5355\u6309\u94ae url \u57df\u540d");
            this.put(40053, "\u4e0d\u5408\u6cd5\u7684actioninfo\uff0c\u8bf7\u5f00\u53d1\u8005\u786e\u8ba4\u53c2\u6570\u6b63\u786e");
            this.put(40066, "\u4e0d\u5408\u6cd5\u7684 url");
            this.put(41001, "\u7f3a\u5c11access_token\u53c2\u6570");
            this.put(41002, "\u7f3a\u5c11appid\u53c2\u6570");
            this.put(41003, "\u7f3a\u5c11refresh_token\u53c2\u6570");
            this.put(41004, "\u7f3a\u5c11secret\u53c2\u6570");
            this.put(41005, "\u7f3a\u5c11\u591a\u5a92\u4f53\u6587\u4ef6\u6570\u636e");
            this.put(41006, "\u7f3a\u5c11media_id\u53c2\u6570");
            this.put(41007, "\u7f3a\u5c11\u5b50\u83dc\u5355\u6570\u636e");
            this.put(41008, "\u7f3a\u5c11oauth code");
            this.put(41009, "\u7f3a\u5c11openid");
            this.put(41010, "\u7f3a\u5931 url \u53c2\u6570");
            this.put(42001, "access_token\u8d85\u65f6");
            this.put(42002, "refresh_token\u8d85\u65f6");
            this.put(42003, "oauth_code\u8d85\u65f6");
            this.put(43001, "\u9700\u8981GET\u8bf7\u6c42");
            this.put(43002, "\u9700\u8981POST\u8bf7\u6c42");
            this.put(43003, "\u9700\u8981HTTPS\u8bf7\u6c42");
            this.put(43004, "\u9700\u8981\u8ba2\u9605\u5173\u7cfb");
            this.put(44001, "\u7a7a\u767d\u7684\u4e8c\u8fdb\u5236\u6570\u636e");
            this.put(44002, "POST\u7684\u6570\u636e\u5305\u4e3a\u7a7a");
            this.put(44003, "\u56fe\u6587\u6d88\u606f\u5185\u5bb9\u4e3a\u7a7a");
            this.put(44004, "\u6587\u672c\u6d88\u606f\u5185\u5bb9\u4e3a\u7a7a");
            this.put(45001, "\u591a\u5a92\u4f53\u6587\u4ef6\u5927\u5c0f\u8d85\u8fc7\u9650\u5236");
            this.put(45002, "\u6d88\u606f\u5185\u5bb9\u8d85\u8fc7\u9650\u5236");
            this.put(45003, "\u6807\u9898\u5b57\u6bb5\u8d85\u8fc7\u9650\u5236");
            this.put(45004, "\u63cf\u8ff0\u5b57\u6bb5\u8d85\u8fc7\u9650\u5236");
            this.put(45005, "\u94fe\u63a5\u5b57\u6bb5\u8d85\u8fc7\u9650\u5236");
            this.put(45006, "\u56fe\u7247\u94fe\u63a5\u5b57\u6bb5\u8d85\u8fc7\u9650\u5236");
            this.put(45007, "\u8bed\u97f3\u64ad\u653e\u65f6\u95f4\u8d85\u8fc7\u9650\u5236");
            this.put(45008, "\u56fe\u6587\u6d88\u606f\u8d85\u8fc7\u9650\u5236");
            this.put(45009, "\u63a5\u53e3\u8c03\u7528\u8d85\u8fc7\u9650\u5236");
            this.put(45010, "\u521b\u5efa\u83dc\u5355\u4e2a\u6570\u8d85\u8fc7\u9650\u5236");
            this.put(45011, "\u9891\u7387\u9650\u5236");
            this.put(45012, "\u6a21\u677f\u5927\u5c0f\u8d85\u8fc7\u9650\u5236");
            this.put(45016, "\u7cfb\u7edf\u5206\u7ec4\uff0c\u4e0d\u5141\u8bb8\u4fee\u6539");
            this.put(45017, "\u5206\u7ec4\u540d\u5b57\u8fc7\u957f");
            this.put(45018, "\u5206\u7ec4\u6570\u91cf\u8d85\u8fc7\u4e0a\u9650");
            this.put(50001, "\u7528\u6237\u672a\u6388\u6743\u8be5api");
        }
    };

    public static String get(int errCode) {
        return errCodeToErrMsg.get(errCode);
    }
}
