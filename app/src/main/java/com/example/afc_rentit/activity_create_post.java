package com.example.afc_rentit;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.afc_rentit.Current_User;
import com.example.afc_rentit.Database.SQLConnection;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class activity_create_post extends AppCompatActivity {
    Current_User current_user = Current_User.getInstance();

    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storagereference;
    private ImageView createImage;
    private EditText createTitle, createDescription, createPrice;
    private CheckBox catEducation, catElectronics, catEntertainment;
    private Button buttonSubmit;
    private String uploadedImageUrl;

    // Activity result launcher for selecting an image
    private ActivityResultLauncher<Intent> selectImageResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        createImage = findViewById(R.id.createImage);
        createTitle = findViewById(R.id.createTitle);
        createDescription = findViewById(R.id.createDesc);
        catEducation = findViewById(R.id.catEducation);
        catElectronics = findViewById(R.id.catElectronics);
        catEntertainment = findViewById(R.id.catEntertainment);
        createPrice = findViewById(R.id.createPrice);
        buttonSubmit = findViewById(R.id.btnSave);

        storage = FirebaseStorage.getInstance();
        storagereference = storage.getReference();

        // Register the ActivityResultLauncher
        selectImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            if (imageUri != null) {
                                createImage.setImageURI(imageUri);
                                uploadPicture(imageUri);
                            } else {
                                Toast.makeText(activity_create_post.this, "No image selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        createImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        buttonSubmit.setOnClickListener(v -> submitPost());
    }

    private void choosePicture() {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        selectImageResultLauncher.launch(photoPicker);
    }

    private void uploadPicture(Uri imageUri) {
        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storagereference.child("images/" + randomKey);
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadedImageUrl = uri.toString();
                                Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(activity_create_post.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    private void submitPost() {
        String title = createTitle.getText().toString().trim();
        String description = createDescription.getText().toString().trim();
        boolean isEducation = catEducation.isChecked();
        boolean isEntertainment = catEntertainment.isChecked();
        boolean isElectronics = catElectronics.isChecked();
        String price = createPrice.getText().toString().trim();
        double dprice = Double.parseDouble(price);

        if (title.isEmpty() || description.isEmpty() || price.isEmpty() || uploadedImageUrl == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder categoriesBuilder = new StringBuilder();
        if (isEducation) categoriesBuilder.append("Education,");
        if (isElectronics) categoriesBuilder.append("Electronics,");
        if (isEntertainment) categoriesBuilder.append("Entertainment,");
        String categories = categoriesBuilder.toString();
        if (!categories.isEmpty()) categories = categories.substring(0, categories.length() - 1);
        else {
            Toast.makeText(this, "Please select at least one category", Toast.LENGTH_SHORT).show();
            return;
        }

        insertPost(title, description, uploadedImageUrl, categories, dprice, true);
        Toast.makeText(this, "Post submitted successfully!", Toast.LENGTH_SHORT).show();
        clearForm();
    }

    private void clearForm() {
        createImage.setImageResource(R.drawable.round_add_photo_alternate_24);
        createTitle.setText("");
        createDescription.setText("");
        catEducation.setChecked(false);
        catEntertainment.setChecked(false);
        catElectronics.setChecked(false);
        createPrice.setText("");
    }

    public void insertPost(String title, String description, String image, String category, double price, boolean isAvailable) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection c = SQLConnection.getConnection()) {

                System.out.println("USER ID: " + current_user.getUser_id());

                String query = "INSERT INTO tblItem (user_id, title, description, image, category, price, isAvailable) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = c.prepareStatement(query)) {
                    statement.setInt(1, current_user.getUser_id());
                    statement.setString(2, title);
                    statement.setString(3, description);
                    statement.setString(4, image);
                    statement.setString(5, category);
                    statement.setDouble(6, price);
                    statement.setInt(7, isAvailable ? 1 : 0);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Post inserted successfully.");
                    } else {
                        System.out.println("Failed to insert post.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
