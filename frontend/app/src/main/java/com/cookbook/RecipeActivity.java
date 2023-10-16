package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookbook.model.Recipe;
import com.cookbook.model.User;

import org.w3c.dom.Text;

public class RecipeActivity extends AppCompatActivity {
    private static User currentUser;
    private static Recipe currentRecipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRecipe = (Recipe) getIntent().getSerializableExtra("current_recipe");
        setContentView(R.layout.activity_recipe);
        ImageView recipePicture = findViewById(R.id.recipeView);
        TextView titleView = findViewById(R.id.titleView);
        TextView nameView = findViewById(R.id.nameView);
        TextView ingredientsView = findViewById(R.id.ingredientsView);
        TextView instructionsView = findViewById(R.id.instructionsView);
        recipePicture.setImageResource(R.drawable.applepie);
        titleView.setText(currentRecipe.getRecipe_name());
        nameView.setText(currentRecipe.getUser_id());
        ingredientsView.setText(currentRecipe.getIngredients());
        instructionsView.setText(currentRecipe.getInstructions());

    }
}