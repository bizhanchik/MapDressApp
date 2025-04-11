package com.example.mapdress;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mapdress.fragments.MapFragment;
import com.example.mapdress.fragments.SettingsFragment;
import com.example.mapdress.fragments.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);

        if (savedInstanceState == null) {
            replaceFragment(new MapFragment());
        }

        bottomNavView.getOrCreateBadge(R.id.menuItemMap);
        bottomNavView.getOrCreateBadge(R.id.menuItemSettings);
        bottomNavView.getOrCreateBadge(R.id.menuItemStore);

        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.menuItemMap){
                    selectedFragment = new MapFragment();
                } else if (item.getItemId() == R.id.menuItemStore) {
                    selectedFragment = new StoreFragment();
                } else if (item.getItemId() == R.id.menuItemSettings) {
                    selectedFragment = new SettingsFragment();
                }

                replaceFragment(selectedFragment);
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
