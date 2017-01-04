package com.example.atack08.prueba_qr;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by atack08 on 23/12/16.
 */

public class GestionRed extends Thread {

    private String cadena;
    private InetAddress ipServidor;
    private int puerto;

    public GestionRed(String c, InetAddress ip, int p){

        this.cadena = c;
        this.ipServidor = ip;
        this.puerto = p;
    }

    public void run(){

        try {
            //PONEMOS EL SERVIDOR A LA ESCUCHA PARA RECIBIR EL CODIGO QR
            Socket conexionServidor = new Socket(ipServidor,puerto);

            DataOutputStream salida = new DataOutputStream(conexionServidor.getOutputStream());

            System.out.println("Enviando la cadena/ " + cadena);

            salida.writeUTF(cadena);

            salida.close();

            conexionServidor.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
