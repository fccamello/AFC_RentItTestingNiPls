package com.example.afc_rentit.Database;

import com.example.afc_rentit.Current_User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager(){
        initializeDB();
    }
    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
        }

        return instance;
    }
    public void initializeDB(){
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
                        "password VARCHAR (50) NOT NULL," +
                        "isOwner INT (1) NOT NULL DEFAULT 0)";
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
                        "FOREIGN KEY (user_id) REFERENCES tbluser (user_id) ON DELETE CASCADE)";
                stmt.execute(createTblItemQuery);

                String createtblRentRequest = "CREATE TABLE IF NOT EXISTS tblRentRequest(" +
                        "rent_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "item_id INT NOT NULL," +
                        "user_id INT NOT NULL," +
                        "requestDate DATE NOT NULL," +
                        "durationCategory VARCHAR (20) NOT NULL," +
                        "duration INT NOT NULL," +
                        "endRentDate DATE NOT NULL," +
                        "totalAmount DOUBLE NOT NULL," +
                        "isApproved INT NOT NULL DEFAULT -1," +
                        "isReturned INT NOT NULL DEFAULT 0," +
                        "FOREIGN KEY (item_id) REFERENCES tblItem (item_id)," +
                        "FOREIGN KEY (user_id) REFERENCES tblUser (user_id) ON DELETE CASCADE)";
                stmt.execute(createtblRentRequest);

                c.commit();
                System.out.println(SQLConnection.DBName);
                System.out.println("Database with TABLES created successfully.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private final Current_User current_user = Current_User.getInstance();
    public void insertUser(String firstName, String lastName, String gender, String email, String address, String username, String userType, String password) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection c = SQLConnection.getConnection()) {
                String query = "INSERT INTO tblUser (firstname, lastname, gender, address, contact_number, email, username, password, isOwner) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    int isOwner = (userType.equals("Owner")) ? 1 : 0; // if userType == 0, then 1 else 0

                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, gender);
                    statement.setString(4, address);
                    statement.setInt(5, 45); // contact_number
                    statement.setString(6, email);
                    statement.setString(7, username);
                    statement.setString(8, password);
                    statement.setInt(9, isOwner);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("User inserted successfully.");
                        ResultSet res = statement.getGeneratedKeys();
                        res.next();
                        int user_id = res.getInt(1);
                        current_user.setCurrent_User(user_id, username, firstName, lastName, email, address, gender, (isOwner == 1));
                        System.out.println(current_user);
                    } else {
                        System.out.println("Failed to insert user.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean userIsFound = false;

    public boolean validateUser(String username, String password) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection connection = SQLConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tblUser WHERE username = ? AND password = ?")) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    userIsFound = true;

                    resultSet.next();
                    current_user.setCurrent_User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("email"),
                            resultSet.getString("address"),
                            resultSet.getString("gender"),
                            (resultSet.getInt("isOwner") == 1)
                    );

                    System.out.println(current_user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return userIsFound;
    }
}