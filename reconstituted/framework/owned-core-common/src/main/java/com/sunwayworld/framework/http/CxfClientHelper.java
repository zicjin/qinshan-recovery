/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.http;

import com.sunwayworld.framework.exception.NetworkException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transport.http.auth.DefaultBasicAuthSupplier;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public abstract class CxfClientHelper {
    private static final long DEFAULT_CONNECTION_TIMEOUT = 10000L;
    private static final long DEFAULT_RECEIVE_TIMEOUT = 300000L;

    public static final Object[] getData(String wsdlUrl, String userId, String password, String operation, Object ... args) {
        AuthenticationHTTPConduitConfigurer authenticationConfigurer = new AuthenticationHTTPConduitConfigurer(userId, password);
        Bus bus = BusFactory.getThreadDefaultBus();
        bus.setExtension(authenticationConfigurer, HTTPConduitConfigurer.class);
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance(bus);
        return CxfClientHelper.getData(dcf, wsdlUrl, Collections.emptyList(), operation, args);
    }

    public static final Object[] getData(String wsdlUrl, String operation, Object ... args) {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        return CxfClientHelper.getData(dcf, wsdlUrl, Collections.emptyList(), operation, args);
    }

    public static final Object[] getData(String wsdlUrl, List<Interceptor<?>> outInterceptors, String operation, Object ... args) {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        return CxfClientHelper.getData(dcf, wsdlUrl, outInterceptors, operation, args);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static final Object[] getData(JaxWsDynamicClientFactory dcf, String wsdlUrl, List<Interceptor<?>> outInterceptors, String operation, Object ... args) {
        try (Client client = dcf.createClient(new URL(wsdlUrl));){
            HTTPConduit httpConduit = (HTTPConduit)client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(10000L);
            policy.setReceiveTimeout(300000L);
            httpConduit.setClient(policy);
            QName qname = CxfClientHelper.getOperateQName(client, operation);
            client.getOutInterceptors().addAll(outInterceptors);
            Object[] objectArray = client.invoke(qname, args);
            return objectArray;
        }
        catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

    private static final QName getOperateQName(Client client, String operation) {
        Endpoint endpoint = client.getEndpoint();
        QName opName = new QName(endpoint.getService().getName().getNamespaceURI(), operation);
        BindingInfo bindingInfo = endpoint.getEndpointInfo().getBinding();
        if (bindingInfo.getOperation(opName) == null) {
            for (BindingOperationInfo operationInfo : bindingInfo.getOperations()) {
                if (!operation.equals(operationInfo.getName().getLocalPart())) continue;
                return operationInfo.getName();
            }
        }
        return opName;
    }

    public static class AuthenticationHTTPConduitConfigurer
    implements HTTPConduitConfigurer {
        private String userId;
        private String password;

        public AuthenticationHTTPConduitConfigurer(String userId, String password) {
            this.userId = userId;
            this.password = password;
        }

        @Override
        public void configure(String name, String address, HTTPConduit c) {
            AuthorizationPolicy authorization = new AuthorizationPolicy();
            authorization.setUserName(this.userId);
            authorization.setPassword(this.password);
            c.setAuthorization(authorization);
            DefaultBasicAuthSupplier supplier = new DefaultBasicAuthSupplier();
            c.setAuthSupplier(supplier);
        }
    }
}
