package com.soapguy.echain.soapcalculator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by echain on 2015/8/22.
 */
public class OilInputAdapter extends BaseAdapter {
    private ArrayList<OilResource.OilClass> mOils;
    private boolean[] mOilCategory;
    private static LayoutInflater sInflater = null;
    private int[] mMapId;
    private int mCount;
    private int [] mOilGrams;

    public OilInputAdapter(Activity activity, ArrayList<OilResource.OilClass> oils,
                           boolean[] oilCategory, int [] oilGrams) {
        mOils = oils;
        mOilCategory = oilCategory;
        sInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMapId = new int[getCount()];
        Arrays.fill(mMapId, -1);

        mOilGrams=oilGrams;
    }

    private int getCategoryCount() {
        if (mCount > 0) return mCount;
        int count=0;
        for(boolean category: mOilCategory){
            if(category) count++;
        }

        mCount = count;
        return mCount;
    }

    @Override
    public int getCount() {
        return getCategoryCount();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        //return i;
        return findSelectedId(i);
    }

    private int findSelectedId(int i) {
        if (mMapId[i] >= 0) {
            return mMapId[i];
        }

        int k = 0;
        for (int j = 0; j < mOilCategory.length; j++) {
            if (mOilCategory[j]) {
                mMapId[k] = j;
                k++;
            }
        }
        return mMapId[i];
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = view;
        if (convertView == null) {
            convertView = sInflater.inflate(R.layout.list_oil_input, null);
        }

        TextView textName = (TextView) convertView.findViewById(R.id.text_input_name);
        textName.setText(mOils.get(findSelectedId(i)).getName());

        TextView textLye = (TextView) convertView.findViewById(R.id.text_input_lye);
        textLye.setText("皂化價: " + mOils.get(findSelectedId(i)).getLye());

        TextView textIns = (TextView) convertView.findViewById(R.id.text_input_ins);
        textIns.setText("INS: " + mOils.get(findSelectedId(i)).getIns());

        TextView textGrams = (TextView) convertView.findViewById(R.id.text_input_grams);
        textGrams.setText(mOilGrams[findSelectedId(i)]  +"克");

        return convertView;
    }

}