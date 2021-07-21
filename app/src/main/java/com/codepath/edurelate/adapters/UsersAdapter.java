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

    public UsersAdapter(Context context, List<ParseUser> users, int inviteType, Group group) {
        this.context = context;
        this.users = users;
        this.inviteType = inviteType;
        this.group = group;
    }

    public void addAll(List<ParseUser> objects) {
        users.addAll(objects);
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
//        View view = itemGroupBinding.getRoot();
        return new ViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UsersAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        try {
            holder.bind(user);
        } catch (ParseException e) {
            Log.e(TAG,"Error while binding: " + e.getMessage(),e);
        }
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

        public void bind(ParseUser user) throws ParseException {
            this.user = user;
            if (inviteType == Invite.GROUP_INVITE_CODE) {
                if (group.getGroupAccess() == Group.OPEN_GROUP_CODE) {
                    bindForOpenGroup();
                }
                bindForGroupInvite();
            }
            else {
                bindForFriendInvite();
            }
            ParseFile image = user.getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemUserBinding.ivUserPic);
            }
            else {
                itemUserBinding.ivUserPic.setImageResource(R.drawable.outline_groups_24);
            }
            itemUserBinding.tvFullName.setText(User.getFullName(user));
        }

        private void bindForOpenGroup() {
            if (!group.isMember(user)) {
                itemUserBinding.tvInvite.setText("Join group");
                itemUserBinding.tvInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.joinGroup(user,group);
                    }
                });
            }
        }

        private void bindForClosedGroup() {

        }

        private void bindForGroupInvite() throws ParseException {
            Log.i(TAG,"bindForGroupInvite");
            if (User.compareUsers(ParseUser.getCurrentUser(),group.getOwner())) {
                Invite invite = group.getInvite(user);
                Log.i(TAG,"invite from request: " + invite);
                if (invite == null) {
                    if (!group.isMember(user)) {
                        bindForNoGroupInvite();
                    }
                    return;
                }
                if (invite.getStatus() == Invite.INVITE_STATUS_NONE) {
                    if (User.compareUsers(invite.getSender(),ParseUser.getCurrentUser())) {
                        // show user
                        bindForNoneGroupInvite();
                    }
                    //
                    bindForOwnerInvited(invite);
                }
            }
        }

        private void bindForNoGroupInvite() {
            Log.i(TAG,"bindForNoGroupInvite");
            itemUserBinding.tvInvite.setVisibility(View.VISIBLE);
            itemUserBinding.tvInvite.setText("Invite to group");
            itemUserBinding.tvInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.sendGroupInvite(user,group);
                    itemUserBinding.tvInvite.setText("Invite sent");
                    itemUserBinding.tvInvite.setClickable(false);
                }
            });
        }

        private void bindForNoneGroupInvite() {
            Log.i(TAG,"bindForNoneGroupInvite");
            itemUserBinding.tvInvite.setVisibility(View.VISIBLE);
            itemUserBinding.tvInvite.setText("Invite sent");
        }

        private void bindForOwnerInvited(Invite invite) {
            Log.i(TAG,"bindForOwnerInvited");
            itemUserBinding.tvInvite.setVisibility(View.GONE);
            itemUserBinding.tvAccept.setVisibility(View.VISIBLE);
            itemUserBinding.tvReject.setVisibility(View.VISIBLE);
            itemUserBinding.tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.acceptInvite(invite);
                    itemUserBinding.tvAccept.setVisibility(View.GONE);
                    itemUserBinding.tvReject.setVisibility(View.GONE);
                }
            });
            itemUserBinding.tvReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.rejectInvite(invite);
                    itemUserBinding.tvAccept.setVisibility(View.GONE);
                    itemUserBinding.tvReject.setVisibility(View.GONE);
                }
            });
        }

        private void bindForFriendInvite() {
            Log.i(TAG,"bindForDeleteInvite");
        }
    }
}
