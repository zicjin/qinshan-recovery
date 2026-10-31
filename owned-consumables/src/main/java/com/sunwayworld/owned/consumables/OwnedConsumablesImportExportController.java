package com.sunwayworld.owned.consumables;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.AuditEvent;
import com.sunwayworld.owned.internal.web.AuditEventSink;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import com.sunwayworld.owned.internal.web.OwnedWriteSupport;

/** Isolated template/download and Excel import seam for consumables. */
@RestController
public final class OwnedConsumablesImportExportController {
    private static final String TEMPLATE = "emcConsumeExportTemplate";
    private static final String[] HEADERS = {"序号", "货号", "材料名称", "规格型号", "库存", "存放位置", "供应商"};
    private static final String[] COMMENTS = {"emcMtl", "productNo", "mtlName", "specification", "stockQty", "location", "vendorid"};

    private final DataSource dataSource;
    private final JdbcPersonnelIdentityProvider provider;
    private final OwnedIdentityHttpBoundary boundary;
    private final AuditEventSink audit;
    private final Environment environment;

    public OwnedConsumablesImportExportController(DataSource dataSource, JdbcPersonnelIdentityProvider provider,
            OwnedIdentityHttpBoundary boundary, AuditEventSink audit, Environment environment) {
        this.dataSource = dataSource;
        this.provider = provider;
        this.boundary = boundary;
        this.audit = audit;
        this.environment = environment;
    }

    /** Exposes the isolated write seam to the owned workbench without weakening its default deny boundary. */
    @GetMapping(value = "/owned/consumables/import-export-available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> available(HttpServletRequest request) {
        try {
            authorize(request);
            return ResponseEntity.ok(java.util.Collections.singletonMap("available", Boolean.TRUE));
        } catch (AccessDeniedException denied) {
            return error(HttpStatus.FORBIDDEN, "GIKAM.PERMISSION.UNAUTHORIZED");
        } catch (Exception failure) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "GIKAM.CONSUMABLES.IMPORT_EXPORT_UNAVAILABLE");
        }
    }

    @GetMapping(value = "/core/module/item/files/{templateCode}/action/download-template/{service}")
    public ResponseEntity<?> downloadTemplate(HttpServletRequest request, @PathVariable String templateCode,
            @PathVariable String service) {
        try {
            OwnedPrincipal principal = authorize(request);
            if (!TEMPLATE.equals(templateCode) || !TEMPLATE.equals(service)) return error(HttpStatus.NOT_FOUND, "GIKAM.CONSUMABLES.TEMPLATE_NOT_FOUND");
            byte[] bytes = template();
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-template-export", principal.getId(), TEMPLATE,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=emcConsumeExportTemplate.xls")
                    .body(bytes);
        } catch (AccessDeniedException denied) { return error(HttpStatus.FORBIDDEN, "GIKAM.PERMISSION.UNAUTHORIZED"); }
        catch (IllegalArgumentException invalid) { return error(HttpStatus.BAD_REQUEST, "GIKAM.CONSUMABLES.INVALID_REQUEST"); }
        catch (Exception failure) { return error(HttpStatus.INTERNAL_SERVER_ERROR, "GIKAM.CONSUMABLES.IMPORT_EXPORT_FAILED"); }
    }

    @PostMapping(value = "/core/module/item/files/{id}/action/import/emcConsumeImportFileFunction",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importWorkbook(HttpServletRequest request, @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        try {
            OwnedPrincipal principal = authorize(request);
            if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is required");
            int rows = importRows(file, principal);
            audit.record(new AuditEvent(java.time.Instant.now(), "consumable-import", principal.getId(), id,
                    AuditEvent.Outcome.ALLOWED, "isolated-write", null));
            Map<String,Object> body = new LinkedHashMap<String,Object>();
            body.put("status", "success"); body.put("rows", rows); body.put("service", "emcConsumeImportFileFunction");
            return ResponseEntity.ok(body);
        } catch (AccessDeniedException denied) { return error(HttpStatus.FORBIDDEN, "GIKAM.PERMISSION.UNAUTHORIZED"); }
        catch (IllegalArgumentException invalid) { return error(HttpStatus.BAD_REQUEST, "GIKAM.CONSUMABLES.INVALID_REQUEST"); }
        catch (Exception failure) { return error(HttpStatus.INTERNAL_SERVER_ERROR, "GIKAM.CONSUMABLES.IMPORT_EXPORT_FAILED"); }
    }

    private OwnedPrincipal authorize(HttpServletRequest request) throws SQLException {
        if (environment == null || !"isolated".equals(environment.getProperty("owned.consumables.import-export.mode"))) throw denied();
        OwnedWriteSupport.requireLoopback(request);
        HttpSession session = request.getSession(false);
        boundary.requirePermission(session, OwnedIdentityProvider.CONSUMABLE_MANAGE);
        OwnedPrincipal principal = boundary.currentPrincipal(session);
        if (principal.getOrgId() == null || !provider.permittedOrganizationIds(principal).contains(principal.getOrgId())) throw denied();
        return principal;
    }

    private int importRows(MultipartFile file, OwnedPrincipal principal) throws Exception {
        List<ImportRow> rows = readRows(file.getInputStream());
        if (rows.isEmpty()) return 0;
        OwnedWriteSupport.transaction(dataSource, connection -> {
            for (ImportRow row : rows) applyRow(connection, row, principal);
            return null;
        });
        return rows.size();
    }

    private void applyRow(Connection connection, ImportRow row, OwnedPrincipal principal) throws SQLException {
        String materialId = findMaterial(connection, row, principal.getOrgId());
        if (materialId == null) {
            materialId = String.valueOf(OwnedWriteSupport.nextIdentity(connection));
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO T_EMC_CONSUME (ID,MTLNAME,SPECIFICATION,STOCKQTY,PRODUCTNO,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,SYSTIMESTAMP,?,?)")) {
                insert.setString(1, materialId); insert.setString(2, row.name); insert.setString(3, row.specification);
                if (row.stock == null) insert.setNull(4, Types.NUMERIC); else insert.setDouble(4, row.stock.doubleValue());
                insert.setString(5, row.productNo); insert.setString(6, principal.getId());
                insert.setString(7, principal.getId()); insert.setString(8, principal.getOrgId()); insert.setString(9, principal.getOrgName());
                insert.executeUpdate();
            }
        } else {
            try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CONSUME SET MTLNAME=?,SPECIFICATION=?,PRODUCTNO=? WHERE ID=? AND CREATEDBYORGID=?")) {
                update.setString(1, row.name); update.setString(2, row.specification); update.setString(3, row.productNo);
                update.setString(4, materialId); update.setString(5, principal.getOrgId()); update.executeUpdate();
            }
        }
        if (row.stock == null) return;
        Long stockId = findStock(connection, materialId);
        if (stockId == null) {
            stockId = OwnedWriteSupport.nextIdentity(connection);
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO T_EMC_CSM_STOCK (ID,MTLID,STOCKQTY,STATUS,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,'0',?,?,SYSTIMESTAMP,?,?)")) {
                insert.setLong(1, stockId); insert.setString(2, materialId); insert.setDouble(3, row.stock.doubleValue());
                insert.setString(4, principal.getId()); insert.setString(5, principal.getId()); insert.setString(6, principal.getOrgId()); insert.setString(7, principal.getOrgName()); insert.executeUpdate();
            }
            insertLog(connection, stockId, "添加", 0d, row.stock, principal);
        } else {
            Double previous = stockQty(connection, stockId);
            try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CSM_STOCK SET STOCKQTY=? WHERE ID=?")) {
                update.setDouble(1, row.stock); update.setLong(2, stockId); update.executeUpdate();
            }
            if (row.stock != null && (previous == null || Double.compare(previous.doubleValue(), row.stock.doubleValue()) != 0)) insertLog(connection, stockId, "盘存", previous == null ? 0d : previous, row.stock.doubleValue(), principal);
        }
        if (row.stock != null) {
            try (PreparedStatement update = connection.prepareStatement("UPDATE T_EMC_CONSUME SET STOCKQTY=? WHERE ID=? AND CREATEDBYORGID=?")) {
                update.setDouble(1, row.stock.doubleValue()); update.setString(2, materialId); update.setString(3, principal.getOrgId()); update.executeUpdate();
            }
        }
    }

    private String findMaterial(Connection c, ImportRow row, String org) throws SQLException {
        String sql = row.productNo == null ? "SELECT ID FROM T_EMC_CONSUME WHERE MTLNAME=? AND (PRODUCTNO IS NULL OR PRODUCTNO='') AND CREATEDBYORGID=?" : "SELECT ID FROM T_EMC_CONSUME WHERE PRODUCTNO=? AND CREATEDBYORGID=?";
        try (PreparedStatement q = c.prepareStatement(sql)) { q.setString(1, row.productNo == null ? row.name : row.productNo); q.setString(2, org); try (ResultSet r=q.executeQuery()) { return r.next() ? r.getString(1) : null; } }
    }
    private Long findStock(Connection c, String material) throws SQLException { try (PreparedStatement q=c.prepareStatement("SELECT ID FROM T_EMC_CSM_STOCK WHERE MTLID=? AND STATUS='0' ORDER BY ID DESC")) { q.setString(1,material); try(ResultSet r=q.executeQuery()){return r.next()?Long.valueOf(r.getLong(1)):null;} } }
    private Double stockQty(Connection c, Long id) throws SQLException { try(PreparedStatement q=c.prepareStatement("SELECT STOCKQTY FROM T_EMC_CSM_STOCK WHERE ID=?")){q.setLong(1,id);try(ResultSet r=q.executeQuery()){return r.next()?r.getDouble(1):null;}} }
    private void insertLog(Connection c, Long stock, String type, double previous, double qty, OwnedPrincipal p) throws SQLException { try(PreparedStatement i=c.prepareStatement("INSERT INTO T_EMC_CSM_STOCK_LOG (ID,STOCKID,LOGTYPE,PREVIOUSQTY,QTY,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?, ?,?,?,?,SYSTIMESTAMP,?,?)")){i.setLong(1,OwnedWriteSupport.nextIdentity(c));i.setLong(2,stock);i.setString(3,type);i.setDouble(4,previous);i.setDouble(5,qty);i.setString(6,p.getId());i.setString(7,p.getId());i.setString(8,p.getOrgId());i.setString(9,p.getOrgName());i.executeUpdate();} }

    private static List<ImportRow> readRows(InputStream input) throws IOException {
        List<ImportRow> result = new ArrayList<ImportRow>();
        try (Workbook workbook = new HSSFWorkbook(input)) {
            if (workbook.getNumberOfSheets() == 0) throw new IllegalArgumentException("workbook has no sheet");
            Row header = workbook.getSheetAt(0).getRow(1);
            if (header == null) throw new IllegalArgumentException("header row is missing");
            for (int i=0;i<HEADERS.length;i++) if (!HEADERS[i].equals(cell(header,i))) throw new IllegalArgumentException("template header mismatch");
            for (int i=2;i<=workbook.getSheetAt(0).getLastRowNum();i++) {
                Row row=workbook.getSheetAt(0).getRow(i); if (row==null || blank(row)) continue;
                String product=empty(cell(row,1)); String name=empty(cell(row,2)); if(name==null) throw new IllegalArgumentException("material name is required at row "+(i+1));
                String specification=empty(cell(row,3)); String stockText=empty(cell(row,4)); Double stock=null;
                if (stockText != null) {
                    try { stock=Double.valueOf(stockText); } catch(NumberFormatException e){throw new IllegalArgumentException("stock is invalid at row "+(i+1));}
                    if (stock.doubleValue() < 0d || Double.isInfinite(stock.doubleValue()) || Double.isNaN(stock.doubleValue())) throw new IllegalArgumentException("stock is invalid at row "+(i+1));
                }
                result.add(new ImportRow(product,name,specification,stock));
            }
        }
        return result;
    }
    private static byte[] template() throws IOException { HSSFWorkbook w=new HSSFWorkbook(); HSSFSheet s=w.createSheet("耗材导入模板"); HSSFPatriarch drawing=s.createDrawingPatriarch(); HSSFRow title=s.createRow(0); title.createCell(0).setCellValue("耗材导入模版"); HSSFRow h=s.createRow(1); for(int i=0;i<HEADERS.length;i++){HSSFCell c=h.createCell(i);c.setCellValue(HEADERS[i]);HSSFComment comment=drawing.createCellComment(new HSSFClientAnchor(0,0,0,0,(short)i,1,(short)(i+2),3));comment.setString(new HSSFRichTextString(COMMENTS[i]));c.setCellComment(comment);} ByteArrayOutputStream out=new ByteArrayOutputStream(); w.write(out); w.close(); return out.toByteArray(); }
    private static boolean blank(Row r){for(int i=0;i<HEADERS.length;i++){String value=empty(cell(r,i));if(value!=null&&!value.isEmpty())return false;}return true;}
    private static String cell(Row row,int index){Cell c=row.getCell(index);if(c==null)return "";if(c.getCellType()==CellType.NUMERIC)return String.valueOf(c.getNumericCellValue()).replaceAll("\\.0$","");return c.toString().trim();}
    private static String empty(String s){return s==null||s.trim().isEmpty()?null:s.trim();}
    private static AccessDeniedException denied(){return new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");}
    private static ResponseEntity<String> error(HttpStatus status,String message){return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body("{\"message\":\""+message+"\"}");}
    private static final class ImportRow { final String productNo,name,specification; final Double stock; ImportRow(String p,String n,String s,Double q){productNo=p;name=n;specification=s;stock=q;} }
}
