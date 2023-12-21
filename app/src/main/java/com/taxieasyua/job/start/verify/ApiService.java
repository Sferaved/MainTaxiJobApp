package com.taxieasyua.job.start.verify;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("android/verifyBlackListUser/{userEmail}/{application}")
    Call<ResponseModel> verifyUser(
            @Path("userEmail") String userEmail,
            @Path("application") String application
    );
}


