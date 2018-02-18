package com.example.vivek.recycleviewdatabasedeleteandsearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by VIVEK on 11/17/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UserManager.db";

    // RegistrationUserDataModel table name
    private static final String TABLE_USER = "user";

    // RegistrationUserDataModel Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_FIRST_NAME = "user_firstname";
    private static final String COLUMN_USER_LAST_NAME = "user_lastname";





    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_FIRST_NAME + " TEXT," +
            COLUMN_USER_LAST_NAME + " TEXT " + ")";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop RegistrationUserDataModel Table if exist
        db.execSQL(DROP_USER_TABLE);

        // Create tables again
        onCreate(db);

    }

    /**
     * This method is to create registrationUserDataModel record
     *
     * @param userDataModel
     */
    public void addUser(ListDataModel userDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FIRST_NAME, userDataModel.getFirstName());
        values.put(COLUMN_USER_LAST_NAME, userDataModel.getSecondName());

        db.insert(TABLE_USER, null, values);
        db.close();
    }


    public List<ListDataModel> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_FIRST_NAME,
                COLUMN_USER_LAST_NAME
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_FIRST_NAME + " ASC";
        List<ListDataModel> userDataModelList = new ArrayList<ListDataModel>();

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ListDataModel listDataModel = new ListDataModel();
                listDataModel.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                listDataModel.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_FIRST_NAME)));
                listDataModel.setSecondName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LAST_NAME)));

                userDataModelList.add(listDataModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userDataModelList;
    }


    boolean deleteUser(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_USER, COLUMN_USER_ID + "=?", new String[]{String.valueOf(id)}) == 1;
    }

    Cursor getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USER, null);
    }


    public boolean deleteFact(ListDataModel user) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_USER + " WHERE " +COLUMN_USER_ID + " ='" + user.getId() + "'");
        database.close();

       //    Log.d("deleted",String.valueOf(user.getId()));
        return true;
    }


}
