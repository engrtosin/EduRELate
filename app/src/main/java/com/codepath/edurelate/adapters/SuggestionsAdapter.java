package com.codepath.edurelate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.R;
import com.codepath.edurelate.models.Category;
import com.google.mlkit.nl.smartreply.SmartReplySuggestion;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    public static final String TAG = "SuggestionsAdapter";
    Context context;
    List<SmartReplySuggestion> suggestions;

    public SuggestionsAdapter(Context context, List<SmartReplySuggestion> suggestions) {
        this.context = context;
        this.suggestions = suggestions;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SuggestionsAdapter.ViewHolder holder, int position) {
        SmartReplySuggestion suggestion = suggestions.get(position);
        holder.tvTitle.setText(suggestion.getText());
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setBackgroundColor(context.getColor(R.color.announcement_text_gray));
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}