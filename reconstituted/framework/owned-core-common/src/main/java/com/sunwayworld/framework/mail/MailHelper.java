/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mail;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import java.io.File;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailHelper {
    private static JavaMailSender mailSender;

    @Autowired
    @Lazy
    public void setJavaMailSender(JavaMailSender mailSender) {
        MailHelper.mailSender = mailSender;
    }

    public static void sendSimpleMail(String subject, String content, List<String> toList) {
        if (toList.isEmpty()) {
            return;
        }
        MailHelper.sendSimpleMail(subject, content, toList.toArray(new String[0]));
    }

    public static void sendSimpleMail(String subject, String content, String ... receivers) {
        if (receivers == null || receivers.length == 0) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(ApplicationContextHelper.getEnvironment().getProperty("spring.mail.username"));
        message.setTo(receivers);
        message.setSubject(subject);
        message.setText(content == null ? "" : content);
        mailSender.send(message);
    }

    public static void sendHtmlMail(String subject, String content, List<String> toList, File ... attachments) {
        if (toList.isEmpty()) {
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(ApplicationContextHelper.getEnvironment().getProperty("spring.mail.username"));
            helper.setTo(toList.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(content, true);
            if (attachments != null && attachments.length > 0) {
                for (File attachment : attachments) {
                    helper.addAttachment(attachment.getName(), attachment);
                }
            }
            mailSender.send(message);
        }
        catch (MessagingException me) {
            throw new UnexpectedException(me);
        }
    }

    public static void sendHtmlMailForList(String subject, String content, List<String> toList, List<String> ccList, File ... attachments) {
        if (toList.isEmpty()) {
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(ApplicationContextHelper.getEnvironment().getProperty("spring.mail.username"));
            helper.setTo(toList.toArray(new String[0]));
            helper.setCc(ccList.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(content, true);
            if (attachments != null && attachments.length > 0) {
                for (File attachment : attachments) {
                    helper.addAttachment(attachment.getName(), attachment);
                }
            }
            mailSender.send(message);
        }
        catch (MessagingException me) {
            throw new UnexpectedException(me);
        }
    }
}
