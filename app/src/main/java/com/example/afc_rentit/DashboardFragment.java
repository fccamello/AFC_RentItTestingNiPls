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

import com.example.afc_rentit.DashboardUtils.dashboard_item;
import com.example.afc_rentit.DashboardUtils.dashboard_item_adapter;
import com.example.afc_rentit.Database.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // for the items
    List<dashboard_item> items = new ArrayList<>();
    RecyclerView item_container;
    dashboard_item_adapter item_adapter;
    Current_User current_user = Current_User.getInstance();

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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

        setupItemModels();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        item_container = view.findViewById(R.id.rv_dashboardItems);
        item_container.setLayoutManager(new LinearLayoutManager(getContext()));
        item_container.hasFixedSize();

        item_adapter = new dashboard_item_adapter(getContext(), items);
        item_container.setAdapter(item_adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupItemModels() {
        List<dashboard_item> temp_items = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            boolean querySuccess = false;
            if (current_user.isOwner()){
                try (Connection conn = SQLConnection.getConnection();
                     PreparedStatement pStmt = conn.prepareStatement(
                             "SELECT item_id, title, image, description, category, price, isAvailable" +
                                     " FROM tblItem WHERE user_id = ?"
                     )){
                    pStmt.setInt(1, current_user.getUser_id());

                    ResultSet res = pStmt.executeQuery();

                    while (res.next()){
                        dashboard_item item = new dashboard_item(
                                res.getInt("item_id"),
                                res.getString("title"),
                                res.getString("image"),
                                res.getString("description"),
                                res.getString("category"),
                                res.getDouble("price")
                        );

                        item.setIsAvailable(res.getInt("isAvailable"));

                        temp_items.add(item);
                        querySuccess = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                try (Connection conn = SQLConnection.getConnection();
                     PreparedStatement pStmt = conn.prepareStatement(
                             "SELECT rent_id, i.item_id, i.user_id, title, image, description, category, price, isReturned " +
                                     "FROM tblItem as i, tblRentRequest as r " +
                                     "WHERE i.item_id = r.item_id AND r.user_id = ? AND isApproved = 1 AND isReturned = 0"
                     )){
                    pStmt.setInt(1, current_user.getUser_id());

                    ResultSet res = pStmt.executeQuery();

                    while (res.next()){
                        dashboard_item item = new dashboard_item(
                                res.getInt("item_id"),
                                res.getString("title"),
                                res.getString("image"),
                                res.getString("description"),
                                res.getString("category"),
                                res.getDouble("price")
                        );

                        item.setRent_id(res.getInt("rent_id"));
                        item.setIsReturned(res.getInt("isReturned"));
                        item.setItemOwner(res.getInt("user_id"));
                        temp_items.add(item);
                        querySuccess = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            getActivity().runOnUiThread(()->{
                items = temp_items;
                System.out.println("dashboard items: " + items.size());
                item_adapter.notifyDataSetChanged();
            });
        });
    }
}