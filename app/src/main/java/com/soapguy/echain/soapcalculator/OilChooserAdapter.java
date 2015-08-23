package com.soapguy.echain.soapcalculator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echain on 2015/8/21.
 */
public class OilChooserAdapter extends BaseAdapter {
    private ArrayList<OilResource.OilClass> mOils;
    private boolean [] mChoices;
    private static LayoutInflater sInflater = null;

    public OilChooserAdapter(Activity activity, ArrayList<OilResource.OilClass> oils,
                             boolean [] choices){
        mOils = oils;
        mChoices = choices;
        sInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mOils.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = view;
        if(convertView == null) {
            convertView = sInflater.inflate(R.layout.list_oil_chooser, null);
        }

        TextView textName = (TextView) convertView.findViewById(R.id.text_chooser_name);
        textName.setText(mOils.get(i).getName());

        TextView textLye = (TextView)convertView.findViewById(R.id.text_chooser_lye);
        textLye.setText("皂化價: " + mOils.get(i).getLye());

        TextView textIns = (TextView) convertView.findViewById(R.id.text_chooser_ins);
        textIns.setText("INS: " + mOils.get(i).getIns());

        CheckBox chkChoice = (CheckBox) convertView.findViewById(R.id.checkbox_oilchooser);
        chkChoice.setChecked(mChoices[i]);

        return  convertView;
    }
}
