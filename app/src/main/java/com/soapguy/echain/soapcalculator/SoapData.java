package com.soapguy.echain.soapcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by echain on 2015/8/21.
 */
public class SoapData {
    private static final String TAG = "SoapData";

    private static OilResource oilResInstance(){
        return OilResource.getInstance();
    }

    private static void initOilTable(SQLiteDatabase db){
        for (OilResource.OilClass oil : oilResInstance().getDefaultOils()){
            insertOilData(db, oil);
        }
    }

    private static void insertOilData(SQLiteDatabase db ,OilResource.OilClass oil){
        ContentValues newRow = new ContentValues();
        newRow.put("name", oil.getName());
        newRow.put("lye", oil.getLye());
        newRow.put("ins", oil.getIns());
        db.insert(SoapDbHelper.DB_OIL_TABLE, null, newRow);
    }

    public static void insertInputRecord(Context context, OilInputRecord record){
        SoapDbHelper dbHelper = new SoapDbHelper(context);
        SQLiteDatabase dbSql = dbHelper.getWritableDatabase();
        ContentValues newRow = new ContentValues();
        newRow.put("data", record.getJsonString());
        newRow.put("time", record.getTime());
        dbSql.insert(SoapDbHelper.DB_RECORD_TABLE, null, newRow);
        dbSql.close();
    }

    public static int getRecordsCount(Context context){
        SoapDbHelper dbHelper = new SoapDbHelper(context);
        Cursor cursor = dbHelper.getAll(dbHelper, SoapDbHelper.DB_RECORD_TABLE,
                SoapDbHelper.RECORD_COLUMNS);
        return cursor.getCount();
    }

    public static OilInputRecord [] getInputRecords(Context context){
        OilInputRecord [] records= null;
        SoapDbHelper dbHelper = new SoapDbHelper(context);
        Cursor cursor = dbHelper.getAll(dbHelper, SoapDbHelper.DB_RECORD_TABLE,
                SoapDbHelper.RECORD_COLUMNS);
        int rows = cursor.getCount();

        if(rows>0){
            records= new OilInputRecord[rows];
            cursor.moveToFirst();
            for (int i=0; i<rows; i++){
                records[i] = new OilInputRecord(cursor.getInt(0),
                        cursor.getString(1),cursor.getString(2), cursor.getString(3));
                cursor.moveToNext();
            }
        }
        cursor.close();
        dbHelper.close();
        return records;
    }

    public static void updateRecordName(Context context, int id, String name){
        SoapDbHelper dbHelper = new SoapDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name );

        String where = "_id=" + id;

        dbHelper.updateValue(db, SoapDbHelper.DB_RECORD_TABLE , cv ,where);
    }

    public static ArrayList<OilResource.OilClass> getOils(Context context){
        if (oilResInstance().isUpdated()){
            updateOilsFromDb(context);
        }
        return oilResInstance().getOils();
    }

    private static void updateOilsFromDb(Context context){
        SoapDbHelper dbHelper = new SoapDbHelper(context);
        Cursor cursor = dbHelper.getAll(dbHelper, SoapDbHelper.DB_OIL_TABLE, SoapDbHelper.OIL_COLUMNS);
        ArrayList<OilResource.OilClass> oils = new ArrayList<>();

        assert cursor.getCount() > 0;

        while(cursor.moveToNext()){
            String name = cursor.getString(1);
            double lye = cursor.getDouble(2);
            int ins = cursor.getInt(3);
            oils.add(new OilResource.OilClass(name,lye,ins));
        }
        cursor.close();
        dbHelper.close();
        oilResInstance().setOils(oils);
        oilResInstance().clear();
    }

    private static void useDefaultOils(){
        oilResInstance().setOils(oilResInstance().getDefaultOils());
    }

    public static void initDatabase(Context context) {
        Log.d(TAG, "initDatabase()");
        SoapDbHelper dbHelper = new SoapDbHelper(context);
        initTableData(dbHelper);
        dbHelper.close();
    }
    private static void initTableData(SoapDbHelper dbHelper){
        SQLiteDatabase soapDb = dbHelper.getWritableDatabase();
        if(dbHelper.isNewDbCreated()) {
            initOilTable(soapDb);
            useDefaultOils();
        }
        soapDb.close();
    }

    /*
    static public SoapDbHelper.IUpdateCallback sCbInitDatabase = new SoapDbHelper.IUpdateCallback(){
        @Override
        public void update(SoapDbHelper dbHelper) {
            initTableData(dbHelper);
        }
    };
    */
}
