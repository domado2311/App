package com.example.sidenav;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private Switch themeSwitch, notificationSwitch;
    private TextView selectedLanguageText;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Load saved language before inflating
        loadLocale();

        View view = inflater.inflate(R.layout.settings_fragment, container, false);



        themeSwitch = view.findViewById(R.id.switch_theme);
        notificationSwitch = view.findViewById(R.id.switch_notifications);
        selectedLanguageText = view.findViewById(R.id.tv_selected_language);
        Button logoutBtn = view.findViewById(R.id.button_logout);
        View languageLayout = view.findViewById(R.id.language_setting);

        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        // Theme switch setup
        boolean isDark = sharedPreferences.getBoolean("dark_mode", false);
        themeSwitch.setChecked(isDark);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Notifications switch logic (optional)
        boolean notifEnabled = sharedPreferences.getBoolean("notifications", true);
        notificationSwitch.setChecked(notifEnabled);
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications", isChecked).apply();
        });

        // Language selection
        languageLayout.setOnClickListener(v -> showLanguageDialog());

        // Set initial language label
        updateLanguageLabel();

        // Logout button
        logoutBtn.setOnClickListener(v -> {
            // Add any session clearing logic here
            startActivity(new Intent(requireActivity(), Login.class));
            requireActivity().finish();
        });

        return view;
    }

    private void showLanguageDialog() {
        final String[] languages = {"English", "Filipino"};
        final String[] codes = {"en", "tl"}; // Corrected language code for Filipino

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.language));
        builder.setItems(languages, (dialog, which) -> {
            setLocale(codes[which]);
            requireActivity().recreate(); // Refresh UI
        });
        builder.show();
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        Context context = requireContext().getApplicationContext();
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        sharedPreferences.edit().putString("app_lang", langCode).apply();
    }

    private void loadLocale() {
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = prefs.getString("app_lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());
    }

    private void updateLanguageLabel() {
        String currentLang = sharedPreferences.getString("app_lang", "en");
        if (currentLang.equals("tl")) {
            selectedLanguageText.setText(getString(R.string.filipino));
        } else {
            selectedLanguageText.setText(getString(R.string.english));
        }
    }
}