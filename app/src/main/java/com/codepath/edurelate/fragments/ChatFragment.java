package com.codepath.edurelate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.MessagesAdapter;
import com.codepath.edurelate.databinding.FragmentChatBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";

    FragmentChatBinding binding;
    private View rootView;
    Group group;
    Member member;
    MessagesAdapter adapter;
    List<Message> messages;
    List<String> messageIds;
    Message newReplyTo;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(Group group) {
        ChatFragment fragment = new ChatFragment();
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
        binding = FragmentChatBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();

        messages = new ArrayList<>();
        messageIds = new ArrayList<>();
        adapter = new MessagesAdapter(getContext(),messages);
        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapterListener();
        queryMessages();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /* ------------------------- PARSE METHODS -------------------------------- */
    public void queryMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo(Message.KEY_GROUP,group);
        query.include(Message.KEY_SENDER);
        query.include(Message.KEY_USERS_LIKING_THIS);
        query.include(Message.KEY_REPLY_TO);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while quering messages for group (" + group.getObjectId() + "): " + e.getMessage(),e);
                }
                adapter.addAll(objects);
                binding.rvMessages.smoothScrollToPosition(messages.size());
                updateMessageIds(objects);
            }
        });
    }

    private void updateMessageIds(List<Message> messages) {
        for (int i = 0; i < messages.size(); i++) {
            messageIds.add(messages.get(i).getObjectId());
        }
    }

    /* ------------------------- ADAPTER LISTENER METHOD -------------------------------- */
    private void setAdapterListener() {
        adapter.setAdapterListener(new MessagesAdapter.MessagesAdapterInterface() {
            @Override
            public void senderClicked(ParseUser sender) {

            }

            @Override
            public void replyClicked(Message replyTo) {
                Log.i(TAG,"Reply position: " + messageIds.indexOf(replyTo.getObjectId()));
                binding.rvMessages.smoothScrollToPosition(messageIds.indexOf(replyTo.getObjectId()));
            }

            @Override
            public void onDoubleTap(Message message) {
                binding.rvMessages.smoothScrollToPosition(messages.size());
                newReplyTo = message;
                bindNewReply();
            }
        });
    }

    private void bindNewReply() {
        binding.vDemarcate1.setVisibility(View.VISIBLE);
        binding.rlReply.setVisibility(View.VISIBLE);
        binding.tvReplySender.setText(User.getFullName(newReplyTo.getSender()));
        binding.tvReplyMsg.setText(newReplyTo.getBody(false));
    }
}