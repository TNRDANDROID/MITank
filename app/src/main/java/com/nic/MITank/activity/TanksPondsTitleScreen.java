package com.nic.MITank.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.nic.MITank.R;
import com.nic.MITank.adapter.TanksPondsTitleAdapter;
import com.nic.MITank.constant.AppConstant;
import com.nic.MITank.dataBase.dbData;
import com.nic.MITank.databinding.TanksPondsTitleScreenBinding;
import com.nic.MITank.model.MITank;

import java.util.ArrayList;

public class TanksPondsTitleScreen extends AppCompatActivity {

    public TanksPondsTitleScreenBinding tanksPondsTitleScreenBinding;
    boolean ExpandedActionBar = true;
    private TanksPondsTitleAdapter tanksPondsTitleAdapter;
    private ArrayList<MITank> TanksPondsTitle;
    public dbData dbData = new dbData(this);

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tanksPondsTitleScreenBinding = DataBindingUtil.setContentView(this, R.layout.tanks_ponds_title_screen);
        tanksPondsTitleScreenBinding.setActivity(this);
        setSupportActionBar(tanksPondsTitleScreenBinding.toolbar);
        tanksPondsTitleScreenBinding.ctolbar.setTitle("");

        tanksPondsTitleScreenBinding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) > 200){
                    ExpandedActionBar = false;
                    tanksPondsTitleScreenBinding.ctolbar.setTitle("Select Structure");
                    tanksPondsTitleScreenBinding.rltop.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                } else {
                    ExpandedActionBar = true;
                    tanksPondsTitleScreenBinding.ctolbar.setTitle("");
                    tanksPondsTitleScreenBinding.rltop.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                }
            }
        });

        TanksPondsTitle = new ArrayList<>();
        tanksPondsTitleAdapter = new TanksPondsTitleAdapter(this,TanksPondsTitle);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        tanksPondsTitleScreenBinding.recyclerView.setLayoutManager(mLayoutManager);
        tanksPondsTitleScreenBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        tanksPondsTitleScreenBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        tanksPondsTitleScreenBinding.recyclerView.setAdapter(tanksPondsTitleAdapter);
        new fetchMITanktask().execute();

    }

    public class fetchMITanktask extends AsyncTask<Void, Void,
            ArrayList<MITank>> {
        @Override
        protected ArrayList<MITank> doInBackground(Void... params) {
            dbData.open();
            TanksPondsTitle = new ArrayList<>();
            TanksPondsTitle = dbData.getStructureForParticularTank(getIntent().getStringExtra(AppConstant.MI_TANK_SURVEY_ID),getIntent().getStringExtra(AppConstant.MINOR_IRRIGATION_TYPE));
            Log.d("TANKS_COUNT", String.valueOf(TanksPondsTitle.size()));
            tanksPondsTitleScreenBinding.tvTitleCount.setText(String.valueOf(TanksPondsTitle.size()).concat(" Items"));
            return TanksPondsTitle;
        }

        @Override
        protected void onPostExecute(ArrayList<MITank> tanksList) {
            super.onPostExecute(tanksList);
            if (tanksList.size() > 0 ) {
                tanksPondsTitleAdapter = new TanksPondsTitleAdapter(TanksPondsTitleScreen.this,TanksPondsTitle);
                tanksPondsTitleScreenBinding.recyclerView.setAdapter(tanksPondsTitleAdapter);
            }else {
                tanksPondsTitleScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }

        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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

    @Override
    protected void onResume() {
        super.onResume();
        tanksPondsTitleAdapter.notifyDataSetChanged();
        tanksPondsTitleScreenBinding.recyclerView.setAdapter(tanksPondsTitleAdapter);
    }
}
