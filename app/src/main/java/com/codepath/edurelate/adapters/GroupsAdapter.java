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
        void groupClicked(Group group);
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
//        View view = itemGroupBinding.getRoot();
        return new ViewHolder(itemGroupBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupsAdapter.ViewHolder holder, int position) {
        Group group = groups.get(position);
        try {
            holder.bind(group);
        } catch (ParseException e) {
            Log.e(TAG,"Error while binding: " + e.getMessage(),e);
        }
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
                    mListener.groupClicked(group);
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

        public void bind(Group group) throws ParseException {
            this.group = group;
            ParseFile image = group.getGroupPic();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemGroupBinding.ivGroupPic);
            }
            else {
                itemGroupBinding.ivGroupPic.setImageResource(R.drawable.outline_groups_24);
            }
            itemGroupBinding.tvGroupName.setText(group.getGroupName());
            itemGroupBinding.tvGroupOwner.setText(User.getFullName(group.getOwner()));
            bindForJoiningGroup();
        }

        private void bindForJoiningGroup() {
            if (requestGroupIds != null) {
                itemGroupBinding.tvActRequestJoin.setVisibility(View.VISIBLE);
                if (requestGroupIds.contains(group.getObjectId())) {
                    itemGroupBinding.tvActRequestJoin.setText("Request sent");
                    return;
                }
                itemGroupBinding.tvActRequestJoin.setText("Send a request");
            }
        }

        private boolean isGroupMember() {
            ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
            query.include(Member.KEY_USER);
            query.whereContainedIn(Member.KEY_USER, Arrays.asList(ParseUser.getCurrentUser()));
            query.whereEqualTo(Member.KEY_GROUP,group);
            final boolean[] memberExists = new boolean[1];
            query.getFirstInBackground(new GetCallback<Member>() {
                @Override
                public void done(Member object, ParseException e) {
                    if (e != null) {
                        Log.e(TAG,"Error while getting member: " + e.getMessage(),e);
                        return;
                    }
                    Log.i(TAG,"Member successfully queried: " + group.getObjectId());
                    if (object != null) {
                        memberExists[0] = true;
                        return;
                    }
                    memberExists[0] = false;
                }
            });
            return memberExists[0];
        }
    }
}
