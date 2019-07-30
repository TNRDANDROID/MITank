package com.nic.MITank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nic.MITank.R;
import com.nic.MITank.model.MITank;

import java.util.List;

/**
 * Created by shanmugapriyan on 25/05/16.
 */
public class CommonAdapter extends BaseAdapter {
    private List<MITank> miTanks;
    private Context mContext;
    private String type;


    public CommonAdapter(Context mContext, List<MITank> pmgsySurvey, String type) {
        this.miTanks = pmgsySurvey;
        this.mContext = mContext;
        this.type = type;
    }


    public int getCount() {
        return miTanks.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.spinner_drop_down_item, parent, false);
//        TextView tv_type = (TextView) view.findViewById(R.id.tv_spinner_item);
        View view = inflater.inflate(R.layout.spinner_value, parent, false);
        TextView tv_type = (TextView) view.findViewById(R.id.spinner_list_value);
        MITank miTank = miTanks.get(position);

        if (type.equalsIgnoreCase("BlockList")) {
            tv_type.setText(miTank.getBlockName());
        } else if (type.equalsIgnoreCase("VillageList")) {
            tv_type.setText(miTank.getPvName());
        } else if (type.equalsIgnoreCase("HabitationList")) {
            tv_type.setText(miTank.getHabitationName());
        }
        return view;
    }

}