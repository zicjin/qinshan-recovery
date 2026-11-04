/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.support;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageDTO;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImHelper {
    public static Set<String> getOnlineUser() {
        HashSet onlineUserIdSet = (HashSet)RedisHelper.get("IM_ONLINE_USER_NAME", "IM_ONLINE_USER_KEY");
        if (onlineUserIdSet == null) {
            onlineUserIdSet = new HashSet();
        }
        return onlineUserIdSet;
    }

    public static List<CoreImMessageDTO> buildOnlineOfflineMessage(List<String> receiveUserIdList, String onlineOfflineUserId, String imStatus) {
        ArrayList<CoreImMessageDTO> imMessageDTOList = new ArrayList<CoreImMessageDTO>();
        receiveUserIdList.forEach(e -> {
            CoreImMessageDTO imMessageDTO = new CoreImMessageDTO();
            imMessageDTO.setReceiveUserId((String)e);
            imMessageDTO.setType("contactState");
            JSONObject data = new JSONObject();
            data.put("userId", (Object)onlineOfflineUserId);
            data.put("imStatus", (Object)imStatus);
            imMessageDTO.setData(data.toJSONString());
            imMessageDTOList.add(imMessageDTO);
        });
        return imMessageDTOList;
    }
}
