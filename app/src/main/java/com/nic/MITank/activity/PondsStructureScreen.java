package com.nic.MITank.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.MITank.R;
import com.nic.MITank.adapter.CommonAdapter;
import com.nic.MITank.adapter.PondsStructureAdapter;
import com.nic.MITank.adapter.TanksPondsListAdapter;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.DBHelper;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.PondsStructureScreenBinding;
import com.nic.MITank.databinding.TanksPondsListScreenBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;
import com.nic.MITank.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nic.MITank.activity.HomePage.db;

public class PondsStructureScreen extends AppCompatActivity {

    public PondsStructureScreenBinding pondsStructureScreenBinding;
    boolean ExpandedActionBar = true;
    private PondsStructureAdapter pondsStructureAdapter;
    private ArrayList<MITank> tankStructureList;
    public dbData dbData = new dbData(this);
    private PrefManager prefManager;
    String miTankStructureId;
    private Context context;
    private AlertDialog alert;
    private List<MITank> ConditionList = new ArrayList<>();
    String Tittle="";
    private List<MITank> InletTypeList = new ArrayList<>();
    private List<MITank> SluiceTypeList = new ArrayList<>();
    Spinner condition;
    Spinner type;
    LinearLayout condition_layout,type_layout,sill_level_layout;
    EditText  sill_level_value_taxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pondsStructureScreenBinding = DataBindingUtil.setContentView(this, R.layout.ponds_structure_screen);
        pondsStructureScreenBinding.setActivity(this);
        pondsStructureScreenBinding.toolbarTitle.setText(getIntent().getStringExtra("Title"));
        Tittle=getIntent().getStringExtra("Title");
        setSupportActionBar(pondsStructureScreenBinding.toolbar);
        prefManager = new PrefManager(this);
        context=this;

        tankStructureList = new ArrayList<>();
        pondsStructureAdapter = new PondsStructureAdapter(this,tankStructureList,dbData,Tittle);
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
            pondsStructureAdapter = new PondsStructureAdapter(PondsStructureScreen.this,tankStructureList,dbData,Tittle);
            pondsStructureScreenBinding.recyclerView.setAdapter(pondsStructureAdapter);
            pondsStructureAdapter.notifyDataSetChanged();
        }
    }

    public void addStructureView(){
        int id=0;
        String name = "",fullName=" ",surveryId = "",structureId="",structureDetailsId = "";
        if(tankStructureList.size() > 0){
            MITank miTank = tankStructureList.get(tankStructureList.size() - 1);
             id= Integer.parseInt(miTank.getMiTankStructureSerialId());
            name= miTank.getMiTankStructureName();
            surveryId= miTank.getMiTankSurveyId();
            structureId= miTank.getMiTankStructureId();
            structureDetailsId="";
        }else if(tankStructureList.size() > 0 && tankStructureList.size() == 1){
            MITank miTank = tankStructureList.get(tankStructureList.size());
             id= Integer.parseInt(miTank.getMiTankStructureSerialId());
            name= miTank.getMiTankStructureName();
            surveryId= miTank.getMiTankSurveyId();
            structureId= miTank.getMiTankStructureId();
            structureDetailsId="";
        }else {
            id= 0;
            name= Tittle;
            surveryId= prefManager.getMiTankSurveyId();
            structureId= getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_ID);
            structureDetailsId="";
        }

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            final View view = inflater.inflate(R.layout.pop_up_add_structure, null);
            TextView header,structure_name;
            ImageView close;
            RelativeLayout camera_activity;
            Button submit;

            camera_activity = (RelativeLayout) view.findViewById(R.id.camera_activity);
            header = (TextView) view.findViewById(R.id.header);
            condition = (Spinner) view.findViewById(R.id.condition);
            structure_name = (TextView) view.findViewById(R.id.structure_name);
            submit = (Button) view.findViewById(R.id.btnBuy);
            close = (ImageView) view.findViewById(R.id.close);
            condition_layout = (LinearLayout) view.findViewById(R.id.condition_layout);
            type_layout = (LinearLayout) view.findViewById(R.id.sec_type_layout);
            sill_level_layout = (LinearLayout) view.findViewById(R.id.sill_level_layout);
            sill_level_value_taxt = (EditText) view.findViewById(R.id.sill_level_text);
            type=(Spinner) view.findViewById(R.id.type);
            submit.setVisibility(View.GONE);
            if(Tittle.equals("Inlet channels")){
                loadInletTypeSpinnerValue();
                type.setAdapter(new CommonAdapter(this, InletTypeList, "TypeList"));
                   sill_level_layout.setVisibility(View.GONE);
                   type_layout.setVisibility(View.VISIBLE);
                   condition_layout.setVisibility(View.VISIBLE);
            }
            else if(Tittle.equals("Surplus Weirs / Outlet structure")){
                sill_level_layout.setVisibility(View.GONE);
                type_layout.setVisibility(View.GONE);
                condition_layout.setVisibility(View.VISIBLE);
            }
            else if(Tittle.equals("Bathing Ghat")){
                sill_level_layout.setVisibility(View.GONE);
                type_layout.setVisibility(View.GONE);
                condition_layout.setVisibility(View.VISIBLE);
            }
            else if(Tittle.equals("Ramp structures")){
                sill_level_layout.setVisibility(View.GONE);
                type_layout.setVisibility(View.GONE);
                condition_layout.setVisibility(View.VISIBLE);
            }
            else if(Tittle.equals("Sluices")){
                loadSluiceTypeSpinnerValue();
                type.setAdapter(new CommonAdapter(this, SluiceTypeList, "TypeList"));
                sill_level_layout.setVisibility(View.VISIBLE);
                type_layout.setVisibility(View.VISIBLE);
                condition_layout.setVisibility(View.VISIBLE);
            }


            close.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close));
            loadConditionSpinnervalue();
            condition.setAdapter(new CommonAdapter(this, ConditionList, "ConditionList"));
            fullName=name+" "+String.valueOf(id+1);
            structure_name.setText(fullName);

            final String finalSurveryId = surveryId;
            final String finalStructureId = structureId;
            final String finalStructureDetailsId = structureDetailsId;

            final String finalFullName = name;
            final String idVal = String.valueOf(id+1);

            camera_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Tittle.equals("Inlet channels")){
                        openCamera(finalFullName,idVal,finalSurveryId, finalStructureId, finalStructureDetailsId,
                                ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName()
                                ,ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionId(),
                                InletTypeList.get(condition.getSelectedItemPosition()).getMiTankTypeName()
                                ,InletTypeList.get(condition.getSelectedItemPosition()).getMiTankTypeId(),"");

                    }
                    else if(Tittle.equals("Surplus Weirs / Outlet structure")){
                        openCamera(finalFullName,idVal,finalSurveryId, finalStructureId, finalStructureDetailsId,
                                ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName()
                                ,ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionId(),
                                "","","");

                    }
                    else if(Tittle.equals("Bathing Ghat")){
                        openCamera(finalFullName,idVal,finalSurveryId, finalStructureId, finalStructureDetailsId,
                                ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName()
                                ,ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionId(),
                                "","","");

                    }
                    else if(Tittle.equals("Ramp structures")){
                        openCamera(finalFullName,idVal,finalSurveryId, finalStructureId, finalStructureDetailsId,
                                ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName()
                                ,ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionId(),
                                "","","");

                    }
                    else if(Tittle.equals("Sluices")){
                        openCamera(finalFullName,idVal,finalSurveryId, finalStructureId, finalStructureDetailsId,
                                ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName()
                                ,ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionId(),
                                SluiceTypeList.get(condition.getSelectedItemPosition()).getMiTankTypeName()
                                ,SluiceTypeList.get(condition.getSelectedItemPosition()).getMiTankTypeId(),sill_level_value_taxt.getText().toString());

                    }

                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!"Select Condition".equalsIgnoreCase(ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName())) {
                        if(getSaveTradeImageTable(idVal,finalSurveryId) == 1){
                            insertStructure(finalFullName,ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName()
                                    ,ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionId(),idVal);
                        }else {
                            Utils.showAlert(PondsStructureScreen.this,"Take Photo! ");
                        }
                    }else {
                        Utils.showAlert(PondsStructureScreen.this,"Please Select Condition");
                    }

                }
            });
            
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });


            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
            dialogBuilder.setView(view);
            alert = dialogBuilder.create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            lp.windowAnimations = R.style.DialogAnimation;
            alert.getWindow().setAttributes(lp);
            alert.show();
            alert.setCanceledOnTouchOutside(true);
            alert.setCancelable(true);
            alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getSaveTradeImageTable(String mi_tank_structure_serial_id , String mi_tank_survey_id){
        String image="";String lat="";String lan="";

        String sql = "SELECT * FROM " + DBHelper.SAVE_MI_TANK_IMAGES + " WHERE "+
              " mi_tank_structure_serial_id ="+mi_tank_structure_serial_id;
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
    public boolean typeVisibility(){

        if (type_layout.getVisibility() == View.VISIBLE) {
            if(Tittle.equals("Sluices")) {
                if (!"Select Type".equalsIgnoreCase(SluiceTypeList.get(type.getSelectedItemPosition()).getMiTankTypeName())) {
                    if(! sill_level_value_taxt.getText().toString().equals("")) {
                        return true;
                    }
                    else {
                        Utils.showAlert(this, "Enter Sill Level!");
                        return false;
                    }
                }
                else {
                    Utils.showAlert(this, "Select Type!");
                    return false;
                }
            }
            else {
                if (!"Select Type".equalsIgnoreCase(InletTypeList.get(type.getSelectedItemPosition()).getMiTankTypeName())) {
                    return true;
                }
                else {
                    Utils.showAlert(this, "Select Type!");
                    return false;

                }

            }
        } else {
            return true;
        }

    }


    public void openCamera(String structureName,String serialId,String surveryId, String structureId, String structureDetailsId
            , String conditionName, String conditionId, String typeName, String typeId, String sill_level_value) {

        if (!"Select Condition".equalsIgnoreCase(ConditionList.get(condition.getSelectedItemPosition()).getMiTankConditionName())) {
            if(typeVisibility()){
                Intent intent = new Intent(this, CameraScreen.class);
                intent.putExtra("KEY","PondsStructureScreen");
                intent.putExtra("Title",Tittle);
                intent.putExtra(AppConstant.MI_TANK_STRUCTURE_NAME,structureName);
                intent.putExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,serialId);
                intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,structureDetailsId);
                intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,surveryId);
                intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,structureId);
                intent.putExtra(AppConstant.MI_TANK_CONDITION_ID,conditionId);
                intent.putExtra(AppConstant.MI_TANK_CONDITION_NAME,conditionName);
                intent.putExtra(AppConstant.MI_TANK_type_ID,typeId);
                intent.putExtra(AppConstant.MI_TANK_TYPE_NAME,typeName);
                intent.putExtra(AppConstant.MI_TANK_SKILL_LEVEL,sill_level_value);
                startActivityForResult(intent,1);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        } else {
            Utils.showAlert(this, "Select Condition!");
        }


    }

    public void openCameraAdapter(int position){
        Intent intent = new Intent(context, CameraScreen.class);
        intent.putExtra("KEY","");
        intent.putExtra("Title",tankStructureList.get(position).getMiTankStructureName());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,tankStructureList.get(position).getMiTankStructureSerialId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,tankStructureList.get(position).getMiTankStructureDetailId());
        intent.putExtra(AppConstant.MI_TANK_SURVEY_ID,tankStructureList.get(position).getMiTankSurveyId());
        intent.putExtra(AppConstant.MI_TANK_STRUCTURE_ID,tankStructureList.get(position).getMiTankStructureId());
        startActivityForResult(intent,2);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void insertStructure(String name,String condion,String condionid,String serialid) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.MI_TANK_STRUCTURE_DETAIL_ID,"");
        values.put(AppConstant.MI_TANK_SURVEY_ID,prefManager.getMiTankSurveyId());
        values.put(AppConstant.MI_TANK_STRUCTURE_ID,getIntent().getStringExtra(AppConstant.MI_TANK_STRUCTURE_ID));
        values.put(AppConstant.MI_TANK_STRUCTURE_SERIAL_ID,serialid);
        values.put(AppConstant.MI_TANK_CONDITION_ID,condionid);
        values.put(AppConstant.MI_TANK_CONDITION_NAME,condion);
        values.put(AppConstant.MI_TANK_SKILL_LEVEL,"");
        values.put(AppConstant.MI_TANK_STRUCTURE_NAME,name);
        values.put(AppConstant.IMAGE_AVAILABLE, "");

        long id = db.insert(DBHelper.MI_TANK_DATA_STRUCTURES, null, values);
        Log.d("Insert_id_structures", String.valueOf(id));
        alert.dismiss();
        new fetchStructuretask().execute();
    }

    public void loadConditionSpinnervalue() {
        Cursor conditionCursor = null;
        conditionCursor = db.rawQuery("SELECT * FROM " + DBHelper.MI_TANK_CONDITION, null);

        ConditionList.clear();
        MITank conditionListValue = new MITank();
        conditionListValue.setMiTankConditionName("Select Condition");
        ConditionList.add(conditionListValue);
        if (conditionCursor.getCount() > 0) {
            if (conditionCursor.moveToFirst()) {
                do {
                    MITank conditionList = new MITank();
                    String miTankConditionId = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_ID));
                    String miTankConditionName = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_CONDITION_NAME));

                    conditionList.setMiTankConditionId(miTankConditionId);
                    conditionList.setMiTankConditionName(miTankConditionName);

                    ConditionList.add(conditionList);
                    Log.d("conditonsize", "" + ConditionList.size());
                } while (conditionCursor.moveToNext());
            }
        }
    }
    public void loadInletTypeSpinnerValue() {
        Cursor conditionCursor = null;
        conditionCursor = db.rawQuery("SELECT * FROM " + DBHelper.MI_TANK_TYPE_INLET_STRUCTURE, null);

        InletTypeList.clear();
        MITank conditionListValue = new MITank();
        conditionListValue.setMiTankTypeName("Select Type");
        InletTypeList.add(conditionListValue);
        if (conditionCursor.getCount() > 0) {
            if (conditionCursor.moveToFirst()) {
                do {
                    MITank conditionList = new MITank();
                    String miTankTypeId = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_type_ID));
                    String miTankTypeName = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_TYPE_NAME));

                    conditionList.setMiTankTypeId(miTankTypeId);
                    conditionList.setMiTankTypeName(miTankTypeName);

                    InletTypeList.add(conditionList);
                    Log.d("conditonsize", "" + InletTypeList.size());
                } while (conditionCursor.moveToNext());
            }
        }

    }
    public void loadSluiceTypeSpinnerValue() {
        Cursor conditionCursor = null;
        conditionCursor = db.rawQuery("SELECT * FROM " + DBHelper.MI_TANK_TYPE_SLUICE_STRUCTURE, null);

        SluiceTypeList.clear();
        MITank conditionListValue = new MITank();
        conditionListValue.setMiTankTypeName("Select Type");
        SluiceTypeList.add(conditionListValue);
        if (conditionCursor.getCount() > 0) {
            if (conditionCursor.moveToFirst()) {
                do {
                    MITank conditionList = new MITank();
                    String miTankTypeId = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_type_ID));
                    String miTankTypeName = conditionCursor.getString(conditionCursor.getColumnIndexOrThrow(AppConstant.MI_TANK_TYPE_NAME));

                    conditionList.setMiTankTypeId(miTankTypeId);
                    conditionList.setMiTankTypeName(miTankTypeName);

                    SluiceTypeList.add(conditionList);
                    Log.d("conditonsize", "" + SluiceTypeList.size());
                } while (conditionCursor.moveToNext());
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

    @Override
    protected void onResume() {
        super.onResume();
        pondsStructureAdapter.notifyDataSetChanged();
        pondsStructureScreenBinding.recyclerView.setAdapter(pondsStructureAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            alert.dismiss();
            new fetchStructuretask().execute();
        }  else {
            // failed to record video
            new fetchStructuretask().execute();

        }
    }

}
