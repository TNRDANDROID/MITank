package com.nic.MITank.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.nic.MITank.R;
import com.nic.MITank.adapter.TanksPondsListAdapter;
import com.nic.MITank.adapter.TanksPondsTitleAdapter;
import com.nic.MITank.databinding.TanksPondsListScreenBinding;
import com.nic.MITank.databinding.TanksPondsTitleScreenBinding;
import com.nic.MITank.model.MITank;

import java.util.ArrayList;
import java.util.List;

public class TanksPondsListScreen extends AppCompatActivity {

    public TanksPondsListScreenBinding tanksPondsListScreenBinding;
    boolean ExpandedActionBar = true;
    private TanksPondsListAdapter tanksPondsListAdapter;
    private List<MITank> TanksPondsList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tanksPondsListScreenBinding = DataBindingUtil.setContentView(this, R.layout.tanks_ponds_list_screen);
        tanksPondsListScreenBinding.setActivity(this);
        setSupportActionBar(tanksPondsListScreenBinding.toolbar);
        tanksPondsListScreenBinding.ctolbar.setTitle("");

        tanksPondsListScreenBinding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) > 200){
                    ExpandedActionBar = false;
                    tanksPondsListScreenBinding.ctolbar.setTitle("Select Items");
                    tanksPondsListScreenBinding.rltop.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                } else {
                    ExpandedActionBar = true;
                    tanksPondsListScreenBinding.ctolbar.setTitle("");
                    tanksPondsListScreenBinding.rltop.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                }
            }
        });

        TanksPondsList = new ArrayList<>();
        tanksPondsListAdapter = new TanksPondsListAdapter(this,TanksPondsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        tanksPondsListScreenBinding.recyclerView.setLayoutManager(mLayoutManager);
        tanksPondsListScreenBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        tanksPondsListScreenBinding.recyclerView.setAdapter(tanksPondsListAdapter);
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

}
