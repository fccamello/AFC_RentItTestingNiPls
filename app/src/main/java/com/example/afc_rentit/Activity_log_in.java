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
                validateUsers();
            }
        });

    }

    public void validateUsers() {
        String username = etusername.getText().toString().trim();
        String password = etpassword.getText().toString().trim();

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.validateUser(username, password);



        Toast.makeText(getApplicationContext(), "Log-in Success!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Activity_log_in.this, MainActivity.class);
        startActivity(intent);
    }
}