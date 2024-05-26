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

        new Thread(() -> {
            try {
                URL url = new URL(item.getImage());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.itemView.post(() -> holder.iv_itemImage.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                // Optionally set a placeholder or error image
                holder.itemView.post(() -> holder.iv_itemImage.setImageResource(R.drawable.round_add_photo_alternate_24));
            }
        }).start();

        holder.tv_price.setText(String.valueOf(item.getPrice()));

        if (current_user.isOwner()){
            if (item.isAvailable == 0){
                holder.btnMakeAvail.setVisibility(View.VISIBLE);
                holder.btnMakeAvail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int res = manageItemToListing(item.getItem_id(), 1);

                        if (res == 1){
                            AppCompatActivity activity = (AppCompatActivity) context;
                            activity.runOnUiThread(()->{
                                Toast.makeText(context, item.getTitle() + " is now available!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            } else {
                holder.btnMakeUnavail.setVisibility(View.VISIBLE);
                holder.btnMakeUnavail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int res = manageItemToListing(item.getItem_id(), 0);

                        if (res == 1){
                            AppCompatActivity activity = (AppCompatActivity) context;

                            activity.runOnUiThread(()->{
                                Toast.makeText(context, item.getTitle() + " is now unavailable!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            }
        } else {
            holder.btnReturnItem.setVisibility(View.VISIBLE);
            holder.btnReturnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isReturned = returnItem(item.getRent_id(), item.getItem_id());

                    if (isReturned){
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.runOnUiThread(()->{
                            Toast.makeText(activity, item.getTitle() + " is now in the owner's hands!", Toast.LENGTH_SHORT).show();
                            items.remove(position);
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

    private boolean returnItem (int rent_id, int item_id){
        final boolean[] res = {false};

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection conn = SQLConnection.getConnection();
                PreparedStatement rStmt = conn.prepareStatement(
                        "UPDATE tblRentRequest SET isReturned = 1 WHERE rent_id = ?"
                );
                PreparedStatement iStmt = conn.prepareStatement(
                        "UPDATE tblItem SET isAvailable = 1 WHERE item_id = ?"
                )) {
                conn.setAutoCommit(false);
                rStmt.setInt(1, rent_id);
                int rentRes = rStmt.executeUpdate();

                iStmt.setInt(1, item_id);
                int itemRes = iStmt.executeUpdate();

                conn.setAutoCommit(true);

                if (itemRes == 1 && rentRes == 1){
                    res[0] = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return res[0];
    }
}
