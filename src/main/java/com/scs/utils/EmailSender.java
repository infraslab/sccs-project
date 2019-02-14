package com.scs.utils;


import org.yaml.snakeyaml.Yaml;

import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class EmailSender {

    private Properties emailProperties;
    private Session mailSession;
    private MimeMessage emailMessage;

    private String email;
    private String password;

    public EmailSender() {
        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        readConfig();
    }

    public void createEmailMessage(String toWhom, String issue, String body) {
        String[] toEmails = {"tienpham@stud.fra-uas.de", toWhom};
        String emailSubject = "Fall Detection Project - " + issue;

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);
        try {
            for (int i = 0; i < toEmails.length; i++) {
                if (toEmails[i].length() > 1) {
                    emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
                }
            }
            emailMessage.setSubject(emailSubject);
            emailMessage.setContent(body, "text/html");//for a html email
            //emailMessage.setText(emailBody);// for a text email
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

    private void readConfig() {
        Yaml conf = new Yaml();
        try {
            InputStream confFile = new FileInputStream(new File("email.conf"));
            Map<String, Object> confMap = conf.load(confFile);
            email = (String) confMap.get("email");
            password = (String) confMap.get("password");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail() {

        String emailHost = "smtp.gmail.com";
        String fromUser = email;//just the id alone without @gmail.com
        String fromUserEmailPassword = password;

        Transport transport;
        try {
            transport = mailSession.getTransport("smtp");
            transport.connect(emailHost, fromUser, fromUserEmailPassword);
            transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
            transport.close();
            System.out.println("Email sent successfully.");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

}