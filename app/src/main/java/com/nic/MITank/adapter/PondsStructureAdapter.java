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



//        ArrayList<MITank> imageOffline = dbData.selectImage(dcode,bcode,pvcode,habcode,mi_tank_structure_detail_id);
//
//        if(imageOffline.size() < 1) {
//            id = db.insert(DBHelper.SAVE_MI_TANK_IMAGES, null, values);
//        }
//        else {
//            id = db.update(DBHelper.SAVE_MI_TANK_IMAGES, values, whereClause, whereArgs);
//        }
//
//       holder.tanksPondsListAdapterBinding.cameraActivity.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               openCamera(position);
//           }
//       });
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

    @Override
    public int getItemCount() {
        return Structure.size();
    }


}
