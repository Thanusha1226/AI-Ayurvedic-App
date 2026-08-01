package com.techno.aiproject.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.techno.aiproject.api.ApiClient;
import com.techno.aiproject.database.AppDatabase;
import com.techno.aiproject.database.FavoritePlant;
import com.techno.aiproject.database.PlantHistory;
import com.techno.aiproject.models.GeminiRequest;
import com.techno.aiproject.utils.CheckConnection;
import com.techno.aiproject.models.GeminiResponse;
import com.techno.aiproject.models.PlantNetResponse;
import com.techno.aiproject.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class PlantRepository {
    private static final String TAG = "PlantRepository";
    private final Context context;
    private final AppDatabase database;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface PipelineCallback {
        void onSuccess(PlantHistory plantHistory);
        void onError(String message);
        void onProgress(String status);
    }

    public PlantRepository(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
    }

    public void identifyAndAnalyze(final String imagePath, final String language, final PipelineCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (!isConnected()) {
                    postError(callback, "Please connect to the internet and try again.");
                    return;
                }

                postProgress(callback, "Compressing and preparing image...");
                String base64Image = getBase64Image(imagePath);
                if (base64Image == null) {
                    postError(callback, "Failed to read image file.");
                    return;
                }

                // PlantNet is temporarily disabled. Use Gemini Vision for plant identification.
                postProgress(callback, "Identifying plant with Gemini Vision...");
                tryGeminiVision(imagePath, base64Image, language, callback);
            }
        });
    }

    private void tryPlantNet(final String imagePath, final String base64Image, final String language, final PipelineCallback callback) {
        if (!isConnected()) {
            postError(callback, "Please connect to the internet and try again.");
            return;
        }

        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
        RequestBody organ = RequestBody.create(MediaType.parse("text/plain"), "leaf");

        ApiClient.getPlantNetService().identify(Constants.PLANTNET_API_KEY, body, organ)
                .enqueue(new retrofit2.Callback<PlantNetResponse>() {
                    @Override
                    public void onResponse(Call<PlantNetResponse> call, Response<PlantNetResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().results != null && !response.body().results.isEmpty()) {
                            PlantNetResponse.Result topResult = response.body().results.get(0);
                            if (topResult.score >= 0.1) {
                                String scientificName = topResult.species.scientificNameWithoutAuthor;
                                String commonName = (topResult.species.commonNames != null && !topResult.species.commonNames.isEmpty()) 
                                        ? topResult.species.commonNames.get(0) : scientificName;
                                postProgress(callback, "Plant identified: " + scientificName + ". Generating Ayurvedic info...");
                                generateExplanation(imagePath, scientificName, commonName, language, callback);
                                return;
                            }
                        }
                        Log.d(TAG, "PlantNet failed or returned low confidence. Trying Gemini Vision...");
                        executor.execute(() -> tryGeminiVision(imagePath, base64Image, language, callback));
                    }

                    @Override
                    public void onFailure(Call<PlantNetResponse> call, Throwable t) {
                        Log.e(TAG, "PlantNet query failed: " + t.getMessage() + ". Trying Gemini Vision...");
                        executor.execute(() -> tryGeminiVision(imagePath, base64Image, language, callback));
                    }
                });
    }

    private void tryGeminiVision(final String imagePath, final String base64Image, final String language, final PipelineCallback callback) {
        if (!isConnected()) {
            postError(callback, "Please connect to the internet and try again.");
            return;
        }

        postProgress(callback, "Running plant identification (Gemini Vision)...");
        String identifyPrompt = "Identify the botanical/scientific name and common name of the plant in this image. " +
                "Respond in this exact format: SCIENTIFIC: <scientific_name> | COMMON: <common_name>. " +
                "If the image does not contain any plant or leaves, answer exactly: NOT_A_PLANT";

        ApiClient.getGeminiService().generateContent(Constants.GEMINI_API_KEY, new GeminiRequest(identifyPrompt, "image/jpeg", base64Image))
                .enqueue(new retrofit2.Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String resultText = response.body().getText().trim();
                            if (resultText.toUpperCase().contains("NOT_A_PLANT")) {
                                postError(callback, "Image validation failed: No plant detected in the uploaded image.");
                                return;
                            }
                            try {
                                String scientificName = "";
                                String commonName = "";
                                if (resultText.contains("SCIENTIFIC:") && resultText.contains("COMMON:")) {
                                    String[] parts = resultText.split("\\|");
                                    scientificName = parts[0].replace("SCIENTIFIC:", "").trim();
                                    commonName = parts[1].replace("COMMON:", "").trim();
                                } else {
                                    scientificName = resultText;
                                    commonName = resultText;
                                }
                                postProgress(callback, "Plant identified: " + scientificName + ". Generating Ayurvedic info...");
                                generateExplanation(imagePath, scientificName, commonName, language, callback);
                            } catch (Exception e) {
                                postError(callback, "Failed to parse plant details from emergency model.");
                            }
                        } else {
                            postError(callback, "Plant identification failed.");
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        postError(callback, "Plant identification network error: " + t.getMessage());
                    }
                });
    }

    private void generateExplanation(final String imagePath, final String scientificName, final String commonName, final String language, final PipelineCallback callback) {
        generateExplanationWithRetry(imagePath, scientificName, commonName, language, callback, 0);
    }

    private void generateExplanationWithRetry(final String imagePath, final String scientificName, final String commonName, final String language, final PipelineCallback callback, final int retryCount) {
        final int MAX_RETRIES = 2;
        
        String langName = "English";
        if (Constants.LANG_SINHALA.equals(language)) {
            langName = "Sinhala";
        } else if (Constants.LANG_TAMIL.equals(language)) {
            langName = "Tamil";
        }

        String prompt = "Write a comprehensive Ayurvedic medicinal report for the herb/plant: " + scientificName + " (commonly known as: " + commonName + "). " +
                "The report MUST be written completely in the " + langName + " language. " +
                "Format the response strictly in clear markdown using exactly the following headers:\n" +
                "# " + commonName + " (" + scientificName + ")\n" +
                "## Scientific Name\n" +
                "## Common Name\n" +
                "## Botanical Description\n" +
                "## Ayurvedic Uses\n" +
                "## Traditional Benefits\n" +
                "## Medicinal Properties\n" +
                "## Preparation Methods\n" +
                "## Usage Instructions\n" +
                "## Precautions\n" +
                "## Side Effects\n" +
                "## Safety Warnings";

        ApiClient.getGeminiService().generateContent(Constants.GEMINI_API_KEY, new GeminiRequest(prompt))
                .enqueue(new retrofit2.Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String explanation = response.body().getText();
                            executor.execute(() -> {
                                PlantHistory history = new PlantHistory(
                                        scientificName,
                                        commonName,
                                        imagePath,
                                        explanation,
                                        System.currentTimeMillis()
                                );
                                long id = database.plantHistoryDao().insert(history);
                                history.id = (int) id;
                                mainHandler.post(() -> callback.onSuccess(history));
                            });
                        } else {
                            // Retry on non-successful response
                            if (retryCount < MAX_RETRIES) {
                                Log.w(TAG, "Gemini response unsuccessful (code: " + response.code() + "). Retrying... (Attempt " + (retryCount + 2) + "/" + (MAX_RETRIES + 1) + ")");
                                postProgress(callback, "Retrying Ayurvedic info generation... (Attempt " + (retryCount + 2) + ")");
                                executor.execute(() -> {
                                    try {
                                        Thread.sleep(2000); // Wait 2 seconds before retry
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    generateExplanationWithRetry(imagePath, scientificName, commonName, language, callback, retryCount + 1);
                                });
                            } else {
                                postError(callback, "Failed to generate Ayurvedic explanation after multiple attempts. Server returned code: " + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        // Retry on timeout or network errors
                        if (retryCount < MAX_RETRIES) {
                            Log.w(TAG, "Gemini request failed: " + t.getMessage() + ". Retrying... (Attempt " + (retryCount + 2) + "/" + (MAX_RETRIES + 1) + ")");
                            postProgress(callback, "Retrying Ayurvedic info generation... (Attempt " + (retryCount + 2) + ")");
                            executor.execute(() -> {
                                try {
                                    Thread.sleep(2000); // Wait 2 seconds before retry
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                                generateExplanationWithRetry(imagePath, scientificName, commonName, language, callback, retryCount + 1);
                            });
                        } else {
                            postError(callback, "Failed to generate Ayurvedic explanation after multiple attempts: " + t.getMessage());
                        }
                    }
                });
    }

    private String getBase64Image(String path) {
        try {
            File file = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; // Resize to half
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            if (bitmap == null) return null;
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] bytes = outputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Base64 conversion failed: " + e.getMessage());
            return null;
        }
    }

    private boolean isConnected() {
        return CheckConnection.isConnected(context);
    }

    private void postProgress(final PipelineCallback callback, final String status) {
        mainHandler.post(() -> callback.onProgress(status));
    }

    private void postError(final PipelineCallback callback, final String message) {
        mainHandler.post(() -> callback.onError(message));
    }
}
