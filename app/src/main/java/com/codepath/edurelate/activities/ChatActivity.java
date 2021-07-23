package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.adapters.MessagesAdapter;
import com.codepath.edurelate.databinding.ActivityAllChatsBinding;
import com.codepath.edurelate.databinding.ActivityChatBinding;
import com.codepath.edurelate.models.Group;
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
    List<Message> messages;
    ParseUser friend;
    MessagesAdapter adapter;
    Message newReplyTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.group = Parcels.unwrap(getIntent().getParcelableExtra(Group.KEY_GROUP));
        Log.i(TAG,"group gotten: " + this.group.getObjectId());
        if (group.getIsFriendGroup()) {
            friend = User.findFriend(ParseUser.getCurrentUser(),group);
        }

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        Message msg1 = new Message(ParseUser.getCurrentUser(),"Msg 1", null, null);
//        Message msg2 = new Message(ParseUser.getCurrentUser(),"Msg 2", null, null);
//        Message msg3 = new Message(ParseUser.getCurrentUser(),"Msg 3", null, null);
//        Message botMsg1 = new Message(User.edurelateBot,"Bot msg", null, null);
//        messages = Arrays.asList(msg1,msg2,msg3,botMsg1);
        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this,messages);
        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        queryMessages();
        Log.i(TAG,"messages size: "+messages.size());

        try {
            initializeViews();
        } catch (ParseException e) {
            Log.e(TAG,"Error while initializing views: " + e.getMessage(),e);
        }
        setClickListeners();
    }

    private void initializeViews() throws ParseException {
        Log.i(TAG,"initializing for group: " + group.getObjectId());
        if (this.group.getIsFriendGroup()) {
            glideFriendPic();
        }
        else {
            glideGroupPic();
        }
    }

    private void glideFriendPic() {
        binding.tvActivityTitle.setText(User.getFirstName(friend));
        ParseFile image = friend.getParseFile(User.KEY_USER_PIC);
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(binding.ivChatPic);
        }
    }

    private void glideGroupPic() {
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
            }
        });
    }

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
            }
        });
    }
}