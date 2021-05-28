package com.abi.whatstrack;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Intro1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Intro1 extends Fragment {
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


    public Intro1() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Intro1 newInstance(String parameter, boolean ads) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        args.putBoolean("ads", ads);
        Intro1 fragment = new Intro1();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.intro1, container, false);

//        interfaceInit();

        return view;
    }

}
