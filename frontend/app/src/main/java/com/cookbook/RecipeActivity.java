package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        currentUser = (User) getIntent().getSerializableExtra("current_user");
        setContentView(R.layout.activity_recipe);
        ImageView recipePicture = findViewById(R.id.recipeView);
        TextView titleView = findViewById(R.id.titleView);
        TextView nameView = findViewById(R.id.nameView);
        TextView ingredientsView = findViewById(R.id.ingredientsView);
        TextView instructionsView = findViewById(R.id.instructionsView);
        TextView descriptionView = findViewById(R.id.descriptionView);
        recipePicture.setImageResource(R.drawable.foodplaceholder);
        titleView.setText(currentRecipe.getRecipe_name());
        nameView.setText(Integer.toString(currentRecipe.getUser_id()));
        ingredientsView.setText(currentRecipe.getIngredients());
        instructionsView.setText(currentRecipe.getInstructions());
        descriptionView.setText(currentRecipe.getDescription());

    }
    @Override
    public void onUserInteraction() {
        changeActivityToUserHome(currentUser);
        super.onUserInteraction();
    }

    private void changeActivityToUserHome(User user){
        final Intent intent = new Intent(RecipeActivity.this, HomeActivity.class);
        intent.putExtra("current_user", user);
        startActivity(intent);
        finish();
    }
}