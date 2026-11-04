/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.data;

import com.sunwayworld.framework.utils.ConvertUtils;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class RestHttpTextEntity
extends ResponseEntity<Map<String, String>> {
    public RestHttpTextEntity() {
        super(HttpStatus.OK);
    }

    public RestHttpTextEntity(HttpStatus status) {
        super(status);
    }

    public RestHttpTextEntity(HttpStatus status, Object body) {
        super(Collections.singletonMap("value", ConvertUtils.convert(body, String.class)), HttpStatus.OK);
    }

    public RestHttpTextEntity(HttpStatus status, MultiValueMap<String, String> headers) {
        super(headers, HttpStatus.OK);
    }

    public RestHttpTextEntity(HttpStatus status, Object body, MultiValueMap<String, String> headers) {
        super(Collections.singletonMap("value", ConvertUtils.convert(body, String.class)), headers, HttpStatus.OK);
    }

    public RestHttpTextEntity(Object body) {
        this(HttpStatus.OK, body);
    }

    public RestHttpTextEntity(MultiValueMap<String, String> headers) {
        this(HttpStatus.OK, headers);
    }

    public RestHttpTextEntity(Object body, MultiValueMap<String, String> headers) {
        this(HttpStatus.OK, Collections.singletonMap("value", ConvertUtils.convert(body, String.class)), headers);
    }
}
