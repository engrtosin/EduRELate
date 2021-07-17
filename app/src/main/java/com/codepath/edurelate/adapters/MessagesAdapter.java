package com.codepath.edurelate.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.models.Message;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    public static final String TAG = "MessagesAdapter";

    Context context;
    List<Message> messages;

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessagesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public class IncomingViewHolder extends RecyclerView.ViewHolder {

        public IncomingViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public class OutgoingViewHolder extends RecyclerView.ViewHolder {

        public OutgoingViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
