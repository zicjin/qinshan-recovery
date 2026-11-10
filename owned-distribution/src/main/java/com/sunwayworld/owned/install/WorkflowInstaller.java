package com.sunwayworld.owned.install;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/** Finite first-installation technical bootstrap, never an application endpoint. */
public final class WorkflowInstaller {
    private static final int PACKAGE_LIMIT = 16 * 1024 * 1024;
    private static final Pattern TOKEN = Pattern.compile("__LIMS_((?:ROLE|USER|ORG)_[0-9]{3})_(ID|NAME)__");
    private static final String BPMN = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    private static final String SVG = "http://www.w3.org/2000/svg";
    private static final Set<String> NAMESPACES = set(BPMN, SVG, "http://camunda.org/schema/1.0/bpmn",
        "http://www.omg.org/spec/BPMN/20100524/DI", "http://www.omg.org/spec/DD/20100524/DC",
        "http://www.omg.org/spec/DD/20100524/DI", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
        XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "http://www.w3.org/1999/xlink");
    private static final Set<String> DIAGRAM_TAGS = set("definitions", "process", "startEvent", "endEvent",
        "userTask", "incoming", "outgoing", "sequenceFlow", "exclusiveGateway", "parallelGateway",
        "candidateRolesElements", "candidateUsersElements", "passNotifiedRolesElements", "passNotifiedUsersElements",
        "rejectNotifiedRolesElements", "rejectNotifiedUsersElements", "properties", "property", "conditionExpression",
        "BPMNDiagram", "BPMNPlane", "BPMNShape", "Bounds", "BPMNLabel", "BPMNEdge", "waypoint");
    private static final Set<String> DIAGRAM_ATTRS = set("id", "targetNamespace", "schemaLocation", "isExecutable",
        "statusCode", "sourceRef", "targetRef", "bpmnElement", "x", "y", "width", "height", "name", "value",
        "type", "isMarkerVisible", "transfer", "filterPreviousCandidators", "commentRequired", "authRequired",
        "attachmentStrategy", "counterSignStrategy", "nextCandidatorOptStrategy", "candidatorFilterStrategy", "autoPass");
    private static final Set<String> SVG_TAGS = set("svg", "defs", "marker", "path", "g", "polyline", "rect", "circle", "text", "tspan", "polygon", "line", "ellipse", "title", "desc");
    private static final Set<String> SVG_ATTRS = set("width", "height", "viewBox", "version", "id", "refX", "refY",
        "markerWidth", "markerHeight", "orient", "d", "style", "class", "data-element-id", "points", "x", "y",
        "transform", "cx", "cy", "r", "lineHeight", "rx", "ry", "fill", "stroke", "stroke-width", "font-size",
        "font-family", "font-weight", "text-anchor", "opacity", "stroke-opacity", "fill-opacity", "marker-end");
    private static final String[] EMPTY_TABLES = {"T_CORE_BPMN_DIAGRAM", "T_CORE_BPMN_PROC", "T_CORE_BPMN_PROC_ORG",
        "T_CORE_BPMN_DRAFT", "T_CORE_BPMN_DRAFT_ORG", "T_CORE_BPMN_TARGET", "T_CORE_BPMN_INST", "T_CORE_BPMN_INST_TASK",
        "T_CORE_BPMN_INST_TASK_USER", "T_CORE_BPMN_INST_TASK_ROLE", "T_CORE_BPMN_INST_TASK_HIS", "T_CORE_BPMN_COMMENT"};
    private WorkflowInstaller() {}
    private static Set<String> set(String... values) { return new HashSet<>(Arrays.asList(values)); }
    private static final class Rejected extends RuntimeException { Rejected(String code) { super(code); } }
    private static void require(boolean value, String code) { if (!value) throw new Rejected(code); }

    public static final class Definition {
        public final String procCode, serviceId, tableName, diagram, svg;
        public final long version;
        public final List<String> organizations;
        private Definition(Map<String,Object> value) {
            keys(value, "proc_code", "proc_version", "service_id", "table_name", "expression", "diagram", "svg", "organization_symbols");
            procCode = text(value.get("proc_code"), 32, "INVALID_DEFINITION");
            require(procCode.matches("[A-Za-z][A-Za-z0-9_.-]{0,31}"), "INVALID_DEFINITION");
            version = integer(value.get("proc_version"), 1, Long.MAX_VALUE, "INVALID_DEFINITION");
            serviceId = identifier(value.get("service_id"), 32, "INVALID_DEFINITION");
            tableName = text(value.get("table_name"), 64, "INVALID_DEFINITION");
            require(tableName.matches("T_[A-Z][A-Z0-9_]{0,61}") && value.get("expression") == null, "INVALID_DEFINITION");
            diagram = text(value.get("diagram"), PACKAGE_LIMIT, "INVALID_XML");
            svg = text(value.get("svg"), PACKAGE_LIMIT, "INVALID_XML");
            organizations = Collections.unmodifiableList(symbolList(value.get("organization_symbols"), "ORG"));
        }
    }
    public static final class Prepared {
        public final List<Definition> definitions;
        public final int blockedCount;
        private final String schema, installer;
        private final Map<String,Map<String,String>> mapping;
        private Prepared(List<Definition> definitions, int blockedCount, String schema, String installer, Map<String,Map<String,String>> mapping) {
            this.definitions = Collections.unmodifiableList(definitions); this.blockedCount = blockedCount;
            this.schema = schema; this.installer = installer; this.mapping = mapping;
        }
        /** Resolve only the declared placeholders; names are literal DOM data. */
        public String materializeXml(String procCode, boolean svg, Map<String,String> targetDisplayNames) throws Exception {
            Set<String> required = new HashSet<>();
            for (Map<String,String> category : mapping.values()) required.addAll(category.keySet());
            require(targetDisplayNames != null && targetDisplayNames.keySet().equals(required), "TARGET_NAMES_INCOMPLETE");
            for (String name : targetDisplayNames.values()) {
                text(name, 512, "INVALID_TARGET_IDENTITY_NAME");
                require(!name.contains("__LIMS"), "INVALID_TARGET_IDENTITY_NAME");
            }
            for (Definition definition : definitions) if (definition.procCode.equals(procCode))
                return resolveXml(svg ? definition.svg : definition.diagram, this, targetDisplayNames);
            throw new Rejected("UNKNOWN_DEFINITION");
        }
    }
    @SuppressWarnings("unchecked") private static Map<String,Object> object(Object value) {
        require(value instanceof Map, "INVALID_JSON_STRUCTURE"); return (Map<String,Object>) value;
    }
    @SuppressWarnings("unchecked") private static List<Object> array(Object value) {
        require(value instanceof List, "INVALID_JSON_STRUCTURE"); return (List<Object>) value;
    }
    private static void keys(Map<String,Object> value, String... names) { require(value.keySet().equals(set(names)), "INVALID_FIELDS"); }
    private static String text(Object value, int maxBytes, String code) {
        require(value instanceof String, code); String result = (String) value;
        require(!result.trim().isEmpty() && result.getBytes(StandardCharsets.UTF_8).length <= maxBytes, code);
        require(result.chars().noneMatch(c -> c == 0 || (c < 32 && c != 9 && c != 10 && c != 13)), code); return result;
    }
    private static String identifier(Object value, int bytes, String code) {
        String result = text(value, bytes, code); require(result.matches("[A-Za-z0-9][A-Za-z0-9_.-]*"), code); return result;
    }
    private static long integer(Object value, long min, long max, String code) {
        require(value instanceof Long, code); long result = (Long)value; require(result >= min && result <= max, code); return result;
    }
    private static List<String> symbolList(Object value, String prefix) {
        List<String> result = new ArrayList<>();
        for (Object entry : array(value)) {
            String symbol = text(entry, 8, "INVALID_SYMBOL");
            require(symbol.matches(prefix + "_[0-9]{3}") && !symbol.endsWith("_000") && !result.contains(symbol), "INVALID_SYMBOL");
            result.add(symbol);
        }
        return result;
    }
    private static String utf8(byte[] bytes) throws Exception {
        return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString();
    }
    private static String hash(byte[] value) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(value); StringBuilder out = new StringBuilder();
        for (byte b : digest) out.append(String.format("%02x", b & 255)); return out.toString();
    }
    private static byte[] read(Path path, int limit) throws Exception {
        require(!Files.isSymbolicLink(path) && Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS), "INVALID_PACKAGE_PATH");
        require(Files.size(path) <= limit, "INPUT_TOO_LARGE"); try (InputStream stream = Files.newInputStream(path)) { return read(stream, limit); }
    }
    private static byte[] read(InputStream stream, int limit) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream(); byte[] buffer = new byte[4096]; int n;
        while ((n = stream.read(buffer)) != -1) { require(out.size() + n <= limit, "INPUT_TOO_LARGE"); out.write(buffer, 0, n); }
        return out.toByteArray();
    }

    /** Pure preflight: no JDBC, Redis, filesystem writes or application startup. */
    public static Prepared prepare(byte[] privateJson, Path manifestPath) throws Exception {
        require(privateJson.length <= 65536, "INPUT_TOO_LARGE");
        Map<String,Object> request = object(new Json(utf8(privateJson)).parse());
        keys(request, "expected_schema", "installer_user_id", "application_stopped", "redis_namespace_unused", "mapping");
        String schema = text(request.get("expected_schema"), 128, "INVALID_TARGET_SCHEMA");
        require(schema.matches("[A-Z][A-Z0-9_]{0,127}") && !set("SYS", "SYSTEM").contains(schema), "INVALID_TARGET_SCHEMA");
        String installer = identifier(request.get("installer_user_id"), 32, "INVALID_INSTALLER");
        require(Boolean.TRUE.equals(request.get("application_stopped")) && Boolean.TRUE.equals(request.get("redis_namespace_unused")), "OFFLINE_INSTALLATION_REQUIRED");
        Path manifest = manifestPath.toAbsolutePath().normalize();
        require(!Files.isSymbolicLink(manifest) && manifest.getParent().equals(manifest.getParent().toRealPath()), "INVALID_PACKAGE_PATH");
        Map<String,Object> packageInfo = object(new Json(utf8(read(manifest, 1048576))).parse());
        require("owned-workflow-package-v1".equals(packageInfo.get("schema")) && "partial".equals(packageInfo.get("status"))
            && Boolean.FALSE.equals(packageInfo.get("deployment_ready")), "INVALID_PACKAGE_MANIFEST");
        Map<String,Object> entry = object(packageInfo.get("definitions")); keys(entry, "path", "sha256", "bytes");
        require("definitions.json".equals(entry.get("path")), "INVALID_PACKAGE_PATH");
        String expectedHash = text(entry.get("sha256"), 64, "INVALID_PACKAGE_HASH");
        require(expectedHash.matches("[0-9a-f]{64}"), "INVALID_PACKAGE_HASH");
        byte[] payload = read(manifest.getParent().resolve("definitions.json"), PACKAGE_LIMIT);
        require(integer(entry.get("bytes"), 1, PACKAGE_LIMIT, "INVALID_PACKAGE_BYTES") == payload.length && expectedHash.equals(hash(payload)), "PACKAGE_INTEGRITY_REJECTED");
        Map<String,Object> content = object(new Json(utf8(payload)).parse()); keys(content, "schema", "definitions", "symbols", "blocked");
        require("owned-workflow-definitions-v1".equals(content.get("schema")), "INVALID_DEFINITIONS_SCHEMA");
        Map<String,Object> registry = object(content.get("symbols")); keys(registry, "roles", "users", "organizations");
        Map<String,List<String>> symbols = new LinkedHashMap<>();
        symbols.put("roles", symbolList(registry.get("roles"), "ROLE")); symbols.put("users", symbolList(registry.get("users"), "USER"));
        symbols.put("organizations", symbolList(registry.get("organizations"), "ORG"));
        Map<String,Object> inputMapping = object(request.get("mapping")); keys(inputMapping, "roles", "users", "organizations");
        Map<String,Map<String,String>> mapping = new LinkedHashMap<>(); Set<String> declared = new HashSet<>();
        for (String category : symbols.keySet()) {
            Map<String,Object> supplied = object(inputMapping.get(category));
            require(supplied.keySet().equals(new HashSet<>(symbols.get(category))), "MAPPING_INCOMPLETE");
            Map<String,String> values = new LinkedHashMap<>(); Set<String> targets = new HashSet<>();
            for (String symbol : symbols.get(category)) {
                String target = identifier(supplied.get(symbol), category.equals("organizations") ? 16 : 32, "INVALID_MAPPING_TARGET");
                if (category.equals("roles")) {
                    require(target.matches("[1-9][0-9]{0,18}"), "INVALID_MAPPING_TARGET");
                    try { require(Long.parseLong(target) > 0, "INVALID_MAPPING_TARGET"); } catch (NumberFormatException bad) { throw new Rejected("INVALID_MAPPING_TARGET"); }
                }
                require(targets.add(target), "MAPPING_COLLAPSE_REJECTED"); values.put(symbol, target); declared.add(symbol);
            }
            mapping.put(category, values);
        }
        List<Definition> definitions = new ArrayList<>(); Set<String> codes = new HashSet<>(), services = new HashSet<>(), usedIds = new HashSet<>(), usedNames = new HashSet<>();
        for (Object raw : array(content.get("definitions"))) {
            Definition definition = new Definition(object(raw));
            require(codes.add(definition.procCode) && services.add(definition.serviceId), "DUPLICATE_DEFINITION");
            for (String symbol : definition.organizations) { require(symbols.get("organizations").contains(symbol), "UNKNOWN_SYMBOL"); usedIds.add(symbol); }
            validateXml(definition.diagram, false, declared, usedIds, usedNames);
            validateXml(definition.svg, true, declared, usedIds, usedNames); definitions.add(definition);
        }
        require(!definitions.isEmpty(), "NO_INSTALLABLE_DEFINITIONS");
        List<Object> blocked = array(content.get("blocked"));
        for (Object raw : blocked) {
            Map<String,Object> block = object(raw); keys(block, "proc_code", "proc_version", "service_id", "table_name", "reason");
            String code = identifier(block.get("proc_code"), 32, "INVALID_BLOCKED_DEFINITION"); require(codes.add(code), "DUPLICATE_DEFINITION");
            integer(block.get("proc_version"), 1, Long.MAX_VALUE, "INVALID_BLOCKED_DEFINITION");
            identifier(block.get("service_id"), 32, "INVALID_BLOCKED_DEFINITION");
            require(text(block.get("table_name"), 64, "INVALID_BLOCKED_DEFINITION").matches("T_[A-Z][A-Z0-9_]{0,61}"), "INVALID_BLOCKED_DEFINITION");
            text(block.get("reason"), 512, "INVALID_BLOCKED_DEFINITION");
        }
        require(usedIds.equals(declared), "UNUSED_OR_MISSING_SYMBOL");
        Set<String> namedIdentities = new HashSet<>(symbols.get("roles")); namedIdentities.addAll(symbols.get("users"));
        require(usedNames.containsAll(namedIdentities), "MISSING_IDENTITY_LABEL");
        Map<String,Object> counts = object(packageInfo.get("counts")); keys(counts, "definitions", "blocked", "roles", "users", "organizations");
        require(integer(counts.get("definitions"), 0, 10000, "INVALID_COUNTS") == definitions.size()
            && integer(counts.get("blocked"), 0, 10000, "INVALID_COUNTS") == blocked.size(), "COUNT_MISMATCH");
        for (String category : symbols.keySet()) require(integer(counts.get(category), 0, 999, "INVALID_COUNTS") == symbols.get(category).size(), "COUNT_MISMATCH");
        return new Prepared(definitions, blocked.size(), schema, installer, mapping);
    }

    private static String local(Node node) { return node.getLocalName() == null ? node.getNodeName() : node.getLocalName(); }
    private static Document parseXml(String xml) throws Exception {
        require(!Pattern.compile("<!DOCTYPE|<!ENTITY|<\\?(?!xml(?:\\s|\\?))", Pattern.CASE_INSENSITIVE).matcher(xml).find(), "UNSAFE_XML");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(true); factory.setXIncludeAware(false); factory.setExpandEntityReferences(false);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false); factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder(); builder.setErrorHandler(new DefaultHandler());
        builder.setEntityResolver((publicId, systemId) -> { throw new org.xml.sax.SAXException("EXTERNAL_ENTITY_REFUSED"); });
        return builder.parse(new InputSource(new StringReader(xml)));
    }
    private static String categoryForGroup(String tag) {
        if (set("candidateRolesElements", "passNotifiedRolesElements", "rejectNotifiedRolesElements").contains(tag)) return "ROLE";
        if (set("candidateUsersElements", "passNotifiedUsersElements", "rejectNotifiedUsersElements").contains(tag)) return "USER";
        return null;
    }
    private static String propertyCategory(Element element) {
        if (!"property".equals(local(element))) return null;
        Node properties = element.getParentNode(), group = properties == null ? null : properties.getParentNode();
        return properties != null && "properties".equals(local(properties)) && group != null ? categoryForGroup(local(group)) : null;
    }
    private static void tokens(String value, String expectedKind, String expectedPart, Set<String> declared, Set<String> usedIds, Set<String> usedNames) {
        Matcher matcher = TOKEN.matcher(value); int count = 0;
        while (matcher.find()) {
            String symbol = matcher.group(1), part = matcher.group(2); require(declared.contains(symbol), "UNKNOWN_SYMBOL");
            require((expectedKind == null || symbol.startsWith(expectedKind + "_")) && part.equals(expectedPart), "INVALID_TOKEN_POSITION");
            (part.equals("ID") ? usedIds : usedNames).add(symbol); count++;
        }
        require(!TOKEN.matcher(value).replaceAll("").contains("__LIMS"), "UNKNOWN_TOKEN");
        if (expectedPart.equals("ID")) require(count == 1 && TOKEN.matcher(value).matches(), "MISSING_IDENTITY_TOKEN");
    }
    private static void validateNonElementNodes(Node node) {
        require(node.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE && node.getNodeType() != Node.ENTITY_REFERENCE_NODE, "UNSAFE_XML");
        if (node.getNodeType() == Node.COMMENT_NODE) require(!node.getNodeValue().contains("__LIMS"), "INVALID_TOKEN_POSITION");
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) validateNonElementNodes(children.item(i));
    }
    private static void validateXml(String xml, boolean svg, Set<String> declared, Set<String> usedIds, Set<String> usedNames) throws Exception {
        Matcher allTokens = TOKEN.matcher(xml);
        while (allTokens.find()) require(declared.contains(allTokens.group(1)), "UNKNOWN_SYMBOL");
        require(!TOKEN.matcher(xml).replaceAll("").contains("__LIMS"), "UNKNOWN_TOKEN");
        Document doc = parseXml(xml); validateNonElementNodes(doc); Element root = doc.getDocumentElement();
        require((svg ? "svg" : "definitions").equals(local(root)) && (svg ? SVG : BPMN).equals(root.getNamespaceURI()), "INVALID_XML_ROOT");
        int starts = 0, ends = 0, processes = 0; Set<String> nodeIds = new HashSet<>(), statuses = new HashSet<>();
        NodeList elements = doc.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element)elements.item(i); String tag = local(element), ns = element.getNamespaceURI();
            require(NAMESPACES.contains(ns) && (svg ? SVG_TAGS : DIAGRAM_TAGS).contains(tag), "UNSAFE_XML_ELEMENT");
            if (svg) require(SVG.equals(ns), "UNSAFE_XML_ELEMENT");
            if (tag.equals("process")) processes++; if (tag.equals("startEvent")) starts++; if (tag.equals("endEvent")) ends++;
            if (!svg && element.hasAttribute("id")) require(nodeIds.add(element.getAttribute("id")), "DUPLICATE_XML_ID");
            if (!svg && set("startEvent", "endEvent", "userTask").contains(tag)) {
                String status = element.getAttribute("statusCode"); require(!status.isEmpty() && statuses.add(status), "INVALID_STATUS_CODE");
            }
            String category = propertyCategory(element);
            if (!svg && tag.equals("property")) require(category != null && element.getAttributes().getLength() == 2
                && element.hasAttribute("name") && element.hasAttribute("value"), "INVALID_CANDIDATE_PROPERTY");
            NamedNodeMap attributes = element.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attribute = (Attr)attributes.item(j); String key = local(attribute), value = attribute.getValue();
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attribute.getNamespaceURI())) { require(NAMESPACES.contains(value), "UNSAFE_XML_NAMESPACE"); continue; }
                require((svg ? SVG_ATTRS : DIAGRAM_ATTRS).contains(key), "UNSAFE_XML_ATTRIBUTE");
                if (attribute.getNamespaceURI() != null) require(NAMESPACES.contains(attribute.getNamespaceURI()), "UNSAFE_XML_NAMESPACE");
                if (!svg && category != null && key.equals("value")) tokens(value, category, "ID", declared, usedIds, usedNames);
                else if (!svg && category != null && key.equals("name")) {
                    require(TOKEN.matcher(value).matches(), "MISSING_IDENTITY_LABEL"); tokens(value, category, "NAME", declared, usedIds, usedNames);
                    require(value.replace("_NAME__", "_ID__").equals(element.getAttribute("value")), "IDENTITY_LABEL_MISMATCH");
                } else require(!value.contains("__LIMS"), "INVALID_TOKEN_POSITION");
                if (svg) {
                    String safe = value.replaceAll("url\\(\\s*['\"]?#[A-Za-z0-9_.:-]+['\"]?\\s*\\)", "");
                    require(!Pattern.compile("url\\s*\\(|expression\\s*\\(|javascript:|https?:|@import|data:", Pattern.CASE_INSENSITIVE).matcher(safe).find(), "UNSAFE_SVG_STYLE");
                }
            }
            NodeList children = element.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                require(child.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE && child.getNodeType() != Node.ENTITY_REFERENCE_NODE, "UNSAFE_XML");
                if (child.getNodeType() == Node.COMMENT_NODE) require(!child.getNodeValue().contains("__LIMS"), "INVALID_TOKEN_POSITION");
                if (child.getNodeType() != Node.TEXT_NODE && child.getNodeType() != Node.CDATA_SECTION_NODE) continue;
                String value = child.getNodeValue();
                if (value.contains("__LIMS")) {
                    require(svg && set("text", "tspan", "title", "desc").contains(tag), "INVALID_TOKEN_POSITION");
                    tokens(value, null, "NAME", declared, usedIds, usedNames);
                }
                if (!svg && tag.equals("conditionExpression")) require(value.trim().matches("\\$\\{[A-Za-z][A-Za-z0-9_]*\\}\\s*(?:===|!==)\\s*(?:null|'[A-Za-z0-9_. -]*'|\"[A-Za-z0-9_. -]*\")"), "UNSAFE_CONDITION");
            }
        }
        require(svg || (starts == 1 && ends == 1 && processes == 1), "INVALID_PROCESS_SHAPE");
    }

    private static String replacement(String symbol, String part, Map<String,Map<String,String>> mapping, Map<String,String> names) {
        if (part.equals("NAME")) { require(names.containsKey(symbol), "TARGET_IDENTITY_MISSING"); return names.get(symbol); }
        String category = symbol.startsWith("ROLE_") ? "roles" : symbol.startsWith("USER_") ? "users" : "organizations";
        String value = mapping.get(category).get(symbol); require(value != null, "MAPPING_INCOMPLETE"); return value;
    }
    private static String replaceTokens(String value, Prepared prepared, Map<String,String> names) {
        Matcher matcher = TOKEN.matcher(value); StringBuffer out = new StringBuffer();
        while (matcher.find()) matcher.appendReplacement(out, Matcher.quoteReplacement(replacement(matcher.group(1), matcher.group(2), prepared.mapping, names)));
        matcher.appendTail(out); require(!out.toString().contains("__LIMS"), "UNKNOWN_TOKEN"); return out.toString();
    }
    private static String resolveXml(String source, Prepared prepared, Map<String,String> names) throws Exception {
        Document doc = parseXml(source); NodeList elements = doc.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element)elements.item(i); NamedNodeMap attrs = element.getAttributes();
            for (int j = 0; j < attrs.getLength(); j++) {
                Attr attr = (Attr)attrs.item(j); if (attr.getValue().contains("__LIMS")) attr.setValue(replaceTokens(attr.getValue(), prepared, names));
            }
            NodeList children = element.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if ((child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) && child.getNodeValue().contains("__LIMS")) child.setNodeValue(replaceTokens(child.getNodeValue(), prepared, names));
            }
        }
        TransformerFactory factory = TransformerFactory.newInstance(); factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        javax.xml.transform.Transformer transformer = factory.newTransformer(); transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter out = new StringWriter(); transformer.transform(new DOMSource(doc), new StreamResult(out));
        String result = out.toString(); require(!result.contains("__LIMS"), "UNKNOWN_TOKEN"); parseXml(result); return result;
    }
    private static PreparedStatement statement(Connection connection, String sql, Object... values) throws SQLException {
        PreparedStatement result = connection.prepareStatement(sql); for (int i = 0; i < values.length; i++) result.setObject(i + 1, values[i]); return result;
    }
    private static long scalar(Connection connection, String sql, Object... values) throws SQLException {
        try (PreparedStatement s = statement(connection, sql, values); ResultSet rows = s.executeQuery()) { require(rows.next(), "TARGET_CHECK_FAILED"); long result = rows.getLong(1); require(!rows.next(), "TARGET_CHECK_FAILED"); return result; }
    }
    private static String identityName(Connection c, String table, String nameColumn, String key, boolean user) throws SQLException {
        String sql = "SELECT " + nameColumn + " FROM " + table + " WHERE ID=?";
        if (user) sql += " AND STATUS='activated' AND LASTSUSPENDEDFLAG='0'";
        else if (table.equals("T_CORE_ROLE")) sql += " AND LASTSUSPENDEDFLAG='0'";
        try (PreparedStatement s = statement(c, sql, key); ResultSet rows = s.executeQuery()) {
            require(rows.next(), "TARGET_IDENTITY_MISSING_OR_INACTIVE"); String name = rows.getString(1); require(!rows.next(), "TARGET_IDENTITY_NOT_UNIQUE");
            return text(name, 512, "INVALID_TARGET_IDENTITY_NAME");
        }
    }
    private static void insert(Connection connection, String sql, Object... values) throws SQLException {
        try (PreparedStatement s = statement(connection, sql, values)) { require(s.executeUpdate() == 1, "INSTALL_WRITE_FAILED"); }
    }
    private static final class Resolved {
        final Definition source; final long diagramId, procId; final String diagram, svg;
        Resolved(Definition source, long diagramId, long procId, String diagram, String svg) { this.source = source; this.diagramId = diagramId; this.procId = procId; this.diagram = diagram; this.svg = svg; }
    }
    private static int install(Properties properties, Prepared prepared) throws Exception {
        String url = properties.getProperty("spring.datasource.url"), user = properties.getProperty("spring.datasource.username"), password = properties.getProperty("spring.datasource.password");
        require(url != null && url.startsWith("jdbc:oracle:thin:@") && prepared.schema.equals(user) && password != null && !password.isEmpty(), "INVALID_RUNTIME_CONFIG");
        Class.forName("oracle.jdbc.OracleDriver");
        try (Connection c = DriverManager.getConnection(url, user, password)) {
            c.setAutoCommit(false);
            try {
                try (Statement s = c.createStatement(); ResultSet rows = s.executeQuery("SELECT USER, SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM DUAL")) {
                    require(rows.next() && prepared.schema.equals(rows.getString(1)) && prepared.schema.equals(rows.getString(2)), "TARGET_SCHEMA_MISMATCH");
                }
                try (Statement s = c.createStatement()) {
                    for (String table : EMPTY_TABLES) s.execute("LOCK TABLE " + table + " IN EXCLUSIVE MODE NOWAIT");
                    for (String table : new String[]{"T_CORE_MENU_EXT", "T_CORE_ORG", "T_CORE_USER", "T_CORE_ROLE", "T_CORE_ROLE_USER"}) s.execute("LOCK TABLE " + table + " IN SHARE MODE NOWAIT");
                }
                for (String table : EMPTY_TABLES) require(scalar(c, "SELECT COUNT(*) FROM " + table) == 0, "EXISTING_WORKFLOW_STATE_REFUSED");
                String authorName = text(identityName(c, "T_CORE_USER", "USERNAME", prepared.installer, true), 64, "INSTALLER_NAME_TOO_LONG"), authorOrg;
                try (PreparedStatement s = statement(c, "SELECT ORGID FROM T_CORE_USER WHERE ID=?", prepared.installer); ResultSet rows = s.executeQuery()) {
                    require(rows.next(), "INSTALLER_MISSING"); authorOrg = identifier(rows.getString(1), 16, "INSTALLER_ORG_MISSING");
                }
                String authorOrgName = text(identityName(c, "T_CORE_ORG", "ORGNAME", authorOrg, false), 270, "INSTALLER_ORG_NAME_TOO_LONG");
                Map<String,String> names = new HashMap<>();
                for (String category : prepared.mapping.keySet()) for (Map.Entry<String,String> entry : prepared.mapping.get(category).entrySet()) {
                    String target = entry.getValue(); String table = category.equals("roles") ? "T_CORE_ROLE" : category.equals("users") ? "T_CORE_USER" : "T_CORE_ORG";
                    String column = category.equals("roles") ? "ROLENAME" : category.equals("users") ? "USERNAME" : "ORGNAME";
                    names.put(entry.getKey(), identityName(c, table, column, target, category.equals("users")));
                    if (category.equals("roles")) require(scalar(c, "SELECT COUNT(*) FROM T_CORE_ROLE_USER RU JOIN T_CORE_USER U ON U.ID=RU.USERID WHERE RU.ROLEID=? AND U.STATUS='activated' AND U.LASTSUSPENDEDFLAG='0'", target) > 0, "ROLE_HAS_NO_ACTIVE_MEMBER");
                    if (category.equals("users")) require(scalar(c, "SELECT COUNT(*) FROM T_CORE_USER U JOIN T_CORE_ORG O ON O.ID=U.ORGID WHERE U.ID=?", target) == 1, "TARGET_USER_ORG_MISSING");
                }
                List<Resolved> resolved = new ArrayList<>(); int n = prepared.definitions.size(), orgCount = 0;
                for (int i = 0; i < n; i++) {
                    Definition d = prepared.definitions.get(i);
                    require(scalar(c, "SELECT COUNT(*) FROM T_CORE_MENU_EXT WHERE ID=? AND TYPE='audit_def' AND ENABLE='1' AND TABLENAME=? AND SERVICENAME IS NOT NULL", d.serviceId, d.tableName) == 1, "SERVICE_DEFINITION_MISMATCH");
                    resolved.add(new Resolved(d, i + 1L, n + i + 1L, prepared.materializeXml(d.procCode, false, names), prepared.materializeXml(d.procCode, true, names)));
                    orgCount += d.organizations.size();
                }
                Timestamp now = new Timestamp(System.currentTimeMillis()); long orgId = 2L * n;
                for (Resolved r : resolved) {
                    try (PreparedStatement s = c.prepareStatement("INSERT INTO T_CORE_BPMN_DIAGRAM (ID,DIAGRAM,SVG,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?)")) {
                        s.setLong(1, r.diagramId); s.setCharacterStream(2, new StringReader(r.diagram), r.diagram.length()); s.setCharacterStream(3, new StringReader(r.svg), r.svg.length());
                        s.setString(4, prepared.installer); s.setString(5, authorName); s.setTimestamp(6, now); s.setString(7, authorOrg); s.setString(8, authorOrgName);
                        require(s.executeUpdate() == 1, "INSTALL_WRITE_FAILED");
                    }
                    insert(c, "INSERT INTO T_CORE_BPMN_PROC (ID,SERVICEID,PROCCODE,PROCVERSION,DIAGRAMID,EXPRESSION,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                        r.procId, r.source.serviceId, r.source.procCode, r.source.version, r.diagramId, null, prepared.installer, authorName, now, authorOrg, authorOrgName);
                    for (String symbol : r.source.organizations) insert(c, "INSERT INTO T_CORE_BPMN_PROC_ORG (ID,PROCID,USEDORGID,CREATEDBYID,CREATEDBYNAME,CREATEDTIME,CREATEDBYORGID,CREATEDBYORGNAME) VALUES (?,?,?,?,?,?,?,?)",
                        ++orgId, r.procId, prepared.mapping.get("organizations").get(symbol), prepared.installer, authorName, now, authorOrg, authorOrgName);
                }
                require(scalar(c, "SELECT COUNT(*) FROM T_CORE_BPMN_DIAGRAM") == n && scalar(c, "SELECT COUNT(*) FROM T_CORE_BPMN_PROC") == n && scalar(c, "SELECT COUNT(*) FROM T_CORE_BPMN_PROC_ORG") == orgCount, "INSTALL_READBACK_MISMATCH");
                for (Resolved r : resolved) {
                    try (PreparedStatement s = statement(c, "SELECT DIAGRAM,SVG FROM T_CORE_BPMN_DIAGRAM WHERE ID=?", r.diagramId); ResultSet rows = s.executeQuery()) {
                        require(rows.next() && hash(r.diagram.getBytes(StandardCharsets.UTF_8)).equals(hash(rows.getString(1).getBytes(StandardCharsets.UTF_8)))
                            && hash(r.svg.getBytes(StandardCharsets.UTF_8)).equals(hash(rows.getString(2).getBytes(StandardCharsets.UTF_8))) && !rows.next(), "INSTALL_READBACK_MISMATCH");
                    }
                    require(scalar(c, "SELECT COUNT(*) FROM T_CORE_BPMN_PROC WHERE ID=? AND SERVICEID=? AND PROCCODE=? AND PROCVERSION=? AND DIAGRAMID=? AND EXPRESSION IS NULL", r.procId, r.source.serviceId, r.source.procCode, r.source.version, r.diagramId) == 1, "INSTALL_READBACK_MISMATCH");
                    require(scalar(c, "SELECT COUNT(*) FROM T_CORE_BPMN_PROC_ORG WHERE PROCID=?", r.procId) == r.source.organizations.size(), "INSTALL_READBACK_MISMATCH");
                    for (String symbol : r.source.organizations) require(scalar(c, "SELECT COUNT(*) FROM T_CORE_BPMN_PROC_ORG WHERE PROCID=? AND USEDORGID=?", r.procId, prepared.mapping.get("organizations").get(symbol)) == 1, "INSTALL_READBACK_MISMATCH");
                }
                c.commit(); return orgCount;
            } catch (Exception failure) { try { c.rollback(); } catch (SQLException ignored) { /* Original safe failure is retained. */ } throw failure; }
        }
    }
    public static void main(String[] args) {
        try {
            require(args.length == 2, "USAGE_RUNTIME_PROPERTIES_MANIFEST_PRIVATE_JSON_STDIN");
            Prepared prepared = prepare(read(System.in, 65536), Paths.get(args[1])); Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(Paths.get(args[0]))) { properties.load(input); }
            int orgs = install(properties, prepared);
            System.out.println("{\"status\":\"installed-partial\",\"definitions\":" + prepared.definitions.size() + ",\"organization_bindings\":" + orgs + ",\"blocked\":" + prepared.blockedCount + ",\"deployment_ready\":false,\"strict_evidence\":false}");
        } catch (Rejected rejected) { System.err.println("workflow-installer: " + rejected.getMessage()); System.exit(2);
        } catch (SQLException failure) { System.err.println("workflow-installer: DATABASE_REJECTED oracle_code=" + failure.getErrorCode()); System.exit(2);
        } catch (Exception failure) { System.err.println("workflow-installer: INSTALLATION_FAILED"); System.exit(2); }
    }

    /** Strict bounded JSON reader: duplicate keys, extensions and type coercion are refused. */
    private static final class Json {
        private final String source; private int position, depth;
        Json(String source) { this.source = source; }
        Object parse() { Object value = value(); space(); require(position == source.length(), "INVALID_JSON"); return value; }
        private void space() { while (position < source.length() && " \t\r\n".indexOf(source.charAt(position)) >= 0) position++; }
        private char take() { require(position < source.length(), "INVALID_JSON"); return source.charAt(position++); }
        private boolean eat(char expected) { space(); if (position < source.length() && source.charAt(position) == expected) { position++; return true; } return false; }
        private Object value() {
            space(); require(++depth <= 64 && position < source.length(), "INVALID_JSON"); char next = source.charAt(position); Object result;
            if (next == '{') {
                position++; Map<String,Object> map = new LinkedHashMap<>();
                if (!eat('}')) { do { space(); require(position < source.length() && source.charAt(position) == '"', "INVALID_JSON"); String key = string(); require(!map.containsKey(key) && eat(':'), "INVALID_JSON"); map.put(key, value()); } while (eat(',')); require(eat('}'), "INVALID_JSON"); } result = map;
            } else if (next == '[') {
                position++; List<Object> list = new ArrayList<>(); if (!eat(']')) { do { list.add(value()); } while (eat(',')); require(eat(']'), "INVALID_JSON"); } result = list;
            } else if (next == '"') result = string();
            else if (source.startsWith("true", position)) { position += 4; result = Boolean.TRUE; }
            else if (source.startsWith("false", position)) { position += 5; result = Boolean.FALSE; }
            else if (source.startsWith("null", position)) { position += 4; result = null; }
            else {
                Matcher m = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?").matcher(source); m.region(position, source.length()); require(m.lookingAt(), "INVALID_JSON"); String number = m.group(); position = m.end();
                try { result = number.matches("-?(?:0|[1-9][0-9]*)") ? (Object)Long.valueOf(number) : new BigDecimal(number); } catch (NumberFormatException bad) { throw new Rejected("INVALID_JSON_NUMBER"); }
            }
            depth--; return result;
        }
        private String string() {
            require(take() == '"', "INVALID_JSON"); StringBuilder out = new StringBuilder();
            while (true) {
                char c = take(); if (c == '"') break; require(c >= 32, "INVALID_JSON");
                if (c == '\\') {
                    char escape = take();
                    if (escape == 'u') { int number = 0; for (int i = 0; i < 4; i++) { char hex = take(); require("0123456789abcdefABCDEF".indexOf(hex) >= 0, "INVALID_JSON"); number = number * 16 + Character.digit(hex, 16); } c = (char)number; }
                    else { int i = "\"\\/bfnrt".indexOf(escape); require(i >= 0, "INVALID_JSON"); c = "\"\\/\b\f\n\r\t".charAt(i); }
                }
                out.append(c);
            }
            for (int i = 0; i < out.length(); i++) if (Character.isSurrogate(out.charAt(i))) { require(Character.isHighSurrogate(out.charAt(i)) && i + 1 < out.length() && Character.isLowSurrogate(out.charAt(i + 1)), "INVALID_JSON"); i++; }
            return out.toString();
        }
    }
}
