package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements RecyclerViewInterface{
    boolean admin=false;
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
        MyAdapter adapter = new MyAdapter(getApplicationContext(),items,this);
        //find way to hide admin
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(int position) {
        if (!admin) {
            Toast.makeText(HomeActivity.this, "user this should open a page eventually ", Toast.LENGTH_SHORT).show();
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.admin_dialog);
            final Button banUser = dialog.findViewById(R.id.banUser);
            final Button banButton = dialog.findViewById(R.id.banButton);

            dialog.show();
        }
    }
}