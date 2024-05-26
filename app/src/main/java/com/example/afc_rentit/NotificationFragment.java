package com.example.afc_rentit;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.afc_rentit.Database.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NotificationFragment extends Fragment {

    List<NotificationItem> notificationItems = new ArrayList<>();
    RecyclerView notificationContainer;
    NotificationAdapter notificationAdapter;
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

        notificationAdapter = new NotificationAdapter(getContext(), notificationItems, this::updateApprovalStatus);
        notificationContainer.setAdapter(notificationAdapter);
    }

        private void setupNotificationModels() {
            List<NotificationItem> tempNotificationItems = new ArrayList<>();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try (Connection conn = SQLConnection.getConnection();
                     PreparedStatement pStmt = conn.prepareStatement(
                             "SELECT r.rent_id, r.item_id, r.user_id, r.isApproved, i.title, i.user_id, i.price, r.durationCategory, r.requestDate, i.image " +
                                     "FROM tblrentrequest r " +
                                     "JOIN tblitem i ON r.item_id = i.item_id " +
                                     "WHERE r.isApproved = -1 AND r.user_id = ?"
                     )) {
                    pStmt.setInt(1, currentUser.getUser_id());

                    ResultSet res = pStmt.executeQuery();

                    while (res.next()) {
                        int ownerId = res.getInt("i.user_id");
                        int rentId = res.getInt("rent_id");
                        int itemId = res.getInt("item_id");
                        int userId = res.getInt("r.user_id");
                        int isApproved = res.getInt("isApproved");
                        String title = res.getString("title");
                        double pricePerDay = res.getDouble("price");
                        String durationCategory = res.getString("durationCategory");
                        String startDateStr = res.getString("requestDate");
                        String imageUrl = res.getString("image");

                        int durationDays = calculateDurationInDays(durationCategory);

                        // Calculate end date and total price
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date startDate = dateFormat.parse(startDateStr);
                        long endDateMillis = startDate.getTime() + TimeUnit.DAYS.toMillis(durationDays);
                        Date endDate = new Date(endDateMillis);
                        String endDateStr = dateFormat.format(endDate);
                        double totalPrice = pricePerDay * durationDays;

                        NotificationItem notificationItem = new NotificationItem(
                                rentId, itemId, userId, isApproved, title, pricePerDay, durationDays, startDateStr, endDateStr, totalPrice, imageUrl,ownerId
                        );

                        tempNotificationItems.add(notificationItem);
                    }
                } catch (SQLException | ParseException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(() -> {
                    notificationItems.clear();
                    notificationItems.addAll(tempNotificationItems);
                    notificationAdapter.notifyDataSetChanged();
                });
            });
        }

    private int calculateDurationInDays(String durationCategory) {
        // Assuming durationCategory can be "days", "weeks", or "months"
        switch (durationCategory) {
            case "days":
                return 1; // 1 day
            case "weeks":
                return 7; // 1 week
            case "months":
                return 30; // 1 month
            default:
                return 0; // Handle invalid category
        }
    }

    private void updateApprovalStatus(int rentId, int status) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pStmt = conn.prepareStatement(
                         "UPDATE tblrentrequest SET isApproved = ? WHERE rent_id = ?"
                 )) {
                pStmt.setInt(1, status);
                pStmt.setInt(2, rentId);
                pStmt.executeUpdate();

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
}
