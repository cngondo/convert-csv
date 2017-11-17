package com.example.ngondo.convertcsv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBController extends SQLiteOpenHelper {

    private final String DB = null; //tag for the DB creation process


    //public contructor to initialize the Db
    public DBController(Context context) {
        super(context, DBModel.DBNAME, null, 1);
        Log.d(DB, "Database created");
    }

    /*
    * Initial call creates the database.
    * */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+ DBModel.TABLE
                + "( Id INTEGER PRIMARY KEY, "
                + DBModel.Columns.ADMNO + " NUMBER, "
                + DBModel.Columns.STUDENTNAME + " TEXT, "
                + DBModel.Columns.PARENTNO + " NUMBER)";

        sqLiteDatabase.execSQL(createTableQuery);
        Log.d("TABLECREATION", "Table sucessfully created " + createTableQuery);
    }

    //uppgrading the DB
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBModel.TABLE);
        onCreate(sqLiteDatabase);
    }
}
