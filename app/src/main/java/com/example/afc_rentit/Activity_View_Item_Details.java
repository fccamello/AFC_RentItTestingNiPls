package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.afc_rentit.Database.SQLConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Activity_View_Item_Details extends AppCompatActivity {
    int item_id;
    TextView tv_price, tv_itemTitle, tv_itemDesc, tv_itemOwner, tv_itemAdd;
    String title, image, price, description, owner, address;
    Button btnRentItem;
    ImageButton btnBackArrow;
    ImageView iv_image;

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
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT title, description, price, image, username, address " +
                                 "FROM tblItem as i, tblUser as u " +
                                 "WHERE i.user_id = u.user_id AND item_id = ?"
                 )) {

                pstmt.setInt(1, item_id);

                ResultSet res = pstmt.executeQuery();

                if (res.next()){
                    price = String.valueOf(res.getDouble("price"));
                    title = res.getString("title");
                    description = res.getString("description");
                    owner = res.getString("username");
                    address = res.getString("address");
                    image = res.getString("image");

                    runOnUiThread(this::displayData);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void displayData(){
        tv_price.setText(price);
        tv_itemTitle.setText(title);
        tv_itemDesc.setText(description);
        tv_itemOwner.setText(owner);
        tv_itemAdd.setText(address);

//        new Thread(() -> {
//            try {
//                URL url = new URL(image);
//                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                runOnUiThread(() -> iv_image.setImageBitmap(bitmap));
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Optionally set a placeholder or error image
//                runOnUiThread(() -> iv_image.setImageResource(R.drawable.round_add_photo_alternate_24));
//            }
//        }).start();

        runOnUiThread(()->{
            Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.round_add_photo_alternate_24) // Optional placeholder image
                    .into(iv_image);
        });
    }

    private void initializeViewItem() {
        iv_image = findViewById(R.id.iv_image);
        tv_price = findViewById(R.id.tv_price);
        tv_itemTitle = findViewById(R.id.tv_itemtitle);
        tv_itemDesc = findViewById(R.id.tv_itemdesc);
        tv_itemOwner = findViewById(R.id.tv_itemowner);
        tv_itemAdd = findViewById(R.id.tv_itemadd);
        btnRentItem = findViewById(R.id.btnRentItem);
        btnBackArrow = findViewById(R.id.btnBackArrow);

        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_View_Item_Details.this, Activity_RentItem.class);
                intent.putExtra("item_id", item_id);
                startActivity(intent);
            }
        });
    }
}