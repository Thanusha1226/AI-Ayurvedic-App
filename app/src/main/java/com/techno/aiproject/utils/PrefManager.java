package com.techno.aiproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class PrefManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "HerbalEyesPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_ONBOARDING_COMPLETE = "onboardingComplete";
    private static final String KEY_LANGUAGE = "selectedLanguage"; // "en", "si", "ta"
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_SCAN_COUNT = "scanCount";
    private static final String KEY_FIRST_SCAN_TIMESTAMP = "firstScanTimestamp";
    private static final String KEY_GEMINI_REQUEST_COUNT = "geminiRequestCount";
    private static final String KEY_FIRST_GEMINI_REQUEST_TIMESTAMP = "firstGeminiRequestTimestamp";

    public PrefManager(Context context) {
        SharedPreferences prefs;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        sharedPreferences = prefs;
        editor = sharedPreferences.edit();
    }

    public void setLoggedIn(boolean isLoggedIn, String userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public void setUserDetails(String userId, String name, String email, String phone, String deviceId) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_DEVICE_ID, deviceId);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }

    public String getDeviceId() {
        return sharedPreferences.getString(KEY_DEVICE_ID, "");
    }

    public void setOnboardingComplete(boolean complete) {
        editor.putBoolean(KEY_ONBOARDING_COMPLETE, complete);
        editor.apply();
    }

    public boolean isOnboardingComplete() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    public void setLanguage(String langCode) {
        editor.putString(KEY_LANGUAGE, langCode);
        editor.apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "en"); // Default is English
    }

    public void setDarkMode(boolean enabled) {
        editor.putBoolean(KEY_DARK_MODE, enabled);
        editor.apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void clearSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_USER_ID, "");
        editor.apply();
    }

    public boolean canPerformScan() {
        long currentTime = System.currentTimeMillis();
        long firstScanTime = sharedPreferences.getLong(KEY_FIRST_SCAN_TIMESTAMP, 0);
        int count = sharedPreferences.getInt(KEY_SCAN_COUNT, 0);

        if (firstScanTime == 0) {
            return true;
        }

        long elapsedTime = currentTime - firstScanTime;
        if (elapsedTime >= 24 * 60 * 60 * 1000) {
            // 24 hours have passed, reset cycle
            editor.putLong(KEY_FIRST_SCAN_TIMESTAMP, 0);
            editor.putInt(KEY_SCAN_COUNT, 0);
            editor.apply();
            return true;
        }

        return count < 3;
    }

    public void incrementScanCount() {
        long currentTime = System.currentTimeMillis();
        long firstScanTime = sharedPreferences.getLong(KEY_FIRST_SCAN_TIMESTAMP, 0);
        int count = sharedPreferences.getInt(KEY_SCAN_COUNT, 0);

        if (firstScanTime == 0) {
            // Start of a new 24-hour cycle
            editor.putLong(KEY_FIRST_SCAN_TIMESTAMP, currentTime);
            editor.putInt(KEY_SCAN_COUNT, 1);
        } else {
            long elapsedTime = currentTime - firstScanTime;
            if (elapsedTime >= 24 * 60 * 60 * 1000) {
                // Reset cycle
                editor.putLong(KEY_FIRST_SCAN_TIMESTAMP, currentTime);
                editor.putInt(KEY_SCAN_COUNT, 1);
            } else {
                editor.putInt(KEY_SCAN_COUNT, count + 1);
            }
        }
        editor.apply();
    }

    public long getScanCooldownRemainingTime() {
        long currentTime = System.currentTimeMillis();
        long firstScanTime = sharedPreferences.getLong(KEY_FIRST_SCAN_TIMESTAMP, 0);
        int count = sharedPreferences.getInt(KEY_SCAN_COUNT, 0);

        if (firstScanTime == 0 || count < 3) {
            return 0;
        }

        long elapsedTime = currentTime - firstScanTime;
        long limitTime = 24 * 60 * 60 * 1000;
        if (elapsedTime >= limitTime) {
            return 0;
        }

        return limitTime - elapsedTime;
    }

    /**
     * Checks if a Gemini API request can be performed (limit: 3 per 24 hours)
     */
    public boolean canPerformGeminiRequest() {
        long currentTime = System.currentTimeMillis();
        long firstRequestTime = sharedPreferences.getLong(KEY_FIRST_GEMINI_REQUEST_TIMESTAMP, 0);
        int count = sharedPreferences.getInt(KEY_GEMINI_REQUEST_COUNT, 0);

        if (firstRequestTime == 0) {
            return true;
        }

        long elapsedTime = currentTime - firstRequestTime;
        if (elapsedTime >= 24 * 60 * 60 * 1000) {
            // 24 hours have passed, reset cycle
            editor.putLong(KEY_FIRST_GEMINI_REQUEST_TIMESTAMP, 0);
            editor.putInt(KEY_GEMINI_REQUEST_COUNT, 0);
            editor.apply();
            return true;
        }

        return count < 3;
    }

    /**
     * Increments the Gemini API request count
     */
    public void incrementGeminiRequestCount() {
        long currentTime = System.currentTimeMillis();
        long firstRequestTime = sharedPreferences.getLong(KEY_FIRST_GEMINI_REQUEST_TIMESTAMP, 0);
        int count = sharedPreferences.getInt(KEY_GEMINI_REQUEST_COUNT, 0);

        if (firstRequestTime == 0) {
            // Start of a new 24-hour cycle
            editor.putLong(KEY_FIRST_GEMINI_REQUEST_TIMESTAMP, currentTime);
            editor.putInt(KEY_GEMINI_REQUEST_COUNT, 1);
        } else {
            long elapsedTime = currentTime - firstRequestTime;
            if (elapsedTime >= 24 * 60 * 60 * 1000) {
                // Reset cycle
                editor.putLong(KEY_FIRST_GEMINI_REQUEST_TIMESTAMP, currentTime);
                editor.putInt(KEY_GEMINI_REQUEST_COUNT, 1);
            } else {
                editor.putInt(KEY_GEMINI_REQUEST_COUNT, count + 1);
            }
        }
        editor.apply();
    }

    /**
     * Gets remaining Gemini API requests for the current 24-hour period
     */
    public int getRemainingGeminiRequests() {
        long currentTime = System.currentTimeMillis();
        long firstRequestTime = sharedPreferences.getLong(KEY_FIRST_GEMINI_REQUEST_TIMESTAMP, 0);
        int count = sharedPreferences.getInt(KEY_GEMINI_REQUEST_COUNT, 0);

        if (firstRequestTime == 0) {
            return 3;
        }

        long elapsedTime = currentTime - firstRequestTime;
        if (elapsedTime >= 24 * 60 * 60 * 1000) {
            return 3;
        }

        return Math.max(0, 3 - count);
    }

    /**
     * Gets cooldown time remaining for Gemini API (in milliseconds)
     */
    public long getGeminiCooldownRemainingTime() {
        long currentTime = System.currentTimeMillis();
        long firstRequestTime = sharedPreferences.getLong(KEY_FIRST_GEMINI_REQUEST_TIMESTAMP, 0);
        int count = sharedPreferences.getInt(KEY_GEMINI_REQUEST_COUNT, 0);

        if (firstRequestTime == 0 || count < 3) {
            return 0;
        }

        long elapsedTime = currentTime - firstRequestTime;
        long limitTime = 24 * 60 * 60 * 1000;
        if (elapsedTime >= limitTime) {
            return 0;
        }

        return limitTime - elapsedTime;
    }
}
