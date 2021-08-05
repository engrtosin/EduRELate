package com.codepath.edurelate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.databinding.ItemCommentBinding;
import com.codepath.edurelate.models.Comment;
import com.codepath.edurelate.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    public static final String TAG = "CommentAdapter";

    Context context;
    List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    /* -------------------- RECYCLERVIEW VIEWHOLDER METHODS ----------------------- */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void add(int position, Comment comment) {
        comments.add(position,comment);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemCommentBinding binding;
        Comment comment;

        public ViewHolder(@NonNull @NotNull ItemCommentBinding itemCommentBinding) {
            super(itemCommentBinding.getRoot());
            binding = itemCommentBinding;
            setClickListeners();
        }

        private void setClickListeners() {
            binding.ivUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comment.upvote();
                    binding.tvVoteCount.setText(comment.getVoteCountString());
                }
            });
            binding.ivDownvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comment.downvote();
                    binding.tvVoteCount.setText(comment.getVoteCountString());
                }
            });
        }

        public void bind(Comment comment) {
            this.comment = comment;
            binding.tvCommentBody.setText(comment.getBody(true));
            binding.tvVoteCount.setText(comment.getVoteCountString());
            binding.tvUser.setText(User.getFullName(comment.getCommentOwner()));
        }
    }
}
