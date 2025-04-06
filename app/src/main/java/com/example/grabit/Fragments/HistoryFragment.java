package com.example.grabit.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Adapter.HistoryAdapter;
import com.example.grabit.Helper.SwipeToDeleteCallback;
import com.example.grabit.Model.Voucher;
import com.example.grabit.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView historyRecyclerView;
    private TextView emptyStateTextView;
    private HistoryAdapter historyAdapter;
    private List<Voucher> voucherList;
    private FirebaseFirestore db;
    private String userId;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("sapID", "0");

        // Initialize views
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
        
        // Initialize RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        voucherList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(voucherList, requireContext());
        historyRecyclerView.setAdapter(historyAdapter);

        // Add swipe functionality
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(historyAdapter, requireContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(historyRecyclerView);

        // Load vouchers
        loadVouchers();

        return view;
    }

    private void loadVouchers() {
        db.collection("Voucher")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        voucherList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Voucher voucher = document.toObject(Voucher.class);
                            voucherList.add(voucher);
                        }
                        
                        // Update UI based on list size
                        if (voucherList.isEmpty()) {
                            historyRecyclerView.setVisibility(View.GONE);
                            emptyStateTextView.setVisibility(View.VISIBLE);
                        } else {
                            historyRecyclerView.setVisibility(View.VISIBLE);
                            emptyStateTextView.setVisibility(View.GONE);
                        }
                        
                        historyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading vouchers", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}