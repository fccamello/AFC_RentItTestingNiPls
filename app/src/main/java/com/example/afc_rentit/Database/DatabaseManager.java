package com.example.afc_rentit.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {
//    private static DatabaseManager instance;
//    private DatabaseManager(){};
//    public static DatabaseManager getInstance(){
//        if (instance == null){
//            instance = new DatabaseManager();
//        }
//
//        return instance;
//    };
    public static void initializeDB(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection c = SQLConnection.getConnection()){
                Statement stmt;
                stmt = c.createStatement();

                String createDBQuery = "CREATE DATABASE IF NOT EXISTS dbrentit;";
                stmt.execute(createDBQuery);

                SQLConnection.DBName = "dbrentit";
                c.setCatalog("dbrentit");

                c.setAutoCommit(false);
                stmt = c.createStatement();

                String createTblUserQuery = "CREATE TABLE IF NOT EXISTS tblUser (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "firstname VARCHAR (50) NOT NULL," +
                        "lastname VARCHAR (50) NOT NULL," +
                        "gender VARCHAR (50) NOT NULL," +
                        "address VARCHAR (100) NOT NULL," +
                        "contact_number INT(11) NOT NULL," +
                        "email VARCHAR (50) NOT NULL," +
                        "username VARCHAR (50) NOT NULL," +
                        "password VARCHAR (50) NOT NULL)," +
                        "isOwner INT (1) NOT NULL DEFAULT 0";
                stmt.execute(createTblUserQuery);

                String createTblItemQuery = "CREATE TABLE IF NOT EXISTS tblItem(" +
                        "item_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id INT NOT NULL," +
                        "title VARCHAR (50) NOT NULL," +
                        "description VARCHAR (100) NOT NULL," +
                        "image VARCHAR (50) NOT NULL," +
                        "category VARCHAR (25) NOT NULL," +
                        "price DOUBLE NOT NULL," +
                        "isAvailable INT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES tbluser (user_id) ON_DELETE CASCADE)";
                stmt.execute(createTblItemQuery);

                String createtblRentRequest = "CREATE TABLE IF NOT EXISTS tblRentRequest(" +
                        "rent_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "item_id INT NOT NULL," +
                        "user_id INT NOT NULL," +
                        "requestDate DATE NOT NULL," +
                        "durationCategory INT/VARCHAR NOT NULL," +
                        "duration INT NOT NULL," +
                        "endRentDate DATE NOT NULL," +
                        ")";

                c.commit();
                System.out.println("Database with TABLES created successfully.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}