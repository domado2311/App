package com.example.sidenav;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_list) {
                selectedFragment = new ListFragment();

                // ✅ Pass fullName and email from Intent to ListFragment
                String fullName = getIntent().getStringExtra("fullName");
                String email = getIntent().getStringExtra("email");

                Bundle bundle = new Bundle();
                bundle.putString("fullName", fullName);
                bundle.putString("email", email);
                selectedFragment.setArguments(bundle);
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Load default fragment
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }
}
