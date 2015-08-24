package com.soapguy.echain.soapcalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
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
            mRecordIndex= -1;
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

    ArrayAdapter<String> mAdaptRecordList;
    private void setupSpinner(){
        mSpnSelect = (Spinner) findViewById(R.id.spin_select_record);
        mSelectItems = new String [mRecords.length];
        updateItemsFromRecords();

        /*
        mAdaptRecordList =  new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,mSelectItems);
        //mAdaptRecordList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
           */

        mAdaptRecordList =  new ArrayAdapter<String>(this,
                R.layout.spinner_records, mSelectItems);
        mAdaptRecordList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpnSelect.setAdapter(mAdaptRecordList);

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
                mSoapOilCalculator.getWaterWeight(3) + ""));

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
            if(mRecords[i].getName().isEmpty()) {
                mSelectItems[mRecords.length - 1 - i] = mRecords[i].getTime() + "_" + (i+1);
            } else{
                mSelectItems[mRecords.length - 1 - i] = mRecords[i].getTime() + "_" +
                    mRecords[i].getName();
            }
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

        if(id == R.id.action_choose){
            boolean[] choice = OilChooserActivity.loadChooserPref(OilRecordsActivity.this);
            Intent intent = new Intent(OilRecordsActivity.this, OilChooserActivity.class);
            if (choice != null) intent.putExtra(SoapGlobalConfig.CHOOSER_CHOICES_DATA, choice);
            super.onResume();
            startActivity(intent);
        }

        if (id == R.id.action_rename) {
            final EditText editName = new EditText(OilRecordsActivity.this);
            final String currentName = mRecords[mRecordIndex].getName();
            editName.setText(currentName);
            new AlertDialog.Builder(OilRecordsActivity.this).setTitle("記錄名稱")
                    .setView(editName)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if ( editName.getText().toString().equals(currentName)) return;

                            SoapData.updateRecordName(OilRecordsActivity.this, mRecords[mRecordIndex].getId()
                                    , editName.getText().toString());
                            initData();
                            updateItemsFromRecords();
                            mAdaptRecordList.notifyDataSetChanged();
                        }
                    })
                    .show();

            //button click
        }


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
