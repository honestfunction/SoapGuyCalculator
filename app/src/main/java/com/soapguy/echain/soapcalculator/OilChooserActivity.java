package com.soapguy.echain.soapcalculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class OilChooserActivity extends AppCompatActivity {
    final private String TAG="OilChooserActivity";
    private ListView mListViewChooser;
    OilChooserAdapter mAdapterItems;

    private Button mButtonNext;
    private ArrayList<OilResource.OilClass> mOils;
    private boolean [] mListCheckBox;

    private SharedPreferences mChooserPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_chooser);

        mOils = SoapData.getOils(this);
        loadIntentData();
        setupContent();
    }

    private void loadIntentData(){
        Intent intent = getIntent();
        mListCheckBox = intent.getBooleanArrayExtra(SoapGlobalConfig.CHOOSER_CHOICES_DATA);

        if(mListCheckBox == null){
            mListCheckBox = new boolean[mOils.size()];
        }
    }

    private void setupContent(){
        mListViewChooser = (ListView) findViewById(R.id.listview_chooser);
        mListViewChooser.setOnItemClickListener(mItemClickListener);
        mAdapterItems = new OilChooserAdapter(this, mOils, mListCheckBox);
        mListViewChooser.setAdapter(mAdapterItems);
        mButtonNext = (Button) findViewById(R.id.button_oilchooser_next);
        mButtonNext.setOnClickListener(mButtonNextListener);
    }

    ListView.OnItemClickListener mItemClickListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CheckBox chkItem = (CheckBox) view.findViewById(R.id.checkbox_oilchooser);
            chkItem.setChecked(!chkItem.isChecked());
            mListCheckBox[i] = chkItem.isChecked();
        }
    };

    Button.OnClickListener mButtonNextListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(isAnItemChecked()) {
                saveChooserPreference();
                startOilInputActivity();
            } else{
                Toast.makeText(OilChooserActivity.this,
                        "至少選擇一種油品",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void startOilInputActivity(){
        Intent intent = new Intent(OilChooserActivity.this, OilInputActivity.class);
        intent.putExtra(SoapGlobalConfig.EXTRA_OIL_INPUT_CATEGORY, mListCheckBox);
        startActivity(intent);
    }


    private void saveChooserPreference(){
        mChooserPref = getSharedPreferences(SoapGlobalConfig.PREFS_CHOOSER_NAME, 0);
        SharedPreferences.Editor editor = mChooserPref.edit();
        String arrayName = SoapGlobalConfig.CHOISE_ARRAY_NAME;
        editor.putInt(arrayName + "_size", mListCheckBox.length);
        for(int i=0; i<mListCheckBox.length; i++){
            editor.putBoolean(arrayName +"_" + i, mListCheckBox[i]);
        }
        editor.commit();
    }

    public static boolean [] loadChooserPref(Context context){
        SharedPreferences chooserPref = context.getSharedPreferences(SoapGlobalConfig.PREFS_CHOOSER_NAME, 0);
        String arrayName = SoapGlobalConfig.CHOISE_ARRAY_NAME;
        int arraySize = chooserPref.getInt(arrayName+"_size",-1);
        if (arraySize<0) return null;
        boolean [] choices = new boolean[arraySize];
        for (int i=0; i<arraySize;i++){
            choices[i] = chooserPref.getBoolean(arrayName + "_" + i, false);
        }
        return choices;
    }

    /*
    private static void saveChooserPreference(Context context, boolean [] arrayCheckBox){
        SharedPreferences chooserPref = context.getSharedPreferences(SoapGlobalConfig.PREFS_CHOOSER_NAME, 0);
        SharedPreferences.Editor editor = chooserPref.edit();
        String arrayName = SoapGlobalConfig.CHOISE_ARRAY_NAME;
        editor.putInt(arrayName + "_size", arrayCheckBox.length);
        for(int i=0; i<arrayCheckBox.length; i++){
            editor.putBoolean(arrayName +"_" + i, arrayCheckBox[i]);
        }
        editor.commit();
    }
    */

    private boolean isAnItemChecked(){
        for (boolean checked: mListCheckBox){
            if(checked) return true;
        }
        return false;
    }

    /*
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.soapguy_logo)
                .setTitle("Closing This App")
                .setMessage("Are you sure you want to exit this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oil_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if (id == R.id.action_reset) {
            Arrays.fill(mListCheckBox,false);
            mAdapterItems.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_records) {
            if(SoapData.getRecordsCount(OilChooserActivity.this)>0) {
                Intent intent = new Intent(OilChooserActivity.this, OilRecordsActivity.class);
                super.onResume(); //tricky: Performing stop of activity that is not resumed
                startActivity(intent);
            } else {
                Toast.makeText(OilChooserActivity.this,
                        "沒有配方紀錄", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
