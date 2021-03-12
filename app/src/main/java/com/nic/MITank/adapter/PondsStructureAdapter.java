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
import com.nic.MITank.activity.PondsStructureScreen;
import com.nic.MITank.activity.TanksPondsListScreen;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.PondsStructureAdapterBinding;
import com.nic.MITank.databinding.TanksPondsListAdapterBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;


public class PondsStructureAdapter extends RecyclerView.Adapter<PondsStructureAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<MITank> Structure;
    static JSONObject dataset = new JSONObject();
    private dbData dbData;
    String type;

    private LayoutInflater layoutInflater;

    public PondsStructureAdapter(Activity context, ArrayList<MITank> tankStructure,dbData dbData,String type) {

        this.context = context;
        this.dbData = dbData;
        prefManager = new PrefManager(context);
        this.Structure = tankStructure;
        this.type=type;
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
        if(type.equals("Inlet channels")){
            holder.tanksPondsListAdapterBinding.typeLayout.setVisibility(View.VISIBLE);
            holder.tanksPondsListAdapterBinding.sillLevelLayout.setVisibility(View.GONE);
        }
        else if(type.equals("Surplus Weirs / Outlet structure")){
            holder.tanksPondsListAdapterBinding.typeLayout.setVisibility(View.GONE);
            holder.tanksPondsListAdapterBinding.sillLevelLayout.setVisibility(View.GONE);
        }
        else if(type.equals("Bathing Ghat")){
            holder.tanksPondsListAdapterBinding.typeLayout.setVisibility(View.GONE);
            holder.tanksPondsListAdapterBinding.sillLevelLayout.setVisibility(View.GONE);
        }
        else if(type.equals("Ramp structures")){
            holder.tanksPondsListAdapterBinding.typeLayout.setVisibility(View.GONE);
            holder.tanksPondsListAdapterBinding.sillLevelLayout.setVisibility(View.GONE);
        }
        else if(type.equals("Sluices")){
            holder.tanksPondsListAdapterBinding.typeLayout.setVisibility(View.VISIBLE);
            holder.tanksPondsListAdapterBinding.sillLevelLayout.setVisibility(View.VISIBLE);
        }

        holder.tanksPondsListAdapterBinding.structureName.setText(Structure.get(position).getMiTankStructureName()+" "+Structure.get(position).getMiTankStructureSerialId());
       holder.tanksPondsListAdapterBinding.condition.setText(Structure.get(position).getMiTankConditionName());
       holder.tanksPondsListAdapterBinding.typeText.setText(Structure.get(position).getMiTankTypeName());
       holder.tanksPondsListAdapterBinding.sillLevelText.setText(Structure.get(position).getMiTankSkillLevel());

        String dcode = prefManager.getDistrictCode();
        String bcode = prefManager.getBlockCode();
        String pvcode = prefManager.getPvCode();
        String habcode = prefManager.getHabCode();
        String mi_tank_structure_detail_id = Structure.get(position).getMiTankStructureDetailId();
        String mi_tank_structure_serial_id = Structure.get(position).getMiTankStructureSerialId();
        String image_available = Structure.get(position).getImageAvailable();
        String mi_tank_structure_id=Structure.get(position).getMiTankStructureId();
        String mi_tank_survey_id=Structure.get(position).getMiTankSurveyId();


        ArrayList<MITank> imageOffline = dbData.selectImage(dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id,mi_tank_structure_serial_id,mi_tank_structure_id,mi_tank_survey_id);

        if(imageOffline.size() < 1) {
            holder.tanksPondsListAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
            holder.tanksPondsListAdapterBinding.takePhoto.setText("Capture Photo");
        }
        else {
            holder.tanksPondsListAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
//            holder.tanksPondsListAdapterBinding.takePhoto.setText("Change Photo");
            holder.tanksPondsListAdapterBinding.takePhoto.setText("Capture Photo");

        }

        if(image_available.equalsIgnoreCase("Y")) {
            holder.tanksPondsListAdapterBinding.viewOnlineImages.setVisibility(View.VISIBLE);
        }
        else {
            holder.tanksPondsListAdapterBinding.viewOnlineImages.setVisibility(View.GONE);
        }

       holder.tanksPondsListAdapterBinding.cameraActivity.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //openCamera(position);
               ((PondsStructureScreen)context).openCameraAdapter(position);
           }
       });

        holder.tanksPondsListAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImages(position,"offline");
            }
        });

        holder.tanksPondsListAdapterBinding.viewOnlineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isOnline()){
                    viewImages(position,"online");
                }
                else {
                    Utils.showAlert(context,"Your Internet seems to be Offline.Image can be viewed only in Online mode.");
                }

            }
        });

    }

    public void openCamera(int position) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, CameraScreen.class);
        intent.putExtra("KEY","");
        intent.putExtra("Title",Structure.get(position).getMiTankStructureName());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,Structure.get(position).getMiTankStructureSerialId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,Structure.get(position).getMiTankStructureDetailId());
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,Structure.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,Structure.get(position).getMiTankStructureId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void viewImages(int position,String type){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("ONOFFTYPE",type);
        intent.putExtra("KEY","PondStructureAdapter");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,Structure.get(position).getMiTankStructureSerialId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,Structure.get(position).getMiTankStructureDetailId());
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,Structure.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,Structure.get(position).getMiTankStructureId());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return Structure.size();
    }


}
