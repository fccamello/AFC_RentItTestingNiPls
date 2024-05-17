package com.example.afc_rentit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.afc_rentit.Database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpItemModels();

        item_views_container = view.findViewById(R.id.rv_ItemViews);
        item_views_container.setLayoutManager(new LinearLayoutManager(getContext()));
        item_views_container.hasFixedSize();

        item_adapter = new Home_Item_RecyclerViewAdapter(getContext(), items);
        item_views_container.setAdapter(item_adapter);
        item_adapter.notifyDataSetChanged();

        noItemView = view.findViewById(R.id.tv_NoItems);
        if (items.isEmpty()){
            noItemView.setVisibility(View.VISIBLE);
            item_views_container.setVisibility(View.INVISIBLE);
        } else {
            item_views_container.setVisibility(View.VISIBLE);
        }
    }


    private void setUpItemModels(){
        dbManager.getItems(items);
    }
}