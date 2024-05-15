package com.example.afc_rentit.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager(){};
    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
        }

        return instance;
    };
    public void initializeDB (){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection c = SQLConnection.getConnection()){
                Statement stmt;
                stmt = c.createStatement();

                String createDBQuery = "CREATE DATABASE IF NOT EXISTS dbrentit;";
                stmt.execute(createDBQuery);

                SQLConnection.DBName = "dbrentit";
                c.setCatalog("dbrentit");

                stmt = c.createStatement();

                String createTblUsersQuery = "CREATE TABLE IF NOT EXISTS tblUser (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "firstname VARCHAR (50) NOT NULL," +
                        "lastname VARCHAR (50) NOT NULL," +
                        "gender VARCHAR (50) NOT NULL," +
                        "email VARCHAR (50) NOT NULL," +
                        "username VARCHAR (50) NOT NULL," +
                        "password VARCHAR (50) NOT NULL)";
                stmt.execute(createTblUsersQuery);

                System.out.println("Database with TABLES created successfully.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}