package com.techno.aiproject.api;

import com.techno.aiproject.models.PublicRes;
import com.techno.aiproject.models.RegReq;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SubmitDetails {
    @Headers({
            "securitykey:xyz12262000"
    })
    @POST("register.php")
    Call<PublicRes> subdetails(@Body RegReq request);
}
