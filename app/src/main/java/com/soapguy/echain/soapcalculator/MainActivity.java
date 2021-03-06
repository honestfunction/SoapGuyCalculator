package com.soapguy.echain.soapcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String TAG="MainActivity";

    final private int SPLASH_TIMEOUT=2000;
    Handler mSplashHandler =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAppSetting();
        startChooseOilActivity();
    }



    private void startChooseOilActivity(){
        mSplashHandler = new Handler();
        mSplashHandler.postDelayed(mDelayRunnable, SPLASH_TIMEOUT);
    }

    Runnable mDelayRunnable = new Runnable(){
        @Override
        public void run() {
            boolean[] choice = OilChooserActivity.loadChooserPref(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, OilChooserActivity.class);
            if (choice != null) intent.putExtra(SoapGlobalConfig.CHOOSER_CHOICES_DATA, choice);
            startActivity(intent);
        }
    };

    void initAppSetting(){
        initDatabase();
    }

    private void initDatabase(){
        Log.d(TAG, "initDatabase() invoked");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SoapData.initDatabase(MainActivity.this);
            }
        }).start();
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

    @Override
    public void onBackPressed() {
        if(mSplashHandler!=null ) mSplashHandler.removeCallbacks(mDelayRunnable);
        finish();
    }

}
