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
        items.add(new Item("Yummy apple pie!", "jmama258",R.drawable.applepie));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items,this));
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(HomeActivity.this, "On Click listener works",Toast.LENGTH_SHORT).show();
    }
}