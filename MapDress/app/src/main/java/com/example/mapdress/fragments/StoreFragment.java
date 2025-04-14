package com.example.mapdress.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapdress.R;
import com.example.mapdress.adapter.ClothingAdapter;
import com.example.mapdress.item.ClothingItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClothingAdapter adapter;
    private List<ClothingItem> clothingItems;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 колонки

        clothingItems = new ArrayList<>();
        adapter = new ClothingAdapter(getContext(), clothingItems);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("clothing_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ClothingItem item = doc.toObject(ClothingItem.class);
                        clothingItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                });

        return view;
    }
}
