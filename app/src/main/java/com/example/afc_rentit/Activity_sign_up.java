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

    private EditText etfname, etlastname, etemail, etusername, etpassword, etaddress, etcontactnum;
    private TextView loginredirect;

    private RadioButton rb_buyer, rb_seller, rbmale, rbfemale, rbothers;
    private Button btnsignUp;
    DatabaseManager dbManager = DatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        etfname = findViewById(R.id.signup_firstname);
        etlastname = findViewById(R.id.signup_lastname);
        etemail = findViewById(R.id.signup_email);
        etusername = findViewById(R.id.signup_username);
        etpassword = findViewById(R.id.signup_password);
        etcontactnum = findViewById(R.id.contact_number);
        etaddress = findViewById(R.id.address);
        rb_seller = findViewById(R.id.rb_seller);
        rb_buyer = findViewById(R.id.rb_buyer);
        rbmale = findViewById(R.id.rbmale);
        rbfemale = findViewById(R.id.rbfemale);
        rbothers = findViewById(R.id.rbothers);


        btnsignUp = findViewById(R.id.signup_button);

        btnsignUp.setOnClickListener(view -> insertUsers());

        loginredirect = findViewById(R.id.loginredirect);
        loginredirect.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_sign_up.this, Activity_log_in.class);
            startActivity(intent);
        });
    }

    public void insertUsers() {

        String firstName = etfname.getText().toString().trim();
        String lastName = etlastname.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String username = etusername.getText().toString().trim();
        String password = etpassword.getText().toString().trim();
        String addresss = etaddress.getText().toString().trim();
        String contactnum = etcontactnum.getText().toString().trim();

        String userType = "Renter"; // Default to Renter
        if (rb_seller.isChecked()) {
            userType = "Owner";
        }

        // Get selected gender
        String gender;
        if (rbfemale.isChecked()){
            gender = "Female";
        }
        else if (rbmale.isChecked()){
            gender="Male";
        }
        else{
            gender="Others";
        }


        boolean user_inserted = dbManager.insertUser(firstName, lastName, gender,addresss,contactnum,email,username,password, userType);

        if (user_inserted) {
            Intent intent1 = new Intent(Activity_sign_up.this, MainActivity.class);
            startActivity(intent1);
        }
    }
}