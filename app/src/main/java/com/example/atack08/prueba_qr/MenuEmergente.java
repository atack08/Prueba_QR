package com.example.atack08.prueba_qr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by atack08 on 23/12/16.
 */

public class MenuEmergente extends Activity {

    private EditText ip1,ip2,ip3,ip4;
    private EditText textPuerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ventana_emergente);

        ip1 = (EditText)findViewById(R.id.textIP1);
        ip2 = (EditText)findViewById(R.id.textIP2);
        ip3 = (EditText)findViewById(R.id.textIP3);
        ip4 = (EditText)findViewById(R.id.textIP4);
        textPuerto = (EditText)findViewById(R.id.textPuerto);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int ancho = dm.widthPixels;
        int alto = dm.heightPixels;

        getWindow().setLayout((int)(ancho*.8)  ,(int)(alto*.6));
    }

    public void asignarValoresCerrar(View v){

        String ipS1 = ip1.getText().toString();
        String ipS2 = ip2.getText().toString();
        String ipS3 = ip3.getText().toString();
        String ipS4 = ip4.getText().toString();

        String puertoS = textPuerto.getText().toString();

        if(ipS1.equals("") || ipS2.equals("") || ipS3.equals("") || ipS4.equals("") || puertoS.equals(""))
            Toast.makeText(this, "FALTAN DATOS OBLIGATORIOS", Toast.LENGTH_SHORT).show();
        else{
            String direccionIP = ipS1+"."+ipS2+"."+ipS3+"."+ipS4;
            Intent intent = new Intent();

            Bundle b = new Bundle();
            b.putString("ipServidor",direccionIP);
            b.putInt("puerto",Integer.parseInt(puertoS));
            intent.putExtras(b);

            setResult(RESULT_OK, intent);
            finish();

        }




    }
}
