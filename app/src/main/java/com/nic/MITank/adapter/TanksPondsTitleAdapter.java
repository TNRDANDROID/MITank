package com.nic.MITank.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.nic.MITank.R;
import com.nic.MITank.activity.PondsStructureScreen;
import com.nic.MITank.activity.TanksPondsListScreen;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.databinding.TanksPondsTitleAdapterBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

 

public class TanksPondsTitleAdapter extends RecyclerView.Adapter<TanksPondsTitleAdapter.MyViewHolder> {

    private Context context;
    private PrefManager prefManager;
    private ArrayList<MITank> miTankList;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;

    public TanksPondsTitleAdapter(Context context, ArrayList<MITank> miTankList) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.miTankList = miTankList;
    }

    @Override
    public TanksPondsTitleAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
         TanksPondsTitleAdapterBinding tanksPondsTitleAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.tanks_ponds_title_adapter, viewGroup, false);
        return new TanksPondsTitleAdapter.MyViewHolder(tanksPondsTitleAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TanksPondsTitleAdapterBinding tanksPondsTitleAdapterBinding;

        public MyViewHolder(TanksPondsTitleAdapterBinding Binding) {
            super(Binding.getRoot());
            tanksPondsTitleAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
       holder.tanksPondsTitleAdapterBinding.tvtitle.setText(miTankList.get(position).getMiTankStructureName());


        holder.tanksPondsTitleAdapterBinding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanksPondsListScreen(position);
            }
        });

    }

    public void tanksPondsListScreen(int position) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, PondsStructureScreen.class);
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,miTankList.get(position).getMiTankStructureId());
        intent.putExtra("Title",miTankList.get(position).getMiTankStructureName());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return miTankList.size();
    }


}
