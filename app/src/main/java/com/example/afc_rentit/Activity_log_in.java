package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afc_rentit.Database.DatabaseManager;
import com.example.afc_rentit.Database.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Activity_log_in extends AppCompatActivity {

    private EditText etusername, etpassword;
    private Button btnlogin;
    private TextView signupredirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etusername = findViewById(R.id.login_username);
        etpassword = findViewById(R.id.login_password);
        btnlogin = findViewById(R.id.login_button);
        signupredirect = findViewById(R.id.signupredirect);


        signupredirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_log_in.this, Activity_sign_up.class);
                startActivity(intent);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etusername.getText().length() != 0 && etpassword.getText().length() != 0){
                    validateUsers();
                }
            }
        });

    }

    public void validateUsers() {
        String username = etusername.getText().toString().trim();
        String password = etpassword.getText().toString().trim();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            Current_User current_user = Current_User.getInstance();

            try (Connection connection = SQLConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tblUser WHERE username = ? AND password = ?")) {
                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();
                boolean user_found = false;

                while(resultSet.next()){
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
                    user_found = true;
                }

                boolean finalUser_found = user_found;
                runOnUiThread(()->{
                    if (finalUser_found){
                        Toast.makeText(getApplicationContext(), "Log-in Success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_log_in.this, MainActivity.class);
                        startActivity(intent);
                        clearForm();
                    } else {
                        Toast.makeText(getApplicationContext(), "Log-in unsuccessful! Username or password is incorrect", Toast.LENGTH_SHORT).show();
                        clearForm();
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            };
        });
    }

    private void clearForm(){
        etusername.setText("");
        etpassword.setText("");
    }
}