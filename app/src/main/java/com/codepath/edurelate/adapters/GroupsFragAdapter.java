package com.codepath.edurelate.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.MockGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GroupsFragAdapter extends RecyclerView.Adapter<GroupsFragAdapter.ViewHolder> {

    public static final String TAG = "GroupsFragAdapter";
    Context context;
    List<Group> groups;
    List<MockGroup> mockGroups;

    public GroupsFragAdapter(Context context, List<MockGroup> mockGroups) {
        this.context = context;
        this.mockGroups = mockGroups;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupsFragAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
