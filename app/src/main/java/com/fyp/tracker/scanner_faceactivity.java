package com.fyp.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class scanner_faceactivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int CAPTURE_REQUEST_CODE = 0;
    private static final String EMAIL_KEY = "email_key";
    private static final String USER_KEY = "user_key";
    private static final String TOKEN_KEY = "token_key";
    private static final String NAME_KEY = "name";
    private ImageView imageView;
    private Button camera_btn, verify_btn;
    private OurRetrofitClient ourRetrofitClient;
    private ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_faceactivity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(scanner_faceactivity.this);

        imageView = findViewById(R.id.imageView);
        camera_btn = findViewById(R.id.camera_btn);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://secure-tundra-64315.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        ourRetrofitClient = retrofit.create(OurRetrofitClient.class);
        progressDialog = new ProgressDialog(scanner_faceactivity.this);
        progressDialog.setMessage("Recognizing face...");

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckPermission()) {
                    Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(capture, CAPTURE_REQUEST_CODE);
                }
            }

        });

        ImageView img = (ImageView) findViewById(R.id.p_btn);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(scanner_faceactivity.this, about_us.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case CAPTURE_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK){
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                    progressDialog.show();
                    ImageUpload(bitmap);
                }
            }
            break;

        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void ImageUpload(Bitmap bitmap) {
        String encoded = bitmapToBase64(bitmap);
        Call <ImageuploadResponse> call = ourRetrofitClient.UploadImage(encoded);
        call.enqueue(new Callback<ImageuploadResponse> () {
            @Override
            public void onResponse(Call<ImageuploadResponse> call, Response<ImageuploadResponse> response) {
                if(response.code()==200){
                    String email = sharedPreferences.getString(EMAIL_KEY, "1");
                    String name = sharedPreferences.getString(NAME_KEY, "1");

                    if(response.body().getEmail().equals(email)){
                        Toast.makeText(scanner_faceactivity.this, "recognized "+name, Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(scanner_faceactivity.this, QRcodeScannerActivity.class);
                        startActivity(intent);

                        progressDialog.dismiss();
                    }
                    else{
                        Toast.makeText(scanner_faceactivity.this, "could not recognize "+name, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }

            @Override
            public void onFailure(Call<ImageuploadResponse> call, Throwable t) {
               Log.d("the error", String.valueOf(t));
                Toast.makeText(scanner_faceactivity.this, "Face did not match", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
     });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.share_btn:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareBody ="https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID+"\n\n";
                String shareSubject = "Attendance Tracker";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "ShareVia"));
                break;
            case R.id.rate_btn:
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean CheckPermission() {
        if (ContextCompat.checkSelfPermission(scanner_faceactivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(scanner_faceactivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(scanner_faceactivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(scanner_faceactivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(scanner_faceactivity.this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(scanner_faceactivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(scanner_faceactivity.this)
                        .setTitle("Permission")
                        .setMessage("Please accept the permissions")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(scanner_faceactivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                startActivity(new Intent(scanner_faceactivity.this, scanner_faceactivity.class));
                                scanner_faceactivity.this.overridePendingTransition(0, 0);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(scanner_faceactivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else {

            return true;

        }
    }
}