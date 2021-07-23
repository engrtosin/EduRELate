package com.codepath.edurelate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.edurelate.MainActivity;
import com.codepath.edurelate.OnDoubleTapListener;
import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ItemIncomingMessageBinding;
import com.codepath.edurelate.databinding.ItemOutgoingMessageBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Message;
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    public static final String TAG = "MessagesAdapter";

    Context context;
    List<Message> messages;
    MessagesAdapter.MessagesAdapterInterface mListener;

    /* ---------------- INTERFACE ------------------- */
    public interface MessagesAdapterInterface {
        void senderClicked(ParseUser sender);
        void replyClicked(Message replyTo);
        void onDoubleTap(Message message);
    }

    public void setAdapterListener(MessagesAdapterInterface mListener) {
        this.mListener = mListener;
    }

    /* ---------------- CONSTRUCTOR ------------------- */
    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    /* ------------------- ADAPTER METHODS ------------------- */
    public void addAll(List<Message> messages) {
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    /* ---------------- VIEWHOLDER METHODS ------------------- */
    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == Message.MESSAGE_INCOMING) {
            ItemIncomingMessageBinding binding = ItemIncomingMessageBinding.inflate(inflater,parent,false);
            return new IncomingViewHolder(binding);
        } else if (viewType == Message.MESSAGE_OUTGOING) {
            ItemOutgoingMessageBinding binding = ItemOutgoingMessageBinding.inflate(inflater,parent,false);
            return new OutgoingViewHolder(binding);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessagesAdapter.MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isCurrUser(position)) {
            return Message.MESSAGE_OUTGOING;
        }
        return Message.MESSAGE_INCOMING;
    }

    /* ---------------- ADAPTER HELPER METHODS ------------------- */
    private boolean isCurrUser(int position) {
        Message message = messages.get(position);
        return message.getSender() != null &&
                message.getSender().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
    }

    /* ---------------- VIEWHOLDER CLASSES ------------------- */
    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        protected Message message;

        public MessageViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);
        }

        public abstract void bind(Message message);
    }

    public class IncomingViewHolder extends MessageViewHolder {

        ItemIncomingMessageBinding binding;

        public IncomingViewHolder(@NonNull @NotNull ItemIncomingMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.getRoot().setOnTouchListener(new OnDoubleTapListener(context) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    Log.i(TAG,"Incoming Message is double tapped");
                    mListener.onDoubleTap(message);
                }
            });
            binding.cvSenderPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Sender: " + message.getSender());
                    mListener.senderClicked(message.getSender());
                }
            });
            binding.tvReplyMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.replyClicked(message.getReplyTo());
                }
            });
            binding.ivLikeInMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void bind(Message message) {
            ParseUser sender = message.getSender();
            this.message = message;
            ParseFile image = null;
            Log.i(TAG,sender.getObjectId());
            image = sender.getParseFile(User.KEY_USER_PIC);
            if (message != null) {
                Glide.with(context).load(image.getUrl()).into(binding.ivSenderPic);
            }
            binding.tvSenderName.setText(User.getFullName(sender));
            binding.tvInTxt.setText(message.getBody(true));
            bindReply();
        }

        private void bindReply() {
            if (message.replyTo != null) {

            }
        }
    }

    public class OutgoingViewHolder extends MessageViewHolder {

        ItemOutgoingMessageBinding binding;

        public OutgoingViewHolder(@NonNull @NotNull ItemOutgoingMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.getRoot().setOnTouchListener(new OnDoubleTapListener(context) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    Log.i(TAG,"Outgoing Message is double tapped");
                    mListener.onDoubleTap(message);
                }
            });
            binding.ivLikeOutMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void bind(Message message) {
            this.message = message;
            binding.tvOutTxt.setText(message.getBody(true));
//            binding.tvOutTxt.setText(message.body);
        }
    }


}
