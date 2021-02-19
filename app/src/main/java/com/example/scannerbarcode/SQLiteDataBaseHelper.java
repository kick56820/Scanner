package com.example.scannerbarcode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteDataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG ="SQLiteDataBaseHelper";
    private static final String TABLE_NAME ="people_table";
    private static final String COL0 = "id";
    private static final String COL1 = "carnumber";
    private static final String COL2 = "cusnumber";
    private static final String COL3 = "pronumber";
    private static final String COL4 = "number";
    private static final String COL5 = "daytime";
    private static final String COL6 = "udpateStatus";

    public SQLiteDataBaseHelper(@Nullable Context context){super(context,TABLE_NAME,null,1);}

    @Override
    public void onCreate(SQLiteDatabase db) {

      String createTable = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COL1 +" TEXT,"+ COL2 +" TEXT,"+ COL3 +" TEXT,"+ COL4 +" TEXT,"+ COL5 +" DATETIME DEFAULT (datetime('now','localtime'))," + COL6 +" TEXT)";
      db.execSQL(createTable);
      
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
       db.execSQL("DROP  TABLE " + TABLE_NAME);
       onCreate(db);
    }

    public boolean addData(String item1, String item2, String item3, String item4) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, item1);
        contentValues.put(COL2, item2);
        contentValues.put(COL3, item3);
        contentValues.put(COL4, item4);
        contentValues.put(COL6, "no");


        Log.d(TAG, "addData: Adding " + item1+"," + item2+"," + item3+"," + item4+"," + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getItemID(String newcusnumber, String pronumber){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + newcusnumber + "' "+ "AND " + COL3+ " = '" + pronumber + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateName(String newcarnumber, String newcusnumber, String pronumber,String number, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "updateName: Setting name to " + id);
        ContentValues values = new ContentValues();
        values.put(COL1, newcarnumber);
        values.put(COL2, newcusnumber);
        values.put(COL3, pronumber);
        values.put(COL4, number);
        db.update(TABLE_NAME, values, "id = '" + id + "'", null);
    }

    public void deleteName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL0 + " = '" + id + "'";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed\n";
        }
        return msg;
    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM people_table";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put(COL1, cursor.getString(1));
                map.put(COL2, cursor.getString(2));
                map.put(COL3, cursor.getString(3));
                map.put(COL4, cursor.getString(4));
                map.put(COL5, cursor.getString(5));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM people_table where udpateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM people_table where udpateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put(COL1, cursor.getString(1));
                map.put(COL2, cursor.getString(2));
                map.put(COL3, cursor.getString(3));
                map.put(COL4, cursor.getString(4));
                map.put(COL5, cursor.getString(5));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    public void updateSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update people_table set udpateStatus = '"+ status +"' where id="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
}
