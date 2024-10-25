package com.example.animal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.util.Log;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnimalAdapter adapter;
    private List<Animal> animalList;
    private DatabaseReference databaseReference;
    private EditText searchBar;
    private boolean isEnglish;

    // Declare the ActivityResultLauncher
    private ActivityResultLauncher<Intent> editAnimalLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load language preference
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        isEnglish = preferences.getBoolean("isEnglish", false);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.search_bar);
        Button switchLanguageButton = findViewById(R.id.switch_language);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add the divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.recycler_view_divider);
        if (dividerDrawable != null) {
            dividerItemDecoration.setDrawable(dividerDrawable);
        }
        recyclerView.addItemDecoration(dividerItemDecoration);  // Apply the divider to the RecyclerView

        animalList = new ArrayList<>();
        adapter = new AnimalAdapter(animalList, this, new AnimalAdapter.OnAnimalClickListener() {
            @Override

            public void onAnimalClick(Animal animal) {
                Intent intent = new Intent(MainActivity.this, EditAnimalActivity.class);
                intent.putExtra("animalId", animal.getId());  // Pass the animal's unique Firebase ID
                intent.putExtra("animalName", animal.getName());  // Pass the current name
                intent.putExtra("animalDescription", animal.getDescription());  // Pass the current description
                intent.putExtra("animalImage", animal.getImage());  // Pass the image URL
                editAnimalLauncher.launch(intent);  // Launch EditAnimalActivity
            }

        });

        recyclerView.setAdapter(adapter);

        // Register the ActivityResultLauncher for editing an animal
        editAnimalLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getBooleanExtra("updateSuccessful", false)) {
                            // Reload data if the update was successful
                            loadFirebaseData(isEnglish ? "animals-english" : "animals");
                        }
                    }
                }
        );

        // Load Firebase data based on language preference
        loadFirebaseData(isEnglish ? "animals-english" : "animals");

        // Add search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());  // Filter animals as the user types in the search bar
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle language switching
        switchLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isEnglish) {
                    switchLanguage("he");  // Switch to Hebrew
                    isEnglish = false;
                    editor.putBoolean("isEnglish", false);
                    loadFirebaseData("animals");  // Load Hebrew animals data
                } else {
                    switchLanguage("en");  // Switch to English
                    isEnglish = true;
                    editor.putBoolean("isEnglish", true);
                    loadFirebaseData("animal-english");  // Load English animals data
                }
                editor.apply();  // Save language preference to SharedPreferences
            }
        });

    }

    // Load animal data from the Firebase Realtime Database
    private void loadFirebaseData(String node) {
        databaseReference = FirebaseDatabase.getInstance("https://animalinfo-hit-2024-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference(node);

        fetchAnimalData();
    }

    // Fetch animal data from Firebase Realtime Database and update the RecyclerView
    private void fetchAnimalData() {
        recyclerView.setVisibility(View.INVISIBLE);  // Hide RecyclerView while data is loading
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalList.clear();  // Clear the list to avoid duplicates
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Animal animal = dataSnapshot.getValue(Animal.class);
                    if (animal != null) {
                        // Set the animal's ID using the Firebase key
                        animal.setId(dataSnapshot.getKey());
                        animalList.add(animal);  // Add each animal to the list
                    }
                }

                adapter.notifyDataSetChanged();  // Notify adapter to refresh the RecyclerView
                recyclerView.setVisibility(View.VISIBLE);  // Make RecyclerView visible once data is loaded
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Error: " + error.getMessage());
            }
        });
    }

    // Filter the list of animals based on the search input
    private void filter(String text) {
        List<Animal> filteredList = new ArrayList<>();
        for (Animal animal : animalList) {
            if (animal.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(animal);
            }
        }
        adapter.updateList(filteredList);  // Update the adapter with the filtered list
    }

    // Method to switch language and restart the activity to apply changes
    private void switchLanguage(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        Intent intent = getIntent();
        finish();
        startActivity(intent);  // Restart the activity to apply language changes
    }
}
