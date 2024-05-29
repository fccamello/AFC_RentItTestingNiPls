package com.example.afc_rentit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.afc_rentit.Database.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView profUsername, profFirstName, profLastName, profGender, profAddress, profEmail, profContact, profUserType;
    TextView profFullName;
    EditText editUsername, editFirstName, editLastName, editGender, editAddress, editContact;
    public String usern, firstn, lastn, gender, address, contactn;
    public String fulln;
    Button btnEditAcc, btnDeleteAcc, btnLogOut, btnConfirmEdit, btnCancelEdit;
    ConstraintLayout layoutProfDisplay, layoutProfEdit;
    Current_User currUser = Current_User.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiate(view);
        setProfDetails();

        btnDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAcc();
            }
        });

        btnEditAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutProfDisplay.setVisibility(View.INVISIBLE);
                layoutProfEdit.setVisibility(View.VISIBLE);
                getProfEditDetails();
            }
        });

        btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfEditDetails();
                editAcc();

                currUser.setUsername(usern);
                currUser.setFirstname(firstn);
                currUser.setLastname(lastn);
                currUser.setGender(gender);
                currUser.setAddress(address);
                currUser.setContact_number(contactn);

                setProfDetails();
                layoutProfEdit.setVisibility(View.INVISIBLE);
                layoutProfDisplay.setVisibility(View.VISIBLE);
            }
        });

        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutProfEdit.setVisibility(View.INVISIBLE);
                layoutProfDisplay.setVisibility(View.VISIBLE);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Activity_log_in.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void setProfDetails() {
        String usertype;

        profUsername.setText("@"+currUser.getUsername());
//        profFirstName.setText(currUser.getFirstname());
//        profLastName.setText(currUser.getLastname());
        profFullName.setText(fulln);
        profGender.setText("Gender: "+currUser.getGender());
        profAddress.setText("Address: "+currUser.getAddress());
        profEmail.setText("Email: "+currUser.getEmail());
        profContact.setText("Contact Number: "+currUser.getContact_number());

        if(currUser.isOwner()) {
            usertype = "Seller";
        } else {
            usertype = "Buyer";
        }

        profUserType.setText(usertype);

    }

    public void getProfEditDetails() {
        editUsername.setText(currUser.getUsername());
        editFirstName.setText(currUser.getFirstname());
        editLastName.setText(currUser.getLastname());
        editGender.setText(currUser.getGender());
        editAddress.setText(currUser.getAddress());
        editContact.setText(currUser.getContact_number());
    }

    public void setProfEditDetails() {
        usern = editUsername.getText().toString();
        firstn = editFirstName.getText().toString();
        lastn = editLastName.getText().toString();

        fulln = firstn + " " + lastn;
        gender = editGender.getText().toString();
        address = editAddress.getText().toString();
        contactn = editContact.getText().toString();
    }
    public void deleteAcc() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection c = SQLConnection.getConnection();
                 PreparedStatement stmt = c.prepareStatement(
                         "DELETE FROM tbluser WHERE user_id = ?")){
                stmt.setInt(1,currUser.getUser_id());
                stmt.executeUpdate();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        });
        Intent intent = new Intent(getActivity(), Activity_sign_up.class);
        startActivity(intent);
    }

    public void editAcc() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try(Connection c = SQLConnection.getConnection();
                PreparedStatement stmt = c.prepareStatement(
                        "UPDATE tbluser SET firstname = ?, lastname = ?, gender = ?, address = ?, contact_number = ?, username = ? WHERE user_id = ?"
                )) {
                stmt.setString(1,firstn);
                stmt.setString(2,lastn);
                stmt.setString(3,gender);
                stmt.setString(4,address);
                stmt.setString(5,contactn);
                stmt.setString(6,usern);
                stmt.setInt(7,currUser.getUser_id());
                stmt.executeUpdate();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void initiate(View view) {
        profUsername = view.findViewById(R.id.profUsername);
//        profFirstName = view.findViewById(R.id.profFullName);
//        profLastName = view.findViewById(R.id.profLastName);
        profUserType = view.findViewById(R.id.profUserType);

        profGender = view.findViewById(R.id.profGender);
        profAddress = view.findViewById(R.id.profAddress);
        profEmail = view.findViewById(R.id.profEmail);
        profContact = view.findViewById(R.id.profContact);

        profFullName = view.findViewById(R.id.profFullName);

        editUsername = view.findViewById(R.id.editUsername);
        editFirstName = view.findViewById(R.id.editFirstName);
        editLastName = view.findViewById(R.id.editLastName);
        editGender = view.findViewById(R.id.editGender);
        editAddress = view.findViewById(R.id.editAddress);
        editContact = view.findViewById(R.id.editContact);

        btnDeleteAcc = view.findViewById(R.id.btnDeleteAcc);
        btnEditAcc = view.findViewById(R.id.btnEditAcc);
        btnLogOut = view.findViewById(R.id.btnLogOut);

        btnConfirmEdit = view.findViewById(R.id.btnConfirmEdit);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);

        layoutProfDisplay = view.findViewById(R.id.layoutProfDisplay);
        layoutProfEdit = view.findViewById(R.id.layoutProfEdit);

    }
}