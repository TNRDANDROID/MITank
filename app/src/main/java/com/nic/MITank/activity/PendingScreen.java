package com.nic.MITank.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.MITank.R;
import com.nic.MITank.adapter.NewPendingScreenAdapter;
import com.nic.MITank.adapter.PendingAdapter;
import com.nic.MITank.api.Api;
import com.nic.MITank.api.ApiService;
import com.nic.MITank.api.ServerResponse;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.PendingScreenBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.UrlGenerator;
import com.nic.MITank.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PendingScreen extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    public PendingScreenBinding pendingScreenBinding;
    private RecyclerView recyclerView;
    private PendingAdapter pendingAdapter;
    private NewPendingScreenAdapter newPendingAdapter;
    private PrefManager prefManager;
    public com.nic.MITank.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private Activity context;
    ArrayList<MITank> pendingLists = new ArrayList<>();
    public HomePage homePage = new HomePage();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pendingScreenBinding = DataBindingUtil.setContentView(this, R.layout.pending_screen);
        pendingScreenBinding.setActivity(this);
        context = this;
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = pendingScreenBinding.pendingList;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        pendingScreenBinding.centerDataRl.setOnClickListener(this);
        pendingScreenBinding.structureDataRl.setOnClickListener(this);
        pendingScreenBinding.trackDataRl.setOnClickListener(this);
        //new fetchPendingtask().execute();
   //     new fetchStructureDataTask().execute();
        new fetchCenterImagesDataTask().execute();
        pendingScreenBinding.v1.setVisibility(View.VISIBLE);
        pendingScreenBinding.v2.setVisibility(View.GONE);
        pendingScreenBinding.v3.setVisibility(View.GONE);
     //   new fetchTrackDataTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.center_data_rl:
                new fetchCenterImagesDataTask().execute();
                pendingScreenBinding.v1.setVisibility(View.VISIBLE);
                pendingScreenBinding.v2.setVisibility(View.GONE);
                pendingScreenBinding.v3.setVisibility(View.GONE);
                break;
            case R.id.structure_data_rl:
                new fetchStructureDataTask().execute();
                pendingScreenBinding.v1.setVisibility(View.GONE);
                pendingScreenBinding.v2.setVisibility(View.VISIBLE);
                pendingScreenBinding.v3.setVisibility(View.GONE);
                break;
            case R.id.track_data_rl:
                new fetchTrackDataTask().execute();
                pendingScreenBinding.v1.setVisibility(View.GONE);
                pendingScreenBinding.v2.setVisibility(View.GONE);
                pendingScreenBinding.v3.setVisibility(View.VISIBLE);
                break;

        }
    }


    public class fetchPendingtask extends AsyncTask<Void, Void,
            ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            pendingLists = new ArrayList<>();
            pendingLists = dbData.getPendingList();
            Log.d("pending_count", String.valueOf(pendingLists.size()));
            return pendingLists;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> pendingLists) {
            super.onPostExecute(pendingLists);
            recyclerView.setVisibility(View.VISIBLE);
            pendingAdapter = new PendingAdapter(PendingScreen.this, pendingLists,dbData);
            recyclerView.setAdapter(pendingAdapter);
        }
    }

    public JSONObject syncStructureData(JSONObject saveStructureDataSet) {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), saveStructureDataSet.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("saveTankData", Api.Method.POST, UrlGenerator.getTankPondListUrl(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("saveTankData", "" + authKey);
        return dataSet;
    }
    public JSONObject syncTrackData(JSONObject saveTrackDataSet) {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), saveTrackDataSet.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("saveTrackDataList", Api.Method.POST, UrlGenerator.getTankPondListUrl(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("saveTrackDataList", "" + authKey);
        return dataSet;
    }
    public JSONObject syncCentreImageData(JSONObject saveCentreImageDataSet) {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), saveCentreImageDataSet.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("saveCentreImageDataList", Api.Method.POST, UrlGenerator.getTankPondListUrl(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("saveCentreImageDataList", "" + authKey);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("saveTrackDataList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this, "Your track data is synchronized to the server!");
                   db.delete(DBHelper.SAVE_TRACK_TABLE, "mi_tank_survey_id = ?", new String[]{prefManager.getKeyDeleteId()});
                    /*new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();*/
                    new fetchTrackDataTask().execute();
                    newPendingAdapter.notifyDataSetChanged();
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")) {
                    Toasty.error(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
//                    db.delete(DBHelper.SAVE_PMAY_DETAILS, "id = ?", new String[]{prefManager.getKeyDeleteId()});
//                    db.delete(DBHelper.SAVE_PMAY_IMAGES, "pmay_id = ? ", new String[]{prefManager.getKeyDeleteId()});
                    /*new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();*/
                    new fetchTrackDataTask().execute();
                    newPendingAdapter.notifyDataSetChanged();
                }
                Log.d("saved_response", "" + responseDecryptedBlockKey);
            }
            if ("saveTankData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    db.delete(DBHelper.SAVE_MI_TANK_IMAGES, "mi_tank_survey_id = ?", new String[]{prefManager.getKeyDeleteId()});
                    /*new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();*/
                    new fetchStructureDataTask().execute();
                    newPendingAdapter.notifyDataSetChanged();
                    HomePage.getInstance().getTankPondList();
                    Utils.showAlert(this,"Your Stucture data is syncronized to the server!");

                }
                Log.d("saved_TankData", "" + responseDecryptedBlockKey);
            }
            if ("saveCentreImageDataList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    db.delete(DBHelper.SAVE_MI_TANK_CENTER_IMAGES, "mi_tank_survey_id = ?", new String[]{prefManager.getKeyDeleteId()});
                    /*new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();*/
                    new fetchCenterImagesDataTask().execute();
                    newPendingAdapter.notifyDataSetChanged();
                    HomePage.getInstance().getTankPondList();
                    Utils.showAlert(this,"Your CentreImage data is synchronized to the server!");

                }
                Log.d("saved_TankData", "" + responseDecryptedBlockKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public class fetchCenterImagesDataTask extends AsyncTask<Void, Void,
            ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            pendingLists = new ArrayList<>();
            pendingLists = dbData.getAllCenterImageData(prefManager.getDistrictCode(),prefManager.getBlockCode());
            Log.d("center_img_count", String.valueOf(pendingLists.size()));
            return pendingLists;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> pendingLists) {
            super.onPostExecute(pendingLists);
            if(pendingLists.size()>0) {
                ArrayList<MITank> centerImageDataList = new ArrayList<>();
                for (int i=0;i<pendingLists.size();i++){
                    for (int j=i+1;j<pendingLists.size();j++){
                        if(pendingLists.get(i).getMiTankSurveyId().equals(pendingLists.get(j).getMiTankSurveyId())){
                            pendingLists.remove(j);
                            j--;
                        }
                    }
                }
                recyclerView.setVisibility(View.VISIBLE);
                pendingScreenBinding.noDataFoundLayout.setVisibility(View.GONE);
                newPendingAdapter = new NewPendingScreenAdapter(PendingScreen.this, pendingLists, dbData, "CentreImage");
                recyclerView.setAdapter(newPendingAdapter);
            }
            else {
                recyclerView.setVisibility(View.GONE);
                pendingScreenBinding.noDataFoundLayout.setVisibility(View.VISIBLE);
            }
        }
    }
    public class fetchStructureDataTask extends AsyncTask<Void, Void,
            ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            pendingLists = new ArrayList<>();
            pendingLists = dbData.getPendingList();
            Log.d("structure_data_count", String.valueOf(pendingLists.size()));
            return pendingLists;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> pendingLists) {
            super.onPostExecute(pendingLists);
            ArrayList<MITank> structureArrayList=new ArrayList<>();
            if(pendingLists.size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    pendingScreenBinding.noDataFoundLayout.setVisibility(View.GONE);
                    newPendingAdapter = new NewPendingScreenAdapter(PendingScreen.this, pendingLists, dbData, "StructureData");
                    recyclerView.setAdapter(newPendingAdapter);

            }
            else {
                recyclerView.setVisibility(View.GONE);
                pendingScreenBinding.noDataFoundLayout.setVisibility(View.VISIBLE);
            }
        }
    }
    public class fetchTrackDataTask extends AsyncTask<Void, Void,
            ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            pendingLists = new ArrayList<>();
            pendingLists = dbData.getAllSavedTrack(prefManager.getDistrictCode(),prefManager.getBlockCode());
            Log.d("track_data_count", String.valueOf(pendingLists.size()));
            return pendingLists;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> pendingLists) {
            if(pendingLists.size()>0) {
                super.onPostExecute(pendingLists);
                ArrayList<MITank> trackDataList = new ArrayList<>();
                for (int i=0;i<pendingLists.size();i++){
                    for (int j=i+1;j<pendingLists.size();j++){
                        if(pendingLists.get(i).getMiTankSurveyId().equals(pendingLists.get(j).getMiTankSurveyId())){
                                pendingLists.remove(j);
                                j--;
                        }
                    }
                }
                recyclerView.setVisibility(View.VISIBLE);
                pendingScreenBinding.noDataFoundLayout.setVisibility(View.GONE);
                newPendingAdapter = new NewPendingScreenAdapter(PendingScreen.this, pendingLists, dbData, "TrackData");
                recyclerView.setAdapter(newPendingAdapter);
            }
            else {
                recyclerView.setVisibility(View.GONE);
                pendingScreenBinding.noDataFoundLayout.setVisibility(View.VISIBLE);
            }
        }
    }

}
