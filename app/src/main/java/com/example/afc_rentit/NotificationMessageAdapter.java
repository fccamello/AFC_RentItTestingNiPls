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


import com.example.afc_rentit.Database.SQLConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationMessageAdapter extends RecyclerView.Adapter<NotificationMessageAdapter.ViewHolder> {

    private Context context;
    private List<NotificationMessage> notificationmessage;
    private NotificationFragment fragment;




    public NotificationMessageAdapter(Context context, List<NotificationMessage> notificationmessage, NotificationFragment fragment) {
        this.context = context;
        this.notificationmessage = notificationmessage;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_notification_item_card, parent, false);
        System.out.println("nisud here???");
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationMessage msg = notificationmessage.get(position);
        holder.status.setText(msg.getMessage());
        holder.title.setText("Item: " + msg.getTitle());
        holder.duration.setText("Duration: " + String.valueOf(msg.getDuration()));
        holder.price.setText("Amount: " + String.valueOf(msg.getAmount()));
        holder.startDate.setText("Start Date: " + msg.getStart_date());
        holder.endDate.setText("End Date: "  +msg.getEnd_date());


        holder.deletebutton.setOnClickListener(v -> {

            //para mawala sa list
            notificationmessage.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notificationmessage.size());

//             para mawala sa database
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try (Connection conn = SQLConnection.getConnection();
                     PreparedStatement pStmt = conn.prepareStatement(
                             "DELETE FROM tblnotifs WHERE receiver_user_id = ?"
                     )) {
                    pStmt.setInt(1, Current_User.getInstance().getUser_id());
                    pStmt.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return notificationmessage.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, price, startDate, endDate,status;
        ImageView image;
        Button deletebutton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status_text);
//            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            duration = itemView.findViewById(R.id.item_duration);
            price = itemView.findViewById(R.id.item_price);
            startDate = itemView.findViewById(R.id.item_start_date);
            endDate = itemView.findViewById(R.id.item_end_date);

            deletebutton = itemView.findViewById(R.id.btn_delete);
        }
    }


}
