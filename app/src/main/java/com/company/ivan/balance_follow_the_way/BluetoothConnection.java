package com.company.ivan.balance_follow_the_way;

import android.bluetooth.*;
import android.os.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import android.util.Log;

public class BluetoothConnection extends Thread{
    private static String TAG = "BTDevice";
    private android.os.Handler handler; // handler that gets info from Bluetooth service
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public BluetoothConnection(BluetoothSocket socket, android.os.Handler handler) {
        mmSocket = socket;
        this.handler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        String message = "";
        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                Log.d(TAG, "i am trying to read message");
                numBytes = mmInStream.read(mmBuffer);
                for (int i = 0; i < numBytes; i++){
                    if (mmBuffer[i] == '{'){
                        message = "";
                        message += "{";
                    }
                    else if (mmBuffer[i] == '}'){
                        message += "}";
                        // Send the obtained bytes to the UI activity.
                        Message readMsg = handler.obtainMessage(1, new coordinates(message));
                        readMsg.sendToTarget();
                        Log.d(TAG, String.format("final message is: " + message));
                    }
                    else {
                        message += (char) mmBuffer[i]; //Byte.toString(mmBuffer[i]);
                    }
                }

            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}