package com.example.afc_rentit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class activity_create_post extends AppCompatActivity {

    private ImageView productImage;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private CheckBox checkBoxEducation;
    private CheckBox checkBoxEntertainment;
    private CheckBox checkBoxElectronics;
    private EditText editTextPrice;
    private Button buttonSubmit;

    //https://www.youtube.com/watch?v=nOtlFl1aUCw uploading image function
    private final ActivityResultLauncher<Intent> selectImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        if (selectedImageUri != null) {
                            productImage.setImageURI(selectedImageUri);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        productImage = findViewById(R.id.product_image);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        checkBoxEducation = findViewById(R.id.checkbox_education);
        checkBoxEntertainment = findViewById(R.id.checkbox_entertainment);
        checkBoxElectronics = findViewById(R.id.checkbox_electronics);
        editTextPrice = findViewById(R.id.edit_text_price);
        buttonSubmit = findViewById(R.id.button_submit);

        Button buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSelectImage.setOnClickListener(v -> selectImageFromGallery());

        buttonSubmit.setOnClickListener(v -> submitPost());
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageResultLauncher.launch(intent);
    }

    private void submitPost() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        boolean isEducation = checkBoxEducation.isChecked();
        boolean isEntertainment = checkBoxEntertainment.isChecked();
        boolean isElectronics = checkBoxElectronics.isChecked();
        String price = editTextPrice.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform the submit action (e.g., save to database, send to server)

        Toast.makeText(this, "Post submitted successfully!", Toast.LENGTH_SHORT).show();

        // Clear the form or navigate away
        clearForm();
    }

    private void clearForm() {
        productImage.setImageResource(R.drawable.round_add_photo_alternate_24);
        editTextTitle.setText("");
        editTextDescription.setText("");
        checkBoxEducation.setChecked(false);
        checkBoxEntertainment.setChecked(false);
        checkBoxElectronics.setChecked(false);
        editTextPrice.setText("");
    }
}