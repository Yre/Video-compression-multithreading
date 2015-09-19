package com.example.yre.media_0723;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FileClientActivity extends Activity {

    EditText editTextAddress, filenameEditText2;
    Button buttonConnect;
    TextView textPort;
    TextView identityText;

    static final int SocketServerPORT = 8080;                                      //set PORT number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_client);

        editTextAddress = (EditText) findViewById(R.id.address);
        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);
        buttonConnect = (Button) findViewById(R.id.connect);
        identityText = (TextView) findViewById(R.id.identityText);
        filenameEditText2 = (EditText) findViewById(R.id.filenameEditText2);

        identityText.setText("File Transfer");

        buttonConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(FileClientActivity.this, "Connecting", Toast.LENGTH_SHORT).show();
                ClientRxThread clientRxThread = new ClientRxThread(editTextAddress.getText().toString(), SocketServerPORT);

                clientRxThread.start();
            }
        });
    }

    private class ClientRxThread extends Thread {
        String dstAddress;
        int dstPort;

        ClientRxThread(String address, int port) {                                  //initialization
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                String filename = filenameEditText2.getText().toString();

                File file;
                String path = getIntent().getStringExtra("path");

                do{
                    file = new File(path, filename);          //get the file!!!
                    filename = "1" + filename;
                } while(file.exists());


                byte[] bytes = new byte[1024];
                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int bytesRead = is.read(bytes, 0, bytes.length);
                bos.write(bytes, 0, bytesRead);

                bos.close();
                socket.close();


                FileClientActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(FileClientActivity.this, "Finished", Toast.LENGTH_LONG).show();
                    }});


            } catch (IOException e) {

                e.printStackTrace();


                final String eMsg = "Something wrong: " + e.getMessage();
                FileClientActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(FileClientActivity.this, eMsg, Toast.LENGTH_LONG).show();
                    }});


            } finally {
                if(socket != null){
                    try {
                        socket.close();                                     //MUST close all sockets
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}


// 7/27 morning TINA