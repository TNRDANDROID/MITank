package com.nic.MITank.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.MITank.R;
import com.nic.MITank.activity.PondsStructureScreen;
import com.nic.MITank.activity.TanksPondsTitleScreen;
import com.nic.MITank.activity.TrackingScreen;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.databinding.TanksPondsListAdapterBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TanksPondsListAdapter extends RecyclerView.Adapter<TanksPondsListAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<MITank> miTankData;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;

    public TanksPondsListAdapter(Activity context, ArrayList<MITank> miTankList) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.miTankData = miTankList;
    }

    @Override
    public TanksPondsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        TanksPondsListAdapterBinding tanksPondsListAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.tanks_ponds_list_adapter, viewGroup, false);
        return new TanksPondsListAdapter.MyViewHolder(tanksPondsListAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TanksPondsListAdapterBinding tanksPondsListAdapterBinding;

        public MyViewHolder(TanksPondsListAdapterBinding Binding) {
            super(Binding.getRoot());
            tanksPondsListAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
       holder.tanksPondsListAdapterBinding.nameOfTank.setText(miTankData.get(position).getNameOftheMITank());
       holder.tanksPondsListAdapterBinding.localName.setText(miTankData.get(position).getLocalName());
       holder.tanksPondsListAdapterBinding.area.setText(miTankData.get(position).getArea()+" hectare");

        holder.tanksPondsListAdapterBinding.viewStructure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStructure(position);
            }
        });

        holder.tanksPondsListAdapterBinding.trackData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackDataScreen(position);
            }
        });


    }

    public void trackDataScreen(int position){
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, TrackingScreen.class);
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,miTankData.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.NAME_OF_THE_MI_TANK,miTankData.get(position).getNameOftheMITank());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void selectStructure(int position) {
        prefManager.setMiTankSurveyId(miTankData.get(position).getMiTankSurveyId());
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, TanksPondsTitleScreen.class);
        intent.putExtra("Title",miTankData.get(position).getNameOftheMITank());
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,miTankData.get(position).getMiTankSurveyId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return miTankData.size();
    }


}
