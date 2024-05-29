package com.example.afc_rentit.DashboardUtils;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.afc_rentit.Current_User;
import com.example.afc_rentit.Database.SQLConnection;
import com.example.afc_rentit.R;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class dashboard_item_adapter extends RecyclerView.Adapter<dashboard_item_adapter.DashItemHolder> {
    Context context;
    List<dashboard_item> items;
    Current_User current_user = Current_User.getInstance();
    public dashboard_item_adapter(Context context, List<dashboard_item> items){
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public dashboard_item_adapter.DashItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where we inflate the layout (Giving a look to our items)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dashboard_item, parent, false);
        return new dashboard_item_adapter.DashItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashItemHolder holder, @SuppressLint("RecyclerView") int position) {
        // assigning values to the item_views we created in the home_item_view file
        // based on the position of the item_view
        dashboard_item item = items.get(position);
        String desc = item.getDescription();

        holder.tv_title.setText(item.getTitle());
        holder.tv_category.setText(item.getCategory());

        if(item.getDescription().length() > 20) {
            desc = desc.substring(0,20);
        }
        holder.tv_desc.setText(desc);
        // to do image, i don know how

//        new Thread(() -> {
//            try {
//                URL url = new URL(item.getImage());
//                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                holder.itemView.post(() -> holder.iv_itemImage.setImageBitmap(bitmap));
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Optionally set a placeholder or error image
//                holder.itemView.post(() -> holder.iv_itemImage.setImageResource(R.drawable.round_add_photo_alternate_24));
//            }
//        }).start();

        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.round_add_photo_alternate_24) // Optional placeholder image
                .into(holder.iv_itemImage);

        holder.tv_price.setText(String.valueOf(item.getPrice()));

        if (current_user.isOwner()){
            if (item.isAvailable == 0){
                holder.btnMakeAvail.setText("Add to listing");
                holder.btnMakeAvail.setVisibility(View.VISIBLE);
                holder.btnMakeAvail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(()->{
                            int res = manageItemToListing(item.getItem_id(), 1);

                            if (res == 1){
                                AppCompatActivity activity = (AppCompatActivity) context;
                                activity.runOnUiThread(()->{
                                    Toast.makeText(activity, item.getTitle() + " is now available!", Toast.LENGTH_SHORT).show();
                                });

                                holder.btnMakeAvail.setVisibility(View.GONE);
                                notifyItemChanged(position);
                            }
                        });
                    }
                });
            } else {
                holder.btnMakeUnavail.setText("Remove from Listing");
                holder.btnMakeUnavail.setVisibility(View.VISIBLE);
                holder.btnMakeUnavail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(()->{
                            int res = manageItemToListing(item.getItem_id(), 0);

                            if (res == 1){
                                AppCompatActivity activity = (AppCompatActivity) context;

                                activity.runOnUiThread(()->{
                                    Toast.makeText(context, item.getTitle() + " is now unavailable!", Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                });
            }
        } else {
            holder.btnReturnItem.setText("Return Item");
            holder.btnReturnItem.setVisibility(View.VISIBLE);
            holder.btnReturnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isReturned = returnItem(item.getRent_id(), item.getItem_id(), item.getOwnerId());
                    System.out.println("na return? " + isReturned);

                    if (isReturned){
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.runOnUiThread(()->{
                            Toast.makeText(activity, item.getTitle() + " is now in the owner's hands!", Toast.LENGTH_SHORT).show();
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                        });
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // number of items we want to display on the recycler view
        return items.size();
    }

    public static class DashItemHolder extends RecyclerView.ViewHolder {
        // grabbing the views from item_card file
        // similar to oncreate method sa activity files

        ImageView iv_itemImage;
        TextView tv_title, tv_category, tv_desc, tv_price;
        Button btnMakeAvail, btnMakeUnavail, btnReturnItem;
        public DashItemHolder(@NonNull View itemView) {
            super(itemView);

            iv_itemImage = itemView.findViewById(R.id.iv_dashImage);
            tv_title = itemView.findViewById(R.id.tv_dashItem);
            tv_category = itemView.findViewById(R.id.tv_dashCategory);
            tv_desc = itemView.findViewById(R.id.tv_dashDesc);
            tv_price = itemView.findViewById(R.id.tv_dashprice);
            btnMakeAvail = itemView.findViewById(R.id.btnAddItem);
            btnMakeUnavail = itemView.findViewById(R.id.btnRemoveItem);
            btnReturnItem = itemView.findViewById(R.id.btnReturnItem);
        }
    }

    private int manageItemToListing(int item_id, int isAvailable) {
        final int[] res = new int[1];
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try(Connection conn = SQLConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE tblItem SET isAvailable = ? WHERE item_id = ?"
                )) {
                pstmt.setInt(1, isAvailable);
                pstmt.setInt(2, item_id);

                res[0] = pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return res[0];
    }

    private boolean returnItem (int rent_id, int item_id, int owner_id){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement rStmt = conn.prepareStatement(
                         "UPDATE tblRentRequest SET isReturned = 1 WHERE rent_id = ?"
                 );
                 PreparedStatement iStmt = conn.prepareStatement(
                         "UPDATE tblItem SET isAvailable = 1 WHERE item_id = ?"
                 );
                 PreparedStatement nStmt = conn.prepareStatement(
                         "INSERT INTO tblNotifs (sender_user_id, receiver_user_id, message, rent_id) VALUES (?,?,?,?) "
                 );

                 PreparedStatement returnBuyer = conn.prepareStatement(
                         "INSERT INTO tblNotifs (sender_user_id, receiver_user_id, message, rent_id) VALUES (?,?,?,?) "
                 );

                 )
            {
                conn.setAutoCommit(false);
                rStmt.setInt(1, rent_id);
                int rentRes = rStmt.executeUpdate();

                iStmt.setInt(1, item_id);
                int itemRes = iStmt.executeUpdate();

                nStmt.setInt(1, current_user.getUser_id());
                nStmt.setInt(2, owner_id);
                nStmt.setString(3, "Your item has been returned!");
                nStmt.setInt(4, rent_id);
                int notifRes = nStmt.executeUpdate();

                returnBuyer.setInt(1, current_user.getUser_id());
                returnBuyer.setInt(2, current_user.getUser_id());
                returnBuyer.setString(3, "You have returned the item!");
                returnBuyer.setInt(4, rent_id);
                returnBuyer.executeUpdate();

                conn.setAutoCommit(true);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return true;
    }

    public void makeUnAvailShow(Button btnMakeUnavail, int item_id, String item_title){
        btnMakeUnavail.setText("Remove from Listing");
        btnMakeUnavail.setVisibility(View.VISIBLE);
        btnMakeUnavail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int res = manageItemToListing(item_id, 0);

                if (res == 1){
                    AppCompatActivity activity = (AppCompatActivity) context;

//                    activity.runOnUiThread(()->{
                    Toast.makeText(context, item_title + " is now unavailable!", Toast.LENGTH_SHORT).show();
//                    });
                }
            }
        });
    }
}