package com.company.ivan.balance_follow_the_way;

import android.bluetooth.BluetoothAdapter;

public class BluetoothConnection {
    public BluetoothAdapter getAdapter(){
        return  BluetoothAdapter.getDefaultAdapter();
    }
}
