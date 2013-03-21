package com.jason.ocbcapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_APPOINTMENTS = "appointments";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_APPT_DATETIME = "appt_datetime";
  public static final String COLUMN_QUEUE_NO = "queue_number";
  public static final String COLUMN_BRANCH_ID = "branch_id";

  private static final String DATABASE_NAME = "appointments.db";
  private static final int DATABASE_VERSION = 1;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_APPOINTMENTS + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_APPT_DATETIME
      + " long, " + COLUMN_BRANCH_ID
      + " integer, " + COLUMN_QUEUE_NO
      + " integer);";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
    onCreate(db);
  }

} 