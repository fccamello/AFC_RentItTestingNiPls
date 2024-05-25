package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afc_rentit.Database.SQLConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Activity_RentItem extends AppCompatActivity {
    TextView tv_rentOwner, tv_rentItem, tv_rentDesc, tv_totalAmount, tv_rentPrice;
    EditText et_rentDate, et_rentDuration;
    RadioGroup rdgDurationCat;
    RadioButton[] radioButtons;
    Spinner spnr_delivery;
    ImageButton btnBackArrow;
    Button btnConfirmRent;
    ImageView iv_itemImage;
    double price, totalAmount = 0.00;
    int totalDays = 0, s_day, s_month, s_year;
    String owner, item_title, item_desc, imageUrl;
    int item_id, rent_id = -1;
    Current_User current_user = Current_User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item);

        item_id = getIntent().getIntExtra("item_id", -1);

        initializeViews();
    }

    private void sendRentRequest() {
        if (et_rentDate.length() == 0 || et_rentDuration.length() == 0
                || rdgDurationCat.getCheckedRadioButtonId() == -1 ){
            return;
        }

        String modeDelivery = spnr_delivery.getSelectedItem().toString();
        System.out.println("mode of delivery : " + modeDelivery);

        LocalDate start_date = LocalDate.of(s_year, s_month, s_day);
        LocalDate end_date = start_date.plusDays(totalDays);

        String category = ((RadioButton) findViewById(rdgDurationCat.getCheckedRadioButtonId())).getText().toString();
        int duration = Integer.parseInt(et_rentDuration.getText().toString());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection conn = SQLConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO tblRentRequest (item_id, user_id, requestDate, durationCategory, duration, endRentDate, modeOfDelivery, totalAmount)" +
                                "VALUES (?,?,?,?,?,?,?,?)"
                )) {

                pstmt.setInt(1, item_id);
                pstmt.setInt(2, current_user.getUser_id());
                pstmt.setDate(3, java.sql.Date.valueOf(start_date.toString()));
                pstmt.setString(4, category);
                pstmt.setInt(5, duration);
                pstmt.setDate(6, java.sql.Date.valueOf(end_date.toString()));
                pstmt.setString(7, modeDelivery);
                pstmt.setDouble(8, totalAmount);

                int res = pstmt.executeUpdate();

                if (res == 1){
                    runOnUiThread(()->{
                        Toast.makeText(this, "Request sent to rent " + item_title + "!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();            }
        });
    }

    private void initializeViews() {
        tv_rentOwner = findViewById(R.id.tv_rentOwner);
        tv_rentItem = findViewById(R.id.tv_rentItem);
        tv_rentDesc = findViewById(R.id.tv_rentDesc);
        tv_totalAmount = findViewById(R.id.tv_totalAmount);
        tv_rentPrice = findViewById(R.id.tv_rentPrice);
        et_rentDate = findViewById(R.id.et_rentDate);
        et_rentDuration = findViewById(R.id.et_rentDuration);
        rdgDurationCat = findViewById(R.id.rdg_durationCategory);
        spnr_delivery = findViewById(R.id.spnr_delivery);

        iv_itemImage = findViewById(R.id.iv_rentImage);
        btnBackArrow = findViewById(R.id.btnBackArrowRent);
        btnConfirmRent = findViewById(R.id.btnConfirmRent);

        radioButtons = new RadioButton[]{
                findViewById(R.id.rd_day), findViewById(R.id.rd_week), findViewById(R.id.rd_month)
        };

        radioButtons[0].setChecked(true);

        btnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConfirmRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRentRequest();
            }
        });

        getItemDetails();
        et_rentDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_rentDuration.length() == 0){
                    totalDays = 0;
                } else {
                    totalDays = Integer.parseInt(et_rentDuration.getText().toString());
                }

                String type = ((RadioButton) findViewById(rdgDurationCat.getCheckedRadioButtonId())).getText().toString();
                changeTvTotalAmount(type);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        for (RadioButton r: radioButtons) {
            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (et_rentDuration.getText().toString().equals("")) {
                        return;
                    }

                    totalDays = Integer.parseInt(et_rentDuration.getText().toString());
                    String cat = r.getText().toString();

                    changeTvTotalAmount(cat);
                }
            });
        }

        et_rentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(Activity_RentItem.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth += 1;
                        String date = selectedyear + "-" + selectedmonth + "-" + selectedday;
                        et_rentDate.setText(date);

                        s_day = selectedday;
                        s_month = selectedmonth;
                        s_year = selectedyear;
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });
    }

    private void changeTvTotalAmount(String durationType){
        switch (durationType) {
            case "months":
                totalDays *= 30;
                break;
            case "weeks":
                totalDays *= 7;
                break;
        }

        totalAmount = price * totalDays;

        tv_totalAmount.setText(String.valueOf(totalAmount));
        System.out.println("Total Amount : " + totalAmount);
    }

    private void displayItem(){
        tv_rentOwner.setText(owner);
        tv_rentItem.setText(item_title);
        tv_rentDesc.setText(item_desc);
        tv_rentPrice.setText(String.valueOf(price));
    }



    private void getItemDetails() {
        if (item_id == -1){
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement ItemStmt = conn.prepareStatement(
                         "SELECT title, description, price, image, username, address " +
                                 "FROM tblItem as i, tblUser as u " +
                                 "WHERE i.user_id = u.user_id AND item_id = ?"
                 );
                 PreparedStatement rentStmt = conn.prepareStatement(
                         "SELECT rent_id FROM tblRentRequest WHERE item_id = ? AND user_id = ?"
                 )) {

                ItemStmt.setInt(1, item_id);

                ResultSet res1 = ItemStmt.executeQuery();

                boolean res1Success = false;

                if (res1.next()){
                     imageUrl = res1.getString("image");
                    price = res1.getDouble("price");
                    item_title = res1.getString("title");
                    item_desc = res1.getString("description");
                    owner = res1.getString("username");


                    // FOR THE IMAGE
                    new Thread(() -> {
                        try {
                            URL url = new URL(imageUrl);
                            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            runOnUiThread(() -> iv_itemImage.setImageBitmap(bitmap));
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Optionally set a placeholder or error image
                            runOnUiThread(() -> iv_itemImage.setImageResource(R.drawable.round_add_photo_alternate_24));
                        }
                    }).start();

                    res1Success = true;
                }

                rentStmt.setInt(1, item_id);
                rentStmt.setInt(2, current_user.getUser_id());

                ResultSet res2 = rentStmt.executeQuery();

                if (res2.next()){
                    rent_id = res2.getInt("rent_id");
                }

                if (res1Success){
                    runOnUiThread(()->{
                        displayItem();

                        if (rent_id != -1){
//                            rentDisabled();
                            Toast.makeText(this, "You have already requested to rent this item. Please wait for confirmation.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void rentDisabled() {
        spnr_delivery.setEnabled(false);
        btnConfirmRent.setClickable(false);
        et_rentDate.setEnabled(false);
        et_rentDuration.setClickable(false);
        et_rentDuration.setFocusable(false);

        for (RadioButton r : radioButtons){
            r.setClickable(false);
        }
    }
}