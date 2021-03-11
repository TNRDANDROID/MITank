package com.nic.MITank.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.appbar.AppBarLayout;
import com.nic.MITank.R;
import com.nic.MITank.adapter.TanksPondsListAdapter;
import com.nic.MITank.adapter.TanksPondsTitleAdapter;
import com.nic.MITank.api.Api;
import com.nic.MITank.api.ApiService;
import com.nic.MITank.api.ServerResponse;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.databinding.TanksPondsListScreenBinding;
import com.nic.MITank.databinding.TanksPondsTitleScreenBinding;
import com.nic.MITank.model.MITank;

import java.util.ArrayList;
import java.util.List;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.UrlGenerator;
import com.nic.MITank.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class TanksPondsListScreen extends AppCompatActivity implements Api.ServerResponseListener {

    public TanksPondsListScreenBinding tanksPondsListScreenBinding;
    boolean ExpandedActionBar = true;
    private TanksPondsListAdapter tanksPondsListAdapter;
    private ArrayList<MITank> tankDataList;
    public dbData dbData = new dbData(this);
    private PrefManager prefManager;
    String checkboxvalue;
    int centerImagePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tanksPondsListScreenBinding = DataBindingUtil.setContentView(this, R.layout.tanks_ponds_list_screen);
        tanksPondsListScreenBinding.setActivity(this);
        tanksPondsListScreenBinding.toolbarTitle.setText(getIntent().getStringExtra("Title"));
        setSupportActionBar(tanksPondsListScreenBinding.toolbar);
        prefManager = new PrefManager(this);

        tankDataList = new ArrayList<>();
        tanksPondsListAdapter = new TanksPondsListAdapter(this,tankDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        tanksPondsListScreenBinding.recyclerView.setLayoutManager(mLayoutManager);
        tanksPondsListScreenBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        tanksPondsListScreenBinding.recyclerView.setAdapter(tanksPondsListAdapter);
        checkboxvalue = getIntent().getStringExtra(AppConstant.CHECK_BOX_CLICKED);
        new fetchtankDatatask().execute();
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("GetCenterImage".equals(urlType) && responseObj != null) {


            }

        }catch (JSONException e){

        }

            }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class fetchtankDatatask extends AsyncTask<Void, Void,
                ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            tankDataList = new ArrayList<>();
            tankDataList = dbData.getAllMITankData("fetch",prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),prefManager.getHabCode(),checkboxvalue);
            Log.d("TANKS_DATA_COUNT", String.valueOf(tankDataList.size()));
            return tankDataList;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> tanksDataList) {
            super.onPostExecute(tanksDataList);
            if(tanksDataList.size() > 0){
                tanksPondsListAdapter = new TanksPondsListAdapter(TanksPondsListScreen.this,tankDataList);
                tanksPondsListScreenBinding.recyclerView.setAdapter(tanksPondsListAdapter);
            }
            else {
                 tanksPondsListScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }

        }
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
    public void openCamera(int position) {
        Intent intent = new Intent(this, CameraScreen.class);
        intent.putExtra("KEY","TanksPondsListAdapter");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,"");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,tankDataList.get(position).getMiTankStructureDetailId());
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,tankDataList.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,tankDataList.get(position).getMiTankStructureId());
        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            new fetchtankDatatask().execute();
        }  else {
                // failed to record video
            }
        }

    public void getCenterImage(int ImagePosition) {
        centerImagePosition=ImagePosition;
        try {
            new ApiService(this).makeJSONObjectRequest("GetCenterImage", Api.Method.POST, UrlGenerator.getTankPondListUrl(), encryptImageJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject encryptImageJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), getCenterImageJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("tankInletStructureType", "" + authKey);
        return dataSet;
    }

    public  JSONObject getCenterImageJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "m_tank_center_image");
        dataSet.put("mi_tank_survey_id", tankDataList.get(centerImagePosition).getMiTankSurveyId());
        dataSet.put("pvcode", tankDataList.get(centerImagePosition).getPvCode());
        dataSet.put("habcode", tankDataList.get(centerImagePosition).getHabCode());
        Log.d("object", "" + dataSet);
        return dataSet;
    }



}
