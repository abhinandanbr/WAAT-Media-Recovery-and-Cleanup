package com.abi.whatstrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PagerFragIntro extends Fragment {

    //UI
    private View view;
    private ImageButton btnBack, btnNext;

    //SharedPrefer
    private static SharedPreferences mPrefs;
    private static SharedPreferences settings1;
    private static SharedPreferences sharedSettingPreferences;
    private static String mPrefs_key;
    private static final String pref_tutorial="tutorial";


    //Rating
    private static final String pref_logbool="logBool";
    private static final String pref_ad_app_open_times="ad_app_open_times";
    private static final String pref_rate_app_open_times="rateappopentimes";
    private static final String pref_review="review";
    private static final String pref_helpCheck="helpCheck";
    private static final String pref_rate_prev_date="rateprevdate";

    //Logs
    private static final String log_version="VERSION:";
    private static final String log_count="Count:";


    //Misc
    private boolean showAds=true;
    private boolean logBool=true;
    private boolean reviewCheck;

    public static PagerFragIntro newInstance(String parameter, boolean ads) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        args.putBoolean("ads", ads);
        PagerFragIntro fragment = new PagerFragIntro();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPrefs_key=getArguments().getString("parameter");
            showAds=getArguments().getBoolean("ads");
            mPrefs = getActivity().getSharedPreferences(mPrefs_key, Context.MODE_PRIVATE);

            reviewCheck = mPrefs.getBoolean(pref_review, false);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.pager_frag_intro, container, false);

        interfaceInit();

        return view;
    }

    //UI Loading Main
    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(Intro1.newInstance(mPrefs_key,showAds),"");
        adapter.addFragment(Intro2.newInstance(mPrefs_key,showAds),"");
        adapter.addFragment(Intro3.newInstance(mPrefs_key,showAds),"");
//        adapter.addFragment(NotiFrag.newInstance(mPrefs_key,showAds), "");

        viewPager.setAdapter(adapter);
    }
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Drawable> mFragmentDrawableList= new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //UI
    public void interfaceInit(){
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tab_layout);
        tabs.setupWithViewPager(viewPager);
        tabs.setVisibility(View.GONE);

        btnNext=(ImageButton)view.findViewById(R.id.btn_next);
        btnNext.setVisibility(View.GONE);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedSettingPreferences.edit().putBoolean(pref_tutorial,false).apply();

                getActivity().finish();
            }
        });

//        tabs.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_storage));
//        tabs.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_message));
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
//            addBottomDots(position);
            if(position==2){
                btnNext.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onResume(){
        super.onResume();

        sharedSettingPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    }
}
