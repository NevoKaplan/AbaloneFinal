package com.example.abalone.play.Recycler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.abalone.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    // ArrayList containing the Drawables to be displayed in the RecyclerView
    ArrayList<Drawable> lst;
    // Context of the RecyclerView
    Context context;
    // View to display a single item in the RecyclerView
    View stoneView;
    // ViewHolder to hold the view for a single item in the RecyclerView
    ViewHolder viewHolder;

    // Constructor which sets the ArrayList containing the Drawables to be displayed in the RecyclerView
    public CustomAdapter(ArrayList<Drawable> arrayList) {
        this.lst = arrayList;
    }

    // Called when a new ViewHolder is created
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get the context of the parent ViewGroup
        this.context = parent.getContext();
        // Inflate the view for a single item in the RecyclerView using the card_adapter.xml layout
        LayoutInflater inflater = LayoutInflater.from(context);
        stoneView = inflater.inflate(R.layout.card_adapter, parent, false);
        // Create a new ViewHolder for the view
        viewHolder = new ViewHolder(stoneView);
        return viewHolder;
    }

    // Called when a ViewHolder needs to be updated with data at a certain position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the position of the Drawable in the ArrayList using modulo to loop around if needed
        int wrappedPosition = position % lst.size();
        // Get the Drawable at the position
        final Drawable imageView = lst.get(wrappedPosition);
        // Set the ImageView in the ViewHolder to display the Drawable
        viewHolder.setImgPicture(imageView);
    }

    // Returns the maximum integer value, indicating that there is no limit to the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    // Returns the position as the item ID for each item in the RecyclerView
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Returns the position as the item view type for each item in the RecyclerView
    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
