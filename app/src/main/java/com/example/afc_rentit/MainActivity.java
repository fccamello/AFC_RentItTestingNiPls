package com.example.afc_rentit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.afc_rentit.Database.DatabaseManager;
import com.example.afc_rentit.Database.SQLConnection;
import com.example.afc_rentit.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton createPost;
    DatabaseManager dbManager = DatabaseManager.getInstance();
    private final HomeFragment homeFragment = new HomeFragment();
    private final DashboardFragment dashboardFragment = new DashboardFragment();
    private final NotificationFragment notificationFragment = new NotificationFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    Current_User current_user = Current_User.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(homeFragment);
            } else if (itemId == R.id.dashboard) {
                replaceFragment(dashboardFragment);
            } else if (itemId == R.id.notifications) {
                replaceFragment(notificationFragment);
            } else if (itemId == R.id.profile) {
                replaceFragment(profileFragment);
            } else {
                return false;
            }

            return true;

        });

        createPost = findViewById(R.id.createPost);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity_create_post.class);
                startActivity(intent);
            }
        });

        checkOwnerStatus();

    }



    private void checkOwnerStatus() {
        int userId = current_user.getUser_id(); // Assuming currentUser is an instance variable or accessible through other means

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pStmt = conn.prepareStatement(
                         "SELECT isOwner FROM tbluser WHERE user_id = ?"
                 )) {
                pStmt.setInt(1, userId);

                ResultSet resultSet = pStmt.executeQuery();
                if (resultSet.next()) {
                    int isOwner = resultSet.getInt("isOwner");
                    if (isOwner == 0) {
                        // If the user is not an owner, hide the floating action button
                        runOnUiThread(() -> createPost.setVisibility(View.GONE));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    private void replaceFragment(Fragment fragment) {
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}