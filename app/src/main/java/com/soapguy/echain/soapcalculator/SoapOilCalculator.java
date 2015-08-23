package com.soapguy.echain.soapcalculator;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by echain on 2015/8/22.
 */
public class SoapOilCalculator {
    private final String TAG="SoapOilCalculator";
    private ArrayList<OilResource.OilClass> mOils;
    private int [] mGrams;
    private boolean [] mCategories;

    public SoapOilCalculator(ArrayList<OilResource.OilClass> oils, OilInputRecord record){
        mOils = oils;
        mGrams = generateGramsFromRecord(record);
        mCategories = new boolean[oils.size()];
        Arrays.fill(mCategories,true);
    }

    public SoapOilCalculator(ArrayList<OilResource.OilClass> oils, int[] grams,
                             boolean[] categories){
        mOils = oils;
        mGrams = grams;
        mCategories = categories;
    }


    public int [] generateGramsFromRecord(OilInputRecord record){
        int [] indexes = record.keys();
        int [] grams = new int[mOils.size()]; // default initialize to 0
        for (int index: indexes){
            grams[index] = record.get(index);
        }
        return grams;
    }


    public void modifyMaterials(ArrayList<OilResource.OilClass> oils, int [] grams,
                                boolean [] categories){

    }

    public void modifyRecord(OilInputRecord record){
        mGrams = generateGramsFromRecord(record);
    }

    public int getTotalOilWeight(){
        int weight =0;
        for(int i=0; i< mGrams.length; i++){
            if(mGrams[i]>0 && mCategories[i]){
                weight += mGrams[i];
            }
        }
        return weight;
    }

    public double getTotalNaoh(){
        double totalNaoh = 0;
        for(int i=0; i< mGrams.length; i++){
            if(mGrams[i]>0 && mCategories[i]){
                double naoh = mGrams[i] * mOils.get(i).getLye();
                totalNaoh += naoh;
            }
        }
        //return Math.floor(totalNaoh * 100) / 100;
        return getDot2(totalNaoh);
    }

    public double getWaterWeight(int i){
        if(i==2) return getDot2(getTotalNaoh()/0.3 - getTotalNaoh());
        if(i==3) return getDot2(getTotalOilWeight() * 0.389);
        return getWaterWeight2Naoh();
    }

    public String getWaterFormula(int i){
        if (i==2) return "NaOH/0.3 - NaOH";
        if (i==3) return "Oil * 0.389";
        return "NaOH * 2";
    }

    private Double getDot2(double input){
        return Math.floor(input * 100) /100;
    }

    public double getWaterWeight2Naoh() {return getTotalNaoh()*2; };


    public int getAverageIns(){
        double oilsWeight = getTotalOilWeight();
        double ins = 0;
        for(int i=0; i< mGrams.length; i++){
            if(mGrams[i]>0 && mCategories[i]){
                ins += mGrams[i]/oilsWeight * mOils.get(i).getIns();
            }
        }
        return (int) ins;
    }
}
