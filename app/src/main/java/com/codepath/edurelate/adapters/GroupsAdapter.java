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
import com.codepath.edurelate.databinding.ItemGroupBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Request;
import com.codepath.edurelate.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    public static final String TAG = "GroupsAdapter";

    Context context;
    List<Group> groups;
    GroupsAdapterInterface mListener;
    List<String> requestGroupIds;

    public interface GroupsAdapterInterface {
        void groupClicked(Group group,View groupPic);
        void ownerClicked(ParseUser owner);
        void joinGroup(Group group);
    }

    public void setAdapterListener(GroupsAdapterInterface groupsAdapterInterface) {
        this.mListener = groupsAdapterInterface;
    }

    /* ---------------- constructor ------------------- */
    public GroupsAdapter(Context context, List<Group> groups, List<String> requestGroupIds) {
        this.context = context;
        this.groups = groups;
        this.requestGroupIds = requestGroupIds;
    }

    /* ------------------- adapter methods ------------------- */
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
        ItemGroupBinding itemGroupBinding = ItemGroupBinding.inflate(LayoutInflater.from(context),
                parent, false);
        return new ViewHolder(itemGroupBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupsAdapter.ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    /* ---------------------- ViewHolder class ---------------------- */
    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemGroupBinding itemGroupBinding;
        Group group;

        /* ------------- constructor -------------- */
        public ViewHolder(@NonNull @NotNull ItemGroupBinding itemGroupBinding) {
            super(itemGroupBinding.getRoot());
            this.itemGroupBinding = itemGroupBinding;
            setViewOnClickListeners();
        }

        /* ---------------- other helper methods --------------------- */
        private void setViewOnClickListeners() {
            itemGroupBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.groupClicked(group,itemGroupBinding.cvGroupPic);
                }
            });
            itemGroupBinding.tvGroupOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.ownerClicked(group.getOwner());
                }
            });
            itemGroupBinding.tvActRequestJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.joinGroup(group);
                    itemGroupBinding.tvActRequestJoin.setText("Request sent");
                }
            });
        }

        public void bind(Group group) {
            this.group = group;
            ParseFile image = group.getGroupPic();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemGroupBinding.ivGroupPic);
            }
            else {
                itemGroupBinding.ivGroupPic.setImageResource(R.drawable.outline_groups_24);
            }
            itemGroupBinding.tvGroupName.setText(group.getGroupName());
            bindForJoiningGroup();
        }

        private void bindForJoiningGroup() {
            if (requestGroupIds != null) {
                String owner = "Owner: " + User.getFullName(group.getOwner());
                itemGroupBinding.tvGroupOwner.setText(owner);
                itemGroupBinding.tvActRequestJoin.setVisibility(View.VISIBLE);
                if (requestGroupIds.contains(group.getObjectId())) {
                    itemGroupBinding.tvActRequestJoin.setText("Request sent");
                    return;
                }
                itemGroupBinding.tvActRequestJoin.setText("Send a request");
            }
        }
    }
}
