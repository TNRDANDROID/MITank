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
import com.nic.MITank.activity.CameraScreen;
import com.nic.MITank.activity.FullImageActivity;
import com.nic.MITank.activity.TanksPondsListScreen;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.PondsStructureAdapterBinding;
import com.nic.MITank.databinding.TanksPondsListAdapterBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;

import org.json.JSONObject;

import java.util.ArrayList;


public class PondsStructureAdapter extends RecyclerView.Adapter<PondsStructureAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<MITank> Structure;
    static JSONObject dataset = new JSONObject();
    private dbData dbData;

    private LayoutInflater layoutInflater;

    public PondsStructureAdapter(Activity context, ArrayList<MITank> tankStructure,dbData dbData) {

        this.context = context;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
        this.Structure = tankStructure;
    }

    @Override
    public PondsStructureAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PondsStructureAdapterBinding pondsStructureAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.ponds_structure_adapter, viewGroup, false);
        return new PondsStructureAdapter.MyViewHolder(pondsStructureAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PondsStructureAdapterBinding tanksPondsListAdapterBinding;

        public MyViewHolder(PondsStructureAdapterBinding Binding) {
            super(Binding.getRoot());
            tanksPondsListAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
       holder.tanksPondsListAdapterBinding.structureName.setText(Structure.get(position).getMiTankStructureName()+" "+Structure.get(position).getMiTankStructureSerialId());
       holder.tanksPondsListAdapterBinding.condition.setText(Structure.get(position).getMiTankConditionName());

        String dcode = prefManager.getDistrictCode();
        String bcode = prefManager.getBlockCode();
        String pvcode = prefManager.getPvCode();
        String habcode = prefManager.getHabCode();
        String mi_tank_structure_detail_id = Structure.get(position).getMiTankStructureDetailId();


        ArrayList<MITank> imageOffline = dbData.selectImage(dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id);

        if(imageOffline.size() < 1) {
            holder.tanksPondsListAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
        }
        else {
            holder.tanksPondsListAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
        }

       holder.tanksPondsListAdapterBinding.cameraActivity.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openCamera(position);
           }
       });

        holder.tanksPondsListAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOfflineImages(position);
            }
        });

    }

    public void openCamera(int position) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, CameraScreen.class);
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,Structure.get(position).getMiTankStructureDetailId());
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,Structure.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,Structure.get(position).getMiTankStructureId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void viewOfflineImages(int position){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("ONOFFTYPE","offline");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,Structure.get(position).getMiTankStructureDetailId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return Structure.size();
    }


}
