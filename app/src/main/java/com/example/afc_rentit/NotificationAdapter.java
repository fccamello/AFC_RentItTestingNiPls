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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.net.URL;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<NotificationItem> notificationItems;

    private OnApprovalStatusChangedListener listener;




    public interface OnApprovalStatusChangedListener {
        void onApprovalStatusChanged(int rentId, int status);
    }

    public NotificationAdapter(Context context, List<NotificationItem> notificationItems, OnApprovalStatusChangedListener listener) {
        this.context = context;
        this.notificationItems = notificationItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item_card, parent, false);
        System.out.println("nisud here");
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notificationItems.get(position);
        System.out.println("CURR USER" + Current_User.getInstance().getUser_id());
        System.out.println("OWNER SA ITEM: " + item.getOwnerId());

        if (item.getOwnerId() == Current_User.getInstance().getUser_id()) {
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


            holder.approveButton.setOnClickListener(v -> listener.onApprovalStatusChanged(item.getRentId(), 1));
            holder.declineButton.setOnClickListener(v -> listener.onApprovalStatusChanged(item.getRentId(), 0));
        } else {
            // If the user_id does not match, hide the itemView
            System.out.println("wala nisud");
            holder.itemView.setVisibility(View.GONE);
            // Optionally, you can also set the itemView's height to 0 to ensure it doesn't take up space
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = 0;
            holder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, price, startDate, endDate;
        ImageView image;
        Button approveButton, declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            duration = itemView.findViewById(R.id.item_duration);
            price = itemView.findViewById(R.id.item_price);
            startDate = itemView.findViewById(R.id.item_start_date);
            endDate = itemView.findViewById(R.id.item_end_date);
            image = itemView.findViewById(R.id.item_image);
            approveButton = itemView.findViewById(R.id.btn_approve);
            declineButton = itemView.findViewById(R.id.btn_decline);
        }
    }
}
