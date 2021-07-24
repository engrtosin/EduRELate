package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.MessagesAdapter;
import com.codepath.edurelate.databinding.ActivityAllChatsBinding;
import com.codepath.edurelate.databinding.ActivityChatBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";

    ActivityChatBinding binding;
    Group group;
    Member member;
    List<Message> messages;
    List<String> messageIds;
    ParseUser friend;
    MessagesAdapter adapter;
    MessagesAdapter.MessagesAdapterInterface adapterListener;
    Message newReplyTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.member = Parcels.unwrap(getIntent().getParcelableExtra(Member.KEY_MEMBER));

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        messages = new ArrayList<>();
        messageIds = new ArrayList<>();
        adapter = new MessagesAdapter(this,messages);
        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        setAdapterListener();
        queryMessages();
        Log.i(TAG,"messages size: "+messages.size());

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        Log.i(TAG,"initializing for group: " + member.getGroup().getObjectId());
        if (this.member.getIsFriendGroup()) {
            glideFriendPic();
        }
        else {
            glideGroupPic();
        }
    }

    private void glideFriendPic() {
        ParseUser friend = member.getFriend();
        binding.tvActivityTitle.setText(User.getFullName(friend));
        ParseFile image = friend.getParseFile(User.KEY_USER_PIC);
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(binding.ivChatPic);
        }
    }

    private void glideGroupPic() {
        Group group = member.getGroup();
        binding.tvActivityTitle.setText(group.getGroupName());
        if (group.has(Group.KEY_GROUP_PIC)) {
            ParseFile image = group.getParseFile(Group.KEY_GROUP_PIC);
            if (image != null) {
                Glide.with(this).load(image.getUrl()).into(binding.ivChatPic);
            }
        }
    }

    private void setClickListeners() {
        binding.ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.ivSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = binding.etNewMessage.getText().toString();
                if (body == null) {
                    Toast.makeText(ChatActivity.this,"Body cannot be empty.",Toast.LENGTH_SHORT).show();
                    return;
                }
                Message newMessage = group.sendNewMessage(body,newReplyTo);
                messages.add(messages.size(),newMessage);
                Log.i(TAG,"messages size: "+messages.size());
                adapter.notifyDataSetChanged();
                binding.rvMessages.smoothScrollToPosition(messages.size()-1);
                binding.etNewMessage.setText("");
                newReplyTo = null;
                binding.vDemarcate1.setVisibility(View.GONE);
                binding.rlReply.setVisibility(View.GONE);
            }
        });
        binding.rlReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.rvMessages.smoothScrollToPosition(messages.indexOf(newReplyTo));
            }
        });
        binding.ivCancelReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newReplyTo = null;
                binding.vDemarcate1.setVisibility(View.GONE);
                binding.rlReply.setVisibility(View.GONE);
            }
        });
    }

    /* ------------------------- PARSE METHODS -------------------------------- */
    public void queryMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo(Message.KEY_GROUP,member.getGroup());
        query.include(Message.KEY_SENDER);
        query.include(Message.KEY_USERS_LIKING_THIS);
        query.include(Message.KEY_REPLY_TO);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while quering messages for group (" + member.getGroup().getObjectId() + "): " + e.getMessage(),e);
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
                goProfileActivity(sender);
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

    /* ------------------------- INTENT METHODS TO ACTIVITIES -------------------------------- */
    private void goProfileActivity(ParseUser user) {
        Intent i = new Intent(ChatActivity.this, ProfileActivity.class);
        i.putExtra(User.KEY_USER,Parcels.wrap(user));
        this.startActivity(i);
    }
}