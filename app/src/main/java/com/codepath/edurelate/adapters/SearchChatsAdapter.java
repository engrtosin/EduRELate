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
import com.codepath.edurelate.databinding.ItemChatBinding;
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

public class SearchChatsAdapter extends RecyclerView.Adapter<SearchChatsAdapter.ViewHolder> {

    public static final String TAG = "SearchChatsAdapter";
//    StyleSpan ORANGE = new StyleSpan(android.graphics.Color.rgb(255,200,0));
    int ORANGE = Color.rgb(255,155,0);

    Context context;
    List<SearchResult> results;
    String queryTxt;
    ChatsAdapterInterface mListener;
    int msgHeaderPos;

    /* -------------------- INTERFACE --------------------- */
    public interface ChatsAdapterInterface {
        void groupClicked(Group group);
        void chatClicked(Member member);
        void friendClicked(ParseUser friend);
    }

    public void setAdapterListener(ChatsAdapterInterface chatsAdapterInterface) {
        this.mListener = chatsAdapterInterface;
    }

    /* -------------------- CONSTRUCTOR ------------------------- */
    public SearchChatsAdapter(Context context, List<SearchResult> results, String queryTxt) {
        this.context = context;
        this.results = results;
        this.queryTxt = queryTxt;
    }

    public int getMsgHeaderPos() {
        return msgHeaderPos;
    }

    public void setQueryTxt(String queryTxt) {
        this.queryTxt = queryTxt;
    }

    public void setMsgHeaderPos(int msgHeaderPos) {
        this.msgHeaderPos = msgHeaderPos;
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == SearchResult.HEADING) {
            ItemSearchHeadingBinding binding = ItemSearchHeadingBinding.inflate(inflater,parent,false);
            return new SearchChatsAdapter.HeaderViewHolder(binding);
        }
        ItemChatBinding itemChatBinding = ItemChatBinding.inflate(inflater,
                parent, false);
        return new SearchChatsAdapter.ResultViewHolder(itemChatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchChatsAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return SearchResult.HEADING;
        }
        return SearchResult.RESULT_ITEM;
    }

    private boolean isHeader(int position) {
        SearchResult result = results.get(position);
        return result.getGroup() == null;
    }

    /* -------------------- VIEW HOLDER CLASS ------------------------- */
    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        
        ItemChatBinding itemChatBinding;
        protected SearchResult result;
        
        /* ----------------- CONSTRUCTOR ------------------- */
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        /* ----------------- SETUP METHODS ------------------- */

        public void setOnClickListeners() {
            itemChatBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mListener.chatClicked(member);
                }
            });
        }

        public abstract void bind(int position);
    }

    public class HeaderViewHolder extends ViewHolder {

        ItemSearchHeadingBinding binding;

        public HeaderViewHolder(@NonNull @NotNull ItemSearchHeadingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void bind(int pos) {
            this.result = results.get(pos);
            binding.tvHeading.setText(result.getTitle());
            binding.tvResultSize.setText(result.getResultNumTxt());
        }
    }

    public class ResultViewHolder extends ViewHolder {

        ItemChatBinding binding;

        public ResultViewHolder(@NonNull @NotNull ItemChatBinding itemChatBinding) {
            super(itemChatBinding.getRoot());
            binding = itemChatBinding;
            this.itemChatBinding = itemChatBinding;
            itemChatBinding.tvChatName.setTypeface(null, Typeface.NORMAL);
        }

        public void bind(int pos) {
            this.result = results.get(pos);
            if (result.getGroup().getIsFriendGroup()) {
                ParseFile image = result.getGroup().getGroupPic();
                if (image != null) {
                    Glide.with(context).load(image.getUrl()).into(itemChatBinding.ivChatPic);
                }
            }
            else {
                Glide.with(context).load(result.getPic().getUrl()).into(itemChatBinding.ivChatPic);
            }
            Log.i(TAG,"pos: " + pos + ", msgPos: " + msgHeaderPos);
            if (pos < msgHeaderPos) {
                bindChat();
                return;
            }
            bindMsg();
        }

        private void bindChat() {
            SpannableStringBuilder stringBuilder = getSpannable(result.getTitle());
            itemChatBinding.tvChatName.setText(stringBuilder);
            String latestMsg = result.getLatestMsg();
            if (latestMsg != null) {
                itemChatBinding.tvLatestMsg.setText(latestMsg);
                return;
            }
            itemChatBinding.tvLatestMsg.setText("No message yet");
            itemChatBinding.tvLatestMsg.setTypeface(null, Typeface.ITALIC);
        }

        private void bindMsg() {
            itemChatBinding.tvChatName.setText(result.getTitle());
            String latestMsg = result.getLatestMsg();
            if (latestMsg != null) {
                SpannableStringBuilder stringBuilder = getSpannable(latestMsg);
                itemChatBinding.tvLatestMsg.setText(stringBuilder);
                return;
            }
        }

        private SpannableStringBuilder getSpannable(String plainTxt) {
            Log.i(TAG,"text: " + plainTxt);
            Log.i(TAG,"query: " + queryTxt);
            int start = plainTxt.toLowerCase().indexOf(queryTxt.toLowerCase());
            if (start > 20) {
                plainTxt = "..." + plainTxt.substring(start-10);
                start = 13;
            }
            int end = start + queryTxt.length();
            if (plainTxt.length() - 14 - end > 20) {
                plainTxt = plainTxt.substring(0,end + 20);
            }
            SpannableStringBuilder sb = new SpannableStringBuilder(plainTxt);
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            sb.setSpan(new ForegroundColorSpan(ORANGE), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            return sb;
        }

        private void bindFullGroup() {
            Member member = new Member();
            Group group = member.getGroup();
            if (group.has(Group.KEY_GROUP_PIC)) {
                ParseFile image = group.getParseFile(Group.KEY_GROUP_PIC);
                if (image != null) {
                    Glide.with(context).load(image.getUrl()).into(itemChatBinding.ivChatPic);
                }
            }
            itemChatBinding.tvChatName.setText(group.getGroupName());
            itemChatBinding.cvChatPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.groupClicked(member.getGroup());
                }
            });
        }

        private void bindFriendGroup() {
            Member member = new Member();
            ParseUser friend = member.getFriend();
            ParseFile image = friend.getParseFile(User.KEY_USER_PIC);
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(itemChatBinding.ivChatPic);
            }
            itemChatBinding.tvChatName.setText(User.getFullName(friend));
            Message latestMsg = member.getGroup().getLatestMsg();
            if (latestMsg != null) {
                String latestMsgTxt = latestMsg.getBody(false);
                itemChatBinding.tvLatestMsg.setText(latestMsgTxt);
                return;
            }
            itemChatBinding.tvLatestMsg.setText("No message yet");
            itemChatBinding.tvLatestMsg.setTypeface(null, Typeface.ITALIC);
            itemChatBinding.cvChatPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.friendClicked(member.getFriend());
                }
            });
        }
    }
}
