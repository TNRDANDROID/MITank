package com.nic.MITank.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.MITank.R;

import com.nic.MITank.activity.FullImageActivity;
import com.nic.MITank.activity.TanksPondsListScreen;
import com.nic.MITank.activity.TanksPondsTitleScreen;
import com.nic.MITank.activity.TrackingScreen;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.databinding.TanksPondsListAdapterBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nic.MITank.activity.HomePage.db;


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

        holder.tanksPondsListAdapterBinding.cameraActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TanksPondsListScreen)context).openCamera(position);

            }
        });
        if(miTankData.get(position).getCenter_point_captured().equals("Y")) {
            holder.tanksPondsListAdapterBinding.endActivity.setVisibility(View.VISIBLE);

        }
        else {
            if (getSaveTradeImageTable(position) == 1) {
                holder.tanksPondsListAdapterBinding.endActivity.setVisibility(View.VISIBLE);
            } else {
                holder.tanksPondsListAdapterBinding.endActivity.setVisibility(View.GONE);
            }
        }

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
        holder.tanksPondsListAdapterBinding.galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    /*((TanksPondsListScreen)context).getCenterImage(position);*/
                    viewImages(position,"online");
                }
                else{
                    if(getSaveTradeImageTable(position)>1) {
                        viewImages(position, "offline");
                    }
                    else {
                        Utils.showAlert(context,"No Image");
                    }
                }
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
        intent.putExtra(AppConstant.MINOR_IRRIGATION_TYPE,miTankData.get(position).getMinorIrrigationType());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public int getSaveTradeImageTable(int position){
        String image="";String lat="";String lan="";

        String sql = "SELECT * FROM " + DBHelper.SAVE_MI_TANK_CENTER_IMAGES + " WHERE mi_tank_survey_id = "+
                miTankData.get(position).getMiTankSurveyId();
/*
        String sql = "SELECT * FROM " + DBHelper.SAVE_MI_TANK_IMAGES + " WHERE mi_tank_survey_id = "+
                miTankData.get(position).getMiTankSurveyId() +"and mi_tank_structure_serial_id ="+miTankData.get(position).getMiTankStructureSerialId();
*/

        Cursor cursor = db.rawQuery(sql, null);
        Log.d("cursor_count", String.valueOf(cursor.getCount()));

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    image = cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    lat = cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.KEY_LATITUDE));
                    lan = cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE));
                    Log.d("lat", "" + lat);
                    Log.d("image", "" + image);
                } while (cursor.moveToNext());
            }
        }
        if(image.getBytes().length>0) {
            return 1;
        }

        else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return miTankData.size();
    }

    public void viewImages(int position,String type){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("ONOFFTYPE",type);
        intent.putExtra("KEY","CenterImageList");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,"");
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,"");
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,miTankData.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,"");
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

}
