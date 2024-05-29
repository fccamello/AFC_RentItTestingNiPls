package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Activity_InitialSignUp extends AppCompatActivity {
    private EditText etemail, etusername, etpassword;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_sign_up);
        initiate();
        Intent intent = new Intent(Activity_InitialSignUp.this, Activity_sign_up.class);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passIntents(intent);
            }
        });
    }

    public void initiate() {
        etemail = findViewById(R.id.signup_email);
        etusername = findViewById(R.id.signup_username);
        etpassword = findViewById(R.id.signup_password);

        btnContinue = findViewById(R.id.btnContinue);
    }

    public void passIntents(Intent intent) {
        String email, usern, pass;

        email = etemail.getText().toString();
        usern = etusername.getText().toString();
        pass = etpassword.getText().toString();

        if(email.isEmpty() || usern.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {

            intent.putExtra("email-key",email);
            intent.putExtra("username-key",usern);
            intent.putExtra("password-key",pass);
            startActivity(intent);
        }
    }

}