package com.example.irvin.integrate_zxing;

/**
 * Created by IRVIN on 21/07/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Sampler;

public class DatabaseHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "SETTINGS_MANAGER";
    private static final String TABLE_SETTINGS = "SETTINGS";
    private static final String KEY_DESCRIPTION = "DESCRIPTION";
    private static final String KEY_VALUE = "VALUE";

    private static final String TABLE_IMAGE = "IMAGE";
    private static final String KEY_IMAGE = "IMAGE";

    public enum SETTING{
        SERVER_ADDRESS("SERVER_ADDRES", 0),
        PORT_NUMBER("PORT_NUMBER", 1),
        SERVER_SERVLET("SERVER_SERVLET", 2);

        private String stringValue;
        private int intValue;

        SETTING(String toString, int value){
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString(){
            return stringValue;
        }
    }

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_SETTING_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "(" +
                                        KEY_DESCRIPTION + " STRING, " +
                                        KEY_VALUE + " STRING)";

        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "(" +
                                        KEY_IMAGE + " STRING)";

        db.execSQL(CREATE_SETTING_TABLE);
        db.execSQL(CREATE_IMAGE_TABLE);

        addSetting(db, SETTING.SERVER_ADDRESS, "");
        addSetting(db, SETTING.PORT_NUMBER, "");
        addSetting(db, SETTING.SERVER_SERVLET, "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(db);
    }

    private void addSetting(SQLiteDatabase db, SETTING setting, String value){
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, setting.toString());
        values.put(KEY_VALUE, value);

        db.insert(TABLE_SETTINGS, null, values);
    }

    public String getSetting(SETTING setting){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SETTINGS,
                                 new String[]{KEY_DESCRIPTION, KEY_VALUE},
                                 KEY_DESCRIPTION + "=?",
                                 new String[]{setting.toString()},
                                 null,
                                 null,
                                 null);

        String value = "";

        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    value = cursor.getString(1);
                } while (cursor.moveToNext());
            }
        }

        return value;
    }

    public int updateSettings(SETTING setting, String value){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, setting.toString());
        values.put(KEY_VALUE, value);

        return db.update(TABLE_SETTINGS,
                         values,
                         KEY_DESCRIPTION + "=?",
                         new String[]{setting.toString()});
    }

    public void saveImage(String stringImage){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        values.put(KEY_IMAGE, stringImage);

        db.insert(TABLE_IMAGE, null, values);
    }

    public String getImage(){
        String value = "";

        return value;
    }

}
