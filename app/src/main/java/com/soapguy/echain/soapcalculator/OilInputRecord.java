package com.soapguy.echain.soapcalculator;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by echain on 2015/8/22.
 */
public class OilInputRecord {
    public final String TAG = "OilInputRecord";

    private HashMap<String, String> mInputData = null;
    private String mInputTime=null;
    private String mJsonData=null;

    public OilInputRecord(String time, String jsonData){
        mInputTime = time;
        mJsonData=jsonData;
        createInputDataMap();
    }

    public OilInputRecord(int [] grams, boolean [] categories){
        createInputDataMap(grams,categories);
    }

    private void createInputDataMap(){
        try {
            JSONObject jsonObject= new JSONObject(mJsonData);
            mInputData = new HashMap<>();
            Iterator<String> keysIterator = jsonObject.keys();
            while(keysIterator.hasNext()){
                String key = keysIterator.next();
                mInputData.put(key, jsonObject.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createInputDataMap(int [] grams, boolean [] categories){
        mInputData = new HashMap<>();
        for (int i=0;i< grams.length; i++) {
            if(grams[i]>0 && categories[i]){
                mInputData.put(String.valueOf(i),String.valueOf(grams[i]));
            }
        }
    }

    public String getJsonString(){
        if(mJsonData!=null) return mJsonData;
        mJsonData = convertInputData();
        return mJsonData;
    }

    private String convertInputData(){
        JSONObject obj=new JSONObject(mInputData);
        // String {"5":"50","0":"100","8":"80"}
        Log.d(TAG, obj.toString());
        return obj.toString();
    }

    public void setTime(String time){
        mInputTime = time;
    }

    public String getTime(){
        if(mInputTime==null) mInputTime = "1986/09/16";
        return mInputTime;
    }

    public int [] keys(){
        int [] keyIntArray = new int[mInputData.size()];
        String [] keyStrArray = mInputData.keySet().toArray(new String[mInputData.size()]);
        for(int i=0; i<keyStrArray.length;i++){
            keyIntArray[i] = Integer.valueOf(keyStrArray[i]);
        }
        return keyIntArray;
    }

    public int get(int key){
        return Integer.valueOf(mInputData.get(String.valueOf(key)));
    }
}
