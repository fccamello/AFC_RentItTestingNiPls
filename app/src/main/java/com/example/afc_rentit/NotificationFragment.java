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
import android.widget.Button;
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

    Button btnm, btni;
    List<NotificationItem> notificationItems = new ArrayList<>();
    List<NotificationMessage> notificationmesage = new ArrayList<>();

    RecyclerView notificationContainer;
    RecyclerView notificationMessageContainer;
    NotificationAdapter notificationAdapter;

    NotificationMessageAdapter notifmessageadapter;
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
        if (currentUser.isOwner()){
            setupMessageModelsSeller();
        }
        else{
            setupMessageModelsBuyer();
        }

        System.out.println("SIZE OF NOTIF " + notificationmesage.size());

        if (notificationmesage != null && !notificationmesage.isEmpty()) {
            // Dataset is not empty, log its size
            System.out.println("notificationmesage dataset size: " + notificationmesage.size());
        } else {
            System.out.println("notificationmesage dataset is empty or null");
        }

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

        notificationMessageContainer = view.findViewById(R.id.rv_notifications_message);
        notificationMessageContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationMessageContainer.setHasFixedSize(true);

        // Initially hide the items list
        notificationContainer.setVisibility(View.GONE);
        notificationMessageContainer.setVisibility(View.VISIBLE);

        notificationAdapter = new NotificationAdapter(getContext(), notificationItems, this);
        notifmessageadapter = new NotificationMessageAdapter(getContext(), notificationmesage, this);

        notificationContainer.setAdapter(notificationAdapter);
        notificationMessageContainer.setAdapter(notifmessageadapter);

        btni = view.findViewById(R.id.btnItems);
        btnm = view.findViewById(R.id.btnMessage);

        if (!currentUser.isOwner()){
            btni.setVisibility(View.GONE);
            btnm.setVisibility(View.GONE);
            notificationMessageContainer.setVisibility(View.VISIBLE);
        }

        btni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show items list when item button is clicked
                notificationContainer.setVisibility(View.VISIBLE);
                notificationMessageContainer.setVisibility(View.GONE);
                System.out.println(" na click ang items");
            }
        });

        btnm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationMessageContainer.setVisibility(View.VISIBLE);
                notificationContainer.setVisibility(View.GONE);
                System.out.println("na click ang message");
            }
        });
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

    private void setupMessageModelsBuyer() {
        List<NotificationMessage> tempNotificationMessages = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pStmt = conn.prepareStatement(
                         "SELECT n.sender_user_id, n.receiver_user_id, n.message, i.title, r.totalAmount, r.duration, r.endRentDate, " +
                                 "r.requestDate, n.rent_id " +
                                 "FROM tblrentrequest r " +
                                 "JOIN tblitem i ON r.item_id = i.item_id " +
                                 "JOIN tblnotifs n ON r.rent_id = n.rent_id " +
                                 "WHERE n.receiver_user_id = ?"

                 )) {
                pStmt.setInt(1, currentUser.getUser_id());

                ResultSet res = pStmt.executeQuery();

                while (res.next()) {
                    int receiver = res.getInt("receiver_user_id");
                    int senderid = res.getInt("sender_user_id");
                    int duration = res.getInt("duration");
                    double totalAmount = res.getDouble("r.totalAmount");
                    String startdate = res.getString("requestDate");
                    String end_date = res.getString("endRentDate");
                    int rentid = res.getInt("n.rent_id");
                    String message = res.getString("message");
                    String title = res.getString("i.title");

                    System.out.println(" CURRENT USER: " + currentUser.getUser_id());
                    System.out.println("RECEIVER USER ID: " + receiver);
                    System.out.println("Sender id: " + senderid + " rent id: " + rentid + " total amnt: "+ totalAmount + " start date " + startdate + " end_date " + end_date + " duration " +  duration + " message " + message + " title " +  title);
                    NotificationMessage msg = new NotificationMessage(senderid, rentid, totalAmount,startdate, end_date, duration, message, title);
                    tempNotificationMessages.add(msg);
                    System.out.println("SIZE NI SIYA: " + tempNotificationMessages.size());
                    System.out.println(" SUCCESS NI? BUYER ");
                    System.out.println("MAO NI NOTIF MESSAGES " + msg);
                }

//                notificationmesage.addAll(tempNotificationMessages);
//                System.out.println("INNER LOOP SIZE: " + notificationmesage.size());


                getActivity().runOnUiThread(() -> {
                    notificationmesage.clear();
                    notificationmesage.addAll(tempNotificationMessages);
                    notifmessageadapter.notifyDataSetChanged();
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }


        });
    }



//    private void setupMessageModelsSeller() {
//        List<NotificationMessage> tempNotificationMessages = new ArrayList<>();
//
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try (Connection conn = SQLConnection.getConnection();
//                 PreparedStatement pStmt = conn.prepareStatement(
//                         "SELECT n.sender_user_id, n.message, i.title, i.price, r.duration, r.endRentDate, " +
//                                 "r.startRentDate, r.totalAmount" +
//                                 "FROM tblrentrequest r " +
//                                 "JOIN tblitem i ON r.item_id = i.item_id " +
//                                 "JOIN tblnotifs n ON r.rent_id = n.rent_id" +
//                                 "WHERE n.receiver_user_id = ?"
//                 )) {
//                pStmt.setInt(1, currentUser.getUser_id());
//
//                ResultSet res = pStmt.executeQuery();
//
//                while (res.next()) {
//                    int senderid = res.getInt("sender_user_id");
//                    int rentid = res.getInt("n.rent_id");
//                    String message = res.getString("message");
//                    String title = res.getString("i.title");
//
//                    NotificationMessage msg = new NotificationMessage(senderid, rentid, message, title);
//                    tempNotificationMessages.add(msg);
//
//                    getActivity().runOnUiThread(() -> {
//                        notificationmesage.clear();
//                        notificationmesage.addAll(tempNotificationMessages);
//                        notifmessageadapter.notifyDataSetChanged();
//                    });
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//
//        });
//    }



    public void updateApprovalStatus(int rentId, int status, int item_id, int user_id) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pstmt2 = conn.prepareStatement("UPDATE tblitem SET isAvailable = ? WHERE item_id = ?");

                 PreparedStatement pStmt = conn.prepareStatement(
                         "UPDATE tblrentrequest SET isApproved = ? WHERE rent_id = ?"
                 );

                 PreparedStatement pStmt3 = conn.prepareStatement(
                         "INSERT INTO tblnotifs (rent_id, sender_user_id, receiver_user_id, message) VALUES (?,?,?,?)"
                 );


                ) {

                String message;

                if(status == 0){
                    message = "Your request has been declined!";}
                else message = "Your request has been approved!";

                pStmt3.setInt(1, rentId);
                pStmt3.setInt(2, currentUser.getUser_id());
                pStmt3.setInt(3, user_id);
                pStmt3.setString(4, message);


                pstmt2.setInt(1, 0);
                pstmt2.setInt(2, item_id);

                pStmt.setInt(1, status);
                pStmt.setInt(2, rentId);

                pStmt.executeUpdate();
                pstmt2.executeUpdate();
                pStmt3.executeUpdate();

//                notifyBuyer(rentId, status);

//                 Update local data and notify adapter
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

    private void setupMessageModelsSeller() {
        System.out.println("NISUD KA?");
        List<NotificationMessage> tempNotificationMessages = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection conn = SQLConnection.getConnection();
                 PreparedStatement pStmt = conn.prepareStatement(
                         "SELECT n.sender_user_id, n.message, n.rent_id, i.title, r.totalAmount, r.duration, r.endRentDate, " +
                                 "r.requestDate " +
                                 "FROM tblrentrequest r " +
                                 "JOIN tblitem i ON r.item_id = i.item_id " +
                                 "JOIN tblnotifs n ON r.rent_id = n.rent_id " +
                                 "WHERE n.receiver_user_id = ? AND i.user_id = ?"

                 )) {
                pStmt.setInt(1, currentUser.getUser_id());
                pStmt.setInt(2, currentUser.getUser_id());
                System.out.println("curr id " + currentUser.getUser_id());

                ResultSet res = pStmt.executeQuery();

                while (res.next()) {
                    int senderid = res.getInt("sender_user_id");
                    int duration = res.getInt("duration");
                    double totalAmount = res.getDouble("r.totalAmount");
                    String startdate = res.getString("requestDate");
                    String end_date = res.getString("endRentDate");
                    int rentid = res.getInt("n.rent_id");
                    String message = res.getString("message");
                    String title = res.getString("i.title");

                    System.out.println("TEST PRINT " + senderid + rentid + totalAmount + startdate + end_date + duration + message+ title);
                    NotificationMessage msg = new NotificationMessage(senderid, rentid, totalAmount,startdate, end_date, duration, message, title);
                    tempNotificationMessages.add(msg);
                    System.out.println(" SUCCESS NI? ");

                    getActivity().runOnUiThread(() -> {
                    notificationmesage.clear();
                    notificationmesage.addAll(tempNotificationMessages);
                    notifmessageadapter.notifyDataSetChanged();
                });
                }

//
            } catch (SQLException e) {
                e.printStackTrace();
            }


        });
    }

//    private void notifyBuyer(int rentId, int status) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try (Connection conn = SQLConnection.getConnection();
//                 PreparedStatement pStmt = conn.prepareStatement(
//                         "SELECT user_id FROM tblrentrequest WHERE rent_id = ?"
//                 )) {
//                pStmt.setInt(1, rentId);
//                ResultSet rs = pStmt.executeQuery();
//
//                if (rs.next()) {
//                    int buyerId = rs.getInt("user_id");
//
//                    // Send notification to buyer
//                    String notificationMessage = (status == 1) ? "Your request has been approved." : "Your request has been rejected.";
////                    sendNotificationToBuyer(buyerId, notificationMessage);
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }

//    private void sendNotificationToBuyer(int buyerId, String message) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try (Connection conn = SQLConnection.getConnection();
//                 PreparedStatement pStmt = conn.prepareStatement(
//                         "INSERT INTO notifications (user_id, message) VALUES (?, ?)"
//                 )) {
//                pStmt.setInt(1, buyerId);
//                pStmt.setString(2, message);
//                pStmt.executeUpdate();
//
//                getActivity().runOnUiThread(() -> {
//                    Toast.makeText(getContext(), "Notification sent to Buyer (ID: " + buyerId + "): " + message, Toast.LENGTH_SHORT).show();
//                });
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }

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
