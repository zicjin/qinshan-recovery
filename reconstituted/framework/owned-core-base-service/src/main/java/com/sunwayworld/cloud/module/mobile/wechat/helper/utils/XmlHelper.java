/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlHelper {
    private final XPath path;
    private final Document doc;
    private static boolean preventedXXE = false;

    private XmlHelper(InputSource inputSource) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = XmlHelper.getDocumentBuilderFactory();
        DocumentBuilder db = dbf.newDocumentBuilder();
        this.doc = db.parse(inputSource);
        this.path = XmlHelper.getXPathFactory().newXPath();
    }

    private static XmlHelper create(InputSource inputSource) {
        try {
            return new XmlHelper(inputSource);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static XmlHelper of(InputStream is) {
        InputSource inputSource = new InputSource(is);
        return XmlHelper.create(inputSource);
    }

    public static XmlHelper of(String xmlStr) {
        StringReader sr = new StringReader(xmlStr.trim());
        InputSource inputSource = new InputSource(sr);
        XmlHelper xmlHelper = XmlHelper.create(inputSource);
        IOUtils.closeQuietly(sr);
        return xmlHelper;
    }

    private Object evalXPath(String expression, Object item, QName returnType) {
        item = null == item ? this.doc : item;
        try {
            return this.path.evaluate(expression, item, returnType);
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String expression) {
        return (String)this.evalXPath(expression, null, XPathConstants.STRING);
    }

    public Boolean getBoolean(String expression) {
        return (Boolean)this.evalXPath(expression, null, XPathConstants.BOOLEAN);
    }

    public Number getNumber(String expression) {
        return (Number)this.evalXPath(expression, null, XPathConstants.NUMBER);
    }

    public Node getNode(String expression) {
        return (Node)this.evalXPath(expression, null, XPathConstants.NODE);
    }

    public NodeList getNodeList(String expression) {
        return (NodeList)this.evalXPath(expression, null, XPathConstants.NODESET);
    }

    public String getString(Object node, String expression) {
        return (String)this.evalXPath(expression, node, XPathConstants.STRING);
    }

    public Boolean getBoolean(Object node, String expression) {
        return (Boolean)this.evalXPath(expression, node, XPathConstants.BOOLEAN);
    }

    public Number getNumber(Object node, String expression) {
        return (Number)this.evalXPath(expression, node, XPathConstants.NUMBER);
    }

    public Node getNode(Object node, String expression) {
        return (Node)this.evalXPath(expression, node, XPathConstants.NODE);
    }

    public NodeList getNodeList(Object node, String expression) {
        return (NodeList)this.evalXPath(expression, node, XPathConstants.NODESET);
    }

    public Map<String, String> toMap() {
        Element root = this.doc.getDocumentElement();
        HashMap<String, String> params = new HashMap<String, String>();
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            Node node = list.item(i);
            if (!(node instanceof Element)) continue;
            params.put(node.getNodeName(), node.getTextContent());
        }
        return params;
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = XmlHelperHolder.documentBuilderFactory;
        if (!preventedXXE) {
            XmlHelper.preventXXE(dbf);
            preventedXXE = true;
        }
        return dbf;
    }

    private static void preventXXE(DocumentBuilderFactory dbf) throws ParserConfigurationException {
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
    }

    private static XPathFactory getXPathFactory() {
        return XmlHelperHolder.xPathFactory;
    }

    private static class XmlHelperHolder {
        private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        private static XPathFactory xPathFactory = XPathFactory.newInstance();

        private XmlHelperHolder() {
        }
    }
}
