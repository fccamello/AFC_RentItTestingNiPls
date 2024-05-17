package com.example.afc_rentit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Home_Item_RecyclerViewAdapter extends RecyclerView.Adapter<Home_Item_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    List<Item> items;
    public Home_Item_RecyclerViewAdapter(Context context, List<Item> items){
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public Home_Item_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our items)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new Home_Item_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Home_Item_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // assigning values to the item_views we created in the home_item_view file
        // based on the position of the item_view
        Item item = items.get(position);
        holder.tv_title.setText(item.getTitle());
        // to do image, i don know how

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) context;

                Intent intent = new Intent(context, Activity_View_Item_Details.class);
                intent.putExtra("item_id", item.getItem_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        // number of items we want to display on the recycler view
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // grabbing the views from item_card file
        // similar to oncreate method sa activity files

        ImageView iv_itemImage;
        TextView tv_title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_itemImage = itemView.findViewById(R.id.item_image);
            tv_title = itemView.findViewById(R.id.item_title);
        }
    }

}
