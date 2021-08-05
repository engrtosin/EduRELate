package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.PostAdapter;
import com.codepath.edurelate.databinding.FragmentDiscussionsBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscussionsFragment extends Fragment {

    public static final String TAG = "DiscussionsFragment";

    FragmentDiscussionsBinding binding;
    View rootView;
    Group group;
    List<Post> posts;
    PostAdapter adapter;

    public DiscussionsFragment() {
        // Required empty public constructor
    }

    public static DiscussionsFragment newInstance(Group group) {
        DiscussionsFragment fragment = new DiscussionsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Group.KEY_GROUP,group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = getArguments().getParcelable(Group.KEY_GROUP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDiscussionsBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posts = new ArrayList<>();
        adapter = new PostAdapter(getContext(),posts);
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration decoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rvPosts.addItemDecoration(decoration);
        queryPosts();
        setPostsAdapterListener();
        setClickListeners();
    }

    private void setClickListeners() {
        binding.llNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.llNewPost.setVisibility(View.GONE);
                binding.rlNewPost.setVisibility(View.VISIBLE);
            }
        });
        binding.ivCancelNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etNewPostBody.setText("");
                binding.etNewPostTopic.setText("");
                binding.llNewPost.setVisibility(View.VISIBLE);
                binding.rlNewPost.setVisibility(View.GONE);
            }
        });
        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        String topic = binding.etNewPostTopic.getText().toString();
        if (topic.isEmpty()) {
            Toast.makeText(getContext(), "Topic cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String body = binding.etNewPostBody.getText().toString();
        if (body.isEmpty()) {
            Toast.makeText(getContext(), "Body cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        savePost(topic,body);
        binding.llNewPost.setVisibility(View.VISIBLE);
        binding.rlNewPost.setVisibility(View.GONE);
    }

    private void savePost(String topic, String body) {
        Post post = Post.newPost(group, topic, body);
        adapter.add(0,post);
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_POST_OWNER);
        query.include(Post.KEY_TOP_RATED_COMMENT);
        query.whereEqualTo(Post.KEY_GROUP,group);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying for posts: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,objects.size() + " posts");
                adapter.addAll(posts.size(),objects);
            }
        });
    }

    public void setPostsAdapterListener() {
        adapter.setAdapterListener(new PostAdapter.PostAdapterListener() {
            @Override
            public void postClicked(Post post) {
                goToPostDetails(post);
            }
        });
    }

    private void goToPostDetails(Post post) {
        binding.rlDiscussions.setVisibility(View.GONE);
        binding.flChildFragment.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        PostDetailsFragment fragment = PostDetailsFragment.newInstance(post);
        fragment.setFragListener(new SearchChatsFragment.SearchFragInterface() {
            @Override
            public void fragmentClosed() {
                binding.rlDiscussions.setVisibility(View.VISIBLE);
                binding.flChildFragment.setVisibility(View.GONE);
            }
        });
        ft.replace(R.id.flChildFragment,fragment);
        ft.commit();
    }
}