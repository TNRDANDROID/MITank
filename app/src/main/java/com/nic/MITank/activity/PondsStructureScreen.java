package com.nic.MITank.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.MITank.R;
import com.nic.MITank.adapter.PondsStructureAdapter;
import com.nic.MITank.adapter.TanksPondsListAdapter;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.PondsStructureScreenBinding;
import com.nic.MITank.databinding.TanksPondsListScreenBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;

import java.util.ArrayList;

public class PondsStructureScreen extends AppCompatActivity {

    public PondsStructureScreenBinding pondsStructureScreenBinding;
    boolean ExpandedActionBar = true;
    private PondsStructureAdapter pondsStructureAdapter;
    private ArrayList<MITank> tankStructureList;
    public dbData dbData = new dbData(this);
    private PrefManager prefManager;
    String miTankStructureId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pondsStructureScreenBinding = DataBindingUtil.setContentView(this, R.layout.ponds_structure_screen);
        pondsStructureScreenBinding.setActivity(this);
        pondsStructureScreenBinding.toolbarTitle.setText(getIntent().getStringExtra("Title"));
        setSupportActionBar(pondsStructureScreenBinding.toolbar);
        prefManager = new PrefManager(this);

        tankStructureList = new ArrayList<>();
        pondsStructureAdapter = new PondsStructureAdapter(this,tankStructureList,dbData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        pondsStructureScreenBinding.recyclerView.setLayoutManager(mLayoutManager);
        pondsStructureScreenBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        pondsStructureScreenBinding.recyclerView.setAdapter(pondsStructureAdapter);
        new fetchStructuretask().execute();
    }

    public class fetchStructuretask extends AsyncTask<Void, Void,
                ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            tankStructureList = new ArrayList<>();
            tankStructureList = dbData.getStructure(prefManager.getMiTankSurveyId(),getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_ID));
            Log.d("TANKS_STRUCT_COUNT", String.valueOf(tankStructureList.size()));
            return tankStructureList;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> structureList) {
            super.onPostExecute(structureList);
            pondsStructureAdapter = new PondsStructureAdapter(PondsStructureScreen.this,tankStructureList,dbData);
            pondsStructureScreenBinding.recyclerView.setAdapter(pondsStructureAdapter);
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
