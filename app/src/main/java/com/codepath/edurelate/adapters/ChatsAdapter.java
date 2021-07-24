package com.codepath.edurelate.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.databinding.ItemChatBinding;
import com.codepath.edurelate.models.Chat;
import com.codepath.edurelate.models.Friend;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    
    public static final String TAG = "ChatsAdapter";
    
    Context context;
    List<Member> members;
    ChatsAdapterInterface mListener;

    /* -------------------- INTERFACE --------------------- */
    public interface ChatsAdapterInterface {
        void groupClicked(Group group);
        void chatClicked(Member member);
        void friendClicked(ParseUser friend);
    }

    public void setAdapterListener(ChatsAdapterInterface chatsAdapterInterface) {
        this.mListener = chatsAdapterInterface;
    }
    
    /* -------------------- CONSTRUCTOR ------------------------- */
    public ChatsAdapter(Context context, List<Member> members) {
        this.context = context;
        this.members = members;
    }

    /* ------------------- ADAPTER METHODS ------------------- */

    public void addAll(List<Member> objects) {
        members.addAll(objects);
        notifyDataSetChanged();
    }

    public void clear() {
        members.clear();
        notifyDataSetChanged();
    }

    /* --------------------- RECYCLERVIEW VIEWHOLDER METHODS ----------------------- */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemChatBinding itemChatBinding = ItemChatBinding.inflate(LayoutInflater.from(context),
                parent, false);
        return new ChatsAdapter.ViewHolder(itemChatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatsAdapter.ViewHolder holder, int position) {
        Member member = members.get(position);
        try {
            holder.bind(member);
        } catch (ParseException e) {
            Log.e(TAG,"Error binding view holder: " + e.getMessage(),e);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    /* -------------------- VIEW HOLDER CLASS ------------------------- */
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        ItemChatBinding itemChatBinding;
        Member member;
        
        /* ----------------- CONSTRUCTOR ------------------- */
        public ViewHolder(@NonNull @NotNull ItemChatBinding itemChatBinding) {
            super(itemChatBinding.getRoot());
            this.itemChatBinding = itemChatBinding;
            setOnClickListeners();
        }

        /* ----------------- SETUP METHODS ------------------- */

        public void setOnClickListeners() {
            itemChatBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.chatClicked(member);
                }
            });
        }

        public void bind(Member member) throws ParseException {
            this.member = member;
            Group group = member.getGroup();
            Log.i(TAG,"binding group: " + group.getObjectId());
            if (group.getIsFriendGroup()) {
                bindFriendGroup();
                return;
            }
            bindFullGroup();
            Message latestMsg = group.getLatestMsg();
            if (latestMsg != null) {
                String latestMsgTxt = latestMsg.getBody(false);
                itemChatBinding.tvLatestMsg.setText(latestMsgTxt);
                return;
            }
            itemChatBinding.tvLatestMsg.setText("No message yet");
            itemChatBinding.tvLatestMsg.setTypeface(null, Typeface.ITALIC);
        }

        private void bindFullGroup() {
            Group group = member.getGroup();
            if (group.has(Group.KEY_GROUP_PIC)) {
                ParseFile image = group.getParseFile(Group.KEY_GROUP_PIC);
                if (image != null) {
                    Glide.with(context).load(image.getUrl()).into(itemChatBinding.ivChatPic);
                }
            }
            itemChatBinding.tvChatName.setText(group.getGroupName());
            itemChatBinding.cvChatPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.groupClicked(member.getGroup());
                }
            });
        }

        private void bindFriendGroup() throws ParseException {
            ParseUser friend = member.getFriend();
            ParseFile image = friend.getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemChatBinding.ivChatPic);
            }
            itemChatBinding.tvChatName.setText(User.getFullName(friend));
            Message latestMsg = member.getGroup().getLatestMsg();
            if (latestMsg != null) {
                String latestMsgTxt = latestMsg.getBody(false);
                itemChatBinding.tvLatestMsg.setText(latestMsgTxt);
                return;
            }
            itemChatBinding.tvLatestMsg.setText("No message yet");
            itemChatBinding.tvLatestMsg.setTypeface(null, Typeface.ITALIC);
            itemChatBinding.cvChatPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.friendClicked(member.getFriend());
                }
            });
        }
    }
}
