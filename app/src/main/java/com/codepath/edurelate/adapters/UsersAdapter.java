package com.codepath.edurelate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ItemUserBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG = "UsersAdapter";

    public final int inviteType;
    private final Group group;
    private final List<String> membersId;
    Context context;
    List<ParseUser> users;
    UsersAdapterInterface mListener;

    public interface UsersAdapterInterface {
        void userClicked(ParseUser user);
        void chatClicked(ParseUser user);
        void inviteUser(ParseUser user);
        void acceptInvite(Invite invite);
        void rejectInvite(Invite invite);
        void sendGroupInvite(ParseUser user, Group group);
        void joinGroup(ParseUser user, Group group);
    }

    public void setAdapterListener(UsersAdapterInterface usersAdapterInterface) {
        this.mListener = usersAdapterInterface;
    }

    public UsersAdapter(Context context, List<ParseUser> users, int inviteType, Group group, List<String> membersId) {
        this.context = context;
        this.users = users;
        this.inviteType = inviteType;
        this.group = group;
        this.membersId = membersId;
    }

    public void addAll(List<ParseUser> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemUserBinding itemUserBinding = ItemUserBinding.inflate(LayoutInflater.from(context),
                parent, false);
        return new ViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UsersAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemUserBinding itemUserBinding;
        ParseUser user;

        public ViewHolder(@NonNull @NotNull ItemUserBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            this.itemUserBinding = itemUserBinding;
            setViewOnClickListeners();
        }

        private void setViewOnClickListeners() {
            itemUserBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.userClicked(user);
                }
            });
            itemUserBinding.tvChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.chatClicked(user);
                }
            });
            itemUserBinding.tvInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.inviteUser(user);
                }
            });
        }

        public void bind(ParseUser user) {
            if (User.compareUsers(group.getOwner(),ParseUser.getCurrentUser())) {
                if (!User.compareUsers(user,group.getOwner())) {
                    if (!membersId.contains(user.getObjectId())) {
                        itemUserBinding.tvInvite.setVisibility(View.VISIBLE);
                        itemUserBinding.tvInvite.setText("Add to group");
                        itemUserBinding.tvInvite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                group.addMember(user);
                                itemUserBinding.tvInvite.setText("User added");
                            }
                        });
                    }
                }
            }
            itemUserBinding.tvFullName.setText(User.getFullName(user));
            ParseFile image = user.getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemUserBinding.ivUserPic);
            }
        }

    }
}
