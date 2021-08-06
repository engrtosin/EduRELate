package com.codepath.edurelate.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.edurelate.R;
import com.codepath.edurelate.fragments.AboutGroupFragment;
import com.codepath.edurelate.models.Category;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        View rootView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            rootView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void bind(Category category) {
            tvTitle.setText(category.getTitle());
            Drawable buttonDrawable = rootView.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            int resourceColor = Category.category_colors[category.getCode()];
            DrawableCompat.setTint(buttonDrawable, ContextCompat.getColor(context, resourceColor));
            rootView.setBackground(buttonDrawable);
        }
    }
}