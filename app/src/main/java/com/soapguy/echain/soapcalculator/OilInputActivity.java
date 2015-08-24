package com.soapguy.echain.soapcalculator;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class OilInputActivity extends AppCompatActivity {

    private boolean[] mOilCategory;

    private ArrayList<OilResource.OilClass> mOils;
    private int[] mOilGrams = null;

    private ListView mListViewOilInput;
    OilInputAdapter mAdapterItems;

    private Dialog mInputDialog;
    private EditText mEditLye;
    private EditText mEditIns;
    private EditText mEditGrams;
    private Button mButtonDlgOk;
    private Button mButtonDlgCancel;
    private int mInputIndex;

    private SoapOilCalculator mOilCalculator;
    private TextView mOilsInfo;

    private Button mButtonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oil_input);

        mOils = SoapData.getOils(this);
        loadIntentData();
        setupContent();
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        mOilCategory = intent.getBooleanArrayExtra(SoapGlobalConfig.EXTRA_OIL_INPUT_CATEGORY);

        mOilGrams = intent.getIntArrayExtra(SoapGlobalConfig.EXTRA_OIL_INPUT_GRAMS);
        if (mOilGrams == null) {
            mOilGrams = new int[mOils.size()];  //int initilized to 0 by default
        }
    }

    private void setupContent() {
        setupListView();
        setupInputDialog();
        setupInfo();
        setupSaveButton();
    }

    private void setupSaveButton(){
        mButtonSave = (Button) findViewById(R.id.button_oilinput_save);
        mButtonSave.setOnClickListener(mBtnSaveListener);
    }

    private Button.OnClickListener mBtnSaveListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(isInputEmpty()) {
                Toast.makeText(OilInputActivity.this,
                        "至少輸入一種油品重量", Toast.LENGTH_SHORT).show();
            } else {
                saveRecord();
                startOilRecordsActivity();
            }
        }
    };

    private void saveRecord(){
        OilInputRecord record = new OilInputRecord(mOilGrams, mOilCategory);
        record.setTime(getDate());
        SoapData.insertInputRecord(this, record);
    }

    private String getDate(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    private void startOilRecordsActivity(){
        Intent intent = new Intent(OilInputActivity.this, OilRecordsActivity.class);
        startActivity(intent);
    }

    private boolean isInputEmpty(){
        for(int i=0; i<mOilGrams.length; i++){
            if(mOilGrams[i]>0 && mOilCategory[i]) return false;
        }
        return true;
    }

    private void setupInfo(){
        mOilCalculator = new SoapOilCalculator(mOils,mOilGrams,mOilCategory);
        mOilsInfo = (TextView) findViewById(R.id.text_oils_info);
        updateInfo();
    }

    private void setupInputDialog() {
        mInputDialog = new Dialog(OilInputActivity.this);
        mInputDialog.setContentView(R.layout.dialog_oil_input);
        mInputDialog.setCancelable(false);

        mEditLye = (EditText) mInputDialog.findViewById(R.id.edit_dlg_lye);
        mEditIns = (EditText) mInputDialog.findViewById(R.id.edit_dlg_ins);
        mEditGrams = (EditText) mInputDialog.findViewById(R.id.edit_dlg_grams);
        mButtonDlgOk = (Button) mInputDialog.findViewById(R.id.btn_dlg_input_ok);
        mButtonDlgCancel = (Button) mInputDialog.findViewById(R.id.btn_dlg_input_cancel);

        mButtonDlgOk.setOnClickListener(mDlgBtnOkListener);
        mButtonDlgCancel.setOnClickListener(mDlgBtnCancelListener);
    }


    private void updateInfo(){
        mOilsInfo.setText("總油重: " + mOilCalculator.getTotalOilWeight() + "克\n" +
                         "水重(NaOH*2): " + mOilCalculator.getWaterWeight2Naoh() + "克\n" +
                         "NaOH: " + mOilCalculator.getTotalNaoh() +
                         ", INS: " + mOilCalculator.getAverageIns());
    }

    private Button.OnClickListener mDlgBtnOkListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            mOilGrams[mInputIndex] = Integer.valueOf(mEditGrams.getText().toString());
            mAdapterItems.notifyDataSetChanged();
            updateInfo();
            mInputDialog.cancel();
        }
    };

    private Button.OnClickListener mDlgBtnCancelListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            mInputDialog.cancel();
        }
    };


    private void setupListView() {
        mListViewOilInput = (ListView) findViewById(R.id.listview_oilinput);
        mListViewOilInput.setOnItemClickListener(mItemClickListener);
        mAdapterItems = new OilInputAdapter(this, mOils, mOilCategory, mOilGrams);
        mListViewOilInput.setAdapter(mAdapterItems);
    }


    private ListView.OnItemClickListener mItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mInputIndex = (int) l;

            mInputDialog.setTitle(mOils.get(mInputIndex).getName());
            mEditLye.setText(String.valueOf(mOils.get(mInputIndex).getLye()));
            mEditIns.setText(String.valueOf(mOils.get(mInputIndex).getIns()));
            mEditGrams.setText(String.valueOf(mOilGrams[mInputIndex]));

            mEditGrams.setSelectAllOnFocus(true);

            mEditGrams.clearFocus();
            mEditGrams.requestFocus();
            mInputDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            mInputDialog.show();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oil_input, menu);
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
            boolean[] choice = OilChooserActivity.loadChooserPref(OilInputActivity.this);
            Intent intent = new Intent(OilInputActivity.this, OilChooserActivity.class);
            if (choice != null) intent.putExtra(SoapGlobalConfig.CHOOSER_CHOICES_DATA, choice);
            super.onResume();
            startActivity(intent);
        }

        if (id == R.id.action_records) {
            if(SoapData.getRecordsCount(OilInputActivity.this)>0) {
                Intent intent = new Intent(OilInputActivity.this, OilRecordsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(OilInputActivity.this,
                        "沒有配方紀錄", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}