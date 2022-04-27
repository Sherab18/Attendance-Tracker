package com.fyp.tracker;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String EMAIL_KEY = "email_key";
    private static final String USER_KEY = "user_key";
    private static final String TOKEN_KEY = "token_key";
    private static final String NAME_KEY = "name";
    SharedPreferences.Editor editor;
    Button login_btn;
    EditText email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        email = findViewById(R.id.email_btn);
        password = findViewById(R.id.pass);


        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(MainActivity.this, "Email or Password Required", Toast.LENGTH_SHORT).show();
                }else{

                    login();
                }
            }
        });
    }
    public void login(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email.getText().toString());
        loginRequest.setPassword(password.getText().toString());
        Call<LoginResponse> loginResponseCall = ApiClient.getUserService().userLogin(loginRequest);

        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()){

                    editor.putString(EMAIL_KEY, response.body().getAuthuser().getEmail()).apply();
                    editor.putString(TOKEN_KEY, response.body().getToken()).apply();
                    editor.putString(USER_KEY, response.body().getAuthuser().get_id()).apply();
                    editor.putString(NAME_KEY, response.body().getAuthuser().getName()).apply();


                    Intent intent= new Intent(MainActivity.this, scanner_faceactivity.class);
                startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Login failed !", Toast.LENGTH_SHORT).show();

            }
        });
    }
    }

