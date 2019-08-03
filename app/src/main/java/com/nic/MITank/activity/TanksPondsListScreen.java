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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.nic.MITank.R;
import com.nic.MITank.adapter.TanksPondsListAdapter;
import com.nic.MITank.adapter.TanksPondsTitleAdapter;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.databinding.TanksPondsListScreenBinding;
import com.nic.MITank.databinding.TanksPondsTitleScreenBinding;
import com.nic.MITank.model.MITank;

import java.util.ArrayList;
import java.util.List;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.Utils;

public class TanksPondsListScreen extends AppCompatActivity {

    public TanksPondsListScreenBinding tanksPondsListScreenBinding;
    boolean ExpandedActionBar = true;
    private TanksPondsListAdapter tanksPondsListAdapter;
    private ArrayList<MITank> tankDataList;
    public dbData dbData = new dbData(this);
    private PrefManager prefManager;
    String checkboxvalue;

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

}
