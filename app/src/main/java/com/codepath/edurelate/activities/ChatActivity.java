package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityAllChatsBinding;
import com.codepath.edurelate.databinding.ActivityChatBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.User;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";

    ActivityChatBinding binding;
    Group group;
    ParseUser friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = Parcels.unwrap(getIntent().getParcelableExtra(Group.KEY_GROUP));
        if (group.getIsFriendGroup()) {
            friend = User.findFriend(User.currentUser,group);
        }

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {
            initializeViews();
        } catch (ParseException e) {
            Log.e(TAG,"Error while initializing views: " + e.getMessage(),e);
        }
        setClickListeners();
    }

    private void initializeViews() throws ParseException {
        if (group.getIsFriendGroup()) {
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

            }
        });
    }
}