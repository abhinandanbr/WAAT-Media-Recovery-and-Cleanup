package com.abi.whatstrack;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsFrag extends PreferenceFragmentCompat implements IOBackPressed,SharedPreferences.OnSharedPreferenceChangeListener {

    SwitchPreference switchPreference;
    SwitchPreference autoCleanDB;
    SharedPreferences sharedPreferences;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private String rootKey;
    private boolean subSettings=false;

    //SharedPrefer
    private static SharedPreferences mPrefs;
    private static SharedPreferences settings1;
    private static String mPrefs_key;

    //Misc
    private boolean showAds=true;
    private boolean logBool=true;


    public static SettingsFrag newInstance(String parameter,boolean ads) {

        Bundle args = new Bundle();
        args.putString("parameter", parameter);
        args.putBoolean("ads", ads);
        SettingsFrag fragment = new SettingsFrag();
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

//            reviewCheck = mPrefs.getBoolean(pref_review, false);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.rootKey=rootKey;
        if (getArguments() != null) {
            mPrefs_key=getArguments().getString("parameter");
            showAds=getArguments().getBoolean("ads");
            mPrefs = getActivity().getSharedPreferences(mPrefs_key, Context.MODE_PRIVATE);
        }
        setPreferencesFromResource(R.xml.settings, rootKey);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        interfacesInit();
    }

    @Override
    public void onStart() {
        super.onStart();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        if(!subSettings) {
            interfacesInit();
        }else {
            interfacesInitSub1();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    public void interfacesInit(){
//        switchPref= getPreferenceScreen().findPreference("NListener");
        autoCleanDB= (SwitchPreference)findPreference("autoCleanDB");
        switchPreference =(SwitchPreference) findPreference("NListener");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getPreferenceManager().getContext());

//        boolean autoClean=sharedPreferences.getBoolean("autoCleanDBVal",false);
//
//        autoCleanDB.setChecked(autoClean);

        if(!isNotificationServiceEnabled()){
            switchPreference.setChecked(false);
        }else {
            switchPreference.setChecked(true);
        }

        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getPreferenceManager().getContext().startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));

                return false;
            }
        });

        Preference preference =(Preference)findPreference("autoCleanMedia");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setPreferencesFromResource(R.xml.settings_sub1, rootKey);
                interfacesInitSub1();
                subSettings=true;
                return false;
            }
        });



        if(showAds) {
            autoCleanDB.setEnabled(false);
        }
    }

    public void interfacesInitSub1(){

    }


    @Override
    public boolean onBackPressed() {
        if(subSettings){
            setPreferencesFromResource(R.xml.settings, rootKey);
            interfacesInit();
            subSettings=false;
        }else {
            getFragmentManager().popBackStack("Home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        return true;
    }

    private boolean isNotificationServiceEnabled(){
        String notificationListenerString = Settings.Secure.getString(this.getPreferenceManager().getContext().getContentResolver(),ENABLED_NOTIFICATION_LISTENERS);
        //Check notifications access permission
        if (notificationListenerString == null || !notificationListenerString.contains(getActivity().getPackageName()))
        {
            return false;
            //The notification access has not acquired yet!
        }else{
            return true;
            //Your application has access to the notifications
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            if(key.equals("autoCleanDB")){
            }
        } else  {
            //Do something
        }
    }
}
