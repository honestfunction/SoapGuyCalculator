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

    public static final int DATABASE_VERSION = 4;

    private static final String DB_FILE= "soap_data.db";

    public static final String DB_OIL_TABLE = "soap_oils";
    public static final String DB_RECORD_TABLE = "soap_records";
    public static final String [] SOAP_TABLES = {DB_OIL_TABLE, DB_RECORD_TABLE};

    public static final String [] OIL_COLUMNS= {"_id", "name", "lye", "ins"};
    public static final String [] RECORD_COLUMNS= {"_id", "time", "data","name"};

    //private static final IUpdateCallback [] sUpdateCallbacks = {SoapData.sCbInitDatabase};

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
                    "data TEXT NOT NULL," +
                    "name TEXT);";

    private ArrayList<DbTable> mDbTables = null;

    private boolean mNewDbCreated;

    private Context mContext;

    public SoapDbHelper(Context context) {
        super(context, DB_FILE, null, DATABASE_VERSION);
        mContext = context;
        mDbTables = new ArrayList<>();
        mDbTables.add(new DbTable(DB_OIL_TABLE, OIL_TABLE_CMD));
        mDbTables.add(new DbTable(DB_RECORD_TABLE, RECORD_TABLE_CMD));

        mNewDbCreated = false;
    }

    public Cursor getAll(SoapDbHelper dbHelper, String table, String [] columns){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, columns, null,null,null,null,null);
        return cursor;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (mDbTables == null) {
            return;
        }
        for (DbTable table : mDbTables) {
            sqLiteDatabase.execSQL(table.getCmdCreate());
        }
        mNewDbCreated = true;
    }

    public boolean isNewDbCreated(){
        return mNewDbCreated;
    }

    @Override
    public void onUpgrade (SQLiteDatabase sqLiteDatabase,int i, int i1){
        if (mDbTables == null) {
            return;
        }
        for (DbTable table : mDbTables) {
            sqLiteDatabase.execSQL(table.getCmdDrop());
            sqLiteDatabase.execSQL(table.getCmdCreate());
        }

        mNewDbCreated = true;
    }

    private class DbTable {
        private String mTableName;
        private String mCmdCreate;

        public DbTable(String tableName, String cmdCreate){
            mTableName = tableName;
            mCmdCreate = cmdCreate;
        }

        private String makeCmdDrop(String name){
            return "DROP TABLE IF EXISTS " + name;
        }

        public String getTableName(){
            return mTableName;
        }

        public String getCmdCreate(){
            return mCmdCreate;
        }

        public String getCmdDrop(){
            return makeCmdDrop(mTableName);
        }
    }

    public interface IUpdateCallback {
        public abstract void update(SoapDbHelper dbHelper);
    }
}