import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.DigestOutputStream;
import java.sql.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Oracle full-content snapshot reader. Compile with Java 8; provide an external
 * ojdbc driver on the classpath. No arguments: stdin is Java Properties with
 * url, user, password, schema. Never put private properties on a command line.
 *
 * v1 canonical framing: big-endian int UTF-8 tag length, tag bytes, big-endian
 * long payload length, payload bytes. Rows start with ROW-v1 (empty payload),
 * followed by column fields in metadata order. NULL has its own tag. Numbers
 * strip trailing zeros (zero becomes 0); text uses UTF-8 without normalization.
 * LOB payload = big-endian long byte length + SHA-256 of streamed bytes. CLOB
 * bytes are UTF-8. DATE/TIMESTAMP use UTC Calendar, ISO local wall-clock text
 * with seconds and nine fractional digits. Oracle DATE retains time-of-day.
 * Tables contain TABLE-v1, COUNT, then lexically sorted hexadecimal row hashes
 * decoded to bytes under ROW tags. Duplicates are retained. Schema hash frames
 * SCHEMA-v1, table name, ordered column metadata, COUNT and content hash for
 * each table sorted by Java String order. Owner is intentionally excluded so
 * baseline/runtime snapshots can be compared. No row or LOB values are output.
 *
 * Row bodies/LOBs stream; only 32-byte row hashes (encoded as hex) accumulate
 * in memory for sorting. An OOM, unsupported type or SQL failure emits no report.
 * This measures content, not fixture/audit/rollback/cleanup or strict evidence.
 */
public final class OracleContentFingerprint {
    private static final java.nio.charset.Charset UTF8 = StandardCharsets.UTF_8;
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS", Locale.ROOT);
    private static final Set<Integer> coverage = new TreeSet<Integer>();

    private static final class Failure extends Exception {
        final String code;
        Failure(String code) { this.code = code; }
    }
    private static MessageDigest digest() throws NoSuchAlgorithmException { return MessageDigest.getInstance("SHA-256"); }
    private static byte[] text(String s) { return s.getBytes(UTF8); }
    private static void field(DataOutputStream out, String tag, byte[] value) throws IOException {
        byte[] t = text(tag);
        out.writeInt(t.length); out.write(t); out.writeLong(value.length); out.write(value);
    }
    private static String hex(byte[] bytes) {
        char[] alphabet = "0123456789abcdef".toCharArray();
        char[] encoded = new char[bytes.length * 2];
        for (int i=0; i<bytes.length; i++) {
            encoded[i*2]=alphabet[(bytes[i] & 255) >>> 4];
            encoded[i*2+1]=alphabet[bytes[i] & 15];
        }
        return new String(encoded);
    }
    private static byte[] unhex(String s) {
        byte[] b = new byte[s.length()/2];
        for (int i=0; i<b.length; i++) b[i]=(byte)Integer.parseInt(s.substring(i*2,i*2+2),16);
        return b;
    }
    private static DataOutputStream sink(MessageDigest d) {
        return new DataOutputStream(new DigestOutputStream(new OutputStream() { public void write(int b) {} public void write(byte[] b,int o,int n) {} }, d));
    }
    private static String quote(String name) throws Failure {
        if (name == null || name.isEmpty() || name.indexOf('\0') >= 0) throw new Failure("INVALID_METADATA");
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }
    private static String json(String s) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            if(c == '\\' || c == '"') out.append('\\').append(c);
            else if(c < 32 || c > 126) out.append(String.format(Locale.ROOT,"\\u%04x",(int)c));
            else out.append(c);
        }
        return out.append('"').toString();
    }
    private static void supported(int type) throws Failure {
        switch(type) {
            case Types.CHAR: case Types.VARCHAR: case Types.LONGVARCHAR:
            case Types.NCHAR: case Types.NVARCHAR: case Types.LONGNVARCHAR:
            case Types.NUMERIC: case Types.DECIMAL: case Types.INTEGER:
            case Types.SMALLINT: case Types.TINYINT: case Types.BIGINT:
            case Types.FLOAT: case Types.REAL: case Types.DOUBLE:
            case Types.DATE: case Types.TIMESTAMP:
            case Types.BINARY: case Types.VARBINARY: case Types.LONGVARBINARY:
            case Types.BLOB: case Types.CLOB: case Types.NCLOB: return;
            default: throw new Failure("UNSUPPORTED_JDBC_TYPE:" + type);
        }
    }
    private static byte[] binary(InputStream stream) throws Exception {
        MessageDigest d = digest(); long length = 0;
        try(InputStream in = stream) {
            byte[] buffer = new byte[16384]; int n;
            while ((n=in.read(buffer)) != -1) { d.update(buffer,0,n); length=Math.addExact(length,n); }
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream(40);
        DataOutputStream out = new DataOutputStream(result); out.writeLong(length); out.write(d.digest());
        return result.toByteArray();
    }
    private static byte[] characters(Reader stream) throws Exception {
        final MessageDigest d = digest(); final long[] length = {0};
        OutputStream bytes = new OutputStream() {
            public void write(int b) { d.update((byte)b); length[0]=Math.addExact(length[0],1); }
            public void write(byte[] b,int offset,int count) { d.update(b,offset,count); length[0]=Math.addExact(length[0],count); }
        };
        // One encoder across chunks preserves surrogate pairs crossing read boundaries.
        try(Reader in=stream; Writer out=new OutputStreamWriter(bytes, UTF8.newEncoder()
                .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
                .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT))) {
            char[] buffer=new char[8192]; int n;
            while((n=in.read(buffer)) != -1) out.write(buffer,0,n);
        }
        ByteArrayOutputStream result=new ByteArrayOutputStream(40);
        DataOutputStream out=new DataOutputStream(result); out.writeLong(length[0]); out.write(d.digest());
        return result.toByteArray();
    }
    private static void value(DataOutputStream out, ResultSet rows,int index,int type) throws Exception {
        String tag; byte[] bytes = null;
        switch(type) {
            case Types.CHAR: case Types.VARCHAR: case Types.LONGVARCHAR:
            case Types.NCHAR: case Types.NVARCHAR: case Types.LONGNVARCHAR:
                tag="TEXT"; String s=rows.getString(index); if(s!=null) bytes=text(s); break;
            case Types.NUMERIC: case Types.DECIMAL: case Types.INTEGER: case Types.SMALLINT:
            case Types.TINYINT: case Types.BIGINT: case Types.FLOAT: case Types.REAL: case Types.DOUBLE:
                tag="NUMBER"; BigDecimal n=rows.getBigDecimal(index);
                if(n!=null) bytes=text(n.signum()==0 ? "0" : n.stripTrailingZeros().toPlainString()); break;
            case Types.DATE: case Types.TIMESTAMP:
                tag=type==Types.DATE ? "DATE" : "TIMESTAMP";
                Calendar utc=Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
                Timestamp t=rows.getTimestamp(index,utc);
                if(t!=null) bytes=text(CLOCK.format(t.toInstant().atOffset(ZoneOffset.UTC))); break;
            case Types.BINARY: case Types.VARBINARY: case Types.LONGVARBINARY: case Types.BLOB:
                tag=type==Types.BLOB ? "BLOB" : "BINARY";
                InputStream bin=rows.getBinaryStream(index); if(bin!=null) bytes=binary(bin); break;
            case Types.CLOB: case Types.NCLOB:
                tag=type==Types.CLOB ? "CLOB" : "NCLOB";
                Reader chars=type==Types.NCLOB ? rows.getNCharacterStream(index) : rows.getCharacterStream(index);
                if(chars!=null) bytes=characters(chars); break;
            default: throw new Failure("UNSUPPORTED_JDBC_TYPE:"+type);
        }
        field(out,bytes==null ? "NULL" : tag,bytes==null ? new byte[0] : bytes);
    }
    private static String snapshot(Connection connection,String owner) throws Exception {
        List<String> tables=new ArrayList<String>();
        try(PreparedStatement inventory=connection.prepareStatement("SELECT table_name FROM all_tables WHERE owner = ?")) {
            inventory.setString(1,owner);
            try(ResultSet rows=inventory.executeQuery()) { while(rows.next()) { String name=rows.getString(1); quote(name); tables.add(name); } }
        }
        if(tables.isEmpty() || new HashSet<String>(tables).size()!=tables.size()) throw new Failure("EMPTY_OR_DUPLICATE_TABLE_METADATA");
        Collections.sort(tables);
        MessageDigest schema=digest(); DataOutputStream schemaOut=sink(schema);
        field(schemaOut,"SCHEMA-v1",new byte[0]);
        StringBuilder report=new StringBuilder("{\"format\":\"oracle-content-sha256-v1\",\"schema\":").append(json(owner))
            .append(",\"transaction_read_only\":true,\"tables\":[");
        boolean first=true;
        for(String table:tables) {
            if(!first) report.append(','); first=false;
            field(schemaOut,"TABLE",text(table));
            report.append("{\"name\":").append(json(table)).append(",\"columns\":[");
            List<String> hashes=new ArrayList<String>();
            try(Statement statement=connection.createStatement()) {
                statement.setFetchSize(128);
                try(ResultSet rows=statement.executeQuery("SELECT * FROM "+quote(owner)+"."+quote(table))) {
                    ResultSetMetaData meta=rows.getMetaData(); int columns=meta.getColumnCount();
                    if(columns<1) throw new Failure("EMPTY_COLUMN_METADATA");
                    int[] types=new int[columns];
                    for(int c=1;c<=columns;c++) {
                        String name=meta.getColumnName(c),nativeType=meta.getColumnTypeName(c); quote(name); quote(nativeType);
                        types[c-1]=meta.getColumnType(c); supported(types[c-1]); coverage.add(types[c-1]);
                        int precision=meta.getPrecision(c),scale=meta.getScale(c),nullable=meta.isNullable(c);
                        String[] values={name,nativeType,Integer.toString(types[c-1]),Integer.toString(precision),Integer.toString(scale),Integer.toString(nullable)};
                        for(int k=0;k<values.length;k++) field(schemaOut,"COLUMN-"+k,text(values[k]));
                        if(c>1) report.append(',');
                        report.append("{\"name\":").append(json(name)).append(",\"native_type\":").append(json(nativeType))
                            .append(",\"jdbc_type\":").append(types[c-1]).append(",\"precision\":").append(precision)
                            .append(",\"scale\":").append(scale).append(",\"nullable\":").append(nullable).append('}');
                    }
                    while(rows.next()) {
                        MessageDigest row=digest(); DataOutputStream out=sink(row); field(out,"ROW-v1",new byte[0]);
                        for(int c=1;c<=columns;c++) value(out,rows,c,types[c-1]);
                        hashes.add(hex(row.digest()));
                    }
                }
            }
            Collections.sort(hashes);
            MessageDigest content=digest(); DataOutputStream tableOut=sink(content);
            field(tableOut,"TABLE-v1",new byte[0]); field(tableOut,"COUNT",text(Integer.toString(hashes.size())));
            for(String hash:hashes) field(tableOut,"ROW",unhex(hash));
            byte[] tableDigest=content.digest();
            field(schemaOut,"COUNT",text(Integer.toString(hashes.size()))); field(schemaOut,"CONTENT",tableDigest);
            report.append("],\"row_count\":").append(hashes.size()).append(",\"content_sha256\":").append(json(hex(tableDigest))).append('}');
        }
        report.append("],\"schema_sha256\":").append(json(hex(schema.digest()))).append(",\"type_coverage\":[");
        first=true; for(Integer type:coverage) { if(!first) report.append(','); first=false; report.append(type); }
        return report.append("]}").toString();
    }
    private static String run(String[] args) throws Exception {
        if(args.length!=0) throw new Failure("STDIN_PROPERTIES_REQUIRED");
        Properties config=new Properties(); config.load(System.in);
        Set<String> required=new HashSet<String>(Arrays.asList("url","user","password","schema"));
        if(!config.stringPropertyNames().equals(required)) throw new Failure("INVALID_CONFIGURATION");
        for(String key:required) if(config.getProperty(key).isEmpty()) throw new Failure("INVALID_CONFIGURATION");
        String owner=config.getProperty("schema"),user=config.getProperty("user");
        if(!owner.equals("LIMS_BASELINE_LOCAL") && !owner.equals("LIMS_RUNTIME_LOCAL")) throw new Failure("SCHEMA_NOT_ALLOWED");
        String login=user.replace("\"", "").trim().toUpperCase(Locale.ROOT);
        if(login.equals("SYS") || login.startsWith("SYS ") || login.contains("SYSDBA") || login.contains("SYSOPER")) throw new Failure("SYS_LOGIN_NOT_ALLOWED");
        if(!config.getProperty("url").startsWith("jdbc:oracle:thin:@")) throw new Failure("ORACLE_THIN_URL_REQUIRED");
        Class.forName("oracle.jdbc.OracleDriver");
        Properties credentials=new Properties(); credentials.setProperty("user",user); credentials.setProperty("password",config.getProperty("password"));
        try(Connection connection=DriverManager.getConnection(config.getProperty("url"),credentials)) {
            connection.setReadOnly(true); connection.setAutoCommit(false);
            try {
                try(Statement transaction=connection.createStatement()) { transaction.execute("SET TRANSACTION READ ONLY"); }
                return snapshot(connection,owner);
            } finally { connection.rollback(); }
        }
    }
    public static void main(String[] args) {
        try { System.out.println(run(args)); }
        catch(Failure e) { System.err.println("fingerprint-failed:"+e.code); System.exit(1); }
        catch(SQLException e) { System.err.println("fingerprint-failed:SQL_ERROR"); System.exit(1); }
        catch(Throwable e) { System.err.println("fingerprint-failed:INPUT_DRIVER_OR_READ_ERROR"); System.exit(1); }
    }
}
