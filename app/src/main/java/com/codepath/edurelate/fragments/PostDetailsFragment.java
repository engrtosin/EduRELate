package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.adapters.CommentAdapter;
import com.codepath.edurelate.databinding.FragmentPostDetailsBinding;
import com.codepath.edurelate.models.Comment;
import com.codepath.edurelate.models.Post;
import com.codepath.edurelate.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsFragment extends Fragment {

    public static final String TAG = "PostDetailsFragment";

    FragmentPostDetailsBinding binding;
    View rootView;
    Post post;
    List<Comment> comments;
    CommentAdapter adapter;
    SearchChatsFragment.SearchFragInterface mListener;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    public static PostDetailsFragment newInstance(Post post) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Post.TAG,post);
        fragment.setArguments(args);
        return fragment;
    }

    public void setFragListener(SearchChatsFragment.SearchFragInterface listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = getArguments().getParcelable(Post.TAG);
            comments = new ArrayList<>();
            queryComments();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostDetailsBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new CommentAdapter(getContext(),comments);
        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        binding.rvComments.addItemDecoration(decoration);
        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        binding.tvPostTitle.setText(post.getTitle());
        binding.tvPostBody.setText(post.getBody(true));
        binding.tvPostInfo.setText(getPostInfo());
    }

    private Spanned getPostInfo() {
        String fullName = "<b>" + User.getFullName(post.getPostOwner()) + "</b>";
        String createdAt = post.getCreatedAt().toString();
        String[] dateList = createdAt.split(" ");
        createdAt =  dateList[0] + " " + dateList[1] + " " + dateList[2] + ", " + dateList[5] +
                " | " + dateList[3].substring(0,5);
        String info = "Post by " + fullName + " | " + createdAt;
        return Html.fromHtml(info);
    }

    private void setClickListeners() {
        binding.ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fragmentClosed();
            }
        });
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_COMMENT_OWNER);
        query.whereEqualTo(Comment.KEY_TO_POST,post);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying for comments for post: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,objects.size() + " comments");
                comments.addAll(objects);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}