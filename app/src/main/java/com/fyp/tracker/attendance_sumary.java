package com.fyp.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class attendance_sumary extends AppCompatActivity {
    private static final String USER_KEY = "user_key";
    private static final String TOKEN_KEY = "token_key";
    private OurRetrofitClient ourRetrofitClient;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    TextView heading, m_name,mark_attendance,period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sumary);

        heading = findViewById(R.id.heading);
        mark_attendance = findViewById(R.id.mark_attendance);
        period = findViewById(R.id.period);


        ImageView img = (ImageView) findViewById(R.id.logout_btn);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(attendance_sumary.this, MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(attendance_sumary.this, "Successfully Logout", Toast.LENGTH_SHORT).show();
            }
        });
        Bundle bundle = getIntent().getExtras();
        String Qrcode = bundle.getString("qrcode");
        String classId = Qrcode.split("--")[0];
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(attendance_sumary.this);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://attendance-management-api.herokuapp.com/students/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        APIService service = retrofit.create(APIService.class);
        AttendanceRequest attendanceRequest = new AttendanceRequest("P");

        String url = "put-present";
        Call<Attendance> attendanceCall = service.putAttendance(url,sharedPreferences.getString(USER_KEY,"00"),
                classId,
                sharedPreferences.getString(TOKEN_KEY,"00"),attendanceRequest);
        progressDialog = new ProgressDialog(attendance_sumary.this);
        progressDialog.setMessage("Recording Attendance...");
        attendanceCall.enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                if(response.code()==200){

                    heading.setText("Your Attendance is recorded");
                    mark_attendance.setText("Present");
                    period.setText(response.body().getCreatedAt());
                }
                else{
                    heading.setText("This attendance is already closed!");
                    mark_attendance.setText("Absent");

                }



            }

            @Override
            public void onFailure(Call<Attendance> call, Throwable t) {
                heading.setText("This attendance is already closed!");
                mark_attendance.setText("Absent");
            }
        });
        progressDialog.dismiss();
    }
}