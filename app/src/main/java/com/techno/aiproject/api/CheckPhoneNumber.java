package com.techno.aiproject.api;

import com.techno.aiproject.models.FrogRequest;
import com.techno.aiproject.models.PublicRes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CheckPhoneNumber {
    @Headers({
            "securitykey:xyz12262000"
    })
    @POST("checkPhoneNumber.php")
    Call<PublicRes> SendCode(@Body FrogRequest request);
}
