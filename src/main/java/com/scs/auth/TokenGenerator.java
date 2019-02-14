package com.scs.auth;

import com.google.common.hash.Hashing;
import com.scs.db.oracle.DBConnector;
import com.scs.utils.EmailSender;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class TokenGenerator {
    private String token;
    private LinkedList<String> userInfoList;

    public TokenGenerator() {
        userInfoList = DBConnector.getInstance().getUserInfo();
    }

    private void generateToken() {
        String sha256hex = Hashing.sha256()
                .hashString(userInfoList.get(0) + userInfoList.get(1) + System.currentTimeMillis(), StandardCharsets.UTF_8)
                .toString();
        token = sha256hex.substring(0,6);
        EmailSender emailSender = new EmailSender();
        String body = "username: " + userInfoList.get(0) + "<br>\n" +
                "email: " + userInfoList.get(2) + "<br>\n" +
                "Your token: " + token;
                ;
        emailSender.createEmailMessage(userInfoList.get(2),"Your token", body);
        emailSender.sendEmail();
    }

    public String getToken() {
        generateToken();
        return token;
    }
}
