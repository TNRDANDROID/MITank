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
    public static final String MI_TANK_DATA_STRUCTURES = "mi_tank_data_structures";
    public static final String MI_TANK_CONDITION = "mi_tank_condition";
    public static final String MI_TANK_TYPE_INLET_STRUCTURE = "mi_tank_type_inlet_structure";
    public static final String MI_TANK_TYPE_SLUICE_STRUCTURE = "mi_tank_type_sluice_structure";
    public static final String SAVE_MI_TANK_IMAGES = "save_mi_tank_images";
    public static final String SAVE_TRACK_TABLE = "TrackTable";
    public static final String MINOR_IRRIGATION_TYPE = "minor_irrigation_type";
    public static final String SAVE_MI_TANK_CENTER_IMAGES = "save_mi_tank_center_images";


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
                 "nature_of_tank_id_1_active  TEXT," +
                 "nature_of_tank_id_2_active  TEXT," +
                "mi_tank_structure_name TEXT)");

        db.execSQL("CREATE TABLE " + MI_TANK_CONDITION + " ("
                + "mi_tank_condition_id  INTEGER," +
                "mi_tank_condition_name TEXT)");

        db.execSQL("CREATE TABLE " + MI_TANK_TYPE_INLET_STRUCTURE + " ("
                + "mi_tank_type_id  INTEGER," +
                "mi_tank_type_name TEXT)");
        db.execSQL("CREATE TABLE " + MI_TANK_TYPE_SLUICE_STRUCTURE + " ("
                + "mi_tank_type_id  INTEGER," +
                "mi_tank_type_name TEXT)");
        db.execSQL("CREATE TABLE " + MINOR_IRRIGATION_TYPE + " ("
                + "mi_tank_type_id  INTEGER," +
                "mi_tank_type_name TEXT)");

        db.execSQL("CREATE TABLE " + MI_TANK_DATA + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "habcode INTEGER," +
                "mi_tank_survey_id INTEGER," +
                "minor_irrigation_type INTEGER," +
                "name_of_the_mi_tank TEXT," +
                "local_name TEXT," +
                "center_point_latitude TEXT," +
                "center_point_longitude TEXT," +
                "center_point_captured TEXT," +
                "area TEXT)");


        db.execSQL("CREATE TABLE " + MI_TANK_DATA_STRUCTURES + " ("
                + "mi_tank_structure_detail_id INTEGER,"+
                "mi_tank_survey_id INTEGER," +
                "mi_tank_structure_id INTEGER," +
                "mi_tank_structure_serial_id INTEGER," +
                "minor_irrigation_type INTEGER," +
                "mi_tank_condition_id INTEGER," +
                "mi_tank_condition_name TEXT," +
                "mi_tank_type_id INTEGER," +
                "mi_tank_type_name TEXT," +
                "mi_tank_sill_level TEXT," +
                "image_available TEXT," +
                "mi_tank_structure_name TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_MI_TANK_IMAGES + " ("
                +   "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "habcode INTEGER," +
                "mi_tank_structure_detail_id INTEGER,"+
                "mi_tank_structure_serial_id INTEGER,"+
                "mi_tank_survey_id INTEGER,"+
                "mi_tank_structure_id INTEGER,"+
                "mi_tank_condition_id INTEGER,"+
                "mi_tank_condition_name TEXT," +
                "mi_tank_type_id INTEGER," +
                "mi_tank_type_name TEXT," +
                "mi_tank_sill_level TEXT," +
                "image BLOB," +
                "latitude TEXT," +
                "longitude TEXT)");
        db.execSQL("CREATE TABLE " + SAVE_MI_TANK_CENTER_IMAGES + " ("
                +   "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "habcode INTEGER," +
                "mi_tank_survey_id INTEGER,"+
                "image BLOB," +
                "latitude TEXT," +
                "longitude TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_TRACK_TABLE + " ("
                +"dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "habcode INTEGER," +
                "mi_tank_survey_id INTEGER," +
                "server_flag  INTEGER DEFAULT 0," +
                "point_sl_no INTEGER," +
                "point_type TEXT," +
                "latitude TEXT," +
                "longitude TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_STRUCTURE);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_DATA_STRUCTURES);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_CONDITION);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_TYPE_INLET_STRUCTURE);
            db.execSQL("DROP TABLE IF EXISTS " + MI_TANK_TYPE_SLUICE_STRUCTURE);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_TRACK_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MINOR_IRRIGATION_TYPE);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_MI_TANK_CENTER_IMAGES);
            onCreate(db);
        }
    }


}
