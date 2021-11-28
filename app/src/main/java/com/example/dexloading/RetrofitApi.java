package com.example.dexloading;

import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RetrofitApi {
    @GET("/")
    @Streaming
    Call<ResponseBody> downloadDex();

}
