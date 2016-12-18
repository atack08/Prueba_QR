package com.example.atack08.prueba_qr;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanner = (ZXingScannerView)findViewById(R.id.contenedorQR);

    }



    public void QRScanner (View v){

        scanner.setVisibility(View.VISIBLE);
        scanner.setResultHandler(this);
        scanner.startCamera();

    }



    @Override
    public void handleResult(Result result) {

        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        scanner.stopCamera();
        scanner.setVisibility(View.INVISIBLE);
    }
}
