package com.fyp.tracker;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIService {
   @POST
   Call<Attendance> putAttendance(@Url String url,
                                  @Query("studentId") String studentId,
                                  @Query("moduleId") String moduleId,
                                  @Header("Authorization") String authHeader,
                                  @Body AttendanceRequest attendance);
}
