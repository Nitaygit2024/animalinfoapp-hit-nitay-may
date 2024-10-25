package com.example.animal;

public class Animal {
    private String id;
    private String name;
    private String description;
    private String image;  // Add the image field

    // Default constructor required for calls to DataSnapshot.getValue(Animal.class)
    public Animal() {
    }

    // Constructor to initialize all fields
    public Animal(String id, String name, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;  // Initialize the image field
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
