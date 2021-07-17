package com.codepath.edurelate.adapters;

import android.content.Context;
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
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    
    public static final String TAG = "ChatsAdapter";
    
    Context context;
    List<Group> groups;
    ChatsAdapterInterface mListener;

    /* -------------------- INTERFACE --------------------- */
    public interface ChatsAdapterInterface {
        void groupClicked(Group group);
        void chatClicked(Group group);
    }

    public void setAdapterListener(ChatsAdapterInterface chatsAdapterInterface) {
        this.mListener = chatsAdapterInterface;
    }
    
    /* -------------------- CONSTRUCTOR ------------------------- */
    public ChatsAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    /* ------------------- ADAPTER METHODS ------------------- */
    public void addAll(List<Group> objects) {
        groups.addAll(objects);
        notifyDataSetChanged();
    }

    public void clear() {
        groups.clear();
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
        Group group = groups.get(position);
        try {
            holder.bind(group);
        } catch (ParseException e) {
            Log.e(TAG,"Error binding view holder: " + e.getMessage(),e);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    /* -------------------- VIEW HOLDER CLASS ------------------------- */
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        ItemChatBinding itemChatBinding;
        Group group;
        
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
                    mListener.chatClicked(group);
                }
            });
            itemChatBinding.cvChatPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.groupClicked(group);
                }
            });
        }

        public void bind(Group group) throws ParseException {
            this.group = group;
            Log.i(TAG,"binding group: " + group.getObjectId());
            if (group.has(Group.KEY_IS_FRIEND_GROUP)) {
                if (group.getIsFriendGroup()) {
                    bindFriendGroup();
                    return;
                }
            }
            bindFullGroup();
        }

        private void bindFullGroup() {
            if (group.has(Group.KEY_GROUP_PIC)) {
                ParseFile image = group.getParseFile(Group.KEY_GROUP_PIC);
                if (image != null) {
                    Glide.with(context).load(image.getUrl()).into(itemChatBinding.ivChatPic);
                }
            }
            itemChatBinding.tvChatName.setText(group.getGroupName());
        }

        private void bindFriendGroup() throws ParseException {
            ParseUser friend = User.findFriend(User.currentUser,group);
            ParseFile image = friend.getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemChatBinding.ivChatPic);
            }
            itemChatBinding.tvChatName.setText(User.getFullName(friend));
        }
    }
}
