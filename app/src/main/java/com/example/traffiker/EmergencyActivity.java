package com.example.traffiker;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EmergencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        Button btnTriggerRFID = findViewById(R.id.btnTriggerRFID);

        btnTriggerRFID.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Scanning RFID Tag...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                Toast.makeText(EmergencyActivity.this, "Tag AMB-9921 Authorized: Green Wave Active", Toast.LENGTH_LONG).show();
            }, 2000);
        });
    }
}