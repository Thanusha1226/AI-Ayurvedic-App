package com.techno.aiproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.techno.aiproject.R;
import com.techno.aiproject.activities.auth.ForgetPasswordActivity;
import com.techno.aiproject.activities.auth.LoginActivity;
import com.techno.aiproject.utils.PrefManager;

public class SettingsActivity extends AppCompatActivity {
    private ImageView btnBack;
    private RadioGroup radioLanguage;
    private LinearLayout btnChangePassword, btnLogout;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefManager = new PrefManager(this);

        btnBack = findViewById(R.id.btnBack);
        radioLanguage = findViewById(R.id.radioLanguage);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> finish());

        // Initialize values
        String currentLang = prefManager.getLanguage();
        if ("en".equals(currentLang)) {
            radioLanguage.check(R.id.radioEnglish);
        } else if ("si".equals(currentLang)) {
            radioLanguage.check(R.id.radioSinhala);
        } else if ("ta".equals(currentLang)) {
            radioLanguage.check(R.id.radioTamil);
        }
        // Set up Listeners
        radioLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLang = "en";
            if (checkedId == R.id.radioSinhala) {
                selectedLang = "si";
            } else if (checkedId == R.id.radioTamil) {
                selectedLang = "ta";
            }
            prefManager.setLanguage(selectedLang);
            Toast.makeText(SettingsActivity.this, "Language updated successfully", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ForgetPasswordActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            prefManager.clearSession();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(SettingsActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
        });
    }
}
