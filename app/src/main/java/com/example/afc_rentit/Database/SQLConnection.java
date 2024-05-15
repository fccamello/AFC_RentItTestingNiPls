package com.example.afc_rentit.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    public static final String IP = "192.168.215.178"; // Charlene IP
    public static final String URL = "jdbc:mysql://" + IP + ":3306/";
    public static String DBName = "";
    public static final String USERNAME = "AFC";
    public static final String PASSWORD = "afcrentit";
    public static Connection getConnection (){
        Connection c = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String finalURL = URL + DBName;
            c = DriverManager.getConnection(finalURL, USERNAME, PASSWORD);
            System.out.println("DB Connection success");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return c;
    }
}