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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

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
        try {
            if (group.getIsFriendGroup()) {
                friend = User.findFriend(User.currentUser,group);
            }
        } catch (ParseException e) {
            Log.e(TAG,"getting is friend group failed: " + e.getMessage(),e);
        }
        messages = group.getMessages();

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        Message msg1 = new Message(User.currentUser,"Msg 1", null, null);
//        Message msg2 = new Message(User.currentUser,"Msg 2", null, null);
//        Message msg3 = new Message(User.currentUser,"Msg 3", null, null);
//        Message botMsg1 = new Message(User.edurelateBot,"Bot msg", null, null);
//        messages = Arrays.asList(msg1,msg2,msg3,botMsg1);
        adapter = new MessagesAdapter(this,messages);
        binding.rvMessages.setAdapter(adapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));

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
                adapter.notifyDataSetChanged();
                binding.etNewMessage.setText("");
            }
        });
    }
}