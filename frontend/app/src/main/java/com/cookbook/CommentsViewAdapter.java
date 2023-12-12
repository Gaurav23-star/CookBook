package com.cookbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.model.Comment;

import java.util.List;

public class CommentsViewAdapter extends RecyclerView.Adapter<CommentsViewAdapter.CommentsViewHolder> {

    Context context;
    List<Comment> comments;
    RecyclerViewInterface recyclerViewInterface;

    // Constructor for the CommentsViewAdapter class
    public CommentsViewAdapter(Context context, List<Comment> comments, RecyclerViewInterface listener) {
        this.context = context;
        this.comments = comments;
        this.recyclerViewInterface = listener;
    }


    // ViewHolder class for individual comments
    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView commentContent;
        private final ImageView avatar;

        // Constructor for CommentsViewHolder
        public CommentsViewHolder(@NonNull View itemView, RecyclerViewInterface listener) {
            super(itemView);
            username = itemView.findViewById(R.id.userName);
            commentContent = itemView.findViewById(R.id.comment);
            avatar = itemView.findViewById(R.id.commentsAvatar);

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getBindingAdapterPosition());
                }
            });
        }

        // Getter methods for the views in the ViewHolder
        public TextView getUsername() {
            return username;
        }

        public TextView getCommentContent() {
            return commentContent;
        }

        public ImageView getAvatar() {
            return avatar;
        }
    }

    // Create a new ViewHolder instance when needed
    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentsViewHolder(LayoutInflater.from(context).inflate(R.layout.comments_view, parent, false), this.recyclerViewInterface);
    }

    // Bind data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        holder.getUsername().setText(comments.get(position).getUsername());
        holder.getCommentContent().setText(comments.get(position).getComment());
        holder.getAvatar().setImageResource(R.drawable.baseline_person_24);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

}
