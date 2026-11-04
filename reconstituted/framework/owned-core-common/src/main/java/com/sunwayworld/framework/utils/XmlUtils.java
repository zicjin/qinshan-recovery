/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

public class XmlUtils {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static final Document read(File xmlFile) {
        try (FileInputStream is = new FileInputStream(xmlFile);){
            Document document = XmlUtils.read(is);
            return document;
        }
        catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static final Document read(String xml) {
        if (StringUtils.isEmpty(xml)) {
            return null;
        }
        try (ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));){
            Document document = XmlUtils.read(is);
            return document;
        }
        catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
    }

    public static final Document read(InputStream xmlInputStream) {
        SAXReader saxReader = new SAXReader();
        try {
            saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            return saxReader.read(xmlInputStream);
        }
        catch (DocumentException | SAXException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public static final String getAttributeValue(Element element, String attributeName) {
        Attribute attribute = element.attribute(attributeName);
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }
}
