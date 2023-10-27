package com.cookbook.Settings;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.R;
import com.cookbook.RecyclerViewInterface;

public class SettingsViewHolder extends RecyclerView.ViewHolder {

    TextView settingsItemNameTextView;

    public SettingsViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        settingsItemNameTextView = itemView.findViewById(R.id.settingsItemTextView);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewInterface!=null){
                    int position = getBindingAdapterPosition();


                    if(position!= RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            }
        });

    }


}
