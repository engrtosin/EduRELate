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
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG = "UsersAdapter";

    Context context;
    List<ParseUser> users;
    UsersAdapterInterface mListener;

    public interface UsersAdapterInterface {
        void userClicked(ParseUser user);
        void chatClicked(ParseUser user);
        void inviteUser(ParseUser user);
    }

    public void setAdapterListener(UsersAdapterInterface usersAdapterInterface) {
        this.mListener = usersAdapterInterface;
    }

    public UsersAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
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
            itemUserBinding.tvInvite.setVisibility(View.VISIBLE);
            ParseFile image = user.getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemUserBinding.ivUserPic);
            }
            else {
                itemUserBinding.ivUserPic.setImageResource(R.drawable.outline_groups_24);
            }

            itemUserBinding.tvFullName.setText(User.getFullName(user));
        }
    }
}
