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
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    public static final String TAG = "GroupsAdapter";

    Context context;
    List<Group> groups;
    GroupsAdapterInterface mListener;

    public interface GroupsAdapterInterface {
        public void groupClicked(Group group);
        public void ownerClicked(ParseUser owner);
    }

    public void setAdapterListener(GroupsAdapterInterface groupsAdapterInterface) {
        this.mListener = groupsAdapterInterface;
    }

    public GroupsAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemGroupBinding itemGroupBinding;

        public ViewHolder(@NonNull @NotNull ItemGroupBinding itemGroupBinding) {
            super(itemGroupBinding.getRoot());
            this.itemGroupBinding = itemGroupBinding;
            setViewOnClickListeners();
        }

        private void setViewOnClickListeners() {
            itemGroupBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            itemGroupBinding.tvGroupOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void bind(Group group) throws ParseException {
            ParseFile image = group.getGroupPic();
            if (image == null) {
                Glide.with(context).load(image.getUrl()).into(itemGroupBinding.ivGroupPic);
            }
            else {
                itemGroupBinding.ivGroupPic.setImageResource(R.drawable.outline_groups_24);
            }

            itemGroupBinding.tvGroupName.setText(group.getGroupName());
            itemGroupBinding.tvGroupOwner.setText(User.getFullName(group.getOwner()));
        }
    }
}