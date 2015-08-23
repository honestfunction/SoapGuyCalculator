package com.soapguy.echain.soapcalculator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by echain on 2015/8/23.
 */
public class OilRecordsAdapter extends BaseAdapter {

    private ArrayList<OilResource.OilClass> mOils;
    private OilInputRecord [] mRecords;
    private OilInputRecord mRecord;

    private static LayoutInflater sInflater = null;

    public OilRecordsAdapter(Activity activity, ArrayList<OilResource.OilClass> oils,
                             OilInputRecord [] records, int index){
        mOils = oils;
        mRecords = records;
        mRecord = records[index];
        sInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateIndex(int index){
        mRecord = mRecords[index];
    }

    @Override
    public int getCount() {
        return mRecord.keys().length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return mRecord.keys()[i];
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = view;
        if (convertView == null) {
            convertView = sInflater.inflate(R.layout.llayout_oil_records, null);
        }

        TextView textName = (TextView) convertView.findViewById(R.id.text_record_name);
        textName.setText(mOils.get(mRecord.keys()[i]).getName());

        TextView textLye = (TextView) convertView.findViewById(R.id.text_record_lye);
        textLye.setText("皂化價: " + mOils.get(mRecord.keys()[i]).getLye());

        TextView textIns = (TextView) convertView.findViewById(R.id.text_record_ins);
        textIns.setText("INS: " + mOils.get(mRecord.keys()[i]).getIns());

        TextView textGrams = (TextView) convertView.findViewById(R.id.text_record_ratio);
        textGrams.setText(mRecord.get(mRecord.keys()[i]) +"g ("+ getOilPercent(i)  +"%)");

        return convertView;
    }

    private int getOilPercent(int index){
        double select = (double) mRecord.get(mRecord.keys()[index]);
        int total = 0;
        for (int i=0; i< mRecord.keys().length;i++){
            total += mRecord.get(mRecord.keys()[i]);
        }
        return (int) (select / total * 100);
    }
}
