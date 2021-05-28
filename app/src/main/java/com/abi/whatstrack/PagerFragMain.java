package com.abi.whatstrack;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.abi.whatstrack.Util.broadcast_String;

public class PagerFragMain extends Fragment {

    //UI
    private View view;

    //SharedPrefer
    private static SharedPreferences mPrefs;
    private static SharedPreferences settings1;
    private static String mPrefs_key;


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

    public static PagerFragMain newInstance(String parameter,boolean ads) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        args.putBoolean("ads", ads);
        PagerFragMain fragment = new PagerFragMain();
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

        view = inflater.inflate(R.layout.pager_frag_main, container, false);

        interfaceInit();

        return view;
    }

    //UI Loading Main
    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(MainFrag.newInstance(mPrefs_key,showAds), "");
        adapter.addFragment(NotiFrag.newInstance(mPrefs_key,showAds), "");

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
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        setupViewPager(viewPager);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tab_layout);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabs.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_storage));
        tabs.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_message));
    }

}
