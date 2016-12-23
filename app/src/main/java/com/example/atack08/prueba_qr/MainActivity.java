package com.example.atack08.prueba_qr;


import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import java.net.InetAddress;
import java.net.UnknownHostException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scanner;
    private InetAddress ip;
    private int puerto;
    private GestionRed gr1;
    private String cadenaQR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanner = (ZXingScannerView)findViewById(R.id.contenedorQR);

        Intent intent = new Intent(this,MenuEmergente.class);
        startActivityForResult(intent,1);

    }



    public void QRScanner (View v){

        scanner.setVisibility(View.VISIBLE);
        scanner.setResultHandler(this);
        scanner.startCamera();

    }



    @Override
    public void handleResult(Result result) {

        cadenaQR = result.getText();
        Toast.makeText(this, cadenaQR, Toast.LENGTH_SHORT).show();
        scanner.stopCamera();
        scanner.setVisibility(View.INVISIBLE);

        gr1 = new GestionRed(cadenaQR,ip,puerto);
        gr1.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            try {
                Bundle b = data.getExtras();
                this.ip = InetAddress.getByName(b.getString("ipServidor"));
                this.puerto = b.getInt("puerto");


            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

    }
}
