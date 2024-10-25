package com.example.animal;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AnimalDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        TextView animalNameTextView = findViewById(R.id.animal_name_detail);
        TextView animalDescriptionTextView = findViewById(R.id.animal_description_detail);

        // Get animal data from the intent
        String animalName = getIntent().getStringExtra("animal_name");
        String animalDescription = getIntent().getStringExtra("animal_description");

        // Set data to the views
        animalNameTextView.setText(animalName);
        animalDescriptionTextView.setText(animalDescription);
    }
}
