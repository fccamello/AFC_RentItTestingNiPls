package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.afc_rentit.Database.DatabaseManager;

public class Activity_sign_up extends AppCompatActivity {

    private EditText etfname, etlastname, etemail, etusername, etpassword;
    private TextView loginredirect;

    private RadioGroup userTypeRadioGroup, genderRadioGroup;
    private Button btnsignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initializeDB();

        etfname = findViewById(R.id.signup_firstname);
        etlastname = findViewById(R.id.signup_lastname);
        etemail = findViewById(R.id.signup_email);
        etusername = findViewById(R.id.signup_username);
        etpassword = findViewById(R.id.signup_password);
        userTypeRadioGroup = findViewById(R.id.rb_usertype);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        btnsignUp = findViewById(R.id.signup_button);

        btnsignUp.setOnClickListener(view -> insertUsers());

        loginredirect = findViewById(R.id.loginredirect);
        loginredirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_sign_up.this, Activity_log_in.class);
                startActivity(intent);
            }
        });


    }

    public void insertUsers(){

        String firstName = etfname.getText().toString().trim();
        String lastName = etlastname.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String username = etusername.getText().toString().trim();
        String password = etpassword.getText().toString().trim();

        // Get selected user type
        RadioButton userTypeRadioButton = findViewById(userTypeRadioGroup.getCheckedRadioButtonId());
//        String userType = userTypeRadioButton.getText().toString();
        String userType = "Renter";
        // Get selected gender
        RadioButton genderRadioButton = findViewById(genderRadioGroup.getCheckedRadioButtonId());
        String gender = genderRadioButton.getText().toString();

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.insertUser(firstName, lastName, gender, email, "address", username, userType, password);
    }

}