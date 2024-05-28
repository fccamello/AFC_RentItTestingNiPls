package com.example.afc_rentit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.List;

public class NotificationAdapterBuyer extends RecyclerView.Adapter<NotificationAdapterBuyer.ViewHolder> {

    private Context context;
    private List<NotificationItem> notificationItems;
    private NotificationFragment fragment;

    public NotificationAdapterBuyer(Context context, List<NotificationItem> notificationItems, NotificationFragment fragment) {
        this.context = context;
        this.notificationItems = notificationItems;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View notificationView = LayoutInflater.from(context).inflate(R.layout.buyer_notification_item_card, parent, false);
        return new ViewHolder(notificationView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notificationItems.get(position);
        holder.title.setText(item.getTitle());
        holder.duration.setText(String.valueOf(item.getDurationDays()) + " day/s");
        holder.price.setText("₱" + String.valueOf(item.getTotalPrice()));
        holder.startDate.setText("Start: " + item.getStartDate());
        holder.endDate.setText("End: " + item.getEndDate());

        new Thread(() -> {
            try {
                URL url = new URL(item.getImage());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.itemView.post(() -> holder.image.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                // Optionally set a placeholder or error image
                holder.itemView.post(() -> holder.image.setImageResource(R.drawable.round_add_photo_alternate_24));
            }
        }).start();

        holder.deleteButton.setOnClickListener(v -> {
            // Implement deletion logic here
            Toast.makeText(context, "Delete button clicked", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, price, startDate, endDate;
        ImageView image;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            duration = itemView.findViewById(R.id.item_duration);
            price = itemView.findViewById(R.id.item_price);
            startDate = itemView.findViewById(R.id.item_start_date);
            endDate = itemView.findViewById(R.id.item_end_date);
            image = itemView.findViewById(R.id.item_image);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}
