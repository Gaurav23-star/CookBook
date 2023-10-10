package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements RecyclerViewInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        List<Item> items = new ArrayList<Item>();
        items.add(new Item("Yummy apple pie!", "joemama258",R.drawable.applepie,"2hrs", "Apple", "Pie Crust"));
        items.add(new Item("Garfield's Favorite Lasagna", "jarbuckle", R.drawable.lasagna, "1:30hrs", "Ground Beef", "Lasagna Noodles"));
        items.add(new Item("Grandma's Tortilla Española", "smaye", R.drawable.tortilla, "45mins", "Eggs", "Potatoes"));
        items.add(new Item("One Pot Sausage and Vegs", "smaye", R.drawable.onepot, "30mins", "Sausage", "Potatoes"));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items,this));
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(HomeActivity.this, "On Click listener works",Toast.LENGTH_SHORT).show();
    }
}