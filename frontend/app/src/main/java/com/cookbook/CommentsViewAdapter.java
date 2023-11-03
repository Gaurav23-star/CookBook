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

    public CommentsViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }


    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView commentContent;
        private final ImageView avatar;
        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userName);
            commentContent = itemView.findViewById(R.id.comment);
            avatar = itemView.findViewById(R.id.commentsAvatar);
        }

        public TextView getUsername() {
            return username;
        }

        public TextView getCommentContent() {
            return commentContent;
        }

        public ImageView getAvatar(){
            return avatar;
        }
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentsViewHolder(LayoutInflater.from(context).inflate(R.layout.comments_view, parent, false));
    }

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
