package com.nic.MITank.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.MITank.R;
import com.nic.MITank.adapter.CommonAdapter;
import com.nic.MITank.api.Api;
import com.nic.MITank.api.ApiService;
import com.nic.MITank.api.ServerResponse;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.HomeScreenBinding;
import com.nic.MITank.dialog.MyDialog;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.UrlGenerator;
import com.nic.MITank.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private HomeScreenBinding homeScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<MITank> Village = new ArrayList<>();
    private List<MITank> Habitation = new ArrayList<>();
    String lastInsertedID;
    JSONObject dataset = new JSONObject();
    JSONObject datasetTrack = new JSONObject();


    String pref_Village;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.home_screen);
        homeScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }
        villageFilterSpinner(prefManager.getBlockCode());
        homeScreenBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    pref_Village = Village.get(position).getPvName();
                    prefManager.setVillageListPvName(pref_Village);
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    habitationFilterSpinner(prefManager.getDistrictCode(), prefManager.getBlockCode(), prefManager.getPvCode());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        homeScreenBinding.habitationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    prefManager.setHabCode(Habitation.get(position).getHabCode());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        homeScreenBinding.all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    homeScreenBinding.miTanks.setChecked(false);
                    homeScreenBinding.ponds.setChecked(false);

                }
            }
        });
        homeScreenBinding.miTanks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    homeScreenBinding.ponds.setChecked(false);
                    homeScreenBinding.all.setChecked(false);
                }
            }
        });
        homeScreenBinding.ponds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    homeScreenBinding.all.setChecked(false);
                    homeScreenBinding.miTanks.setChecked(false);
                }

            }
        });
        if (Utils.isOnline() && !isHome.equalsIgnoreCase("Home")) {
            getTankPondList();
            getTankPondStructureList();
            getTankCondition();
        }
        syncButtonVisibility();
    }

    public void toUpload() {
//        if(Utils.isOnline()) {
//            new toUploadTankTask().execute();
//            new toUploadTankTrackDataTask().execute();
//        }
//        else {
//            Utils.showAlert(this,"Please Turn on Your Mobile Data to Upload");
//        }
        Intent intent = new Intent(this, PendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public class toUploadTankTask extends AsyncTask<Void, Void,
            JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            dbData.open();
            JSONArray track_data = new JSONArray();
            ArrayList<MITank> tanks = dbData.getSavedData(prefManager.getDistrictCode(),prefManager.getBlockCode());

            if (tanks.size() > 0) {
                for (int i = 0; i < tanks.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(AppConstant.PV_CODE,tanks.get(i).getPvCode());
                        jsonObject.put(AppConstant.HAB_CODE,tanks.get(i).getHabCode());
                        jsonObject.put(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,tanks.get(i).getMiTankStructureDetailId());
                        jsonObject.put(AppConstant.MI_TANK_STRUCTURE_ID,tanks.get(i).getMiTankStructureId());
                        jsonObject.put(AppConstant.MI_TANK_SURVEY_ID,tanks.get(i).getMiTankSurveyId());
                        jsonObject.put(AppConstant.MI_TANK_CONDITION_ID,tanks.get(i).getMiTankConditionId());
                        jsonObject.put(AppConstant.KEY_LATITUDE,tanks.get(i).getLatitude());
                        jsonObject.put(AppConstant.KEY_LONGITUDE,tanks.get(i).getLongitude());

                        Bitmap bitmap = tanks.get(i).getImage();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                        byte[] imageInByte = baos.toByteArray();
                        String image_str = Base64.encodeToString(imageInByte, Base64.DEFAULT);

                        jsonObject.put(AppConstant.KEY_IMAGE,image_str);

                        track_data.put(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                dataset = new JSONObject();

                try {
                    dataset.put(AppConstant.KEY_SERVICE_ID,"mi_tank_detail_save");
                    dataset.put(AppConstant.KEY_TRACK_DATA,track_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return dataset;
        }

        @Override
        protected void onPostExecute(JSONObject dataset) {
            super.onPostExecute(dataset);
           syncData();
        }
    }

    public class toUploadTankTrackDataTask extends AsyncTask<Void, Void,
            JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                dbData.open();
                ArrayList<MITank> saveLatLongLists = dbData.getSavedTrack();
                JSONArray saveLatLongArray = new JSONArray();
                if (saveLatLongLists.size() > 0) {
                    for (int i = 0; i < saveLatLongLists.size(); i++) {
                        JSONObject latLongData = new JSONObject();
                        latLongData.put(AppConstant.PV_CODE, saveLatLongLists.get(i).getPvCode());
                        latLongData.put(AppConstant.HAB_CODE, saveLatLongLists.get(i).getHabCode());
                        latLongData.put(AppConstant.KEY_POINT_TYPE, saveLatLongLists.get(i).getPointType());
                        latLongData.put(AppConstant.KEY_POINT_SERIAL_NO, String.valueOf(saveLatLongLists.get(i).getPointSerialNo()));
                        latLongData.put(AppConstant.KEY_LATITUDE, saveLatLongLists.get(i).getLatitude());
                        latLongData.put(AppConstant.KEY_LONGITUDE, saveLatLongLists.get(i).getLongitude());
                        latLongData.put(AppConstant.MI_TANK_SURVEY_ID, saveLatLongLists.get(i).getMiTankSurveyId());

                        saveLatLongArray.put(latLongData);
                    }
                }

                datasetTrack = new JSONObject();
                try {
                    datasetTrack.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_TANK_TRACK_SAVE);
                    datasetTrack.put(AppConstant.KEY_TRACK_DATA, saveLatLongArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return datasetTrack;
        }

        protected void onPostExecute(JSONObject dataset) {
            super.onPostExecute(dataset);
            syncDataTrack();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    public void villageFilterSpinner(String filterVillage) {
        Cursor VillageList = null;
        VillageList = db.rawQuery("SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " where dcode = " + prefManager.getDistrictCode() + " and bcode = '" + filterVillage + "'", null);

        Village.clear();
        MITank villageListValue = new MITank();
        villageListValue.setPvName("Select Village");
        Village.add(villageListValue);
        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    MITank villageList = new MITank();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));

                    villageList.setDistictCode(districtCode);
                    villageList.setBlockCode(blockCode);
                    villageList.setPvCode(pvCode);
                    villageList.setPvName(pvname);

                    Village.add(villageList);
                    Log.d("spinnersize", "" + Village.size());
                } while (VillageList.moveToNext());
            }
        }
        homeScreenBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "VillageList"));
    }


    public void habitationFilterSpinner(String dcode, String bcode, String pvcode) {
        Cursor HABList = null;
        HABList = db.rawQuery("SELECT * FROM " + DBHelper.HABITATION_TABLE_NAME + " where dcode = '" + dcode + "'and bcode = '" + bcode + "' and pvcode = '" + pvcode + "' order by habitation_name asc", null);

        Habitation.clear();
        MITank habitationListValue = new MITank();
        habitationListValue.setHabitationName("Select Habitation");
        Habitation.add(habitationListValue);
        if (HABList.getCount() > 0) {
            if (HABList.moveToFirst()) {
                do {
                    MITank habList = new MITank();
                    String districtCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String habCode = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABB_CODE));
                    String habName = HABList.getString(HABList.getColumnIndexOrThrow(AppConstant.HABITATION_NAME));

                    habList.setDistictCode(districtCode);
                    habList.setBlockCode(blockCode);
                    habList.setPvCode(pvCode);
                    habList.setHabCode(habCode);
                    habList.setHabitationName(habName);

                    Habitation.add(habList);
                    Log.d("spinnersize", "" + Habitation.size());
                } while (HABList.moveToNext());
            }
        }
        homeScreenBinding.habitationSpinner.setAdapter(new CommonAdapter(this, Habitation, "HabitationList"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void logout() {
        dbData.open();
        ArrayList<MITank> TankImageCount = dbData.getSavedData(prefManager.getDistrictCode(),prefManager.getBlockCode());
        ArrayList<MITank> trackCount = dbData.getSavedTrack();


        if (!Utils.isOnline()) {
            Utils.showAlert(this, "Logging out while offline may leads to loss of data!");
        } else {
            if (!(TankImageCount.size() > 0 || trackCount.size() > 0)) {
                closeApplication();
            } else {
                Utils.showAlert(this, "Sync all the data before logout!");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }


    public void getTankPondList() {
        try {
            new ApiService(this).makeJSONObjectRequest("TankPondList", Api.Method.POST, UrlGenerator.getTankPondListUrl(), tankPondListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTankPondStructureList() {
        try {
            new ApiService(this).makeJSONObjectRequest("TankPondStructure", Api.Method.POST, UrlGenerator.getTankPondListUrl(), tankPondStructureListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTankCondition() {
        try {
            new ApiService(this).makeJSONObjectRequest("TankCondition", Api.Method.POST, UrlGenerator.getTankPondListUrl(), tankConditionJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject tankPondListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.tankPondListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("tankPondList", "" + authKey);
        return dataSet;
    }

    public JSONObject tankPondStructureListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.tankPondStructureListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("tankPondStructure", "" + authKey);
        return dataSet;
    }

    public JSONObject tankConditionJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.tankConditionListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("tankCondition", "" + authKey);
        return dataSet;
    }

    public void syncData() {
        try {
            new ApiService(this).makeJSONObjectRequest("saveTankData", Api.Method.POST, UrlGenerator.getTankPondListUrl(), saveTankListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject saveTankListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), dataset.toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("savebridgesList", "" + authKey);
        return dataSet;
    }

    public void syncDataTrack() {
        try {
            new ApiService(this).makeJSONObjectRequest("saveTrackDataList", Api.Method.POST, UrlGenerator.getTankPondListUrl(), saveTrackDataListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject saveTrackDataListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), datasetTrack.toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("saveTrackDataList", "" + authKey);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("TankPondList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    insertMiTankData(jsonObject.getJSONArray(AppConstant.JSON_DATA));

                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")) {
//                    Utils.showAlert(this, "No Record Found !");
                }
                Log.d("TankPondList", "" + responseDecryptedBlockKey);

            }

            if ("TankPondStructure".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertTankStructureTask().execute(jsonObject);

                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")) {
//                    Utils.showAlert(this, "No Record Found !");
                }
                Log.d("TankPondStructure", "" + responseDecryptedBlockKey);
            }

            if ("TankCondition".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertTankConditionTask().execute(jsonObject);


                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")) {
//                    Utils.showAlert(this, "No Record Found !");
                }
                Log.d("TankCondition", "" + responseDecryptedBlockKey);
            }
            if ("saveTankData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    dbData.open();
                    dbData.deleteMITankData();
                    dbData.deleteMITankImages();
                    dbData.deleteStructures();
                    dataset = new JSONObject();
                    getTankPondList();
                    Utils.showAlert(this,"Saved");
                    syncButtonVisibility();
                }
                Log.d("saved_TankData", "" + responseDecryptedBlockKey);
            }
            if ("saveTrackDataList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    dbData.open();
                    dbData.update_Track();
                    datasetTrack = new JSONObject();
                    Utils.showAlert(this, "Tracked Data Saved");
                    syncButtonVisibility();
                }
                Log.d("saved_TrackDataList", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {
        volleyError.printStackTrace();
    }


    public void closeApplication() {
        new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }


    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<MITank> TankImageCount = dbData.getSavedData(prefManager.getDistrictCode(),prefManager.getBlockCode());
        ArrayList<MITank> trackCount = dbData.getSavedTrack();

        if (TankImageCount.size() > 0 || trackCount.size() > 0) {
            homeScreenBinding.synData.setVisibility(View.VISIBLE);
        }else {
            homeScreenBinding.synData.setVisibility(View.GONE);
        }
    }


    public class InsertTankStructureTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.deleteTankStructure();
            dbData.open();
            ArrayList<MITank> all_pmayListCount = dbData.getAllTankStructure();
            if (all_pmayListCount.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MITank tankStructure = new MITank();
                        try {
                            tankStructure.setMiTankStructureId(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_STRUCTURE_ID));
                            tankStructure.setMiTankStructureName(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_STRUCTURE_NAME));

                            dbData.insertTankStructure(tankStructure);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }

    public class InsertTankConditionTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.deleteMITankCondition();
            dbData.open();
            ArrayList<MITank> condition_Count = dbData.getTankCondition();
            if (condition_Count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MITank tankCondition = new MITank();
                        try {
                            tankCondition.setMiTankConditionId(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_CONDITION_ID));
                            tankCondition.setMiTankConditionName(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_CONDITION_NAME));

                            dbData.insertTankCondition(tankCondition);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }

    public void insertMiTankData(JSONArray tankdataArray) {

        dbData.open();
        if (Utils.isOnline()) {
            dbData.deleteMITankData();
            dbData.deleteStructures();
        }
        dbData.open();
        ArrayList<MITank> tankData_count = dbData.getAllMITankData("insert","","","","","");
        if(tankData_count.size() <= 0) {
            for (int i = 0; i < tankdataArray.length(); i++) {
                MITank miTankValue = new MITank();
                try {


                    miTankValue.setDistictCode(tankdataArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                    miTankValue.setBlockCode(tankdataArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                    miTankValue.setPvCode(tankdataArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                    miTankValue.setHabCode(tankdataArray.getJSONObject(i).getString(AppConstant.HAB_CODE));
                    miTankValue.setMiTankSurveyId(tankdataArray.getJSONObject(i).getString(AppConstant.MI_TANK_SURVEY_ID));
                    miTankValue.setMinorIrrigationType(tankdataArray.getJSONObject(i).getString(AppConstant.MINOR_IRRIGATION_TYPE));
                    miTankValue.setNameOftheMITank(tankdataArray.getJSONObject(i).getString(AppConstant.NAME_OF_THE_MI_TANK));
                    miTankValue.setLocalName(tankdataArray.getJSONObject(i).getString(AppConstant.LOCAL_NAME));
                    miTankValue.setArea(tankdataArray.getJSONObject(i).getString(AppConstant.AREA));

                    dbData.insertMITankData(miTankValue);

                    JSONArray structureArray = tankdataArray.getJSONObject(i).getJSONArray(AppConstant.STRUCTURES);
                    new InsertStructureTask().execute(structureArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class  InsertStructureTask extends AsyncTask<JSONArray ,Void ,Void> {

        @Override
        protected Void doInBackground(JSONArray... params) {
            dbData.open();
            if (params.length > 0) {
                JSONArray jsonArray = params[0];
                for (int i = 0; i < jsonArray.length(); i++) {
                    MITank sturctureValue = new MITank();
                    try {
                        sturctureValue.setMiTankStructureDetailId(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID));
                        sturctureValue.setMiTankSurveyId(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_SURVEY_ID));
                        sturctureValue.setMiTankStructureId(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_STRUCTURE_ID));
                        sturctureValue.setMiTankStructureSerialId(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID));
                        sturctureValue.setMiTankConditionId(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_CONDITION_ID));
                        sturctureValue.setMiTankConditionName(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_CONDITION_NAME));
                        sturctureValue.setMiTankSkillLevel(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_SKILL_LEVEL));
                        sturctureValue.setMiTankStructureName(jsonArray.getJSONObject(i).getString(AppConstant.MI_TANK_STRUCTURE_NAME));
                        sturctureValue.setImageAvailable(jsonArray.getJSONObject(i).getString(AppConstant.IMAGE_AVAILABLE));
                        dbData.insertStructure(sturctureValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            return null;
        }
    }

    public void checkFields() {
        if (!"Select Village".equalsIgnoreCase(Village.get(homeScreenBinding.villageSpinner.getSelectedItemPosition()).getPvName())) {
            if (!"Select Habitation".equalsIgnoreCase(Habitation.get(homeScreenBinding.habitationSpinner.getSelectedItemPosition()).getHabitationName())) {
                if((homeScreenBinding.all.isChecked()) || (homeScreenBinding.miTanks.isChecked()) || (homeScreenBinding.ponds.isChecked())){
                    tanksPondsTitleScreen();
                }
                else{
                    Utils.showAlert(this, "Select MI Tanks/Ponds");
                }
            } else {
                Utils.showAlert(this, "Select Habitation!");
            }
        } else {
            Utils.showAlert(this, "Select Village!");
        }
    }

    public void tanksPondsTitleScreen() {
        String checkboxvalue = "";
        String selectedTitle = "";
        if(homeScreenBinding.all.isChecked()){
            checkboxvalue = "all";
            selectedTitle = "ALL";
        }
        else if(homeScreenBinding.miTanks.isChecked()){
            checkboxvalue = "1";
            selectedTitle = "MI TANKS";
        }
        else if(homeScreenBinding.ponds.isChecked()){
            checkboxvalue = "2";
            selectedTitle = "PONDS";
        }
        prefManager.setSchemeName(selectedTitle);
        Intent intent = new Intent(this, TanksPondsListScreen.class);
        intent.putExtra("Title",selectedTitle);
        intent.putExtra(AppConstant.CHECK_BOX_CLICKED,checkboxvalue);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
