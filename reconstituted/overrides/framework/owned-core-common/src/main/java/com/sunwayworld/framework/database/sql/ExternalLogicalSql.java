package com.sunwayworld.framework.database.sql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/** Apply the packaged external-object profile to database-stored SQL at execution. */
public final class ExternalLogicalSql {
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Z][A-Z0-9_$#]{0,29}");
    private static final String CONTRACT = "META-INF/lims-external-schema.json";
    private static final String PROPERTIES = "META-INF/lims-external-schema.properties";

    private ExternalLogicalSql() { }

    public static String render(String sql) {
        if (sql == null || sql.indexOf('@') < 0) return sql;
        List<Token> tokens = tokenize(sql);
        String[] replacements = new String[tokens.size()];
        Profile profile = null;
        for (int index = 0; index < tokens.size(); index++) {
            Token at = tokens.get(index);
            if (!"@".equals(at.text) || at.kind != Kind.SYMBOL) continue;
            if (profile == null) profile = Profile.load();
            if ("original-db-link".equals(profile.mode)) return sql;
            int tableIndex = significant(tokens, index - 1, -1);
            int linkIndex = significant(tokens, index + 1, 1);
            if (tableIndex < 0 || linkIndex >= tokens.size()
                    || tokens.get(tableIndex).kind != Kind.IDENTIFIER
                    || tokens.get(linkIndex).kind != Kind.IDENTIFIER) throw rejected();
            int previous = significant(tokens, tableIndex - 1, -1);
            int following = significant(tokens, linkIndex + 1, 1);
            if ((previous >= 0 && (".".equals(tokens.get(previous).text)
                    || "@".equals(tokens.get(previous).text)))
                    || (following < tokens.size() && (".".equals(tokens.get(following).text)
                    || "@".equals(tokens.get(following).text)))) throw rejected();
            String table = tokens.get(tableIndex).text;
            String link = tokens.get(linkIndex).text.toUpperCase(Locale.ROOT);
            Object allowed = profile.objects.get(link);
            if (!(allowed instanceof List)
                    || !((List<?>) allowed).contains(table.toUpperCase(Locale.ROOT))) throw rejected();
            Object schema = profile.schemas.get(link);
            if (!(schema instanceof String)) throw rejected();
            replacements[tableIndex] = "direct-schema".equals(profile.mode) ? schema + "." + table : table;
            replacements[index] = "";
            replacements[linkIndex] = "";
        }
        StringBuilder rendered = new StringBuilder(sql.length());
        for (int index = 0; index < tokens.size(); index++) {
            rendered.append(replacements[index] == null ? tokens.get(index).text : replacements[index]);
        }
        return rendered.toString();
    }

    private static IllegalArgumentException rejected() {
        return new IllegalArgumentException("Unapproved external SQL object or qualifier");
    }

    private static int significant(List<Token> tokens, int index, int direction) {
        while (index >= 0 && index < tokens.size() && tokens.get(index).kind == Kind.TRIVIA) index += direction;
        return index;
    }

    private enum Kind { IDENTIFIER, SYMBOL, QUOTED, TRIVIA }

    private static final class Token {
        final Kind kind;
        final String text;
        Token(Kind kind, String text) { this.kind = kind; this.text = text; }
    }

    private static boolean identifierStart(char value) {
        return value >= 'A' && value <= 'Z' || value >= 'a' && value <= 'z' || value == '_';
    }

    private static boolean identifierPart(char value) {
        return identifierStart(value) || value >= '0' && value <= '9' || value == '$' || value == '#';
    }

    private static List<Token> tokenize(String sql) {
        List<Token> result = new ArrayList<>();
        int index = 0;
        while (index < sql.length()) {
            int start = index;
            char value = sql.charAt(index);
            Kind kind;
            if (Character.isWhitespace(value)) {
                kind = Kind.TRIVIA;
                while (index < sql.length() && Character.isWhitespace(sql.charAt(index))) index++;
            } else if (sql.startsWith("--", index)) {
                kind = Kind.TRIVIA;
                while (index < sql.length() && sql.charAt(index) != '\n' && sql.charAt(index) != '\r') index++;
            } else if (sql.startsWith("/*", index)) {
                kind = Kind.TRIVIA;
                int end = sql.indexOf("*/", index + 2);
                if (end < 0) throw rejected();
                index = end + 2;
            } else if ((value == 'q' || value == 'Q') && index + 2 < sql.length()
                    && sql.charAt(index + 1) == '\'') {
                kind = Kind.QUOTED;
                char open = sql.charAt(index + 2);
                char close = open == '[' ? ']' : open == '{' ? '}' : open == '(' ? ')' : open == '<' ? '>' : open;
                int end = sql.indexOf("" + close + '\'', index + 3);
                if (end < 0) throw rejected();
                index = end + 2;
            } else if (value == '\'' || value == '"') {
                kind = Kind.QUOTED;
                index++;
                boolean closed = false;
                while (index < sql.length()) {
                    if (sql.charAt(index++) == value) {
                        if (index < sql.length() && sql.charAt(index) == value) index++;
                        else { closed = true; break; }
                    }
                }
                if (!closed) throw rejected();
            } else if (identifierStart(value)) {
                kind = Kind.IDENTIFIER;
                index++;
                while (index < sql.length() && identifierPart(sql.charAt(index))) index++;
            } else {
                kind = Kind.SYMBOL;
                index++;
            }
            result.add(new Token(kind, sql.substring(start, index)));
        }
        return result;
    }

    private static final class Profile {
        final String mode;
        final Map<?, ?> schemas, objects;
        Profile(String mode, Map<?, ?> schemas, Map<?, ?> objects) {
            this.mode = mode; this.schemas = schemas; this.objects = objects;
        }

        static Profile load() {
            ClassLoader loader = ExternalLogicalSql.class.getClassLoader();
            try {
                byte[] raw = read(loader, CONTRACT);
                Properties properties = new Properties();
                try (InputStream stream = loader.getResourceAsStream(PROPERTIES)) {
                    if (stream == null) throw new IllegalArgumentException("External SQL contract properties missing");
                    properties.load(stream);
                }
                if (!hex(MessageDigest.getInstance("SHA-256").digest(raw))
                        .equals(properties.getProperty("contract_sha256"))) {
                    throw new IllegalArgumentException("External SQL contract hash differs");
                }
                JSONObject contract = JSON.parseObject(new String(raw, StandardCharsets.UTF_8));
                String mode = contract.getString("mode");
                if (!"external-schema-build-contract-v1".equals(contract.getString("schema"))
                        || !("local-view".equals(mode) || "direct-schema".equals(mode)
                            || "original-db-link".equals(mode))
                        || !mode.equals(properties.getProperty("mode"))) throw rejected();
                Object dynamic = contract.get("dynamic_sql");
                if ("original-db-link".equals(mode)) {
                    if (!contract.containsKey("dynamic_sql") || dynamic != null) throw rejected();
                    return new Profile(mode, null, null);
                }
                if (!(dynamic instanceof Map)) throw rejected();
                Map<?, ?> configuration = (Map<?, ?>) dynamic;
                if (!configuration.keySet().equals(new HashSet<>(Arrays.asList("qualifier_schemas", "objects_by_qualifier")))) throw rejected();
                Object schemasValue = configuration.get("qualifier_schemas");
                Object objectsValue = configuration.get("objects_by_qualifier");
                if (!(schemasValue instanceof Map) || !(objectsValue instanceof Map)) throw rejected();
                Map<?, ?> schemas = (Map<?, ?>) schemasValue, objects = (Map<?, ?>) objectsValue;
                if (schemas.isEmpty() || !schemas.keySet().equals(objects.keySet())) throw rejected();
                for (Object qualifier : schemas.keySet()) {
                    if (!identifier(qualifier) || !identifier(schemas.get(qualifier))
                            || !(objects.get(qualifier) instanceof List)) throw rejected();
                    List<?> names = (List<?>) objects.get(qualifier);
                    if (names.isEmpty() || new HashSet<>(names).size() != names.size()) throw rejected();
                    for (Object name : names) if (!identifier(name)) throw rejected();
                }
                return new Profile(mode, schemas, objects);
            } catch (IOException | NoSuchAlgorithmException failure) {
                throw new IllegalArgumentException("External SQL contract unavailable", failure);
            }
        }

        private static boolean identifier(Object value) {
            return value instanceof String && IDENTIFIER.matcher((String) value).matches();
        }

        private static byte[] read(ClassLoader loader, String resource) throws IOException {
            try (InputStream stream = loader.getResourceAsStream(resource)) {
                if (stream == null) throw new IllegalArgumentException("External SQL contract missing");
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                for (int count; (count = stream.read(buffer)) >= 0;) result.write(buffer, 0, count);
                return result.toByteArray();
            }
        }

        private static String hex(byte[] value) {
            StringBuilder result = new StringBuilder();
            for (byte element : value) result.append(String.format(Locale.ROOT, "%02x", element & 255));
            return result.toString();
        }
    }
}
