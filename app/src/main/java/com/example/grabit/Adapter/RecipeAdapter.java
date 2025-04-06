package com.example.grabit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Model.Recipe;
import com.example.grabit.R;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeImage.setImageResource(recipe.getImage());
        holder.recipeTime.setText(recipe.getTime() + " min");
        holder.recipeDifficulty.setText(recipe.getDifficulty());
        holder.recipeCalories.setText(recipe.getCalories());
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName;
        TextView recipeDescription;
        TextView recipeTime;
        TextView recipeDifficulty;
        TextView recipeCalories;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            recipeTime = itemView.findViewById(R.id.recipeTime);
            recipeDifficulty = itemView.findViewById(R.id.recipeDifficulty);
            recipeCalories = itemView.findViewById(R.id.recipeCalories);
        }
    }
} 