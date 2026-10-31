package com.sunwayworld.owned.attachments;

import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Isolated CoreFile-compatible upload for home attachments only. */
@RestController
@ConditionalOnProperty(name = "owned.attachments.enabled", havingValue = "true")
public final class OwnedCoreFileUploadController {
    private static final long MAX_BYTES = 1048576L;
    private final DataSource dataSource;
    private final OwnedIdentityHttpBoundary identity;
    private final AuditEventSink audit;
    private final Environment environment;
    private final Path root;
    private final OwnedAttachmentFileStore files;

    public OwnedCoreFileUploadController(DataSource dataSource, OwnedIdentityHttpBoundary identity,
            AuditEventSink audit, Environment environment) {
        this.dataSource = dataSource; this.identity = identity; this.audit = audit; this.environment = environment;
        String configured = environment.getProperty("owned.attachments.root");
        if (configured == null || configured.trim().isEmpty()) throw new IllegalStateException("Explicit owned.attachments.root required");
        this.root = Paths.get(configured).toAbsolutePath().normalize();
        this.files = new OwnedAttachmentFileStore(this.root);
    }

    @PostMapping(value = "/core/module/item/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(HttpServletRequest request, @RequestParam("file") MultipartFile file,
            @RequestParam("targetId") String targetId, @RequestParam(value = "bizCategory", required = false) String bizCategory) {
        Path written = null;
        try {
            OwnedPrincipal principal = authorize(request);
            if (file == null || file.isEmpty() || file.getSize() > MAX_BYTES) throw new IllegalArgumentException("file is empty or too large");
            if (targetId == null || !targetId.matches("T_EMC_HOME_ATTACHMENT\\$[1-9][0-9]*")) throw new IllegalArgumentException("unsupported attachment target");
            long attachmentId = Long.parseLong(targetId.substring(targetId.indexOf('$') + 1));
            ensureOwnedAttachment(attachmentId, principal);
            String name = safeName(file.getOriginalFilename()); String extension = extension(name);
            long fileId = nextIdentity(); String md5 = md5(fileId + "$" + name); String date = databaseDate();
            Path directory = root.resolve("secure").resolve(date).resolve(md5).normalize();
            if (!directory.startsWith(root)) throw new IllegalArgumentException("unsafe attachment path");
            Files.createDirectories(directory); Path target = directory.resolve(name).normalize(); if (!target.startsWith(root)) throw new IllegalArgumentException("unsafe attachment path");
            Path temporary = Files.createTempFile(directory, ".upload-", ".part");
            try (InputStream input = file.getInputStream()) { Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING); }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); written = target;
            final long id = fileId; final String finalName = name; final String finalExt = extension; final String finalMd5 = md5; final String finalTarget = targetId; final String finalCategory = bizCategory;
            final long finalSize = file.getSize();
            java.util.List<Path> stale = OwnedWriteSupport.transaction(dataSource,
                    connection -> replaceMetadata(connection, id, finalTarget, finalName, finalExt, finalMd5, finalSize));
            for (Path old : stale) if (!old.equals(target)) files.deleteQuietly(old);
            audit.record(new AuditEvent(java.time.Instant.now(), "attachment-file-upload", principal.getId(), targetId, AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"id\":" + fileId + ",\"targetId\":\"" + finalTarget + "\",\"name\":\"" + escape(finalName) + "\"}");
        } catch (AccessDeniedException denied) { deleteQuietly(written); return error(HttpStatus.FORBIDDEN, "GIKAM.PERMISSION.UNAUTHORIZED"); }
        catch (IllegalArgumentException invalid) { deleteQuietly(written); return error(HttpStatus.BAD_REQUEST, "GIKAM.ATTACHMENT.INVALID_REQUEST"); }
        catch (Exception failure) { deleteQuietly(written); return error(HttpStatus.INTERNAL_SERVER_ERROR, "GIKAM.ATTACHMENT.UPLOAD_FAILED"); }
    }

    private OwnedPrincipal authorize(HttpServletRequest request) throws SQLException {
        if (!"isolated".equals(environment.getProperty("owned.attachments.write.mode"))) throw denied();
        OwnedWriteSupport.requireLoopback(request); HttpSession session = request.getSession(false);
        identity.requirePermission(session, OwnedIdentityProvider.ATTACHMENT_MANAGE); return identity.currentPrincipal(session);
    }
    private void ensureOwnedAttachment(long id, OwnedPrincipal principal) throws SQLException {
        try (Connection c=dataSource.getConnection(); PreparedStatement q=c.prepareStatement("SELECT 1 FROM T_EMC_HOME_ATTACHMENT WHERE ID=? AND CREATEDBYORGID=?")) { q.setLong(1,id); q.setString(2,principal.getOrgId()); try(ResultSet r=q.executeQuery()){if(!r.next())throw denied();} }
    }
    private long nextIdentity() throws SQLException { try(Connection c=dataSource.getConnection()){return OwnedWriteSupport.nextIdentity(c);} }
    private String databaseDate() throws SQLException { try (Connection c=dataSource.getConnection(); PreparedStatement q=c.prepareStatement("SELECT TO_CHAR(SYSTIMESTAMP,'YYYYMMDD') FROM DUAL"); ResultSet r=q.executeQuery()) { if(!r.next()) throw new SQLException("database date unavailable"); return r.getString(1); } }
    private java.util.List<Path> replaceMetadata(Connection c,long id,String target,String name,String ext,String md5,long size) throws SQLException {
        java.util.List<Path> stale = new java.util.ArrayList<Path>();
        try (PreparedStatement old=c.prepareStatement("SELECT SCOPE,MD5NAME,NAME,TO_CHAR(CREATEDTIME,'YYYYMMDD') FROM T_CORE_FILE WHERE TARGETID=?")) {
            old.setString(1,target);
            try(ResultSet r=old.executeQuery()) { while(r.next()) stale.add(files.resolve(r.getString(1), r.getString(4), r.getString(2), r.getString(3))); }
        }
        try (PreparedStatement d=c.prepareStatement("DELETE FROM T_CORE_FILE WHERE TARGETID=?")){d.setString(1,target);d.executeUpdate();}
        try (PreparedStatement i=c.prepareStatement("INSERT INTO T_CORE_FILE (ID,TARGETID,NAME,FILEEXT,SCOPE,MD5NAME,CREATEDTIME,\"SIZE\") VALUES (?,?,?,?,?,?,SYSTIMESTAMP,?)")){i.setLong(1,id);i.setString(2,target);i.setString(3,name);i.setString(4,ext);i.setString(5,"secure");i.setString(6,md5);i.setLong(7,size);i.executeUpdate();}
        return stale;
    }
    private static String safeName(String value){if(value==null||value.trim().isEmpty())throw new IllegalArgumentException("filename required");String n=value.replace('\\','/');n=n.substring(n.lastIndexOf('/')+1);if(n.length()>200||n.equals(".")||n.equals("..")||n.matches(".*[\\x00-\\x1f\\x7f].*"))throw new IllegalArgumentException("unsafe filename");return n;}
    private static String extension(String name){int dot=name.lastIndexOf('.');return dot<0?"":name.substring(dot+1).toLowerCase(java.util.Locale.ROOT);}
    private static String md5(String value){try{byte[] b=MessageDigest.getInstance("MD5").digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x&255));return s.toString();}catch(Exception e){throw new IllegalStateException(e);}}
    private void deleteQuietly(Path p){files.deleteQuietly(p);}
    private static AccessDeniedException denied(){return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");}
    private static ResponseEntity<String> error(HttpStatus status,String message){return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\""+message+"\"}");}
    private static String escape(String s){return s.replace("\\","\\\\").replace("\"","\\\"");}
}
