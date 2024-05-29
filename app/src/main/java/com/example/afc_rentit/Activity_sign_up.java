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
import android.widget.Toast;

import com.example.afc_rentit.Database.DatabaseManager;

public class Activity_sign_up extends AppCompatActivity {

    private EditText etfname, etlastname, etemail, etusername, etpassword, etaddress, etcontactnum;
    private TextView loginredirect;

    private RadioButton rb_buyer, rb_seller, rbmale, rbfemale, rbothers;
    private Intent intent;
    private Button btnsignUp;
    DatabaseManager dbManager = DatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        etfname = findViewById(R.id.signup_firstname);
        etlastname = findViewById(R.id.signup_lastname);
        etcontactnum = findViewById(R.id.contact_number);
        etaddress = findViewById(R.id.address);

        rb_seller = findViewById(R.id.rb_seller);
        rb_buyer = findViewById(R.id.rb_buyer);

        rbmale = findViewById(R.id.rbmale);
        rbfemale = findViewById(R.id.rbfemale);
        rbothers = findViewById(R.id.rbothers);

        btnsignUp = findViewById(R.id.signup_button);

        btnsignUp.setOnClickListener(view -> insertUsers());

    }

    public void insertUsers() {
        intent = getIntent();

        String firstName = etfname.getText().toString().trim();
        String lastName = etlastname.getText().toString().trim();

        String email = intent.getStringExtra("email-key");
        String username = intent.getStringExtra("username-key");
        String password = intent.getStringExtra("password-key");

        String address = etaddress.getText().toString().trim();
        String contactnum = etcontactnum.getText().toString().trim();

        System.out.println("email" +email);
        System.out.println("username " +username);
        System.out.println("password " + password);

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

        if(firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || contactnum.isEmpty() || userType.isEmpty() || gender.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        boolean user_inserted = dbManager.insertUser(firstName, lastName, gender,address,contactnum,email,username,password, userType);

        if (user_inserted) {
            Intent intent1 = new Intent(Activity_sign_up.this, Activity_log_in.class);
            startActivity(intent1);
        }
    }
}