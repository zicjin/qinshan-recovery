/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base.SoapRequestUrl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SoapConnection
extends WSHTTPConnection {
    private static final BasePropertySet.PropertyMap propertyMap = SoapConnection.parse(SoapConnection.class);
    private final HttpServletRequest httpRequest;
    private final HttpServletResponse httpResponse;
    private final SoapRequestUrl soapRequestUrl;
    private final WebServiceContextDelegate webServiceContextDelegate;
    private Map<String, List<String>> requestHeaders;
    private Map<String, List<String>> responseHeaders;

    public SoapConnection(HttpServletRequest request, HttpServletResponse response, SoapRequestUrl soapRequestUrl, WebServiceContextDelegate delegate) {
        this.httpRequest = request;
        this.httpResponse = response;
        this.soapRequestUrl = soapRequestUrl;
        this.webServiceContextDelegate = delegate;
    }

    @Override
    public void setResponseHeaders(Map<String, List<String>> headers) {
        this.responseHeaders = headers;
        if (headers == null) {
            return;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            if (name.equalsIgnoreCase("Content-Type") || name.equalsIgnoreCase("Content-Length")) continue;
            for (String value : entry.getValue()) {
                this.httpResponse.setHeader(name, value);
            }
        }
    }

    @Override
    public void setResponseHeader(String key, List<String> value) {
        this.initializeResponseHeaders();
        this.responseHeaders.put(key, value);
        for (String v : value) {
            this.httpResponse.setHeader(key, v);
        }
    }

    @Override
    public void setContentTypeResponseHeader(String value) {
        this.setResponseHeader("Content-Type", Collections.singletonList(value));
    }

    @Override
    public void setStatus(int status) {
        this.httpResponse.setStatus(status);
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.response.code"})
    public int getStatus() {
        return this.httpResponse.getStatus();
    }

    @Override
    public InputStream getInput() throws IOException {
        return this.httpRequest.getInputStream();
    }

    @Override
    public OutputStream getOutput() throws IOException {
        return this.httpResponse.getOutputStream();
    }

    @Override
    public WebServiceContextDelegate getWebServiceContextDelegate() {
        return this.webServiceContextDelegate;
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.method"})
    public String getRequestMethod() {
        return this.httpRequest.getMethod();
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.headers", "com.sun.xml.ws.api.message.packet.inbound.transport.headers"})
    public Map<String, List<String>> getRequestHeaders() {
        if (this.requestHeaders == null) {
            this.initializeRequestHeaders();
        }
        return this.requestHeaders;
    }

    @Override
    public Set<String> getRequestHeaderNames() {
        if (this.requestHeaders == null) {
            this.initializeRequestHeaders();
        }
        return this.responseHeaders.keySet();
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return this.responseHeaders;
    }

    @Override
    public String getRequestHeader(String headerName) {
        return this.httpRequest.getHeader(headerName);
    }

    @Override
    public List<String> getRequestHeaderValues(String headerName) {
        if (this.requestHeaders == null) {
            this.initializeRequestHeaders();
        }
        return this.requestHeaders.get(headerName);
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.querystring"})
    public String getQueryString() {
        return this.soapRequestUrl.queryString;
    }

    @Override
    @PropertySet.Property(value={"javax.xml.ws.http.request.pathinfo"})
    public String getPathInfo() {
        return this.soapRequestUrl.pathInfo;
    }

    @Override
    public String getRequestURI() {
        return this.httpRequest.getRequestURI();
    }

    @Override
    public String getRequestScheme() {
        return this.soapRequestUrl.isSecure ? "https" : "http";
    }

    @Override
    public String getServerName() {
        return this.soapRequestUrl.serverName;
    }

    @Override
    public int getServerPort() {
        return this.soapRequestUrl.serverPort;
    }

    @Override
    public boolean isSecure() {
        return this.soapRequestUrl.isSecure;
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return propertyMap;
    }

    @Override
    public String getContextPath() {
        return this.soapRequestUrl.contextPath;
    }

    @Override
    public String getBaseAddress() {
        return this.soapRequestUrl.baseAddress;
    }

    @Override
    public String getProtocol() {
        return this.httpRequest.getProtocol();
    }

    @Override
    public void setContentLengthResponseHeader(int value) {
        this.setResponseHeader("Content-Length", Collections.singletonList(String.valueOf(value)));
    }

    private void initializeResponseHeaders() {
        if (this.responseHeaders == null) {
            this.responseHeaders = new HashMap<String, List<String>>();
        }
    }

    private void initializeRequestHeaders() {
        Enumeration<String> headerNames = this.httpRequest.getHeaderNames();
        this.requestHeaders = new HashMap<String, List<String>>();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            Enumeration<String> headers = this.httpRequest.getHeaders(s);
            ArrayList<String> headerList = new ArrayList<String>();
            while (headers.hasMoreElements()) {
                headerList.add(headers.nextElement());
            }
            this.requestHeaders.put(s, headerList);
        }
    }
}
