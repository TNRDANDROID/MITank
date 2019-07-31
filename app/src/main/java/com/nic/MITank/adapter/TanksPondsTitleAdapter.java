package com.nic.MITank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.nic.MITank.R;
import com.nic.MITank.databinding.TanksPondsTitleAdapterBinding;
import com.nic.MITank.model.MITank;
import com.nic.MITank.session.PrefManager;

import org.json.JSONObject;

import java.util.List;

 

public class TanksPondsTitleAdapter extends RecyclerView.Adapter<TanksPondsTitleAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<MITank> miTankList;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;

    public TanksPondsTitleAdapter(Activity context, List<MITank> miTankList) {

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
//        holder.tanksPondsTitleAdapterBinding.tvtitle.setText(miTankList.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return 6;
    }


}
