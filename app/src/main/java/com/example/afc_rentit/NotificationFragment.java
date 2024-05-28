package com.example.afc_rentit;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afc_rentit.Database.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationFragment extends Fragment {

    List<NotificationItem> notificationItems = new ArrayList<>();
    RecyclerView notificationContainer;
    NotificationAdapter notificationAdapter;

    NotificationAdapterBuyer notifbuyeradapter;
    Current_User currentUser = Current_User.getInstance();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setupNotificationModels();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationContainer = view.findViewById(R.id.rv_notifications);
        notificationContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationContainer.setHasFixedSize(true);

        if (!Current_User.getInstance().isOwner()) {
            notifbuyeradapter = new NotificationAdapterBuyer(getContext(), notificationItems, this);
            notificationContainer.setAdapter(notifbuyeradapter);
        } else {
            notificationAdapter = new NotificationAdapter(getContext(), notificationItems, this);
            notificationContainer.setAdapter(notificationAdapter);
        }
    }

    private void setupNotificationModels() {
        List<NotificationItem> tempNotificationItems = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pStmt = conn.prepareStatement(
                         "SELECT r.rent_id, r.item_id, r.user_id, r.isApproved, r.duration, i.title, " +
                                 "i.user_id AS owner_id, i.price, r.durationCategory, r.endRentDate, " +
                                 "r.requestDate, i.image " +
                                 "FROM tblrentrequest r " +
                                 "JOIN tblitem i ON r.item_id = i.item_id " +
                                 "WHERE r.isApproved = -1 AND i.user_id = ?"
                 )) {
                pStmt.setInt(1, currentUser.getUser_id());

                ResultSet res = pStmt.executeQuery();

                while (res.next()) {
                    int ownerId = res.getInt("owner_id");
                    int rentId = res.getInt("rent_id");
                    int itemId = res.getInt("item_id");
                    int userId = res.getInt("user_id");
                    int isApproved = res.getInt("isApproved");
                    String title = res.getString("title");
                    double pricePerDay = res.getDouble("price");
                    String durationCategory = res.getString("durationCategory");
                    String startDateStr = res.getString("requestDate");
                    String imageUrl = res.getString("image");
                    int duration = res.getInt("duration");
                    String edate = res.getString("endRentDate");

                    if ("months".equals(durationCategory)) {
                        duration *= 30;
                    } else if ("weeks".equals(durationCategory)) {
                        duration *= 7;
                    }

                    double totalPrice = pricePerDay * duration;

                    NotificationItem notificationItem = new NotificationItem(
                            rentId, itemId, userId, isApproved, title, pricePerDay,
                            duration, startDateStr, edate, totalPrice, imageUrl, ownerId);

                    tempNotificationItems.add(notificationItem);

                    getActivity().runOnUiThread(() -> {
                        notificationItems.clear();
                        notificationItems.addAll(tempNotificationItems);
                        notificationAdapter.notifyDataSetChanged();
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        });
    }

    public void updateApprovalStatus(int rentId, int status, int item_id) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pstmt2 = conn.prepareStatement("UPDATE tblitem SET isAvailable = ? WHERE item_id = ?");

                 PreparedStatement pStmt = conn.prepareStatement(
                         "UPDATE tblrentrequest SET isApproved = ? WHERE rent_id = ?"
                 )) {

                pstmt2.setInt(1, 0);
                pstmt2.setInt(2, item_id);

                pStmt.setInt(1, status);
                pStmt.setInt(2, rentId);

                pStmt.executeUpdate();
                pstmt2.executeUpdate();

                notifyBuyer(rentId, status);

                // Update local data and notify adapter
                getActivity().runOnUiThread(() -> {
                    for (NotificationItem item : notificationItems) {
                        if (item.getRentId() == rentId) {
                            item.setIsApproved(status);
                            notificationAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void notifyBuyer(int rentId, int status) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pStmt = conn.prepareStatement(
                         "SELECT user_id FROM tblrentrequest WHERE rent_id = ?"
                 )) {
                pStmt.setInt(1, rentId);
                ResultSet rs = pStmt.executeQuery();

                if (rs.next()) {
                    int buyerId = rs.getInt("user_id");

                    // Send notification to buyer
                    String notificationMessage = (status == 1) ? "Your request has been approved." : "Your request has been rejected.";
                    sendNotificationToBuyer(buyerId, notificationMessage);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendNotificationToBuyer(int buyerId, String message) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pStmt = conn.prepareStatement(
                         "INSERT INTO notifications (user_id, message) VALUES (?, ?)"
                 )) {
                pStmt.setInt(1, buyerId);
                pStmt.setString(2, message);
                pStmt.executeUpdate();

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Notification sent to Buyer (ID: " + buyerId + "): " + message, Toast.LENGTH_SHORT).show();
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    //ambot ani oi !!!!!!!!!!
//        public List<Notifications> getNotificationsForUser() {
//        List<Notifications> notifications = new ArrayList<>();
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try (Connection conn = SQLConnection.getConnection();
//                 PreparedStatement pStmt = conn.prepareStatement(
//                         "SELECT message, FROM notifications WHERE user_id = ?"
//                 )) {
//                pStmt.setInt(1, NotificationItem.getUserId());
//                ResultSet rs = pStmt.executeQuery();
//
//                while (rs.next()) {
//                    int id = rs.getInt("id");
//                    String message = rs.getString("message");
//
//                    Notifications notifs = new Notifications(id, message);
//                    notifications.add(notifs);
//                }
//
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//        return notifications;
//    }
//


}
