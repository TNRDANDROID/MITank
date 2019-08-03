package com.nic.MITank.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.model.MITank;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public dbData(Context context){
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    /****** DISTRICT TABLE *****/


    /****** VILLAGE TABLE *****/
    public MITank insertVillage(MITank miTank) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, miTank.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, miTank.getBlockCode());
        values.put(AppConstant.PV_CODE, miTank.getPvCode());
        values.put(AppConstant.PV_NAME, miTank.getPvName());

        long id = db.insert(DBHelper.VILLAGE_TABLE_NAME,null,values);
        Log.d("Inserted_id_village", String.valueOf(id));

        return miTank;
    }
    public ArrayList<MITank > getAll_Village(String dcode, String bcode) {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by pvname asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank  card = new MITank ();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public MITank insertHabitation(MITank miTank) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, miTank.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, miTank.getBlockCode());
        values.put(AppConstant.PV_CODE, miTank.getPvCode());
        values.put(AppConstant.HABB_CODE, miTank.getHabCode());
        values.put(AppConstant.HABITATION_NAME, miTank.getHabitationName());

        long id = db.insert(DBHelper.HABITATION_TABLE_NAME,null,values);
        Log.d("Inserted_id_habitation", String.valueOf(id));

        return miTank;
    }
    public ArrayList<MITank > getAll_Habitation(String dcode, String bcode) {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.HABITATION_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by habitation_name asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank  card = new MITank ();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABB_CODE)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public MITank insertTankStructure(MITank miTank) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.MI_TANK_STRUCTURE_ID, miTank.getMiTankStructureId());
        values.put(AppConstant.MI_TANK_STRUCTURE_NAME, miTank.getMiTankStructureName());

        long id = db.insert(DBHelper.MI_TANK_STRUCTURE,null,values);
        Log.d("Insert_id_TANK_STRUCT", String.valueOf(id));

        return miTank;
    }

    public ArrayList<MITank > getAllTankStructure() {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(DBHelper.MI_TANK_STRUCTURE,
                   new String[]{"*"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank  card = new MITank ();
                    card.setMiTankStructureId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_ID)));
                    card.setMiTankStructureName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<MITank > getStructureForParticularTank(String mi_tank_survey_id) {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(DBHelper.MI_TANK_STRUCTURE,
                    new String[]{"*"}, null, null, null, null, null);
            cursor = db.rawQuery("select a.mi_tank_structure_id as mi_tank_structure_id,b.mi_tank_structure_name as mi_tank_structure_name from (select distinct mi_tank_structure_id from "+DBHelper.STRUCTURES+" where mi_tank_survey_id = "+mi_tank_survey_id+")a left join (select * from "+DBHelper.MI_TANK_STRUCTURE+") b on a.mi_tank_structure_id = b.mi_tank_structure_id",null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank  card = new MITank ();
                    card.setMiTankStructureId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_ID)));
                    card.setMiTankStructureName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public MITank insertMITankData(MITank miTank) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, miTank.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, miTank.getBlockCode());
        values.put(AppConstant.PV_CODE, miTank.getPvCode());
        values.put(AppConstant.HAB_CODE, miTank.getHabCode());
        values.put(AppConstant.MI_TANK_SURVEY_ID, miTank.getMiTankSurveyId());
        values.put(AppConstant.MINOR_IRRIGATION_TYPE, miTank.getMinorIrrigationType());
        values.put(AppConstant.NAME_OF_THE_MI_TANK, miTank.getNameOftheMITank());
        values.put(AppConstant.LOCAL_NAME, miTank.getLocalName());
        values.put(AppConstant.AREA, miTank.getArea());

        long id = db.insert(DBHelper.MI_TANK_DATA,null,values);
        Log.d("Insert_id_TANK_Data", String.valueOf(id));

        return miTank;
    }

    public ArrayList<MITank > getAllMITankData(String purpose,String dcode,String bcode,String pvcode,String habcode,String checkboxvalue) {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

       if(purpose.equals("fetch")){

           if(checkboxvalue.equals("all")){
               selection = "dcode = ? and bcode = ? and pvcode = ? and habcode = ? ";
               selectionArgs = new String[]{dcode,bcode,pvcode,habcode};
           }else {
               selection = "dcode = ? and bcode = ? and pvcode = ? and habcode = ? and minor_irrigation_type = ?";
               selectionArgs = new String[]{dcode,bcode,pvcode,habcode,checkboxvalue};
           }

        }


        try {
            cursor = db.query(DBHelper.MI_TANK_DATA,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank  card = new MITank ();

                    card.setDistictCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setMiTankSurveyId(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_SURVEY_ID)));
                    card.setMinorIrrigationType(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MINOR_IRRIGATION_TYPE)));
                    card.setNameOftheMITank(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.NAME_OF_THE_MI_TANK)));
                    card.setLocalName(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.LOCAL_NAME)));
                    card.setArea(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.AREA)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public MITank insertStructure(MITank miTank) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,miTank.getMiTankStructureDetailId());
        values.put(AppConstant.MI_TANK_SURVEY_ID,miTank.getMiTankSurveyId());
        values.put(AppConstant.MI_TANK_STRUCTURE_ID,miTank.getMiTankStructureId());
        values.put(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,miTank.getMiTankStructureSerialId());
        values.put(AppConstant.MI_TANK_CONDITION_ID,miTank.getMiTankConditionId());
        values.put(AppConstant.MI_TANK_CONDITION_NAME,miTank.getMiTankConditionName());
        values.put(AppConstant.MI_TANK_SKILL_LEVEL,miTank.getMiTankSkillLevel());
        values.put(AppConstant.MI_TANK_STRUCTURE_NAME,miTank.getMiTankStructureName());

        long id = db.insert(DBHelper.STRUCTURES,null,values);
        Log.d("Insert_id_structures", String.valueOf(id));

        return miTank;
    }

    public ArrayList<MITank> getStructure(String mi_tank_survey_id,String mi_tank_structure_id) {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

        selection = "mi_tank_survey_id = ? and mi_tank_structure_id = ? ";
        selectionArgs = new String[]{mi_tank_survey_id,mi_tank_structure_id};



        try {
            cursor = db.query(DBHelper.STRUCTURES,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank  card = new MITank ();

                    card.setMiTankStructureDetailId(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID)));
                    card.setMiTankSurveyId(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_SURVEY_ID)));
                    card.setMiTankStructureId(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_ID)));
                    card.setMiTankStructureSerialId(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID)));
                    card.setMiTankConditionId(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_ID)));
                    card.setMiTankConditionName(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_NAME)));
                    card.setMiTankSkillLevel(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_SKILL_LEVEL)));
                    card.setMiTankStructureName(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public MITank insertTankCondition(MITank miTank) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.MI_TANK_CONDITION_ID, miTank.getMiTankConditionId());
        values.put(AppConstant.MI_TANK_CONDITION_NAME, miTank.getMiTankConditionName());

        long id = db.insert(DBHelper.MI_TANK_CONDITION,null,values);
        Log.d("Insert_id_CONDITION", String.valueOf(id));

        return miTank;
    }

    public ArrayList<MITank > getTankCondition() {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(DBHelper.MI_TANK_CONDITION,
                    new String[]{"*"}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank  card = new MITank ();
                    card.setMiTankConditionId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_ID)));
                    card.setMiTankConditionName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<MITank> selectImage(String dcode,String bcode, String pvcode,String habcode,String mi_tank_structure_detail_id) {
        db.isOpen();
        ArrayList<MITank> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

        selection = "dcode = ? and bcode = ? and pvcode = ? and habcode = ? and mi_tank_structure_detail_id = ? ";
        selectionArgs = new String[]{dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id};


        try {
            cursor = db.query(DBHelper.SAVE_MI_TANK_IMAGES,
                    new String[]{"*"}, selection,selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    MITank card = new MITank();

                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndex(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setMiTankStructureDetailId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID)));
                    card.setMiTankStructureId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_ID)));
                    card.setMiTankSurveyId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_SURVEY_ID)));
                    card.setMiTankConditionId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_ID)));
                    card.setLatitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    card.setLongitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));
                    card.setImage(decodedByte);

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<MITank> getSavedData(String dcode,String bcode) {
        db.isOpen();
        ArrayList<MITank> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

        selection = "dcode = ? and bcode = ? ";
        selectionArgs = new String[]{dcode,bcode};


        try {
            cursor = db.query(DBHelper.SAVE_MI_TANK_IMAGES,
                    new String[]{"*"}, selection,selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    MITank card = new MITank();

                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndex(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setMiTankStructureDetailId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID)));
                    card.setMiTankStructureId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_STRUCTURE_ID)));
                    card.setMiTankSurveyId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_SURVEY_ID)));
                    card.setMiTankConditionId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_ID)));
                    card.setLatitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    card.setLongitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));
                    card.setImage(decodedByte);

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public MITank saveLatLong(MITank saveLatLongValue) {
        ContentValues values = new ContentValues();
        String pointtype = saveLatLongValue.getPointType();
        values.put(AppConstant.DISTRICT_CODE, saveLatLongValue.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, saveLatLongValue.getBlockCode());
        values.put(AppConstant.PV_CODE, saveLatLongValue.getPvCode());
        values.put(AppConstant.HAB_CODE, saveLatLongValue.getHabCode());
        values.put(AppConstant.MI_TANK_SURVEY_ID, saveLatLongValue.getMiTankSurveyId());
        values.put(AppConstant.KEY_POINT_SERIAL_NO, saveLatLongValue.getPointSerialNo());
        values.put(AppConstant.KEY_POINT_TYPE, pointtype);
        values.put(AppConstant.KEY_LATITUDE, saveLatLongValue.getLatitude());
        values.put(AppConstant.KEY_LONGITUDE, saveLatLongValue.getLongitude());
        long id = db.insert(DBHelper.SAVE_TRACK_TABLE, null, values);
        if (id > 0) {
            if (pointtype.equalsIgnoreCase("1")) {
                Toasty.success(context, "Start Point Inserted", Toast.LENGTH_SHORT, true).show();
            } else if (pointtype.equalsIgnoreCase("2")) {
                Toasty.success(context, "Middle Point Inserted", Toast.LENGTH_SHORT, true).show();
            } else if (pointtype.equalsIgnoreCase("3")) {
                Toasty.success(context, "End Point Inserted", Toast.LENGTH_SHORT, true).show();
            }

        }
        Log.d("Inserted_id_saveLatLong", String.valueOf(id));

        return saveLatLongValue;
    }

    public ArrayList<MITank> getSavedTrack() {

        ArrayList<MITank> sendPostLatLong = new ArrayList<>();
        Cursor cursor = null;

        try {
            // cursor = db.rawQuery("select * from " + DBHelper.SAVE_LAT_LONG_TABLE, null);
            cursor = db.query(DBHelper.SAVE_TRACK_TABLE,
                    new String[]{"*"}, "server_flag = ?", new String[]{"0"}, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    MITank postLatLong = new MITank();

                    postLatLong.setDistictCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    postLatLong.setBlockCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    postLatLong.setPvCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    postLatLong.setHabCode(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    postLatLong.setMiTankSurveyId(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.MI_TANK_SURVEY_ID)));
                    postLatLong.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    postLatLong.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));
                    postLatLong.setPointType(cursor.getString(cursor.getColumnIndex(AppConstant.KEY_POINT_TYPE)));


                    sendPostLatLong.add(postLatLong);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sendPostLatLong;
    }

    public void update_Track() {
        String whereClause = "server_flag = server_flag";
        Log.d("Update id is ", "id");
        ContentValues values = new ContentValues();
        values.put("server_flag", 1);
        db.update(DBHelper.SAVE_TRACK_TABLE, values, whereClause, null);
    }

    public void deleteVillageTable() {
        db.execSQL("delete from " + DBHelper.VILLAGE_TABLE_NAME);
    }

    public void deleteTankStructure() {
        db.execSQL("delete from " + DBHelper.MI_TANK_STRUCTURE);
    }

    public void deleteMITankData() {
        db.execSQL("delete from " + DBHelper.MI_TANK_DATA);
    }

    public void deleteStructures() {
        db.execSQL("delete from " + DBHelper.STRUCTURES);
    }

    public void deleteMITankCondition() {
        db.execSQL("delete from " + DBHelper.MI_TANK_CONDITION);
    }


    public void deleteAll() {

        deleteVillageTable();
        deleteTankStructure();
        deleteMITankData();
        deleteStructures();
        deleteMITankCondition();
    }



}
