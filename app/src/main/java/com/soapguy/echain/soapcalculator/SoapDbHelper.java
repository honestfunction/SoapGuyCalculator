package com.soapguy.echain.soapcalculator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by echain on 2015/8/21.
 */
public class SoapDbHelper extends SQLiteOpenHelper {

    private static final String DB_FILE= "soap_data.db";
    public static final int DATABASE_VERSION = 1;
    public static final String DB_OIL_TABLE = "soap_oils";
    public static final String DB_RECORD_TABLE = "soap_records";

    public static final String [] OIL_COLUMNS= {"_id", "name", "lye", "ins"};
    public static final String [] RECORD_COLUMNS= {"_id", "time", "data"};


    private static final String OIL_TABLE_CMD =
            "CREATE TABLE IF NOT EXISTS " + DB_OIL_TABLE + "(" +
                    "_id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "lye INT NOT NULL," +
                    "ins INT NOT NULL);";

    private static final String RECORD_TABLE_CMD =
            "CREATE TABLE IF NOT EXISTS " + DB_RECORD_TABLE + "(" +
                    "_id INTEGER PRIMARY KEY," +
                    "time TEXT," +
                    "data TEXT NOT NULL);";


    private ArrayList<String> mTableCmds = null;
    private boolean mNewDbCreated;

    public SoapDbHelper(Context context) {
        super(context, DB_FILE, null, DATABASE_VERSION);
        mTableCmds = new ArrayList<>();
        addTableCmd(OIL_TABLE_CMD);
        addTableCmd(RECORD_TABLE_CMD);

        mNewDbCreated = false;
    }

    public void addTableCmd(String cmd) {
        mTableCmds.add(cmd);
    }

    public Cursor getAll(SoapDbHelper dbHelper, String table, String [] columns){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, columns, null,null,null,null,null);
        return cursor;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (mTableCmds == null) {
            return;
        }
        for (String cmd : mTableCmds) {
            sqLiteDatabase.execSQL(cmd);
        }
        mNewDbCreated = true;
    }

    public boolean isNewDbCreated(){
        return mNewDbCreated;
    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase,int i, int i1){

    }
}