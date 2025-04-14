package com.example.mapdress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mapdress.R;
import com.example.mapdress.item.ClothingItem;

import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ClothingViewHolder> {

    private Context context;
    private List<ClothingItem> itemList;

    public ClothingAdapter(Context context, List<ClothingItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ClothingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView, priceTextView;

        public ClothingViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }

    @Override
    public ClothingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clothing, parent, false);
        return new ClothingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClothingViewHolder holder, int position) {
        ClothingItem item = itemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(item.getPrice() + " ₸");
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

