package com.soapguy.echain.soapcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class OilRecordsActivity extends AppCompatActivity {

    private int mRecordIndex;
    private ArrayList<OilResource.OilClass> mOils;
    private OilInputRecord [] mRecords;

    private Spinner mSpnSelect;
    private String [] mSelectItems;

    private OilRecordsAdapter mAdapterItems;

    private LinearLayout mLayoutList;

    private Button mButtonAdjust;

    private SoapOilCalculator mSoapOilCalculator;

    private LinearLayout mLayoutResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_records);

        initData();
        setupContent();
        updateView();
    }

    private void initData(){
        mOils = SoapData.getOils(this);
        mRecords = SoapData.getInputRecords(this);

        if(mRecords == null) {
            mRecordIndex=-1;
        } else {
            mRecordIndex = mRecords.length -1;
        }
    }

    private void setupContent(){
        setupSpinner();
        setupLayoutList();
        setupButtonAdjust();
        //setupTextResult();
        setupLayoutResult();
    }

    private void setupSpinner(){
        mSpnSelect = (Spinner) findViewById(R.id.spin_select_record);
        mSelectItems = new String [mRecords.length];
        updateItemsFromRecords();
        ArrayAdapter<String> adaptRecordList =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,mSelectItems);
        adaptRecordList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnSelect.setAdapter(adaptRecordList);

        mSpnSelect.setOnItemSelectedListener(mSpinRecordListener);
    }

    Spinner.OnItemSelectedListener mSpinRecordListener = new Spinner.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mRecordIndex = mRecords.length -i -1;
            updateView();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void setupLayoutList(){
        mLayoutList = (LinearLayout) findViewById(R.id.linear_oilrecords);
        mAdapterItems = new OilRecordsAdapter(this, mOils, mRecords,mRecordIndex);
        updateLayoutList();
    }

    private void updateLayoutList(){
        final int adapterCount = mAdapterItems.getCount();
        mLayoutList.removeAllViews();
        for (int i=0; i<adapterCount; i++){
            View item = mAdapterItems.getView(i,null,null);
            mLayoutList.addView(item);
        }
        mLayoutList.invalidate();
    }

    private void setupLayoutResult(){
        mLayoutResult = (LinearLayout) findViewById(R.id.linear_oilresult);
        mSoapOilCalculator = new SoapOilCalculator(mOils,mRecords[mRecordIndex]);
        updateLayoutResult();
    }

    private void updateLayoutResult(){

        ArrayList<LinearLayout> lLayouts = new ArrayList<>();

        lLayouts.add(generateResultLayout("總油量", "公克",
                mSoapOilCalculator.getTotalOilWeight()+ ""));
        lLayouts.add(generateResultLayout("NaOH", "氫氧化鈉",
                mSoapOilCalculator.getTotalNaoh()+""));
        lLayouts.add(generateResultLayout("INS", "120~180",
                mSoapOilCalculator.getAverageIns()+""));
        lLayouts.add(generateResultLayout("水量(1)", mSoapOilCalculator.getWaterFormula(1),
                mSoapOilCalculator.getWaterWeight(1)+""));
        lLayouts.add(generateResultLayout("水量(2)", mSoapOilCalculator.getWaterFormula(2),
                mSoapOilCalculator.getWaterWeight(2)+""));
        lLayouts.add(generateResultLayout("水量(3)", mSoapOilCalculator.getWaterFormula(3),
                mSoapOilCalculator.getWaterWeight(3)+""));

        mLayoutResult.removeAllViews();
        for(LinearLayout layout: lLayouts) {
            mLayoutResult.addView(layout);
        }
        mLayoutResult.invalidate();
    }

    private LinearLayout generateResultLayout(String name, String hint, String number){
        LayoutInflater inflater = LayoutInflater.from(OilRecordsActivity.this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.llayout_oil_result, null, false);
        TextView textName = (TextView) layout.findViewById(R.id.text_result_name);
        TextView textHint = (TextView) layout.findViewById(R.id.text_result_hint);
        TextView textNumber = (TextView) layout.findViewById(R.id.text_result_number);

        textName.setText(name);
        textHint.setText(hint);
        textNumber.setText(number);
        return layout;
    }

    private void setupButtonAdjust(){
        mButtonAdjust = (Button) findViewById(R.id.btn_record_adjust);
        mButtonAdjust.setOnClickListener(mBtnAdjustListener);
    }

    private Button.OnClickListener mBtnAdjustListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            startInputActivity();
        }
    };

    private void startInputActivity(){
        Intent intent = new Intent(OilRecordsActivity.this, OilInputActivity.class);

        intent.putExtra(SoapGlobalConfig.EXTRA_OIL_INPUT_CATEGORY, getCurrentCategory());
        intent.putExtra(SoapGlobalConfig.EXTRA_OIL_INPUT_GRAMS, getCurrentGrams());

        startActivity(intent);
    }

    private boolean [] getCurrentCategory(){
        boolean [] category = new boolean [mOils.size()];
        OilInputRecord currentRecord = mRecords[mRecordIndex];
        for(int i=0; i< currentRecord.keys().length; i++){
            category[currentRecord.keys()[i]] = true;
        }
        return category;
    }

    private int [] getCurrentGrams() {
        int [] grams = new int [mOils.size()];
        OilInputRecord currentRecord = mRecords[mRecordIndex];
        for(int i=0; i< currentRecord.keys().length; i++){
            int index = currentRecord.keys()[i];
            grams[index] = currentRecord.get(index);
        }
        return grams;
    }

    private void updateItemsFromRecords(){
        for (int i = mRecords.length-1 ; i>=0 ; i--){
            mSelectItems[mRecords.length-1-i] = mRecords[i].getTime() + "_" + i;
        }
    }

    private void updateView(){
        mAdapterItems.updateIndex(mRecordIndex);
        mAdapterItems.notifyDataSetChanged();
        updateLayoutList();

        mSoapOilCalculator.modifyRecord(mRecords[mRecordIndex]);
        //updateTextResult();
        updateLayoutResult();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oil_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.action_reset){
            boolean[] choice = loadChooserPref();
            Intent intent = new Intent(OilRecordsActivity.this, OilChooserActivity.class);
            if (choice != null) intent.putExtra(SoapGlobalConfig.CHOOSER_CHOICES_DATA, choice);
            super.onResume();
            startActivity(intent);
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean [] loadChooserPref(){
        SharedPreferences chooserPref = getSharedPreferences(SoapGlobalConfig.PREFS_CHOOSER_NAME,0);
        String arrayName = SoapGlobalConfig.CHOISE_ARRAY_NAME;
        int arraySize = chooserPref.getInt(arrayName + "_size", -1);
        if(arraySize<0) return null;
        boolean [] choices = new boolean[arraySize];
        for (int i=0; i<arraySize;i++){
            choices[i] = chooserPref.getBoolean(arrayName + "_" + i, false);
        }
        return choices;
    }

}
