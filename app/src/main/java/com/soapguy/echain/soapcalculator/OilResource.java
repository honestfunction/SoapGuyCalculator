package com.soapguy.echain.soapcalculator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by echain on 2015/8/21.
 */
public class OilResource {


    private ArrayList<OilClass> mOils;

    private boolean mUpdated=true;

    private ArrayList<OilClass>  mDefaultOils= new ArrayList<OilClass>(Arrays.asList(
            new OilClass("橄欖油", 0.134, 109),
            new OilClass("棕櫚油", 0.141, 145),
            new OilClass("椰子油", 0.19 , 258),
            new OilClass("蓖麻油", 0.1286, 95),
            new OilClass("芥花油", 0.1241, 56),
            new OilClass("葡萄籽油", 0.1265, 66),
            new OilClass("葵花油", 0.134, 63),
            new OilClass("棕櫚核油", 0.156, 227),
            new OilClass("紅棕櫚油", 0.142, 110),
            new OilClass("白油", 0.135, 115),
            new OilClass("酪梨油", 0.133, 69),
            new OilClass("荷荷芭油", 0.069, 11),
            new OilClass("榛果油", 0.1356, 94),
            new OilClass("甜杏仁油", 0.136, 97),
            new OilClass("月見草油", 0.1357, 30),
            new OilClass("芝麻油", 0.133, 81),
            new OilClass("大麻籽油", 0.1345, 39),
            new OilClass("蜜蠟", 0.069, 84),
            new OilClass("可可脂", 0.137, 157),
            new OilClass("玫瑰果油", 0.1378, 16),
            new OilClass("杏核油", 0.135, 91),
            new OilClass("小麥胚芽油", 0.131, 58),
            new OilClass("米糠油", 0.128, 70),
            new OilClass("芒果脂", 0.1371, 146),
            new OilClass("澳洲胡桃油", 0.139, 119),
            new OilClass("乳油木果脂", 0.128, 116),
            new OilClass("玉米油", 0.136, 69),
            new OilClass("大豆油", 0.135, 61),
            new OilClass("花生油", 0.136, 99),
            new OilClass("椿油", 0.136, 108),
            new OilClass("苦茶油", 0.136, 108)
            ));

    //Singleton
    private static OilResource sInstance = null;
    public static OilResource getInstance(){
        if(sInstance == null){
            sInstance = new OilResource();
        }
        return sInstance;
    }


    public ArrayList<OilClass> getDefaultOils(){
        return mDefaultOils;
    }

    public ArrayList<OilClass> getOils(){
        return mOils;
    }

    public void setOils(ArrayList<OilClass> oils){
        mOils = oils;
    }

    public boolean isUpdated(){
        return mUpdated;
    }

    public void updated(){
        mUpdated= true;
    }

    public void clear(){
        mUpdated= false;
    }


    public static class OilClass {
        private String mName;
        private double mLye;
        private int mIns;
        public OilClass (String name,double lye ,int ins){
            mName = name;
            mLye = lye;
            mIns = ins;
        }

        public String getName(){return mName;}
        public int getIns () {return mIns;}
        public double getLye () {return mLye;}
    }


}
