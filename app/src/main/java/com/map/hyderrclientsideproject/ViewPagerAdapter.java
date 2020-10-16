package com.map.hyderrclientsideproject;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context myContext;
    String[] catArray;

    public ViewPagerAdapter(Context context, FragmentManager fm, String[] catArray) {
        super(fm);
        myContext = context;
        this.catArray=catArray;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        CatagoryFragment fragobj = new CatagoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("catagory_name", catArray[position]);
        fragobj.setArguments(bundle);
        return  fragobj;
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return catArray.length;
    }
}