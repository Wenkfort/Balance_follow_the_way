package com.company.ivan.balance_follow_the_way;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.Set;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 0;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothAdapter bluetoothAdapter = null;   //адаптер для бл.
    private OutputStream mmOutStream = null;    //Отправка данных
    private InputStream mmInStream = null;      //получение данных
    private BluetoothSocket btSocket = null;
    private Set<BluetoothDevice> pairedDevices = null;
    private String TAG = "BTDevice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothOnCreate();
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
        byte [] bytes = (obj.toString() + "\0").getBytes();
        try{
            mmOutStream.write(bytes);
            Log.d(TAG, "writeData was success");
        }catch (Exception e){
            Log.d(TAG, "Error occurred when sending data", e);
        }
        //получение команды
    }

    public void Connect(View view){
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()){
            Log.d(TAG, "search paired devices");
            for (BluetoothDevice device : pairedDevices){
                Log.d(TAG, "name:" + device.getName() + ", adress:" + device.getAddress());
                if (device.getName().contentEquals("HC-05") || device.getAddress() == "00:21:13:02:C1:31"){
                    Log.d(TAG, "device is founded");
                    ConnectThread(device);
                }
            }
        }
        else Log.d(TAG, "paired devices is empty");
    }

    OutputStream tmpOut = null;
    InputStream tmpIn = null;
    public void ConnectThread(BluetoothDevice device){
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            try {
                btSocket.connect();
                try{
                    tmpIn = btSocket.getInputStream();
                    tmpOut = btSocket.getOutputStream();
                } catch (Exception e){}
                mmOutStream = tmpOut;
                mmInStream = tmpIn;
            } catch (IOException e){
                Log.d(TAG, "1");
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
}
