package com.sunwayworld.owned.internal.data;

import com.alibaba.fastjson.JSON;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;

/** Reconstructs the account DTO; assignment metadata never substitutes for authorization. */
public final class AccountQueryService {
    private final AccountQueryDao dao;
    public AccountQueryService(AccountQueryDao dao) {
        if (dao == null) throw new IllegalArgumentException("account DAO required");
        this.dao = dao;
    }

    /** Caller must supply the identity provider's freshly validated authenticated ID. */
    public Map<String, Object> load(String userId) throws SQLException {
        if (userId == null || userId.trim().isEmpty()) throw new AccessDeniedException("authenticated account required");
        AccountQueryDao.Snapshot snapshot = dao.read(userId);
        if (snapshot == null || !userId.equals(snapshot.user.get("id"))) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        Map<String, Object> account = new LinkedHashMap<String, Object>(snapshot.user);
        Object parent = account.remove("parentOrgId");
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        if (parent != null) extension.put("parentorgid", parent);
        // System login currently rejects active password-expiry policies;
        // in this supported mode the original service emits String.valueOf(null).
        extension.put("passwordexpireremainingdays", "null");
        if (!snapshot.assignments.isEmpty()) extension.put("roles", JSON.toJSONString(snapshot.assignments));
        boolean designer = false;
        for (Map<String, Object> role : snapshot.assignments) designer |= "2".equals(String.valueOf(role.get("roleId")));
        extension.put("lcdpdesignflag", designer ? "1" : "0");
        if (snapshot.appUuid != null) extension.put("appuuid", snapshot.appUuid);
        account.put("ext$", extension);
        return account;
    }
}
