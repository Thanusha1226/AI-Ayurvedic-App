package com.techno.aiproject.api;

import com.techno.aiproject.models.PlantNetResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PlantNetService {
    @Multipart
    @POST("identify/all")
    Call<PlantNetResponse> identify(
            @Query("api-key") String apiKey,
            @Part MultipartBody.Part image,
            @Part("organs") RequestBody organ
    );
}
