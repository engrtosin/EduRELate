package com.codepath.edurelate.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.databinding.ItemPostBinding;
import com.codepath.edurelate.models.Comment;
import com.codepath.edurelate.models.Post;
import com.codepath.edurelate.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public static final String TAG = "PostAdapter";

    Context context;
    List<Post> posts;
    PostAdapterListener mListener;

    /* ------------------ LISTENER ------------------------- */
    public interface PostAdapterListener {
        void postClicked(Post post);
    }

    public void setAdapterListener(PostAdapterListener listener) {
        mListener = listener;
    }

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void add(int position, Post post) {
        posts.add(position,post);
        notifyItemInserted(position);
    }

    public void addAll(int position, List<Post> objects) {
        posts.addAll(position,objects);
        notifyDataSetChanged();
    }

    /* ---------------------- RECYCLER VIEW ADAPTER METHODS -------------------------- */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /* ---------------------- CUSTOM VIEWHOLDER -------------------------- */
    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding binding;
        Post post;
        Comment comment;

        public ViewHolder(@NonNull @NotNull ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());
            binding = itemPostBinding;
            setClickListener();
        }

        private void setClickListener() {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.postClicked(post);
                }
            });
        }

        public void bind(Post post) {
            this.post = post;
            binding.tvPostTitle.setText(post.getTitle());
            binding.tvPostBody.setText(post.getBody(false));
            String fullName = "<b>" + User.getFullName(post.getPostOwner()) + "</b>";
            String createdAt = post.getCreatedAt().toString();
            String[] dateList = createdAt.split(" ");
            createdAt =  dateList[0] + " " + dateList[1] + " " + dateList[2] + ", " + dateList[5] +
            " | " + dateList[3].substring(0,5);
            String info = "Post by " + fullName + " | " + createdAt;
            binding.tvPostInfo.setText(Html.fromHtml(info));
            comment = post.getTopRatedComment();
            if (comment != null) {
                binding.rlTopComment.setVisibility(View.VISIBLE);
                binding.tvCommentBody.setText(comment.getBody(false));
            }
        }
    }
}
