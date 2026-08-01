package com.techno.aiproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.techno.aiproject.R;
import com.techno.aiproject.utils.Constants;
import com.techno.aiproject.utils.PrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private TextView tvGreeting;
    private ImageView ivProfile;
    private EditText etSearch;
    private CardView cardCamera, cardGallery, cardHistory, cardFavorites, cardSettings, cardAbout;
    private ExtendedFloatingActionButton fabScan;
    private BottomNavigationView bottomNav;
    private PrefManager prefManager;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefManager = new PrefManager(this);

        tvGreeting = findViewById(R.id.tvGreeting);
        ivProfile = findViewById(R.id.ivProfile);
        etSearch = findViewById(R.id.etSearch);
        cardCamera = findViewById(R.id.cardCamera);
        cardGallery = findViewById(R.id.cardGallery);
        cardHistory = findViewById(R.id.cardHistory);
        cardFavorites = findViewById(R.id.cardFavorites);
        cardSettings = findViewById(R.id.cardSettings);
        cardAbout = findViewById(R.id.cardAbout);
        fabScan = findViewById(R.id.fabScan);
        bottomNav = findViewById(R.id.bottomNav);

        setGreetingMessage();
        setupListeners();
        setupBottomNavigation();
    }

    private void setGreetingMessage() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) {
            greeting = "Good Morning 🌿";
        } else if (hour < 17) {
            greeting = "Good Afternoon 🌿";
        } else {
            greeting = "Good Evening 🌿";
        }
        tvGreeting.setText(greeting);
    }

    private void setupListeners() {
        cardCamera.setOnClickListener(v -> {
            if (checkScanLimit()) {
                checkCameraPermissionAndCapture();
            }
        });
        fabScan.setOnClickListener(v -> {
            if (checkScanLimit()) {
                checkCameraPermissionAndCapture();
            }
        });
        
        cardGallery.setOnClickListener(v -> {
            if (checkScanLimit()) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.REQUEST_IMAGE_PICK);
            }
        });

        cardHistory.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, HistoryActivity.class)));
        cardFavorites.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FavoritesActivity.class)));
        cardSettings.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SettingsActivity.class)));
        cardAbout.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AboutActivity.class)));

        ivProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SettingsActivity.class)));
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_history) {
                startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
                return true;
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                return true;
            }
            return id == R.id.nav_home;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_PERMISSION);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                Log.e(TAG, "Error creating image file: " + ex.getMessage());
                Toast.makeText(this, "Could not create image file.", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "No camera application found.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to capture photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && currentPhotoPath != null) {
                navigateToResults(currentPhotoPath);
            } else if (requestCode == Constants.REQUEST_IMAGE_PICK && data != null && data.getData() != null) {
                String selectedPath = getPathFromUri(data.getData());
                if (selectedPath != null) {
                    navigateToResults(selectedPath);
                } else {
                    Toast.makeText(this, "Failed to load selected image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        try {
            File cacheFile = File.createTempFile("scan_", ".jpg", getCacheDir());
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(cacheFile);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return cacheFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "Error copying uri: " + e.getMessage());
            return null;
        }
    }

    private boolean checkScanLimit() {
        if (!prefManager.canPerformScan()) {
            long remainingMs = prefManager.getScanCooldownRemainingTime();
            long hours = remainingMs / (1000 * 60 * 60);
            long minutes = (remainingMs % (1000 * 60 * 60)) / (1000 * 60);
            String message;
            if (hours > 0) {
                message = String.format("Scan limit reached! Please wait %d hours and %d minutes.", hours, minutes);
            } else {
                message = String.format("Scan limit reached! Please wait %d minutes.", minutes);
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void navigateToResults(String imagePath) {
        Intent intent = new Intent(HomeActivity.this, ResultsActivity.class);
        intent.putExtra("image_path", imagePath);
        startActivity(intent);
    }
}
