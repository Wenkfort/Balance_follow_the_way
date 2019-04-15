package com.company.ivan.balance_follow_the_way;

import org.json.*;
import android.util.Log;

public class coordinates {
    private static String TAG = "BTDevice";
    public float x;
    public float y;
    public float angle;

    public coordinates (float x, float y, float angle){
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
    public  coordinates(String message){
        try {
            JSONObject jsonObject = new JSONObject(message);
            try{
                this.x =(float) jsonObject.getDouble("x");
                this.y =(float) jsonObject.getDouble("y");
                this.angle =(float) jsonObject.getDouble("a");
                Log.d(TAG, "Transformation message to JSON is complited");
            } catch (JSONException e){
                Log.d(TAG, "Transformation message to JSON isn`t complited");
            }
        }catch (JSONException e){
            Log.d(TAG, "can't convert message to Json");
        }
    }
}