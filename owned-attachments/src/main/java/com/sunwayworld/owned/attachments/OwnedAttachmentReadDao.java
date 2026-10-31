package com.sunwayworld.owned.attachments;
import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentBean;
import com.sunwayworld.emc.module.item.homeattachment.dao.EmcHomeAttachmentDao;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.data.OwnedReadDao;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedReadRequest;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;
/** Original attachment/file association, with download grants enforced for every read. */
public final class OwnedAttachmentReadDao extends MybatisDaoSupport<EmcHomeAttachmentBean, Long> implements EmcHomeAttachmentDao, OwnedReadDao<EmcHomeAttachmentBean, Long> {
    private static final String SELECT = "SELECT A.ID,A.ENDDATE,A.ACTIVATEDFLAG,A.CREATEDTIME,B.ID FILEID,B.NAME FILENAME,B.FILEEXT,B.SCOPE,B.MD5NAME,B.CREATEDTIME FILECREATEDTIME,B.\"SIZE\" FILESIZE FROM T_EMC_HOME_ATTACHMENT A LEFT JOIN T_CORE_FILE B ON B.TARGETID=CONCAT('T_EMC_HOME_ATTACHMENT$',A.ID)";
    private static final String SELECT_METADATA_ONLY = "SELECT A.ID,A.ENDDATE,A.ACTIVATEDFLAG,A.CREATEDTIME,CAST(NULL AS NUMBER) FILEID,CAST(NULL AS VARCHAR2(255)) FILENAME,CAST(NULL AS VARCHAR2(32)) FILEEXT,CAST(NULL AS VARCHAR2(32)) SCOPE,CAST(NULL AS VARCHAR2(64)) MD5NAME,CAST(NULL AS TIMESTAMP) FILECREATEDTIME,CAST(NULL AS NUMBER) FILESIZE FROM T_EMC_HOME_ATTACHMENT A";
    private static final String GRANT = " A.ACTIVATEDFLAG='1' AND A.ENDDATE>=SYSDATE AND EXISTS (SELECT 1 FROM T_EMC_HOME_ATTA_PERMISSION D WHERE D.ATTACHMENTID=A.ID AND D.USERID=?)";
    private final DataSource dataSource;
    private final OwnedIdentityHttpBoundary identity;
    public OwnedAttachmentReadDao(DataSource dataSource, OwnedIdentityHttpBoundary identity) { this.dataSource=dataSource; this.identity=identity; }
    @Override public Page<EmcHomeAttachmentBean> readPage(RestJsonWrapperBean wrapper) {
        try {
            OwnedPrincipal principal=principal();
            PageWindow request=PageWindow.parse(wrapper);
            List<EmcHomeAttachmentBean> all;
            try { all=readAll(SELECT, principal); }
            catch (SQLException missingFileTable) {
                // sss2 intentionally omits large attachment/file tables. Keep
                // the original metadata query usable and expose null file
                // fields instead of turning the workspace panel into HTTP 500.
                all=readAll(SELECT_METADATA_ONLY, principal);
            }
                int from=(int)Math.min((long)(request.getPageNumber()-1)*request.getPageSize(),all.size());
                int to=Math.min(from+request.getPageSize(),all.size());
                return new Pagination<EmcHomeAttachmentBean>(all.size(),request.getPageSize(),request.getPageNumber(),new ArrayList<EmcHomeAttachmentBean>(all.subList(from,to)));
        } catch(SQLException failure) { throw new IllegalStateException("Attachment read failed",failure); }
    }
    private List<EmcHomeAttachmentBean> readAll(String select, OwnedPrincipal principal) throws SQLException {
        try (Connection c=dataSource.getConnection(); PreparedStatement s=c.prepareStatement(select+" WHERE "+GRANT+" ORDER BY A.ID DESC")) {
            s.setString(1,principal.getId()); List<EmcHomeAttachmentBean> all=new ArrayList<EmcHomeAttachmentBean>();
            try(ResultSet rows=s.executeQuery()) { while(rows.next()) all.add(bean(rows)); }
            return all;
        }
    }
    @Override public EmcHomeAttachmentBean readDetail(Long id) { return required(id).bean; }
    public String downloadUrl(Long id) { return required(id).url(); }
    FileRecord content(String scope,String date,String md5,String name) {
        try {
            OwnedPrincipal principal=principal();
            try(Connection c=dataSource.getConnection(); PreparedStatement s=c.prepareStatement(SELECT+" WHERE B.SCOPE=? AND TO_CHAR(B.CREATEDTIME,'YYYYMMDD')=? AND B.MD5NAME=? AND B.NAME=? AND "+GRANT)) {
                s.setString(1,scope);s.setString(2,date);s.setString(3,md5);s.setString(4,name);s.setString(5,principal.getId());
                try(ResultSet rows=s.executeQuery()) { if(!rows.next()) throw denied(); FileRecord result=file(rows);if(rows.next())throw new IllegalStateException("Ambiguous attachment path");return result; }
            }
        } catch(SQLException failure) { throw new IllegalStateException("Attachment content read failed",failure); }
    }
    private FileRecord required(Long id) {
        if(id==null || id<1)throw new IllegalArgumentException("Invalid attachment identifier");
        try {
            OwnedPrincipal principal=principal();
            try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(SELECT+" WHERE A.ID=? AND "+GRANT)) {
                s.setLong(1,id);s.setString(2,principal.getId());
                try(ResultSet rows=s.executeQuery()) { if(!rows.next())throw denied();FileRecord result=file(rows);if(rows.next())throw new IllegalStateException("Ambiguous attachment association");return result; }
            }
        }catch(SQLException failure){throw new IllegalStateException("Attachment detail read failed",failure);}
    }
    private OwnedPrincipal principal() throws SQLException {
        if(!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes))throw denied();
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        return identity.currentPrincipal(request.getSession(false));
    }
    private static void validate(QueryRequest request) {
        for(Map.Entry<String,Object> entry:request.getMapperParameters().entrySet()) {
            if("targetType".equals(entry.getKey()) && "download".equals(entry.getValue()))continue;
            if("activatedFlag_SEQ".equals(entry.getKey()) && "1".equals(String.valueOf(entry.getValue())))continue;
            throw new IllegalArgumentException("Unsupported attachment query parameter");
        }
        for(FilterParamPattern f:request.getFilterPatterns()) {
            if(!"SEQ".equals(f.getMatchPattern()) || f.getMap().size()!=1 || !"1".equals(String.valueOf(f.getMap().get("ACTIVATEDFLAG"))))throw new IllegalArgumentException("Unsupported attachment filter");
        }
        if(!request.getAdvancedPatterns().isEmpty() || request.getMultiPropertyPattern()!=null || !request.getOrderParameters().isEmpty())throw new IllegalArgumentException("Unsupported attachment query options");
    }

    /** The original raw query uses n=-1 to request the first complete page. */
    private static final class PageWindow {
        private final int number; private final int size;
        private PageWindow(int number, int size) { this.number=number; this.size=size; }
        static PageWindow parse(RestJsonWrapperBean wrapper) {
            int number=1, size=50;
            try {
                String body=wrapper == null ? null : wrapper.getRequestJson();
                if (body != null && !body.trim().isEmpty()) {
                    com.alibaba.fastjson.JSONObject root=com.alibaba.fastjson.JSON.parseObject(body);
                    Object raw=root.get("p");
                    com.alibaba.fastjson.JSONObject p=raw instanceof com.alibaba.fastjson.JSONObject ? (com.alibaba.fastjson.JSONObject)raw : root;
                    number=parseInt(p.get("n"),1); size=parseInt(p.get("s"),50);
                }
            } catch (RuntimeException invalid) { throw new IllegalArgumentException("invalid attachment query", invalid); }
            if (number < 1) number=1; if (size < 1) size=50; if (size > 500) size=500;
            return new PageWindow(number,size);
        }
        private static int parseInt(Object value,int fallback) {
            if(value==null)return fallback; try{return Integer.parseInt(String.valueOf(value));}
            catch(NumberFormatException failure){throw new IllegalArgumentException("invalid attachment page",failure);}
        }
        int getPageNumber(){return number;} int getPageSize(){return size;}
    }
    private static EmcHomeAttachmentBean bean(ResultSet rows)throws SQLException {
        EmcHomeAttachmentBean bean=new EmcHomeAttachmentBean();bean.setId(rows.getLong("ID"));bean.setActivatedFlag(rows.getString("ACTIVATEDFLAG"));
        Timestamp end=rows.getTimestamp("ENDDATE"),created=rows.getTimestamp("CREATEDTIME");
        if(end!=null)bean.setEndDate(end.toLocalDateTime());if(created!=null)bean.setCreatedTime(created.toLocalDateTime());
        for(String column:new String[]{"FILENAME","FILEID","FILEEXT"})if(rows.getObject(column)!=null)bean.setExt$Item(column.toLowerCase(Locale.ROOT),String.valueOf(JdbcValue.normalize(rows.getObject(column))));
        return bean;
    }
    private static FileRecord file(ResultSet rows)throws SQLException {
        if(rows.getObject("FILEID")==null)throw new MissingFile();
        return new FileRecord(bean(rows),rows.getString("SCOPE"),rows.getTimestamp("FILECREATEDTIME"),rows.getString("MD5NAME"),rows.getString("FILENAME"),rows.getLong("FILESIZE"));
    }
    private static AccessDeniedException denied(){return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");}
    static final class MissingFile extends RuntimeException { }
    static final class FileRecord {
        final EmcHomeAttachmentBean bean;final String scope,date,md5,name;final long size;
        FileRecord(EmcHomeAttachmentBean bean,String scope,Timestamp created,String md5,String name,long size){this.bean=bean;this.scope=scope;this.date=created==null?null:created.toLocalDateTime().toLocalDate().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);this.md5=md5;this.name=name;this.size=size;}
        String url(){validate();return "/files/"+scope+"/"+date+"/"+md5+"/"+UriUtils.encodePathSegment(name,java.nio.charset.StandardCharsets.UTF_8);}
        void validate(){if(!"secure".equals(scope)||date==null||!date.matches("[0-9]{8}")||md5==null||!md5.matches("[a-f0-9]{32}")||name==null||name.length()>200||name.indexOf('/')>=0||name.indexOf('\\')>=0||name.equals(".")||name.equals("..")||name.matches(".*[\\x00-\\x1f\\x7f].*")||size<0||size>1048576)throw new IllegalArgumentException("Unsafe or unsupported attachment metadata");}
    }
}
