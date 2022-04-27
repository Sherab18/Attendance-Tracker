package com.fyp.tracker;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface OurRetrofitClient {

   @POST("/face_rec")
   @FormUrlEncoded
   Call<ImageuploadResponse> UploadImage(@Field("image") String image);
}