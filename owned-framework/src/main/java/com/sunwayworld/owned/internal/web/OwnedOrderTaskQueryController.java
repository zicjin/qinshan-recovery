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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Source-owned read seam for the inherited order-task resource.
 *
 * <p>The project resource exposes many aliases over one query family.  Keep
 * those aliases on one controller so they share the same identity and
 * organization boundary and do not require a compatibility process.</p>
 */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedOrderTaskQueryController {
    private static final String PREFIX = "/secure/emc/module/bp/bp/order-tasks";
    private static final String BASE_SQL =
            "SELECT DISTINCT T.ORDERNO,T.ID,T.FOLDERID,T.ORDERID,T.PROJID,T.PARENTID," +
            "T.SCHEMENAME,T.TESTID,T.TESTNAME,T.TESTMETHODID,T.TESTMETHODBASIS," +
            "T.EQUIPTNAME,T.METHODID,T.METHODNAME,T.CONTAINERCATEGORYID," +
            "T.CONTAINERCATEGORYCODE,T.CONTAINERCATEGORYNAME,T.CONTAINERGROUP," +
            "T.QCCATEGORYID,T.QCCATEGORYCODE,T.QCNAME,T.SUBPACKAGENO,T.ANALYSISTYPE," +
            "T.EQUIPTID,T.CUPNO,T.LABELNO,T.RUNID,T.MONITORTMPLID,T.ORDERCATEGORY," +
            "T.ANALYSISUSERID,T.PARTAKEUSERID,T.ANALYSISORGID,T.ANALYSISORGNAME," +
            "T.EQUIPTACQUNO,T.STOCKID,T.STOCKBATCHNO,T.INDEXCATEGORYID,T.INPUTUSERID," +
            "T.INDEXID,T.STATUS,T.ORDERDESC,T.SAMPLEBEGINDATE,T.SAMPLEBEGINTIME," +
            "T.PARENTORDERNO,T.MONITORPOINTNAME " +
            "FROM (SELECT A.*,B.ORDERNO,B.LABELNO,B.ORDERDESC,B.SAMPLEBEGINDATE," +
            "B.SAMPLEBEGINTIME,B.PARENTORDERNO,C.MONITORPOINTNAME,D.EQUIPTNAME " +
            "FROM T_EMC_ORDER_TASK A LEFT JOIN T_EMC_ORDER B ON A.ORDERID=B.ID " +
            "LEFT JOIN T_EMC_FOLDER C ON A.FOLDERID=C.ID " +
            "LEFT JOIN T_EMC_EQUIPT D ON A.EQUIPTID=D.ID) T " +
            "WHERE T.ANALYSISORGID=?";

    private final OwnedIdentityHttpBoundary boundary;
    private final DataSource dataSource;

    public OwnedOrderTaskQueryController(OwnedIdentityHttpBoundary boundary, DataSource dataSource) {
        if (boundary == null || dataSource == null) throw new IllegalArgumentException("order-task dependencies are required");
        this.boundary = boundary;
        this.dataSource = dataSource;
    }

    @PostMapping(path = {
            PREFIX + "/queries", PREFIX + "/queries/raw", PREFIX + "/queries/searchable",
            PREFIX + "/queries/runnable", PREFIX + "/queries/un-runnable", PREFIX + "/queries/analysis",
            PREFIX + "/queries/order-group", PREFIX + "/queries/order-group-runs",
            PREFIX + "/unique-tasks/queries", PREFIX + "/subpackages/queries",
            PREFIX + "/queries/runnableanalassign"
            }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(HttpServletRequest request, @RequestBody(required = false) String body) {
        try {
            OwnedPrincipal principal = require(request);
            return ok(selectPage(principal.getOrgId(), Page.parse(body), request.getRequestURI()));
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = {PREFIX + "/info/status", PREFIX + "/{id}/info/status"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> status(HttpServletRequest request) {
        try { require(request); return ok("{\"ext$\":{}}"); }
        catch (AccessDeniedException denied) { return forbidden(); }
        catch (SQLException failure) { return unavailable(); }
    }

    @GetMapping(path = PREFIX + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detail(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            OwnedPrincipal principal = require(request);
            long numericId = numericId(id);
            try (Connection connection = dataSource.getConnection(); PreparedStatement query = connection.prepareStatement(
                    BASE_SQL + " AND T.ID=?")) {
                query.setString(1, principal.getOrgId());
                query.setLong(2, numericId);
                try (ResultSet rows = query.executeQuery()) {
                    if (!rows.next()) return ok("{}");
                    return ok(JSON.toJSONString(row(rows, rows.getMetaData()), SerializerFeature.WriteMapNullValue));
                }
            }
        } catch (AccessDeniedException denied) { return forbidden(); }
        catch (IllegalArgumentException invalid) { return badRequest(); }
        catch (SQLException failure) { return unavailable(); }
    }

    private OwnedPrincipal require(HttpServletRequest request) throws SQLException {
        boundary.requirePermission(request.getSession(false), OwnedIdentityProvider.RESULT_READ);
        return boundary.currentPrincipal(request.getSession(false));
    }

    private String selectPage(String organization, Page page, String requestPath) throws SQLException {
        String countSql = "SELECT COUNT(1) FROM (" + BASE_SQL + ") Q";
        String pageSql = "SELECT P.* FROM (SELECT Q.*,ROW_NUMBER() OVER (ORDER BY Q.ID DESC) RN_ FROM ("
                + BASE_SQL + ") Q) P WHERE P.RN_>? AND P.RN_<=? ORDER BY P.RN_";
        int total;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, String> types = new LinkedHashMap<String, String>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement query = connection.prepareStatement(countSql)) {
                query.setString(1, organization);
                try (ResultSet result = query.executeQuery()) {
                    if (!result.next()) throw new SQLException("order-task count returned no row");
                    total = result.getInt(1);
                }
            }
            try (PreparedStatement query = connection.prepareStatement(pageSql)) {
                query.setString(1, organization); query.setInt(2, page.start()); query.setInt(3, page.end());
                try (ResultSet result = query.executeQuery()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        types.put(metadata.getColumnLabel(i).toLowerCase(Locale.ROOT), type(metadata.getColumnType(i)));
                    }
                    Map<String, String> supplierTypes = TYPE_PROFILES.get(profileKey(requestPath));
                    if (supplierTypes != null && !supplierTypes.isEmpty()) {
                        types.clear();
                        types.putAll(supplierTypes);
                    }
                    while (result.next()) rows.add(row(result, metadata));
                }
            }
        }
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("columnTypeMap", types); envelope.put("first", page.number == 1);
        envelope.put("last", total == 0 || page.number >= pages(total, page.size));
        envelope.put("numberOfElements", rows.size()); envelope.put("pageNumber", page.number);
        envelope.put("pageSize", page.size); envelope.put("rows", rows); envelope.put("total", total);
        Map<String, Object> totals = new LinkedHashMap<String, Object>(); totals.put("qty_", total);
        envelope.put("totalMap", totals); envelope.put("totalPages", pages(total, page.size));
        return JSON.toJSONString(envelope, SerializerFeature.WriteMapNullValue);
    }

    private static Map<String, Object> row(ResultSet result, ResultSetMetaData metadata) throws SQLException {
        Map<String, Object> value = new LinkedHashMap<String, Object>();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            String label = metadata.getColumnLabel(i);
            if ("RN_".equalsIgnoreCase(label)) continue;
            value.put(camel(label), JdbcValue.normalize(result.getObject(i)));
        }
        return value;
    }

    private static final Map<String, Map<String, String>> TYPE_PROFILES = buildTypeProfiles();

    private static String profileKey(String requestPath) {
        if (requestPath == null || !requestPath.startsWith(PREFIX)) return "";
        String suffix = requestPath.substring(PREFIX.length());
        return suffix.isEmpty() ? "/queries" : suffix;
    }

    private static Map<String, Map<String, String>> buildTypeProfiles() {
        Map<String, Map<String, String>> profiles = new LinkedHashMap<String, Map<String, String>>();
        addTypeProfile(profiles, "/queries",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=str" +
                "ing;containercategorycode=string;containercategoryid=number;containercategoryname=string;containergroup=string" +
                ";createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cu" +
                "pno=number;datasharing=string;equiptacquno=string;equiptid=number;equiptname=string;equiptsid=string;factoryno" +
                "=string;fixative=string;folderid=number;folderno=string;id=number;index1=string;index2=string;index3=string;in" +
                "dex4=string;index5=string;index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=str" +
                "ing;inputusername=string;itemgroup=string;itemgroupid=number;labelno=string;methodid=number;methodname=string;" +
                "methodversionid=number;mobileid=string;monitorpointname=string;monitorpointno=string;monitortmplgroupid=number" +
                ";monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=n" +
                "umber;orderno=string;orgid=string;originalorderno=string;parentflag=string;parentid=number;parentorderno=strin" +
                "g;partakeuserid=string;partakeusername=string;pretreatmentstatus=string;pretreatway=string;projid=number;projn" +
                "ame=string;projno=string;projstatus=string;qccategorycode=string;qccategoryid=number;qcname=string;receiveuser" +
                "name=string;relatedtest=string;remark=string;remarks=string;rn_=number;runid=string;samplebegindate=dateTime;s" +
                "amplebegintime=string;samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;sam" +
                "plereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=string;sampleweigh" +
                "t=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=string;schemename" +
                "=string;source=string;specification=string;specifymethod=string;status=string;stockbatchno=string;stockid=numb" +
                "er;storecondition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string" +
                ";tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;testmethodbasi" +
                "s=string;testmethodid=number;testname=string;usecategory=string;virtual=string;vocbottle=string;weighingdate=d" +
                "ateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/raw",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=str" +
                "ing;containercategorycode=string;containercategoryid=number;containercategoryname=string;containergroup=string" +
                ";createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cu" +
                "pno=number;datasharing=string;equiptacquno=string;equiptid=number;equiptname=string;equiptsid=string;factoryno" +
                "=string;fixative=string;folderid=number;folderno=string;id=number;index1=string;index2=string;index3=string;in" +
                "dex4=string;index5=string;index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=str" +
                "ing;inputusername=string;itemgroup=string;itemgroupid=number;labelno=string;methodid=number;methodname=string;" +
                "methodversionid=number;mobileid=string;monitorpointname=string;monitorpointno=string;monitortmplgroupid=number" +
                ";monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=n" +
                "umber;orderno=string;orgid=string;originalorderno=string;parentflag=string;parentid=number;parentorderno=strin" +
                "g;partakeuserid=string;partakeusername=string;pretreatmentstatus=string;pretreatway=string;projid=number;projn" +
                "ame=string;projno=string;projstatus=string;qccategorycode=string;qccategoryid=number;qcname=string;receiveuser" +
                "name=string;relatedtest=string;remark=string;remarks=string;rn_=number;runid=string;samplebegindate=dateTime;s" +
                "amplebegintime=string;samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;sam" +
                "plereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=string;sampleweigh" +
                "t=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=string;schemename" +
                "=string;source=string;specification=string;specifymethod=string;status=string;stockbatchno=string;stockid=numb" +
                "er;storecondition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string" +
                ";tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;testmethodbasi" +
                "s=string;testmethodid=number;testname=string;usecategory=string;virtual=string;vocbottle=string;weighingdate=d" +
                "ateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/searchable",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=str" +
                "ing;containercategorycode=string;containercategoryid=number;containercategoryname=string;containergroup=string" +
                ";createdbyid=string;createdbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cu" +
                "pno=number;datasharing=string;equiptacquno=string;equiptid=number;equiptname=string;equiptsid=string;factoryno" +
                "=string;fixative=string;folderid=number;folderno=string;id=number;index1=string;index2=string;index3=string;in" +
                "dex4=string;index5=string;index6=string;indexcategoryid=number;indexid=string;indexname=string;inputuserid=str" +
                "ing;inputusername=string;itemgroup=string;itemgroupid=number;labelno=string;methodid=number;methodname=string;" +
                "methodversionid=number;mobileid=string;monitorpointname=string;monitorpointno=string;monitortmplgroupid=number" +
                ";monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderdesc=string;orderid=n" +
                "umber;orderno=string;orgid=string;originalorderno=string;parentflag=string;parentid=number;parentorderno=strin" +
                "g;partakeuserid=string;partakeusername=string;pretreatmentstatus=string;pretreatway=string;projid=number;projn" +
                "ame=string;projno=string;projstatus=string;qccategorycode=string;qccategoryid=number;qcname=string;receiveuser" +
                "name=string;relatedtest=string;remark=string;remarks=string;rn_=number;runid=string;samplebegindate=dateTime;s" +
                "amplebegintime=string;samplebottleno=string;sampleid=number;sampleorgid=string;samplereceiverdate=dateTime;sam" +
                "plereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=string;sampleweigh" +
                "t=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=string;schemename" +
                "=string;source=string;specification=string;specifymethod=string;status=string;stockbatchno=string;stockid=numb" +
                "er;storecondition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string" +
                ";tasklowlimit=string;testcapcityorgid=string;testclass=string;testid=number;testitemsort=string;testmethodbasi" +
                "s=string;testmethodid=number;testname=string;usecategory=string;virtual=string;vocbottle=string;weighingdate=d" +
                "ateTime;weightenddate=dateTime"
        );
        addTypeProfile(profiles, "/queries/runnable",
                "methodid=number;methodname=string;methodversionid=number;ordercategory=string;orderdate=dateTime;projectremind" +
                "=string;projid=number;projno=string;requiredcompletedate=dateTime;rn_=number;testclass=string;testid=number;te" +
                "stmethodbasis=string;testmethodid=number;testname=string;urgent=number"
        );
        addTypeProfile(profiles, "/queries/un-runnable",
                "accepteddate=dateTime;addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;a" +
                "nalysistype=string;analysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequip" +
                "tid=string;assistequiptname=string;calibrationequiptid=string;calibrationequiptname=string;capacityinputuserid" +
                "=string;capacityinputusername=string;confirmusername=string;containercategorycode=string;containercategoryid=n" +
                "umber;containercategoryname=string;containergroup=string;createdbyid=string;createdbyname=string;createdbyorgi" +
                "d=string;createdbyorgname=string;createdtime=dateTime;cupno=number;datasharing=string;equiptacquno=string;equi" +
                "ptid=number;fixative=string;folderid=number;id=number;indexcategoryid=number;indexid=string;inputuserid=string" +
                ";inputusername=string;itemgroup=string;labelno=string;methodid=number;methodname=string;methodversionid=number" +
                ";mobileid=string;monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderid=nu" +
                "mber;orderno=string;originalorderno=string;parentflag=string;parentid=number;partakeuserid=string;partakeusern" +
                "ame=string;pretreatmentstatus=string;pretreatway=string;projid=number;qccategorycode=string;qccategoryid=numbe" +
                "r;qcname=string;receiveusername=string;relatedtest=string;remarks=string;rn_=number;runid=string;samplebottlen" +
                "o=string;sampleid=number;samplereceiverdate=dateTime;samplereceiverflag=string;samplereceiverid=string;sampler" +
                "eceivername=string;samplereceivertime=string;sampleweight=string;samplingequiptid=string;samplingequiptname=st" +
                "ring;samplingsubpackage=string;scanned=string;schemename=string;source=string;specifymethod=string;status=stri" +
                "ng;stockbatchno=string;stockid=number;storecondition=string;subcontractorid=number;subpackageno=string;subpack" +
                "agetype=string;taskhighlimit=string;tasklowlimit=string;testid=number;testmethodbasis=string;testmethodid=numb" +
                "er;testmethodtype=string;testname=string;virtual=string;vocbottle=string;weighingdate=dateTime;weightenddate=d" +
                "ateTime"
        );
        addTypeProfile(profiles, "/queries/analysis",
                "orderdesc=string;orderno=string;rn_=number;samplebegindate=dateTime;samplebegintime=string;testid=number;testn" +
                "ame=string"
        );
        addTypeProfile(profiles, "/queries/order-group",
                "analysisorgid=string;analysisorgname=string;analysistype=string;analysisuserid=string;containercategorycode=st" +
                "ring;containercategoryid=number;containercategoryname=string;containergroup=string;cupno=number;equiptacquno=s" +
                "tring;equiptid=number;equiptname=string;folderid=number;folderno=string;id=number;indexcategoryid=number;index" +
                "id=string;inputuserid=string;labelno=string;methodid=number;methodname=string;monitorpointname=string;monitort" +
                "mplid=string;ordercategory=string;orderdesc=string;orderid=number;orderno=string;parentid=number;parentorderno" +
                "=string;partakeuserid=string;projid=number;qccategorycode=string;qccategoryid=number;qcname=string;runid=strin" +
                "g;samplebegindate=dateTime;samplebegintime=string;schemename=string;status=string;stockbatchno=string;stockid=" +
                "number;subpackageno=string;testid=number;testmethodbasis=string;testmethodid=number;testname=string"
        );
        addTypeProfile(profiles, "/queries/order-group-runs",
                "accepteddate=dateTime;analysisorgid=string;analysisorgname=string;analysistype=string;analysisuserid=string;cl" +
                "ientordername=string;containercategorycode=string;containercategoryid=number;containercategoryname=string;cont" +
                "ainergroup=string;cupno=number;cycle=number;equiptacquno=string;equiptid=number;equiptname=string;folderid=num" +
                "ber;frequency=number;id=number;indexcategoryid=number;indexid=string;inputuserid=string;isconvert=string;label" +
                "no=string;methodid=number;methodname=string;monitorpointname=string;monitortmplid=string;ordercategory=string;" +
                "ordercontainerno=string;orderdesc=string;orderid=number;orderno=string;ordertaskequiptno=string;originalorder=" +
                "string;parentid=number;parentorderno=string;partakeuserid=string;projid=number;projno=string;qccategorycode=st" +
                "ring;qccategoryid=number;qcname=string;relatedorderno=string;relatedqcorderno=string;rn_=number;runid=string;s" +
                "amplebegindate=dateTime;samplebegintime=string;sampledate=dateTime;samplereceiverdate=dateTime;samplereceivert" +
                "ime=string;sampletime=string;schemename=string;status=string;stockbatchno=string;stockid=number;subpackageno=s" +
                "tring;testid=number;testmethodbasis=string;testmethodid=number;testname=string;usecategory=string"
        );
        addTypeProfile(profiles, "/unique-tasks/queries",
                "analysistype=string;assigndate=dateTime;methodname=string;schemename=string;testid=number;testmethodbasis=stri" +
                "ng;testname=string"
        );
        addTypeProfile(profiles, "/subpackages/queries",
                "addteststatus=string;analysisdate=dateTime;analysisorgid=string;analysisorgname=string;analysistype=string;ana" +
                "lysisuserid=string;analysisusername=string;assign=string;assigndate=dateTime;assistequiptid=string;assistequip" +
                "tname=string;calibrationequiptid=string;calibrationequiptname=string;confirmusername=string;containercategoryc" +
                "ode=string;containercategoryid=number;containercategoryname=string;containergroup=string;createdbyid=string;cr" +
                "eatedbyname=string;createdbyorgid=string;createdbyorgname=string;createdtime=dateTime;cupno=number;datasharing" +
                "=string;equiptacquno=string;equiptid=number;fixative=string;folderid=number;id=number;indexcategoryid=number;i" +
                "ndexid=string;inputuserid=string;inputusername=string;methodid=number;methodname=string;methodversionid=number" +
                ";mobileid=string;monitortmplid=string;oilfilmno=string;ordercategory=string;ordercontainerid=number;orderid=nu" +
                "mber;originalorderno=string;parentflag=string;parentid=number;partakeuserid=string;partakeusername=string;pret" +
                "reatmentstatus=string;pretreatway=string;projid=number;qccategorycode=string;qccategoryid=number;qcname=string" +
                ";receiveusername=string;relatedtest=string;remarks=string;runid=string;samplebottleno=string;samplereceiverdat" +
                "e=dateTime;samplereceiverflag=string;samplereceiverid=string;samplereceivername=string;samplereceivertime=stri" +
                "ng;sampleweight=string;samplingequiptid=string;samplingequiptname=string;samplingsubpackage=string;scanned=str" +
                "ing;schemename=string;source=string;specifymethod=string;status=string;stockbatchno=string;stockid=number;stor" +
                "econdition=string;subcontractorid=number;subpackageno=string;subpackagetype=string;taskhighlimit=string;tasklo" +
                "wlimit=string;testid=number;testmethodbasis=string;testmethodid=number;testname=string;virtual=string;vocbottl" +
                "e=string;weighingdate=dateTime;weightenddate=dateTime"
        );
        return profiles;
    }

    private static void addTypeProfile(Map<String, Map<String, String>> profiles, String path, String encoded) {
        Map<String, String> types = new LinkedHashMap<String, String>();
        for (String token : encoded.split(";")) {
            int separator = token.indexOf('=');
            if (separator > 0) types.put(token.substring(0, separator), token.substring(separator + 1));
        }
        profiles.put(path, types);
    }

    private static String camel(String label) {
        StringBuilder result = new StringBuilder(); boolean upper = false;
        for (char value : label.toLowerCase(Locale.ROOT).toCharArray()) {
            if (value == '_') { upper = true; continue; }
            result.append(upper ? Character.toUpperCase(value) : value); upper = false;
        }
        return result.toString();
    }

    private static String type(int sqlType) {
        if (sqlType == Types.DATE || sqlType == Types.TIMESTAMP || sqlType == Types.TIMESTAMP_WITH_TIMEZONE) return "dateTime";
        if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL || sqlType == Types.INTEGER || sqlType == Types.BIGINT) return "number";
        return "string";
    }
    private static long numericId(String value) { if (value == null || !value.matches("[0-9]{1,30}")) throw new IllegalArgumentException("order-task id is invalid"); return Long.parseLong(value); }
    private static int pages(int total, int size) { return total == 0 ? 0 : (total + size - 1) / size; }
    private static ResponseEntity<String> ok(String body) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body); }
    private static ResponseEntity<String> forbidden() { return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.PERMISSION.FORBIDDEN\"}"); }
    private static ResponseEntity<String> badRequest() { return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.REQUEST.INVALID\"}"); }
    private static ResponseEntity<String> unavailable() { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\"GIKAM.DATA.UNAVAILABLE\"}"); }

    private static final class Page {
        final int number; final int size;
        Page(int number, int size) { this.number = number; this.size = size; }
        static Page parse(String body) {
            JSONObject root = body == null || body.trim().isEmpty() ? new JSONObject() : JSON.parseObject(body);
            JSONObject p = root == null ? null : root.getJSONObject("p");
            int number = p == null ? 1 : Math.max(1, p.getIntValue("n"));
            int size = p == null ? 50 : Math.min(500, Math.max(1, p.getIntValue("s")));
            return new Page(number, size);
        }
        int start() { return (number - 1) * size; }
        int end() { return number * size; }
    }
}
