package com.codepath.edurelate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.databinding.ItemNotificationBinding;
import com.codepath.edurelate.models.Group;
import com.codepath.edurelate.models.Invite;
import com.codepath.edurelate.models.Notification;
import com.codepath.edurelate.models.Request;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    public static final String TAG = "NotificationsAdapter";

    Context context;
    List<Notification> notifications;
    NotificationsAdapterInterface mListener;

    /* -------------------- INTERFACE --------------------- */
    public interface NotificationsAdapterInterface {
        void requestAccepted(Request request);
        void requestRejected(Request request);
    }

    public void setAdapterListener(NotificationsAdapterInterface notificationsAdapterInterface) {
        this.mListener = notificationsAdapterInterface;
    }

    /* -------------------- CONSTRUCTOR ------------------------- */
    public NotificationsAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    /* ------------------- ADAPTER METHODS ------------------- */
    public void addAll(List<Notification> objects) {
        notifications.addAll(objects);
        notifyDataSetChanged();
    }

    public void clear() {
        notifications.clear();
        notifyDataSetChanged();
    }

    /* --------------------- RECYCLERVIEW VIEWHOLDER METHODS ----------------------- */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemNotificationBinding itemNotificationBinding = ItemNotificationBinding.inflate(LayoutInflater.from(context),
                parent, false);
        return new NotificationsAdapter.ViewHolder(itemNotificationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationsAdapter.ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /* -------------------- VIEW HOLDER CLASS ------------------------- */
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        ItemNotificationBinding binding;
        Notification notification;
        
        /* ----------------- CONSTRUCTOR ------------------- */
        public ViewHolder(@NonNull @NotNull ItemNotificationBinding itemNotificationBinding) {
            super(itemNotificationBinding.getRoot());
            this.binding = itemNotificationBinding;
            setOnClickListeners();
        }

        /* ----------------- SETUP METHODS ------------------- */
        public void setOnClickListeners() {
            binding.ivAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.requestAccepted(notification.getRequest());
                    binding.ivAccept.setVisibility(View.GONE);
                    binding.ivReject.setVisibility(View.GONE);
                }
            });
            binding.ivReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.requestRejected(notification.getRequest());
                    binding.ivAccept.setVisibility(View.GONE);
                    binding.ivReject.setVisibility(View.GONE);
                }
            });
        }

        public void bind(Notification notification) {
            this.notification = notification;
            Log.i(TAG,"binding notification: " + notification.getObjectId());
            binding.tvNotifText.setText(notification.getNotifText());
            if (notification.getNotifType() == Notification.INVITEE_CODE) {
                Invite invite = notification.getInvite();
                if (invite.getStatus() == Invite.STATUS_NONE) {
                    binding.ivAccept.setVisibility(View.VISIBLE);
                    binding.ivReject.setVisibility(View.VISIBLE);
                    return;
                }
                return;
            }
            if (notification.getNotifType() == Notification.REQUEST_RECEIVED_CODE) {
                Request request = notification.getRequest();
                if (request != null) {
                    binding.ivAccept.setVisibility(View.VISIBLE);
                    binding.ivReject.setVisibility(View.VISIBLE);
                    return;
                }
                return;
            }
        }

        private void bindInviteeNotif() {
        }
    }
}
