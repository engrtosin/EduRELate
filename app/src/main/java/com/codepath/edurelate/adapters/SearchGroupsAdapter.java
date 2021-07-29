package com.codepath.edurelate.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ItemChatBinding;
import com.codepath.edurelate.databinding.ItemGroupBinding;
import com.codepath.edurelate.databinding.ItemSearchHeadingBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.SearchResult;
import com.codepath.edurelate.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchGroupsAdapter extends RecyclerView.Adapter<SearchGroupsAdapter.ViewHolder> {

    public static final String TAG = "SearchGroupsAdapter";
    int ORANGE = Color.rgb(255,155,0);

    Context context;
    List<SearchResult> results;
    String queryTxt;
    ChatsAdapterInterface mListener;

    /* -------------------- INTERFACE --------------------- */
    public interface ChatsAdapterInterface {
        void groupClicked(Group group);
        void chatClicked(Member member);
        void friendClicked(ParseUser friend);
        void joinGroup(Group group);
    }

    public void setAdapterListener(ChatsAdapterInterface chatsAdapterInterface) {
        this.mListener = chatsAdapterInterface;
    }

    /* -------------------- CONSTRUCTOR ------------------------- */
    public SearchGroupsAdapter(Context context, List<SearchResult> results, String queryTxt) {
        this.context = context;
        this.results = results;
        this.queryTxt = queryTxt;
    }

    public void setQueryTxt(String queryTxt) {
        this.queryTxt = queryTxt;
    }

    /* ------------------- ADAPTER METHODS ------------------- */
    public void addAll(List<SearchResult> objects) {
        results.addAll(objects);
        notifyDataSetChanged();
    }

    public void clear() {
        results.clear();
        notifyDataSetChanged();
    }

    /* --------------------- RECYCLERVIEW VIEWHOLDER METHODS ----------------------- */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemGroupBinding itemGroupBinding = ItemGroupBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new SearchGroupsAdapter.ViewHolder(itemGroupBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchGroupsAdapter.ViewHolder holder, int position) {
        SearchResult result = results.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    /* -------------------- VIEW HOLDER CLASS ------------------------- */
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        ItemGroupBinding binding;
        Group group;
        protected SearchResult result;
        
        /* ----------------- CONSTRUCTOR ------------------- */
        public ViewHolder(@NonNull @NotNull ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /* ----------------- SETUP METHODS ------------------- */
        private void setViewOnClickListeners() {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mListener.groupClicked(group,binding.cvGroupPic);
                }
            });
            binding.tvGroupOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mListener.ownerClicked(group.getOwner());
                }
            });
            binding.tvActRequestJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.joinGroup(group);
                    binding.tvActRequestJoin.setText("Request sent");
                }
            });
        }

        public void bind(SearchResult result) {
            this.result = result;
            this.group = result.getGroup();
            ParseFile image = group.getGroupPic();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(binding.ivGroupPic);
            }
            else {
                binding.ivGroupPic.setImageResource(R.drawable.outline_groups_24);
            }
            binding.tvGroupName.setText(group.getGroupName());
            binding.tvGroupOwner.setText(User.getFullName(group.getOwner()));
            bindForJoiningGroup();
        }

        private void bindForJoiningGroup() {
//            if (requestGroupIds != null) {
//                binding.tvActRequestJoin.setVisibility(View.VISIBLE);
//                if (requestGroupIds.contains(group.getObjectId())) {
//                    binding.tvActRequestJoin.setText("Request sent");
//                    return;
//                }
//                binding.tvActRequestJoin.setText("Send a request");
//            }
        }
    }
}
