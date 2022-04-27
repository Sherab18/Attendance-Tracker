package com.fyp.tracker;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

   private String token;

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public user getAuthuser() {
      return authuser;
   }

   public void setAuthuser(user authuser) {
      this.authuser = authuser;
   }

   @SerializedName("user")
   private user authuser;



}
