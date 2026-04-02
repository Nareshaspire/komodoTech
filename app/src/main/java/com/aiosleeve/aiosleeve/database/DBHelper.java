package com.aiosleeve.aiosleeve.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by oneclick-android on 10/1/18.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.aiosleeve.aiosleeve/databases/";

    public final static String DB_NAME = "AIOSleeve.sqlite";

    private final Context myContext;

    private static SQLiteDatabase db;

    ContentValues mContentValues;
    /*SELECT  tbl_new_dish_category.category_name, product_en_name  FROM tbl_new_dish_category
    left join tbl_dishes on tbl_dishes.category_id = tbl_new_dish_category.category_id
    where  ( tbl_new_dish_category.category_name like  '%Blue%'  or  product_en_name  like  '%Blue%'  )*/

    //Table Name
    public static String mTableBPMDetails = "bpm_details";
    public static String mTableDistanceDetails = "distance_details";
    public static String mTableECGDetails = "ecg_details";
    public static String mTableHRVDetails = "hrv_details";
    public static String mTableMainActivityTable = "main_activity_table";
    public static String mTableMETDetails = "met_details";
    public static String mTableSPO2Details = "spo2_details";
    public static String mTableStepDetails = "step_details";
    public static String mTableTimeTable = "time_table";
    public static String mTableSleepDetail = "sleep_details";
    public static String mTableMedication = "table_medication";
    public static String mTableTakenMedication = "taken_medication_table";

    //Medication Table
    public static String mTable_Medication_ID = "m_id";
    public static String mTable_Medication_name = "m_name";
    public static String mTable_Medication_User_ID = "user_id";

    //Taken Medication Table
    public static String mTaken_Medication_Table_ID = "t_id";
    public static String mTaken_Medication_Table_User_ID = "user_id";
    public static String mTaken_Medication_Table_Medication_Name = "madication_name";
    public static String mTaken_Medication_Date_N_Time = "date_time";
    public static String mTaken_Medication_Date = "t_date";
    public static String mTaken_Medication_Is_Sync = "is_sync";


    //bpm_details Table
    public static String mBPM_DETAILS_ID = "id";
    public static String mBPM_DETAILS_Parent_Id = "parent_id";
    public static String mBPM_DETAILS_Server_RecordId = "server_bpm_record_id";
    public static String mBPM_DETAILS_BPM_Value = "bpm_value";
    public static String mBPM_DETAILS_MAX_BPM = "max_bpm";//2021
    public static String mBPM_DETAILS_AVERAGE_BPM = "average_bpm";//2021
    public static String mBPM_DETAILS_date_time = "date_time";
    public static String mBPM_DETAILS_Type = "type";
    public static String mBPM_DETAILS_User_ID = "user_id";
    public static String mBPM_DEATILS_Is_Sync = "is_sync";

    // distance_details Table
    public static String mDISTANCE_DETAILS_ID = "id";
    public static String mDISTANCE_DETAILS_Parent_ID = "parent_id";
    public static String mDISTANCE_DETAILS_Server_Distance_Record_ID = "server_distance_record_id";
    public static String mDISTANCE_DETAILS_Distance_Value = "diatance_value";
    public static String mDISTANCE_DETAILS_Date_Time = "date_time";
    public static String mDISTANCE_DETAILS_Type = "type";
    public static String mDISTANCE_DETAILS_User_ID = "user_id";
    public static String mDISTANCE_DETAILS_Is_Sync = "is_sync";

    // ecg_activity Table
    public static String mECG_ACTIVITY_ID = "id";
    public static String mECG_ACTIVITY_START_TIME = "start_time";
    public static String mECG_ACTIVITY_END_TIME = "end_time";
    public static String mECG_ACTIVITY_TOTAL_TIME = "total_time";
    public static String mECG_ACTIVITY_DATE = "date";
    public static String mECG_ACTIVITY_SERVER_RECORD_ID = "server_record_id";
    public static String mECG_ACTIVITY_USER_ID = "user_id";
    public static String mECG_ACTIVITY_Is_Sync = "is_sync";


    //ecg_details Table
    public static String mECG_DETAILS_ID = "id";
    public static String mECG_DETAILS_Parent_ID = "parent_id";
    public static String mECG_DETAILS_Server_ECG_Record_ID = "server_ecg_record_id";
    public static String mECG_DETAILS_ECG_VALUE = "ecg_value";
    public static String mECG_DETAILS_DATE_TIME = "date_time";
    public static String mECG_DETAILS_TYPE = "type";
    public static String mECG_DETAILS_USER_ID = "user_id";
    public static String mECG_DETAILS_Is_Sync = "is_sync";
    public static String mECG_DETAILS_HRV_VALUE = "hrv_value";//2021
    public static String mECG_DETAILS_AVERAGE_HRV = "average_hrv";//2021
    //    public static String mECG_DETAILS_Time_Diff = "ecg_time_diff";//Commented in 2021
    public static String mECG_DETAILS_START_TIME = "start_time";
    public static String mECG_DETAILS_END_TIME = "end_time";

    //hrv_details Table
    public static String mHRV_DETAILS_ID = "id";
    public static String mHRV_DETAILS_Parent_ID = "parent_id";
    public static String mHRV_DETAILS_Server_HRV_Record_ID = "server_hrv_record_id";
    public static String mHRV_DETAILS_ECG_ID = "ecg_id";
    public static String mHRV_DETAILS_HRV_VALUE = "hrv_value";
    public static String mHRV_DETAILS_AVERAGE_HRV = "average_hrv";//2021
    public static String mHRV_DETAILS_EVENT_TYPE = "event_type";//2021
    public static String mHRV_DETAILS_EVENT_COMMENT = "event_comment";//2021
    public static String mHRV_DETAILS_USER_ID = "user_id";
    public static String mHRV_DETAILS_TYPE = "type";//Commented in 2021
    public static String mHRV_DETAILS_Is_Sync = "is_sync";
    public static String mHRV_DETAILS_DATE_TIME = "date_time";//Commented in 2021

    // main_activity Table
    public static String mMain_ACTIVITY_ID = "id";
    public static String mMain_ACTIVITY_START_TIME = "start_time";
    public static String mMain_ACTIVITY_END_TIME = "end_time";
    public static String mMain_ACTIVITY_TOTAL_TIME = "total_time";
    public static String mMain_ACTIVITY_DATE = "date";
    public static String mMain_ACTIVITY_SERVER_RECORD_ID = "server_record_id";
    public static String mMain_ACTIVITY_USER_ID = "user_id";
    public static String mMain_ACTIVITY_Is_Sync = "is_sync";
    public static String mMain_ACTIVITY_TYPE = "type";
    public static String mMain_ACTIVITY_ACTIVITY_TYPE = "activity_type";//New In 2021
    public static String mMain_ACTIVITY_RANDOM_NUMBER = "random_number";

    // met_details Table
    public static String mMET_DETAILS_ID = "id";
    public static String mMET_DETAILS_Parent_ID = "parent_id";
    public static String mMET_DETAILS_SERVER_RECORD_ID = "server_met_record_id";
    public static String mMET_DETAILS_Met_Value = "met_value";
    public static String mMET_DETAILS_Average_Met = "average_met";//2021
    public static String mMET_DETAILS_Date_Time = "date_time";
    public static String mMET_DETAILS_Type = "type";
    public static String mMET_DETAILS_User_ID = "user_id";
    public static String mMET_DETAILS_ACTIVITY_TYPE = "activity_type";//2021
    public static String mMET_DETAILS_Is_Sync = "is_sync";

    //sleep_activity Table
    public static String mSLEEP_ACTIVITY_ID = "id";
    public static String mSLEEP_ACTIVITY_START_TIME = "start_time";
    public static String mSLEEP_ACTIVITY_END_TIME = "end_time";
    public static String mSLEEP_ACTIVITY_TOTAL_TIME = "total_time";
    public static String mSLEEP_ACTIVITY_DATE = "date";
    public static String mSLEEP_ACTIVITY_SLEEP_ACTIVTY_TABLE = "sleep_activity_table";
    public static String mSLEEP_ACTIVITY_USER_ID = "user_id";
    public static String mSLEEP_ACTIVITY_Is_Sync = "is_sync";

    //spo2_details Table
    public static String mSPO2_DETAILS_ID = "id";
    public static String mSPO2_DETAILS_Parent_ID = "parent_id";
    public static String mSPO2_DETAILS_SERVER_SPO2_RECORD_ID = "server_sop2_record_id";
    public static String mSPO2_DETAILS_SPO2_Value = "spo2_value";
    public static String mSPO2_DETAILS_Date_Time = "date_time";
    public static String mSPO2_DETAILS_Type = "type";
    public static String mSPO2_DETAILS_User_ID = "user_id";
    public static String mSPO2_DETAILS_Is_Sync = "is_sync";

    // step_details Table
    public static String mSTEP_DETAILS_ID = "id";
    public static String mSTEP_DETAILS_Parent_ID = "parent_id";
    public static String mSTEP_DETAILS_SERVER_STEP_RECORD_ID = "server_steps_record_id";
    public static String mSTEP_DETAILS_STEP_Value = "step_value";
    public static String mSTEP_DETAILS_Date_Time = "date_time";
    public static String mSTEP_DETAILS_Type = "type";
    public static String mSTEP_DETAILS_User_ID = "user_id";
    public static String mSTEP_DETAILS_Is_Sync = "is_sync";

    //time_table Table
    public static String mTable_ID = "id";
    public static String mTable_Parent_ID = "parent_id";
    public static String mTable_SERVER_STEP_RECORD_TIME = "server_time_record_id";
    public static String mTable_STEP_Value = "time";
    public static String mTable_Date_Time = "date_time";
    public static String mTable_Type = "type";
    public static String mTable_User_ID = "user_id";
    public static String mTable_Is_Sync = "is_sync";
    public static String mTable_DATE = "date";

    // USER TABLE
    public static String mUSER_TABLE_ID = "id";
    public static String mUSER_TABLE_NAME = "name";
    public static String mUSER_TABLE_EMAIL = "email";
    public static String mUSER_TABLE_PHOTO = "photo";
    public static String mUSER_TABLE_HEIGHT = "height";
    public static String mUSER_TABLE_WEIGHT = "weight";
    public static String mUSER_TABLE_USER_ID = "user_id";
    public static String mUSER_TABLE_UNIT = "unit";
    public static String mUSER_TABLE_GENDER = "gender";
    public static String mUSER_TABLE_DOB = "dob";
    public static String mUSER_TABLE_TOWN = "town";
    public static String mUSER_TABLE_PHONE_NO = "phone_no";
    public static String mUSER_TABLE_ACCESS_TOKEN = "access_token";
    public static String mUSER_TABLE_DEVICE_TYPE = "device_type";
    public static String mUSER_TABLE_DEVICE_TOKEN = "device_token";

    //sleep_detail Table
    public static String mSLEEP_DETAIL_ID = "id";
    public static String mSLEEP_DETAIL_RANDOM_NUMBER = "random_number";
    public static String mSLEEP_DETAIL_SERVER_SLEEP_RECORD_ID = "server_sleep_record_id";
    public static String mSLEEP_DETAIL_SLEEP_VALUE = "sleep_value";
    public static String mSLEEP_DETAIL_SLEEP_DIFFERENCE = "sleep_difference";
    public static String mSLEEP_DETAIL_TOTAL_TIME = "total_time";
    public static String mSLEEP_DETAIL_USER_ID = "user_id";
    public static String mSLEEP_DETAIL_IS_SYNC = "is_sync";
    public static String mSLEEP_DETAIL_START_TIME = "start_time";
    public static String mSLEEP_DETAIL_END_TIME = "end_time";
    public static String mSLEEP_DETAIL_DATE = "date";

    //    public static int DATABASE_VERSION=1;
    public static int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        //		mCommomMethod=new CommomMethod(myContext);
        //		myImageLoader=new MyImageLoader(myContext);
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
        } else {
            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();
            this.close();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);

            checkDB = getWritableDatabase();
        } catch (SQLiteException e) {
            // database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[2048];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

        //Adding new data
        openDatabase();
        newChangesInTablesIn2021(db);
    }

    public void openDatabase() throws SQLException {
        try {
            db.close();
        } catch (Exception e) {
        }
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

//    public synchronized void close() {
//        if (db != null)
//            db.close();
//        super.close();
//    }

//    public void onCreate(SQLiteDatabase db) {
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }

//    /**
//     * Constructor Takes and keeps a reference of the passed context in order to
//     * access to the application assets and resources.
//     *
//     * @param context
//     */
//    public DBHelper(Context context) {
//        super(context, DB_NAME, null, 2);
//        this.myContext = context;
//        //		mCommomMethod=new CommomMethod(myContext);
//        //		myImageLoader=new MyImageLoader(myContext);
//
////        if (android.os.Build.VERSION.SDK_INT >= 17) {
////            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
////        } else {
////            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
////        }
//    }
//
//    /**
//     * Creates a empty database on the system and rewrites it with your own
//     * database.
//     */
//    public void createDataBase() throws IOException {
//        boolean dbExist = checkDataBase();
//
//        if (dbExist) {
//            // do nothing - database already exist
//        } else {
//            // By calling this method and empty database will be created into
//            // the default system path
//            // of your application so we are gonna be able to overwrite that
//            // database with our database.
//            this.getReadableDatabase();
//            try {
//                copyDataBase();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    /**
//     * Check if the database already exist to avoid re-copying the file each
//     * time you open the application.
//     *
//     * @return true if it exists, false if it doesn't
//     */
////    public boolean checkDataBase() {
////        SQLiteDatabase checkDB = null;
////        try {
////            String myPath = DB_PATH + DB_NAME;
////            checkDB = SQLiteDatabase.openDatabase(myPath, null,
////                    SQLiteDatabase.OPEN_READWRITE);
////
////            checkDB = getWritableDatabase();
////        } catch (SQLiteException e) {
////            // database does't exist yet.
////        }
////        if (checkDB != null) {
////            checkDB.close();
////        }
////        return checkDB != null ? true : false;
////    }
//    public boolean checkDataBase() {
//        SQLiteDatabase checkDB = null;
//        try {
//
//            String myPath = DB_PATH + DB_NAME;
//
//
//            System.out.println("Sanjay ...."+myPath);
//
//            File file = new File(myPath);
//            if (file.exists() && !file.isDirectory()) {
//                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
//                checkDB = getWritableDatabase();
//            }
//
//        } catch (SQLiteException e) {
//            // database does't exist yet.
//        }
//
//        if (checkDB != null) {
//            checkDB.close();
//        }
//
//        return checkDB != null ? true : false;
//    }

    public long getCount(String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return cnt;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
//    public void copyDataBase() throws IOException {
//        // Open your local db as the input stream
//        InputStream myInput = myContext.getAssets().open(DB_NAME);
//
//        // Path to the just created empty db
//        String outFileName = DB_PATH + DB_NAME;
//
//        // Open the empty db as the output stream
//        OutputStream myOutput = new FileOutputStream(outFileName);
//
//        // transfer bytes from the inputfile to the outputfile
//        byte[] buffer = new byte[2048];
//        int length;
//        while ((length = myInput.read(buffer)) > 0) {
//            myOutput.write(buffer, 0, length);
//        }
//
//        // Close the streams
//        myOutput.flush();
//        myOutput.close();
//        myInput.close();
//
//    }
    public void copyDatabaseToExternalStoage(Context c, String DATABASE_NAME) {
        String databasePath = c.getDatabasePath(DATABASE_NAME).getPath();
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
        Log.d("testing", " testing db path " + databasePath);
        Log.d("testing", " testing db exist " + f.exists());
        if (f.exists()) {
            try {
                File directory = new File("/mnt/sdcard/DB_ONECLICK");
                if (!directory.exists())
                    directory.mkdir();
                myOutput = new FileOutputStream(directory.getAbsolutePath()
                        + "/" + DATABASE_NAME);
                myInput = new FileInputStream(databasePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (myOutput != null) {
                        myOutput.close();
                        myOutput = null;
                    }
                    if (myInput != null) {
                        myInput.close();
                        myInput = null;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

//    public void openDatabase() throws SQLException {
//        try {
//            db.close();
//        } catch (Exception e) {
//
//        }
//        // Open the database
//        String myPath = DB_PATH + DB_NAME;
//        db = SQLiteDatabase.openDatabase(myPath, null,
//                SQLiteDatabase.OPEN_READWRITE);
//    }

//    public synchronized void close() {
//        if (db != null)
//            db.close();
//        super.close();
//    }

    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DBHelper", "onUpgrade() Called\noldVersion=" + oldVersion + "newVersion=" + newVersion);
        if (oldVersion == 1) {

            newChangesInTablesIn2021(db);
        }
    }

    /**
     * Use this function to set the value of a particular column
     *
     * @param columnName       The column name whose value is to be changed
     * @param newColumnValue   The value to be replaced in the column
     * @param whereColumnName  The column name to be compared with the where clause
     * @param whereColumnValue The value to be compared in the where clause
     */

    void onUpdateSet(String columnName, String newColumnValue,
                     String[] whereColumnName, String[] whereColumnValue) {
        String expanded_ColumnNames = new String(whereColumnName[0]);
        String expanded_ColumnValues = new String(whereColumnValue[0]);
        for (int i = 1; i < whereColumnName.length; i++) {
            expanded_ColumnNames = expanded_ColumnNames + ","
                    + whereColumnName[i];
            expanded_ColumnValues = expanded_ColumnValues + ","
                    + whereColumnValue[i];
        }
        try {
            openDatabase();
            db.execSQL("update recipe set \"" + columnName + "\" = \""
                    + newColumnValue + "\" where \"" + expanded_ColumnNames
                    + "\" = \"" + expanded_ColumnValues + "\"");
        } catch (Exception e) {
        }
//        db.close();
    }

    public void deleteTableData(String TableName) {
        try {
            openDatabase();
            db.execSQL("delete from " + TableName);
        } catch (Exception e) {
        }
//        db.close();
    }

    /**
     * Query the given table, returning a Cursor over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will return
     *                      all columns, which is discouraged to prevent reading data from
     *                      storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an SQL
     *                      WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the
     *                      values from selectionArgs, in order that they appear in the
     *                      selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor, if
     *                      row grouping is being used, formatted as an SQL HAVING clause
     *                      (excluding the HAVING itself). Passing null will cause all row
     *                      groups to be included, and is required when row grouping is
     *                      not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return A Cursor object, which is positioned before the first entry
     */

    public Cursor onQueryGetCursor(String table, String[] columns, String selection,
                                   String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor query = null;
        try {
            openDatabase();
            query = db.query(table, columns, selection, selectionArgs, groupBy,
                    having, orderBy);
        } catch (Exception e) {
        }
        query.close();
        return query;
    }

    /**
     * Use this method to search a particular String in the provided field.
     *
     * @param columns     The array of columns to be returned
     * @param table       The table name
     * @param whereColumn The where clause specifying a particular columns
     * @param keyword     The keyword which is to be searched
     * @return The cursor containing the result of the query
     */

//	 public Cursor rawQuery(String sql, String[] selectionArgs) {
////	        return rawQueryWithFactory(null, sql, selectionArgs, null, null);
//		 return;
//	    }

//	 public Cursor rawQueryWithFactory(
//	            CursorFactory cursorFactory, String sql, String[] selectionArgs,
//	            String editTable, CancellationSignal cancellationSignal) {
//	        acquireReference();
//	        try {
//	            SQLiteCursorDriver driver = new SQLiteDirectCursorDriver(this, sql, editTable,
//	                    cancellationSignal);
//	            return driver.query(cursorFactory != null ? cursorFactory : mCursorFactory,
//	                    selectionArgs);
//	        } finally {
//	            releaseReference();
//	        }
//	    }

    Cursor onSearchGetCursor(String[] columns, String table,
                             String[] whereColumn, String keyword) {
        String expColumns = new String(columns[0]);
        Cursor rawquery = null;
        for (int i = 1; i < columns.length; i++)
            expColumns = expColumns + "," + columns[i];
        try {
            openDatabase();
            rawquery = db.rawQuery("SELECT " + expColumns + " from " + table
                    //	+ " where " + whereColumn[0] + " like \"%" + keyword
                    //+ "%\" or " + whereColumn[1] + " like \"%" + keyword
                    //+ "%\" or " + whereColumn[2] + " like \"%" + keyword
                    //+ "%\""
                    , null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rawquery != null)
            rawquery.close();
        return rawquery;
    }


    public Cursor Query(String sql) {

        Cursor c = null;

        try {
            c = db.rawQuery(sql, null);
        } catch (Exception e) {
        }
        c.close();
//        db.close();
        return c;
    }

    public int getFirstRecordSqlQueryInt(String sql) {

        Cursor c = null;

        try {
            c = db.rawQuery(sql, null);
        } catch (Exception e) {
        }
        c.moveToFirst();
        c.close();
//        db.close();
        return c.getInt(0);
    }

    public String getFirstRecordSqlQueryString(String sql) {

        Cursor c = null;

        try {
            c = db.rawQuery(sql, null);
        } catch (Exception e) {
        }
        c.moveToFirst();
        c.close();
//        db.close();
        return c.getString(0);

    }


    /**
     * update particular record in the database.
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     */

    public void onDelete(String table, String whereClause, String[] whereArgs) {
        openDatabase();
        try {
            db.delete(table, whereClause, whereArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        db.close();
    }

    /**
     * update particular record in the database.
     *
     * @param tableName
     * @param cValue
     * @param WhereField
     * @param complareValue
     */

    public void updateRecord(String tableName, ContentValues cValue, String WhereField, String[] complareValue) {
        openDatabase();
        try {
            db.update(tableName, cValue, WhereField, complareValue);
        } catch (SQLException e) {
        }
//        db.close();

    }

    /**
     * Insert the record in the database.
     *
     * @param tableName
     * @param cValue
     */

    public int insertRecord(String tableName, ContentValues cValue) {
        openDatabase();
        try {
            return (int) db.insert(tableName, null, cValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        db.close();

        return -1;
    }

    /**
     * Insert the record in the database.
     *
     * @param tableName
     * @param cValue
     */

    public void insertUpdateRecord(String tableName, ContentValues cValue) {
        openDatabase();
        try {
            db.replaceOrThrow(tableName, null, cValue);
//            db.close();
        } catch (SQLException e) {
        }
    }

    public void exeQuery(String sql) {
        openDatabase();
        try {
            db.execSQL(sql);
        } catch (Exception e) {
        }
    }


    @SuppressWarnings("null")
    public DataHolder READCURSOR(Cursor MCURSOR) {
        DataHolder _HOLDER = null;
        if (MCURSOR != null && MCURSOR.getCount() > 0) {
            MCURSOR.moveToFirst();
            while (!MCURSOR.isAfterLast()) {
                int COUNT = MCURSOR.getColumnCount();
                _HOLDER.CreateRow();
                for (int i = 0; i < COUNT; i++) {
                    _HOLDER.set_Lmap(MCURSOR.getColumnName(i), MCURSOR.getString(i));
                }
                _HOLDER.AddRow();
                MCURSOR.moveToNext();
            }
        }
        return _HOLDER;
    }


    public DataHolder read(String sql) {
        openDatabase();
        DataHolder _holder = null;
        try {
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor != null) {
                cursor.moveToFirst();
                _holder = new DataHolder();

                while (!cursor.isAfterLast()) {

                    int count = cursor.getColumnCount();

                    _holder.CreateRow();

                    for (int i = 0; i < count; i++) {
                        _holder.set_Lmap(cursor.getColumnName(i), cursor.getString(i));
                    }
                    _holder.AddRow();
                    cursor.moveToNext();
                }
            }
            if (cursor != null) {
                cursor.close();
                close();
                db.close();
            }
        } catch (Exception e) {
        }
        return _holder;
    }

    public DataHolder readFromTableName(String TableName, String cols[], String where[], String keyword) {

        //openDataBase();
        DataHolder _holder = null;

        Cursor c = null;

        c = db.query(TableName, cols, where[0], null, null, null, null);

        if (c != null) {
            c.moveToFirst();
            _holder = new DataHolder();
            while (!c.isAfterLast()) {
                int count = c.getColumnCount();
                _holder.CreateRow();
                for (int i = 0; i < count; i++) {
                    _holder.set_Lmap(c.getColumnName(i), c.getString(i));
                }
                _holder.AddRow();
                c.moveToNext();
            }
        }
        c.close();
        close();
//        db.close();
        return _holder;
    }

    public Cursor rawQuery(String SQL, Object object) {
        // TODO Auto-generated method stub
        return rawQuery(SQL, object);
    }

    static public String createInsert(final String tableName, final String[] columnNames, final StringBuilder columnValues) {
        if (tableName == null || columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException();
        }
        final StringBuilder s = new StringBuilder();
        s.append("INSERT INTO ").append(tableName).append(" (");
        for (String column : columnNames) {
            s.append(column).append(" ,");
        }
        int length = s.length();
        s.delete(length - 2, length);
        s.append(" ) VALUES " + columnValues);

        return s.toString();
    }

    public void ReadUsingRawQery(String sql) {
        openDataBase();
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }

    public void openDataBase() throws SQLException {

        //TODO last change here
        try {
            if (db == null || !db.isOpen()) {
                // Open the database
                String myPath = DB_PATH + DB_NAME;
                db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void newChangesInTablesIn2021(SQLiteDatabase database) {
        Log.d("DbHelper", "newChangesInTablesIn2021() called.");
        try {

            //Adding new column activity_type in main_activity_table
            database.execSQL("ALTER TABLE " + mTableMainActivityTable + " ADD COLUMN " + mMain_ACTIVITY_ACTIVITY_TYPE + " text;");

            //Adding new columns max_bpm and average_bpm in bpm_details
            database.execSQL("ALTER TABLE " + mTableBPMDetails + " ADD COLUMN " + mBPM_DETAILS_MAX_BPM + " text;");
            database.execSQL("ALTER TABLE " + mTableBPMDetails + " ADD COLUMN " + mBPM_DETAILS_AVERAGE_BPM + " text;");

            //Adding new columns activity_type and average_met in met_details
            database.execSQL("ALTER TABLE " + mTableMETDetails + " ADD COLUMN " + mMET_DETAILS_ACTIVITY_TYPE + " text;");
            database.execSQL("ALTER TABLE " + mTableMETDetails + " ADD COLUMN " + mMET_DETAILS_Average_Met + " text;");

            //Adding new columns average_hrv,event_type and event_comment in hrv_details
            database.execSQL("ALTER TABLE " + mTableHRVDetails + " ADD COLUMN " + mHRV_DETAILS_AVERAGE_HRV + " text;");
            database.execSQL("ALTER TABLE " + mTableHRVDetails + " ADD COLUMN " + mHRV_DETAILS_EVENT_TYPE + " text;");
            database.execSQL("ALTER TABLE " + mTableHRVDetails + " ADD COLUMN " + mHRV_DETAILS_EVENT_COMMENT + " text;");

            //Adding new columns average_hrv and hrv_value in ecg_details
            database.execSQL("ALTER TABLE " + mTableECGDetails + " ADD COLUMN " + mECG_DETAILS_AVERAGE_HRV + " text;");
            database.execSQL("ALTER TABLE " + mTableECGDetails + " ADD COLUMN " + mECG_DETAILS_HRV_VALUE + " text;");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //2021
    public void removeAllBlankEntries() {

        try {
            openDatabase();
            db.execSQL("delete from " + mTableMainActivityTable + " where end_time=\"" + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        db.close();
    }

    //April 2021
    public boolean isThereAnyOnGoingSleepRecord() {

        try {
            openDatabase();
            Cursor res = db.rawQuery( "select * from " + mTableSleepDetail + " where end_time IS NULL",null );
            if(res.getCount()>0){
                res.close();
                return true;
            }else{
                res.close();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
//        db.close();
    }

    //April 2021
    public Cursor getSleepRecordWhichIsGoingOn() {

        try {
            openDatabase();
            Cursor res = db.rawQuery( "select * from " + mTableSleepDetail + " where end_time IS NULL",null );
            if(res.getCount()>0){
                return res;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        db.close();
    }


    //2021
    public void removeAllBlankSleepEntries() {
        try {
            openDatabase();
            db.execSQL("delete from " + mTableSleepDetail + " where end_time IS NULL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
