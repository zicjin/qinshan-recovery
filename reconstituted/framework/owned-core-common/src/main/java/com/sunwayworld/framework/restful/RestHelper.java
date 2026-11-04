/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestHelper {
    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        RestHelper.restTemplate = restTemplate;
    }

    public static final <T> T getForObject(String url, Class<T> requiredClass, Cookie ... cookies) {
        HttpHeaders headers = new HttpHeaders();
        ArrayList<String> cookieList = new ArrayList<String>();
        for (Cookie c : cookies) {
            cookieList.add(c.getName() + "=" + c.getValue());
        }
        headers.put("Cookie", (List<String>)cookieList);
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(null, headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, requiredClass, new Object[0]);
        return responseEntity.getBody();
    }
}
