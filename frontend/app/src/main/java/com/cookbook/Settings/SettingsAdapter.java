package com.cookbook.Settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cookbook.R;
import com.cookbook.RecyclerViewInterface;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsViewHolder> {
    Context context;
    private RecyclerViewInterface recyclerViewInterface;
    List<SettingsItem> items;

    public SettingsAdapter(Context context, List<SettingsItem> items, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingsViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_item_view,parent,false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        holder.settingsItemNameTextView.setText(items.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
