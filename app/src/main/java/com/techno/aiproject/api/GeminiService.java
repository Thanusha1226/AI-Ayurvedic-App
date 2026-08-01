package com.techno.aiproject.api;

import com.techno.aiproject.models.GeminiRequest;
import com.techno.aiproject.models.GeminiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiService {
    @POST("models/gemini-3.5-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body GeminiRequest request
    );
}
