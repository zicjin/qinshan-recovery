import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;
import javax.sql.DataSource;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/** Test lifecycle only; all page, identity and data routes are shipped runtime. */
public final class OwnedInventoryBrowserApplication {
    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(args[0]))) { p.load(input); }
        com.sunwayworld.emc.projectfunction.GikamWpjcApplication.main(new String[]{
            "--server.address=127.0.0.1", "--server.port=0", "--spring.config.name=owned-browser-test",
            "--spring.main.banner-mode=off", "--spring.main.log-startup-info=false",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
            "--spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource",
            "--spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
            "--spring.datasource.url="+p.getProperty("url"), "--spring.datasource.username="+p.getProperty("user"),
            "--spring.datasource.password="+p.getProperty("password"), "--sunway.user.password.encoder.type=MD5",
            "--server.tomcat.threads.max=2", "--server.tomcat.threads.min-spare=1",
            "--owned.identity.audit.type=file", "--owned.identity.audit.path="+args[3],
            "--owned.consumables.import-export.mode=isolated", "--owned.consumables.write.mode=isolated",
            "--owned.equipment.write.mode=isolated"});
        ConfigurableApplicationContext app = OwnedApplicationContext.getApplicationContext();
        try {
            DataSource ds = app.getBean(DataSource.class);
            // Extra, explicitly synthetic employees exercise real UI pagination.
            try (Connection c = ds.getConnection(); PreparedStatement s = c.prepareStatement(
                    "INSERT INTO T_EMC_EMP (ID,USERNAME,CREATEDTIME,ORGID,AGE,REMARK) VALUES (?,?,TIMESTAMP '2025-01-01 00:00:00','org-a',25,'synthetic pagination fixture')")) {
                for (int i=1;i<=21;i++) { s.setString(1,String.format("page-%03d",i)); s.setString(2,String.format("Fixture %03d",i)); s.executeUpdate(); }
            }
            try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
                for (int i=1;i<=21;i++) {
                    s.execute("INSERT INTO T_EMC_EQUIPT (ID,EQUIPTID,EQUIPTNAME,EQUIPTCATEGORYID,USEDDEPARTMENTID,ORGID) VALUES ("+(100+i)+",'EQ-PAGE-"+String.format("%03d",i)+"','Synthetic equipment page "+i+"','11','org-a','org-a')");
                    s.execute("INSERT INTO T_EMC_CONSUME (ID,MTLNAME,SPECIFICATION,STOCKQTY,CREATEDBYORGID) VALUES ('CSM-PAGE-"+String.format("%03d",i)+"','Synthetic material page "+i+"','fixture',0,'org-a')");
                }
            }
            try (org.apache.poi.hssf.usermodel.HSSFWorkbook workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook()) {
                org.apache.poi.hssf.usermodel.HSSFSheet sheet = workbook.createSheet("耗材导入");
                sheet.createRow(0).createCell(0).setCellValue("耗材导入模版");
                String[] headers = {"序号", "货号", "材料名称", "规格型号", "库存", "存放位置", "供应商"};
                org.apache.poi.hssf.usermodel.HSSFRow header = sheet.createRow(1);
                for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
                org.apache.poi.hssf.usermodel.HSSFRow row = sheet.createRow(2);
                String[] values = {"1", "P-BROWSER", "Browser Imported", "box", "9", "Shelf", "vendor"};
                for (int i = 0; i < values.length; i++) row.createCell(i).setCellValue(values[i]);
                try (OutputStream output = Files.newOutputStream(Paths.get(args[4]))) { workbook.write(output); }
            }
            try (org.apache.poi.hssf.usermodel.HSSFWorkbook workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook()) {
                org.apache.poi.hssf.usermodel.HSSFSheet sheet = workbook.createSheet("耗材导入");
                sheet.createRow(0).createCell(0).setCellValue("耗材导入模版");
                String[] headers = {"序号", "货号", "材料名称", "规格型号", "库存", "存放位置", "供应商"};
                org.apache.poi.hssf.usermodel.HSSFRow header = sheet.createRow(1);
                for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);
                org.apache.poi.hssf.usermodel.HSSFRow row = sheet.createRow(2);
                String[] values = {"1", "P-BROWSER-BAD", "Browser Invalid", "box", "-1", "Shelf", "vendor"};
                for (int i = 0; i < values.length; i++) row.createCell(i).setCellValue(values[i]);
                try (OutputStream output = Files.newOutputStream(Paths.get(args[5]))) { workbook.write(output); }
            }
            int port = ((ServletWebServerApplicationContext)app).getWebServer().getPort();
            Files.write(Paths.get(args[1]),String.valueOf(port).getBytes(StandardCharsets.UTF_8));
            long deadline = System.currentTimeMillis()+600000;
            while (!Files.exists(Paths.get(args[2])) && System.currentTimeMillis()<deadline) Thread.sleep(200);
        } finally { app.close(); }
        System.exit(0);
    }
}
