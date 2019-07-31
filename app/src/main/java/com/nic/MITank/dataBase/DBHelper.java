package com.nic.MITank.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MITank";
    private static final int DATABASE_VERSION = 1;


    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String HABITATION_TABLE_NAME = " habitaionTable";
    public static final String MI_TANK_STRUCTURE = "mi_tank_structure";
    public static final String MI_TANK_DATA = "mi_tank_data";
    public static final String STRUCTURES = "structures";


    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + VILLAGE_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "pvname TEXT)");

        db.execSQL("CREATE TABLE " + HABITATION_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habitation_code TEXT," +
                "habitation_name TEXT)");

        db.execSQL("CREATE TABLE " + MI_TANK_STRUCTURE + " ("
                + "mi_tank_structure_id  INTEGER," +
                "mi_tank_structure_name TEXT)");

        db.execSQL("CREATE TABLE " + MI_TANK_DATA + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "habcode INTEGER," +
                "mi_tank_survey_id INTEGER," +
                "minor_irrigation_type INTEGER," +
                "name_of_the_mi_tank TEXT," +
                "local_name TEXT," +
                "area TEXT)");


        db.execSQL("CREATE TABLE " + STRUCTURES + " ("
                + "mi_tank_structure_detail_id INTEGER,"+
                "mi_tank_survey_id INTEGER," +
                "mi_tank_structure_id INTEGER," +
                "mi_tank_structure_serial_id INTEGER," +
                "mi_tank_condition_id INTEGER," +
                "mi_tank_condition_name TEXT," +
                "mi_tank_sill_level TEXT," +
                "mi_tank_structure_name TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_STRUCTURE);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + STRUCTURES);
            onCreate(db);
        }
    }


}
