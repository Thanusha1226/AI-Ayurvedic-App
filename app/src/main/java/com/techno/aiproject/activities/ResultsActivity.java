package com.techno.aiproject.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.techno.aiproject.R;
import com.techno.aiproject.database.PlantHistory;
import com.techno.aiproject.utils.MarkdownUtils;
import com.techno.aiproject.utils.PrefManager;
import com.techno.aiproject.viewmodel.FavoritesViewModel;
import com.techno.aiproject.viewmodel.IdentifyViewModel;

import java.io.File;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {
    private static final String TAG = "ResultsActivity";
    
    private ImageView ivPlantResult, btnBack, btnShare, btnFavorite;
    private TextView tvCommonName, tvScientificName, tvExplanation, tvLoadingStatus;
    private Button btnTTS, btnCopy;
    private Button btnEnglish, btnSinhala, btnTamil;
    private RelativeLayout loadingOverlay;

    private IdentifyViewModel identifyViewModel;
    private FavoritesViewModel favoritesViewModel;
    private PrefManager prefManager;
    private TextToSpeech tts;

    private String imagePath;
    private String currentScientificName;
    private String currentCommonName;
    private String currentExplanation;
    private String selectedLanguage;
    private boolean isFavorite = false;
    private boolean scanCounted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        prefManager = new PrefManager(this);
        selectedLanguage = prefManager.getLanguage();

        if (savedInstanceState != null) {
            scanCounted = savedInstanceState.getBoolean("scan_counted", false);
        }

        // Bind Views
        ivPlantResult = findViewById(R.id.ivPlantResult);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnFavorite = findViewById(R.id.btnFavorite);
        tvCommonName = findViewById(R.id.tvCommonName);
        tvScientificName = findViewById(R.id.tvScientificName);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvLoadingStatus = findViewById(R.id.tvLoadingStatus);
        btnTTS = findViewById(R.id.btnTTS);
        btnCopy = findViewById(R.id.btnCopy);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnSinhala = findViewById(R.id.btnSinhala);
        btnTamil = findViewById(R.id.btnTamil);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // ViewModels Setup
        identifyViewModel = new ViewModelProvider(this).get(IdentifyViewModel.class);
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        // Setup TTS
        setupTextToSpeech();

        // Check Intent Extras
        Intent intent = getIntent();
        imagePath = intent.getStringExtra("image_path");

        if (intent.hasExtra("scientific_name")) {
            // Re-viewing from history/favorites
            currentScientificName = intent.getStringExtra("scientific_name");
            currentCommonName = intent.getStringExtra("common_name");
            currentExplanation = intent.getStringExtra("explanation");
            displayResultsDirectly();
        } else if (imagePath != null) {
            // Triggering new analysis pipeline
            startPlantAnalysis();
        } else {
            Toast.makeText(this, "No plant information found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupListeners();
        setupLanguageButtons();
    }

    private void setupTextToSpeech() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "TTS Language is not supported or missing data.");
                }
            } else {
                Log.e(TAG, "TTS Initialization Failed.");
            }
        });
    }

    private void startPlantAnalysis() {
        identifyViewModel.getProgressState().observe(this, status -> {
            if (status != null) {
                loadingOverlay.setVisibility(View.VISIBLE);
                tvLoadingStatus.setText(status);
            } else {
                loadingOverlay.setVisibility(View.GONE);
            }
        });

        identifyViewModel.getIdentifySuccess().observe(this, history -> {
            if (history != null) {
                currentScientificName = history.scientificName;
                currentCommonName = history.commonName;
                currentExplanation = history.explanationText;
                displayResultsDirectly();
                if (!scanCounted) {
                    prefManager.incrementScanCount();
                    scanCounted = true;
                }
            }
        });

        identifyViewModel.getIdentifyError().observe(this, error -> {
            if (error != null) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(ResultsActivity.this, error, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        identifyViewModel.startIdentification(imagePath, selectedLanguage);
    }

    private void displayResultsDirectly() {
        tvCommonName.setText(currentCommonName);
        tvScientificName.setText(currentScientificName);
        
        // Render Markdown into HTML text view
        String htmlText = MarkdownUtils.markdownToHtml(currentExplanation);
        tvExplanation.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));

        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(this)
                    .load(new File(imagePath))
                    .placeholder(R.drawable.ic_plant_placeholder)
                    .centerCrop()
                    .into(ivPlantResult);
        }

        // Check if bookmarked
        favoritesViewModel.getIsFav().observe(this, isFav -> {
            isFavorite = isFav;
            if (isFavorite) {
                btnFavorite.setImageResource(R.drawable.ic_heart_filled);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_heart_outline);
            }
        });
        favoritesViewModel.checkIsFavorite(currentScientificName);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> {
            if (currentScientificName != null) {
                favoritesViewModel.toggleFavorite(currentScientificName, currentCommonName, imagePath, currentExplanation);
                String msg = isFavorite ? "Removed from Favorites" : "Added to Favorites";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        btnCopy.setOnClickListener(v -> {
            if (currentExplanation != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Ayurvedic Report", currentExplanation);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Report copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTTS.setOnClickListener(v -> {
            if (currentExplanation != null && tts != null) {
                // Strip HTML tags for clean audio narration
                String plainText = Html.fromHtml(MarkdownUtils.markdownToHtml(currentExplanation), Html.FROM_HTML_MODE_LEGACY).toString();
                tts.speak(plainText, TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(this, "Speaking...", Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(v -> {
            if (currentCommonName != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "HerbalEyes: " + currentCommonName);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Identify plant: " + currentCommonName + " (" + currentScientificName + ")\n\n" + currentExplanation);
                startActivity(Intent.createChooser(shareIntent, "Share Plant Report"));
            }
        });
    }

    private void setupLanguageButtons() {
        updateLanguageButtonState();

        btnEnglish.setOnClickListener(v -> changeSelectedLanguage("en"));
        btnSinhala.setOnClickListener(v -> changeSelectedLanguage("si"));
        btnTamil.setOnClickListener(v -> changeSelectedLanguage("ta"));
    }

    private void changeSelectedLanguage(String lang) {
        if (!selectedLanguage.equals(lang)) {
            selectedLanguage = lang;
            prefManager.setLanguage(lang);
            updateLanguageButtonState();
            
            // Re-run analysis pipeline to fetch description in new language
            if (imagePath != null) {
                startPlantAnalysis();
            } else {
                Toast.makeText(this, "Scan again to get explanation in " + lang, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateLanguageButtonState() {
        btnEnglish.setBackgroundResource(R.drawable.bg_card_rounded);
        btnEnglish.setTextColor(getResources().getColor(R.color.green_primary));
        btnSinhala.setBackgroundResource(R.drawable.bg_card_rounded);
        btnSinhala.setTextColor(getResources().getColor(R.color.green_primary));
        btnTamil.setBackgroundResource(R.drawable.bg_card_rounded);
        btnTamil.setTextColor(getResources().getColor(R.color.green_primary));

        if ("en".equals(selectedLanguage)) {
            btnEnglish.setBackgroundResource(R.drawable.bg_button_green);
            btnEnglish.setTextColor(getResources().getColor(R.color.white));
        } else if ("si".equals(selectedLanguage)) {
            btnSinhala.setBackgroundResource(R.drawable.bg_button_green);
            btnSinhala.setTextColor(getResources().getColor(R.color.white));
        } else if ("ta".equals(selectedLanguage)) {
            btnTamil.setBackgroundResource(R.drawable.bg_button_green);
            btnTamil.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("scan_counted", scanCounted);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
