package com.company.ivan.balance_follow_the_way;

import android.content.Context;
import android.os.Message;
import android.os.Handler;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 0;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String TAG = "BTDevice";

    public BluetoothAdapter bluetoothAdapter = null;   //адаптер для бл.
    private OutputStream mmOutStream = null;    //Отправка данных
    private InputStream mmInStream = null;      //получение данных
    private BluetoothSocket btSocket = null;
    private Set<BluetoothDevice> pairedDevices = null;
    private TextView infoTable;
    private BluetoothConnection btConnection = null;
    private ImageView myImageView;
    private Bitmap myBitmap;
    private Canvas tempCanvas;
    private android.os.Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTable = (TextView) findViewById(R.id.infoTable);
        myImageView = (ImageView) findViewById(R.id.imageView);

        myBitmap = Bitmap.createBitmap(344, 337, Bitmap.Config.RGB_565);
        tempCanvas = new Canvas(myBitmap);

        makeHandler();  //Handler must be here
        draw(80, 80);
        BluetoothOnCreate();
    }

    private void makeHandler(){
        handler = new Handler(Looper.getMainLooper()){
          @Override
          public void handleMessage(Message inptMsg){
              try {
                  Log.d(TAG, "something is happening");
                  //infoTable.setText(inptMsg.obj.toString());
                  //Log.d(TAG, inptMsg.obj.toString());
              } catch(Exception e){
                  Log.d(TAG, "something is happening");
                  //Log.d(TAG, inptMsg.obj.toString());
                  //Log.d(TAG, "can't show you data");
              }
          }
        };
    }

    public void GetRoboInfo(View view){
        if (!btSocket.isConnected()){
            return;
        }
        //Отправка команды
        JSONObject obj = new JSONObject();
        try{
            obj.put("c", new Integer(1));
        }catch (Exception e){}
        byte [] bytes = (obj.toString()).getBytes();
        try{
            Log.d(TAG, "Trying to write this data: " + obj.toString());
            btConnection.write(bytes);
            Log.d(TAG, "writeData was success");
        }catch (Exception e){
            Log.d(TAG, "Error occurred when sending data", e);
        }
    }

    public void Connect(View view){
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()){
            Log.d(TAG, "search paired devices");
            for (BluetoothDevice device : pairedDevices){
                //Log.d(TAG, "name:" + device.getName() + ", adress:" + device.getAddress());
                if (device.getName().contentEquals("HC-05") || device.getAddress().contentEquals("00:21:13:02:C1:31")){
                    infoTable.setText("device is founded");
                    Log.d(TAG, "device is founded. Trying to connect to device");
                    ConnectThread(device);
                }
            }
        }
        else Log.d(TAG, "paired devices is empty");
    }

    public void ConnectThread(BluetoothDevice device){
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.d(TAG, "Creating of socket");
            try {
                btSocket.connect();
                Log.d(TAG, "device was connected");
                btConnection = new BluetoothConnection(btSocket, handler);
                btConnection.start();
            } catch (IOException e){
                infoTable.setText("connection was failed");
                Log.d(TAG, "connection was failed");
            }
        } catch (Exception e){
            Log.d(TAG, "1");
        }
    }

    private void BluetoothOnCreate(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (bluetoothAdapter == null){
            //device doesn't support Bluetooth
            Log.d(TAG, "BTAdapter is null");
            return;
        }
    }

    private void draw(int x, int y){
            try{
            Paint p = new Paint();
            p.setColor(Color.BLUE);
            tempCanvas.drawCircle(x, y, 1, p);
            myImageView.setImageDrawable(new BitmapDrawable(getResources(), myBitmap));
        } catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }
}
