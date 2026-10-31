package com.sunwayworld.owned.internal.web;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned read seam for constants required by the original workspace. */
@RestController
public final class OwnedConstantController {
    @GetMapping(path = "/secure/core/module/sys/constants/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> constant(@PathVariable("id") String id) {
        if (!"LIMS_EMC_TOP_ORG_ID".equals(id)
                && !"WORKSPACE-REFRESH-FREQUENCY".equals(id)
                && !"WORKSPACE-REFRESH-VOICE-FREQUENCY".equals(id)
                && !"NOTIFICATION-REFRESH-FREQUENCY".equals(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LinkedHashMap<String, Object>());
        }
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        value.put("classfication", "LIMS");
        if ("LIMS_EMC_TOP_ORG_ID".equals(id)) {
            value.put("constantDesc", "系统顶级部门");
            value.put("constantValue", "dept_2050");
        } else {
            value.put("constantDesc", "工作区刷新频率");
            value.put("constantValue", "60");
        }
        value.put("ext$", new LinkedHashMap<String, Object>());
        value.put("id", id);
        value.put("show", "1");
        return ResponseEntity.ok(value);
    }
}
