package com.soapguy.echain.soapcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String TAG="MainActivity";

    private SharedPreferences mInitSettings;
    private SharedPreferences mChooserPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAppSetting();
        startChooseOilActivity();
    }

    private void startChooseOilActivity(){
        boolean [] choice = loadChooserPref();
        Intent intent = new Intent (this, OilChooserActivity.class);
        if (choice!=null) intent.putExtra(SoapGlobalConfig.CHOOSER_CHOICES_DATA, choice);
        startActivity(intent);
    }

    private boolean [] loadChooserPref(){
        mChooserPref = getSharedPreferences(SoapGlobalConfig.PREFS_CHOOSER_NAME,0);
        String arrayName = SoapGlobalConfig.CHOISE_ARRAY_NAME;
        int arraySize = mChooserPref.getInt(arrayName+"_size",-1);
        if(arraySize<0) return null;
        boolean [] choices = new boolean[arraySize];
        for (int i=0; i<arraySize;i++){
            choices[i] = mChooserPref.getBoolean(arrayName + "_" + i, false);
        }
        return choices;
    }

    void initAppSetting(){
        mInitSettings = getSharedPreferences(SoapGlobalConfig.PREFS_INIT_NAME, 0);

        if(!isDbCreated()) {
            initDatabase();
        }
    }

    private void initDatabase(){
        Log.d(TAG,"initDatabase() invoked");
        //TODO: AsyncTask
        SoapData.initDatabase(this);
        mInitSettings.edit().putBoolean("db_created", true).commit();
    }

    private boolean isDbCreated(){
        return mInitSettings.getBoolean("db_created", false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
