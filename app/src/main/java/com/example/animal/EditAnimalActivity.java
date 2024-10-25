package com.example.animal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class EditAnimalActivity extends AppCompatActivity {

    private EditText editAnimalName, editAnimalDescription;
    private ImageView editAnimalImage;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private String animalId;
    private boolean isEnglish;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        // Initialize views
        editAnimalName = findViewById(R.id.edit_animal_name);
        editAnimalDescription = findViewById(R.id.edit_animal_description);
        editAnimalImage = findViewById(R.id.edit_animal_image);
        saveButton = findViewById(R.id.save_animal_button);

        // Set larger font size for the animal name
        editAnimalName.setTextSize(24);  // Increased font size for animal name

        // Retrieve language preference
        isEnglish = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("isEnglish", false);

        // Determine correct Firebase node based on language
        String firebaseNode = isEnglish ? "animals-english" : "animals";
        databaseReference = FirebaseDatabase.getInstance("https://animalinfo-hit-2024-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference(firebaseNode);

        // Retrieve data passed from MainActivity
        Intent intent = getIntent();
        animalId = intent.getStringExtra("animalId");
        String animalName = intent.getStringExtra("animalName");
        String animalDescription = intent.getStringExtra("animalDescription");
        imageUrl = intent.getStringExtra("animalImage");

        // Set data to views
        editAnimalName.setText(animalName);
        editAnimalDescription.setText(formatDescription(animalDescription));  // Format description

        // Load the animal image into the ImageView and apply circular crop
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .apply(RequestOptions.circleCropTransform())  // Apply circular crop
                    .into(editAnimalImage);
        }

        // Handle save button click
        saveButton.setOnClickListener(v -> saveAnimal());
    }

    private void saveAnimal() {
        // Get the updated data from input fields
        String updatedName = editAnimalName.getText().toString().trim();
        String updatedDescription = editAnimalDescription.getText().toString().trim();

        // Ensure both fields are filled
        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedDescription)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the current animal data to preserve the image URL
        databaseReference.child(animalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Animal existingAnimal = dataSnapshot.getValue(Animal.class);
                if (existingAnimal != null) {
                    // Update only the description and keep the image URL intact
                    existingAnimal.setDescription(updatedDescription);
                    existingAnimal.setImage(imageUrl);  // Preserve the image URL
                    databaseReference.child(animalId).setValue(existingAnimal)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("updateSuccessful", true);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                } else {
                                    Toast.makeText(EditAnimalActivity.this, "Failed to update animal.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditAnimalActivity.this, "Failed to retrieve animal data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to format the description by adding new lines after each period
    private String formatDescription(String description) {
        return description.replace(". ", ".");
    }
}
