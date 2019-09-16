package com.nic.MITank.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.MITank.R;
import com.nic.MITank.activity.PendingScreen;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.PendingAdapterBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<MITank> pendingListValues;
    static JSONObject datasetStructure = new JSONObject();
    private final dbData dbData;
    JSONObject datasetTrack = new JSONObject();

    private LayoutInflater layoutInflater;

    public PendingAdapter(Activity context, List<MITank> pendingListValues, dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.pendingListValues = pendingListValues;
        this.dbData = dbData;
    }

    @Override
    public PendingAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PendingAdapterBinding pendingAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.pending_adapter, viewGroup, false);
        return new PendingAdapter.MyViewHolder(pendingAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private PendingAdapterBinding pendingAdapterBinding;

        public MyViewHolder(PendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.habitationName.setText(pendingListValues.get(position).getHabitationName());
        holder.pendingAdapterBinding.villageName.setText(pendingListValues.get(position).getPvName());
        holder.pendingAdapterBinding.localName.setText(pendingListValues.get(position).getLocalName());
        holder.pendingAdapterBinding.area.setText(pendingListValues.get(position).getArea()+" hectare");
        holder.pendingAdapterBinding.nameOfTankPond.setText(pendingListValues.get(position).getNameOftheMITank());
        final String mi_tank_survey_id = pendingListValues.get(position).getMiTankSurveyId();
        dbData.open();
        ArrayList<MITank> tanksStructure = dbData.getSavedDataForParticularTank(mi_tank_survey_id);
        if (tanksStructure.size() > 0 ){
            holder.pendingAdapterBinding.syncStructureLayout.setVisibility(View.VISIBLE);
        }else {
            holder.pendingAdapterBinding.syncStructureLayout.setVisibility(View.GONE);
        }
        ArrayList<MITank> tanksTrack = dbData.getSavedTrackForParticularTank(mi_tank_survey_id);
        if (tanksTrack.size() > 0 ){
            holder.pendingAdapterBinding.syncTrackLayout.setVisibility(View.VISIBLE);
        }else {
            holder.pendingAdapterBinding.syncTrackLayout.setVisibility(View.GONE);
        }

        holder.pendingAdapterBinding.syncTrackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline()) {
                    new toUploadTankTrackDataTask().execute(mi_tank_survey_id);
                    prefManager.setKeyDeleteId(mi_tank_survey_id);
                 //   prefManager.setKeyClickedPosition(String.valueOf(position));
                } else {
                    Utils.showAlert(context, "Turn On Mobile Data To Synchronize!");
                }

            }
        });

        holder.pendingAdapterBinding.syncStructureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline()) {
                    new toUploadTankStructureTask().execute(mi_tank_survey_id);
                    prefManager.setKeyDeleteId(mi_tank_survey_id);
                    //   prefManager.setKeyClickedPosition(String.valueOf(position));
                } else {
                    Utils.showAlert(context, "Turn On Mobile Data To Synchronize!");
                }

            }
        });

    }

    public class toUploadTankTrackDataTask extends AsyncTask<String, Void,
            JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                dbData.open();
                ArrayList<MITank> saveLatLongLists = dbData.getSavedTrackForParticularTank(String.valueOf(params[0]));
                JSONArray saveLatLongArray = new JSONArray();
                if (saveLatLongLists.size() > 0) {
                    for (int i = 0; i < saveLatLongLists.size(); i++) {
                        JSONObject latLongData = new JSONObject();
                        latLongData.put(AppConstant.PV_CODE, saveLatLongLists.get(i).getPvCode());
                        latLongData.put(AppConstant.HAB_CODE, saveLatLongLists.get(i).getHabCode());
                        latLongData.put(AppConstant.KEY_POINT_TYPE, saveLatLongLists.get(i).getPointType());
                        latLongData.put(AppConstant.KEY_POINT_SERIAL_NO, String.valueOf(saveLatLongLists.get(i).getPointSerialNo()));
                        latLongData.put(AppConstant.KEY_LATITUDE, saveLatLongLists.get(i).getLatitude());
                        latLongData.put(AppConstant.KEY_LONGITUDE, saveLatLongLists.get(i).getLongitude());
                        latLongData.put(AppConstant.MI_TANK_SURVEY_ID, saveLatLongLists.get(i).getMiTankSurveyId());

                        saveLatLongArray.put(latLongData);
                    }
                }

                datasetTrack = new JSONObject();
                try {
                    datasetTrack.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_TANK_TRACK_SAVE);
                    datasetTrack.put(AppConstant.KEY_TRACK_DATA, saveLatLongArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return datasetTrack;
        }

        protected void onPostExecute(JSONObject dataset) {
            super.onPostExecute(dataset);
            ((PendingScreen)context).syncTrackData(dataset);
        }
    }

    public class toUploadTankStructureTask extends AsyncTask<String, Void,
            JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            dbData.open();
            JSONArray track_data = new JSONArray();
            ArrayList<MITank> tanks = dbData.getSavedDataForParticularTank(String.valueOf(params[0]));

            if (tanks.size() > 0) {
                for (int i = 0; i < tanks.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(AppConstant.PV_CODE,tanks.get(i).getPvCode());
                        jsonObject.put(AppConstant.HAB_CODE,tanks.get(i).getHabCode());
                        jsonObject.put(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,tanks.get(i).getMiTankStructureDetailId());
                        jsonObject.put(AppConstant.MI_TANK_STRUCTURE_ID,tanks.get(i).getMiTankStructureId());
                        jsonObject.put(AppConstant.MI_TANK_SURVEY_ID,tanks.get(i).getMiTankSurveyId());
                        jsonObject.put(AppConstant.MI_TANK_CONDITION_ID,tanks.get(i).getMiTankConditionId());
                        jsonObject.put(AppConstant.KEY_LATITUDE,tanks.get(i).getLatitude());
                        jsonObject.put(AppConstant.KEY_LONGITUDE,tanks.get(i).getLongitude());

                        Bitmap bitmap = tanks.get(i).getImage();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                        byte[] imageInByte = baos.toByteArray();
                        String image_str = Base64.encodeToString(imageInByte, Base64.DEFAULT);

                        jsonObject.put(AppConstant.KEY_IMAGE,image_str);

                        track_data.put(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                datasetStructure = new JSONObject();

                try {
                    datasetStructure.put(AppConstant.KEY_SERVICE_ID,"mi_tank_detail_save");
                    datasetStructure.put(AppConstant.KEY_TRACK_DATA,track_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return datasetStructure;
        }

        @Override
        protected void onPostExecute(JSONObject dataset) {
            super.onPostExecute(dataset);
            ((PendingScreen)context).syncStructureData(dataset);
        }
    }


    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }


}
