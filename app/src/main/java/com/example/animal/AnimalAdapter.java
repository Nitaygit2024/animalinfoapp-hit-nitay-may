package com.example.animal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animalList;
    private Context context;
    private OnAnimalClickListener listener;  // Callback interface for handling clicks

    // Define a callback interface to handle item clicks
    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    // Modify the constructor to accept a listener for handling item clicks
    public AnimalAdapter(List<Animal> animalList, Context context, OnAnimalClickListener listener) {
        this.animalList = animalList;
        this.context = context;
        this.listener = listener;  // Initialize the listener
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_list_item, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = animalList.get(position);

        // Set the animal name and description
        holder.animalNameTextView.setText(animal.getName());
        holder.animalDescriptionTextView.setText(animal.getDescription());

        // Load and display image using Glide, making the image circular
        if (animal.getImage() != null) {
            Glide.with(context)
                    .load(animal.getImage())
                    .apply(RequestOptions.circleCropTransform())  // Make the image circular
                    .placeholder(R.drawable.silhouetteprofile)  // Set placeholder
                    .into(holder.animalImageView);
        } else {
            // Set a default image if no image is available
            holder.animalImageView.setImageResource(R.drawable.silhouetteprofile);
        }

        // Handle item click and trigger the callback to MainActivity
        holder.itemView.setOnClickListener(v -> listener.onAnimalClick(animal));
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        TextView animalNameTextView;
        TextView animalDescriptionTextView;
        ImageView animalImageView;  // Add an ImageView for animal image

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTextView = itemView.findViewById(R.id.animal_name);
            animalDescriptionTextView = itemView.findViewById(R.id.animal_description);  // Initialize description TextView
            animalImageView = itemView.findViewById(R.id.animal_image);  // Initialize ImageView
        }
    }

    public void updateList(List<Animal> updatedList) {
        animalList = updatedList;
        notifyDataSetChanged();  // Refresh the RecyclerView
    }
}
