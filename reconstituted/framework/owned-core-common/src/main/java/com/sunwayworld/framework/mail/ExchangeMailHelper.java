/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mail;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.springframework.core.env.Environment;

public class ExchangeMailHelper {
    private static final String EXCHANGE_MAIL_USERNAME = "exchange.mail.username";
    private static final String EXCHANGE_MAIL_PASSWORD = "exchange.mail.password";
    private static final String EXCHANGE_MAIL_DOMAIN = "exchange.mail.domain";
    private static final String EXCHANGE_MAIL_SERVER = "exchange.mail.server";
    private static volatile ExchangeService exchangeService;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ExchangeService getExchangeService() {
        if (exchangeService != null) return exchangeService;
        Class<ExchangeMailHelper> clazz = ExchangeMailHelper.class;
        synchronized (ExchangeMailHelper.class) {
            if (exchangeService != null) return exchangeService;
            exchangeService = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
            Environment env = ApplicationContextHelper.getEnvironment();
            String username = env.getProperty(EXCHANGE_MAIL_USERNAME);
            String passwrod = env.getProperty(EXCHANGE_MAIL_PASSWORD);
            String domain = env.getProperty(EXCHANGE_MAIL_DOMAIN);
            String server = env.getProperty(EXCHANGE_MAIL_SERVER);
            WebCredentials credentials = new WebCredentials(username, passwrod, domain);
            exchangeService.setCredentials(credentials);
            try {
                exchangeService.setUrl(new URI(server));
            }
            catch (URISyntaxException ex) {
                throw new UnexpectedException(ex);
            }
            return exchangeService;
        }
    }

    public static void sendHtmlMail(String subject, String content, List<String> toList, File ... attachments) {
        if (toList.isEmpty()) {
            return;
        }
        try {
            EmailMessage message = new EmailMessage(ExchangeMailHelper.getExchangeService());
            message.setSubject(subject);
            MessageBody body = MessageBody.getMessageBodyFromText(content);
            body.setBodyType(BodyType.HTML);
            message.setBody(body);
            toList.forEach(item -> {
                try {
                    message.getToRecipients().add((String)item);
                }
                catch (ServiceLocalException e) {
                    throw new UnexpectedException(e);
                }
            });
            if (attachments != null) {
                for (File attachment : attachments) {
                    message.getAttachments().addFileAttachment(attachment.getName());
                }
            }
            message.send();
        }
        catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    public static void sendHtmlMailForList(String subject, String content, List<String> toList, List<String> ccList, File ... attachments) {
        if (toList.isEmpty()) {
            return;
        }
        try {
            EmailMessage message = new EmailMessage(ExchangeMailHelper.getExchangeService());
            message.setSubject(subject);
            MessageBody body = MessageBody.getMessageBodyFromText(content);
            body.setBodyType(BodyType.HTML);
            message.setBody(body);
            toList.forEach(item -> {
                try {
                    message.getToRecipients().add((String)item);
                }
                catch (ServiceLocalException e) {
                    throw new UnexpectedException(e);
                }
            });
            ccList.forEach(item -> {
                try {
                    message.getCcRecipients().add((String)item);
                }
                catch (ServiceLocalException e) {
                    throw new UnexpectedException(e);
                }
            });
            if (attachments != null) {
                for (File attachment : attachments) {
                    message.getAttachments().addFileAttachment(attachment.getAbsolutePath());
                }
            }
            message.send();
        }
        catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }
}
