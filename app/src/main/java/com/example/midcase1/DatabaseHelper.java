package com.example.midcase1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "students.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "students";
    public static final String COL_ID = "id";
    public static final String COL_STUDENT_ID = "student_id";
    public static final String COL_STUDENT_NAME = "student_name";
    public static final String COL_MAJOR = "major";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STUDENT_ID + " TEXT UNIQUE NOT NULL, " +
                COL_STUDENT_NAME + " TEXT NOT NULL, " +
                COL_MAJOR + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String studentId, String studentName, String major) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_STUDENT_NAME, studentName);
        values.put(COL_MAJOR, major);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updateData(String id, String studentId, String studentName, String major) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_STUDENT_NAME, studentName);
        values.put(COL_MAJOR, major);

        long result = db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{id});
        return result > 0;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{id});
    }
}