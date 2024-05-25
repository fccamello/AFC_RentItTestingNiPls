package com.example.afc_rentit;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afc_rentit.Database.DatabaseManager;
import com.example.afc_rentit.Database.SQLConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // for the items
    DatabaseManager dbManager = DatabaseManager.getInstance();
    List<Item> items = new ArrayList<>();
    RecyclerView item_views_container;
    Home_Item_RecyclerViewAdapter item_adapter;
    SearchView searchbox;
    Button btnEducation, btnEntertainment, btnElectronic, btnAll;
    TextView noItemView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        setUpItemModels();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // ANG PAGPASHOW SA ITEMS
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        item_views_container = view.findViewById(R.id.rv_ItemViews);
        searchbox = view.findViewById(R.id.searchbox);
        btnEducation = view.findViewById(R.id.btnEducation);
        btnElectronic = view.findViewById(R.id.btnElectronic);
        btnEntertainment = view.findViewById(R.id.btnEntertainment);
        btnAll = view.findViewById(R.id.btnAll);

        item_views_container.setLayoutManager(new GridLayoutManager(getContext(),2));
        item_views_container.hasFixedSize();

        item_adapter = new Home_Item_RecyclerViewAdapter(getContext(), items);
        item_views_container.setAdapter(item_adapter);
        item_adapter.notifyDataSetChanged();

        // SEARCH

        searchbox.clearFocus();
        searchbox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        btnEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryList("Education");
            }
        });

        btnEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryList("Entertainment");
            }
        });

        btnElectronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryList("Electronics");
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item_adapter.items = items;
                item_adapter.notifyDataSetChanged();
            }
        });

//        noItemView = view.findViewById(R.id.tv_NoItems);
//        System.out.println("number of items: " + items.size());
//        if (items.isEmpty()){
//            noItemView.setVisibility(View.VISIBLE);
//            item_views_container.setVisibility(View.INVISIBLE);
//        } else {
//            item_views_container.setVisibility(View.VISIBLE);
//        }
    }

    private void filterList(String text) {
        List<Item> filteredList = new ArrayList<>();

        for(Item item : items) {
            if(item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if(filteredList.isEmpty()) {
            Toast.makeText(getContext(),"No items found!", Toast.LENGTH_SHORT).show();
        } else {
            item_adapter.setFilteredList(filteredList);
        }
    }

    private void categoryList(String category) {
        List<Item> categoryList = new ArrayList<>();

        for (Item item : items) {
            String[] categories = item.getCategory().split(",");

            for (String cat : categories) {
                if (cat.trim().equals(category)) {
                    categoryList.add(item);
                }
            }
        }

        item_adapter.setCategoryList(categoryList);
    }

    private void setUpItemModels(){
//        items = dbManager.getItems();
//        if (!items.isEmpty()) System.out.println("yey success");
        List <Item> tempItems = new ArrayList<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()-> {
            try (Connection conn = SQLConnection.getConnection();
                 Statement stmt = conn.createStatement()){

                String query = "SELECT item_id, user_id, title, description, image, category, price " +
                        "FROM tblitem WHERE isAvailable = 1";
                ResultSet res = stmt.executeQuery(query);

                while (res.next()){
                    System.out.println("item_id: " + res.getInt("item_id"));
                    tempItems.add(new Item(
                            res.getInt("item_id"),
                            res.getInt("user_id"),
                            res.getString("title"),
                            res.getString("image"),
                            res.getString("description"),
                            res.getString("category"),
                            res.getDouble("price")
                    ));
                }

                getActivity().runOnUiThread(()->{
                    items = tempItems;
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}