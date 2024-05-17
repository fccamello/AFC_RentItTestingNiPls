package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Activity_View_Item_Details extends AppCompatActivity {
    int item_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_details);

        Intent intent = getIntent();
        item_id = intent.getIntExtra("item_id", 0);

    }
}