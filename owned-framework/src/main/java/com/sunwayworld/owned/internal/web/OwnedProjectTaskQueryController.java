package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.owned.internal.data.JdbcValue;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned implementation of the inherited project task-query aliases. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedProjectTaskQueryController {
    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;
    public OwnedProjectTaskQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("project task dependencies are required");
        this.boundary = boundary; this.dataSource = dataSource;
    }
    @PostMapping(path = {
            "/secure/emc/module/bp/project/projects/queries/tasks",
            "/secure/emc/module/bp/edit/project-change/projects/queries/tasks",
            "/secure/emc/module/bp/edit/project-change/projects/queries",
            "/secure/emc/module/bp/edit/project-change/projects/queries/raw",
            "/secure/emc/module/bp/edit/project-change/projects/queries/searchable"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try { OwnedPrincipal principal = require(request); boolean projectChange = request.getRequestURI().contains("/edit/project-change/");
            return ok(selectPage(principal.getOrgId(), Query.parse(body), projectChange ? PROJECT_CHANGE_TYPE_PROFILE : PROJECT_TYPE_PROFILE, projectChange)); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = "/secure/emc/module/bp/edit/project-change/projects/info/status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> projectChangeStatus(HttpServletRequest request) {
        try { require(request); return ok("{\"auditStatus\":{\"processStatus\":\"draft\",\"statusCode\":\"draft\"},\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }
    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }
    private String selectPage(String organization, Query query, Map<String,String> profile, boolean projectChange) throws SQLException {
        SqlSpec spec = SqlSpec.forQuery(organization, query, projectChange);
        String countSql = "SELECT COUNT(1) FROM (" + spec.sql + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM (" + spec.sql + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total; List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        try (Connection connection=dataSource.getConnection()) {
            try (PreparedStatement count=connection.prepareStatement(countSql)) {
                spec.bind(count); try (ResultSet result=count.executeQuery()) { if (!result.next()) throw new SQLException("project task count returned no row"); total=result.getInt(1); }
            }
            try (PreparedStatement page=connection.prepareStatement(pageSql)) {
                int next=spec.bind(page); page.setInt(next++,query.page.start()); page.setInt(next,query.page.end());
                try (ResultSet result=page.executeQuery()) { ResultSetMetaData metadata=result.getMetaData(); while (result.next()) rows.add(row(result,metadata)); }
            }
        }
        Map<String,Object> envelope=new LinkedHashMap<String,Object>(); envelope.put("columnTypeMap", profile);
        envelope.put("first", query.page.number == 1); envelope.put("last", total == 0 || query.page.number >= pages(total,query.page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber",query.page.number); envelope.put("pageSize",query.page.size);
        envelope.put("rows",rows); envelope.put("total",total); Map<String,Object> totals=new LinkedHashMap<String,Object>(); totals.put("qty_",total); envelope.put("totalMap",totals); envelope.put("totalPages",pages(total,query.page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }
    private static Map<String,Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException { Map<String,Object> value=new LinkedHashMap<String,Object>(); for(int i=1;i<=metadata.getColumnCount();i++){String label=metadata.getColumnLabel(i);if("RN_".equalsIgnoreCase(label))continue;value.put(camel(label),JdbcValue.normalize(result.getObject(i)));}return value; }
    private static String camel(String label){StringBuilder result=new StringBuilder();boolean upper=false;for(char c:label.toLowerCase(Locale.ROOT).toCharArray()){if(c=='_'){upper=true;continue;}result.append(upper?Character.toUpperCase(c):c);upper=false;}return result.toString();}
    private static int pages(int total,int size){return total==0?0:(total+size-1)/size;}
    private static ResponseEntity<String> ok(String body){return ResponseEntity.ok().header("Cache-Control","no-store").body(body);}
    private static ResponseEntity<String> forbidden(){return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\":\"GIKAM.PERMISSION.UNAUTHORIZED\"}");}
    private static ResponseEntity<String> badRequest(){return ResponseEntity.badRequest().body("{\"message\":\"GIKAM.PROJECT.INVALID_QUERY\"}");}
    private static ResponseEntity<String> unavailable(){return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"GIKAM.PROJECT.UNAVAILABLE\"}");}
    public static String projectSourceTable(boolean projectChange) {
        return projectChange ? "T_EMC_PROJECT_CHANGE" : "T_EMC_PROJECT";
    }

    private static final Map<String,String> PROJECT_TYPE_PROFILE=profile("monitororgname=string;syncncstatus=string;qccheck=string;year=number;testorgname=string;qcreporttype=string;reportreviewerid=string;entrustorgemail=string;reportcnaddress=string;starttime=dateTime;samplinglocation=string;deliverydate=dateTime;accountingflag=string;entrustmethod=string;projname=string;safetymeasures=string;erpcontactgbid=string;scheduleactivedflag=string;id=number;testprojectname=string;subcontractorid=number;editmonitorinfo=string;packmethod=string;detectpurpose=string;reportfee=string;period=string;reportuse=string;testorgid=string;sendusername=string;beforestatus=string;requiredsamptime=string;taskdate=dateTime;createdbyorgid=string;qcreq=string;signusername=string;lastestaccepteddate=dateTime;reportissueuserid=string;subpackagedesc=string;belongarea=string;cancelbyname=string;erpordernumber=string;nexttaskdate=dateTime;firstissuedatetime=dateTime;testorgfixedphone=string;prospectdate=dateTime;testprojects=string;folderremark=string;projectprice=string;status=string;processstatus=string;reporttype=string;projno=string;reportcnname=string;reportreviewername=string;createdbyid=string;samplesource=string;reportedituserid=string;monitorcondition=string;sampleordercount=number;testinglab=string;acceptdate=dateTime;reportassignstatus=string;orgname=string;syncncdesc=string;otherreportuse=string;businessuserid=string;senddate=dateTime;schemereportlineid=string;subpackage=string;urgent=string;subcontractsupplier=string;businessusername=string;entrustdate=dateTime;reportorgid=string;ndcal=string;contractno=string;endtime=dateTime;sample=string;createdtime=dateTime;opinion=string;drbs=string;prospect=string;reportenaddress=string;customerphone=string;nccode=string;reportdeliveryway=string;testcycle=string;senduserid=string;taskno=string;entrustorgphone=string;customercontact=string;testorgaddress=string;monitoringcategory=string;inspectedcontact=string;completestatus=string;customersource=string;amountdeterm=string;crm_sync_state=string;source=string;periodtype=string;projectqcuserid=string;projectqcusername=string;testorgcontact=string;projectamount=string;formstyle=string;sampleorgid=string;projtype=string;entrustorgcontact=string;contractid=string;erpreviewid=number;biddername=string;remarktwo=string;testorgphone=string;cron=string;erpgbid=number;projnode=string;rn_=number;needqcreport=string;cancelreason=string;qualitycontrol=string;entrustorgid=string;requiredcompletedate=dateTime;bizcategoryid=number;bizcategorycode=string;reportorgname=string;sendtype=string;entrustorgaddress=string;datastatus=string;newestauditdate=string;testorgemail=string;orgid=string;registdate=dateTime;remarkone=string;reportissuetime=dateTime;reportremark=string;operationorg=string;cancelbyid=string;addressee=string;projaddress=string;minsampleduration=string;projamount=string;testcontent=string;reportenname=string;compledate=dateTime;drbgbh=string;projcreatedtime=dateTime;bizcategoryname=string;detect=string;description=string;remark=string;recipientphone=string;reportissueusername=string;reportaptitude=string;reporttitle=string;mobileid=string;splitcontractid=number;reportpostaddress=string;prospecremark=string;createdbyorgname=string;projecttype=string;crmsyncstate=string;customername=string;reporteditusername=string;mergereport=string;projectsamplebyid=string;canceltime=dateTime;contractname=string;needreport=string;ordernum=string;toreporteditdate=dateTime;canclereason=string;needsplitreport=string;testmethodassigner=string;completedate=dateTime;projsection=string;securitylevel=string;entrustorgname=string;inspectedphone=string;pretreatment=string;bidderid=string;projectsamplebyname=string;createdbyname=string");
    private static final Map<String,String> PROJECT_CHANGE_TYPE_PROFILE=profile("acceptdate=dateTime;addressee=string;beforestatus=string;bidderid=string;biddername=string;bizcategorycode=string;bizcategoryid=number;bizcategoryname=string;cancelreason=string;cancelstatus=string;canclereason=string;changeno=string;compledate=dateTime;completedate=dateTime;completestatus=string;contractid=string;contractno=string;createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;crm_sync_state=string;crmsyncstate=string;cron=string;customercontact=string;customername=string;customerphone=string;datastatus=string;deliverydate=dateTime;description=string;detect=string;discountreviewstatus=number;drbgbh=string;drbs=string;editmonitorinfo=string;endtime=dateTime;entrustdate=dateTime;entrustmethod=string;entrustorgaddress=string;entrustorgcontact=string;entrustorgid=string;entrustorgname=string;entrustorgphone=string;erpgbid=number;erpordernumber=string;erpreviewid=number;formstyle=string;id=number;inspectedcontact=string;inspectedphone=string;lastestaccepteddate=dateTime;mergereport=string;mobileid=string;monitorcondition=string;monitoringcategory=string;monitororgname=string;ndcal=string;needqcreport=string;needsplitreport=string;newestauditdate=string;nexttaskdate=dateTime;operationorg=string;opinion=string;ordernum=string;orgid=string;orgname=string;packmethod=string;pretreatment=string;processstatus=string;projaddress=string;projamount=string;projcreatedtime=dateTime;projectamount=string;projectprice=string;projectqcuserid=string;projectqcusername=string;projectsamplebyid=string;projectsamplebyname=string;projname=string;projno=string;projnode=string;prospecremark=string;prospect=string;prospectdate=dateTime;qccheck=string;qcreq=string;qualitycontrol=string;recipientphone=string;registdate=dateTime;remark=string;reportaptitude=string;reportcnaddress=string;reportcnname=string;reportdeliveryway=string;reportedituserid=string;reporteditusername=string;reportenaddress=string;reportenname=string;reportissueuserid=string;reportissueusername=string;reportorgid=string;reportorgname=string;reportpostaddress=string;reportreviewerid=string;reportreviewername=string;reporttype=string;reportuse=string;requiredsamptime=string;rn_=number;safetymeasures=string;sample=string;sampleorgid=string;samplesource=string;samplinglocation=string;schemereportlineid=string;senddate=dateTime;sendtype=string;senduserid=string;sendusername=string;source=string;starttime=dateTime;status=string;subcontractsupplier=string;subpackage=string;subpackagedesc=string;taskdate=dateTime;taskno=string;testcontent=string;testcycle=string;testinglab=string;testmethodassigner=string;testorgaddress=string;testorgcontact=string;testorgemail=string;testorgfixedphone=string;testorgid=string;testorgname=string;testorgphone=string;urgent=string");
    private static Map<String,String> profile(String encoded){Map<String,String> result=new LinkedHashMap<String,String>();for(String token:encoded.split(";")){int equals=token.indexOf('=');if(equals>0)result.put(token.substring(0,equals),token.substring(equals+1));}return result;}
    private static final class Query {
        final Page page;final String targetType;final String sampleUserId;final String sceneUserId;
        Query(Page page,String targetType,String sampleUserId,String sceneUserId){this.page=page;this.targetType=targetType;this.sampleUserId=sampleUserId;this.sceneUserId=sceneUserId;}
        static Query parse(String body){JSONObject root=body==null||body.trim().isEmpty()?new JSONObject():JSON.parseObject(body);JSONObject p=root==null?null:root.getJSONObject("p");int number=p==null?1:Math.max(1,p.getIntValue("n"));int size=p==null?50:Math.min(500,Math.max(1,p.getIntValue("s")));return new Query(new Page(number,size),text(root,"targetType"),text(root,"sampleUserId"),text(root,"sceneUserId"));}
        private static String text(JSONObject root,String key){if(root==null)return null;String value=root.getString(key);if(value==null){JSONObject q=root.getJSONObject("q");if(q!=null)value=q.getString(key);}return value==null||value.trim().isEmpty()?null:value.trim();}
    }
    private static final class Page{final int number;final int size;Page(int n,int s){number=n;size=s;}int start(){return(number-1)*size;}int end(){return number*size;}}
    private static final class SqlSpec {
        final String sql;final List<String> values;SqlSpec(String sql,List<String> values){this.sql=sql;this.values=values;}
        static SqlSpec forQuery(String organization,Query query,boolean projectChange){String source="SELECT T.* FROM (SELECT A.*, B.FORMSTYLE, B.BIZCATEGORYNAME, B.EDITMONITORINFO, B.SAMPLEORGID, C.ORGNAME AS REPORTORGNAME, CASE WHEN A.STATUS='cancel' THEN '终止' ELSE '正常' END CANCELSTATUS FROM " + projectSourceTable(projectChange) + " A LEFT JOIN T_EMC_BIZ_CATEGORY B ON A.BIZCATEGORYID=B.ID LEFT JOIN T_CORE_ORG C ON A.REPORTORGID=C.ID) T";StringBuilder sql=new StringBuilder(source).append(" WHERE T.CREATEDBYORGID LIKE ?");List<String> values=new ArrayList<String>();values.add((organization==null?"__NO_ORG__":organization)+"%");if("analysis".equals(query.targetType))sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK O WHERE O.PROJID=T.ID AND O.STATUS='logged' AND O.ANALYSISTYPE='analysis' AND (O.RUNID IS NULL OR O.RUNID=''))");if("samplePlaning".equals(query.targetType))sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_FOLDER F WHERE F.PROJID=T.ID AND F.PROCESSSTATUS='approve')");if("resultInput".equals(query.targetType))sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK O,T_EMC_FOLDER F WHERE O.FOLDERID=F.ID AND F.PROJID=T.ID AND F.PROCESSSTATUS='done')");if("subcontractor".equals(query.targetType))sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK O,T_EMC_SUBCONTRACT_RECORD S WHERE O.ID=S.ORDERTASKID AND O.PROJID=T.ID AND O.STATUS='done' AND O.ANALYSISTYPE='analysis' AND O.SUBCONTRACTORID IS NOT NULL)");if("dataSharing".equals(query.targetType))sql.append(" AND EXISTS (SELECT 1 FROM T_EMC_ORDER_TASK O WHERE O.PROJID=T.ID AND O.STATUS='done' AND O.ANALYSISTYPE='analysis' AND O.DATASHARING='1')");return new SqlSpec(sql.toString(),values);}
        int bind(PreparedStatement statement)throws SQLException{int i=1;for(String value:values)statement.setString(i++,value);return i;}
    }
}
