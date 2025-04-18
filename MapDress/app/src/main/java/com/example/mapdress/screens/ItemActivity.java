package com.example.mapdress.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.mapdress.R;

public class ItemActivity extends AppCompatActivity {

    ImageView imageView;
    TextView description;
    TextView brandTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item);

        imageView = findViewById(R.id.imageView);
        description = findViewById(R.id.descriptionTextView);
        brandTextView = findViewById(R.id.brandTextView);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String weather = intent.getStringExtra("weather");
        String imageUrl = intent.getStringExtra("imageUrl");
        String brand = intent.getStringExtra("brand");

        Glide.with(this).load(imageUrl).into(imageView);
        description.setText(weather);
        brandTextView.setText(brand);

    }
}