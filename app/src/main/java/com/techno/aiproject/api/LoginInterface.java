package com.techno.aiproject.api;

import com.techno.aiproject.models.LoginResponce;
import com.techno.aiproject.models.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginInterface {
    @Headers({
            "securitykey:xyz12262000"
    })
    @POST("login.php")
    Call<LoginResponce> logResponce(@Body LoginRequest request);
}
