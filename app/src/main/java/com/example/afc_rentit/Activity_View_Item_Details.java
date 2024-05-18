package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Activity_View_Item_Details extends AppCompatActivity {
    int item_id;
    TextView tv_price, tv_itemTitle, tv_itemDesc, tv_itemOwner, tv_itemAdd;
    Item item;
    String owner, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_details);

        Intent intent = getIntent();
        item_id = intent.getIntExtra("item_id", 0);

        System.out.println("item_id: " + item_id);

        initializeViewItem();
        getItemDetails();
    }

    private void getItemDetails() {
    }

    private void displayData(){
        tv_price.setText(Double.toString(item.getPrice()));

    }

    private void initializeViewItem() {
        tv_price = findViewById(R.id.tv_price);
        tv_itemTitle = findViewById(R.id.tv_itemtitle);
        tv_itemDesc = findViewById(R.id.tv_itemdesc);
        tv_itemOwner = findViewById(R.id.tv_itemowner);
        tv_itemAdd = findViewById(R.id.tv_itemadd);
    }
}