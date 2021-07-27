package com.codepath.edurelate.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ItemMemberBinding;
import com.codepath.edurelate.databinding.ItemUserBinding;
import com.codepath.edurelate.models.Member;
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    public static final String TAG = "MembersAdapter";

    Context context;
    List<Member> members;
    MembersAdapterInterface mListener;

    public interface MembersAdapterInterface {
        void memberClicked(ParseUser user);
        void chatClicked(ParseUser user);
        void removeMember(Member member);
    }

    public void setAdapterListener(MembersAdapterInterface membersAdapterInterface) {
        this.mListener = membersAdapterInterface;
    }

    public MembersAdapter(Context context, List<Member> members) {
        this.context = context;
        this.members = members;
    }

    public void addAll(List<Member> objects) {
        members.addAll(objects);
        notifyDataSetChanged();
    }

    public void remove(Member member) {
        int pos = members.indexOf(member);
        members.remove(member);
        notifyItemRemoved(pos);
    }

    public void clear() {
        members.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMemberBinding itemMemberBinding = ItemMemberBinding.inflate(LayoutInflater.from(context),
                parent, false);
        return new ViewHolder(itemMemberBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MembersAdapter.ViewHolder holder, int position) {
        Member member = members.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemMemberBinding itemMemberBinding;
        Member member;

        public ViewHolder(@NonNull @NotNull ItemMemberBinding itemMemberBinding) {
            super(itemMemberBinding.getRoot());
            this.itemMemberBinding = itemMemberBinding;
            setViewOnClickListeners();
        }

        private void setViewOnClickListeners() {
            itemMemberBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.memberClicked(member.getUser());
                }
            });
            itemMemberBinding.tvChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.chatClicked(member.getUser());
                }
            });
            itemMemberBinding.tvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimatorSet shrink = new AnimatorSet();
                    ObjectAnimator shrinkAnimY = ObjectAnimator.ofFloat(itemMemberBinding.getRoot(),
                            View.SCALE_Y,0.2f);
                    ObjectAnimator shrinkAnimX = ObjectAnimator.ofFloat(itemMemberBinding.getRoot(),
                            View.SCALE_X,0.2f);
                    shrink.playTogether(shrinkAnimX,shrinkAnimY);
                    shrink.setDuration(2000);
                    shrink.start();
                    shrink.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mListener.removeMember(member);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            });
        }

        public void bind(Member member) {
            this.member = member;
            ParseFile image = member.getUser().getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemMemberBinding.ivUserPic);
            }
            else {
                itemMemberBinding.ivUserPic.setImageResource(R.drawable.outline_groups_24);
            }
            itemMemberBinding.tvFullName.setText(User.getFullName(member.getUser()));
        }
    }
}
