package com.scs.db.oracle;

import com.google.common.hash.Hashing;
import com.scs.variables.DataVariables;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.LinkedList;
import java.util.Map;

public class DBConnector {
    private String userName;
    private String password;
    private String passwordSalt = "This is password salt 1234;214 !@$$$@#@#!" + userName;

    private String hostName;
    private String dbPort;
    private String serviceName;
    private String dbUser;
    private String dbPassword;

    private Statement statement;
    private Connection connection;
    private ResultSet resultSet;
    private ResultSetMetaData resultSetMetaData;

    private LinkedList<String> userInfo;

    private volatile static DBConnector dbInstance;

    private DBConnector() {
        readConfig();
        userInfo = new LinkedList<>();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@" + hostName + ":" + dbPort + "/" + serviceName,
                    dbUser, dbPassword);
            if (connection != null) {
                System.out.println("!! Connected With Oracle Database !!\n");
                statement = connection.createStatement();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void readConfig() {
        Yaml conf = new Yaml();
        try {
            InputStream confFile = new FileInputStream(new File("db.conf"));
            Map<String, Object> confMap = conf.load(confFile);
            hostName = (String) confMap.get("hostname");
            dbPort = confMap.get("port").toString();
            serviceName = (String) confMap.get("service_name");
            dbUser = (String) confMap.get("db_user");
            dbPassword = (String) confMap.get("db_password");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static DBConnector getInstance() {
        if (dbInstance == null) {
            synchronized (DBConnector.class) {
                if (dbInstance == null) {
                    dbInstance = new DBConnector();
                }
            }
        }
        return dbInstance;
    }

    private void closeDB() {
        try {
            resultSet.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        synchronized (DBConnector.class) {
            if (dbInstance != null) {
                dbInstance.closeDB();
                dbInstance = null;
            }
        }
    }

    public static void initialize() {
        reset();
        getInstance();
    }

    private String getHash(String input) {
        String sha256hex = Hashing.sha256()
                .hashString(input, StandardCharsets.UTF_8)
                .toString();
//        System.out.println("Finsihed hash calculation");
        return sha256hex;
    }

    public void setUser(String user, String pass) {
        userName = user;
        password = getHash(passwordSalt + userName);
        DataVariables.userName = userName;
    }

    public boolean addUser(String user, String pass, String phone, String email, String address, String postal, String country, String ans1, String ans2, String ans3) {
        boolean addSuccess = true;
        setUser(user, pass);
        String sql = "INSERT INTO users_info (" +
                "id," +
                "username," +
                "password," +
                "phone," +
                "email," +
                "address," +
                "postal," +
                "country," +
                "ANSWER_1," +
                "ANSWER_2," +
                "ANSWER_3" +
                ")" +
                "VALUES (" +
                "users_info_seq.NEXTVAL" + ",'" +
                userName + "','" +
                password + "','" +
                phone + "','" +
                email + "','" +
                address + "','" +
                postal + "','" +
                country + "','" +
                ans1 + "','" +
                ans2 + "','" +
                ans3 + "')";
        try {
            statement.execute(sql);
            statement.execute("commit");
        } catch (SQLException e) {
            e.printStackTrace();
            addSuccess = false;
        }
        return addSuccess;
    }

    public boolean verifyUser(String userName, String password) {
        setUser(userName, password);

        String sql = "SELECT *" +
                "    FROM   users_info" +
                "    WHERE  username = '" + this.userName + "'" +
                "    AND    password = '" + this.password + "'";
        boolean verified = false;
        try {
            resultSet = statement.executeQuery(sql);
            resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                userInfo.add(resultSet.getString(2));
                for (int i = 4; i < 11; i++) {
                    userInfo.add(resultSet.getString(i));
                }
                verified = true;
                DataVariables.userEmail = userInfo.get(2);
            }
//            System.out.println(verified);
        } catch (SQLException e) {
            e.printStackTrace();
            verified = false;
        }
        return verified;
    }

    public boolean changeUserInfo(String password, String phone, String email, String address, String post, String country) {
        boolean changeSuccess = true;
        String updates = "";
        String newPass;
        if (password.length() >= 8) {
            newPass = getHash(password);
            updates += "password = '" + newPass + "',";
        }
        if (phone.length() >= 1) {
            updates += "phone = '" + phone + "',";
        }
        if (email.length() >= 1) {
            updates += "email = '" + email + "',";
        }
        if (address.length() >= 1) {
            updates += "address = '" + address + "',";
        }
        if (post.length() >= 1) {
            updates += "postal = '" + post + "',";
        }
        if (country.length() >= 1) {
            updates += "country = '" + country + "',";
        }
        String sql = "update users_info set " + updates.substring(0, updates.length() - 1) + " WHERE username = '" +
                this.userName + "' AND password = '" + this.password + "'";
        try {
            statement.execute(sql);
            statement.execute("commit");
        } catch (SQLException e) {
            e.printStackTrace();
            changeSuccess = false;
        }
        if (changeSuccess && password.length() >= 8) {
            setUser(this.userName, password);
        }
        return changeSuccess;
    }

    public LinkedList<String> getUserInfo() {
        verifyUser(this.userName, this.password);
        return userInfo;
    }
}
