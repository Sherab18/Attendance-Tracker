package com.fyp.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public interface QRCodeFoundListener {
    void onQRCodeFound(String qrCode);
    void qrCodeNotFound();
}