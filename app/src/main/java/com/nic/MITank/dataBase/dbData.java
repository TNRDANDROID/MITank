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


import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.model.MITank;

import java.util.ArrayList;


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

    public ArrayList<MITank > getAllMITankData() {

        ArrayList<MITank > cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(DBHelper.MI_TANK_DATA,
                    new String[]{"*"}, null, null, null, null, null);
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

    public ArrayList<MITank> getSavedPMAYDetails() {

        ArrayList<MITank> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;


        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_DETAILS,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    MITank card = new MITank();


                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow("id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));


                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public ArrayList<MITank> getSavedPMAYImages(String pmay_id, String type_of_photo) {

        ArrayList<MITank> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

        if(!type_of_photo.isEmpty()){
            selection = "pmay_id = ? and type_of_photo = ? ";
            selectionArgs = new String[]{pmay_id,type_of_photo};
        }
        else if(type_of_photo.isEmpty()) {
            selection = "pmay_id = ? ";
            selectionArgs = new String[]{pmay_id};
        }


        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_IMAGES,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    MITank card = new MITank();


                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PMAY_ID)));
                    card.setTypeOfPhoto(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO)));
                    card.setLatitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    card.setLongitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));

                    card.setImage(decodedByte);

                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
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


    public void deleteAll() {

        deleteVillageTable();
        deleteTankStructure();
    }



}
