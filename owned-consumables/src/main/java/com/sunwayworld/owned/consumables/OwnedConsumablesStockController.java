package com.sunwayworld.owned.consumables;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.sql.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

/** Isolated stock workbench capabilities and scoped recipient selection. */
@RestController
public final class OwnedConsumablesStockController {
    private final Environment environment;
    private final OwnedIdentityHttpBoundary boundary;
    private final JdbcPersonnelIdentityProvider provider;
    private final DataSource source;
    public OwnedConsumablesStockController(Environment environment, OwnedIdentityHttpBoundary boundary,
            JdbcPersonnelIdentityProvider provider, DataSource source) {
        this.environment = environment; this.boundary = boundary; this.provider = provider; this.source = source;
    }
    @GetMapping(value="/owned/consumables/stock-write-available", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> available(HttpServletRequest request) {
        try { authorize(request); return ok(Collections.singletonMap("available", Boolean.TRUE)); }
        catch (AccessDeniedException denied) { return error(HttpStatus.FORBIDDEN); }
        catch (Exception failure) { return error(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    @GetMapping(value="/owned/consumables/material-write-available", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> materialAvailable(HttpServletRequest request) {
        try { authorize(request); return ok(Collections.singletonMap("available", Boolean.TRUE)); }
        catch (AccessDeniedException denied) { return error(HttpStatus.FORBIDDEN); }
        catch (Exception failure) { return error(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    @GetMapping(value="/owned/consumables/stock-recipients", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recipients(HttpServletRequest request) {
        try {
            Set<String> organizations = provider.permittedOrganizationIds(authorize(request));
            List<Map<String,String>> values = new ArrayList<Map<String,String>>();
            if (organizations.isEmpty()) return ok(values);
            StringBuilder sql = new StringBuilder("SELECT ID,USERNAME,ORGID,ORGNAME FROM T_CORE_USER WHERE STATUS='activated' AND NVL(LASTSUSPENDEDFLAG,'0')='0' AND ORGID IN (");
            for (int i=0; i<organizations.size(); i++) { if (i>0) sql.append(','); sql.append('?'); }
            sql.append(") ORDER BY USERNAME,ID");
            try (Connection connection=source.getConnection(); PreparedStatement query=connection.prepareStatement(sql.toString())) {
                int index=1; for (String org:organizations) query.setString(index++,org);
                try (ResultSet rows=query.executeQuery()) {
                    while (rows.next()) {
                        Map<String,String> value=new LinkedHashMap<String,String>();
                        value.put("id",rows.getString(1)); value.put("name",rows.getString(2));
                        value.put("orgId",rows.getString(3)); value.put("orgName",rows.getString(4)); values.add(value);
                    }
                }
            }
            return ok(values);
        } catch (AccessDeniedException denied) { return error(HttpStatus.FORBIDDEN); }
        catch (Exception failure) { return error(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    private OwnedPrincipal authorize(HttpServletRequest request) throws SQLException {
        OwnedWriteSupport.requireLoopback(request);
        if (!"isolated".equals(environment.getProperty("owned.consumables.write.mode"))) throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        boundary.requirePermission(request.getSession(false),OwnedIdentityProvider.CONSUMABLE_MANAGE);
        return boundary.currentPrincipal(request.getSession(false));
    }
    private static ResponseEntity<?> ok(Object body) { return ResponseEntity.ok().header("Cache-Control","no-store").body(body); }
    private static ResponseEntity<?> error(HttpStatus status) {
        return ResponseEntity.status(status).header("Cache-Control","no-store").body(Collections.singletonMap("message",status==HttpStatus.FORBIDDEN?"GIKAM.PERMISSION.UNAUTHORIZED":"GIKAM.CONSUMABLES.STOCK_WRITE_UNAVAILABLE"));
    }
}
