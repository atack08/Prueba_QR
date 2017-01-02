package com.example.atack08.prueba_qr;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static  int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE ;
    private static int MY_PERMISSIONS_REQUEST_CAMERA ;
    private ZXingScannerView scanner;
    private InetAddress ip;
    private int puerto;
    private GestionRed gr1;
    private String cadenaQR;
    private ImageView contenedorImg;
    private GestorBBDD g1;
    private SQLiteDatabase db;
    private TextView labelDesc;

    //CONSTANTES Y VARIABLES PARA DIBUJAR EL QR EN PANTALLA
    private final static int WHITE = 0xFFFFFFFF;
    private final static int BLACK = 0xFF000000;
    private final static int WIDTH = 500;

    //VARIABLES PARA LA BBDD
    private  String db_NAME;
    private  String db_PATH;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        labelDesc = (TextView)findViewById(R.id.descProducto);
        db_NAME = "efecto2000.db";
        db_PATH = "/data/data/" + getApplicationContext().getPackageName() + "/databases/";

        peticionPermisos();



        Intent intent = new Intent(this,MenuEmergente.class);
        startActivityForResult(intent,1);

        createDatabase();

    }



    public void QRScanner (View v){

        labelDesc.setText("");

        scanner = (ZXingScannerView)findViewById(R.id.contenedorQR);
        contenedorImg = (ImageView)findViewById(R.id.contenedorIMG);

        contenedorImg.setVisibility(View.INVISIBLE);
        scanner.setVisibility(View.VISIBLE);
        scanner.setResultHandler(this);
        scanner.startCamera();

    }



    @Override
    public void handleResult(Result result) {


        reproducirSonido();

        cadenaQR = result.getText();
        Toast.makeText(this, cadenaQR, Toast.LENGTH_SHORT).show();
        scanner.stopCamera();
        scanner.setVisibility(View.INVISIBLE);

        gr1 = new GestionRed(cadenaQR,ip,puerto);
        gr1.start();

        //GENERAMOS LA IMAGEN PARA IMPRIMIRLA EN PANTALLA
        Bitmap bitmap = encodeAsBitmap();
        contenedorImg.setImageBitmap(bitmap);
        contenedorImg.setVisibility(View.VISIBLE);

        infoBBDD(cadenaQR);

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

    public void reproducirSonido(){
        MediaPlayer mp = MediaPlayer.create(this,R.raw.beep);
        mp.start();
    }


    public Bitmap encodeAsBitmap (){

        BitMatrix result;

        try {
            result = new MultiFormatWriter().encode(cadenaQR, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels =  new int [ w * h];

            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, WIDTH,0, 0, w, h);
            return bitmap;



        } catch (WriterException e) {
            e.printStackTrace();
        }


        return null;
    }


    //MÉTODO QUE DETERMINA SI EXISTE O NO LA BASE DE DATOS
    //SI NO EXISTIERA LA COPIARÍA DE LOS RECURSOS DE LA APLICACIÓN
    public void createDatabase(){

        //COMPROBAMOS SI EXISTE LA BASE DE DATOS
        File databaseFile = new File(db_PATH + db_NAME);

        if(databaseFile.exists()){
            System.out.println("EXISTEEEEEE");

        }
        else{
            System.out.println("NO EXISTE COPIANDOOOO");
            copiarDataBase();

        }
        instanciarBBDD();

    }


    public void copiarDataBase(){

        InputStream entrada = getApplicationContext().getResources().openRawResource(R.raw.efecto2000);
        String rutaSalida = db_PATH + db_NAME;

        try {

            File directorioBBDD = new File(db_PATH);
            if(!directorioBBDD.exists()){
                directorioBBDD.mkdir();
            }

            OutputStream escritura = new FileOutputStream(rutaSalida);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = entrada.read(buffer))>0){
                escritura.write(buffer, 0, length);
            }

            entrada.close();
            escritura.close();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //PETICIÓN DE PERMISOS
    public void peticionPermisos(){

        //SI NO TIENE PERMISOS PARA * LO SOLICITA
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }



    }

    public void instanciarBBDD(){

        g1 = new GestorBBDD(getApplicationContext(),db_NAME,null,1);
        db = g1.getWritableDatabase();

        if(db == null)
            Toast.makeText(this, "DATABASE NULL", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, db.getPath(), Toast.LENGTH_LONG).show();
    }

    public void infoBBDD(String idP){

        int idProducto = Integer.parseInt(idP);


        Cursor c = db.rawQuery("select descripcion from productos where id = " + idProducto, null);
        c.moveToFirst();
        String desc = c.getString(0);


        labelDesc.setText(desc);

    }

}
